package br.gov.es.openpmo.apis.edocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.service.process.ProcessService;
import org.springframework.util.StopWatch;

@Service
public class EDocsProcessService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(EDocsProcessService.class);

    private final ProcessService processService;

    public EDocsProcessService(final ProcessService processService) {
        this.processService = processService;
    }

    /**
     * Executa a cada 5 minutos
     */
    @Scheduled(cron = "${app.scheduler.update-processes}")
    public void updateProcessesFromEDocs() {

        StopWatch stopWatch = new StopWatch("Job eDocs");

        LOGGER.info("▶ Job eDocs iniciado");

        stopWatch.start("updateAllProcesses");

        try {
            processService.updateAllProcesses();
        } catch (Exception ex) {
            LOGGER.error("❌ Erro durante o job de atualização de processos (eDocs)", ex);
            throw ex;
        } finally {
            stopWatch.stop();

            LOGGER.info(
                "✔ Job eDocs finalizado | Duração real: {} ms ({} s)",
                stopWatch.getTotalTimeMillis(),
                stopWatch.getTotalTimeSeconds()
            );
        }
    }
}
