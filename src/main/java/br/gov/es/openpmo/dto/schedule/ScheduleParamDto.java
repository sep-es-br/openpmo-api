package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.NotNull;

public class ScheduleParamDto {

	private Long id;
	@NotNull
	private Long idWorkpack;

	@NotNull
	private LocalDate end;

	@NotNull
	private LocalDate start;

	@NotNull
	private BigDecimal plannedWork;
	private BigDecimal actualWork;
	private Set<CostSchedule> costs;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdWorkpack() {
		return idWorkpack;
	}

	public void setIdWorkpack(Long idWorkpack) {
		this.idWorkpack = idWorkpack;
	}

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

	public BigDecimal getPlannedWork() {
		return plannedWork;
	}

	public void setPlannedWork(BigDecimal plannedWork) {
		this.plannedWork = plannedWork;
	}

	public BigDecimal getActualWork() {
		return actualWork;
	}

	public void setActualWork(BigDecimal actualWork) {
		this.actualWork = actualWork;
	}

	public Set<CostSchedule> getCosts() {
		return costs;
	}

	public void setCosts(Set<CostSchedule> costs) {
		this.costs = costs;
	}
}
