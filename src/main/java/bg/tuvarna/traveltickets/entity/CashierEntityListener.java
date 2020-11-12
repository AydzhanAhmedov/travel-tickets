package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;

import javax.persistence.PrePersist;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

public final class CashierEntityListener {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @PrePersist
    public void prePersist(final Cashier cashier) {
        cashier.createdBy = authService.getLoggedUser();
        cashier.createdAt = OffsetDateTime.now(UTC);
    }

}
