package br.gov.es.openpmo.service.schedule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.repository.ProjectModelRepository;

@Service
public class NotificationsScheduleService {

    private final ProjectModelRepository projectModelRepository;

    @Autowired
    public NotificationsScheduleService(
        final ProjectModelRepository projectModelRepository
    ){
        this.projectModelRepository = projectModelRepository;
    }

    // Executa a cada 10 segundos apenas para teste.
    // Em produção provavelmente seria 0 0 8 * * *
    @Scheduled(cron = "*/10 * * * * *")
    public void checkProjectsSchedules() {

        System.out.println("Executando verificação de agendas...");

        List<ProjectModel> list = projectModelRepository.findAllWithNotificationsSessionActive();

        if (list.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        int todayDay = today.getDayOfMonth();
        int lastDayOfMonth = today.lengthOfMonth();

        for (ProjectModel model : list) {

            Long configuredDay = model.getNotificationsEventScheduleDayOfMonth();
            Boolean notificationEnabled = model.getNotificationsEventScheduleEnabled();

            if (Boolean.TRUE.equals(notificationEnabled)) {

                if (configuredDay == null || configuredDay < 1 || configuredDay > 31) {
                    System.out.println("⚠ Configuração de dia inválida para o projeto: " + model.getId());
                    continue;
                }

                boolean shouldTrigger = (configuredDay == todayDay) || // Caso 1 — Dia existe no mês
                                (configuredDay > lastDayOfMonth && todayDay == lastDayOfMonth); // Caso 2 — Dia NÃO existe no mês → dispara no último dia

                if (shouldTrigger) {
                    System.out.println("🔔 Disparando notificação para o projeto ID: " + model.getId());
                }
            }
        }
    }

}
