package ua.frootloop;

import org.junit.Before;
import org.junit.Test;
import ua.karatnyk.impl.CurrencyConversion;
import ua.karatnyk.impl.ExpensesProgramAPI;

import java.text.ParseException;
import java.util.Map;

public class CurrencyConvertorTest {

    private ExpensesProgramAPI test;
    private CurrencyConversion conversion;
    @Before
    public void init() {
        test = new ExpensesProgramAPI();
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
    final static double AMOUNT_MINIMUM = 0.0d, AMOUNT_MAXIMUM = 10000.0d;
    final static String[] CURRENCIES = new String[]{"USD", "CAD", "GBP", "EUR", "CHF", "INR", "AUD"};

    @Test
    public static void testConvert() {
        Map<String, Double> rates =
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
     *          -
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
