package br.gov.es.openpmo.dto.dashboards.datasheet;

import java.util.List;

public class DatasheetTotalizers {

    private final List<ChildrenByTypeResponse> childrensByType;


    public DatasheetTotalizers(List<ChildrenByTypeResponse> childrensByType) {
        this.childrensByType = childrensByType;
    }

    public List<ChildrenByTypeResponse> getChildrensByType() {
        return childrensByType;
    }
}
