package br.gov.es.openpmo.service.preprojects;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.dto.preprojects.CreateProjectFromPreProjectRequest;
import br.gov.es.openpmo.dto.workpack.ProjectParamDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.office.plan.PlanModel;
import br.gov.es.openpmo.model.preprojects.PreProject;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.PreProjectRepository;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import br.gov.es.openpmo.utils.ApplicationCacheUtil;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class CreateProjectFromPreProjectServiceTest {

  private final PreProjectRepository preProjectRepository = mock(PreProjectRepository.class);
  private final PlanService planService = mock(PlanService.class);
  private final WorkpackModelService workpackModelService = mock(WorkpackModelService.class);
  private final WorkpackService workpackService = mock(WorkpackService.class);
  private final ApplicationCacheUtil applicationCacheUtil = mock(ApplicationCacheUtil.class);
  private CreateProjectFromPreProjectService service;

  @Before
  public void setUp() {
    this.service = new CreateProjectFromPreProjectService(
      this.preProjectRepository,
      this.planService,
      this.workpackModelService,
      this.workpackService,
      this.applicationCacheUtil
    );
  }

  @Test
  public void rejectsAnotherProjectWhenOneIsActive() {
    when(this.preProjectRepository.lockForProjectCreation(1L)).thenReturn(Optional.of(1L));
    when(this.preProjectRepository.findActiveProjectId(1L)).thenReturn(Optional.of(20L));

    try {
      this.service.create(1L, this.request());
      fail("Should reject a second active project");
    } catch (NegocioException expected) {
      verify(this.preProjectRepository).lockForProjectCreation(1L);
      verify(this.preProjectRepository).findActiveProjectId(1L);
      verify(this.workpackService, never()).criarWorkpackFromPreProject(any(ProjectParamDto.class));
    }
  }

  @Test(expected = RegistroNaoEncontradoException.class)
  public void rejectsMissingPreProject() {
    this.service.create(1L, this.request());
  }

  @Test
  public void createsNewProjectAfterPreviousOneWasDeleted() {
    when(this.preProjectRepository.lockForProjectCreation(1L)).thenReturn(Optional.of(1L));
    when(this.preProjectRepository.findActiveProjectId(1L)).thenReturn(Optional.empty());
    final PreProject preProject = new PreProject();
    preProject.setName("Anteprojeto");
    preProject.setFullName("Anteprojeto de exemplo");
    when(this.preProjectRepository.findByIdThin(1L)).thenReturn(Optional.of(preProject));

    final PlanModel planModel = mock(PlanModel.class);
    when(planModel.getId()).thenReturn(4L);
    final Plan plan = mock(Plan.class);
    when(plan.getPlanModel()).thenReturn(planModel);
    when(this.planService.findById(2L)).thenReturn(plan);

    final ProjectModel projectModel = mock(ProjectModel.class);
    when(projectModel.getId()).thenReturn(5L);
    when(projectModel.getPlanModel()).thenReturn(planModel);
    when(projectModel.getProperties()).thenReturn(Collections.emptySet());
    final WorkpackModel parentModel = mock(WorkpackModel.class);
    when(parentModel.getChildren()).thenReturn(Collections.<WorkpackModel>singleton(projectModel));
    final Workpack parent = mock(Workpack.class);
    when(parent.getWorkpackModelInstance()).thenReturn(parentModel);
    when(this.workpackService.findById(3L)).thenReturn(parent);
    when(this.workpackModelService.findById(5L)).thenReturn(projectModel);

    final Project created = new Project();
    created.setId(9L);
    when(this.workpackService.criarWorkpackFromPreProject(any(ProjectParamDto.class)))
      .thenReturn(created);

    assertSame(created, this.service.create(1L, this.request()));
    verify(this.preProjectRepository).createOriginatedRelationship(1L, 9L);
    verify(this.applicationCacheUtil).loadAllCacheSync();
  }

  private CreateProjectFromPreProjectRequest request() {
    final CreateProjectFromPreProjectRequest request = new CreateProjectFromPreProjectRequest();
    request.setIdPlan(2L);
    request.setIdParent(3L);
    return request;
  }
}
