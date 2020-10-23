package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseAuditEntity;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "tickets")
public class Ticket extends BaseAuditEntity {

    private static final long serialVersionUID = 8812749995652436145L;

    @ManyToOne
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Column(name = "buyer_name", nullable = false)
    private String buyerName;

    @Column(name = "buyer_phone", nullable = false)
    private String buyerPhone;

    @Column(name = "buyer_email")
    private String buyerEmail;

    public Ticket() {
    }

    public Ticket(long id) {
        super.id = id;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    @Override
    public User getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    public OffsetDateTime getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(travel, ticket.travel) &&
                Objects.equals(buyerName, ticket.buyerName) &&
                Objects.equals(buyerPhone, ticket.buyerPhone) &&
                Objects.equals(buyerEmail, ticket.buyerEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), travel, buyerName, buyerPhone, buyerEmail);
    }
}
