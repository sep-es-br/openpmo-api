package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.actor.PersonController;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.PersonGetByIdDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.Configuration.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest class PersonTest extends BaseTest {

  @Autowired
  private PersonController personController;

  @Test void shouldList() {
    ResponseEntity<ResponseBase<PersonGetByIdDto>> response = this.personController.findByEmail(
      "test@test.com",
      null,
      null
    );
    Assertions.assertEquals(204, response.getStatusCodeValue());
    this.getToken(true);
    response = this.personController.findByEmail("user.test@openpmo.com", null, null);
    Assertions.assertEquals(200, response.getStatusCodeValue());
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getData());
    Assertions.assertEquals("user.test@openpmo.com", response.getBody().getData().getContactEmail());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }
}
