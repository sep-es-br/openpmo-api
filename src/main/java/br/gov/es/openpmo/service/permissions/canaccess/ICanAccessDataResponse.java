package br.gov.es.openpmo.service.permissions.canaccess;

public interface ICanAccessDataResponse {

  String getKey();

  Boolean getEdit();

  Boolean getRead();

  Boolean getUpdate();

  Boolean getBasicRead();

  Boolean getAdmin();

  Boolean canEditResource();

  Boolean canUpdateResource();

  Boolean canReadResource();

  Boolean getManagementOrReadResource();

  Boolean getManagementEdit();

  Boolean getManagementRead();

  Boolean getSelf();

  void ensureCanReadResource();

  void ensureCanUpdateResource();

  void ensureCanEditResource();

  void ensureCanAccessAdminResource();

  void ensureCanAccessSelfResource();

  void ensureCanAccessManagementResource();

  void ensureCanAccessManagementOrReadResource();

}
