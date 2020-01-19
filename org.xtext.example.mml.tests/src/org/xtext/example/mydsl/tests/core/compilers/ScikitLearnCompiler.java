package org.xtext.example.mydsl.tests.core.compilers;

import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.xtext.example.mydsl.tests.core.AbstractMmlCompiler;
import org.xtext.example.mydsl.tests.model.MMLModelFacade;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.AlgorithmEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.DataInputFacade;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.FrameworkEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.MetricEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.RFormulaFacade;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.StratificationEnum;

/**
 * Implémentation du compilateur pour la cible Python/Scikit Learn
 * 
 * @author Fabien
 *
 */
public class ScikitLearnCompiler extends AbstractMmlCompiler {

	public ScikitLearnCompiler(MMLModelFacade model) {
		super(model);
	}

	@Override
	public boolean targetFramework(FrameworkEnum framework) {
		return framework == FrameworkEnum.SCIKIT;
	}

	@Override
	public boolean supportAlgorithm(AlgorithmEnum algo) {
		// Scikit supporte tous les algorithmes du méta-modèle
		return Arrays.asList(AlgorithmEnum.ALL).contains(algo);
	}

	@Override
	public boolean supportMetric(MetricEnum metric) {
		// Scikit ne propose pas directement de fonction pour MAPE
		List<MetricEnum> supported = Arrays.asList(MetricEnum.MAE, MetricEnum.MSE);
		return supported.contains(metric);
	}

	@Override
	public void writeImports() {
		append("import pandas as pd");

		AlgorithmEnum algo = model.getTargetInfo().getAlgorithm();
		List<MetricEnum> metrics = model.getValidation().getMetrics();
		StratificationEnum stratification = model.getValidation().getStratificationMethod();

		if (algo == AlgorithmEnum.DT) {
			append("from sklearn.tree import DecisionTreeRegressor");
		} else if (algo == AlgorithmEnum.SVR) {
			append("from sklearn.svm import SVC");
		} else if (algo == AlgorithmEnum.GTB) {
			append("from sklearn.ensemble import GradientBoostingRegressor");
		} else if (algo == AlgorithmEnum.RandomForest) {
			append("from sklearn.ensemble import RandomForestRegressor");
		} else if (algo == AlgorithmEnum.SGD) {
			append("from sklearn.linear_model import SGDRegressor");
		}

		// FIXME Autres algorithmes
		if (stratification == StratificationEnum.TRAINING_TEST) {
			append("from sklearn.model_selection import train_test_split");
		} else if (stratification == StratificationEnum.CROSS_VALIDATION) {
			append("from sklearn.model_selection import cross_validate");
		}

		if (metrics.contains(MetricEnum.MSE)) {
			append("from sklearn.metrics import mean_squared_error");
		}
		if (metrics.contains(MetricEnum.MAE)) {
			append("from sklearn.metrics import mean_absolute_error");
		}

	}

	@Override
	public void writeDataLoading() {
		DataInputFacade dataInput = model.getDataInput();

		String csvReading = String.format("df = pd.read_csv('%s', sep='%s')", dataInput.getFileLocation(),
				dataInput.getCsvSeparator().orElse(","));
		append(csvReading);
	}

	@Override
	public void writeColumnsExtract() {
		append("df.head()");

		RFormulaFacade formula = model.getFormula();
		if (formula.getLiteralPredictive().isPresent()) {
			append("y = df[%s]", formula.getLiteralPredictive().get());
		} else {
			// Par défaut, on sélectionne la dernière colonne
			append("y = df.iloc[:,-1]");
		}

		if (!formula.hasAllPredictors() && !formula.getLiteralPredictors().isEmpty()) {
			append("X = df[[%s]]", join(", ", formula.getLiteralPredictors()));
		} else {
			if (formula.getLiteralPredictive().isPresent()) {
				append("X = df.drop(columns=[%s])", formula.getLiteralPredictive().get());
			} else {
				// Par défaut, on sélectionne toutes les colonnes sauf la dernière
				append("X = df[df.columns[:-1]]");
			}
		}
	}

