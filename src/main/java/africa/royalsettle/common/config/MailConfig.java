package africa.royalsettle.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.username}") String username,
            @Value("${spring.mail.password}") String password,
            @Value("${spring.mail.properties.mail.smtp.auth:true}") boolean smtpAuth,
            @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}") boolean starttlsEnable,
            @Value("${spring.mail.properties.mail.smtp.ssl.enable:false}") boolean sslEnable,
            @Value("${spring.mail.properties.mail.smtp.timeout:5000}") int timeout,
            @Value("${spring.mail.properties.mail.debug:false}") boolean mailDebug) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        boolean implicitSsl = sslEnable || port == 465;

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.ssl.enable", implicitSsl);
        props.put("mail.smtp.starttls.enable", !implicitSsl && starttlsEnable);
        props.put("mail.smtp.connectiontimeout", timeout);
        props.put("mail.smtp.timeout", timeout);
        props.put("mail.smtp.writetimeout", timeout);
        props.put("mail.debug", mailDebug);

        return mailSender;
    }
}
