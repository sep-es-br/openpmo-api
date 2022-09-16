package br.gov.es.openpmo.service.permissions.canaccess;

import java.util.List;

public interface ICanAccessService {

  void ensureCanReadResource(
    Long id,
    String authorization
  );

  void ensureCanEditResource(
    Long id,
    String authorization
  );

  void ensureIsAdministrator(String authorization);

  void ensureCanAccessSelfResource(
    Long idPerson,
    String authorization
  );

  void ensureCanAccessManagementResource(
    Long id,
    String authorization
  );


  void ensureCanAccessManagementOrSelfResource(
    List<Long> ids,
    String authorization
  );

  void ensureCanReadManagementResource(
    Long idOffice,
    String key,
    String authorization
  );

  void ensureCanEditResource(
    List<Long> ids,
    String authorization
  );

}
