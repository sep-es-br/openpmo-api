package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.dto.indicators.period.PeriodGoalDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class IndicatorUpdateDto {

    @NotNull
    private final Long id;

    @NotNull
    private final Long idWorkpack;

    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final String description;

    @NotNull
    @NotEmpty
    private final String source;

    @NotNull
    @NotEmpty
    private final String measure;

    @NotNull
    @NotEmpty
    private final String finalGoal;

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
    private final List<PeriodGoalDto> expectedGoals;

    @NotNull
    private final List<PeriodGoalDto> achievedGoals;

    public IndicatorUpdateDto(
        final Long id,
        final Long idWorkpack,
        final String name,
        final String description,
        final String source,
        final String measure,
        final String finalGoal,
        final String startDate,
        final String endDate,
        final String periodicity,
        final List<PeriodGoalDto> expectedGoals,
        final List<PeriodGoalDto> achievedGoals
    ) {
        this.id = id;
        this.idWorkpack = idWorkpack;
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.finalGoal = finalGoal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodicity = periodicity;
        this.expectedGoals = expectedGoals;
        this.achievedGoals = achievedGoals;
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

    public String getFinalGoal() {
        return finalGoal;
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

    public List<PeriodGoalDto> getExpectedGoals() {
        return expectedGoals;
    }

    public List<PeriodGoalDto> getAchievedGoals() {
        return achievedGoals;
    }
}
