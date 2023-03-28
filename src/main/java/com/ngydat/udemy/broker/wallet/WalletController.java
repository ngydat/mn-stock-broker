package com.ngydat.udemy.broker.wallet;

import com.ngydat.udemy.broker.data.InMemoryAccountStore;
import com.ngydat.udemy.broker.wallet.error.CustomError;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import java.util.Collection;
import java.util.List;

@Controller("/account/wallets")
public record WalletController(InMemoryAccountStore store) {

    public static final List<String> SUPPORTED_FIAT_CURRENCIES = List.of("EUR", "USD", "CHF", "GBP");


    @Get(produces = MediaType.APPLICATION_JSON)
    public Collection<Wallet> get() {
        return store.getWallets(InMemoryAccountStore.ACCOUNT_ID);
    }

    @Post(
            value="/deposit",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public HttpResponse<CustomError> depositFiatMoney(@Body DepositFiatMoney deposit) {
        //Option 1: custom HttpResponse
        if (!SUPPORTED_FIAT_CURRENCIES.contains(deposit.symbol().value())){
            return HttpResponse.badRequest()
                    .body(new CustomError(
                            HttpStatus.BAD_REQUEST.getCode(),
                            "UNSUPPORTED FIAT CURRENCY",
                            String.format("Only %s are supported", SUPPORTED_FIAT_CURRENCIES)
                    ));
        }



        return HttpResponse.ok();
    }

    @Post(
            value="/withdraw",
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON
    )
    public void withdrawFiatMoney(@Body WithdrawFiatMoney withdraw) {
        //Option 2: custom error processing

    }
}
