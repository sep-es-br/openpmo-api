package br.gov.es.openpmo.dto;

public class ComboDto {
  private Long id;
  private String name;

  public ComboDto() {
  }

  public ComboDto(final Long id, final String name) {
    this.id = id;
    this.name = name;
  }

  public ComboDto(final Long id, final Long numero) {
    this.id = id;
    this.name = String.valueOf(numero);
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
