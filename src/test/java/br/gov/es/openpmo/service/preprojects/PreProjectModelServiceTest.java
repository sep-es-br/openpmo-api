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
import br.gov.es.openpmo.enumerator.CriteriaOperation;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.preprojects.models.PreProjectModel;
import br.gov.es.openpmo.repository.PreProjectModelRepository;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.reports.models.GetPropertyModelDtosFromEntities;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class PreProjectModelServiceTest {

  private PreProjectModelRepository preProjectModelRepository;

  private OfficeService officeService;

  private GetPropertyModelDtosFromEntities getPropertyModelDtosFromEntities;

  private PreProjectModelService service;

  @Before
  public void setUp() {
    this.preProjectModelRepository = mock(PreProjectModelRepository.class);
    this.officeService = mock(OfficeService.class);
    this.getPropertyModelDtosFromEntities = mock(GetPropertyModelDtosFromEntities.class);
    when(this.getPropertyModelDtosFromEntities.execute(org.mockito.ArgumentMatchers.any()))
      .thenReturn(Collections.emptyList());
    this.service = new PreProjectModelService(
      this.preProjectModelRepository,
      this.officeService,
      this.getPropertyModelDtosFromEntities
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

}
