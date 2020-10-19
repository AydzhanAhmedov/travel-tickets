package bg.tuvarna.traveltickets.entity.base;

import bg.tuvarna.traveltickets.entity.User;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Base class of each entity that created_by and created_at columns in the corresponding db table,
 * it encapsulates the audit properties of an entity.
 */
@MappedSuperclass
public abstract class BaseAuditEntity extends BaseEntity {

    private static final long serialVersionUID = -3330059911789806595L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false, nullable = false)
    protected User createdBy;

    @Column(name = "created_at", updatable = false, nullable = false)
    protected OffsetDateTime createdAt;

    public User getCreatedBy() {
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
        BaseAuditEntity that = (BaseAuditEntity) o;
        return Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdBy, createdAt);
    }

    @PrePersist
    protected final void prePersist() {
        createdBy = new User(1L, null); // TODO in next sprint: implement logic for retrieving the currently logged user
        createdAt = OffsetDateTime.now(); // TODO: get the time in GMT
    }

}
