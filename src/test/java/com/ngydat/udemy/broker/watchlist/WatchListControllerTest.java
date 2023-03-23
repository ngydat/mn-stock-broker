package com.ngydat.udemy.broker.watchlist;

import com.fasterxml.jackson.databind.JsonNode;
import com.ngydat.udemy.broker.Symbol;
import com.ngydat.udemy.broker.data.InMemoryAccountStore;
import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/account/watchlist")
    HttpClient client;

    @Inject
    InMemoryAccountStore inMemoryAccountStore;

    @BeforeEach
    void setup() {
        inMemoryAccountStore.deleteWatchList(TEST_ACCOUNT_ID);
    }

    @Test
    void returnsEmptyWatchListForTestAccount() {
        final WatchList result = client.toBlocking().retrieve(HttpRequest.GET("/"), WatchList.class);
        assertNull(result.symbols());
        assertTrue(inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols().isEmpty());
    }

    @Test
    void returnsWatchListForTestAccount() {
        inMemoryAccountStore.updateWatchList(TEST_ACCOUNT_ID, new WatchList(
                Stream.of("AAPL", "GOOGL", "MSFT")
                        .map(Symbol::new)
                        .toList()
        ));

        var response = client.toBlocking().exchange("/", JsonNode.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("""
                {
                    "symbols" : [ {
                        "value" : "AAPL"
                     }, {
                        "value" : "GOOGL"
                     }, {
                        "value" : "MSFT"
                     } ]
                }
                """, response.getBody().get().toPrettyString());
    }

    @Test
    void canUpdateWatchListForTestAccount() {
        var symbols = Stream.of("AAPL", "GOOGL", "MSFT").map(Symbol::new).toList();
        final var request = HttpRequest.PUT("/", new WatchList(symbols))
                .accept(MediaType.APPLICATION_JSON);
        var added = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(symbols, inMemoryAccountStore.getWatchList(TEST_ACCOUNT_ID).symbols());
    }

}
