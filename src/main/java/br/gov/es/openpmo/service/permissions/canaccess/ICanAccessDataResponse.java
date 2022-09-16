package br.gov.es.openpmo.service.permissions.canaccess;

public interface ICanAccessDataResponse {

  String getKey();

  Boolean getEdit();

  Boolean getRead();

  Boolean getBasicRead();

  Boolean getAdmin();

  Boolean canEditResource();

  Boolean canReadResource();

  void ensureCanReadResource();

  void ensureCanEditResource();

  void ensureCanAccessAdminResource();

  void ensureCanAccessSelfResource();

  void ensureCanAccessManagementResource();

  Boolean getManagementEdit();

  Boolean getManagementRead();

  Boolean getSelf();

}
