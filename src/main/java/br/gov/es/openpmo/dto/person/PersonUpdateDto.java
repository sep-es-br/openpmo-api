package br.gov.es.openpmo.dto.person;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PersonUpdateDto {

  @NotNull
  private final Long id;

  @NotNull
  private final Long idOffice;

  @NotEmpty
  @NotNull
  @NotBlank
  private final String name;

  private final String contactEmail;

  private final String phoneNumber;

  private final String address;

  private final boolean unify;

  public PersonUpdateDto(
    final Long id,
    final Long idOffice,
    final String name,
    final String contactEmail,
    final String phoneNumber,
    final String address,
    final boolean unify
  ) {
    this.id = id;
    this.idOffice = idOffice;
    this.name = name;
    this.contactEmail = contactEmail;
    this.phoneNumber = phoneNumber;
    this.address = address;
    this.unify = unify;
  }

  public Long getId() {
    return this.id;
  }

  public Long getIdOffice() {
    return this.idOffice;
  }

  public String getName() {
    return this.name;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getAddress() {
    return this.address;
  }

  public boolean getUnify() {
    return this.unify;
  }



}
