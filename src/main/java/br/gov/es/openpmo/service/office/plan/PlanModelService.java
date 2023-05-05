package br.gov.es.openpmo.service.office.plan;

import br.gov.es.openpmo.dto.office.OfficeDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelStoreDto;
import br.gov.es.openpmo.dto.planmodel.PlanModelUpdateDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.CustomFilterRepository;
import br.gov.es.openpmo.repository.PlanModelRepository;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.repository.custom.filters.FindAllPlanModelUsingCustomFilter;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.utils.ApplicationMessage.CUSTOM_FILTER_NOT_FOUND;

@Service
public class PlanModelService {

  private final PlanModelRepository planModelRepository;
  private final WorkpackModelRepository workpackModelRepository;
  private final CustomFilterRepository customFilterRepository;
  private final FindAllPlanModelUsingCustomFilter findAllPlanModel;
  private final ModelMapper modelMapper;
  private final OfficeService officeService;

  @Autowired
  public PlanModelService(
    final PlanModelRepository planModelRepository,
    final WorkpackModelRepository workpackModelRepository,
    final CustomFilterRepository customFilterRepository,
    final FindAllPlanModelUsingCustomFilter findAllPlanModel,
    final ModelMapper modelMapper,
    final OfficeService officeService
  ) {
    this.planModelRepository = planModelRepository;
    this.workpackModelRepository = workpackModelRepository;
    this.customFilterRepository = customFilterRepository;
    this.findAllPlanModel = findAllPlanModel;
    this.modelMapper = modelMapper;
    this.officeService = officeService;
  }

  public List<PlanModel> findAll() {
    final List<PlanModel> planModels = new ArrayList<>();
    this.planModelRepository.findAll().forEach(planModels::add);
    return planModels;
  }

  public List<PlanModel> findAllInOffice(
    final Long idOffice,
    final Long idFilter
  ) {
    if(idFilter == null) {
      return this.findAllInOffice(idOffice);
    }

    final CustomFilter filter = this.customFilterRepository.findById(idFilter)
      .orElseThrow(() -> new NegocioException(CUSTOM_FILTER_NOT_FOUND));

    final Map<String, Object> params = new HashMap<>();
    params.put("idOffice", idOffice);

    return this.findAllPlanModel.execute(filter, params);
  }

  public List<PlanModel> findAllInOffice(final Long id) {
    final List<PlanModel> planModels = this.planModelRepository.findAllInOffice(id);
    planModels.sort(Comparator.comparing(PlanModel::getName));
    return planModels;
  }

  public void delete(final PlanModel planModel) {
    final List<WorkpackModel> workpackModelList = this.workpackModelRepository.findAllByIdPlanModel(planModel.getId());
    final Collection<WorkpackModel> workpackModels = new HashSet<>(workpackModelList);

    if(!workpackModels.isEmpty()) {
      throw new NegocioException(ApplicationMessage.PLAN_MODEL_DELETE_RELATIONSHIP_ERROR);
    }

    this.planModelRepository.delete(planModel);
  }

  public PlanModel store(final PlanModelStoreDto planModelStoreDto) {
    final PlanModel planModel = this.modelMapper.map(planModelStoreDto, PlanModel.class);

    this.setOffice(planModel, planModelStoreDto.getIdOffice());
    this.sharedWith(planModelStoreDto, planModel);

    return this.save(planModel);
  }

  private void sharedWith(
    final PlanModelStoreDto planModelStoreDto,
    final PlanModel planModel
  ) {
    final boolean isSharedWithAll = planModelStoreDto.isSharedWithAll();
    planModel.setPublicShared(isSharedWithAll);

    final Set<Office> sharedWith = new HashSet<>();

    if(!isSharedWithAll && Objects.nonNull(planModelStoreDto.getSharedWith())) {
      this.share(planModelStoreDto, sharedWith);
    }

    planModel.setSharedWith(sharedWith);
  }

  private void share(
    final PlanModelStoreDto planModelStoreDto,
    final Collection<? super Office> sharedWith
  ) {
    final Set<Long> idOffices = planModelStoreDto.getSharedWith()
      .stream()
      .map(OfficeDto::getId)
      .collect(Collectors.toSet());

    final Set<Office> offices = this.officeService.findAllByIds(idOffices);
    sharedWith.addAll(offices);
  }

  private void setOffice(
    final PlanModel planModel,
    final Long idOffice
  ) {
    final Office office = this.officeService.findById(idOffice);
    planModel.setOffice(office);
  }

  public PlanModel save(final PlanModel planModel) {
    return this.planModelRepository.save(planModel);
  }

  public PlanModel update(final PlanModelUpdateDto planModelUpdateDto) {
    final PlanModel planModel = this.getPlanModel(planModelUpdateDto);

    final PlanModelStoreDto planModelStoreDto = new PlanModelStoreDto(
      planModelUpdateDto.getIdOffice(),
      planModelUpdateDto.getName(),
      planModelUpdateDto.getFullName(),
      planModelUpdateDto.isSharedWithAll(),
      planModelUpdateDto.getSharedWith()
    );

    this.setOffice(planModel, planModelStoreDto.getIdOffice());
    this.sharedWith(planModelStoreDto, planModel);

    return this.save(planModel);
  }

  private PlanModel getPlanModel(final PlanModelUpdateDto planModelUpdateDto) {
    final PlanModel planModel = this.findById(planModelUpdateDto.getId());

    planModel.setName(planModelUpdateDto.getName());
    planModel.setFullName(planModelUpdateDto.getFullName());

    return planModel;
  }

  public PlanModel findById(final Long id) {
    return this.planModelRepository.findById(id)
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PLAN_MODEL_NOT_FOUND));
  }

  public List<PlanModelDto> findAllSharedWithOffice(final Long idOffice) {
    final List<PlanModel> sharedWithOffice = this.planModelRepository.findAllSharedWithOffice(idOffice);
    return sharedWithOffice.stream()
      .map(PlanModelDto::new)
      .sorted(Comparator.comparing(PlanModelDto::getName))
      .collect(Collectors.toList());
  }

}
