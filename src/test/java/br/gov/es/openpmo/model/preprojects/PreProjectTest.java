package br.gov.es.openpmo.model.preprojects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import br.gov.es.openpmo.model.actors.Organization;
import java.time.LocalDate;
import org.junit.Test;
import org.neo4j.ogm.annotation.Relationship;

public class PreProjectTest {

  @Test
  public void shouldStorePreProjectBasicData() {
    final PreProject preProject = new PreProject();
    final Organization organization = new Organization();
    final LocalDate expectedCompletionDate = LocalDate.of(2026, 12, 31);

    preProject.setExpectedCompletionDate(expectedCompletionDate);
    preProject.setExpectedDeliveries("Produto implantado e equipe capacitada");
    preProject.setOrganization(organization);

    assertEquals(expectedCompletionDate, preProject.getExpectedCompletionDate());
    assertEquals("Produto implantado e equipe capacitada", preProject.getExpectedDeliveries());
    assertSame(organization, preProject.getOrganization());
  }

  @Test
  public void shouldMapOrganizationAsIncomingIsRelationship() throws Exception {
    final Relationship relationship = PreProject.class
      .getDeclaredField("organization")
      .getAnnotation(Relationship.class);

    assertEquals("IS", relationship.value());
    assertEquals(Relationship.INCOMING, relationship.direction());
  }

}
