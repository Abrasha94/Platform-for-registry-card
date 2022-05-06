package com.modsen.cardissuer.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cards")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Card {

    @Id
    private Long number;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 10)
    private String type;

    @ManyToMany(mappedBy = "cards",fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @ToString.Exclude
    private Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Card card = (Card) o;
        return number != null && Objects.equals(number, card.number);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
