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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.DUPLICATED_VALUE;
import static br.gov.es.openpmo.utils.ApplicationMessage.REGISTRO_NOT_FOUND;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;

@Service
public class WorkpackSharedService {

  public static final String ALL = "All";
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

  private static boolean validStructure(
    final WorkpackModel baseWorkpackModel,
    final Collection<WorkpackModel> baseWorkpackModelStructure,
    final WorkpackModel instanceWorkpack
  ) {
    if(!baseWorkpackModel.getModelName().equals(instanceWorkpack.getModelName())) {
      return false;
    }
    final List<WorkpackModel> instanceChildren = fetchChildren(instanceWorkpack);
    if(baseWorkpackModelStructure.size() != instanceChildren.size()) {
      return false;
    }
    return baseWorkpackModelStructure.stream()
      .allMatch(f -> instanceChildren.stream().anyMatch(s -> sameTypeName(f, s)));
  }

  private static String getOfficeName(final Workpack workpack) {
    return workpack
      .getOriginalOffice()
      .map(a -> " (" + a.getName() + ")")
      .orElse("");
  }

  private static boolean sameTypeName(
    final WorkpackModel first,
    final WorkpackModel second
  ) {
    final String firstTypeName = first.getClass().getTypeName();
    final String secondTypeName = second.getClass().getTypeName();
    return Objects.equals(firstTypeName, secondTypeName);
  }

  private static List<WorkpackModel> fetchChildren(final WorkpackModel workpackModelShared) {
    return Optional.ofNullable(workpackModelShared.getChildren())
      .map(ArrayList::new)
      .orElseGet(ArrayList::new);
  }

  private static boolean verifyIfItWillNeedToIncludeAllOffices(final List<WorkpackSharedParamDto> request) {
    if(request.size() != 1) {
      return false;
    }
    final WorkpackSharedParamDto firstItem = request.get(0);
    return ALL.equalsIgnoreCase(firstItem.officeName()) || firstItem.idOffice() == null;
  }

  private static List<WorkpackSharedDto> getWorkpackSharedDtos(final Collection<IsSharedWith> sharedWith) {
    return sharedWith.stream()
      .map(WorkpackSharedDto::of)
      .collect(Collectors.toList());
  }

  private static Optional<IsSharedWith> searchSharedRelationship(
    final Collection<IsSharedWith> isSharedWiths,
    final Long id
  ) {
    return isSharedWiths.stream()
      .filter(filtro -> filtro.getId().equals(id))
      .findFirst();
  }

  private void addItem(
    final Collection<? super ComboDto> itens,
    final IsSharedWith relationship
  ) {
    final String workpackName = relationship.getWorkpack().getName();
    final String officeName = getOfficeName(relationship.getWorkpack());
    itens.add(new ComboDto(relationship.workpackId(), workpackName + officeName));
  }

  private void revokePublicAccess(final Long idWorkpack) {
    final Workpack workpack = this.workpackService.findById(idWorkpack);
    this.workpackService.setWorkpackPublicShared(workpack.getId(), false, PermissionLevelEnum.NONE.toString());
    this.deleteAllSharedRelationship(workpack.getId());
  }

  private void deleteAllSharedRelationship(final Long idWorkpack) {
    this.deleteSharedRelationship(idWorkpack, null);
  }

  private void deleteSharedRelationship(
    final Long idWorkpack,
    final Long idOffice
  ) {
    this.repository.deleteExternalPermission(idWorkpack, idOffice);
    this.repository.deleteExternalParent(idWorkpack, idOffice);
    this.repository.deleteExternalLinkedRelationship(idWorkpack, idOffice);
    this.repository.deleteSharedRelationship(idWorkpack, idOffice);
  }

  private void addItem(
    final Collection<? super ComboDto> itens,
    final Workpack publicWorkpack
  ) {
    final String workpackName = publicWorkpack.getName();
    final String officeName = getOfficeName(publicWorkpack);
    itens.add(new ComboDto(publicWorkpack.getId(), workpackName + officeName));
  }

