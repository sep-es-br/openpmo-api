package br.gov.es.openpmo.repository.custom;

import br.gov.es.openpmo.dto.stakeholder.StakeholderAndPermissionQuery;
import br.gov.es.openpmo.model.filter.CustomFilter;
import br.gov.es.openpmo.model.filter.Rules;
import br.gov.es.openpmo.model.relations.CanAccessWorkpack;
import br.gov.es.openpmo.model.relations.IsStakeholderIn;
import org.neo4j.ogm.model.Result;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public abstract class FindStakeholderAndPermissionUsingCustomFilterBuilder extends AbstractCustomFilterBuilder {

  public Optional<StakeholderAndPermissionQuery> execute(
    final CustomFilter filter,
    final Map<String, Object> params
  ) {
    this.validateArgs(
      filter,
      params
    );
    this.verifyExternalParam(params);

    final String query = this.buildQuery(
      filter,
      params
    );

    final Result result = this.getSession().query(
      query,
      params
    );
    final Iterable<Map<String, Object>> maps = result.queryResults();
    final Map<String, Object> objectMap = maps.iterator().next();

    return Optional.ofNullable(objectMap).map(this::getPermissionQuery);
  }

  private StakeholderAndPermissionQuery getPermissionQuery(final Map<String, Object> objectMap) {
    return new StakeholderAndPermissionQuery(
      this.getStakeholders(objectMap),
      this.getPermissions(objectMap)
    );
  }

  @SuppressWarnings("unchecked")
  private ArrayList<CanAccessWorkpack> getPermissions(final Map<String, Object> objectMap) {
    try {
      return (ArrayList<CanAccessWorkpack>) objectMap.get("permissions");
    }
    catch (final ClassCastException exception) {
      return new ArrayList<>();
    }
  }

  @SuppressWarnings("unchecked")
  private ArrayList<IsStakeholderIn> getStakeholders(final Map<String, Object> objectMap) {
    try {
      return (ArrayList<IsStakeholderIn>) objectMap.get("stakeholders");
    }
    catch (final ClassCastException exception) {
      return new ArrayList<>();
    }
  }

  @Override
  protected String buildCustomFilterRule(
    final Rules rule,
    final String label
  ) {
    final PropertyValue propertyValue = PropertyValue.find(rule.getPropertyName());
    return propertyValue.getCondition(
      rule.getRelationalOperator().getOperador(),
      label
    ) + " ";
  }

  @Override
  protected void buildOrderingAndDirectionClause(
    final CustomFilter filter,
    final Map<String, Object> params,
    final StringBuilder query
  ) {
    // sobrescreve ordenação padrão.
  }

  protected enum PropertyValue {
    NAME(
      "name",
      "actor",
      "person"
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s OR %s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label,
          this.secondNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s, %s.%s",
          this.firstNode,
          this.property,
          this.secondNode,
          this.property
        );
      }
    },
    FULL_NAME(
      "fullName",
      "actor",
      "person"
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s OR %s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label,
          this.secondNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s, %s.%s",
          this.firstNode,
          this.property,
          this.secondNode,
          this.property
        );
      }
    },
    SECTOR(
      "sector",
      "actor",
      null
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s",
          this.firstNode,
          this.property
        );
      }
    },
    ADDRESS(
      "address",
      "contact",
      "actor"
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s OR %s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label,
          this.secondNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s, %s.%s",
          this.firstNode,
          this.property,
          this.secondNode,
          this.property
        );
      }
    },
    CONTACT_EMAIL(
      "contactEmail",
      "contact",
      "actor"
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s OR %s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label,
          this.secondNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s, %s.%s",
          this.firstNode,
          this.property,
          this.secondNode,
          this.property
        );
      }
    },
    PHONE_NUMBER(
      "phoneNumber",
      "contact",
      "actor"
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s OR %s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label,
          this.secondNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s, %s.%s",
          this.firstNode,
          this.property,
          this.secondNode,
          this.property
        );
      }
    },
    ADMINISTRATOR(
      "administrator",
      "person",
      "actor"
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s OR %s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label,
          this.secondNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s, %s.%s",
          this.firstNode,
          this.property,
          this.secondNode,
          this.property
        );
      }
    },
    LEVEL(
      "level",
      "canAccessWorkpack",
      null
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(%s.%s %s $%s)",
          this.firstNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s",
          this.firstNode,
          this.property
        );
      }
    },
    ROLE(
      "role",
      "isStakeholderIn",
      null
    ) {
      @Override
      String getCondition(
        final String operator,
        final String label
      ) {
        return String.format(
          "(toLower(%s.%s) %s toLower($%s))",
          this.firstNode,
          this.property,
          operator,
          label
        );
      }

      @Override
      String getOrdering(final CustomFilter filter) {
        return String.format(
          "ORDER BY %s.%s",
          this.firstNode,
          this.property
        );
      }
    };

    protected final String property;

    protected final String firstNode;

    protected final String secondNode;

    PropertyValue(
      final String property,
      final String firstNode,
      final String secondNode
    ) {
      this.property = property;
      this.firstNode = firstNode;
      this.secondNode = secondNode;
    }

    public static PropertyValue find(final String property) {
      for (final PropertyValue value : PropertyValue.values()) {
        if (value.property.equals(property)) {
          return value;
        }
      }
      throw new IllegalArgumentException("error");
    }

    abstract String getCondition(
      String operator,
      String label
    );

    abstract String getOrdering(CustomFilter filter);
  }

}
