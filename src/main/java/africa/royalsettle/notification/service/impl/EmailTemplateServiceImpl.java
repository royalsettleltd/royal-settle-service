package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private static final String OTP_PLACEHOLDER = "{{OTP}}";
    private static final String OTP_EMAIL_TEMPLATE_PATH = "otp-email.html";

    @Override
    public String buildOtpEmail(String otp) {
        return loadTemplate(OTP_EMAIL_TEMPLATE_PATH).replace(OTP_PLACEHOLDER, otp);
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load email template: " + path, e);
        }
    }
}
