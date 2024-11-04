package br.gov.es.openpmo.model.workpacks;

import br.gov.es.openpmo.enumerator.CategoryEnum;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.baselines.Baseline;
import br.gov.es.openpmo.model.baselines.Snapshotable;
import br.gov.es.openpmo.model.budget.PlanoOrcamentario;
import br.gov.es.openpmo.model.budget.UnidadeOrcamentaria;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.relations.IsCostAccountSnapshotOf;
import br.gov.es.openpmo.model.workpacks.models.CostAccountModel;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.HashSet;
import java.util.Set;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@NodeEntity
public class CostAccount extends Entity implements Snapshotable<CostAccount> {

  @Relationship(type = "IS_SNAPSHOT_OF")
  private IsCostAccountSnapshotOf master;

  @Relationship(type = "IS_SNAPSHOT_OF", direction = INCOMING)
  private Set<IsCostAccountSnapshotOf> snapshots;

  @Relationship(type = "COMPOSES")
  private Baseline baseline;

  @Relationship(type = "FEATURES", direction = Relationship.INCOMING)
  private Set<Property> properties;

  @Relationship(type = "APPLIES_TO")
  private Workpack workpack;

  @Relationship("IS_INSTANCE_BY")
  private CostAccountModel instance;

  private CategoryEnum category;

  @Relationship(type = "CONTROLS", direction = INCOMING)
  private UnidadeOrcamentaria unidadeOrcamentaria;

  @Relationship(type = "ASSIGNED", direction = INCOMING)
  private PlanoOrcamentario planoOrcamentario;

  public CostAccount() {
  }

  public Set<Property> getProperties() {
    return this.properties;
  }

  public void setProperties(final Set<Property> properties) {
    this.properties = properties;
  }

  public Workpack getWorkpack() {
    return this.workpack;
  }

  public void setWorkpack(final Workpack workpack) {
    this.workpack = workpack;
  }

  public CostAccountModel getInstance() {
    return instance;
  }

  public void setInstance(CostAccountModel instance) {
    this.instance = instance;
  }

  @Transient
  public Long getWorkpackId() {
    return this.workpack.getId();
  }

  @Override
  public CostAccount snapshot() {
    return new CostAccount();
  }

  @Override
  public Baseline getBaseline() {
    return this.baseline;
  }

  @Override
  public void setBaseline(final Baseline baseline) {
    this.baseline = baseline;
  }

  @Override
  public CategoryEnum getCategory() {
    return this.category;
  }

  @Override
  public void setCategory(final CategoryEnum category) {
    this.category = category;
  }

  @Override
  public boolean hasChanges(final CostAccount other) {
    return false;
  }

  public IsCostAccountSnapshotOf getMaster() {
    return this.master;
  }

  public void setMaster(final IsCostAccountSnapshotOf master) {
    this.master = master;
  }

  public Set<IsCostAccountSnapshotOf> getSnapshots() {
    return this.snapshots;
  }

  public void setSnapshots(final Set<IsCostAccountSnapshotOf> snapshots) {
    this.snapshots = snapshots;
  }

  public Set<PropertyModel> getPropertyModels() {
    final Set<PropertyModel> propertyModels = new HashSet<>();
    this.getProperties().forEach(property -> propertyModels.add(property.getPropertyModel()));
    return propertyModels;
  }

  public UnidadeOrcamentaria getUnidadeOrcamentaria() {
    return unidadeOrcamentaria;
  }

  public void setUnidadeOrcamentaria(UnidadeOrcamentaria unidadeOrcamentaria) {
    this.unidadeOrcamentaria = unidadeOrcamentaria;
  }

  public PlanoOrcamentario getPlanoOrcamentario() {
    return planoOrcamentario;
  }

  public void setPlanoOrcamentario(PlanoOrcamentario planoOrcamentario) {
    this.planoOrcamentario = planoOrcamentario;
  }
}
