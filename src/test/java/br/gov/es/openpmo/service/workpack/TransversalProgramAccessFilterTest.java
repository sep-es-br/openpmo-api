package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpackmodel.TransversalContextDto;
import br.gov.es.openpmo.dto.workpackmodel.TransversalProgramOptionDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TransversalProgramAccessFilterTest {

  private static final Long PLAN_ID = 182L;
  private static final Long VIEW_ID = 1144621L;

  private TransversalProjectService service;

  @Before
  public void setUp() {
    this.service = new TransversalProjectService(null);
  }

  @Test
  public void shouldKeepOnlyAccessibleProgramAndItsDescendants() {
    final List<TransversalProgramOptionDto> programs = Arrays.asList(
      program(1558785L, null, VIEW_ID, PLAN_ID),
      program(1558841L, 1558826L, VIEW_ID, PLAN_ID),
      program(1558826L, null, VIEW_ID, PLAN_ID),
      program(1558827L, 1558841L, VIEW_ID, PLAN_ID),
      program(1558814L, null, VIEW_ID, PLAN_ID)
    );
    final List<TransversalProgramOptionDto> result = this.service.filterProgramsForAccessContexts(
      programs,
      Collections.singletonList(context(1558826L, VIEW_ID, PLAN_ID)),
      VIEW_ID,
      PLAN_ID
    );

    assertEquals(Arrays.asList(1558841L, 1558826L, 1558827L), ids(result));
  }

  @Test
  public void shouldIgnoreAccessContextsFromAnotherViewOrPlan() {
    final List<TransversalProgramOptionDto> programs = Arrays.asList(
      program(1558826L, null, VIEW_ID, PLAN_ID),
      program(1558841L, 1558826L, VIEW_ID, PLAN_ID)
    );
    final List<TransversalProgramOptionDto> result = this.service.filterProgramsForAccessContexts(
      programs,
      Arrays.asList(
        context(1558826L, VIEW_ID + 1, PLAN_ID),
        context(1558826L, VIEW_ID, PLAN_ID + 1)
      ),
      VIEW_ID,
      PLAN_ID
    );

    assertEquals(Collections.emptyList(), result);
  }

  private static List<Long> ids(final List<TransversalProgramOptionDto> programs) {
    return programs.stream().map(TransversalProgramOptionDto::getIdTransversalProgram).collect(Collectors.toList());
  }

  private static TransversalProgramOptionDto program(
    final Long id,
    final Long idParent,
    final Long idView,
    final Long idPlan
  ) {
    final TransversalProgramOptionDto program = new TransversalProgramOptionDto();
    program.setIdTransversalProgram(id);
    program.setIdParent(idParent);
    program.setIdTransversalView(idView);
    program.setIdPlan(idPlan);
    return program;
  }

  private static TransversalContextDto context(final Long idProgram, final Long idView, final Long idPlan) {
    final TransversalContextDto context = new TransversalContextDto();
    context.setIdTransversalProgram(idProgram);
    context.setIdTransversalView(idView);
    context.setIdPlan(idPlan);
    return context;
  }
}
