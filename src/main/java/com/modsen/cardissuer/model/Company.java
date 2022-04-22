package com.modsen.cardissuer.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@RequiredArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "varchar(10) default 'ACTIVE'")
    private String status;


    @OneToMany(mappedBy = "company")
    @ToString.Exclude
    private List<User> users;

    @OneToMany(mappedBy = "company")
    @ToString.Exclude
    private List<Card> cards;

}
