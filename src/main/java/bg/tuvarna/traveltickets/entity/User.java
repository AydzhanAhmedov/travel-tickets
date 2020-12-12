package bg.tuvarna.traveltickets.entity;

import bg.tuvarna.traveltickets.entity.base.BaseEntity;
import bg.tuvarna.traveltickets.service.impl.AuthServiceImpl;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6966848063661159474L;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recipient")
    private List<NotificationRecipient> notifications = new ArrayList<>();

    public User() {
        super();
    }

    public User(final Role role) {
        this.role = role;
    }

    public User(final Long id, final Role role) {
        this(role);
        super.id = id;
    }

    public List<NotificationRecipient> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationRecipient> notifications) {
        this.notifications = notifications;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    @PostLoad
    public void postLoad() {
        role = AuthServiceImpl.getInstance().findRoleById(role.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), email, username, password, role);
    }

}
