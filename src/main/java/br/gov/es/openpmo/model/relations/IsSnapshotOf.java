package br.gov.es.openpmo.model.relations;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.StartNode;

import java.time.LocalDateTime;

public abstract class IsSnapshotOf<T> {

  @StartNode
  private final T snapshot;

  @EndNode
  private final T master;

  private final LocalDateTime date;

  @Id
  @GeneratedValue
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
