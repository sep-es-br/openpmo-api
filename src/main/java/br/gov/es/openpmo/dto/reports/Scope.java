package br.gov.es.openpmo.dto.reports;

import java.util.List;

public interface Scope {

    Long getId();

    void setHasPermission(Boolean hasPermission);

    Boolean getHasPermission();

    List<? extends Scope> getChildren();

    default void enablePermission() {
        this.setHasPermission(true);
        this.getChildren().parallelStream().forEach(Scope::enablePermission);
    }

    default void disablePermission() {
        this.setHasPermission(false);
    }

}
