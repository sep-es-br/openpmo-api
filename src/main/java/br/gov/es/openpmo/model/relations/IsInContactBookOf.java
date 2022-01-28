package br.gov.es.openpmo.model.relations;

import br.gov.es.openpmo.dto.person.PersonDto;
import br.gov.es.openpmo.dto.person.PersonUpdateDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.springframework.data.annotation.Transient;

import java.util.Objects;

@RelationshipEntity(type = "IS_IN_CONTACT_BOOK_OF")
public class IsInContactBookOf {

  @Id
  @GeneratedValue
  private Long id;

  private String email;

  private String address;

  private String phoneNumber;

  @StartNode
  private Person person;

  @EndNode
  private Office office;

  public IsInContactBookOf() {
  }

  public IsInContactBookOf(final PersonDto dto) {
    this.email = dto.getContactEmail();
    this.phoneNumber = dto.getPhoneNumber();
    this.address = dto.getAddress();
  }

  public IsInContactBookOf(final PersonDto personDto, final Office office, final Person person) {
    this.person = person;
    this.office = office;
    this.address = personDto.getAddress();
    this.email = personDto.getContactEmail();
    this.phoneNumber = personDto.getPhoneNumber();
  }

  public void setPerson(final Person person) {
    this.person = person;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  @Transient
  public Long getPersonId() {
    return this.getPerson().getId();
  }

  public Person getPerson() {
    return this.person;
  }

  @Transient
  public Long getOfficeId() {
    return this.office.getId();
  }

  public Office getOffice() {
    return this.office;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getEmail() {
    return this.email;
  }

  public String getAddress() {
    return this.address;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    final IsInContactBookOf isInContactBookOf = (IsInContactBookOf) o;
    return this.id.equals(isInContactBookOf.id);
  }

  public void update(final PersonUpdateDto personUpdateDto) {
    this.email = personUpdateDto.getContactEmail();
    this.address = personUpdateDto.getAddress();
    this.phoneNumber = personUpdateDto.getPhoneNumber();
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

  public void setPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
