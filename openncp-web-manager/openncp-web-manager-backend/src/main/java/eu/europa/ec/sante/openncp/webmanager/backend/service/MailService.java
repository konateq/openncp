package eu.europa.ec.sante.openncp.webmanager.backend.service;

import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManager;
import eu.europa.ec.sante.openncp.common.configuration.ConfigurationManagerFactory;
import eu.europa.ec.sante.openncp.common.configuration.PropertyNotFoundException;
import eu.europa.ec.sante.openncp.common.property.PropertyService;
import eu.europa.ec.sante.openncp.webmanager.backend.config.ApplicationProperties;
import eu.europa.ec.sante.openncp.webmanager.backend.config.SmtpProperties;
import eu.europa.ec.sante.openncp.webmanager.backend.persistence.model.User;
import org.apache.commons.lang3.Validate;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Properties;

@Service
public class MailService implements MessageSourceAware {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final ApplicationProperties applicationProperties;

    private final JavaMailSender mailSender;

    private final SmtpProperties smtpProperties;

    private MessageSourceAccessor messages;
    private String resetUrl;

    private final PropertyService propertyService;

    public MailService(final ApplicationProperties applicationProperties, final SmtpProperties smtpProperties, final JavaMailSender mailSender, final PropertyService propertyService) {
        this.applicationProperties = applicationProperties;
        this.mailSender = mailSender;
        this.smtpProperties = smtpProperties;
        this.propertyService = Validate.notNull(propertyService, "PropertyService must not be null");
    }

    @Override
    public void setMessageSource(@NonNull final MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }

    @Async
    public void sendMail(final String to, final String subject, final String content, final boolean multipart, final boolean html) throws MessagingException {

        final Properties properties = new Properties();
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

        final Session session;
        if (smtpProperties.getSmtp().getAuth()) {
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(smtpProperties.getUsername(), smtpProperties.getPassword());
                }
            });
        } else {
            session = Session.getInstance(properties);
        }
        final MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(applicationProperties.getMail().getFrom(), false));
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(content, "text/html");
        mimeMessage.setSentDate(new Date());
        Transport.send(mimeMessage);
    }

    @Async
    public String sendMailFromTemplate(final User user, final String titleKey) {
        final ConfigurationManager configurationManager = ConfigurationManagerFactory.getConfigurationManager();
        boolean mail;

        final String resetUrl = "<a href='" +
                applicationProperties.getPortal().getBaseUrl() +
                "/#/reset?key=" +
                user.getResetKey() +
                "'>Change OpenNCP Gateway password</a>";

        String emailBody = "<html></html>";
        String content = resetUrl;

        final String email = user.getUsername() + " [" + user.getEmail() + "]";

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/templates/mails/passwordResetMail.html");
        try (final InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                emailBody += line;
            }
        } catch (final IOException e) {
            logger.error("IOException: '{}'", e.getMessage());
        }

        emailBody = emailBody
                    .replaceAll("\\%(USERNAME)\\%", email)
                    .replaceAll("\\%(URL_RESET)\\%", resetUrl);

        try {
            mail = Boolean.getBoolean(propertyService.getPropertyValueMandatory("GTW_MAIL_ENABLED"));
        } catch (final NoSuchElementException e) {
            logger.error("MessagingException: property GTW_MAIL_ENABLED not specified '{}'", e.getMessage());
            mail = false;
        }

        if (mail) {
            try {
                final String emailSubject = messages.getMessage(titleKey, "Subject");
                sendMail(user.getEmail(), emailSubject, emailBody, false, true);
            } catch (final PropertyNotFoundException e) {
                logger.error("PropertyNotFoundException: '{}'", e.getMessage());
                content = e.getMessage();
            } catch (final MessagingException e) {
                logger.error("MessagingException: '{}'", e.getMessage());
                content = e.getMessage();
            }
        }
        return content;
    }

    @Async
    public String sendPasswordResetMail(final User user) {
        return sendMailFromTemplate(user, "Mail.SmpEditor.PasswordReset.Title");
    }
}
