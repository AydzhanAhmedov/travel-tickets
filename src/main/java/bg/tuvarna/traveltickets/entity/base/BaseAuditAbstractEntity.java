package bg.tuvarna.traveltickets.entity.base;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Base class of each entity that has created_by (mapping user) and created_at columns in the corresponding db table,
 * it encapsulates the audit properties of an entity.
 */
@MappedSuperclass
public sealed abstract class BaseAuditAbstractEntity<T> extends BaseEntity permits BaseClientAuditEntity, BaseUserAuditEntity {

    private static final long serialVersionUID = -3330059911789806595L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false, nullable = false)
    protected T createdBy;

    @Column(name = "created_at", updatable = false, nullable = false)
    protected OffsetDateTime createdAt;

    public T getCreatedBy() {
        return createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaseAuditAbstractEntity<?> that = (BaseAuditAbstractEntity<?>) o;
        return Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdBy, createdAt);
    }

}
