package br.gov.es.openpmo.enumerator;

public enum PermissionLevelEnum {
  NONE(0), BASIC_READ(1), READ(2), UPDATE(3), EDIT(4);

  int level;

  PermissionLevelEnum(final int level) {
    this.level = level;
  }

  public int getLevel() {
    return this.level;
  }

}
