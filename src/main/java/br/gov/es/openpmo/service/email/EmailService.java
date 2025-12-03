package br.gov.es.openpmo.service.email;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.gov.es.openpmo.dto.NotificationResultDto;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendProjectDeliverablesNotification(String to, String subject,
        List<NotificationResultDto.ProjectEntryDto> projects,
        String userName,
        String htmlTemplate) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setFrom("notificacoes@openpmo.gov.br");
        helper.setTo(to);
        helper.setSubject(subject);

        ClassPathResource brasao = new ClassPathResource("static/email/img/brasao-branco.png");
        helper.addInline("brasao", brasao);

        ClassPathResource pmoFullLogo = new ClassPathResource("static/email/img/pmo-logo.png");
        helper.addInline("pmo_logo", pmoFullLogo);

        ClassPathResource pmoIcon = new ClassPathResource("static/email/img/pmo.png");
        helper.addInline("pmo_icon", pmoIcon);

        ClassPathResource pmoIcon2 = new ClassPathResource("static/email/img/pmo.png");
        helper.addInline("pmo_icon2", pmoIcon2);

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        String mesAno = hoje.format(formatter);

        StringBuilder projectsBlock = new StringBuilder();

            projectsBlock
            .append("<p style='margin-top:20px;'>")
            .append("O sistema identificou que o(s) cronograma(s) físico-financeiro do(s) mês(es) de referência <strong>")
            .append(mesAno)
            .append("</strong> do(s) projeto(s) abaixo não foi(foram) preenchido(s) até a data limite (10):")
            .append("</p>");

            for (NotificationResultDto.ProjectEntryDto project : projects) {

                projectsBlock.append("<table border='0' cellpadding='0' cellspacing='0' width='100%' style='margin-bottom:20px; border:1px solid #e0e0e0; border-radius:4px;'>")
                .append("<tr><td style='padding:15px 20px; background-color:#f9f9f9; font-weight:bold; color:#20998a;'>")
                .append("Projeto: ").append(project.getProjectFullName()).append("</td></tr>")
                .append("<tr><td style='padding:15px 20px;'>")
                .append("<p><strong>Status:</strong> ").append(project.getStatus() != null ? project.getStatus() : "-").append("</p>")
                .append("<p style='font-weight:bold;'>Entregas:</p><ul>");

                if (project.getDeliverables() != null) {
                for (NotificationResultDto.DeliverableEntryDto del : project.getDeliverables()) {
                projectsBlock.append("<li>").append(del.getFullName()).append("</li>");
                }
            }

            projectsBlock.append("</ul></td></tr></table>");
        }

        String html = htmlTemplate.replace("{{PROJECTS_BLOCK}}", projectsBlock.toString());

        helper.setText(html, true);

        mailSender.send(message);
    }
}

