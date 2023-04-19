package br.gov.es.openpmo.service.permissions.canaccess;

import br.gov.es.openpmo.exception.CannotAccessResourceException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static br.gov.es.openpmo.utils.ApplicationMessage.CANNOT_ACCESS_MANAGEMENT_RESOURCE;

@Component
public class CanAccessManagementData implements ICanAccessManagementData {

  private final ICanAccessData canAccessData;

  public CanAccessManagementData(final ICanAccessData canAccessData) {
    this.canAccessData = canAccessData;
  }

  private static boolean isSelfData(
      final String key,
      final ICanAccessDataResponse canAccess) {
    return Objects.nonNull(key) && canAccess.getKey().equals(key);
  }

  @Override
  public void ensureCanAccessManagementResource(
      final Long id,
      final String authorization) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(id, authorization);
    if (Boolean.TRUE.equals(canAccess.getAdmin()))
      return;
    if (Boolean.FALSE.equals(canAccess.getManagementEdit())) {
      throw new CannotAccessResourceException(CANNOT_ACCESS_MANAGEMENT_RESOURCE);
    }
  }

  @Override
  public void ensureCanAccessManagementOrSelfResource(
      final List<Long> ids,
      final String authorization) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(ids, authorization);
    if (Boolean.TRUE.equals(canAccess.getAdmin()))
      return;
    if (Boolean.TRUE.equals(canAccess.getSelf()))
      return;
    if (Boolean.FALSE.equals(canAccess.getManagementEdit())) {
      throw new CannotAccessResourceException(CANNOT_ACCESS_MANAGEMENT_RESOURCE);
    }
  }

  @Override
  public void ensureCanAccessManagementOrReadResource(
      final Long idOffice,
      final String authorization) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(idOffice, authorization);
    if (Boolean.TRUE.equals(canAccess.getAdmin()))
      return;
    if (Boolean.TRUE.equals(canAccess.canReadResource()))
      return;
    if (Boolean.FALSE.equals(canAccess.getManagementOrReadResource())) {
      throw new CannotAccessResourceException(CANNOT_ACCESS_MANAGEMENT_RESOURCE);
    }
  }

  @Override
  public void ensureCanReadManagementResource(
      final Long idOffice,
      final String key,
      final String authorization) {
    final ICanAccessDataResponse canAccess = this.getCanAccess(idOffice, authorization);
    if (isSelfData(key, canAccess))
      return;
    if (Boolean.TRUE.equals(canAccess.getAdmin()))
      return;
    if (Boolean.FALSE.equals(canAccess.getManagementEdit())) {
      throw new CannotAccessResourceException(CANNOT_ACCESS_MANAGEMENT_RESOURCE);
    }
  }

  private ICanAccessDataResponse getCanAccess(
      final Long id,
      final String authorization) {
    return this.canAccessData.execute(id, authorization);
  }

  private ICanAccessDataResponse getCanAccess(
      final List<Long> ids,
      final String authorization) {
    return this.canAccessData.execute(ids, authorization);
  }

}
