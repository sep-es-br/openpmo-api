package br.gov.es.openpmo.integration;

import br.gov.es.openpmo.controller.actor.PersonController;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.PersonGetByIdDto;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.config.Configuration.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
class PersonTest extends BaseTest {

  @Autowired
  private PersonController personController;

  @Test void shouldList() {
    ResponseEntity<ResponseBase<PersonGetByIdDto>> response = this.personController.findByEmail(
      "test@test.com",
      null,
      null
    );
    assertEquals(204, response.getStatusCodeValue());
    this.getToken(true);
    response = this.personController.findByEmail("user.test@openpmo.com", null, null);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getData());
  }

  @TestConfiguration
  static class Config {

    @Bean
    public Configuration configuration() {
      return new Builder().uri(databaseServer.getBoltUrl()).build();
    }
  }
}
