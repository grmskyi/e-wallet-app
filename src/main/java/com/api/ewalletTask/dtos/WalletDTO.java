package com.api.ewalletTask.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {
    private Long id;
    private String accountName;
    private String accountNumber;
    private String description;
    private String status;
    private Double balance;
}
