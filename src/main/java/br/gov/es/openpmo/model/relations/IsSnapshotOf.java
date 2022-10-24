package br.gov.es.openpmo.model.relations;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public abstract class IsSnapshotOf<T> {

  private final T snapshot;

  @TargetNode
  private final T master;

  private final LocalDateTime date;

  @RelationshipId
  private Long id;

  protected IsSnapshotOf(
    final T master,
    final T snapshot
  ) {
    this.master = master;
    this.snapshot = snapshot;
    this.date = LocalDateTime.now();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public LocalDateTime getDate() {
    return this.date;
  }

  public T getMaster() {
    return this.master;
  }

  public T getSnapshot() {
    return this.snapshot;
  }

}
