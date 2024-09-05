package br.gov.es.openpmo.scheduler.updateActualValues;

import br.gov.es.openpmo.exception.NotUpdatedPlannedValuesException;
import br.gov.es.openpmo.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UpdatePlannedValuesFromActualValues {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePlannedValuesFromActualValues.class);

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public UpdatePlannedValuesFromActualValues(
            final ScheduleRepository scheduleRepository
    ) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional
    public void updateValuesInSchedule() {
        LOGGER.info("Updating planned values from actual values...");
        try {
            this.scheduleRepository.updatePlannedCostsFromActualValues();
        } catch (Exception e) {
            throw new NotUpdatedPlannedValuesException("Error updating planned values from actual values");
        }
    }
}
