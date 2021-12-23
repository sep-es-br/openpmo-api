package br.gov.es.openpmo.model.actors;


import br.gov.es.openpmo.model.office.Office;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Organization extends Actor {

  private String website;
  private OrganizationEnum sector;
  private String address;
  private String phoneNumber;
  private String contactEmail;

  @Relationship(type = "IS_REGISTERED_IN")
  private Office office;

  public String getWebsite() {
    return this.website;
  }

  public void setWebsite(final String website) {
    this.website = website;
  }

  public Office getOffice() {
    return this.office;
  }

  public void setOffice(final Office office) {
    this.office = office;
  }

  public OrganizationEnum getSector() {
    return this.sector;
  }

  public void setSector(final OrganizationEnum sector) {
    this.sector = sector;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public void setPhoneNumber(final String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public void setContactEmail(final String contactEmail) {
    this.contactEmail = contactEmail;
  }
}
