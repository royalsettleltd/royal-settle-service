package africa.royalsettle.common.dto;

import africa.royalsettle.common.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseEntityNoId extends BaseAuditEntity {
    @Version private int version;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 31)
    @ColumnDefault("'ACTIVE'")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Status recordStatus = Status.ACTIVE;
}
