package br.gov.es.openpmo.service;

import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModelClassification;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WorkpackTransversalContractTest {

  @Test
  public void shouldTreatMissingClassificationAsStructural() {
    final ProjectModel model = new ProjectModel();

    assertEquals(WorkpackModelClassification.STRUCTURAL, model.getClassification());

    model.setClassification(null);

    assertEquals(WorkpackModelClassification.STRUCTURAL, model.getClassification());
  }

  @Test
  public void shouldExposeTransversalRelationsWithDomainDirections() {
    final Program program = new Program();
    final ProgramModel view = new ProgramModel();
    final Project project = new Project();

    program.getTransversalViews().add(view);
    program.getProjects().add(project);

    assertTrue(program.getTransversalViews().contains(view));
    assertTrue(program.getProjects().contains(project));
  }
}
