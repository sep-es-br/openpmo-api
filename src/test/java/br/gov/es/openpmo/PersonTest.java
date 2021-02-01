package br.gov.es.openpmo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.gov.es.openpmo.controller.PersonController;
import br.gov.es.openpmo.dto.ResponseBase;
import br.gov.es.openpmo.dto.person.PersonDto;

@Testcontainers
@SpringBootTest
public class PersonTest extends BaseTest {

    @Autowired
    private PersonController personController;

    @TestConfiguration
    static class Config {

        @Bean
        public Configuration configuration() {
            return new Configuration.Builder().uri(databaseServer.getBoltUrl()).build();
        }
    }

    @Test
    public void shouldList() {
        ResponseEntity<ResponseBase<PersonDto>> response = personController.findByEmail("test@test.com");
        Assertions.assertEquals(204, response.getStatusCodeValue());
        getToken(true);
        response = personController.findByEmail("user.test@openpmo.com");
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getData());
        Assertions.assertTrue(response.getBody().getData().getEmail().equals("user.test@openpmo.com"));
    }
}
