package bg.tuvarna.traveltickets.entity.base;

import bg.tuvarna.traveltickets.entity.User;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(BaseUserAuditEntityListener.class)
@MappedSuperclass
public non-sealed abstract class BaseUserAuditEntity extends BaseAuditAbstractEntity<User> {
    private static final long serialVersionUID = 3671856383556914033L;
}
