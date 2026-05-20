package africa.royalsettle.notification.service.impl;

import africa.royalsettle.notification.dto.request.NotificationRequest;
import africa.royalsettle.notification.dto.request.TermiiRequest;
import africa.royalsettle.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static africa.royalsettle.notification.Utils.PhoneNumberUtils.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class TermiiSmsService implements SmsService {

    private final RestTemplate restTemplate;
    public static final String URL = "https://api.ng.termii.com/api/sms/send";
    public static final String API_KEY = "TLIEuaHiwxp54GctP8t0VwYAty44HYpumezd9NLZuOZmfCgqMqGEs5ViZA5olu";
    public static final String SENDER = "Crust";
    public static final String TYPE = "plain";
    public static final String SMS_CHANNEL = "dnd";

    @Override
    public void sendSms(NotificationRequest notificationRequest) {
        TermiiRequest request = TermiiRequest.builder()
                .to(format(notificationRequest.getRecipient()))
                .sms(notificationRequest.getMessage())
                .from(SENDER)
                .type(TYPE)
                .channel(SMS_CHANNEL)
                .api_key(API_KEY)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<TermiiRequest> entity = new HttpEntity<>(request, headers);
        try {
            restTemplate.postForEntity(URL, entity, String.class);
        } catch (HttpClientErrorException exception) {
            log.error("Error Sending SMS : {}", exception.getResponseBodyAsString());

        } catch (HttpStatusCodeException exception) {
            log.error("Exception Sending SMS : {} ", exception.getResponseBodyAsString());
        }
    }
}