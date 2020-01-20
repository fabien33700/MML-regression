package org.xtext.example.mydsl.tests.model;

import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;
import static org.junit.platform.commons.util.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.mml.AllVariables;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.CSVSeparator;
import org.xtext.example.mydsl.mml.CrossValidation;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.FormulaItem;
import org.xtext.example.mydsl.mml.FrameworkLang;
import org.xtext.example.mydsl.mml.GTB;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MLChoiceAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.mml.PredictorVariables;
import org.xtext.example.mydsl.mml.RFormula;
import org.xtext.example.mydsl.mml.RandomForest;
import org.xtext.example.mydsl.mml.SGD;
import org.xtext.example.mydsl.mml.SVMKernel;
import org.xtext.example.mydsl.mml.SVR;
import org.xtext.example.mydsl.mml.StratificationMethod;
import org.xtext.example.mydsl.mml.TrainingTest;
import org.xtext.example.mydsl.mml.Validation;
import org.xtext.example.mydsl.mml.ValidationMetric;

/**
 * Une façade qui permet de manipuler le méta-modèle de manière simplifiée.
 * Celle-ci contient la logique particulière à la récupération des informations
 * depuis le méta-modèle fourni par EMF et réduit au maximum la complexité
 * induite par la structure Ecore.
 * 
 * Dans certains cas, certains types sont juste des délégations simples pour
 * retirer tout le couplage restant entre le code des compilateurs et le
 * méta-modèle fourni par la plateforme EMF.
 * 
 * Vos compilateurs doivent utiliser EXCLUSIVEMENT cette façade pour récupérer
 * des informations depuis un programme écrit en MML.
 * 
 * @author Fabien
 *
 */
public class MMLModelFacade {

    /**
     * Enumération pour les méthodes de stratification
     * 
     * @author Fabien
     *
     */
    public static enum StratificationEnum {
        CROSS_VALIDATION, TRAINING_TEST;
    }

    /**
     * Enumération pour les algorithmes
     * 
     * @author Fabien
     *
     */
    public static enum AlgorithmEnum {
        DT, GTB, RandomForest, SVR, SGD;

        /**
         * La liste de tous les algorithmes
         */
        public static AlgorithmEnum[] ALL = { DT, GTB, RandomForest, SVR, SGD };
    }

    /**
     * Une énumération pour la liste des frameworks
     * 
     * @author Fabien
     *
     */
    public static enum FrameworkEnum {
        SCIKIT(FrameworkLang.SCIKIT), R(FrameworkLang.R), JAVA_WEKA(FrameworkLang.JAVA_WEKA),
        XG_BOOST(FrameworkLang.XG_BOOST), TENSOR_FLOW(FrameworkLang.TENSOR_FLOW);

        private final FrameworkLang frameworkLang;

        FrameworkEnum(FrameworkLang frameworkLang) {
            this.frameworkLang = frameworkLang;
        }

        public FrameworkLang getFrameworkLang() {
            return frameworkLang;
        }

        public static FrameworkEnum forFramework(FrameworkLang frameworkLang) {
            for (FrameworkEnum en : values()) {
                if (en.getFrameworkLang() == frameworkLang) {
                    return en;
                }
            }
            return null;
        }
    }

    /**
     * Une énumération pour les métriques de validation
     * 
     * @author Fabien
     *
     */
    public static enum MetricEnum {
        MSE(ValidationMetric.MSE), MAE(ValidationMetric.MAE), MAPE(ValidationMetric.MAPE), ACC(ValidationMetric.ACC);

        private final ValidationMetric validationMetric;

        MetricEnum(ValidationMetric validationMetric) {
            this.validationMetric = validationMetric;
        }

        public ValidationMetric getValidationMetric() {
            return validationMetric;
        }

        public static MetricEnum forMetric(ValidationMetric validationMetric) {
            for (MetricEnum en : values()) {
                if (en.getValidationMetric() == validationMetric) {
                    return en;
                }
            }
            return null;
        }
    }

    /**
     * Une énumération pour les kernels pour l'algorithme SVR
     * 
     * @author Fabien
     *
     */
    public static enum SVMKernelEnum {
        LINEAR(SVMKernel.LINEAR), POLY(SVMKernel.POLY), RBF(SVMKernel.RBF);

        private final SVMKernel kernel;

        SVMKernelEnum(SVMKernel kernel) {
            this.kernel = kernel;
        }

        public SVMKernel getKernel() {
            return kernel;
        }

        public static SVMKernelEnum forKernel(SVMKernel kernel) {
            for (SVMKernelEnum en : values()) {
                if (en.getKernel() == kernel) {
                    return en;
                }
            }
            return null;
        }
    }

