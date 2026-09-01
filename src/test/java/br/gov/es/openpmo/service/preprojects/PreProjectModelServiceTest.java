package br.gov.es.openpmo.service.preprojects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.dto.preprojects.PreProjectModelDto;
import br.gov.es.openpmo.dto.preprojects.UpdatePreProjectModelRequest;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.CriteriaTabModelDto;
import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.repository.PreProjectModelRepository;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.properties.PropertyModelService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtoFromEntity;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelFromDto;
import br.gov.es.openpmo.service.workpack.UpdatePropertyModels;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class PreProjectModelServiceTest {

  private PreProjectModelRepository preProjectModelRepository;

  private OfficeService officeService;

  private GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  private GetPropertyModelFromDto getPropertyModelFromDto;

  private GetPropertyModelDtoFromEntity getPropertyModelDtoFromEntity;

  private PropertyModelService propertyModelService;

  private UpdatePropertyModels updatePropertyModels;

  private PreProjectModelService service;

  @Before
  public void setUp() {
    this.preProjectModelRepository = mock(PreProjectModelRepository.class);
    this.officeService = mock(OfficeService.class);
    this.getPropertyModelDtosFromEntities = mock(GetPropertyModelDtosFromEntities.class);
    this.getPropertyModelFromDto = mock(GetPropertyModelFromDto.class);
    this.getPropertyModelDtoFromEntity = mock(GetPropertyModelDtoFromEntity.class);
    this.propertyModelService = mock(PropertyModelService.class);
    this.updatePropertyModels = mock(UpdatePropertyModels.class);
    when(this.getPropertyModelDtosFromEntities.execute(org.mockito.ArgumentMatchers.any()))
      .thenReturn(Collections.emptyList());
    this.service = new PreProjectModelService(
      this.preProjectModelRepository,
      this.officeService,
      this.getPropertyModelDtosFromEntities,
      this.getPropertyModelFromDto,
      this.getPropertyModelDtoFromEntity,
      this.propertyModelService,
      this.updatePropertyModels
    );
  }

  @Test
  public void shouldReturnTheExistingPreProjectModel() {
    final PreProjectModel existing = new PreProjectModel();
    existing.setId(10L);
    existing.setActive(true);
    existing.setOperation(CriteriaOperation.SUM);
    when(this.preProjectModelRepository.findIdByOfficeId(1L)).thenReturn(Optional.of(10L));
    when(this.preProjectModelRepository.findById(10L)).thenReturn(Optional.of(existing));

    final PreProjectModelDto result = this.service.findOrCreateByOfficeId(1L);

    assertEquals(Long.valueOf(10L), result.getId());
    assertTrue(result.isActive());
    assertEquals(CriteriaOperation.SUM, result.getOperation());
    assertTrue(result.getProperties().isEmpty());
    verify(this.officeService, never()).findByIdThin(1L);
    verify(this.preProjectModelRepository, never()).save(existing);
  }

  @Test
  public void shouldCreateTheDefaultPreProjectModelWhenItDoesNotExist() {
    final Office office = new Office();
    office.setId(1L);
    when(this.preProjectModelRepository.findIdByOfficeId(1L)).thenReturn(Optional.empty());
    when(this.officeService.findByIdThin(1L)).thenReturn(office);
    when(this.preProjectModelRepository.save(org.mockito.ArgumentMatchers.any(PreProjectModel.class)))
      .thenAnswer(invocation -> {
        final PreProjectModel saved = invocation.getArgument(0);
        saved.setId(20L);
        return saved;
      });

    final PreProjectModelDto result = this.service.findOrCreateByOfficeId(1L);

    assertEquals(Long.valueOf(20L), result.getId());
    assertFalse(result.isActive());
    assertEquals(CriteriaOperation.AVERAGE, result.getOperation());
    assertTrue(result.getProperties().isEmpty());
    verify(this.officeService).findByIdThin(1L);
    verify(this.preProjectModelRepository).save(org.mockito.ArgumentMatchers.argThat(model ->
      model.getOffice() == office && model.getProperties().isEmpty()
    ));
  }

  @Test
  public void shouldUpdateConfigurationAndPropertiesByOffice() {
    final PreProjectModel existing = new PreProjectModel();
    existing.setId(10L);
    existing.setProperties(new HashSet<>());
    when(this.preProjectModelRepository.findById(10L)).thenReturn(Optional.of(existing));
    when(this.preProjectModelRepository.save(existing)).thenReturn(existing);

    final UpdatePreProjectModelRequest request = new UpdatePreProjectModelRequest();
    request.setActive(true);
    request.setOperation(CriteriaOperation.SUM);

    final PreProjectModelDto result = this.service.update(10L, request);

    assertEquals(Long.valueOf(10L), result.getId());
    assertTrue(result.isActive());
    assertEquals(CriteriaOperation.SUM, result.getOperation());
    assertTrue(result.getProperties().isEmpty());
    verify(this.preProjectModelRepository).save(existing);
  }

  @Test(expected = RegistroNaoEncontradoException.class)
  public void shouldNotCreatePreProjectModelDuringUpdate() {
    when(this.preProjectModelRepository.findById(10L)).thenReturn(Optional.empty());
    final UpdatePreProjectModelRequest request = new UpdatePreProjectModelRequest();
    request.setActive(true);
    request.setOperation(CriteriaOperation.AVERAGE);

    this.service.update(10L, request);
  }

  @Test
  public void shouldCreateCriteriaTabWithItsOrganizedProperties() {
    final PreProjectModel preProjectModel = new PreProjectModel();
    preProjectModel.setId(10L);
    final CriteriaTabModelDto request = new CriteriaTabModelDto();
    request.setName("technical-criteria");
    request.setLabel("Criterios tecnicos");
    request.setIcon("settings");
    request.setSortIndex(1L);
    request.setWeight(1D);
    request.setOperation(CriteriaOperation.AVERAGE);
    request.setOrganizedProperties(Collections.emptyList());

    final CriteriaTabModel criteriaTabModel = new CriteriaTabModel();
    criteriaTabModel.setName("technical-criteria");
    criteriaTabModel.setLabel("Criterios tecnicos");
    criteriaTabModel.setIcon("settings");
    criteriaTabModel.setSortIndex(1L);
    criteriaTabModel.setWeight(1D);
    criteriaTabModel.setOperation(CriteriaOperation.AVERAGE);
    final HashSet<PropertyModel> extractedProperties = new HashSet<>();
    extractedProperties.add(criteriaTabModel);

    when(this.preProjectModelRepository.findById(10L)).thenReturn(Optional.of(preProjectModel));
    when(this.getPropertyModelFromDto.execute(Collections.singletonList(request)))
      .thenReturn(extractedProperties);
    when(this.preProjectModelRepository.save(preProjectModel)).thenReturn(preProjectModel);
    when(this.getPropertyModelDtoFromEntity.execute(criteriaTabModel)).thenReturn(request);

    final CriteriaTabModelDto result = this.service.createCriteriaTab(10L, request);

    assertTrue(preProjectModel.getProperties().contains(criteriaTabModel));
    assertTrue(result == request);
    verify(this.preProjectModelRepository).save(preProjectModel);
  }

  @Test
  public void shouldFindCriteriaTabById() {
    final CriteriaTabModel criteriaTabModel = new CriteriaTabModel();
    criteriaTabModel.setId(30L);
    final CriteriaTabModelDto response = new CriteriaTabModelDto();
    response.setId(30L);
    when(this.propertyModelService.findById(30L)).thenReturn(criteriaTabModel);
    when(this.getPropertyModelDtoFromEntity.execute(criteriaTabModel)).thenReturn(response);

    final CriteriaTabModelDto result = this.service.findCriteriaTabById(30L);

    assertEquals(Long.valueOf(30L), result.getId());
  }

  @Test
  public void shouldUpdateCriteriaTabById() {
    final CriteriaTabModel criteriaTabModel = new CriteriaTabModel();
    criteriaTabModel.setId(30L);
    criteriaTabModel.setOrganizedProperties(new HashSet<>());
    final CriteriaTabModel requestedCriteriaTab = new CriteriaTabModel();
    final CriteriaTabModelDto request = new CriteriaTabModelDto();
    final CriteriaTabModelDto response = new CriteriaTabModelDto();
    response.setId(30L);
    final HashSet<PropertyModel> extractedProperties = new HashSet<>();
    extractedProperties.add(requestedCriteriaTab);

    when(this.propertyModelService.findById(30L)).thenReturn(criteriaTabModel);
    when(this.getPropertyModelFromDto.execute(Collections.singletonList(request)))
      .thenReturn(extractedProperties);
    when(this.propertyModelService.save(criteriaTabModel)).thenReturn(criteriaTabModel);
    when(this.getPropertyModelDtoFromEntity.execute(criteriaTabModel)).thenReturn(response);

    final CriteriaTabModelDto result = this.service.updateCriteriaTab(30L, request);

    assertEquals(Long.valueOf(30L), requestedCriteriaTab.getId());
    assertEquals(Long.valueOf(30L), result.getId());
    verify(this.updatePropertyModels).execute(
      Collections.singleton(requestedCriteriaTab),
      Collections.singleton(criteriaTabModel)
    );
    verify(this.propertyModelService).save(criteriaTabModel);
  }

  @Test
  public void shouldDeleteCriteriaTabById() {
    final CriteriaTabModel criteriaTabModel = new CriteriaTabModel();
    criteriaTabModel.setId(30L);
    when(this.propertyModelService.findById(30L)).thenReturn(criteriaTabModel);
    when(this.propertyModelService.canDeleteProperty(30L)).thenReturn(true);

    this.service.deleteCriteriaTab(30L);

    verify(this.propertyModelService).deleteSelectionOptions(Collections.singletonList(30L));
    verify(this.propertyModelService).delete(org.mockito.ArgumentMatchers.argThat(properties ->
      properties.iterator().next() == criteriaTabModel
    ));
  }

}
