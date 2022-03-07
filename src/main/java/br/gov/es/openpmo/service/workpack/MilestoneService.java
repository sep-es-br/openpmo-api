package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.dto.workpack.MilestoneDateQueryResult;
import br.gov.es.openpmo.dto.workpack.MilestoneDetailDto;
import br.gov.es.openpmo.enumerator.MilestoneStatus;
import br.gov.es.openpmo.model.properties.Date;
import br.gov.es.openpmo.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class MilestoneService {

    private final MilestoneRepository repository;

    @Autowired
    public MilestoneService(final MilestoneRepository repository) {
        this.repository = repository;
    }

    public void addStatus(final Long milestoneId, final MilestoneDetailDto milestoneDetailDto) {
        final boolean late = this.repository.isLate(milestoneId).orElse(false);
        final boolean lateConcluded = this.repository.isLateConcluded(milestoneId).orElse(false);
        final boolean onTime = this.repository.isOnTime(milestoneId).orElse(false);
        final boolean concluded = this.repository.isConcluded(milestoneId).orElse(false);

        if (late) {
            milestoneDetailDto.setStatus(MilestoneStatus.LATE);
            return;
        }
        if (lateConcluded) {
            milestoneDetailDto.setStatus(MilestoneStatus.LATE_CONCLUDED);
            return;
        }
        if (onTime) {
            milestoneDetailDto.setStatus(MilestoneStatus.ON_TIME);
            return;
        }
        if (concluded) {
            milestoneDetailDto.setStatus(MilestoneStatus.CONCLUDED);
        }
    }

    public void addDate(final Long milestoneId, final MilestoneDetailDto milestoneDetailDto) {
        this.repository.fetchMilestoneDate(milestoneId)
                .map(Date::getValue)
                .map(LocalDateTime::toLocalDate)
                .ifPresent(milestoneDetailDto::setMilestoneDate);

        final MilestoneDateQueryResult queryResult = this.repository.getMilestoneDateQueryResult(milestoneId);

        Optional.ofNullable(queryResult)
                .map(MilestoneDateQueryResult::getExpirationDate)
                .map(ZonedDateTime::toLocalDate)
                .ifPresent(milestoneDetailDto::setExpirationDate);

        Optional.ofNullable(queryResult)
                .map(MilestoneDateQueryResult::isWithinAWeek)
                .ifPresent(milestoneDetailDto::setWithinAWeek);
    }

}