    /**
     * Classe interne pour fournir une façade au type DataInput du méta-modèle.
     * 
     * @author Fabien
     *
     */
    public static final class DataInputFacade {
        private final DataInput source;

        DataInputFacade(DataInput source) {
            this.source = requireNonNull(source, "source object");
        }

        /**
         * Le chemin d'accès du fichier d'entrée
         * 
         * @return
         */
        public String getFileLocation() {
            return source.getFilelocation();
        }

        /**
         * Le séparateur CSV du fichier d'entrée
         * 
         * @return
         */
        public Optional<String> getCsvSeparator() {
            return Optional.ofNullable(source.getParsingInstruction()).map(CSVParsingConfiguration::getSep)
                    .map(CSVSeparator::toString);
        }
    }

    /**
     * Classe interne pour fournir une façade au type MLChoiceAlgorithm du
     * méta-modèle. TargetInfo = Framework + Algorithm + hyperparamètres requis par
     * certains algorithmes
     * 
     * @author Fabien
     *
     */
    public static final class TargetInfoFacade {
        private final MLChoiceAlgorithm source;

        TargetInfoFacade(MLChoiceAlgorithm source) {
            this.source = requireNonNull(source, "source object");
        }

        /**
         * Le framework à utiliser
         * 
         * @return
         */
        public FrameworkEnum getFramework() {
            return FrameworkEnum.forFramework(source.getFramework());
        }

        /**
         * La valeur de l'hyper paramètre max_depth dans le cas de l'utilisation de
         * l'algorithm DecisionTree (DT)
         * 
         * @return
         */
        public int getDT_MaxDepth() {
            if (getAlgorithm() == AlgorithmEnum.DT) {
                return ((DT) source.getAlgorithm()).getMax_depth();
            } else {
                throw new UnsupportedOperationException(
                        "Only the DecisionTree algorithm have max_depth hyper parameter");
            }
        }

        /**
         * La valeur de l'hyper paramètre C dans le cas de l'utilisation de l'algorithm
         * SVR
         * 
         * @return
         */
        public String getSVR_C() {
            if (getAlgorithm() == AlgorithmEnum.SVR) {
                return ((SVR) source.getAlgorithm()).getC();
            } else {
                throw new UnsupportedOperationException("Only the SVR algorithm have C hyper parameter");
            }
        }

        /**
         * La valeur de l'hyper paramètre SVMKernel dans le cas de l'utilisation de
         * l'algorithm SVR
         * 
         * @return
         */
        public SVMKernelEnum getSVR_SVMKernel() {
            if (getAlgorithm() == AlgorithmEnum.SVR) {
                SVMKernel kernel = ((SVR) source.getAlgorithm()).getKernel();
                return SVMKernelEnum.forKernel(kernel);
            } else {
                throw new UnsupportedOperationException("Only the SVR algorithm have svm_kernel hyper parameter");
            }
        }

        /**
         * Récupère l'algorithme à utiliser
         * 
         * @return
         */
        public AlgorithmEnum getAlgorithm() {
            MLAlgorithm algoType = source.getAlgorithm();
            if (algoType instanceof DT) {
                return AlgorithmEnum.DT;
            } else if (algoType instanceof GTB) {
                return AlgorithmEnum.GTB;
            } else if (algoType instanceof RandomForest) {
                return AlgorithmEnum.RandomForest;
            } else if (algoType instanceof SVR) {
                return AlgorithmEnum.SVR;
            } else if (algoType instanceof SGD) {
                return AlgorithmEnum.SGD;
            } else {
                return null;
            }
        }
    }

    /**
     * Classe interne pour fournir une façade au type Validation du méta-modèle.
     * 
     * @author Fabien
     *
     */
    public static final class ValidationFacade {
        private final Validation source;

        ValidationFacade(Validation source) {
            this.source = requireNonNull(source, "source object");
        }

        /**
         * La méthode de stratification
         * 
         * @return
         */
        public StratificationEnum getStratificationMethod() {
            StratificationMethod method = source.getStratification();
            if (method instanceof CrossValidation) {
                return StratificationEnum.CROSS_VALIDATION;
            } else if (method instanceof TrainingTest) {
                return StratificationEnum.TRAINING_TEST;
            } else {
                return null;
            }
        }

        /**
         * La valeur associée à la stratification. TrainingTest -> percentageTraining
         * CrossValidation -> numRepetitionCross
         * 
         * @return
         */
        public int getValue() {
            return source.getStratification().getNumber();
        }

