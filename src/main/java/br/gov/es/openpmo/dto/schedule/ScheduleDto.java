package br.gov.es.openpmo.dto.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.util.CollectionUtils;

public class ScheduleDto {

	private Long id;
	private LocalDate end;
	private LocalDate start;
	private List<GroupStepDto> groupStep;
	private Long idWorkpack;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}

	public List<GroupStepDto> getGroupStep() {
		return groupStep;
	}

	public Long getIdWorkpack() {
		return idWorkpack;
	}

	public void setIdWorkpack(Long idWorkpack) {
		this.idWorkpack = idWorkpack;
	}

	public void setGroupStep(List<GroupStepDto> groupStep) {
		this.groupStep = groupStep;
	}

	public BigDecimal getPlaned() {
		BigDecimal planed = BigDecimal.ZERO;
		if (!CollectionUtils.isEmpty(groupStep)) {
			for (GroupStepDto group : groupStep) {
				if (!CollectionUtils.isEmpty(group.getSteps())) {
					for (StepDto step : group.getSteps()) {
						if (step.getPlannedWork() != null) {
							planed = planed.add(step.getPlannedWork());
						}
					}
				}
			}
		}
		return planed;
	}

	public BigDecimal getActual() {
		BigDecimal actual = BigDecimal.ZERO;
		if (!CollectionUtils.isEmpty(groupStep)) {
			for (GroupStepDto group : groupStep) {
				if (!CollectionUtils.isEmpty(group.getSteps())) {
					for (StepDto step : group.getSteps()) {
						if (step.getActualWork() != null) {
							actual = actual.add(step.getActualWork());
						}
					}
				}
			}
		}
		return actual;
	}

	public BigDecimal getPlanedCost() {
		BigDecimal planedCost = BigDecimal.ZERO;
		if (!CollectionUtils.isEmpty(groupStep)) {
			for (GroupStepDto group : groupStep) {
				if (!CollectionUtils.isEmpty(group.getSteps())) {
					for (StepDto step : group.getSteps()) {
						if (!CollectionUtils.isEmpty(step.getConsumes())) {
							for (ConsumesDto consume : step.getConsumes()) {
								if (consume.getPlannedCost() != null) {
									planedCost = planedCost.add(consume.getPlannedCost());
								}
							}
						}
					}
				}
			}
		}
		return planedCost;
	}

	public BigDecimal getActualCost() {
		BigDecimal planedCost = BigDecimal.ZERO;
		if (!CollectionUtils.isEmpty(groupStep)) {
			for (GroupStepDto group : groupStep) {
				if (!CollectionUtils.isEmpty(group.getSteps())) {
					for (StepDto step : group.getSteps()) {
						if (!CollectionUtils.isEmpty(step.getConsumes())) {
							for (ConsumesDto consume : step.getConsumes()) {
								if (consume.getActualCost() != null) {
									planedCost = planedCost.add(consume.getActualCost());
								}
							}
						}
					}
				}
			}
		}
		return planedCost;
	}
}
