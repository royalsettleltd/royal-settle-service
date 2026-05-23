package africa.royalsettle.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermiiRequest {
    private String to;
    private String from;
    private String sms;
    private String type;
    private String channel;
    private String api_key;
    private String code;
    private String phone_number;
}