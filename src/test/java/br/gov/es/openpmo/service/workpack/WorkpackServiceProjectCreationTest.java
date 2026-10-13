package br.gov.es.openpmo.service.workpack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.dto.workpack.ProjectParamDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.preprojects.PreProjectModelService;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class WorkpackServiceProjectCreationTest {

  private WorkpackService workpackService;
  private PlanService planService;
  private PreProjectModelService preProjectModelService;
  private WorkpackModelService workpackModelService;
  private ProjectParamDto project;

  @Before
  public void setUp() {
    this.workpackService = mock(WorkpackService.class, CALLS_REAL_METHODS);
    this.planService = mock(PlanService.class);
    this.preProjectModelService = mock(PreProjectModelService.class);
    this.workpackModelService = mock(WorkpackModelService.class);
    ReflectionTestUtils.setField(this.workpackService, "planService", this.planService);
    ReflectionTestUtils.setField(this.workpackService, "preProjectModelService", this.preProjectModelService);
    ReflectionTestUtils.setField(this.workpackService, "workpackModelService", this.workpackModelService);

    this.project = new ProjectParamDto();
    this.project.setIdPlan(1L);
    this.project.setIdWorkpackModel(3L);

    final Office office = new Office();
    office.setId(2L);
    final Plan plan = new Plan();
    plan.setOffice(office);
    when(this.planService.findById(1L)).thenReturn(plan);
    when(this.preProjectModelService.isActiveByOfficeId(2L)).thenReturn(true);
  }

  @Test
  public void blocksDirectProjectCreationWhenPreProjectIsActive() {
    try {
      this.workpackService.criarWorkpack(this.project);
      fail("A criação direta deveria ser bloqueada");
    } catch (NegocioException exception) {
      assertEquals(ApplicationMessage.PROJECT_DIRECT_CREATION_BLOCKED_BY_PRE_PROJECT, exception.getMessage());
    }
    verify(this.preProjectModelService).isActiveByOfficeId(2L);
    verify(this.workpackModelService, never()).getPropertyModels(3L);
  }

  @Test
  public void allowsDirectProjectCreationToContinueWhenPreProjectIsInactive() {
    when(this.preProjectModelService.isActiveByOfficeId(2L)).thenReturn(false);
    final IllegalStateException reachedValidation = new IllegalStateException("reached validation");
    when(this.workpackModelService.getPropertyModels(3L)).thenThrow(reachedValidation);

    try {
      this.workpackService.criarWorkpack(this.project);
      fail("A validação do modelo deveria ser alcançada");
    } catch (IllegalStateException exception) {
      assertEquals(reachedValidation, exception);
    }
    verify(this.preProjectModelService).isActiveByOfficeId(2L);
  }

  @Test
  public void preProjectFlowBypassesDirectCreationRestriction() {
    final IllegalStateException reachedValidation = new IllegalStateException("reached validation");
    when(this.workpackModelService.getPropertyModels(3L)).thenThrow(reachedValidation);

    try {
      this.workpackService.criarWorkpackFromPreProject(this.project);
      fail("A validação do modelo deveria ser alcançada");
    } catch (IllegalStateException exception) {
      assertEquals(reachedValidation, exception);
    }
    verify(this.preProjectModelService, never()).isActiveByOfficeId(2L);
  }
}
