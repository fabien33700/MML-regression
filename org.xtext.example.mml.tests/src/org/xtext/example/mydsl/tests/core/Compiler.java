package org.xtext.example.mydsl.tests.core;

import org.xtext.example.mydsl.tests.model.MMLModelFacade.AlgorithmEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.FrameworkEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.MetricEnum;

/**
 * Représente le comportement minimal d'un compilateur, qui cible un langage de
 * programmation associé ) un framework de Machine Learning particuler.
 * L'objectif est de générer un programme correct et fonctionnel à partir du
 * langage MML dans le langage de programmation ciblé.
 * 
 * @author Fabien
 *
 * @param <M> Le type de méta-modèle utilisé par le compilateur pour générer les
 *            programmes
 */
public interface Compiler<M> extends ProgramStructure {
    /**
     * Retourne le méta-modèle donné au compilateur
     * 
     * @return
     */
    M getModel();

    /**
     * Renvoie la chaîne de caractère correspondant au code source généré par le
     * compilateur.
     * 
     * @return
     */
    String printCode();

    /**
     * Indique si ce compilateur cible le framework passé en paramètre.
     * 
     * @param framework Le framework pour lequel vérifier
     * @return true si le compilateur supporte le framework en paramètre, false
     *         sinon
     */
    boolean targetFramework(FrameworkEnum framework);

    /**
     * Indique si le langage et le framework ciblé par le compilateur supporte
     * l'algorithme en paramètre.
     * 
     * @param algo L'algorithme pour lequel vérifier
     * @return true si le framework et le langage de programmation supporte
     *         l'algorithme en paramètre
     */
    boolean supportAlgorithm(AlgorithmEnum algo);

    /**
     * Indique si le langage et le framework ciblé par le compilateur supporte la
     * métrique en paramètre
     * 
     * @param metric La métrique pour laquelle vérifier
     * @return true si le framework et le langage de programmation supporte la
     *         métrique en paramètre
     */
    boolean supportMetric(MetricEnum metric);
}