	@Override
	public void writeStratification() {
		StratificationEnum stratification = model.getValidation().getStratificationMethod();
		if (stratification == StratificationEnum.TRAINING_TEST) {
			append("test_size = %.2f", model.getValidation().getValue() / 100.0f);
			append("X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)");
		}
	}

	@Override
	public void writeAlgorithmInvocation() {
		AlgorithmEnum algo = model.getTargetInfo().getAlgorithm();
		StratificationEnum stratification = model.getValidation().getStratificationMethod();
		Map<String, String> params = new HashMap<>();
		if (algo == AlgorithmEnum.DT) {
			int maxDepth = model.getTargetInfo().getDT_MaxDepth();
			if (maxDepth > 0) {
				params.put("max_depth", valueOf(maxDepth));
			}
			append("clf = DecisionTreeRegressor(%s)", mapToString(", ", "=", params));
		} else if (algo == AlgorithmEnum.SVR) {
			Optional.ofNullable(model.getTargetInfo().getSVR_C())
				.filter(C -> !isBlank(C))
				.ifPresent(C -> params.put("C", C));
			
			Optional.ofNullable(model.getTargetInfo().getSVR_SVMKernel())
				.map(K -> K.getKernel().getLiteral())
				.filter(K -> !isBlank(K))
				.map(this::singleQuote)
				.ifPresent(K -> params.put("kernel", K));
			
			append("clf = SVC(%s)", mapToString(", ", "=", params));
		} else if (algo == AlgorithmEnum.GTB) {
			append("clf = GradientBoostingRegressor()");
		} else if (algo == AlgorithmEnum.RandomForest) {
			append("clf = RandomForestRegressor()");
		} else if (algo == AlgorithmEnum.SGD) {
			append("clf = SGDRegressor()");
		}
		if (stratification == StratificationEnum.TRAINING_TEST) {
			append("clf.fit(X_train, y_train)");
		} else if (stratification == StratificationEnum.CROSS_VALIDATION) {
			//TODO définir le scoring en fonction des métriques
			List<MetricEnum> metrics = model.getValidation().getMetrics();
			append(getScoringFromMetrics(metrics));
			//append("scoring = ['neg_mean_absolute_error','neg_mean_squared_error']");
			append("results = cross_validate(clf, X, y, cv=%d,scoring=scoring)",
					model.getValidation().getValue());
		}
	}
	
	private String getScoringFromMetrics(List<MetricEnum> metrics){
		List<String> scorings = new ArrayList<>();
		
		if(metrics.contains(MetricEnum.MAE)){
			scorings.add("'neg_mean_absolute_error'");
		}
		if(metrics.contains(MetricEnum.MSE)){
			scorings.add("'neg_mean_squared_error'");
		}
		
		return "scoring = [" + String.join(", ", scorings) + "]";
	}

	@Override
	public void writeResultPrinting() {
		List<MetricEnum> metrics = model.getValidation().getMetrics();
		StratificationEnum stratification = model.getValidation().getStratificationMethod();

		if(stratification!=StratificationEnum.CROSS_VALIDATION)
		{
			if (metrics.contains(MetricEnum.MAE)) {
				append("mae_accuracy = mean_absolute_error(y_test, clf.predict(X_test))");
				append("print(\"mean_absolute_error = \" + str(mae_accuracy))");
			}

			if (metrics.contains(MetricEnum.MSE)) {
				append("mse_accuracy = mean_squared_error(y_test, clf.predict(X_test))");
				append("print(\"mean_squared_error = \" + str(mse_accuracy))");
			}
		}
		else
		{
			if (metrics.contains(MetricEnum.MAE)) {
				append("print('mean_absolute_errors = '+str(results['test_neg_mean_absolute_error']))");
			}
			if (metrics.contains(MetricEnum.MAE)) {
				append("print('mean_squared_errors = '+str(results['test_neg_mean_squared_error']))");
			}
		}
	}

}
