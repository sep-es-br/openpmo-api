package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.dto.indicators.period.PeriodGoalDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class IndicatorCreateDto {

    @NotNull
    private Long idWorkpack;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @NotEmpty
    private String source;

    @NotNull
    @NotEmpty
    private String measure;

    @NotNull
    @NotEmpty
    private String startDate;

    @NotNull
    @NotEmpty
    private String endDate;

    @NotNull
    @NotEmpty
    private String periodicity;

    @NotNull
    private List<PeriodGoalDto> periodGoals;


    public IndicatorCreateDto() {
    }

    public IndicatorCreateDto(
        final Long idWorkpack,
        final String name,
        final String description,
        final String source,
        final String measure,
        final String startDate,
        final String endDate,
        final String periodicity,
        final List<PeriodGoalDto> periodGoals
    ) {
        this.idWorkpack = idWorkpack;
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodicity = periodicity;
        this.periodGoals = periodGoals;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public void setIdWorkpack(Long idWorkpack) {
        this.idWorkpack = idWorkpack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public List<PeriodGoalDto> getPeriodGoals() {
        return periodGoals;
    }

    public void setPeriodGoalDto(List<PeriodGoalDto> periodGoals) {
        this.periodGoals = periodGoals;
    }

    


}
