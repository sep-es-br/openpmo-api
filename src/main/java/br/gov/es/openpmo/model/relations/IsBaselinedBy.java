package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.workpacks.Workpack;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.beans.Transient;

@RelationshipProperties
public class IsBaselinedBy {

  @RelationshipId
  private Long id;

  private Workpack workpack;

  @TargetNode
  private Baseline baseline;

  public IsBaselinedBy() {
  }

  public IsBaselinedBy(
    final Baseline baseline,
    final Workpack workpack
  ) {
    this.workpack = workpack;
    this.baseline = baseline;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public Baseline getBaseline() {
    return this.baseline;
  }

  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
  }

  @Transient
  public Long getIdWorkpack() {
    return this.workpack.getId();
  }

  @Transient
  public Long getIdBaseline() {
    return this.baseline.getId();
  }

}
