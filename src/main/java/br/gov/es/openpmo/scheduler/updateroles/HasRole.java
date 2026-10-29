package br.gov.es.openpmo.scheduler.updateroles;

import java.util.Objects;

public interface HasRole {

  String getRole();

  default String getRoleGuid() {
    return null;
  }

  static boolean sameRole(final HasRole first, final HasRole second) {
    if (first.getRoleGuid() != null && second.getRoleGuid() != null) {
      return Objects.equals(first.getRoleGuid(), second.getRoleGuid());
    }
    return Objects.equals(first.getRole(), second.getRole());
  }

}
