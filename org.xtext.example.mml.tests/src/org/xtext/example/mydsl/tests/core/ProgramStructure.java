package org.xtext.example.mydsl.tests.core;

/**
 * Représente la structure d'un programme. Les méthodes décrites ici
 * correspondent aux différentes parties du programme cible qui sera généré par
 * le compilateur. Elles écrivent généralement dans un tampon de sortie. Leur
 * ordre d'appel est important.
 * 
 * @author Fabien
 *
 */
public interface ProgramStructure {
    /**
     * Ecrit les imports nécessaires au programme cible
     */
    default void writeImports() {
    }

    /**
     * Ecrit le code de chargement des données
     */
    default void writeDataLoading() {
    }

    /**
     * 
     */
    default void writeShuffling() {
    }

    /**
     * Ecrit le code de sélections/exclusions des colonnes à traiter
     */
    default void writeColumnsExtract() {
    }

    /**
     * Ecrit le code correspondant à la stratification des données
     */
    default void writeStratification() {
    }

    /**
     * Ecrit le code qui appel l'algorithme de Machine Learning choisi
     */
    default void writeAlgorithmInvocation() {
    }

    /**
     * Ecrit le code qui appel les fonctions de calculs de métriques et qui retourne
     * le résultat
     */
    default void writeResultPrinting() {
    }

    /**
     * Comportement de base pour l'exécution dans l'ordre de toutes les méthodes de
     * générations de code
     */
    default void writeProgram() {
        writeImports();
        writeDataLoading();
        writeShuffling();
        writeColumnsExtract();
        writeStratification();
        writeAlgorithmInvocation();
        writeResultPrinting();
    }

}
