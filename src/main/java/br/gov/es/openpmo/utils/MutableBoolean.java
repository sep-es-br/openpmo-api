package br.gov.es.openpmo.utils;

public class MutableBoolean {

  private boolean value;

  public MutableBoolean() {
    this.value = false;
  }

  public MutableBoolean(final boolean value) {
    this.value = value;
  }

  public boolean isValue() {
    return this.value;
  }

  public void setValue(final boolean value) {
    this.value = value;
  }

}
