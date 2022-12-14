package ua.frootloop;

public class CurrencyConvertorTest {

    /**
     * ===========================
     *      BLACK BOX TESTING
     * ===========================
     * Selon nos spécifications, la méthode 'CurrencyConvertor.convert()' devrait;
     *    - Seulement accepter les devises : USD, CAD, GBP, EUR, CHF, INR, AUD
     *    - Seulement accepter les montants [0, 10000]
     *
     * Nous avons donc les classes d'équivalences d'inputs suivantes à tester;
     *      - Montants valides
     *      - Montants < 0
     *      - Montants > 10000
     *      - Devises valides
     *      - Devises incluses dans le dictionnaire "rates", mais non-incluses dans USD, CAD, GBP, EUR, CHF, INR, AUD
     *      - Devises (Strings) qui ne sont pas des devises (non-incluses dans le dictionnaire "rates").
     *
     * Nous avons aussi les frontières (edge cases) à tester;
     *      - On utilisera une valeur à la borne des spécifications (à la borne d'équivalence), et une valeur typique.
     *      - Pour les devises:
     *          - Modifier un des chars aléatoirement des devises
     *      - Pour les montants:
     *          - Montant = 0
     *          - Montant = 10000
     *
     *
     *
     */


}
