package br.gov.es.openpmo.service.baselines;

import br.gov.es.openpmo.dto.baselines.EvaluationItem;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsEvaluatedBy;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.repository.IsEvaluatedByRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GetAllBaselineEvaluations implements IGetAllBaselineEvaluations {

    private final IsCCBMemberRepository isCCBMemberRepository;

    private final IsEvaluatedByRepository evaluatedByRepository;

    @Autowired
    public GetAllBaselineEvaluations(
            IsCCBMemberRepository isCCBMemberRepository,
            IsEvaluatedByRepository evaluatedByRepository
    ) {
        this.isCCBMemberRepository = isCCBMemberRepository;
        this.evaluatedByRepository = evaluatedByRepository;
    }

    @Override
    public List<EvaluationItem> getEvaluations(final Long idBaseline) {
        return this.getMembers(idBaseline)
                .stream()
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

    private Set<Person> getMembers(final Long idBaseline) {
        Set<Person> activeMembersOfBaseline = this.isCCBMemberRepository.findAllActiveMembersOfBaseline(idBaseline);
        Set<Person> evaluators = this.evaluatedByRepository.findEvaluators(idBaseline);
        activeMembersOfBaseline.addAll(evaluators);
        return activeMembersOfBaseline;
    }

}
