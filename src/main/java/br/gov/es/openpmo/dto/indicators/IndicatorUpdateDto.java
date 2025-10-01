package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.dto.indicators.period.PeriodGoalDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;

public class IndicatorUpdateDto {

    @NotNull
    private final Long id;

    @NotNull
    private final Long idWorkpack;

    @NotNull
    private final Long idPlan;

    @NotNull
    @NotEmpty
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    private final String name;

    @NotNull
    @NotEmpty
    @Size(max = 600, message = "A descrição deve ter no máximo 600 caracteres")
    private final String description;

    @NotNull
    @NotEmpty
    private final String source;

    @NotNull
    @NotEmpty
    private final String measure;

    @NotNull
    @NotEmpty
    private final String startDate;

    @NotNull
    @NotEmpty
    private final String endDate;

    @NotNull
    @NotEmpty
    private final String periodicity;

    @NotNull
    private final List<PeriodGoalDto> periodGoals;

    public IndicatorUpdateDto(
        final Long id,
        final Long idWorkpack,
        final Long idPlan,
        final String name,
        final String description,
        final String source,
        final String measure,
        final String startDate,
        final String endDate,
        final String periodicity,
        final List<PeriodGoalDto> periodGoals
    ) {
        this.id = id;
        this.idWorkpack = idWorkpack;
        this.idPlan = idPlan;
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodicity = periodicity;
        this.periodGoals = periodGoals;
    }

    public Long getId() {
        return id;
    }

    public Long getIdWorkpack() {
        return idWorkpack;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public String getMeasure() {
        return measure;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public List<PeriodGoalDto> getPeriodGoals() {
        return periodGoals;
    }

    public Long getIdPlan() {
        return idPlan;
    }

    

}
