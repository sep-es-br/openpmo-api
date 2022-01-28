package br.gov.es.openpmo.repository.dashboards;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseRepositoryTest {

  @Container
  protected static final Neo4jContainer<?> container = getContainer();

  private static Neo4jContainer<?> getContainer() {
    return new Neo4jContainer(getDockerImageName()).withoutAuthentication();
  }

  private static @NotNull DockerImageName getDockerImageName() {
    return DockerImageName.parse("neo4j").withTag("3.5.0");
  }

}
