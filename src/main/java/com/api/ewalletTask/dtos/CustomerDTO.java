package com.api.ewalletTask.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private Boolean blocked;
    private Date blockedUntil;
    private Duration timeLeftToUnblock;
    private Boolean unblockRequested;
    private List<WalletDTO> wallets;
}
