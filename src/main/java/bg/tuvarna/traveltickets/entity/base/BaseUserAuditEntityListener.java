package bg.tuvarna.traveltickets.entity.base;

import bg.tuvarna.traveltickets.service.AuthService;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;

import javax.persistence.PrePersist;
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

public final class BaseUserAuditEntityListener {

    private final AuthService authService = AuthServiceImpl.getInstance();

    @PrePersist
    public void prePersist(final BaseUserAuditEntity baseUserAuditEntity) {
        baseUserAuditEntity.createdBy = authService.getLoggedUser();
        baseUserAuditEntity.createdAt = OffsetDateTime.now(UTC);
    }

}
