package br.gov.es.openpmo.service.stakeholder;

import br.gov.es.openpmo.dto.stakeholder.StakeholderDto;
import br.gov.es.openpmo.model.actors.OrganizationEnum;

import java.util.Comparator;

public enum StakeholderSorter {

  NAME("name", (t1, t2) -> {
    final String name1 = t1.isPerson() ? t1.getPerson().getName() : t1.getOrganization().getName();
    final String name2 = t2.isPerson() ? t2.getPerson().getName() : t2.getOrganization().getName();
    if(name1 == null) return -1;
    if(name2 == null) return 1;
    return name1.compareTo(name2);
  }),
  FULL_NAME("fullName", (t1, t2) -> {
    final String fullName1 = t1.isPerson() ? t1.getPerson().getFullName() : t1.getOrganization().getFullName();
    final String fullName2 = t2.isPerson() ? t2.getPerson().getFullName() : t2.getOrganization().getFullName();
    if(fullName1 == null) return -1;
    if(fullName2 == null) return 1;
    return fullName1.compareTo(fullName2);
  }),
  SECTOR("sector", (t1, t2) -> {
    if(!t1.isPerson()) return -1;
    if(!t2.isPerson()) return 1;
    final OrganizationEnum sector1 = t1.getOrganization().getSector();
    final OrganizationEnum sector2 = t2.getOrganization().getSector();
    if(sector1 == null) return -1;
    if(sector2 == null) return 1;
    return sector1.compareTo(sector2);
  }),
  ADDRESS("address", (t1, t2) -> {
    final String address1 = t1.isPerson() ? t1.getPerson().getAddress() : t1.getOrganization().getAddress();
    final String address2 = t2.isPerson() ? t2.getPerson().getAddress() : t2.getOrganization().getAddress();
    if(address1 == null) return -1;
    if(address2 == null) return 1;
    return address1.compareTo(address2);
  }),
  CONTACT_EMAIL("contactEmail", (t1, t2) -> {
    final String contactEmail1 = t1.isPerson() ? t1.getPerson().getContactEmail() : t1.getOrganization().getContactEmail();
    final String contactEmail2 = t2.isPerson() ? t2.getPerson().getContactEmail() : t2.getOrganization().getContactEmail();
    if(contactEmail1 == null) return -1;
    if(contactEmail2 == null) return 1;
    return contactEmail1.compareTo(contactEmail2);
  }),
  PHONE_NUMBER("phoneNumber", (t1, t2) -> {
    final String phoneNumber1 = t1.isPerson() ? t1.getPerson().getPhoneNumber() : t1.getOrganization().getPhoneNumber();
    final String phoneNumber2 = t2.isPerson() ? t2.getPerson().getPhoneNumber() : t2.getOrganization().getPhoneNumber();
    if(phoneNumber1 == null) return -1;
    if(phoneNumber2 == null) return 1;
    return phoneNumber1.compareTo(phoneNumber2);
  }),
  ADMINISTRATOR("administrator", (t1, t2) -> {
    if(!t1.isPerson()) return -1;
    if(!t2.isPerson()) return 1;
    final Boolean administrator1 = t1.getPerson().isAdministrator();
    final Boolean administrator2 = t2.getPerson().isAdministrator();
    return administrator1.compareTo(administrator2);
  });


  private final String keyToCompare;
  private final Comparator<StakeholderDto> comparator;

  StakeholderSorter(
    final String keyToCompare,
    final Comparator<StakeholderDto> comparator
  ) {
    this.keyToCompare = keyToCompare;
    this.comparator = comparator;
  }

  public static StakeholderSorter find(final String key) {
    for(final StakeholderSorter ordering : StakeholderSorter.values()) {
      if(ordering.getKeyToCompare().equalsIgnoreCase(key)) {
        return ordering;
      }
    }
    throw new IllegalArgumentException("This key '" + key + "' not recognized");
  }

  public String getKeyToCompare() {
    return this.keyToCompare;
  }

  public Comparator<StakeholderDto> getComparator() {
    return this.comparator;
  }
}
