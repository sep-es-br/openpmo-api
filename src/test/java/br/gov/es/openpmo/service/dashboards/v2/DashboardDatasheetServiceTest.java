package br.gov.es.openpmo.service.dashboards.v2;

import br.gov.es.openpmo.dto.dashboards.DashboardParameters;
import br.gov.es.openpmo.dto.dashboards.datasheet.DatasheetResponse;
import br.gov.es.openpmo.dto.dashboards.datasheet.WorkpackByModelQueryResult;
import br.gov.es.openpmo.dto.workpackmodel.TransversalStructuralLinkDto;
import br.gov.es.openpmo.repository.TransversalRepository;
import br.gov.es.openpmo.repository.dashboards.DashboardDatasheetRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DashboardDatasheetServiceTest {

  private static final Long PROGRAM_ID = 1558826L;
  private DashboardDatasheetRepository repository;
  private TransversalRepository transversalRepository;
  private DashboardDatasheetService service;

  @Before
  public void setUp() {
    this.repository = mock(DashboardDatasheetRepository.class);
    this.transversalRepository = mock(TransversalRepository.class);
    this.service = new DashboardDatasheetService(this.repository, this.transversalRepository);
  }

  @Test
  public void shouldUseStructuralLinksForTransversalTotalizersAndDeliverableTotal() {
    when(this.repository.workpackByModelForTransversal(PROGRAM_ID))
      .thenReturn(Collections.<WorkpackByModelQueryResult>emptyList());
    when(this.repository.stakeholders(PROGRAM_ID)).thenReturn(Collections.emptyList());
    when(this.transversalRepository.findStructuralLinks(PROGRAM_ID, null, null, true, true))
      .thenReturn(Arrays.asList(
        link(1L, 158L, "Entrega", "Entregas", "Deliverable"),
        link(2L, 158L, "Entrega", "Entregas", "Deliverable"),
        link(3L, 158L, "Entrega", "Entregas", "Deliverable"),
        link(4L, 158L, "Entrega", "Entregas", "Deliverable"),
        link(5L, 158L, "Entrega", "Entregas", "Deliverable"),
        link(6L, 158L, "Entrega", "Entregas", "Deliverable"),
        link(7L, 119L, "Projeto", "Projetos", "Project"),
        pathNode(8L, 119L, "Projeto", "Projetos", "Project")
      ));

    final DashboardParameters parameters = new DashboardParameters(
      true, PROGRAM_ID, null, null, 182L, null, null, false, null, UriComponentsBuilder.newInstance()
    );
    parameters.setTransversal(true);

    final DatasheetResponse response = this.service.build(parameters);
    final List<?> totalizers = response.getDatasheetTotalizers().getWorkpacksByModel();

    assertEquals(2, totalizers.size());
    assertEquals(Long.valueOf(6L), response.getDatasheetTotalizers().getWorkpacksByModel().get(0).getQuantity());
    assertEquals("Entregas", response.getDatasheetTotalizers().getWorkpacksByModel().get(0).getModelName());
    assertEquals(Long.valueOf(6L), response.getDatasheetTotalizers().getTransversalDeliverableCount());
  }

  private static TransversalStructuralLinkDto link(
    final Long workpackId,
    final Long modelId,
    final String modelName,
    final String pluralName,
    final String type
  ) {
    final TransversalStructuralLinkDto link = new TransversalStructuralLinkDto();
    link.setIdWorkpack(workpackId);
    link.setIdWorkpackModel(modelId);
    link.setModelName(modelName);
    link.setModelNameInPlural(pluralName);
    link.setWorkpackModelType(type);
    link.setFontIcon("icon");
    link.setClickable(true);
    return link;
  }

  private static TransversalStructuralLinkDto pathNode(
    final Long workpackId,
    final Long modelId,
    final String modelName,
    final String pluralName,
    final String type
  ) {
    final TransversalStructuralLinkDto link = link(workpackId, modelId, modelName, pluralName, type);
    link.setClickable(false);
    return link;
  }
}
