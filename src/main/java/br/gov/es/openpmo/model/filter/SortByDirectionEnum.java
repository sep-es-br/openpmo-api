package br.gov.es.openpmo.model.filter;

public enum SortByDirectionEnum {
  ASC(1), DESC(-1);

  private final int order;

  SortByDirectionEnum(final int order) {
    this.order = order;
  }

  public int getOrder() {
    return this.order;
  }
}
