package br.gov.es.openpmo.service.email;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.gov.es.openpmo.dto.NotificationResultDto;
import br.gov.es.openpmo.model.workpacks.models.ProjectModel;
import br.gov.es.openpmo.repository.ProjectModelRepository;

@Service
public class NotificationsService {

    private final ProjectModelRepository projectModelRepository;

    private final EmailService emailService; 

    @Autowired
    public NotificationsService(
        final ProjectModelRepository projectModelRepository,
        final EmailService emailService
    ){
        this.projectModelRepository = projectModelRepository;
        this.emailService = emailService;
    }

    // Em produção provavelmente seria 0 0 8 * * *
    @Scheduled(cron = "0 */2 * * * *")
    public void checkProjectSchedulesAndMilestones() {

        System.out.println("Executando verificação de agendas...");

        List<ProjectModel> list = projectModelRepository.findAllWithNotificationsSessionActive();

        if (list.isEmpty()) {
            return;
        }

        for (ProjectModel model : list) {

            Boolean scheduleNotificationEnabled = model.getNotificationsEventScheduleEnabled();

            Boolean MilestoneEventsNotificationEnabled = model.getNotificationsEventMilestoneEnabled();

            if (Boolean.TRUE.equals(scheduleNotificationEnabled)) {

                LocalDate today = LocalDate.now();
                int todayDay = today.getDayOfMonth();
                int lastDayOfMonth = today.lengthOfMonth();

                Long configuredDay = model.getNotificationsEventScheduleDayOfMonth();

                boolean shouldTrigger = (configuredDay == todayDay) || // Caso 1 — Dia existe no mês
                                (configuredDay > lastDayOfMonth && todayDay == lastDayOfMonth); // Caso 2 — Dia NÃO existe no mês → dispara no último dia

                if (shouldTrigger) {
                
                    List<NotificationResultDto> results =
                        projectModelRepository.fetchScheduleNotificationData(model.getId());
                
                    if (!results.isEmpty()) {
                        for (NotificationResultDto dto : results) {

                            try {
                                ClassPathResource resource = new ClassPathResource("static/email/openpmo_email_template.html");
                                String htmlTemplate = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
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

                List<NotificationResultDto> results =
                        projectModelRepository.fetchMilestoneNotificationData(model.getId());

                if (!results.isEmpty()) {
                    for (NotificationResultDto dto : results) {

                        try {
                            String htmlTemplate = Files.readString(Paths.get("src/main/resources/static/email/openpmo_email_template.html"));
                            emailService.sendProjectMilestonesNotification(
                                dto.getEmail(),
                                "Atualização de Marcos Críticos",
                                dto.getProjects(),  
                                dto.getFullName(),
                                model.getNotificationsEventMilestoneDaysBefore(),
                                htmlTemplate
                            );
                        } catch (Exception e) {
                            System.err.println("Erro ao enviar e-mail para " + dto.getEmail() + ": " + e.getMessage());
                        }
            
                    }
                }
                
            }
        }
    }

}
