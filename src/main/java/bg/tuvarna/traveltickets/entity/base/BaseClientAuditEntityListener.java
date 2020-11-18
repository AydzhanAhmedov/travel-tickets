package bg.tuvarna.traveltickets.entity.base;

import bg.tuvarna.traveltickets.entity.Client;
import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;

import javax.persistence.PrePersist;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

public final class BaseClientAuditEntityListener {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @SuppressWarnings("unchecked")
    @PrePersist
    public <T extends Client> void prePersist(final BaseClientAuditEntity<T> baseClientAuditEntity) {
        baseClientAuditEntity.createdBy = (T) authService.getLoggedClient();
        baseClientAuditEntity.createdAt = OffsetDateTime.now(UTC);
    }

}
