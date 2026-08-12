package br.gov.es.openpmo.scheduler;

import br.gov.es.openpmo.service.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class ProcessUpdateScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessUpdateScheduler.class);
    private final ProcessService processService;

    public ProcessUpdateScheduler(final ProcessService processService) {
        this.processService = processService;
    }

    @Scheduled(cron = "${app.scheduler.update-processes}")
    public void updateProcesses() {
        final StopWatch stopWatch = new StopWatch("Atualização de processos");
        LOGGER.info("Job de atualização de processos iniciado");
        stopWatch.start("updateAllProcesses");
        try {
            processService.updateAllProcesses();
        } catch (final RuntimeException exception) {
            LOGGER.error("Erro durante o job de atualização de processos", exception);
            throw exception;
        } finally {
            stopWatch.stop();
            LOGGER.info(
                "Job de atualização de processos finalizado em {} ms ({} s)",
                stopWatch.getTotalTimeMillis(),
                stopWatch.getTotalTimeSeconds()
            );
        }
    }
}
