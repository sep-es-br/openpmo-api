package br.gov.es.openpmo.dto.person;

public final class CitizenByNameQuery {

  private final String name;
  private final String sub;

  public CitizenByNameQuery(
    final String name,
    final String sub
  ) {
    this.name = name;
    this.sub = sub;
  }

  public String getName() {
    return this.name;
  }

  public String getSub() {
    return this.sub;
  }

}

