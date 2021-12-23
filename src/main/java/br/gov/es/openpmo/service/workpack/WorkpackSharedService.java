package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.ComboDto;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedDto;
import br.gov.es.openpmo.dto.workpackshared.WorkpackSharedParamDto;
import br.gov.es.openpmo.enumerator.PermissionLevelEnum;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.IsSharedWith;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackSharedRepository;
import br.gov.es.openpmo.service.office.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.DUPLICATED_VALUE;
import static br.gov.es.openpmo.utils.ApplicationMessage.REGISTRO_NOT_FOUND;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;

@Service
public class WorkpackSharedService {

  private final WorkpackSharedRepository repository;

  private final WorkpackService workpackService;

  private final WorkpackModelService workpackModelService;

  private final OfficeService officeService;

  @Autowired
  public WorkpackSharedService(
    final WorkpackSharedRepository repository,
    final WorkpackService workpackService,
    final WorkpackModelService workpackModelService,
    final OfficeService officeService
  ) {
    this.repository = repository;
    this.workpackService = workpackService;
    this.workpackModelService = workpackModelService;
    this.officeService = officeService;
  }

  private static boolean validateStructure(
    final WorkpackModel baseWorkpackModel,
    final List<WorkpackModel> baseWorkpackModelStructure,
    final WorkpackModel instanceWorkpack
  ) {
    if(!baseWorkpackModel.getModelName().equals(instanceWorkpack.getModelName())) {
      return true;
    }
    final List<WorkpackModel> instanceChildren = fetchChildren(instanceWorkpack);
    if(hasSameSize(baseWorkpackModelStructure, instanceChildren)) {
      return true;
    }
    return !verifyStructure(baseWorkpackModelStructure, instanceChildren);
  }

  private static void addItem(final Collection<? super ComboDto> itens, final IsSharedWith relationship) {
    itens.add(new ComboDto(
      relationship.workpackId(),
      relationship.comboName()
    ));
  }

  private static void addItem(final Collection<? super ComboDto> itens, final Workpack publicWorkpack, final WorkpackModel instance) {
    itens.add(new ComboDto(
      publicWorkpack.getId(),
      instance.getModelNameWithOffice()
    ));
  }

  private static boolean hasSameSize(
    final Collection<WorkpackModel> workpackModelStructure,
    final Collection<WorkpackModel> workpackModelSharedStructure
  ) {
    return workpackModelStructure.size() != workpackModelSharedStructure.size();
  }

  private static boolean verifyStructure(
    final List<WorkpackModel> workpackModelStructure,
    final List<WorkpackModel> workpackModelSharedStructure
  ) {
    boolean equalStructure = true;
    for(int i = 0; i < workpackModelStructure.size(); i++) {
      final String workpackModelType = workpackModelStructure.get(i).getClass().getTypeName();
      final String workpackModelSharedType = workpackModelSharedStructure.get(i).getClass().getTypeName();
      if(!workpackModelType.equals(workpackModelSharedType)) {
        equalStructure = false;
        break;
      }
    }
    return equalStructure;
  }

  private static List<WorkpackModel> fetchChildren(final WorkpackModel workpackModelShared) {
    if(workpackModelShared.getChildren() == null) return emptyList();
    return new ArrayList<>(workpackModelShared.getChildren());
  }

  @Transactional
  public void revokeShare(final Long idSharedWith, final Long idWorkpack) {
    if(idSharedWith == null) {
      this.revokePublicAccess(idWorkpack);
      return;
    }
    final IsSharedWith isSharedWith = this.findById(idSharedWith);
    final Long idOffice = isSharedWith.getOfficeId();

    this.deleteSharedRelationship(idWorkpack, idOffice);
  }

  private void revokePublicAccess(final Long idWorkpack) {
    final Workpack workpack = this.workpackService.findById(idWorkpack);
    workpack.setPublicShared(false);
    this.workpackService.saveDefault(workpack);
    this.deleteAllSharedRelationship(workpack.getId());
  }

  private void deleteAllSharedRelationship(final Long idWorkpack) {
    this.deleteSharedRelationship(idWorkpack, null);
  }

  private void deleteSharedRelationship(final Long idWorkpack, final Long idOffice) {
    this.repository.deleteExternalPermission(idWorkpack, idOffice);
    this.repository.deleteExternalParent(idWorkpack, idOffice);
    this.repository.deleteExternalLinkedRelationship(idWorkpack, idOffice);
    this.repository.deleteSharedRelationship(idWorkpack, idOffice);
  }

  public IsSharedWith findById(final Long id) {
    return this.repository
      .findById(id)
      .orElseThrow(() -> new NegocioException(REGISTRO_NOT_FOUND));
  }

  public void store(final Long idWorkpack, final List<WorkpackSharedParamDto> request) {
    final Workpack workpack = this.workpackService.findByIdDefault(idWorkpack);

    final boolean workpackPublic = verifyIfItWillNeedToIncludeAllOffices(request);
    workpack.setPublicShared(workpackPublic);
    workpack.setPublicLevel(ifIsPublicGetFirstLevelOrNull(request, workpackPublic));
    this.workpackService.saveDefault(workpack);

    final List<IsSharedWith> isSharedWiths = this.repository.findSharedWithDataByWorkpackId(idWorkpack);

    if(workpackPublic) {
      this.repository.deleteAll(isSharedWiths);
      return;
    }

    request.forEach(item -> this.createIsSharedRelationship(
      workpack,
      isSharedWiths,
      item
    ));
  }

