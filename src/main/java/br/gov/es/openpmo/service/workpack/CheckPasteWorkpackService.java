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
                && allAreWokpackModelsCompatible(
                model.getChildren(),
                other.getChildren(),
                response,
                CheckPasteWorkpackService::areWorkpackModelsCompatible);

        if (areCompatible) {
            allArePropertyModelsCompatible(
                    model.getProperties(),
                    other.getProperties(),
                    response,
                    CheckPasteWorkpackService::arePropertyModelsCompatible);
        }

        return areCompatible;
    }

    private static void allArePropertyModelsCompatible(
            final Collection<PropertyModel> first,
            final Collection<PropertyModel> second,
            final WorkpackPasteResponse response,
            final TriPredicate<PropertyModel, PropertyModel, ? super WorkpackPasteResponse> compatibleComparator
    ) {
        if (first == null && second == null) {
            return;
        }

        if (first == null || second == null) {
            return;
        }

        if (first.size() > second.size()) {
            return;
        }

        Collection<PropertyModel> fromCopy = new HashSet<>(first);
        Iterator<PropertyModel> fromIterator = fromCopy.iterator();

        while (fromIterator.hasNext()) {
            PropertyModel from = fromIterator.next();
            Collection<PropertyModel> toCopy = new HashSet<>(second);

            for (PropertyModel to : toCopy) {
                if (compatibleComparator.test(from, to, response)) {
                    fromIterator.remove();
                    break;
                }
            }
        }
    }

    private static boolean allAreWokpackModelsCompatible(
            final Collection<WorkpackModel> first,
            final Collection<WorkpackModel> second,
            final WorkpackPasteResponse response,
            final TriPredicate<WorkpackModel, WorkpackModel, ? super WorkpackPasteResponse> compatibleComparator
    ) {
        if (first == null && second == null) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        if (first.size() > second.size()) {
            return false;
        }

        Collection<WorkpackModel> fromCopy = new HashSet<>(first);
        Iterator<WorkpackModel> fromIterator = fromCopy.iterator();

        while (fromIterator.hasNext()) {
            WorkpackModel from = fromIterator.next();
            Collection<WorkpackModel> toCopy = new HashSet<>(second);

            for (WorkpackModel to : toCopy) {
                if (compatibleComparator.test(from, to, response)) {
                    fromIterator.remove();
                    break;
                }
            }
        }

        boolean fromCopyEmpty = fromCopy.isEmpty();
        response.setCanPaste(response.getCanPaste() && fromCopyEmpty);
        return fromCopyEmpty;
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
        if (!this.workpackModelRepository.isWorkpackInstanceByModel(idWorkpack, idWorkpackModelFrom)) {
            throw new NegocioException(ApplicationMessage.WORKPACK_PASTE_ERROR);
        }
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

}
