package br.gov.es.openpmo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@EnableNeo4jRepositories("br.gov.es.openpmo.repository")
@SpringBootApplication
public class OpenPmoApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenPmoApplication.class, args);
	}

}
