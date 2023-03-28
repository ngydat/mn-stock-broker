package com.ngydat.udemy.broker.wallet.error;

public record CustomError(
        int status,
        String error,
        String message
) {
}
