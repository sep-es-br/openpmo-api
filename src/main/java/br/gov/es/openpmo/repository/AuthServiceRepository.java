package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.AuthService;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface AuthServiceRepository extends Neo4jRepository<AuthService, Long> {

  Optional<AuthService> findAuthServiceByServer(String server);

}
