package bg.tuvarna.traveltickets.entity.base;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.entity.User;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.io.Serial;
import java.time.OffsetDateTime;
import java.util.Objects;

import static bg.tuvarna.traveltickets.entity.base.BaseAuditAbstractEntity.BaseClientAuditEntity;
import static bg.tuvarna.traveltickets.entity.base.BaseAuditAbstractEntity.BaseUserAuditEntity;
import static java.time.ZoneOffset.UTC;

/**
 * Base class of each entity that has created_by (mapping user) and created_at columns in the corresponding db table,
 * it encapsulates the audit properties of an entity.
 *
 * @param <T> type of the auditor.
 */
@MappedSuperclass
public sealed abstract class BaseAuditAbstractEntity<T> extends BaseEntity permits BaseClientAuditEntity, BaseUserAuditEntity {

    @MappedSuperclass
    public static non-sealed abstract class BaseUserAuditEntity extends BaseAuditAbstractEntity<User> {
        @Serial
        private static final long serialVersionUID = -5046333786382417004L;
    }

    @MappedSuperclass
    public static non-sealed abstract class BaseClientAuditEntity<T extends Client> extends BaseAuditAbstractEntity<T> {
        @Serial
        private static final long serialVersionUID = -5046333786382417004L;
    }

    @Serial
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

    @SuppressWarnings("unchecked")
    @PrePersist
    public final void prePersistAuditEntity() {
        createdBy = (T) (
                this instanceof BaseUserAuditEntity
                        ? AuthServiceImpl.getInstance().getLoggedUser()
                        : AuthServiceImpl.getInstance().getLoggedClient()
        );
        createdAt = OffsetDateTime.now(UTC);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final BaseAuditAbstractEntity<?> that = (BaseAuditAbstractEntity<?>) o;
        return Objects.equals(createdBy, that.createdBy) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdBy, createdAt);
    }

}
