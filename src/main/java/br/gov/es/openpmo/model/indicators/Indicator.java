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
    private String startDate;
    private String endDate;
    private String periodicity;

    @Relationship(type = "IS_DEFINED_FOR", direction = Relationship.INCOMING)
    private List<PeriodGoal> periodGoals = new ArrayList<>();

    @Relationship("RELATED_TO")
    private Workpack workpack;

    public Indicator() {
    }

    public Indicator(
        final String name,
        final String description,
        final String source,
        final String measure,
        final String startDate,
        final String endDate,
        final String periodicity,
        final Workpack workpack
    ) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodicity = periodicity;
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
                request.getStartDate(),
                request.getEndDate(),
                request.getPeriodicity(),
                workpack

        );

        request.getPeriodGoals().forEach(goal ->
                indicator.addPeriodGoals(new PeriodGoal(goal.getPeriod(), goal.getExpectedValue(), goal.getAchievedValue(), goal.getLastUpdate(), goal.getJustification())));



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
        ObjectUtils.updateIfPresent(request::getStartDate, this::setStartDate);
        ObjectUtils.updateIfPresent(request::getEndDate, this::setEndDate);
        ObjectUtils.updateIfPresent(request::getPeriodicity, this::setPeriodicity);

        request.getPeriodGoals().forEach(goal ->
                this.addPeriodGoals(new PeriodGoal(goal.getPeriod(), goal.getExpectedValue(), goal.getAchievedValue(), goal.getLastUpdate(), goal.getJustification())));
    }

    public void addPeriodGoals(PeriodGoal goal) {
        this.periodGoals.add(goal);
    }

    public List<PeriodGoal> getPeriodGoals() {
        return periodGoals;
    }

    public void setPeriodGoals(List<PeriodGoal> periodGoals) {
        this.periodGoals = periodGoals;
    }

    
}
