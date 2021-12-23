package br.gov.es.openpmo.dto.person.detail;


public class PersonContactDetail {

  private String email;
  private String phone;
  private String address;

  public PersonContactDetail() {
  }

  public PersonContactDetail(final String email, final String phone, final String address) {
    this.email = email;
    this.phone = phone;
    this.address = address;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(final String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }
}
