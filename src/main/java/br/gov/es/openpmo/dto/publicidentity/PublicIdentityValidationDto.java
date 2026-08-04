package br.gov.es.openpmo.dto.publicidentity;

public class PublicIdentityValidationDto {

  private PublicIdentitySearchType searchType;
  private String cpf;
  private String sub;

  public PublicIdentitySearchType getSearchType() {
    return this.searchType;
  }

  public void setSearchType(final PublicIdentitySearchType searchType) {
    this.searchType = searchType;
  }

  public String getCpf() {
    return this.cpf;
  }

  public void setCpf(final String cpf) {
    this.cpf = cpf;
  }

  public String getSub() {
    return this.sub;
  }

  public void setSub(final String sub) {
    this.sub = sub;
  }
}
