package br.gov.es.openpmo.dto.ccbmembers;

import br.gov.es.openpmo.dto.person.PersonDto;

import java.util.List;

public class CCBMemberRequest {

  private Long idWorkpack;
  private Long idOffice;
  private PersonDto person;
  private List<MemberAs> memberAs;

  public CCBMemberRequest() {
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public void setIdOffice(final Long idOffice) {
    this.idOffice = idOffice;
  }

  public Long getIdWorkpack() {
    return this.idWorkpack;
  }

  public void setIdWorkpack(final Long idWorkpack) {
    this.idWorkpack = idWorkpack;
  }

  public PersonDto getPerson() {
    return this.person;
  }

  public void setPerson(final PersonDto person) {
    this.person = person;
  }

  public List<MemberAs> getMemberAs() {
    return this.memberAs;
  }

  public void setMemberAs(final List<MemberAs> memberAs) {
    this.memberAs = memberAs;
  }
}
