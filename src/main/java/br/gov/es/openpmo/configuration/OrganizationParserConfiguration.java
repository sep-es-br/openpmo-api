package br.gov.es.openpmo.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
  basePackages = "br.gov.es.pmo.organization_parser.organograma_parser.model"
)
public class OrganizationParserConfiguration {
}
