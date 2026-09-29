package br.gov.es.openpmo.service;

import br.gov.es.openpmo.model.workpacks.Program;
import br.gov.es.openpmo.model.workpacks.Project;
import br.gov.es.openpmo.model.workpacks.models.ProgramModel;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModelClassification;
import br.gov.es.openpmo.model.properties.models.TransversalViewSelectionModel;
import br.gov.es.openpmo.model.properties.models.SelectionModel;
import br.gov.es.openpmo.dto.workpackmodel.WorkpackModelUsesRequest;
import br.gov.es.openpmo.dto.workpack.ProgramParamDto;
import br.gov.es.openpmo.dto.workpackmodel.params.ProgramModelParamDto;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

  @Test
  public void shouldAcceptUseTargetIds() {
    final WorkpackModelUsesRequest request = new WorkpackModelUsesRequest();
    request.setIdsWorkpackModel(new LinkedHashSet<>(Arrays.asList(10L, 20L)));

    assertEquals(new LinkedHashSet<>(Arrays.asList(10L, 20L)), request.getIdsWorkpackModel());
  }

  @Test
  public void shouldExposeTransversalViewSelectionContract() {
    final TransversalViewSelectionModel property = new TransversalViewSelectionModel();

    assertFalse(SelectionModel.class.isAssignableFrom(TransversalViewSelectionModel.class));
    property.setPossibleValues("Programa A,Programa B");
    property.setMultipleSelection(true);

    assertEquals("Programa A,Programa B", property.getPossibleValues());
    assertTrue(property.isMultipleSelection());
  }

  @Test
  public void shouldRejectBlankNamesForTransversalPrograms() {
    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    final ProgramParamDto program = new ProgramParamDto();
    program.setIdPlan(1L);
    program.setIdWorkpackModel(2L);
    program.setName("   ");
    program.setFullName("\t");

    assertEquals(2, validator.validate(program).size());
  }

  @Test
  public void shouldRejectWhitespaceOnlyModelNames() {
    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    final ProgramModelParamDto model = new ProgramModelParamDto();
    model.setFontIcon("fas fa-cogs");
    model.setModelName("   ");
    model.setModelNameInPlural("\t");
    model.setPosition(1L);
    model.setIdPlanModel(102L);

    assertEquals(2, validator.validate(model).size());
  }
}
