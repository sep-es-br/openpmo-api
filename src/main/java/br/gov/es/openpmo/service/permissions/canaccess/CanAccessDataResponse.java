package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.exception.CannotAccessResourceException;
import br.gov.es.openpmo.utils.ApplicationMessage;

import static br.gov.es.openpmo.utils.ApplicationMessage.CANNOT_ACCESS_ADMIN_RESOURCE;
import static br.gov.es.openpmo.utils.ApplicationMessage.CANNOT_ACCESS_PERSONAL_RESOURCE;
import static br.gov.es.openpmo.utils.ApplicationMessage.CANNOT_EDIT_RESOURCE;
import static br.gov.es.openpmo.utils.ApplicationMessage.CANNOT_READ_RESOURCE;

public class CanAccessDataResponse implements ICanAccessDataResponse {

  private final Boolean edit;
  private final Boolean read;
  private final Boolean basicRead;
  private final Boolean admin;
  private final Boolean self;
  private final String key;
  private final ICanAccessManagementDataResponse canAccessManagement;

  public CanAccessDataResponse(
    final Boolean edit,
    final Boolean read,
    final Boolean basicRead,
    final Boolean admin,
    final Boolean self,
    final String key,
    final ICanAccessManagementDataResponse canAccessManagement
  ) {
    this.edit = edit;
    this.read = read;
    this.basicRead = basicRead;
    this.admin = admin;
    this.self = self;
    this.key = key;
    this.canAccessManagement = canAccessManagement;
  }

  public static CanAccessDataResponse administrator(final String key, final Boolean self) {
    return new CanAccessDataResponse(
      false,
      false,
      false,
      true,
      self,
      key,
      new CanAccessManagementDataResponse(true, true)
    );
  }

  public static CanAccessDataResponse edit(final String key, final Boolean editManagement, final Boolean self) {
    return new CanAccessDataResponse(
      true,
      false,
      false,
      false,
      self,
      key,
      new CanAccessManagementDataResponse(editManagement, true)
    );
  }

  public static CanAccessDataResponse read(final String key, final Boolean editManagement, final Boolean self) {
    return new CanAccessDataResponse(
      false,
      true,
      false,
      false,
      self,
      key,
      new CanAccessManagementDataResponse(editManagement, true)
    );
  }


  @Override
  public String getKey() {
    return this.key;
  }

  @Override
  public Boolean getEdit() {
    return this.edit;
  }

  @Override
  public Boolean getRead() {
    return this.read;
  }

  @Override
  public Boolean getBasicRead() {
    return this.basicRead;
  }

  @Override
  public Boolean getAdmin() {
    return this.admin;
  }

  @Override
  public Boolean canEditResource() {
    if (Boolean.TRUE.equals(this.admin))
      return true;
    return this.edit;
  }

  @Override
  public Boolean canReadResource() {
    if (Boolean.TRUE.equals(this.admin))
      return true;
    if (Boolean.TRUE.equals(this.edit))
      return true;
    return this.read || this.basicRead;
  }

  @Override
  public void ensureCanReadResource() {
    if (Boolean.FALSE.equals(this.canReadResource())) {
      throw new CannotAccessResourceException(CANNOT_READ_RESOURCE);
    }
  }

  @Override
  public void ensureCanEditResource() {
    if (Boolean.FALSE.equals(this.canEditResource())) {
      throw new CannotAccessResourceException(CANNOT_EDIT_RESOURCE);
    }
  }

  @Override
  public void ensureCanAccessAdminResource() {
    if (Boolean.FALSE.equals(this.admin)) {
      throw new CannotAccessResourceException(CANNOT_ACCESS_ADMIN_RESOURCE);
    }
  }

  @Override
  public void ensureCanAccessSelfResource() {
    if (Boolean.TRUE.equals(this.admin))
      return;
    if (Boolean.FALSE.equals(this.self)) {
      throw new CannotAccessResourceException(CANNOT_ACCESS_PERSONAL_RESOURCE);
    }
  }

  @Override
  public void ensureCanAccessManagementResource() {
    if (Boolean.TRUE.equals(this.admin))
      return;
    if (Boolean.FALSE.equals(this.canAccessManagement.getEdit())) {
      throw new CannotAccessResourceException(ApplicationMessage.CANNOT_ACCESS_MANAGEMENT_RESOURCE);
    }
  }

  @Override
  public void ensureCanAccessManagementOrReadResource() {
    if (Boolean.TRUE.equals(this.admin))
      return;
    if (Boolean.FALSE.equals(this.canAccessManagement.getRead())) {
      throw new CannotAccessResourceException(ApplicationMessage.CANNOT_ACCESS_MANAGEMENT_RESOURCE);
    }
  }

  @Override
  public Boolean getManagementEdit() {
    return this.canAccessManagement.getEdit();
  }

  @Override
  public Boolean getManagementRead() {
    return this.canAccessManagement.getRead();
  }

  @Override
  public Boolean getSelf() {
    return this.self;
  }

  @Override
  public Boolean getManagementOrReadResource() {
    return this.canAccessManagement.getRead();
  }

}
