package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "cashiers")
@PrimaryKeyJoinColumn(name = "client_id")
public class Cashier extends Client {

    private static final long serialVersionUID = 1596590746408352705L;

    @Column(nullable = false)
    private BigDecimal honorarium;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", updatable = false, nullable = false)
    private User createdBy;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    public Cashier() {
        super();
    }

    public Cashier(final User user) {
        super(user);
    }

    public BigDecimal getHonorarium() {
        return honorarium;
    }

    public void setHonorarium(BigDecimal honorarium) {
        this.honorarium = honorarium;
    }

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
        Cashier cashier = (Cashier) o;
        return Objects.equals(honorarium, cashier.honorarium) &&
                Objects.equals(createdBy, cashier.createdBy) &&
                Objects.equals(createdAt, cashier.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), honorarium, createdBy, createdAt);
    }

    //TODO: implement with BaseAuditEntity in sprint 3
    @PrePersist
    protected final void prePersist() {
        createdBy = new User(1L, null);
        createdAt = OffsetDateTime.now();
    }

}
