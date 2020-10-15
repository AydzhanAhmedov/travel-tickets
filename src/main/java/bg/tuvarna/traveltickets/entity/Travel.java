package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseAuditEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "travels")
public class Travel extends BaseAuditEntity {

    private static final long serialVersionUID = 6063243358784196914L;

    @ManyToOne
    @JoinColumn(name = "travel_type_id", nullable = false)
    private TravelType travelType;

    @ManyToOne
    @JoinColumn(name = "travel_status_id", nullable = false)
    private TravelStatus travelStatus;

    @Column(name = "start_date", nullable = false)
    private OffsetDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private OffsetDateTime endDate;

    @Column(name = "ticket_quantity", nullable = false)
    private int ticketQuantity;

    @Column(name = "current_ticket_quantity", nullable = false)
    private int currentTicketQuantity;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    @Column(name = "ticket_buy_limit")
    private int ticketBuyLimit;

    private String details;

    //TODO implement many to many logic here
    //@OneToMany(mappedBy = "travel")
    //List<TravelRoute> travelRouteList;

    public Travel() {
    }

    public Travel(long id){
        super.id = id;
    }

    public TravelType getTravelType() {
        return travelType;
    }

    public void setTravelType(TravelType travelType) {
        this.travelType = travelType;
    }

    public TravelStatus getTravelStatus() {
        return travelStatus;
    }

    public void setTravelStatus(TravelStatus travelStatus) {
        this.travelStatus = travelStatus;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public int getCurrentTicketQuantity() {
        return currentTicketQuantity;
    }

    public void setCurrentTicketQuantity(int currentTicketQuantity) {
        this.currentTicketQuantity = currentTicketQuantity;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTicketBuyLimit() {
        return ticketBuyLimit;
    }

    public void setTicketBuyLimit(int ticketBuyLimit) {
        this.ticketBuyLimit = ticketBuyLimit;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
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
        Travel travel = (Travel) o;
        return Objects.equals(travelType, travel.travelType) &&
                Objects.equals(travelStatus, travel.travelStatus) &&
                Objects.equals(startDate, travel.startDate) &&
                Objects.equals(endDate, travel.endDate) &&
                Objects.equals(ticketQuantity,travel.ticketQuantity) &&
                Objects.equals(currentTicketQuantity, travel.currentTicketQuantity) &&
                Objects.equals(ticketPrice, travel.ticketPrice) &&
                Objects.equals(ticketBuyLimit, travel.ticketBuyLimit) &&
                Objects.equals(details, travel.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                travelType,
                startDate,
                endDate,
                ticketQuantity,
                currentTicketQuantity,
                ticketPrice,
                ticketBuyLimit,
                details
        );
    }
}
