package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.WorkpackPasteResponse;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.properties.models.PropertyModel;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.repository.WorkpackModelRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import br.gov.es.openpmo.utils.MutableBoolean;
import br.gov.es.openpmo.utils.TetraPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.BiPredicate;

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
            final WorkpackPasteResponse response,
            final MutableBoolean entered
    ) {
        final boolean areCompatible = model.hasSameType(other)
                && allAreWokpackModelsCompatible(
                model.getChildren(),
                other.getChildren(),
                response,
                entered,
                CheckPasteWorkpackService::areWorkpackModelsCompatible);

        if (areCompatible) {
            allArePropertyModelsCompatible(
                    model.getProperties(),
                    other.getProperties(),
                    response,
                    (m, o) -> m.hasSameType(o) && m.hasSameName(o));
        }

        return areCompatible;
    }

    private static void allArePropertyModelsCompatible(
            final Collection<PropertyModel> first,
            final Collection<PropertyModel> second,
            final WorkpackPasteResponse response,
            final BiPredicate<PropertyModel, PropertyModel> compatibleComparator
    ) {
        if (first == null && second == null) {
            response.setIncompatiblesProperties(true);
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
                if (compatibleComparator.test(from, to)) {
                    fromIterator.remove();
                    break;
                }
            }
        }

        boolean fromCopyEmpty = fromCopy.isEmpty();
        response.setIncompatiblesProperties(response.getIncompatiblesProperties() || !fromCopyEmpty);
    }

    private static boolean allAreWokpackModelsCompatible(
            final Collection<WorkpackModel> first,
            final Collection<WorkpackModel> second,
            final WorkpackPasteResponse response,
            final MutableBoolean entered,
            final TetraPredicate<WorkpackModel, WorkpackModel, WorkpackPasteResponse, MutableBoolean> compatibleComparator
    ) {
        entered.setValue(true);

        if (first == null && second == null) {
            return true;
        }

        if (first == null || second == null) {
            response.setCanPaste(false);
            return false;
        }

        if (first.size() > second.size()) {
            response.setCanPaste(false);
            return false;
        }

        Collection<WorkpackModel> fromCopy = new HashSet<>(first);
        Iterator<WorkpackModel> fromIterator = fromCopy.iterator();

        while (fromIterator.hasNext()) {
            WorkpackModel from = fromIterator.next();
            Collection<WorkpackModel> toCopy = new HashSet<>(second);

            for (WorkpackModel to : toCopy) {
                if (compatibleComparator.test(from, to, response, entered)) {
                    fromIterator.remove();
                    break;
                }
            }
        }

        boolean fromCopyEmpty = fromCopy.isEmpty();
        response.setCanPaste(response.getCanPaste() && fromCopyEmpty);
        return fromCopyEmpty;
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

        MutableBoolean entered = new MutableBoolean();
        entered.setValue(false);

        areWorkpackModelsCompatible(workpackModelFrom, workpackModelTo, response, entered);

        if (response.getCanPaste() && !entered.isValue()) {
            response.setCanPaste(false);
        }

        return response;
    }

    private WorkpackModel getWorkpackModel(final Long idWorkpackModelFrom) {
        return this.workpackModelRepository.findByIdWorkpackWithChildren(idWorkpackModelFrom)
                .orElseThrow(() -> new NegocioException(ApplicationMessage.WORKPACKMODEL_NOT_FOUND));
    }

}
