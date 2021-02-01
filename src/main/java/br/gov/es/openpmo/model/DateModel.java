package br.gov.es.openpmo.model;

import java.time.LocalDateTime;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DateModel extends PropertyModel {

	private LocalDateTime min;
	private LocalDateTime max;
	private LocalDateTime defaultValue;

	public LocalDateTime getMin() {
		return min;
	}

	public void setMin(LocalDateTime min) {
		this.min = min;
	}

	public LocalDateTime getMax() {
		return max;
	}

	public void setMax(LocalDateTime max) {
		this.max = max;
	}

	public LocalDateTime getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(LocalDateTime defaultValue) {
		this.defaultValue = defaultValue;
	}
}
