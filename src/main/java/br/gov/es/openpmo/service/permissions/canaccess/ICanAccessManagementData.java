package br.gov.es.openpmo.service.permissions.canaccess;

import java.util.List;

public interface ICanAccessManagementData {

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

}
