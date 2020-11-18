package bg.tuvarna.traveltickets.entity.base;

import bg.tuvarna.traveltickets.entity.Client;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(BaseClientAuditEntityListener.class)
@MappedSuperclass
public non-sealed abstract class BaseClientAuditEntity<T extends Client> extends BaseAuditAbstractEntity<T> {
    private static final long serialVersionUID = 1896569536857474340L;
}
