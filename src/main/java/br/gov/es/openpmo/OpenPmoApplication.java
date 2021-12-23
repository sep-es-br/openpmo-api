package br.gov.es.openpmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableNeo4jRepositories("br.gov.es.openpmo.repository")
public class OpenPmoApplication {

  public static void main(final String[] args) {
    SpringApplication.run(OpenPmoApplication.class, args);
  }

}
