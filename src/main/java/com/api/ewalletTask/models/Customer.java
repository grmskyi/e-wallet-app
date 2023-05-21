package com.api.ewalletTask.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_table")
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;


    private Boolean blocked;

    @Column(name = "blocked_until")
    private Date blockedUntil;

    @Transient
    @Column(name = "time_left_to_unblock")
    private Duration timeLeftToUnblock;

    @Column(name = "unblock_requested")
    private Boolean unblockRequested;

    @JsonManagedReference
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

}
