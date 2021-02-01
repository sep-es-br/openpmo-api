package br.gov.es.openpmo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import br.gov.es.openpmo.model.relations.Consumes;

@NodeEntity
public class Step extends Entity {

	private BigDecimal actualWork;
	private BigDecimal plannedWork;
	private LocalDate periodFromStart;

	@Relationship(type = "COMPOSES")
	private Schedule schedule;

	@Relationship("CONSUMES")
	private Set<Consumes> consumes;

	public LocalDate getPeriodFromStart() {
		return periodFromStart;
	}

	public void setPeriodFromStart(LocalDate periodFromStart) {
		this.periodFromStart = periodFromStart;
	}

	public BigDecimal getActualWork() {
		return actualWork;
	}

	public void setActualWork(BigDecimal actualWork) {
		this.actualWork = actualWork;
	}

	public BigDecimal getPlannedWork() {
		return plannedWork;
	}

	public void setPlannedWork(BigDecimal plannedWork) {
		this.plannedWork = plannedWork;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Set<Consumes> getConsumes() {
		return consumes;
	}

	public void setConsumes(Set<Consumes> consumes) {
		this.consumes = consumes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		Step step = (Step) o;
		return Objects.equals(actualWork, step.actualWork) && Objects.equals(plannedWork, step.plannedWork)
			&& Objects.equals(periodFromStart, step.periodFromStart);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), actualWork, plannedWork, periodFromStart);
	}
}
