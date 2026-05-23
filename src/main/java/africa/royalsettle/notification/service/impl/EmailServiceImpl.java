package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.dto.request.NotificationAttachment;
import africa.royalsettle.notification.dto.request.NotificationRequest;
import africa.royalsettle.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void send(NotificationRequest notificationRequest) {
        if (!isValidRequest(notificationRequest)) {
            log.warn("Invalid notification request missing recipient or message");
            return;
        }

        try {
            MimeMessage message = createMimeMessage(notificationRequest);
            javaMailSender.send(message);
        } catch (MessagingException ex) {
            log.error("Failed to create email message for recipient {}: {}",
                    notificationRequest.getRecipient(), ex.getMessage());
        } catch (MailException ex) {
            log.error("Failed to send email to recipient {}: {}",
                    notificationRequest.getRecipient(), ex.getMessage());
        }
    }

    private boolean isValidRequest(NotificationRequest request) {
        return StringUtils.isNotBlank(request.getRecipient()) &&
                StringUtils.isNotBlank(request.getMessage());
    }

    private MimeMessage createMimeMessage(NotificationRequest request) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String[] recipients = splitEmail(request.getRecipient());

        helper.setFrom(request.getSender());
        helper.setTo(recipients);
        helper.setSubject(request.getSubject());
        helper.setText(request.getMessage(), true);

        addAttachments(helper, request.getAttachments());
        return message;
    }

    private void addAttachments(MimeMessageHelper helper,
                                List<NotificationAttachment> attachments) throws MessagingException {
        if (Objects.isNull(attachments) || attachments.isEmpty()) {
            return;
        }

        for (NotificationAttachment attachment : attachments) {
            if (!isValidAttachment(attachment)) {
                log.warn("Skipping invalid attachment");
                continue;
            }

            helper.addAttachment(
                    attachment.getFilename(),
                    new ByteArrayResource(attachment.getData()),
                    attachment.getContentType()
            );
        }
    }

    private boolean isValidAttachment(NotificationAttachment attachment) {
        return Objects.nonNull(attachment) &&
                Objects.nonNull(attachment.getData()) &&
                attachment.getData().length > 0 &&
                StringUtils.isNotBlank(attachment.getFilename());
    }

    private static String[] splitEmail(String emails) {
        return emails.split(",");
    }
}