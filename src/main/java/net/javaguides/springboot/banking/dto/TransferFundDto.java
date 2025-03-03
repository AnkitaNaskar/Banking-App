package net.javaguides.springboot.banking.dto;

public record TransferFundDto(Long fromAccountId,
        Long toAccountId,
        double amount) {
}


