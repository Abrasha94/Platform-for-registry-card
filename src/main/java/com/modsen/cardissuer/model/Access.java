package com.modsen.cardissuer.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "access")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Access {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String permission;

    @ManyToMany(mappedBy = "accessSet", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Access access = (Access) o;
        return id != null && Objects.equals(id, access.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
