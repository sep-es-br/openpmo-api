package br.gov.es.openpmo.service.preprojects;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.CriteriaGroup;
import br.gov.es.openpmo.model.properties.CriteriaList;
import br.gov.es.openpmo.model.properties.CriteriaSelection;
import br.gov.es.openpmo.model.properties.CriteriaTab;
import br.gov.es.openpmo.model.properties.Currency;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.model.properties.Group;
import br.gov.es.openpmo.model.properties.Integer;
import br.gov.es.openpmo.model.properties.LocalitySelection;
import br.gov.es.openpmo.model.properties.Number;
import br.gov.es.openpmo.model.properties.OrganizationSelection;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.properties.Selection;
import br.gov.es.openpmo.model.properties.Text;
import br.gov.es.openpmo.model.properties.TextArea;
import br.gov.es.openpmo.model.properties.Toggle;
import br.gov.es.openpmo.model.properties.UnitSelection;
import br.gov.es.openpmo.model.properties.models.CriteriaGroupModel;
import br.gov.es.openpmo.model.properties.models.CriteriaListModel;
import br.gov.es.openpmo.model.properties.models.CriteriaSelectionModel;
import br.gov.es.openpmo.model.properties.models.CriteriaTabModel;
import br.gov.es.openpmo.model.properties.models.CurrencyModel;
import br.gov.es.openpmo.model.properties.models.DateModel;
import br.gov.es.openpmo.model.properties.models.GroupModel;
import br.gov.es.openpmo.model.properties.models.IntegerModel;
import br.gov.es.openpmo.model.properties.models.LocalitySelectionModel;
import br.gov.es.openpmo.model.properties.models.NumberModel;
import br.gov.es.openpmo.model.properties.models.OrganizationSelectionModel;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.properties.models.SelectionModel;
import br.gov.es.openpmo.model.properties.models.TabModel;
import br.gov.es.openpmo.model.properties.models.TextAreaModel;
import br.gov.es.openpmo.model.properties.models.TextModel;
import br.gov.es.openpmo.model.properties.models.ToggleModel;
import br.gov.es.openpmo.model.properties.models.UnitSelectionModel;
import br.gov.es.openpmo.utils.ApplicationMessage;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
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
    if (model instanceof GroupModel) {
      return this.createGroup((GroupModel) model);
    }
    if (model instanceof IntegerModel) {
      final Integer property = new Integer();
      property.setDriver((IntegerModel) model);
      return property;
    }
    if (model instanceof TextModel) {
      final Text property = new Text();
      property.setDriver((TextModel) model);
      return property;
    }
    if (model instanceof DateModel) {
      final Date property = new Date();
      property.setDriver((DateModel) model);
      return property;
    }
    if (model instanceof ToggleModel) {
      final Toggle property = new Toggle();
      property.setDriver((ToggleModel) model);
      return property;
    }
    if (model instanceof UnitSelectionModel) {
      final UnitSelection property = new UnitSelection();
      property.setDriver((UnitSelectionModel) model);
      return property;
    }
    if (model instanceof SelectionModel) {
      final Selection property = new Selection();
      property.setDriver((SelectionModel) model);
      return property;
    }
    if (model instanceof TextAreaModel) {
      final TextArea property = new TextArea();
      property.setDriver((TextAreaModel) model);
      return property;
    }
    if (model instanceof NumberModel) {
      final Number property = new Number();
      property.setDriver((NumberModel) model);
      return property;
    }
    if (model instanceof CurrencyModel) {
      final Currency property = new Currency();
      property.setDriver((CurrencyModel) model);
      return property;
    }
    if (model instanceof LocalitySelectionModel) {
      final LocalitySelection property = new LocalitySelection();
      property.setDriver((LocalitySelectionModel) model);
      return property;
    }
    if (model instanceof OrganizationSelectionModel) {
      final OrganizationSelection property = new OrganizationSelection();
      property.setDriver((OrganizationSelectionModel) model);
      return property;
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
    property.setActive(!model.isEnablementKey());
    property.setValue(this.createChildren(model));
    return property;
  }

  private Group createGroup(final GroupModel model) {
    final Group property = new Group();
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
    // Criteria values belong to the explicit save action on the criterion tab.
    // Do not persist model default options when the pre-project is created.
    property.setValue(Collections.emptySet());
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

}
