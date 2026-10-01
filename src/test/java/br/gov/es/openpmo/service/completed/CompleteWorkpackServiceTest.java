package br.gov.es.openpmo.service.completed;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.model.workpacks.Organizer;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.repository.completed.CompletedRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static br.gov.es.openpmo.utils.ApplicationMessage.PROJECT_COMPLETION_REQUIREMENTS_NOT_MET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CompleteWorkpackServiceTest {

  private CompletedRepository repository;
  private WorkpackRepository workpackRepository;
  private CompleteWorkpackService service;

  @Before
  public void setUp() {
    this.repository = mock(CompletedRepository.class);
    this.workpackRepository = mock(WorkpackRepository.class);
    this.service = new CompleteWorkpackService(this.repository, this.workpackRepository);
  }

  @Test
  public void completesProjectThroughAnIncompleteOrganizer() {
    final Deliverable deliverable = new Deliverable();
    deliverable.setId(1L);
    when(this.repository.findById(1L)).thenReturn(Optional.of(deliverable));
    when(this.repository.getAncestorIds(1L)).thenReturn(Arrays.asList(2L, 3L));
    when(this.repository.allSonsAreCompleted(2L)).thenReturn(false);
    when(this.workpackRepository.isProject(3L)).thenReturn(true);
    when(this.repository.allProjectDeliverablesAndMilestonesAreCompleted(3L)).thenReturn(true);

    this.service.apply(1L, new CompleteWorkpackRequest(true, null));

    verify(this.repository).setCompleted(2L, false);
    verify(this.repository).setCompleted(3L, true);
    verify(this.workpackRepository).updateSituationValue(3L, "Concluído");
  }

  @Test
  public void rejectsDirectCompletionOfProjectWithoutEligibleChildren() {
    final Project project = new Project();
    project.setId(3L);
    when(this.repository.findById(3L)).thenReturn(Optional.of(project));
    when(this.repository.allProjectDeliverablesAndMilestonesAreCompleted(3L)).thenReturn(false);

    try {
      this.service.apply(3L, new CompleteWorkpackRequest(true, null));
      fail("A project without completed deliverables or milestones must not be completed");
    } catch (NegocioException expected) {
      assertEquals(PROJECT_COMPLETION_REQUIREMENTS_NOT_MET, expected.getMessage());
    }

    verify(this.repository, never()).setCompleted(3L, true);
  }

  @Test
  public void resetsProjectWhenLastEligibleChildIsDeleted() {
    final Deliverable deliverable = new Deliverable();
    deliverable.setId(1L);
    when(this.repository.getAncestorIds(1L)).thenReturn(Collections.singletonList(3L));
    when(this.workpackRepository.isProject(3L)).thenReturn(true);
    when(this.repository.allProjectDeliverablesAndMilestonesAreCompleted(3L)).thenReturn(false);
    when(this.workpackRepository.isSituationCompleted(3L)).thenReturn(true);

    this.service.onWorkpackDeleted(deliverable);

    verify(this.repository).setCompleted(3L, false);
    verify(this.workpackRepository).resetSituationOrStatusToDefault(3L);
  }

  @Test
  public void creatingAnOrganizerDoesNotReopenCompletedProject() {
    final Organizer organizer = new Organizer();
    organizer.setId(2L);
    when(this.repository.getAncestorIds(2L)).thenReturn(Collections.singletonList(3L));
    when(this.workpackRepository.isProject(3L)).thenReturn(true);
    when(this.repository.allProjectDeliverablesAndMilestonesAreCompleted(3L)).thenReturn(true);

    this.service.onWorkpackCreated(organizer);

    verify(this.repository).setCompleted(3L, true);
    verify(this.workpackRepository, never()).resetSituationOrStatusToDefault(3L);
  }
}
