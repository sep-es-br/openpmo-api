package br.gov.es.openpmo.service.permissions.canaccess;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CanAccessService implements ICanAccessService {

  private final ICanAccessData canAccessData;
  private final ICanAccessManagementData canAccessManagementData;

  public CanAccessService(
    final ICanAccessData canAccessData,
    final ICanAccessManagementData canAccessManagementData
  ) {
    this.canAccessData = canAccessData;
    this.canAccessManagementData = canAccessManagementData;
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
  public void ensureCanReadResourceWorkpack(Long idWorkpack, String authorization) {
    final ICanAccessDataResponse canAccess = this.canAccessData.executeWorkpack(idWorkpack, authorization);
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
    final ICanAccessDataResponse canAccess = this.getCanAccess(authorization);
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

  @Override
  public void ensureCanAccessManagementResource(
    final Long id,
    final String authorization
  ) {
    this.canAccessManagementData.ensureCanAccessManagementResource(id, authorization);
  }

  @Override
  public void ensureCanAccessManagementOrSelfResource(
    final List<Long> ids,
    final String authorization
  ) {
    this.canAccessManagementData.ensureCanAccessManagementOrSelfResource(ids, authorization);
  }

  @Override
  public void ensureCanAccessManagementOrReadResource(
    final Long idOffice,
    final String authorization
  ) {
    this.canAccessManagementData.ensureCanAccessManagementOrReadResource(idOffice, authorization);
  }

  @Override
  public void ensureCanReadManagementResource(
    final Long idOffice,
    final String key,
    final String authorization
  ) {
    this.canAccessManagementData.ensureCanReadManagementResource(idOffice, key, authorization);
  }

  @Override
  public void ensureCanEditResource(
    final List<Long> ids,
    final String authorization
  ) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(ids, authorization);
    canAccess.ensureCanEditResource();
  }

  private ICanAccessDataResponse getCanAccess(
    final List<Long> ids,
    final String authorization
  ) {
    return this.canAccessData.execute(ids, authorization);
  }

  private ICanAccessDataResponse getCanAccess(final String authorization) {
    return this.canAccessData.execute(authorization);
  }

  private ICanAccessDataResponse getCanAccess(
    final Long id,
    final String authorization
  ) {
    return this.canAccessData.execute(id, authorization);
  }

}
