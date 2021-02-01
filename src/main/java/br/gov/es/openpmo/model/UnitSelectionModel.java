package br.gov.es.openpmo.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class UnitSelectionModel extends PropertyModel {

    @Relationship(value = "DEFAULTS_TO")
	UnitMeasure defaultValue;

    public UnitMeasure getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(UnitMeasure defaultValue) {
        this.defaultValue = defaultValue;
    }
}
