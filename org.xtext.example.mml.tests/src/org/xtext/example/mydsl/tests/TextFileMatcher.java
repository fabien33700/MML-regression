package org.xtext.example.mydsl.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matcher de test pour comparer des textes lignes par lignes
 * 
 * @author Fabien
 *
 */
public class TextFileMatcher extends TypeSafeDiagnosingMatcher<String> {

    /**
     * Le texte attendu
     */
    private final String expectedText;

    /**
     * Flag pour ignorer ou non les lignes vides
     */
    private boolean ignoreEmptyLines;

    /**
     * Constructeur du matcher
     * 
     * @param expectedText Le texte attendu
     */
    private TextFileMatcher(String expectedText, boolean ignoreEmptyLines) {
        super(String.class);
        this.expectedText = expectedText;
        this.ignoreEmptyLines = ignoreEmptyLines;
    }

    /**
     * Génère un matcher de texte
     * 
     * @param expectedValue Le texte attendu
     * @return Une instance du matcher pour le texte attendu
     */
    public static Matcher<String> containsSameText(String expectedValue) {
        return new TextFileMatcher(expectedValue, true);
    }

    /**
     * Génère un matcher de texte qui prend en compte les lignes vides
     * 
     * @param expectedValue Le texte attendu
     * @return Une instance du matcher pour le texte attendu
     */
    public static Matcher<String> containsSameTextWithEmptyLines(String expectedValue) {
        return new TextFileMatcher(expectedValue, false);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("both text should be the same");
    }

    @Override
    protected boolean matchesSafely(String actualText, Description description) {
        // Pas de correspondances si un des deux textes est nul
        if (expectedText == null || actualText == null) {
            return false;
        }

        // On récupère une liste de lignes pour chacun des textes (séparateur de saut de
        // ligne \n ou \r\n)
        List<String> expectedLines = new ArrayList<>(Arrays.asList(expectedText.split("\r?\n")));
        List<String> actualLines = new ArrayList<>(Arrays.asList(actualText.split("\r?\n")));

        // On retire les lignes vides
        if (ignoreEmptyLines) {
            expectedLines.removeIf(line -> line.trim().isEmpty());
            actualLines.removeIf(line -> line.trim().isEmpty());
        }

        // Récupération d'un itérateur pour les lignes de chaque texte,
        // ce qui va nous permettre de parcourir les deux textes ligne par ligne
        // simultanément
        Iterator<String> itExpected = expectedLines.iterator();
        Iterator<String> itActual = actualLines.iterator();

        for (int line = 1; itExpected.hasNext() && itActual.hasNext(); line++) {
            // Lignes actuelles
            String expectedLine = itExpected.next();
            String actualLine = itActual.next();

            // Comparaison en supprimant les espaces de début et de fin de chaînes
            if (!actualLine.trim().equals(expectedLine)) {
                description.appendText("Line ").appendValue(line).appendText(" expected to be <")
                        .appendValue(expectedLine).appendText("> but was:\n <").appendValue(actualLine)
                        .appendText("> (Must be the same, whitespace characters are ignored) ");
                return false;
            }
        }

        // Les deux textes sont identiques
        return true;
    }

}