package com.modsen.cardissuer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String keycloakUserId;

    @Column(nullable = false, length = 55, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    @Transient
    private String confirmPassword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companies_id", nullable = false)
    @ToString.Exclude
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @ToString.Exclude
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_permissions",
    joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name = "access_id", referencedColumnName = "id")})
    @ToString.Exclude
    private Set<Access> accessSet;

    @OneToMany(mappedBy = "user")
    private List<UsersCards> usersCards;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
