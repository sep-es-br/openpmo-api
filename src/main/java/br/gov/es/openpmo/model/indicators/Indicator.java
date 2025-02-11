package br.gov.es.openpmo.model.indicators;

import br.gov.es.openpmo.dto.indicators.IndicatorCreateDto;
import br.gov.es.openpmo.dto.indicators.IndicatorUpdateDto;
import br.gov.es.openpmo.model.Entity;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.utils.ObjectUtils;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.data.annotation.Transient;

import java.util.Optional;

@NodeEntity
public class Indicator extends Entity {

    private String name;
    private String description;
    private String source;
    private String measure;
    private String finalGoal;
    private String periodicity;

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
        final String periodicity,
        final Workpack workpack
    ) {
        this.name = name;
        this.description = description;
        this.source = source;
        this.measure = measure;
        this.finalGoal = finalGoal;
        this.periodicity = periodicity;
        this.workpack = workpack;
    }

    public static Indicator of(
            final IndicatorCreateDto request,
            final Workpack workpack
    ) {
        return new Indicator(
                request.getName(),
                request.getDescription(),
                request.getSource(),
                request.getMeasure(),
                request.getFinalGoal(),
                request.getPeriodicity(),
                workpack
        );
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
        ObjectUtils.updateIfPresent(request::getFinalGoal, this::setFinalGoal);
        ObjectUtils.updateIfPresent(request::getPeriodicity, this::setPeriodicity);
    }
}
