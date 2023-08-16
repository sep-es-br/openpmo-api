package br.gov.es.openpmo.dto.actor;

import br.gov.es.openpmo.enumerator.CcbMemberFilterEnum;
import br.gov.es.openpmo.enumerator.StakeholderFilterEnum;
import br.gov.es.openpmo.enumerator.UserFilterEnum;

public class PersonListFilterParameters {

  private final StakeholderFilterEnum stakeholderStatus;
  private final UserFilterEnum userStatus;
  private final CcbMemberFilterEnum ccbMemberStatus;
  private final String name;
  private final Long[] planScope;
  private final Long[] workpackScope;

  public PersonListFilterParameters(
    final StakeholderFilterEnum stakeholderStatus,
    final UserFilterEnum userStatus,
    final CcbMemberFilterEnum ccbMemberStatus,
    final String name,
    final Long[] planScope,
    final Long[] workpackScope
  ) {
    this.stakeholderStatus = stakeholderStatus;
    this.userStatus = userStatus;
    this.ccbMemberStatus = ccbMemberStatus;
    this.name = name;
    this.planScope = planScope;
    this.workpackScope = workpackScope;
  }

  public StakeholderFilterEnum getStakeholderStatus() {
    return this.stakeholderStatus;
  }

  public UserFilterEnum getUserStatus() {
    return this.userStatus;
  }

  public CcbMemberFilterEnum getCcbMemberStatus() {
    return this.ccbMemberStatus;
  }

  public String getName() {
    return this.name;
  }

  public Long[] getPlanScope() {
    return this.planScope;
  }

  public Long[] getWorkpackScope() {
    return this.workpackScope;
  }
}
