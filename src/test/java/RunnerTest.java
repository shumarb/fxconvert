import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import model.Currency;
import model.User;
import exceptions.InsufficientAmountForConversionException;
import exceptions.InvalidAmountException;
import exceptions.InvalidCurrencyException;
import exceptions.SameCurrencyException;
import exceptions.UserHasNoCurrencyException;
import exceptions.UserNotFoundException;

class RunnerTest {

    @Mock
    User user1;

    @Mock
    User user2;

    @Mock
    Currency eur;

    @Mock
    Currency gbp;

    @Mock
    Currency jpy;

    @Mock
    Currency aud;

    @BeforeEach
    void setUp() {
        eur = new Currency
                (
                        0.98535489535028,
                        1.0148627714936,
                        "eur",
                        "EUR",
                        "EUR",
                        "978",
                        "Euro",
                        "Tue, 13 Sep 2022 11:55:01 GMT"
                );

        gbp = new Currency
                (
                        0.85438980693642,
                        1.1704259482983,
                        "gbp",
                        "GBP",
                        "GBP",
                        "826",
                        "U.K. Pound Sterling",
                        "Tue, 13 Sep 2022 11:55:01 GMT"
                );

        jpy = new Currency
                (
                        142.32291211472,
                        0.007026275566888,
                        "jpy",
                        "JPY",
                        "JPY",
                        "392",
                        "Japanese Yen",
                        "Tue, 13 Sep 2022 11:55:01 GMT"
                );

        aud = new Currency
                (
                        1.4537833499222,
                        0.68786040234504,
                        "aud",
                        "AUD",
                        "AUD",
                        "036",
                        "Australian Dollar",
                        "Tue, 13 Sep 2022 11:55:01 GMT"
                );

        Runner.users = new ArrayList<> ();
        user1 = new User("Ali");
        user1.addCurrencyToWallet("jpy", 10.0);
        user1.addCurrencyToWallet("aud", 56.4);
        user1.addCurrencyToWallet("eur", 88.0);
        user1.addCurrencyToWallet("gbp", 1331.4);
        Runner.users.add(user1);

        user2 = new User("John");
        user2.addCurrencyToWallet("eur", 88.0);
        user2.addCurrencyToWallet("gbp", 1331.4);
        user2.addCurrencyToWallet("usd", 40);
        Runner.users.add(user2);

        Runner.currencies = new HashMap<> ();
        Runner.currencies.put("eur", eur);
        Runner.currencies.put("gbp", gbp);
        Runner.currencies.put("jpy", jpy);
        Runner.currencies.put("aud", aud);
    }

    @Test
    public void testUserNotFoundExceptionNotThrownForExistingUser() {
        Runner.users.add(user1);
        assertDoesNotThrow(() -> Runner.getsUser("Ali"));
    }

    @Test
    public void testUserNotFoundExceptionThrownForNonExistentUser() {
        assertThrows(UserNotFoundException.class, () -> Runner.getsUser("afa"));
    }

    @Test
    public void testSameCurrencyExceptionThrownForSameCurrencies() {
        assertThrows(SameCurrencyException.class, () -> Runner.isSameCurrency("sgd", "sgd"));
    }

    @Test
    public void testSameCurrencyExceptionNotThrownForDifferentCurrencies() {
        assertDoesNotThrow(() -> Runner.isSameCurrency("eur", "gbp"));
    }

    @Test
    public void testInvalidCurrencyExceptionThrownForNonExistentCurrency() {
        assertThrows(InvalidCurrencyException.class, () -> Runner.isValidCurrency("ppp"));
    }

    @Test
    public void testInvalidCurrencyExceptionNotThrownForExistentCurrency() {
        assertDoesNotThrow(() -> Runner.isValidCurrency("eur"));
    }

    @Test
    public void testInvalidAmountExceptionThrownForInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> Runner.isValidAmount(-10.14));
    }

    @Test
    public void testInvalidAmountExceptionNotThrownForValidAmount() {
        assertDoesNotThrow(() -> Runner.isValidAmount(10.14));
    }


    @Test
    public void testUserHasNoCurrencyExceptionThrownWhenUserDoesNotHaveCurrency() {
        assertThrows(UserHasNoCurrencyException.class, () -> Runner.doesUserHaveCurrency(user1, "sgd"));
    }

    @Test
    public void testUserHasNoCurrencyExceptionNotThrownWhenUserHasCurrency() {
        assertDoesNotThrow(() -> Runner.doesUserHaveCurrency(user1, "eur"));
    }

    @Test
    public void testInsufficientAmountExceptionThrownForInsufficientAmountForConversion() {
        assertThrows(InsufficientAmountForConversionException.class, () -> Runner.isSufficientAmountForConversion(user1, "eur", 100));
    }

    @Test
    public void testInsufficientAmountExceptionNotThrownForSufficientAmountForConversion() {
        assertDoesNotThrow(() -> Runner.isSufficientAmountForConversion(user1, "eur", 5));
    }


    @Test
    public void testConversionOfCurrencyForAllAmountOfNonUsdCurrencyInWalletToAnotherNonUsdCurrencyInWalletTotalCurrencyReducedByOne() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "eur", "gbp", 88.0);
        assertEquals(numberOfCurrenciesBeforeConversion - 1, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForAllAmountOfUsdCurrencyInWalletToAnotherNonUsdCurrencyInWalletTotalCurrencyReducedByOne() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "usd", "gbp", 40.0);
        assertEquals(numberOfCurrenciesBeforeConversion - 1, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForAllAmountOfNonUsdCurrencyInWalletToUsdCurrencyInWalletTotalCurrencyReducedByOne() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "gbp", "usd", 1331.4);
        assertEquals(numberOfCurrenciesBeforeConversion - 1, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForSomeAmountOfNonUsdCurrencyInWalletToAnotherNonUsdCurrencyInWalletTotalCurrencyRemainsTheSame() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "eur", "gbp", 22.5);
        assertEquals(numberOfCurrenciesBeforeConversion, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForSomeAmountOfNonUsdCurrencyInWalletToUsdCurrencyInWalletTotalCurrencyRemainsTheSame() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "eur", "usd", 22.5);
        assertEquals(numberOfCurrenciesBeforeConversion, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForSomeAmountOfUsdInWalletToNonUsdCurrencyInWalletTotalCurrencyRemainsTheSame() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "usd", "eur", 20);
        assertEquals(numberOfCurrenciesBeforeConversion, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForSomeAmountOfNonUsdCurrencyInWalletToNonUsdCurrencyNotInWalletTotalCurrencyIncreasedByOne() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "gbp", "jpy", 22.5);
        assertEquals(numberOfCurrenciesBeforeConversion + 1, user2.getsNumberOfCurrenciesInWallet());
    }

    @Test
    public void testConversionOfCurrencyForSomeAmountOfUsdCurrencyInWalletToNonUsdCurrencyNotInWalletTotalCurrencyIncreasedByOne() throws IOException {
        int numberOfCurrenciesBeforeConversion = user2.getsNumberOfCurrenciesInWallet();
        Runner.currencyConversion(user2, "usd", "jpy", 12);
        assertEquals(numberOfCurrenciesBeforeConversion + 1, user2.getsNumberOfCurrenciesInWallet());
    }

}
