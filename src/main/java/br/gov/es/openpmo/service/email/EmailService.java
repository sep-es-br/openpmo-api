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
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private ClassPathResource brasao = new ClassPathResource("static/email/img/brasao-branco.png");

    private ClassPathResource pmoFullLogo = new ClassPathResource("static/email/img/pmo-logo.png");
    
    private ClassPathResource pmoIcon = new ClassPathResource("static/email/img/pmo.png");

    private ClassPathResource fix_inline = new ClassPathResource("static/email/img/transparent.gif");

    private String email =  "naoresponda@pmo.es.gov.br";
    

    public void sendProjectDeliverablesNotification(String to, String subject,
        List<NotificationResultDto.ProjectEntryDto> projects,
        String userName,
        String htmlTemplate) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = createOutlookStrictHelper(message);


        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);
        
        helper.addInline("brasao", brasao, "image/png");
        helper.addInline("pmo_logo", pmoFullLogo, "image/png");
        helper.addInline("pmo_icon", pmoIcon, "image/png");
        // helper.addInline("fix_inline", fix_inline, "image/gif"); // evita bug do último item

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

                if (project.getItems() != null) {
                for (NotificationResultDto.WorkEntryDto del : project.getItems()) {
                projectsBlock.append("<li>").append(del.getFullName()).append("</li>");
                }
            }

            projectsBlock.append("</ul></td></tr></table>");
        }

        String html = htmlTemplate.replace("{{PROJECTS_BLOCK}}", projectsBlock.toString());

        setHtml(message, html);

        mailSender.send(message);
    }

    public void sendProjectMilestonesNotification(
        String to,
        String subject,
        List<NotificationResultDto.ProjectEntryDto> projects,
        String userName,
        Long daysBefore,
        String htmlTemplate) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = createOutlookStrictHelper(message);


        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subject);

        // Imagens inline
        helper.addInline("brasao", brasao, "image/png");
        helper.addInline("pmo_logo", pmoFullLogo, "image/png");
        helper.addInline("pmo_icon", pmoIcon, "image/png");
        // helper.addInline("fix_inline", fix_inline, "image/gif"); // evita bug do último item

        StringBuilder projectsBlock = new StringBuilder();

        projectsBlock
            .append("Lembrete automático referente ao(s) projeto(s) abaixo!<br>")
            .append("O sistema identificou que existe(m) <strong>marco(s) crítico(s)</strong> com vencimento nos próximos <strong>")
            .append(daysBefore)
            .append(" dia(s)</strong> e que <strong>ainda não possuem registro de conclusão ou reprogramação</strong>.")
            .append("</p>");

        for (NotificationResultDto.ProjectEntryDto project : projects) {

            projectsBlock.append("<table border='0' cellpadding='0' cellspacing='0' width='100%' ")
                .append("style='margin-bottom:20px; border:1px solid #e0e0e0; border-radius:4px;'>")

                // Cabeçalho do projeto
                .append("<tr><td style='padding:15px 20px; background-color:#f9f9f9; ")
                .append("font-weight:bold; color:#20998a;'>")
                .append("Projeto: ").append(project.getProjectFullName())
                .append("</td></tr>")

                // Status e marcos críticos
                .append("<tr><td style='padding:15px 20px;'>")
                .append("<p><strong>Status:</strong> ")
                .append(project.getStatus() != null ? project.getStatus() : "-")
                .append("</p>")
                .append("<p style='font-weight:bold;'>Marcos Críticos:</p><ul>");

            if (project.getItems() != null) {
                for (NotificationResultDto.WorkEntryDto milestone : project.getItems()) {
                    projectsBlock.append("<li>")
                        .append(milestone.getFullName())
                        .append("</li>");
                }
            }

            projectsBlock.append("</ul></td></tr></table>");
        }

        // Substitui o placeholder no template
        String html = htmlTemplate.replace("{{PROJECTS_BLOCK}}", projectsBlock.toString());

        setHtml(message, html);

        mailSender.send(message);
    }

private MimeMessageHelper createOutlookStrictHelper(MimeMessage message)
            throws MessagingException {

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        MimeMessageHelper.MULTIPART_MODE_RELATED,
                        StandardCharsets.UTF_8.name()
                );

        try {
            MimeMultipart root = (MimeMultipart) message.getContent();

            MimeBodyPart container = new MimeBodyPart();
            MimeMultipart alternative = new MimeMultipart("alternative");

            MimeBodyPart plain = new MimeBodyPart();
            plain.setText("", "UTF-8");
            alternative.addBodyPart(plain);

            MimeBodyPart html = new MimeBodyPart();
            html.setContent("<html></html>", "text/html; charset=UTF-8");
            alternative.addBodyPart(html);

            container.setContent(alternative);
            root.addBodyPart(container);

        } catch (IOException e) {
            throw new MessagingException("Erro ao montar estrutura MIME", e);
        }

        return helper;
    }

    private void setHtml(MimeMessage message, String html)
            throws MessagingException {

        try {
            MimeMultipart root = (MimeMultipart) message.getContent();
            MimeBodyPart container = (MimeBodyPart) root.getBodyPart(0);
            MimeMultipart alt = (MimeMultipart) container.getContent();
            MimeBodyPart htmlPart = (MimeBodyPart) alt.getBodyPart(1);

            htmlPart.setContent(html, "text/html; charset=UTF-8");

        } catch (IOException e) {
            throw new MessagingException("Erro ao definir HTML", e);
        }
    }

}

