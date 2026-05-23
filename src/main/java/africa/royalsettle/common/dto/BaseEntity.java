package africa.royalsettle.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

import static africa.royalsettle.common.constants.AppConstant.CODE_TIMESTAMP_FORMATTER;

@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BaseEntity extends BaseEntityNoId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @JsonIgnore
    private Long id;

    @NaturalId
    @Column(nullable = false, unique = true, updatable = false, length = 32)
    private String code = generateCode();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (Objects.isNull(object) || getClass() != object.getClass()) return false;

        if (Objects.isNull(id)) return super.equals(object);

        BaseEntity baseModel = (BaseEntity) object;

        return new EqualsBuilder()
                .append(id, baseModel.id)
                .append(getCreatedOn(), baseModel.getCreatedOn())
                .append(getLastModifiedOn(), baseModel.getLastModifiedOn())
                .isEquals();
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(id)) return super.hashCode();

        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(getCreatedOn())
                .append(getLastModifiedOn())
                .toHashCode();
    }

    private static String generateCode() {
        return CODE_TIMESTAMP_FORMATTER.format(LocalDateTime.now())
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
