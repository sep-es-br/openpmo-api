package br.gov.es.openpmo.model.indicators;

import br.gov.es.openpmo.dto.indicators.IndicatorCreateDto;
import br.gov.es.openpmo.dto.indicators.IndicatorUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.*;

@NodeEntity
public class Indicator extends Entity {

    private String name;
    private String description;
    private String source;
    private String measure;
    private String finalGoal;
    private String startDate;
    private String endDate;
    private String periodicity;
    private String lastUpdate;

    @Relationship(type = "HAS_EXPECTED_GOAL", direction = Relationship.OUTGOING)
    private List<PeriodGoal> expectedGoals = new ArrayList<>();

    @Relationship(type = "HAS_ACHIEVED_GOAL", direction = Relationship.OUTGOING)
    private List<PeriodGoal> achievedGoals = new ArrayList<>();

    @Relationship("RELATED_TO")
    private Workpack workpack;

    public Indicator() {
    }

    public Indicator(
        final String name,
        final String description,
        final String source,
        final String measure,
        final String finalGoal,
        final String startDate,
        final String endDate,
        final String periodicity,
        final String lastUpdate,
        final Workpack workpack
    ) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.finalGoal = finalGoal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodicity = periodicity;
        this.lastUpdate = lastUpdate;
        this.expectedGoals = expectedGoals;
        this.achievedGoals = achievedGoals;
        this.workpack = workpack;
    }

    public static Indicator of(
            final IndicatorCreateDto request,
            final Workpack workpack
    ) {
        Indicator indicator =  new Indicator(
                request.getName(),
                request.getDescription(),
                request.getSource(),
                request.getMeasure(),
                request.getFinalGoal(),
                request.getStartDate(),
                request.getEndDate(),
                request.getPeriodicity(),
                request.getLastUpdate(),
                workpack

        );

        request.getExpectedGoals().forEach(goal ->
                indicator.addExpectedGoals(new PeriodGoal(goal.getPeriod(), goal.getValue())));

        request.getAchievedGoals().forEach(goal ->
                indicator.addAchievedGoals(new PeriodGoal(goal.getPeriod(), goal.getValue())));

        return indicator;
    }


    @Transient
    public Long getIdWorkpack() {
        return Optional.ofNullable(this.workpack)
                .map(Entity::getId)
                .orElse(null);
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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Workpack getWorkpack() {
        return workpack;
    }

    public void setWorkpack(Workpack workpack) {
        this.workpack = workpack;
    }

    public void update(final IndicatorUpdateDto request) {
        ObjectUtils.updateIfPresent(request::getName, this::setName);
        ObjectUtils.updateIfPresent(request::getDescription, this::setDescription);
        ObjectUtils.updateIfPresent(request::getSource, this::setSource);
        ObjectUtils.updateIfPresent(request::getMeasure, this::setMeasure);
        ObjectUtils.updateIfPresent(request::getFinalGoal, this::setFinalGoal);
        ObjectUtils.updateIfPresent(request::getStartDate, this::setStartDate);
        ObjectUtils.updateIfPresent(request::getEndDate, this::setEndDate);
        ObjectUtils.updateIfPresent(request::getPeriodicity, this::setPeriodicity);

        request.getExpectedGoals().forEach(goal ->
                this.addExpectedGoals(new PeriodGoal(goal.getPeriod(), goal.getValue())));

        request.getAchievedGoals().forEach(goal ->
                this.addAchievedGoals(new PeriodGoal(goal.getPeriod(), goal.getValue())));
    }

    public List<PeriodGoal> getExpectedGoals() {
        return expectedGoals;
    }

    public void addExpectedGoals(PeriodGoal goal) {
        this.expectedGoals.add(goal);
    }

    public List<PeriodGoal> getAchievedGoals() {
        return achievedGoals;
    }

    public void addAchievedGoals(PeriodGoal goal) {
        this.achievedGoals.add(goal);
    }

    public void setExpectedGoals(List<PeriodGoal> expectedGoals) {
        this.expectedGoals = expectedGoals;
    }

    public void setAchievedGoals(List<PeriodGoal> achievedGoals) {
        this.achievedGoals = achievedGoals;
    }
}
