package bg.tuvarna.traveltickets.entity;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "distributors")
@PrimaryKeyJoinColumn(name = "client_id")
public class Distributor extends Client {

    private static final long serialVersionUID = 2518638848280660547L;

    public Distributor() {
        super();
    }

    public Distributor(final User user) {
        super(user);
    }

}
