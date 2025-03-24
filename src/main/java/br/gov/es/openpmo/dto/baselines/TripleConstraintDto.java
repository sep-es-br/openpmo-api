package br.gov.es.openpmo.dto.baselines;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.neo4j.annotation.QueryResult;

import br.gov.es.openpmo.enumerator.CategoryEnum;

@QueryResult
public class TripleConstraintDto {
    private Long idWorkpack;
    private String name;
    private String fullName;
    private LocalDateTime date;
    private String unitMeasure;
    private List<String> labels;
    private String type;
    private String fontIcon;
    private BigDecimal sumPlannedCost;
    private LocalDate end;
    private LocalDate start;
    private BigDecimal sumPlannedWork;
    private CategoryEnum category;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public void setUnitMeasure(String unitMeasure) {
        this.unitMeasure = unitMeasure;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getType() {
        if (type == null && labels != null) {
            labels.forEach(t -> type = t);
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFontIcon() {
        return fontIcon;
    }

    public void setFontIcon(String fontIcon) {
        this.fontIcon = fontIcon;
    }

    public BigDecimal getSumPlannedCost() {
        return sumPlannedCost;
    }

    public void setSumPlannedCost(BigDecimal sumPlannedCost) {
        this.sumPlannedCost = sumPlannedCost;
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

    public BigDecimal getSumPlannedWork() {
        return sumPlannedWork;
    }

    public void setSumPlannedWork(BigDecimal sumPlannedWork) {
        this.sumPlannedWork = sumPlannedWork;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }
}
