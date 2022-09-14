package br.gov.es.openpmo.service.permissions.canaccess;

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

}
