package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class CurrencyTest {

    @Mock
    Map <String, Currency> currenciesMap;

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

        currenciesMap = new HashMap<> ();

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
    }

    @Test
    public void testCurrenciesMapIsZeroWhenCurrenciesMapIsEmpty() {
        assertEquals(0, currenciesMap.size());
    }

    @Test
    public void testCurrencyListIsNotANonZeroValueWhenCurrenciesMapIsEmpty() {
        assertNotEquals(10, currenciesMap.size());
    }

    @Test
    public void testCurrenciesMapSizeIsCorrectSingleValueAfterInsertingOneCurrency() {
        currenciesMap.put("eur", eur);
        assertEquals(1, currenciesMap.size());
    }

    @Test
    public void testCurrenciesMapSizeIsCorrectMultipleValueAfterInsertingFourCurrencies() {
        currenciesMap.put("eur", eur);
        currenciesMap.put("jpy", eur);
        currenciesMap.put("gbp", eur);
        currenciesMap.put("aud", eur);
        assertEquals(4, currenciesMap.size());
    }

    @Test
    public void testCurrenciesMapReturnsTrueAfterInsertingCurrency() {
        currenciesMap.put("eur", eur);
        assertTrue(currenciesMap.containsKey("eur"));
    }

    @Test
    public void testCurrenciesMapDoesNotReturnFalseAfterInsertingCurrency() {
        currenciesMap.put("eur", eur);
        assertNotEquals(false, currenciesMap.containsKey("eur"));
    }

    @Test
    public void testCorrectAcronymReturnedAfterInsertingCurrency() {
        currenciesMap.put("aud", aud);
        assertEquals("aud", currenciesMap.get("aud").getAcronym());
    }

    @Test
    public void testIncorrectAcronymNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("aud", aud);
        currenciesMap.put("eur", eur);
        assertNotEquals(currenciesMap.get("eur").getAcronym(), currenciesMap.get("aud").getAcronym());
    }

    @Test
    public void testCorrectRateReturnedAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        assertEquals(142.32291211472, currenciesMap.get("jpy").getRate());
    }

    @Test
    public void testIncorrectRateNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.put("aud", aud);
        assertNotEquals(currenciesMap.get("jpy").getRate(), currenciesMap.get("aud").getRate());
    }

    @Test
    public void testCorrectInverseRateReturnedAfterInsertingCurrency() {
        currenciesMap.put("gbp", gbp);
        assertEquals(1.1704259482983, currenciesMap.get("gbp").getInverseRate());
    }

    @Test
    public void testIncorrectInverseRateNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("gbp", gbp);
        currenciesMap.put("aud", aud);
        assertNotEquals(currenciesMap.get("gbp").getInverseRate(), currenciesMap.get("aud").getInverseRate());
    }

    @Test
    public void testCorrectCodeReturnedAfterInsertingCurrency() {
        currenciesMap.put("gbp", gbp);
        assertEquals("GBP", currenciesMap.get("gbp").getCode());
    }

    @Test
    public void testIncorrectCodeNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("gbp", gbp);
        currenciesMap.put("jpy", jpy);
        assertNotEquals(currenciesMap.get("jpy").getCode(), currenciesMap.get("gbp").getCode());
    }

    @Test
    public void testCorrectAlphaCodeReturnedAfterInsertingCurrency() {

        currenciesMap.put("eur", eur);
        assertEquals("EUR", currenciesMap.get("eur").getAlphaCode());
    }

    @Test
    public void testIncorrectAlphaCodeNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("eur", eur);
        currenciesMap.put("aud", aud);
        assertNotEquals(currenciesMap.get("eur").getAlphaCode(), currenciesMap.get("aud").getAlphaCode());
    }

    @Test
    public void testCorrectNumericCodeReturnedAfterInsertingCurrency() {
        currenciesMap.put("eur", eur);
        assertEquals("978", currenciesMap.get("eur").getNumericCode());
    }

    @Test
    public void testIncorrectNumericCodeNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("eur", eur);
        currenciesMap.put("jpy", jpy);
        assertNotEquals(currenciesMap.get("eur").getNumericCode(), currenciesMap.get("jpy").getNumericCode());
    }

    @Test
    public void testCorrectNameReturnedAfterInsertingCurrency() {
        currenciesMap.put("gbp", gbp);
        assertEquals("U.K. Pound Sterling", currenciesMap.get("gbp").getName());
    }

    @Test
    public void testIncorrectNameNotReturnedAfterInsertingCurrency() {
        currenciesMap.put("gbp", gbp);
        currenciesMap.put("jpy", jpy);
        assertNotEquals(currenciesMap.get("jpy").getName(), currenciesMap.get("gbp").getName());
    }

    @Test
    public void testCorrectDateReturnedAfterInsertingCurrency() {
        currenciesMap.put("aud", aud);
        assertEquals("Tue, 13 Sep 2022 11:55:01 GMT", currenciesMap.get("aud").getDate());
    }

    @Test
    public void testAcronymSetCorrectlyAfterInsertingCurrency() {
        currenciesMap.put("aud", aud);
        currenciesMap.get("aud").setAcronym("mmm");
        assertEquals("mmm", currenciesMap.get("aud").getAcronym());
    }

    @Test
    public void testAcronymNotSetIncorrectlyAfterInsertingCurrency() {
        currenciesMap.put("aud", aud);
        currenciesMap.get("aud").setAcronym("gvvkv");
        assertNotEquals("mmm", currenciesMap.get("aud").getAcronym());
    }

    @Test
    public void testCodeSetCorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setCode("ERAFA");
        assertEquals("ERAFA", currenciesMap.get("jpy").getCode());
    }

    @Test
    public void testCodeNotSetIncorrectlyAfterInsertingCurrency() {
        currenciesMap.put("eur", eur);
        currenciesMap.get("eur").setCode("ERAFA");
        assertNotEquals("eur", currenciesMap.get("eur").getCode());
    }

    @Test
    public void testAlphaCodeSetCorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setAlphaCode("araer");
        assertEquals("araer", currenciesMap.get("jpy").getAlphaCode());
    }

    @Test
    public void testAlphaCodeNotSetIncorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setAlphaCode("ERAFA");
        assertNotEquals("jpy", currenciesMap.get("jpy").getAlphaCode());
    }

    @Test
    public void testNumericCodeSetCorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setNumericCode("131");
        assertEquals("131", currenciesMap.get("jpy").getNumericCode());
    }

    @Test
    public void testNumericCodeNotSetIncorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setNumericCode("131");
        assertNotEquals("392", currenciesMap.get("jpy").getNumericCode());
    }

    @Test
    public void testNameSetCorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setName("Singapore Dollar");
        assertEquals("Singapore Dollar", currenciesMap.get("jpy").getName());
    }

    @Test
    public void testNameNotSetIncorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setName("Singapore Dollar");
        assertNotEquals("Japanese Yen", currenciesMap.get("jpy").getName());
    }

    @Test
    public void testDateSetCorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setDate("1 January 2024");
        assertEquals("1 January 2024", currenciesMap.get("jpy").getDate());
    }

    @Test
    public void testDateNotSetIncorrectlyAfterInsertingCurrency() {
        currenciesMap.put("jpy", jpy);
        currenciesMap.get("jpy").setDate("1 January 2024");
        assertNotEquals("Tue, 13 Sep 2022 11:55:01 GMT", currenciesMap.get("jpy").getDate());
    }

}