  private static PermissionLevelEnum ifIsPublicGetFirstLevelOrNull(
    final List<? extends WorkpackSharedParamDto> request,
    final boolean workpackPublic
  ) {
    return workpackPublic ? request.get(0).getLevel() : null;
  }

  private void createIsSharedRelationship(
    final Workpack workpack,
    final Collection<IsSharedWith> isSharedWiths,
    final WorkpackSharedParamDto item
  ) {
    final Optional<IsSharedWith> maybeIsShared = searchSharedRelationship(isSharedWiths, item.getId());
    if(maybeIsShared.isPresent()) {
      this.updateSharedPermissionLevel(item);
      return;
    }
    this.validateDuplicatedValues(workpack.getId(), item);
    this.createSharedRelationship(workpack, item);
  }

  private void createSharedRelationship(final Workpack workpack, final WorkpackSharedParamDto item) {
    final IsSharedWith newRelationship = new IsSharedWith();
    newRelationship.setWorkpack(workpack);
    newRelationship.setOffice(this.officeService.findById(item.idOffice()));
    newRelationship.setPermissionLevel(item.getLevel());
    this.repository.save(newRelationship);
  }

  private static Optional<IsSharedWith> searchSharedRelationship(
    final Collection<IsSharedWith> isSharedWiths, final Long id
  ) {
    return isSharedWiths.stream()
      .filter(filtro -> filtro.getId().equals(id))
      .findFirst();
  }

  private void updateSharedPermissionLevel(final WorkpackSharedParamDto item) {
    final IsSharedWith isSharedWith = this.findById(item.getId());
    isSharedWith.setPermissionLevel(item.getLevel());
    this.repository.save(isSharedWith);
  }

  private void validateDuplicatedValues(final Long idWorkpack, final WorkpackSharedParamDto request) {
    final Optional<IsSharedWith> entity = this.repository.findByIdWorkpackAndIdOffice(
      idWorkpack,
      request.idOffice()
    );
    if(!entity.isPresent()) {
      return;
    }

    throw new NegocioException(DUPLICATED_VALUE);
  }

  private static boolean verifyIfItWillNeedToIncludeAllOffices(final List<WorkpackSharedParamDto> request) {
    if(shouldHasOneItem(request)) return false;
    final WorkpackSharedParamDto firstItem = request.get(0);
    return "All".equalsIgnoreCase(firstItem.officeName()) || firstItem.idOffice() == null;
  }

  private static boolean shouldHasOneItem(final List<WorkpackSharedParamDto> request) {
    return request.size() != 1;
  }

  public List<WorkpackSharedDto> getAll(final Long idWorkpack) {
    final Optional<Workpack> maybeWorkpack = this.repository.findWorkpackById(idWorkpack);
    if(!maybeWorkpack.isPresent()) {
      return emptyList();
    }
    final Workpack workpack = maybeWorkpack.get();
    if(TRUE.equals(workpack.getPublicShared())) {
      return Collections.singletonList(WorkpackSharedDto.of(workpack));
    }
    return Optional.ofNullable(workpack.getSharedWith())
      .map(sharedWith ->
             sharedWith.stream()
               .map(WorkpackSharedDto::of)
               .collect(Collectors.toList()))
      .orElse(new ArrayList<>());
  }

  public WorkpackSharedDto getById(final Long id, final Long idWorkpack) {
    final IsSharedWith isSharedWith = this.findById(id);
    return WorkpackSharedDto.of(isSharedWith);
  }

  public List<ComboDto> getSharedWorkpacks(final Long idworkpackModel) {
    final WorkpackModel workpackModel = this.workpackModelService.findById(idworkpackModel);
    final List<WorkpackModel> workpackModelStructure = fetchChildren(workpackModel);

    final List<ComboDto> itens = new ArrayList<>();

    this.addPublicWorkpack(itens, workpackModel, workpackModelStructure);
    this.addDirectlySharedWorkpack(itens, workpackModel, workpackModelStructure);
    return itens;
  }

  private void addPublicWorkpack(
    final Collection<? super ComboDto> itens,
    final WorkpackModel workpackModel,
    final List<WorkpackModel> workpackModelStructure
  ) {
    final List<Workpack> publicWorkpacks = this.repository.listAllWorkpacksPublic();

    for(final Workpack publicWorkpack : publicWorkpacks) {
      final WorkpackModel instance = publicWorkpack.getWorkpackModelInstance();
      if(validateStructure(workpackModel, workpackModelStructure, instance)) continue;
      addItem(itens, publicWorkpack, instance);
    }
  }

  private void addDirectlySharedWorkpack(
    final Collection<? super ComboDto> itens,
    final WorkpackModel workpackModel,
    final List<WorkpackModel> workpackModelStructure
  ) {

    final List<IsSharedWith> isSharedWiths = this.repository.listAllWorkpacksShared(workpackModel.getId());

    for(final IsSharedWith relationship : isSharedWiths) {
      final WorkpackModel instanceWorkpackRelationship = relationship.workpackInstance();
      if(validateStructure(workpackModel, workpackModelStructure, instanceWorkpackRelationship)) continue;
      addItem(itens, relationship);
    }
  }
}
