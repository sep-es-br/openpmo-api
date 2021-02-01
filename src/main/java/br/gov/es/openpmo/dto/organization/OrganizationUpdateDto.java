package br.gov.es.openpmo.dto.organization;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.gov.es.openpmo.enumerator.OrganizationEnum;
import br.gov.es.openpmo.utils.ApplicationMessage;

public class OrganizationUpdateDto {

	@NotNull(message = ApplicationMessage.ID_NOT_NULL)
	private Long id;
	@NotBlank(message = ApplicationMessage.NAME_NOT_BLANK)
	private String name;
	@NotBlank(message = ApplicationMessage.FULLNAME_NOT_BLANK)
	private String fullName;
	private String phoneNumber;
	private String address;
	private String email;
	private String contactEmail;
	private String website;
	private OrganizationEnum sector;

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

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public OrganizationEnum getSector() {
		return this.sector;
	}

	public void setSector(OrganizationEnum sector) {
		this.sector = sector;
	}
}
