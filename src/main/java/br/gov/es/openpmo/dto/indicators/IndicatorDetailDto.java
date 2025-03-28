package br.gov.es.openpmo.dto.indicators;

import br.gov.es.openpmo.dto.indicators.period.PeriodGoalDto;
import br.gov.es.openpmo.model.indicators.Indicator;
import br.gov.es.openpmo.model.indicators.PeriodGoal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class IndicatorDetailDto {

    @NotNull
    private final Long id;

    @NotNull
    private final Long idWorkpack;

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
    private String finalGoal;

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
    private List<PeriodGoal> expectedGoals;

    @NotNull
    private List<PeriodGoal> achievedGoals;

    public IndicatorDetailDto(
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
        final List<PeriodGoal> expectedGoals,
        final List<PeriodGoal> achievedGoals
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

    public static IndicatorDetailDto of(final Indicator indicator) {
        return new IndicatorDetailDto(
            indicator.getId(),
            indicator.getIdWorkpack(),
            indicator.getName(),
            indicator.getDescription(),
            indicator.getSource(),
            indicator.getMeasure(),
            indicator.getFinalGoal(),
            indicator.getStartDate(),
            indicator.getEndDate(),
            indicator.getPeriodicity(),
            indicator.getExpectedGoals().stream()
                    .map(goal -> new PeriodGoal(goal.getPeriod(), goal.getValue(), goal.getLastUpdate()))
                    .collect(Collectors.toList()),
            indicator.getAchievedGoals().stream()
                    .map(goal -> new PeriodGoal(goal.getPeriod(), goal.getValue(), goal.getLastUpdate()))
                    .collect(Collectors.toList())
        );
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

    public String getFinalGoal() {
        return finalGoal;
    }

    public void setFinalGoal(String finalGoal) {
        this.finalGoal = finalGoal;
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

    public List<PeriodGoal> getExpectedGoals() {
        return expectedGoals;
    }

    public List<PeriodGoal> getAchievedGoals() {
        return achievedGoals;
    }

    public void setAchievedGoals(List<PeriodGoal> achievedGoals) {
        this.achievedGoals = achievedGoals;
    }

    public void setExpectedGoals(List<PeriodGoal> expectedGoals) {
        this.expectedGoals = expectedGoals;
    }
}
