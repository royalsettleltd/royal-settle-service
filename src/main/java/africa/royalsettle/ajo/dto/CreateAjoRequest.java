package africa.royalsettle.ajo.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CreateAjoRequest {

    private String name;
    private BigDecimal amount;
    private String createdBy;
    private String frequency;
    private String duration;


}