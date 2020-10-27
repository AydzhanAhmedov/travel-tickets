package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "clients")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Client implements Serializable {

    private static final long serialVersionUID = -9066096581032000088L;

    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @MapsId("user_id")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_type_id", nullable = false)
    private ClientType clientType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Address address;

    public Client() {
        super();
    }

    public Client(final User user) {
        userId = user.getId();
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(userId, client.userId) &&
                Objects.equals(user, client.user) &&
                Objects.equals(clientType, client.clientType) &&
                Objects.equals(name, client.name) &&
                Objects.equals(phone, client.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, user, clientType, name, phone);
    }

}