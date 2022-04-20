package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GetAllBaselineEvaluations implements IGetAllBaselineEvaluations {

    private final IsEvaluatedByRepository evaluatedByRepository;

    @Autowired
    public GetAllBaselineEvaluations(final IsEvaluatedByRepository evaluatedByRepository) {
        this.evaluatedByRepository = evaluatedByRepository;
    }

    @Override
    public List<EvaluationItem> getEvaluations(final Long idBaseline) {
        final List<Person> members = this.getMembers(idBaseline);
        return members.stream()
                .map(member -> this.getEvaluationItem(member, idBaseline))
                .collect(Collectors.toList());
    }

    private EvaluationItem getEvaluationItem(final Person member, final Long idBaseline) {
        final Optional<IsEvaluatedBy> maybeEvaluation = this.getEvaluation(member, idBaseline);
        return maybeEvaluation.map(EvaluationItem::fromBaselineEvaluated)
                .orElseGet(() -> EvaluationItem.fromBaselineNotEvaluated(member));
    }

    private Optional<IsEvaluatedBy> getEvaluation(final Person member, final Long idBaseline) {
        return this.evaluatedByRepository.findEvaluation(idBaseline, member.getId());
    }

    private List<Person> getMembers(final Long idBaseline) {
        return this.evaluatedByRepository.findEvaluators(idBaseline);
    }

}
