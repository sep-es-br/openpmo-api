package br.gov.es.openpmo.service.preprojects;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.CriteriaGroup;
import br.gov.es.openpmo.model.properties.CriteriaList;
import br.gov.es.openpmo.model.properties.CriteriaSelection;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.SelectionOption;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.TabModel;
import br.gov.es.openpmo.model.relations.Accepts;
import br.gov.es.openpmo.utils.ApplicationMessage;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class InstantiatePreProjectProperty {

  public Property execute(final PropertyModel model) {
    if (model instanceof CriteriaTabModel) {
      return this.createTab((CriteriaTabModel) model);
    }
    if (model instanceof CriteriaGroupModel) {
      return this.createGroup((CriteriaGroupModel) model);
    }
    if (model instanceof CriteriaListModel) {
      return this.createList((CriteriaListModel) model);
    }
    if (model instanceof CriteriaSelectionModel) {
      return this.createSelection((CriteriaSelectionModel) model);
    }
    throw new NegocioException(ApplicationMessage.PROPERTY_MODEL_INVALID_TYPE);
  }

  private CriteriaTab createTab(final CriteriaTabModel model) {
    final CriteriaTab property = new CriteriaTab();
    property.setDriver(model);
    property.setValue(this.createChildren(model));
    return property;
  }

  private CriteriaGroup createGroup(final CriteriaGroupModel model) {
    final CriteriaGroup property = new CriteriaGroup();
    property.setDriver(model);
    property.setValue(this.createChildren(model));
    return property;
  }

  private CriteriaList createList(final CriteriaListModel model) {
    final CriteriaList property = new CriteriaList();
    property.setDriver(model);
    property.setValue(new HashSet<>());
    return property;
  }

  private CriteriaSelection createSelection(final CriteriaSelectionModel model) {
    final CriteriaSelection property = new CriteriaSelection();
    property.setDriver(model);
    final Set<SelectionOption> defaultOptions = this.acceptedOptions(model).stream()
      .filter(accepts -> Boolean.TRUE.equals(accepts.getDefaultOption()))
      .map(Accepts::getSelectionOption)
      .collect(Collectors.toSet());
    property.setValue(defaultOptions);
    return property;
  }

  private Set<Property> createChildren(final PropertyModel model) {
    final Set<PropertyModel> children;
    if (model instanceof TabModel) {
      children = ((TabModel) model).getOrganizedProperties();
    } else if (model instanceof GroupModel) {
      children = ((GroupModel) model).getGroupedProperties();
    } else {
      children = Collections.emptySet();
    }
    if (children == null) {
      return this.newIdentitySet();
    }
    final Set<Property> properties = this.newIdentitySet();
    children.stream().map(this::execute).forEach(properties::add);
    return properties;
  }

  private Set<Property> newIdentitySet() {
    return Collections.newSetFromMap(new IdentityHashMap<>());
  }

  private Set<Accepts> acceptedOptions(final CriteriaSelectionModel model) {
    return model.getAcceptedOptions() == null ? Collections.emptySet() : model.getAcceptedOptions();
  }

}
