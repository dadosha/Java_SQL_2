package test;

import data.DataHelper;
import data.SQLHelper;
import org.junit.jupiter.api.*;

import java.util.List;

import static data.DataHelper.*;
import static data.DataHelper.CardInfo.getCorrectCard1;
import static data.DataHelper.CardInfo.getCorrectCard2;
import static data.DataHelper.Card;
import static data.RequestsHelper.*;
import static data.SQLHelper.getCardBalance;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiTransferTests {
    static String authToken;
    Card cardTo;
    Card cardFrom;

    @BeforeAll
    static void setup() {
        authToken = loginUser(getCorrectUserLogInInfo());
    }

    @BeforeEach
    void chooseCards() {
        List<DataHelper.Card> cards = chooseCard(List.of(getCorrectCard1(), getCorrectCard2()));
        if (getCardBalance(cards.get(0).getNumber()) <= 0) {
            cardTo = cards.get(0);
            cardFrom = cards.get(1);
        } else {
            cardTo = cards.get(1);
            cardFrom = cards.get(0);
        }
    }

    @AfterAll
    static void delete() {
        SQLHelper.deleteAll();
    }

    @Test
    @DisplayName("Successful transfer money")
    void shouldSuccessfulTransfer() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = getTransferAmount(cardFromBalance);
        transferMoney(cardFrom.getNumber(), cardTo.getNumber(), transferAmount, authToken);
        assertAll(() -> assertEquals(cardToBalance + transferAmount, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance - transferAmount, getCardBalance(cardFrom.getNumber())));
    }

    @Test
    @DisplayName("Successful transfer all money")
    void shouldSuccessfulTransferAllMoney() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = cardFromBalance;
        transferMoney(cardFrom.getNumber(), cardTo.getNumber(), transferAmount, authToken);
        assertAll(() -> assertEquals(cardToBalance + transferAmount, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance - transferAmount, getCardBalance(cardFrom.getNumber())));
    }

    @Test
    @DisplayName("Unsuccessful transfer more all balance")
    void shouldSuccessfulTransferMoreAllBalance() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = cardFromBalance + 1;
        transferMoneyWithError(cardFrom.getNumber(), cardTo.getNumber(), transferAmount, authToken);
        assertAll(() -> assertEquals(cardToBalance, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance, getCardBalance(cardFrom.getNumber())));
    }

    @Test
    @DisplayName("Unsuccessful transfer zero amount")
    void shouldSuccessfulTransferZeroAmount() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = 0;
        transferMoneyWithError(cardFrom.getNumber(), cardTo.getNumber(), transferAmount, authToken);
        assertAll(() -> assertEquals(cardToBalance, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance, getCardBalance(cardFrom.getNumber())));
    }

    @Test
    @DisplayName("Unsuccessful transfer without from card")
    void shouldSuccessfulTransferWithoutFromCard() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = getTransferAmount(cardFromBalance);
        transferMoneyWithError("", cardTo.getNumber(), transferAmount, authToken);
        assertAll(() -> assertEquals(cardToBalance, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance, getCardBalance(cardFrom.getNumber())));
    }

    @Test
    @DisplayName("Unsuccessful transfer without to card")
    void shouldSuccessfulTransferWithoutToCard() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = getTransferAmount(cardFromBalance);
        transferMoney(cardFrom.getNumber(), "", transferAmount, authToken);
        assertAll(() -> assertEquals(cardToBalance, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance, getCardBalance(cardFrom.getNumber())));
    }

    @Test
    @DisplayName("Unsuccessful transfer without amount")
    void shouldSuccessfulTransferWithoutAmount() {
        int cardToBalance = getCardBalance(cardTo.getNumber());
        int cardFromBalance = getCardBalance(cardFrom.getNumber());
        int transferAmount = cardFromBalance + 1;
        transferMoneyWithoutAmount(cardFrom.getNumber(), cardTo.getNumber(), authToken);
        assertAll(() -> assertEquals(cardToBalance, getCardBalance(cardTo.getNumber())),
            () -> assertEquals(cardFromBalance, getCardBalance(cardFrom.getNumber())));
    }
}
