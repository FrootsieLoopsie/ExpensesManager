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
     *      - Devises réelles, qui sont dans le dictionnaire des devises ("rates"), mais qui sont invalides selon la spécification
     *      - Devises invalides qui ne sont pas dans le dictionnaire "rates" (random String).
     *
     * Nous avons aussi les frontières (edge cases) à tester;
     *      - On utilisera une valeur à la borne des spécifications (à la borne d'équivalence), et une valeur typique.
     *      - Pour les devises:
     *          - N'ayant pas de bornes (les valeurs sont connues et finies), nous ne pouvons pas définir de tests frontière pour les devises.
     *      - Pour les montants:
     *          - Montant = 0, 1, -1
     *          - Montant = 10000, 10001, 9999
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
        catch(Exception e) {
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
        System.out.println("[ ASSERTION ERROR ]\nTest for method 'ua.karatnyk.impl.CurrencyConvertor.convert()' failed:\n" + description + "\n    Input values --> Amount: " + amount + ", CurrencyA: '" + currencyA + "', CurrencyB: '" + currencyB + "'");
        throw e;
    }


    @Test
    public void testConvertEquivalence_ValidInputs() {
        // Testing the method to ensure that valid amounts, for any and all valid currencies,
        // there should be NO thrown error, like, at all. This covers all possible cases for currencies.

        // BONUS: Unspoken boon of this test is that, if ever there is a currency with a rate of zero,
        // it would normally cause a division by zero and throw an error, but this test ensures that won't
        // be true for any of the valid inputs by the fact that it cycles through each currency combination.
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
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MAXIMUM + AMOUNT_MID, validCurrencyA, validCurrencyB));;
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Accepted, but the amount was too high, it should have been rejected.", AMOUNT_MAXIMUM + AMOUNT_MID, validCurrencyA, validCurrencyB);
                }
            }
        }
    }

    @Test
    public void testConvertEquivalence_LowAmounts() {
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MINIMUM - AMOUNT_MID, validCurrencyA, validCurrencyB));;
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Accepted, but the amount was too low, it should have been rejected.", AMOUNT_MINIMUM - AMOUNT_MID, validCurrencyA, validCurrencyB);
                }
            }
        }
    }

    @Test
    public void testConvertEquivalence_PlausibleCurrencies() {

        // Extracted from list of circulating currencies:
        String[] invalidButPlausibleCurrencies = new String[]{"CAD","USD","EUR","JPY","CHF","HKD","KRW","RUB","ALL","GBP","ARS","EGP"};

        for (String invalidCurrency : invalidButPlausibleCurrencies) {
            if(isValidCurrency(invalidCurrency)) continue;
            for(String validCurrency : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, invalidCurrency));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e,  "Accepted invalid, but plausible currency '" + invalidCurrency + "', which wasn't in the specifications given.", AMOUNT_MID, validCurrency, invalidCurrency);
                }
            }
        }
    }

    @Test
    public void testConvertEquivalence_RandomStringsAsCurrency() {
        for(String validCurrency : CURRENCIES) {
            try{
                assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "a"));
                assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "aA"));
                assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "aAB"));
                assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "SAXaAB"));
                assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, "xAVAABc"));
            } catch (AssertionError e) {
                printAssertionErrorMessage(e, "Accepted, but the inputted string was just a random jumble instead of a currency signifier.", AMOUNT_MID, validCurrency, "a");
            }
        }
    }


    @Test
    public void testConvertEdges_AmountsMax2() {
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
    public void testConvertEdges_AmountsMax3() {
        for(String validCurrencyA : CURRENCIES) {
            for (String validCurrencyB : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MAXIMUM + 1, validCurrencyA, validCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Accepted, but the amount is just above the acceptable maximum.", AMOUNT_MAXIMUM, validCurrencyA, validCurrencyB);
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
                    printAssertionErrorMessage(e, "Accepted, but the amount is just below the acceptable minimum.", AMOUNT_MINIMUM, validCurrencyA, validCurrencyB);
                }
            }
        }
    }



    /**
     * ==================================================================================================
     *                                          WHITE BOX TESTING
     *                               Testing the implementation of the method.
     * ==================================================================================================
     */
    @Test
    public void testConvert_ThrownException() {

        // Generate a totally random string that isn't in "rates":
        String randomString;
        do randomString = "" + Math.random();
        while(this.conversion.getRates().containsKey(randomString));

        // Get a list of all inputs that should normally generate an exception:
        String[] rateKeys = this.conversion.getRates().keySet().toArray(new String[0]);

        // Test all input values leading to the exception being thrown, and make sure they all indeed throw said exception!
        // If only the first condition of convert()'s if statement is true:
        for(String realCurrency : rateKeys) {
            try {
                assertTrue(doInputsThrowAnException(AMOUNT_MID, randomString, realCurrency));
            } catch (AssertionError e) {
                printAssertionErrorMessage(e, "Accepted, but the first currency doesn't belong in the \"rates\" dictionary.", AMOUNT_MID, randomString, realCurrency);
            }
        }

        // If only the second condition of convert()'s if statement is true:
        for(String realCurrency : rateKeys) {
            try {
                assertTrue(doInputsThrowAnException(AMOUNT_MID, realCurrency, randomString));
            } catch (AssertionError e) {
                printAssertionErrorMessage(e, "Accepted, but the second currency doesn't belong in the \"rates\" dictionary.", AMOUNT_MID, realCurrency, randomString);
            }
        }

        // If both conditions of convert()'s if statement are true:
        try {
            assertTrue(doInputsThrowAnException(AMOUNT_MID, randomString, randomString));
        } catch (AssertionError e) {
            printAssertionErrorMessage(e, "Accepted, but neither currencies belong in the \"rates\" dictionary.", AMOUNT_MID, randomString, randomString);
        }
    }

    @Test
    public void testConvert_ValueReturned() {

        // Get a list of all inputs that should normally generate an exception:
        String[] rateKeys = this.conversion.getRates().keySet().toArray(new String[0]);

        for(String realCurrencyA : rateKeys) {
            for (String realCurrencyB : rateKeys) {
                try {
                    assertTrue(!doInputsThrowAnException(AMOUNT_MID, realCurrencyA, realCurrencyB));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e, "Rejected, but both currencies inputted belong in the \"rates\" dictionary.", AMOUNT_MID, realCurrencyA, realCurrencyB);
                }
            }
        }
    }

    @Test
    public void testConvert_ValueReturned_GivenSpecifications() {
        // If a currency is not in our specification, but is still an existing currency in the
        // "rates" dictionary/hashmap, it should still be rejected:
        for (String invalidCurrency : conversion.getRates().keySet()) {
            if(isValidCurrency(invalidCurrency)) continue;
            for(String validCurrency : CURRENCIES) {
                try {
                    assertTrue(doInputsThrowAnException(AMOUNT_MID, validCurrency, invalidCurrency));
                } catch (AssertionError e) {
                    printAssertionErrorMessage(e,  "Accepted invalid currency '" + invalidCurrency + "', which was in the \"rates\" map, but is considered invalid considering specification.", AMOUNT_MID, validCurrency, invalidCurrency);
                }
            }
        }
    }

}
