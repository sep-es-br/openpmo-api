package br.gov.es.openpmo.dto.organization;

import br.gov.es.openpmo.enumerator.OrganizationEnum;
import br.gov.es.openpmo.model.Organization;

public class OrganizationDto {
	private Long id;
	private String name;
	private String address;
	private String fullName;
	private String phoneNumber;
	private String email;
	private String contactEmail;
	private OrganizationEnum sector;
	private String website;

	public OrganizationDto() {

	}

	public OrganizationDto(Organization organization) {
		this.id = organization.getId();
		this.name = organization.getName();
		this.fullName = organization.getFullName();
		this.address = organization.getAddress();
		this.phoneNumber = organization.getPhoneNumber();
		this.email = organization.getEmail();
		this.contactEmail = organization.getContactEmail();
		this.sector = organization.getSector();
		this.website = organization.getWebsite();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactEmail() {
		return this.contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public OrganizationEnum getSector() {
		return this.sector;
	}

	public void setSector(OrganizationEnum sector) {
		this.sector = sector;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}
