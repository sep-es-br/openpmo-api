package br.gov.es.openpmo.dto.ccbmembers;

import br.gov.es.openpmo.dto.person.RoleResource;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CCBMemberResponse {

  private final PersonResponse person;

  private final List<? extends MemberAs> memberAs;

  private Boolean active;

  public CCBMemberResponse(final PersonResponse person, final List<? extends MemberAs> memberAs, final Boolean active) {
    this.person = person;
    this.memberAs = memberAs;
    this.active = active;
  }

  public PersonResponse getPerson() {
    return this.person;
  }

  @JsonIgnore
  public Long getPersonId() {
    return Optional.ofNullable(this.person).map(PersonResponse::getId).orElse(null);
  }

  public List<MemberAs> getMemberAs() {
    return Collections.unmodifiableList(this.memberAs);
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(final Boolean active) {
    this.active = active;
  }

  public void addAllRoles(final Collection<? extends RoleResource> roles) {
    this.person.addAllRoles(roles);
  }
}
