package africa.royalsettle.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BaseEntity extends BaseEntityNoId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

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
}
