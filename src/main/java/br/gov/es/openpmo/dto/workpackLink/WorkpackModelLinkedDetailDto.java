package br.gov.es.openpmo.dto.workpackLink;

public class WorkpackModelLinkedDetailDto {
  private Long idWorkpackModelLinked;
  private String nameWorkpackModelLinked;
  private String nameInPluralWorkpackModelLinked;
  private Long idWorkpackModelOriginal;

  public Long getIdWorkpackModelLinked() {
    return this.idWorkpackModelLinked;
  }

  public void setIdWorkpackModelLinked(final Long idWorkpackModelLinked) {
    this.idWorkpackModelLinked = idWorkpackModelLinked;
  }

  public String getNameWorkpackModelLinked() {
    return this.nameWorkpackModelLinked;
  }

  public void setNameWorkpackModelLinked(final String nameWorkpackModelLinked) {
    this.nameWorkpackModelLinked = nameWorkpackModelLinked;
  }

  public String getNameInPluralWorkpackModelLinked() {
    return this.nameInPluralWorkpackModelLinked;
  }

  public void setNameInPluralWorkpackModelLinked(final String nameInPluralWorkpackModelLinked) {
    this.nameInPluralWorkpackModelLinked = nameInPluralWorkpackModelLinked;
  }

  public Long getIdWorkpackModelOriginal() {
    return this.idWorkpackModelOriginal;
  }

  public void setIdWorkpackModelOriginal(final Long idWorkpackModelOriginal) {
    this.idWorkpackModelOriginal = idWorkpackModelOriginal;
  }
}
