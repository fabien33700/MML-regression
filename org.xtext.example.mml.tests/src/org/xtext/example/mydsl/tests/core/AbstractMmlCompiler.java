package org.xtext.example.mydsl.tests.core;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.xtext.example.mydsl.tests.model.MMLModelFacade;

/**
 * Définit la structure de base d'un compilateur.
 * 
 * @author Fabien
 */
public abstract class AbstractMmlCompiler implements Compiler<MMLModelFacade> {

    /**
     * Saut de ligne (Windows)
     */
    public static final String CRLF = "\r\n";

    /**
     * Tampon de sortie du code source
     */
    protected final StringBuilder codeBuilder = new StringBuilder();

    /**
     * Instance du méta-modèle
     */
    protected final MMLModelFacade model;

    /**
     * Ajoute une ligne de code au programme courant.
     * 
     * @param line La ligne de code
     */
    protected void append(String line) {
        codeBuilder.append(requireNonNull(line, "line text")).append(CRLF);
    }

    /**
     * Ajoute une ligne de code au programme courant, en gérant le format.
     * 
     * @param line La ligne de code
     * @param args Les arguments
     */
    protected void append(String format, Object... args) {
        append(String.format(Locale.ENGLISH, requireNonNull(format, "format text"), args));
    }

    /**
     * Ajoute toutes les lignes de code d'un tampon de sortie extérieur au tampon de
     * sortie courant du compilateur.
     * 
     * @param builder L'autre tampon de sortie
     */
    protected void append(StringBuilder builder) {
        codeBuilder.append(requireNonNull(builder, "line builder"));
    }

    /**
     * Vérifie la compatibilité du compilateur actuel avec le méta-modèle donné
     * (framework + algorithme + liste de métriques)
     * 
     * @return true si le compilateur est compatible avec l'instance du méta-modèle
     */
    public final boolean isCompatibleWithModel() {
        return targetFramework(model.getTargetInfo().getFramework())
                && supportAlgorithm(model.getTargetInfo().getAlgorithm())
                && model.getValidation().getMetrics().stream().allMatch(this::supportMetric);
    }

    /**
     * Constructeur de base
     * 
     * @param model L'instance du méta-modèle
     */
    protected AbstractMmlCompiler(MMLModelFacade model) {
        this.model = requireNonNull(model, "model");
    }

    @Override
    public MMLModelFacade getModel() {
        return model;
    }

    @Override
    public String printCode() {
        return codeBuilder.toString();
    }

    /**
     * Met le code entre simple guillemets (')
     * 
     * @param code Le code à modifier
     * @return
     */
    protected String singleQuote(String code) {
        return "'" + code + "'";
    }

    /**
     * Représente une map sous forme de chaîne de caractères
     * 
     * @param tupleDelim    Délimiteur entre les paires
     * @param keyValueDelim Délimiteur entre les clés et les valeurs
     * @param map           La map source
     * @return La représentation de la map
     */
    protected String mapToString(String tupleDelim, String keyValueDelim, Map<?, ?> map) {
        List<String> tuples = new ArrayList<>();
        map.forEach((k, v) -> tuples.add(k.toString() + keyValueDelim + v.toString()));
        return join(tupleDelim, tuples);
    }

    /**
     * Représente une liste sous forme de chaîne de caractères
     * 
     * @param delimiter Le délimiteur entre les éléments de la liste
     * @param elements  La liste source
     * @return La représentation de la liste
     */
    protected String join(String delimiter, Iterable<?> elements) {
        return StreamSupport.stream(elements.spliterator(), false).filter(Objects::nonNull).map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Indique si la chaîne de caractères en paramètre est vide
     * 
     * @param string
     * @return
     */
    protected boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}