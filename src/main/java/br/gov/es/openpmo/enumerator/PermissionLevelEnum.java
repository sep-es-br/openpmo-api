package br.gov.es.openpmo.enumerator;

public enum PermissionLevelEnum {
  NONE(0), BASIC_READ(1), READ(2), EDIT(3);

  int level;

  PermissionLevelEnum(final int level) {
    this.level = level;
  }

  public int getLevel() {
    return this.level;
  }

}
