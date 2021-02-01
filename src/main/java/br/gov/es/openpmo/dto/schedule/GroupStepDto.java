package br.gov.es.openpmo.dto.schedule;

import java.util.ArrayList;
import java.util.List;

public class GroupStepDto {

	private Integer year;
	private List<StepDto> steps = new ArrayList<>(0);

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<StepDto> getSteps() {
		return steps;
	}

	public void setSteps(List<StepDto> steps) {
		this.steps = steps;
	}
}
