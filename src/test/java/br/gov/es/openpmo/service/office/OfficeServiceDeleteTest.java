package br.gov.es.openpmo.service.office;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.repository.OfficeRepository;
import br.gov.es.openpmo.repository.PlanRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class OfficeServiceDeleteTest {
  private final OfficeRepository offices = mock(OfficeRepository.class);
  private final PlanRepository plans = mock(PlanRepository.class);
  private final WorkpackRepository workpacks = mock(WorkpackRepository.class);
  private final OfficeService service = new OfficeService(offices, null, null, null, null, null, plans, workpacks);

  @Test
  public void deletesEmptyPlanBeforeOffice() {
    final Office office = new Office();
    office.setId(1L);
    final Plan plan = new Plan();
    plan.setId(2L);
    when(plans.findActiveByOfficeId(1L)).thenReturn(Arrays.asList(plan));

    service.delete(office);

    org.mockito.InOrder order = inOrder(plans, offices);
    order.verify(plans).delete(plan);
    order.verify(offices).delete(office);
  }

  @Test
  public void preservesPlanAndOfficeWhenDeletedWorkpacksRemain() {
    final Office office = new Office();
    office.setId(1L);
    final Plan plan = new Plan();
    plan.setId(2L);
    when(plans.findActiveByOfficeId(1L)).thenReturn(Arrays.asList(plan));
    when(workpacks.existsRootWorkpackByPlanId(2L)).thenReturn(true);
    when(plans.existsByOfficeId(1L)).thenReturn(true);

    service.delete(office);

    verify(plans).setPlanDeleted(2L);
    verify(offices).setOfficeDeleted(1L);
    verify(plans, never()).delete(any(Plan.class));
    verify(offices, never()).delete(any(Office.class));
  }

  @Test
  public void checksAllPlansBeforeDeletingAny() {
    final Office office = new Office();
    office.setId(1L);
    final Plan empty = new Plan();
    empty.setId(2L);
    final Plan active = new Plan();
    active.setId(3L);
    when(plans.findActiveByOfficeId(1L)).thenReturn(Arrays.asList(empty, active));
    when(workpacks.existsActiveRootWorkpackByPlanId(3L)).thenReturn(true);

    try {
      service.delete(office);
      fail("Office with active workpacks must remain");
    } catch (NegocioException expected) {
      verify(plans, never()).delete(any(Plan.class));
      verify(plans, never()).setPlanDeleted(anyLong());
      verifyNoInteractions(offices);
    }
  }
}
