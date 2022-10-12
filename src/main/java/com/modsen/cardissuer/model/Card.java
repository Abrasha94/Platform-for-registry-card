package com.modsen.cardissuer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "pay_system", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaySystem paySystem;

    @OneToMany(mappedBy = "card")
    @ToString.Exclude
    private List<UsersCards> usersCards;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    @ToString.Exclude
    private Company company;

    @Column(name = "balance")
    private BigDecimal balance;

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
