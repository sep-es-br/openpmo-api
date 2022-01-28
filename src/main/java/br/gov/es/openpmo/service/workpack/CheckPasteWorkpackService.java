package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.WorkpackPasteResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.TriPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

@Service
public class CheckPasteWorkpackService {

  private final WorkpackModelRepository workpackModelRepository;

  @Autowired
  public CheckPasteWorkpackService(final WorkpackModelRepository workpackModelRepository) {
    this.workpackModelRepository = workpackModelRepository;
  }

  private static boolean areWorkpackModelsCompatible(
      final WorkpackModel model,
      final WorkpackModel other,
      final WorkpackPasteResponse response
  ) {
    final boolean areCompatible = model.hasSameType(other)
        && model.hasSameName(other)
        && allAreCompatible(
        model.getChildren(),
        other.getChildren(),
        response,
        CheckPasteWorkpackService::areWorkpackModelsCompatible);

    if (areCompatible) {
      allAreCompatible(
          model.getProperties(),
          other.getProperties(),
          response,
          CheckPasteWorkpackService::arePropertyModelsCompatible);
    }

    response.setCanPaste(response.getCanPaste() && areCompatible);
    return areCompatible;
  }

  private static <T> boolean allAreCompatible(
      final Collection<? extends T> first,
      final Collection<? extends T> second,
      final WorkpackPasteResponse response,
      final TriPredicate<T, T, ? super WorkpackPasteResponse> compatibleComparator
  ) {
    if (first == null && second == null) {
      return true;
    }

    if (first == null || second == null) {
      return false;
    }

    if (first.size() != second.size()) {
      return false;
    }

    final Collection<T> fromCopy = copySet(first);
    final Collection<T> toCopy = copySet(second);

    final Iterator<T> fromIterator = fromCopy.iterator();
    final Iterator<T> toIterator = toCopy.iterator();

    while (fromIterator.hasNext() && toIterator.hasNext()) {
      if (compatibleComparator.test(fromIterator.next(), toIterator.next(), response)) {
        fromIterator.remove();
        toIterator.remove();
      }
    }

    return fromCopy.isEmpty();
  }

  private static <T> Collection<T> copySet(final Collection<? extends T> collection) {
    return new HashSet<>(collection);
  }

  private static boolean arePropertyModelsCompatible(
      final PropertyModel model,
      final PropertyModel other,
      final WorkpackPasteResponse response
  ) {
    final boolean areCompatible = model.hasSameType(other) && model.hasSameName(other);
    response.setIncompatiblesProperties(response.getIncompatiblesProperties() || !areCompatible);
    return areCompatible;
  }

  public WorkpackPasteResponse checksIfCanPasteWorkpack(
      final Long idWorkpack,
      final Long idWorkpackModelTo,
      final Long idWorkpackModelFrom
  ) {
    this.ifWorkpackIsNotInstantiatedByModelThrowsException(idWorkpack, idWorkpackModelFrom);

    final WorkpackModel workpackModelFrom = this.getWorkpackModel(idWorkpackModelFrom);
    final WorkpackModel workpackModelTo = this.getWorkpackModel(idWorkpackModelTo);

    final WorkpackPasteResponse response = new WorkpackPasteResponse(true, false);
    areWorkpackModelsCompatible(workpackModelFrom, workpackModelTo, response);
    return response;
  }

  private WorkpackModel getWorkpackModel(final Long idWorkpackModelFrom) {
    return this.workpackModelRepository.findByIdWorkpackWithChildren(idWorkpackModelFrom)
        .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
  }

  private void ifWorkpackIsNotInstantiatedByModelThrowsException(final Long idWorkpack, final Long idWorkpackModelFrom) {
    if (!this.isWorkpackInstanceByModel(idWorkpack, idWorkpackModelFrom)) {
      throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
    }
  }

  private boolean isWorkpackInstanceByModel(final Long idWorkpack, final Long idWorkpackModelFrom) {
    return this.workpackModelRepository.isWorkpackInstanceByModel(idWorkpack, idWorkpackModelFrom);
  }

}
