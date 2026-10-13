package br.gov.es.openpmo.service.schedule;

import br.gov.es.openpmo.dto.completed.CompleteWorkpackRequest;
import br.gov.es.openpmo.model.workpacks.Deliverable;
import br.gov.es.openpmo.repository.StepRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.completed.ICompleteWorkpackService;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdateStatusServiceTest {

  @Test
  public void recalculatesAncestorsThroughTheCompletionServiceWhenDeliverableReopens() {
    final WorkpackRepository workpackRepository = mock(WorkpackRepository.class);
    final StepRepository stepRepository = mock(StepRepository.class);
    final ICompleteWorkpackService completionService = mock(ICompleteWorkpackService.class);
    final UpdateStatusService service = new UpdateStatusService(
      workpackRepository, stepRepository, completionService, mock(Logger.class)
    );
    final Deliverable deliverable = new Deliverable();
    deliverable.setId(1L);

    when(workpackRepository.hasScheduleRelated(1L)).thenReturn(true);
    when(stepRepository.hasWorkToCompleteComparingWithMaster(1L)).thenReturn(true);

    service.update(Collections.singletonList(deliverable));

    final ArgumentCaptor<CompleteWorkpackRequest> request =
      ArgumentCaptor.forClass(CompleteWorkpackRequest.class);
    verify(completionService).apply(org.mockito.ArgumentMatchers.eq(1L), request.capture());
    assertFalse(request.getValue().getCompleted());
    verify(workpackRepository, never()).findProject(anyLong());
    verify(workpackRepository, never()).findProgram(anyLong());
  }
}