  public IsSharedWith findById(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(REGISTRO_NOT_FOUND));
  }

  @Transactional
  public void revokeShare(
    final Long idSharedWith,
    final Long idWorkpack
  ) {
    if(idSharedWith == null) {
      this.revokePublicAccess(idWorkpack);
      return;
    }
    final IsSharedWith isSharedWith = this.findById(idSharedWith);
    final Long idOffice = isSharedWith.getOfficeId();
    this.deleteSharedRelationship(idWorkpack, idOffice);
  }

  private void createSharedRelationship(
    final Workpack workpack,
    final WorkpackSharedParamDto item
  ) {
    final IsSharedWith newRelationship = new IsSharedWith();
    newRelationship.setWorkpack(workpack);
    newRelationship.setOffice(this.officeService.findById(item.idOffice()));
    newRelationship.setPermissionLevel(item.getLevel());
    this.repository.save(newRelationship);
  }

  private void updateSharedPermissionLevel(final WorkpackSharedParamDto item) {
    final IsSharedWith isSharedWith = this.findById(item.getId());
    isSharedWith.setPermissionLevel(item.getLevel());
    this.repository.save(isSharedWith);
  }

  public void store(
    final Long idWorkpack,
    final List<WorkpackSharedParamDto> request
  ) {
    final Workpack workpack = this.workpackService.findByIdDefault(idWorkpack);

    final boolean workpackPublic = verifyIfItWillNeedToIncludeAllOffices(request);
    workpack.setPublicShared(workpackPublic);
    workpack.setPublicLevel(workpackPublic ? request.get(0).getLevel() : null);
    this.workpackService.setWorkpackPublicShared(workpack.getId(), workpack.getPublicShared(), workpack.getPublicLevel().toString());

    final List<IsSharedWith> isSharedWiths = this.repository.findSharedWithDataByWorkpackId(idWorkpack);

    if(workpackPublic) {
      this.repository.deleteAll(isSharedWiths);
      return;
    }

    request.forEach(item -> this.createIsSharedRelationship(workpack, isSharedWiths, item));
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

  private void validateDuplicatedValues(
    final Long idWorkpack,
    final WorkpackSharedParamDto request
  ) {
    final Optional<IsSharedWith> entity = this.repository.findByIdWorkpackAndIdOffice(idWorkpack, request.idOffice());
    if(!entity.isPresent()) {
      return;
    }
    throw new NegocioException(DUPLICATED_VALUE);
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
      .map(WorkpackSharedService::getWorkpackSharedDtos)
      .orElseGet(ArrayList::new);
  }

  public WorkpackSharedDto getById(final Long id) {
    final IsSharedWith isSharedWith = this.findById(id);
    return WorkpackSharedDto.of(isSharedWith);
  }

  public List<ComboDto> getSharedWorkpacks(final Long idworkpackModel, final Long idPlan) {
    final WorkpackModel workpackModel = this.workpackModelService.findById(idworkpackModel);
    final List<WorkpackModel> workpackModelStructure = fetchChildren(workpackModel);
    final List<ComboDto> itens = new ArrayList<>();
    this.addPublicWorkpack(itens, workpackModel, workpackModelStructure, idPlan);
    this.addDirectlySharedWorkpack(itens, workpackModel, workpackModelStructure, idPlan);
    return itens;
  }

  private void addPublicWorkpack(
    final Collection<? super ComboDto> itens,
    final WorkpackModel workpackModel,
    final Collection<WorkpackModel> workpackModelStructure,
    final Long idPlan
  ) {
    final List<Workpack> publicWorkpacks = this.repository.listAllWorkpacksPublic(idPlan);
    for(final Workpack publicWorkpack : publicWorkpacks) {
      final WorkpackModel instance = publicWorkpack.getWorkpackModelInstance();
      if(validStructure(workpackModel, workpackModelStructure, instance)) {
        this.addItem(itens, publicWorkpack);
      }
    }
  }

  private void addDirectlySharedWorkpack(
    final Collection<? super ComboDto> itens,
    final WorkpackModel workpackModel,
    final Collection<WorkpackModel> workpackModelStructure,
    final Long idPlan
  ) {
    final List<IsSharedWith> isSharedWiths = this.repository.listAllWorkpacksShared(workpackModel.getId(), idPlan);
    for(final IsSharedWith relationship : isSharedWiths) {
      final WorkpackModel instanceWorkpackRelationship = relationship.workpackInstance();
      if(validStructure(workpackModel, workpackModelStructure, instanceWorkpackRelationship)) {
        this.addItem(itens, relationship);
      }
    }
  }

}
