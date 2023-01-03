package com.modsen.cardissuer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "companies")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Card> cards;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Company company = (Company) o;
        return getId() != null && Objects.equals(getId(), company.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
