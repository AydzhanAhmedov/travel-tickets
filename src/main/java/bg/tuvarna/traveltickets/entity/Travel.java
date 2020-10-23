package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseAuditEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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
    private Integer ticketQuantity;

    @Column(name = "current_ticket_quantity", nullable = false)
    private Integer currentTicketQuantity;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    @Column(name = "ticket_buy_limit")
    private Integer ticketBuyLimit;

    private String details;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL)
    private List<TravelRoute> travelRoutes = new ArrayList<>();

    //@OneToMany(mappedBy = "travel")
    //private List<TravelDistributorRequest> distributorRequests = new ArrayList<>();

    //public void addDistributorRequest(Distributor distributor, RequestStatus requestStatus) {
    //    TravelDistributorRequest distributorRequest = new TravelDistributorRequest(this, distributor);
    //    distributorRequest.setRequestStatus(requestStatus);
    //    distributorRequests.add(distributorRequest);
    //}

    public void addTravelRoute(City city, OffsetDateTime date, TransportType type) {
        TravelRoute travelRoute = new TravelRoute(this, city);
        travelRoute.setArrivalDate(date);
        travelRoute.setTransportType(type);
        travelRoutes.add(travelRoute);
    }

    // TODO test remove
    public void removeTravelRoute(City city) {
        for (Iterator<TravelRoute> iterator = travelRoutes.iterator();
             iterator.hasNext(); ) {
            TravelRoute travelRoute = iterator.next();

            if (travelRoute.getTravel().equals(this) &&
                    travelRoute.getCity().equals(city)) {
                iterator.remove();
                travelRoute.setCity(null);
                travelRoute.setTravel(null);
            }
        }
    }

    public Travel() {
    }

    public Travel(Long id) {
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

    public Integer getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(Integer ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public Integer getCurrentTicketQuantity() {
        return currentTicketQuantity;
    }

    public void setCurrentTicketQuantity(Integer currentTicketQuantity) {
        this.currentTicketQuantity = currentTicketQuantity;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Integer getTicketBuyLimit() {
        return ticketBuyLimit;
    }

    public void setTicketBuyLimit(Integer ticketBuyLimit) {
        this.ticketBuyLimit = ticketBuyLimit;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<TravelRoute> getTravelRoutes() {
        return travelRoutes;
    }

    public void setTravelRoutes(List<TravelRoute> travelRoutes) {
        this.travelRoutes = travelRoutes;
    }

    //public List<TravelDistributorRequest> getDistributorRequests() {
    //    return distributorRequests;
    //}
//
    //public void setDistributorRequests(List<TravelDistributorRequest> distributorRequests) {
    //    this.distributorRequests = distributorRequests;
    //}


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
                Objects.equals(ticketQuantity, travel.ticketQuantity) &&
                Objects.equals(currentTicketQuantity, travel.currentTicketQuantity) &&
                Objects.equals(ticketPrice, travel.ticketPrice) &&
                Objects.equals(ticketBuyLimit, travel.ticketBuyLimit) &&
                Objects.equals(details, travel.details) &&
                Objects.equals(travelRoutes, travel.travelRoutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), travelType, travelStatus, startDate, endDate, ticketQuantity, currentTicketQuantity, ticketPrice, ticketBuyLimit, details, travelRoutes);
    }
}
