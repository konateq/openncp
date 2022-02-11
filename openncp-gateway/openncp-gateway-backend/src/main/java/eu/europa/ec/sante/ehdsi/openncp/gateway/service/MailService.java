package eu.europa.ec.sante.ehdsi.openncp.gateway.service;

import eu.europa.ec.sante.ehdsi.openncp.configmanager.ConfigurationManager;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.ConfigurationManagerFactory;
import eu.europa.ec.sante.ehdsi.openncp.configmanager.PropertyNotFoundException;
import eu.europa.ec.sante.ehdsi.openncp.gateway.config.ApplicationProperties;
import eu.europa.ec.sante.ehdsi.openncp.gateway.config.SmtpProperties;
import eu.europa.ec.sante.ehdsi.openncp.gateway.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class MailService implements MessageSourceAware {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final ApplicationProperties applicationProperties;

    private final JavaMailSender mailSender;

    private final SmtpProperties smtpProperties;

    private MessageSourceAccessor messages;

    public MailService(ApplicationProperties applicationProperties, SmtpProperties smtpProperties, JavaMailSender mailSender) {
        this.applicationProperties = applicationProperties;
        this.mailSender = mailSender;
        this.smtpProperties = smtpProperties;
    }

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }

    @Async
    public void sendMail(String to, String subject, String content, boolean multipart, boolean html) throws MessagingException {

        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpProperties.getHost());
        properties.put("mail.smtp.port", smtpProperties.getPort());
        properties.put("mail.smtp.auth", smtpProperties.getSmtp().getAuth());
        properties.put("mail.smtp.connectiontimeout", smtpProperties.getSmtp().getConnectionTimeout());
        properties.put("mail.smtp.timeout", smtpProperties.getSmtp().getTimeout());
        properties.put("mail.smtp.writetimeout", smtpProperties.getSmtp().getWriteTimeout());
        properties.put("mail.smtp.starttls.enable", smtpProperties.getSmtp().getStartTls().getEnabled());
        properties.put("mail.smtp.starttls.required", smtpProperties.getSmtp().getStartTls().getRequired());
        properties.put("mail.smtp.ssl.enable", smtpProperties.getSmtp().getSsl().getEnable());
        properties.put("mail.smtp.ssl.trust", smtpProperties.getSmtp().getSsl().getTrust());

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpProperties.getUsername(), smtpProperties.getPassword());
            }
        });
        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(applicationProperties.getMail().getFrom(), false));
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html");
        mimeMessage.setSentDate(new Date());
        Transport.send(mimeMessage);
    }

    @Async
    public String sendMailFromTemplate(User user, String titleKey) {
        ConfigurationManager configurationManager = ConfigurationManagerFactory.getConfigurationManager();
        boolean mail = false;
        String content = "Change your password <a href='" +
                applicationProperties.getPortal().getBaseUrl() +
                "/#/reset?key=" +
                user.getResetKey() +
                "'>here</a>";
        try {
            mail = configurationManager.getBooleanProperty("GTW_MAIL_ENABLED");
            if (mail) {
                String subject = messages.getMessage(titleKey, "subject");
                sendMail(user.getEmail(), subject, content, false, true);
            }
        } catch (PropertyNotFoundException e) {
            logger.error("PropertyNotFoundException: '{}'", e.getMessage());
            content = e.getMessage();
        } catch (MessagingException e) {
            logger.error("MessagingException: '{}'", e.getMessage());
            content = e.getMessage();
        }
        return content;
    }

    @Async
    public String sendPasswordResetMail(User user) {
        return sendMailFromTemplate(user, "Mail.PasswordReset.Title");
    }
}
