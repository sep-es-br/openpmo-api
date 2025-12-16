package br.gov.es.openpmo.service.email;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.homeURI}")
    private String appHomeURI;

    @Value("${spring.mail.username}")
    private String email;
    

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
        helper.addInline("fix_inline", fix_inline, "image/gif"); // evita bug do último item

        LocalDate hoje = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        String mesAno = hoje.format(formatter);

        StringBuilder projectsBlock = new StringBuilder();

        String baseUrl = appHomeURI;

        projectsBlock
            .append("<p style='margin-top:20px;'>")
            .append("Este é um lembrete de cortesia para que você atualize:")
            .append("<br><br>")
            .append("1. Nos cronogramas, os valores realizados de custo e escopo referentes ao mês <strong>")
            .append(mesAno)
            .append("</strong> do item abaixo;")
            .append("<br>")
            .append("2. Nos diários, as informações adicionais e as evidências documentais correspondentes.")
            .append("</p>");

        projectsBlock.append(buildProjectsBlock(projects, baseUrl));

        String html = htmlTemplate.replace("{{PROJECTS_BLOCK}}", projectsBlock.toString());

        setHtml(message, html);

        mailSender.send(message);
    }

    public void sendProjectMilestonesNotification(
        String to,
        List<NotificationResultDto.ProjectEntryDto> projects,
        String userName,
        Long daysBefore,
        String htmlTemplate) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = createOutlookStrictHelper(message);

        String milestoneModelNames = projects.stream()
            .flatMap(p ->
                p.getItems() != null
                    ? p.getItems().stream()
                    : java.util.stream.Stream.<NotificationResultDto.WorkModelGroupDto>empty()
            )
            .map(NotificationResultDto.WorkModelGroupDto::getModelName)
            .distinct()
            .collect(java.util.stream.Collectors.joining(", "));
        
        String subjectFinal = milestoneModelNames + " próximos do vencimento";

        helper.setFrom(email);
        helper.setTo(to);
        helper.setSubject(subjectFinal);

        // Imagens inline
        helper.addInline("brasao", brasao, "image/png");
        helper.addInline("pmo_logo", pmoFullLogo, "image/png");
        helper.addInline("pmo_icon", pmoIcon, "image/png");
        helper.addInline("fix_inline", fix_inline, "image/gif"); // evita bug do último item

        StringBuilder projectsBlock = new StringBuilder();

        String baseUrl = appHomeURI;

        projectsBlock
            .append("<p style='margin-top:20px;'>")
            .append("Esta é uma notificação de cortesia, informando que:<br><br>")
            .append("1. O <strong>")
            .append(milestoneModelNames)
            .append("</strong> abaixo relacionado ")
            .append("vencerá ")
            .append("dentro de <strong>")
            .append(daysBefore)
            .append(" dia(s)</strong>.")
            .append("</p>");
        

        projectsBlock.append(buildProjectsBlock(projects, baseUrl));

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

    private String buildProjectsBlock(
        List<NotificationResultDto.ProjectEntryDto> projects,
        String baseUrl
    ) {
        StringBuilder projectsBlock = new StringBuilder();
    
        for (NotificationResultDto.ProjectEntryDto project : projects) {
    
            String projectUrl =
                baseUrl + "/#/workpack?id=" + project.getId()
                + "&idPlan=" + project.getPlanId();
    
            projectsBlock
                .append("<table border='0' cellpadding='0' cellspacing='0' width='100%' ")
                .append("style='margin-bottom:20px; border:1px solid #e0e0e0; border-radius:4px;'>")
    
                .append("<tr><td style='padding:15px 20px; background-color:#f9f9f9; ")
                .append("font-weight:bold; color:#44b39b;'>")
                .append(project.getModelName())
                .append(": ")
                .append(project.getProjectFullName())
                .append(" (Acesse em ")
                .append("<a href='").append(projectUrl).append("' ")
                .append("style='color:#44b39b; text-decoration:underline;'>")
                .append(projectUrl)
                .append("</a>)")
                .append("</td></tr>")
    
                .append("<tr><td style='padding:15px 20px;'>")
                .append("<p><strong>Status:</strong> ")
                .append(project.getStatus() != null ? project.getStatus() : "-")
                .append("</p>");
    
            if (project.getItems() != null) {
                for (NotificationResultDto.WorkModelGroupDto group : project.getItems()) {
    
                    projectsBlock
                        .append("<p style='font-weight:bold; margin-top:15px;'>")
                        .append(group.getModelName())
                        .append(":</p>")
                        .append("<ul>");
    
                    for (NotificationResultDto.WorkEntryDto item : group.getItems()) {
    
                        String itemUrl =
                            baseUrl + "/#/workpack?id=" + item.getId()
                            + "&idPlan=" + project.getPlanId();
    
                        projectsBlock
                            .append("<li>")
                            .append(item.getFullName())
                            .append(" (Acesse em ")
                            .append("<a href='").append(itemUrl).append("' ")
                            .append("style='color:#333; text-decoration:underline;'>")
                            .append(itemUrl)
                            .append("</a>)")
                            .append("</li>");
                    }
    
                    projectsBlock.append("</ul>");
                }
            }
    
            projectsBlock.append("</td></tr></table>");
        }
    
        projectsBlock
            .append("<p style='margin:30px 0 0 0; font-size:14px; color:#666666;'>")
            .append("Acesse o OpenPMO em ")
            .append("<a href='").append(baseUrl).append("' ")
            .append("style='color:#666666; text-decoration:underline;'>")
            .append(baseUrl)
            .append("</a>")
            .append("</p>");
    
        return projectsBlock.toString();
    }
    

}

