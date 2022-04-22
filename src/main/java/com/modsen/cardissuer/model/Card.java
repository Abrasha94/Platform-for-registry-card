package com.modsen.cardissuer.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "cards")
@Data
@RequiredArgsConstructor
public class Card {

    @Id
    private Long number;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false)
    private Timestamp expirationDate;

    @Column(nullable = false, length = 3)
    private Integer CVVCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

}
