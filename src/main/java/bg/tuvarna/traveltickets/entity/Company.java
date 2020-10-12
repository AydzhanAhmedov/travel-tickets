package bg.tuvarna.traveltickets.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "companies")
@PrimaryKeyJoinColumn(name = "client_id")
public class Company extends Client {

    private static final long serialVersionUID = -8277742577799198024L;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column
    private String description;

    public Company() {
        super();
    }

    public Company(final User user) {
        super(user);
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Company company = (Company) o;
        return Objects.equals(logoUrl, company.logoUrl) &&
                Objects.equals(description, company.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), logoUrl, description);
    }

}
