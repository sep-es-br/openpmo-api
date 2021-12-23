package br.gov.es.openpmo.dto.workpackLink;

import java.util.List;

public class WorkpackModelLinkedDto {
  private Long id;
  private String name;
  private String nameInPlural;
  private List<WorkpackModelLinkedDetailDto> children;

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

  public String getNameInPlural() {
    return this.nameInPlural;
  }

  public void setNameInPlural(final String nameInPlural) {
    this.nameInPlural = nameInPlural;
  }

  public List<WorkpackModelLinkedDetailDto> getChildren() {
    return this.children;
  }

  public void setChildren(final List<WorkpackModelLinkedDetailDto> children) {
    this.children = children;
  }

}
