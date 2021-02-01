package br.gov.es.openpmo.model;
import java.lang.Integer;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class NumberModel extends PropertyModel {

	private Double defaultValue;
	private Double min;
	private Double max;
	private Integer decimals;

	public Double getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Double defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

}
