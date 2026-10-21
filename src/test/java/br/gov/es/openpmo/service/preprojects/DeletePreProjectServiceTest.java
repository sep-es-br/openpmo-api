package br.gov.es.openpmo.service.preprojects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.exception.RegistroNaoEncontradoException;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.repository.PreProjectRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessService;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

public class DeletePreProjectServiceTest {

  private final PreProjectRepository preProjectRepository = mock(PreProjectRepository.class);
  private final WorkpackRepository workpackRepository = mock(WorkpackRepository.class);
  private final WorkpackService workpackService = mock(WorkpackService.class);
  private final ICompleteWorkpackService completeWorkpackService = mock(ICompleteWorkpackService.class);
  private final ICanAccessService canAccessService = mock(ICanAccessService.class);
  private DeletePreProjectService service;

  @Before
  public void setUp() {
    this.service = new DeletePreProjectService(
      this.preProjectRepository,
      this.workpackRepository,
      this.workpackService,
      this.completeWorkpackService,
      this.canAccessService
    );
  }

  @Test
  public void deletesPreProjectAndOwnedPropertiesInElaboration() {
    when(this.preProjectRepository.existsById(1L)).thenReturn(true);
    when(this.preProjectRepository.findActiveProjectId(1L)).thenReturn(Optional.empty());

    this.service.delete(1L, null, "token");

    verify(this.preProjectRepository).deleteOwnedProperties(1L);
    verify(this.preProjectRepository).deletePreProject(1L);
    verify(this.workpackService, never()).delete(org.mockito.ArgumentMatchers.any());
  }

  @Test
  public void deletesOnlyProjectInStructuring() {
    final Project project = new Project();
    project.setId(2L);
    when(this.preProjectRepository.existsById(1L)).thenReturn(true);
    when(this.preProjectRepository.findActiveProjectId(1L, 2L)).thenReturn(Optional.of(2L));
    when(this.preProjectRepository.findStructuringProjectId(1L, 2L)).thenReturn(Optional.of(2L));
    when(this.workpackService.findById(2L)).thenReturn(project);

    this.service.delete(1L, 2L, "token");

    verify(this.canAccessService).ensureCanEditResource(2L, "token");
    verify(this.workpackService).delete(project);
    verify(this.completeWorkpackService).onWorkpackDeleted(project);
    verify(this.preProjectRepository, never()).deleteOwnedProperties(1L);
    verify(this.preProjectRepository, never()).deletePreProject(1L);
  }

  @Test(expected = NegocioException.class)
  public void doesNotDeleteProjectOutsideStructuring() {
    when(this.preProjectRepository.existsById(1L)).thenReturn(true);
    when(this.preProjectRepository.findActiveProjectId(1L, 2L)).thenReturn(Optional.of(2L));

    this.service.delete(1L, 2L, "token");
  }

  @Test(expected = NegocioException.class)
  public void doesNotDeletePreProjectWhileItHasAnActiveProject() {
    when(this.preProjectRepository.existsById(1L)).thenReturn(true);
    when(this.preProjectRepository.findActiveProjectId(1L)).thenReturn(Optional.of(2L));

    this.service.delete(1L, null, "token");
  }

  @Test(expected = NegocioException.class)
  public void doesNotDeleteAnUnrelatedProject() {
    when(this.preProjectRepository.existsById(1L)).thenReturn(true);

    this.service.delete(1L, 99L, "token");
  }

  @Test(expected = NegocioException.class)
  public void doesNotDeleteProjectWithActiveBaseline() {
    when(this.preProjectRepository.existsById(1L)).thenReturn(true);
    when(this.preProjectRepository.findActiveProjectId(1L, 2L)).thenReturn(Optional.of(2L));
    when(this.preProjectRepository.findStructuringProjectId(1L, 2L)).thenReturn(Optional.of(2L));
    when(this.workpackRepository.hasActiveBaseline(2L)).thenReturn(true);

    this.service.delete(1L, 2L, "token");
  }

  @Test(expected = RegistroNaoEncontradoException.class)
  public void doesNotDeleteMissingPreProject() {
    this.service.delete(1L, null, "token");
  }
}
