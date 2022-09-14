package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.service.permissions.canaccess.ICanAccessData.ICanAccessDataResponse;
import org.springframework.stereotype.Component;

@Component
public class CanAccessService implements ICanAccessService {

  private final ICanAccessData canAccessData;


  public CanAccessService(final ICanAccessData canAccessData) {
    this.canAccessData = canAccessData;
  }

  @Override
  public void ensureCanReadResource(
    final Long id,
    final String authorization
  ) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(id, authorization);
    canAccess.ensureCanReadResource();
  }

  @Override
  public void ensureCanEditResource(
    final Long id,
    final String authorization
  ) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(id, authorization);
    canAccess.ensureCanEditResource();
  }

  @Override
  public void ensureIsAdministrator(final String authorization) {
    final ICanAccessDataResponse canAccess = this.canAccessData.execute(authorization);
    canAccess.ensureCanAccessAdminResource();
  }

  @Override
  public void ensureCanAccessSelfResource(
    final Long id,
    final String authorization
  ) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(id, authorization);
    canAccess.ensureCanAccessSelfResource();
  }

  private ICanAccessDataResponse getCanAccess(
    final Long id,
    final String authorization
  ) {
    return this.canAccessData.execute(id, authorization);
  }

}
