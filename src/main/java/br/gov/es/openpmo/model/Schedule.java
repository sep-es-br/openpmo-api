package br.gov.es.openpmo.model;

import java.time.LocalDate;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Schedule extends Entity {

	private LocalDate end;
	private LocalDate start;

	@Relationship(type = "FEATURES")
	private Workpack workpack;

	@Relationship(type = "COMPOSES", direction = Relationship.INCOMING)
	private Set<Step> steps;

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public Workpack getWorkpack() {
		return workpack;
	}

	public void setWorkpack(Workpack workpack) {
		this.workpack = workpack;
	}

	public Set<Step> getSteps() {
		return steps;
	}

	public void setSteps(Set<Step> steps) {
		this.steps = steps;
	}
}