        /**
         * La liste des métriques à calculer puis à afficher
         * 
         * @return
         */
        public List<MetricEnum> getMetrics() {
            return source.getMetric().stream().map(MetricEnum::forMetric).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Structure de la façade pour la partie RFormula (sélection des colonnes)
     * 
     * @author Fabien
     *
     */
    public static interface RFormulaFacade {
        /**
         * Retourne la formule de la colonne prédictive sous forme littérale, si elle
         * existe
         * 
         * @return
         */
        Optional<String> getLiteralPredictive();

        /**
         * Retourne la formule des colonnes de prédictions
         * 
         * @return
         */
        List<String> getLiteralPredictors();

        /**
         * Flag de sélection de toutes les colonnes
         * 
         * @return
         */
        boolean hasAllPredictors();
    }

    /**
     * Implémentation pour une section RFormula absente ou vide
     * 
     * @author Fabien
     *
     */
    static final class EmptyRFormulaFacade implements RFormulaFacade {

        @Override
        public Optional<String> getLiteralPredictive() {
            return Optional.empty();
        }

        @Override
        public List<String> getLiteralPredictors() {
            return Collections.emptyList();
        }

        @Override
        public boolean hasAllPredictors() {
            return false;
        }

    }

    /**
     * Implémentation par défaut d'une section RFormula
     * 
     * @author Fabien
     *
     */
    static final class RFormulaFacadeImpl implements RFormulaFacade {
        private final RFormula source;

        RFormulaFacadeImpl(RFormula source) {
            this.source = requireNonNull(source, "source object");
        }

        public Optional<String> getLiteralPredictive() {
            return Optional.of(source).map(org.xtext.example.mydsl.mml.RFormula::getPredictive)
                    .map(MMLModelFacade::printFormula);
        }

        public List<String> getLiteralPredictors() {
            List<String> literals = new ArrayList<>();
            if (source.getPredictors() instanceof PredictorVariables) {
                EList<FormulaItem> formulaItems = ((PredictorVariables) source.getPredictors()).getVars();
                if (formulaItems != null) {
                    for (FormulaItem item : formulaItems) {
                        literals.add(printFormula(item));
                    }
                }
            }
            return literals;
        }

        public boolean hasAllPredictors() {
            return source.getPredictors() instanceof AllVariables;
        }

    }

    /**
     * Convertit une instance de FormulaItem en string représentant le nom/l'index
     * de la colonne
     * 
     * @param formula
     * @return
     */
    static String printFormula(FormulaItem formula) {
        if (!isBlank(formula.getColName())) {
            return "\"" + formula.getColName() + "\"";
        }
        return valueOf(formula.getColumn());
    }

    private final DataInputFacade dataInput;
    private final TargetInfoFacade targetInfo;
    private final RFormulaFacade formula;
    private final ValidationFacade validation;

    /**
     * Constructeur de la façade du méta-modèle MML
     * 
     * @param sourceModel L'instance source du méta-modèle
     */
    public MMLModelFacade(MMLModel sourceModel) {
        requireNonNull(sourceModel, "source model");
        this.dataInput = new DataInputFacade(requireNonNull(sourceModel.getInput(), "data input"));
        this.targetInfo = new TargetInfoFacade(requireNonNull(sourceModel.getAlgorithm(), "algorithm"));
        this.validation = new ValidationFacade(requireNonNull(sourceModel.getValidation(), "validation"));

        if (sourceModel.getFormula() != null) {
            this.formula = new RFormulaFacadeImpl(sourceModel.getFormula());
        } else {
            this.formula = new EmptyRFormulaFacade();
        }
    }

    /**
     * Récupère la façade de la section DataInput
     * 
     * @return
     */
    public DataInputFacade getDataInput() {
        return dataInput;
    }

    /**
     * Récupère la façade de la section TargetInfo (correspond à MLChoiceAlgorithm)
     * 
     * @return
     */
    public TargetInfoFacade getTargetInfo() {
        return targetInfo;
    }

    /**
     * Récupère la façade de la section Validation
     * 
     * @return
     */
    public ValidationFacade getValidation() {
        return validation;
    }

    /**
     * Récupère la façade de la section RFormula. Cette section étant faculative,
     * renvoie une instance de EmptyRFormulaFacade dans ce cas.
     * 
     * @return
     */
    public RFormulaFacade getFormula() {
        return formula;
    }

    /**
     * Description de la cible de compilation. Mentionne le framework, l'algorithme,
     * et les métriques à utiliser dans le programme
     * 
     * @return
     */
    public String describeTarget() {
        String metricsList = validation.getMetrics().stream().map(MetricEnum::getValidationMetric)
                .map(ValidationMetric::getLiteral).collect(Collectors.joining(", "));

        return String.format("framework: %s, algorithm: %s, metrics: {%s}", targetInfo.getFramework(),
                targetInfo.getAlgorithm(), metricsList);
    }
}
