package br.gov.es.openpmo.configuration;

import br.gov.es.pmo.organization_parser.pmo_base.model.IWorkLocationParser;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertNotNull;

public class OrganizationParserConfigurationTest {

  @Test
  public void shouldRegisterTheWorkLocationParser() {
    try(final AnnotationConfigApplicationContext context =
          new AnnotationConfigApplicationContext(OrganizationParserConfiguration.class)) {
      assertNotNull(context.getBean(IWorkLocationParser.class));
    }
  }
}
