package br.gov.es.openpmo.scheduler.updateroles;

public interface HasRole {

  String getRole();

  default String getRoleGuid() {
    return null;
  }

}
