package ua.frootloop;

import org.junit.Before;
import org.junit.Test;
import ua.karatnyk.impl.CurrencyConversion;
import ua.karatnyk.impl.CurrencyConvertor;
import ua.karatnyk.impl.ExpensesProgramAPI;
import ua.karatnyk.impl.OfflineJsonWorker;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class CurrencyConvertorTest {
    private CurrencyConversion conversion;

    @Before
    public void init() {
        conversion  = new OfflineJsonWorker().parser();
    }

    /**
     * ==================================================================================================
     *                                          BLACK BOX TESTING
     *                               Testing the functionality of the method.
     * ==================================================================================================
     *
     * Selon nos spécifications, la méthode 'CurrencyConvertor.convert()' devrait;
     *    - Seulement accepter les devises : USD, CAD, GBP, EUR, CHF, INR, AUD
     *    - Seulement accepter les montants [0, 10000]
     *
     * Nous avons donc les classes d'équivalences d'inputs suivantes à tester;
     *      - Montants valides
     *      - Montants < 0
     *      - Montants > 10000
     *      - Devises valides, i.e. elements de {USD, CAD, GBP, EUR, CHF, INR, AUD}
     *      - Devises invalides qui sont dans le dictionnaire "rates" (voir le JSON).
     *      - Devises invalides qui ne sont pas dans le dictionnaire "rates" (String random).
     *
     * Nous avons aussi les frontières (edge cases) à tester;
     *      - On utilisera une valeur à la borne des spécifications (à la borne d'équivalence), et une valeur typique.
     *      - Pour les devises:
     *          - Modifier un des chars aléatoirement des devises
     *      - Pour les montants:
     *          - Montant = 0
     *          - Montant = 10000
     */
    final static double AMOUNT_MINIMUM = 0.0d, AMOUNT_MAXIMUM = 10000.0d,
                        AMOUNT_MID = (AMOUNT_MAXIMUM - AMOUNT_MINIMUM)/2.0d,
                        AMOUNT_THIRD = (AMOUNT_MAXIMUM - AMOUNT_MINIMUM)/3.0d,
                        AMOUNT_QUARTER = (AMOUNT_MAXIMUM - AMOUNT_MINIMUM)/4.0d;
    final static String[] CURRENCIES = new String[]{"USD", "CAD", "GBP", "EUR", "CHF", "INR", "AUD"};

    private boolean doInputsThrowAnException(double amount, String currencyA, String currencyB) {
        try {
            CurrencyConvertor.convert(amount, currencyA, currencyB, this.conversion);
        }
        catch(ParseException e) {
            return true;
        }
        return false;
    }

    private boolean isValidCurrency(String currency) {
        if(currency.length() != 3) return false;
        for(String validCurrency : CURRENCIES)
            if(currency.equals(validCurrency)) return true;
        return false;
    }

    private static void printAssertionErrorMessage(AssertionError e, String description, double amount, String currencyA, String currencyB) {
        System.out.println("[ ASSERTION ERROR ]\nTest for method 'ua.karatnyk.impl.CurrencyConvertor.convert()' failed:\n" + description + ".\n    Input values --> Amount: " + amount + ", CurrencyA: '" + currencyA + "', CurrencyB: '" + currencyB + "'");
        throw e;
    }


    @Test
    public void testConvertEquivalence_ValidInputs() {
        // Testing the method to ensure that valid amounts, for any and all valid currencies,
        // there should be NO thrown error, like, at all. This covers all possible cases for currencies.
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(!doInputsThrowAnException(AMOUNT_MID, validCurrencyA, validCurrencyB));
                    assertTrue(!doInputsThrowAnException(AMOUNT_THIRD, validCurrencyA, validCurrencyB));
                    assertTrue(!doInputsThrowAnException(AMOUNT_QUARTER, validCurrencyA, validCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Rejected, but both the currencies and the amount were valid.", AMOUNT_MID, validCurrencyA, validCurrencyB);
                }
            }
        }
    }

    @Test
    public void testConvertEquivalence_HighAmounts() {
        // For any valid currency, if the amount is too high, it should throw an error:
        for(String validCurrencyA : CURRENCIES)
            for(String validCurrencyB : CURRENCIES)
                assertTrue(doInputsThrowAnException(AMOUNT_MAXIMUM + AMOUNT_MID, validCurrencyA, validCurrencyB));
    }

    @Test
    public void testConvertEquivalence_LowAmounts() {
        // For any valid currency, if the amount is too low, it should throw an error:
        for(String validCurrencyA : CURRENCIES)
            for(String validCurrencyB : CURRENCIES)
                assertTrue(doInputsThrowAnException(AMOUNT_MINIMUM - AMOUNT_MID, validCurrencyA, validCurrencyB));
    }

    @Test
    public void testConvertEquivalence_InvalidCurrencies() {

        // If a currency is not in our specification, but is still an existing currency in the
        // "rates" dictionary/hashmap, it should still be rejected:
        for (String invalidCurrency : conversion.getRates().keySet()) {
            if(isValidCurrency(invalidCurrency)) continue;
            for(String validCurrency : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, invalidCurrency));
                } catch (AssertionError e) {
                    System.out.println("[ ASSERTION ERROR ]\n" +
                            "Test for method 'ua.karatnyk.impl.CurrencyConvertor.convert()' failed:\n" +
                            "Accepted invalid currency '" + invalidCurrency + "', which was in the 'rates' map.\n" +
                            "    Input values --> Amount: " + AMOUNT_MID + ", CurrencyA: '" + validCurrency + "', CurrencyB: '" + invalidCurrency);
                    throw e;
                }
            }
        }
    }

    @Test
    public void testConvertEquivalence_RandomStringsAsCurrency() {

        // The method should reject these systematically, since they are not actual currency signifiers:
        for(String validCurrency : CURRENCIES) {
            assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "a"));
            assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "aA"));
            assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "aAB"));
            assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "SAXaAB"));
            assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "xAVAABc"));
        }
    }

    @Test
    public void testConvertEdges_AmountsMax1() {
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(!doInputsThrowAnException(AMOUNT_MAXIMUM, validCurrencyA, validCurrencyB));
                    assertTrue(!doInputsThrowAnException(AMOUNT_MAXIMUM - 1, validCurrencyA, validCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Rejected, but the amount is just barely acceptable, and should have passed.", AMOUNT_MAXIMUM, validCurrencyA, validCurrencyB);
                }
            }
        }
    }
    @Test
    public void testConvertEdges_AmountsMax2() {
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MAXIMUM + 1, validCurrencyA, validCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Accepted, but the amount is just above the acceptable limit.", AMOUNT_MAXIMUM, validCurrencyA, validCurrencyB);
                }
            }
        }
    }

    @Test
    public void testConvertEdges_AmountsMin1() {
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(!doInputsThrowAnException(AMOUNT_MINIMUM, validCurrencyA, validCurrencyB));
                    assertTrue(!doInputsThrowAnException(AMOUNT_MINIMUM + 1, validCurrencyA, validCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Rejected, but the amount is just barely acceptable, and should have passed.", AMOUNT_MINIMUM, validCurrencyA, validCurrencyB);
                }
            }
        }
    }

    @Test
    public void testConvertEdges_AmountsMin2() {
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MINIMUM - 1, validCurrencyA, validCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Accepted, but the amount is just below the acceptable limit.", AMOUNT_MINIMUM, validCurrencyA, validCurrencyB);
                }
            }
        }
    }



    /**
     * ==================================================================================================
     *                                          WHITE BOX TESTING
     *                               Testing the implementation of the method.
     * ==================================================================================================
     *
     * La fonction se base sur un objet de la classe "ua.karatnyk.impl.CurrencyConversion";
     *      - Les paramètres "base", "date" et "rates" sont établis en fonction d'un fichier JSON.
     *
     * A. Critère de couverture des instructions
     *      -
     *
     * B. Critère de couverture des arcs du graphe de flot de contrôle
     *
     * C. Critère de couverture des chemins indépendants du graphe de flot de contrôle
     *
     * D. Critère de couverture des conditions
     *
     * E. Critère de couverture des i-chemins
     *
     */

}
