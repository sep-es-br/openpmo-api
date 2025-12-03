package br.gov.es.openpmo.service.schedule;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.NotificationResultDto;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.repository.ProjectModelRepository;
import br.gov.es.openpmo.service.email.EmailService;

@Service
public class NotificationsScheduleService {

    private final ProjectModelRepository projectModelRepository;

    private final EmailService emailService; 

    @Autowired
    public NotificationsScheduleService(
        final ProjectModelRepository projectModelRepository,
        final EmailService emailService
    ){
        this.projectModelRepository = projectModelRepository;
        this.emailService = emailService;
    }

    // Em produção provavelmente seria 0 0 8 * * *
    @Scheduled(cron = "0 */2 * * * *")
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

            Boolean scheduleNotificationEnabled = model.getNotificationsEventScheduleEnabled();

            Boolean MilestoneEventsNotificationEnabled = model.getNotificationsEventMilestoneEnabled();

            if (Boolean.TRUE.equals(scheduleNotificationEnabled)) {

                Long configuredDay = model.getNotificationsEventScheduleDayOfMonth();

                if (configuredDay == null || configuredDay < 1 || configuredDay > 31) {
                    System.out.println("⚠ Configuração de dia inválida para o projeto: " + model.getId());
                    continue;
                }

                boolean shouldTrigger = (configuredDay == todayDay) || // Caso 1 — Dia existe no mês
                                (configuredDay > lastDayOfMonth && todayDay == lastDayOfMonth); // Caso 2 — Dia NÃO existe no mês → dispara no último dia

                if (shouldTrigger) {
                
                    List<NotificationResultDto> results =
                        projectModelRepository.fetchNotificationData(model.getId());
                
                    if (!results.isEmpty()) {
                        for (NotificationResultDto dto : results) {

                            try {
                                String htmlTemplate = Files.readString(Paths.get("src/main/resources/static/email/openpmo_email_template.html"));
                                emailService.sendProjectDeliverablesNotification(
                                    dto.getEmail(),
                                    "Atualização de cronograma e Diário da entrega",
                                    dto.getProjects(),  
                                    dto.getFullName(),
                                    htmlTemplate
                                );
                            } catch (Exception e) {
                                System.err.println("Erro ao enviar e-mail para " + dto.getEmail() + ": " + e.getMessage());
                            }
                
                        }
                    }
                }
            }

            if (Boolean.TRUE.equals(MilestoneEventsNotificationEnabled)) {

                Long configuredDay = model.getNotificationsEventMilestoneDaysBefore();

                
            }
        }
    }

}
