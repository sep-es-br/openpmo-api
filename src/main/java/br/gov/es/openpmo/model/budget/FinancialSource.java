/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.model.budget;

import br.gov.es.openpmo.model.Entity;
import java.util.Objects;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 *
 * @author gean.carneiro
 */
@NodeEntity
public class FinancialSource extends Entity {
    
    private String typeCode;
    private String typeName;
    private String detailedSourceCode;
    private String detailedSourceName;
    private String sourceCode;
    private String sourceName;
    private String sourceGroupCode;
    private String sourceGroupName;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDetailedSourceCode() {
        return detailedSourceCode;
    }

    public void setDetailedSourceCode(String detailedSourceCode) {
        this.detailedSourceCode = detailedSourceCode;
    }

    public String getDetailedSourceName() {
        return detailedSourceName;
    }

    public void setDetailedSourceName(String detailedSourceName) {
        this.detailedSourceName = detailedSourceName;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceGroupCode() {
        return sourceGroupCode;
    }

    public void setSourceGroupCode(String sourceGroupCode) {
        this.sourceGroupCode = sourceGroupCode;
    }

    public String getSourceGroupName() {
        return sourceGroupName;
    }

    public void setSourceGroupName(String sourceGroupName) {
        this.sourceGroupName = sourceGroupName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FinancialSource)) {
            return false;
        }
        FinancialSource that = (FinancialSource) object;
        return Objects.equals(typeCode, that.typeCode)
            && Objects.equals(sourceGroupCode, that.sourceGroupCode)
            && Objects.equals(sourceCode, that.sourceCode)
            && Objects.equals(detailedSourceCode, that.detailedSourceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeCode, sourceGroupCode, sourceCode, detailedSourceCode);
    }
}
