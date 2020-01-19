package org.xtext.example.mydsl.tests.core.compilers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.xtext.example.mydsl.tests.core.AbstractMmlCompiler;
import org.xtext.example.mydsl.tests.model.MMLModelFacade;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.AlgorithmEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.DataInputFacade;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.FrameworkEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.MetricEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.StratificationEnum;

/**
 * Implémentation du compilateur pour la cible R
 */
public class RCompiler extends AbstractMmlCompiler {

	public RCompiler(MMLModelFacade model) {
		super(model);
	}

	@Override
	public boolean targetFramework(FrameworkEnum framework) {
		return framework == FrameworkEnum.R;
	}

	@Override
	public boolean supportAlgorithm(AlgorithmEnum algo) {
		return Arrays.asList(AlgorithmEnum.DT).contains(algo);
	}

	@Override
	public boolean supportMetric(MetricEnum metric) {
		List<MetricEnum> supported = Arrays.asList(MetricEnum.MAE, MetricEnum.MAPE, MetricEnum.ACC);
		return supported.contains(metric);
	}

	@Override
	public void writeImports() {
		
		AlgorithmEnum algo = model.getTargetInfo().getAlgorithm();
//		List<MetricEnum> metrics = model.getValidation().getMetrics();
//		StratificationEnum stratification = model.getValidation().getStratificationMethod();
		if (algo == AlgorithmEnum.DT) {
			append("library(caret)");
			append("library(e1071)");
			append("library(rpart)");
			append("library(rpart.plot)");
			append("library(dplyr)");
		} else System.out.println("Algorithme non pris en charge.");
	}

	@Override
	public void writeDataLoading() {
		DataInputFacade dataInput = model.getDataInput();
		String dataInputName = dataInput.getFileLocation();
		int seed = 678;
		append("set.seed(" + seed + ")");
		append("path <- '" + dataInputName + "'");
		append("titanic <- read.csv(path)");
		append("shuffle_index <- sample(1:nrow(titanic))");
		append("titanic <- titanic[shuffle_index, ]");
	}
	
	@Override
	public void writeStratification() {
		StratificationEnum stratification = model.getValidation().getStratificationMethod();
		if (stratification == StratificationEnum.TRAINING_TEST) {
			float testSize = model.getValidation().getValue() / 100.0f;
			append("create_train_test <- function(data, size = " + testSize + ", train = TRUE) {"); 
			append("n_row = nrow(data)");
			append("total_row = size * n_row");
			append("train_sample <- 1: total_row");
			append("if (train == TRUE) {");
			append("return (data[train_sample, ])");
			append("} else {");
			append("return (data[-train_sample, ])");
			append("}");
			append("}");
			append("data_train <- create_train_test(titanic, " + testSize + ", train = TRUE)");
			append("data_test <- create_train_test(titanic, " + testSize + ", train = FALSE)");
		}
	}

	@Override
	public void writeColumnsExtract() {
		
		if(model.getFormula().getLiteralPredictors().isEmpty()) {
			append("clean_titanic <- titanic");
		}else {
			String keptColumns = model.getFormula().getLiteralPredictors()
					.stream().collect(Collectors.joining(","));
			Optional<String> lastColumn = model.getFormula().getLiteralPredictive();
			if(lastColumn.isEmpty()) {
				throw new UnsupportedOperationException("You must specify a predictor column.");
			}
			keptColumns += "," + lastColumn.get();
			append("clean_titanic <- select(titanic,"+ keptColumns + " )");
		}
		append("mutate(pclass = factor(pclass, levels = c(1, 2, 3), labels = c('Upper', 'Middle', 'Lower')),");
		append("survived = factor(survived, levels = c(0, 1), labels = c('No', 'Yes'))) % > %");
		append("na.omit()");
	}

	@Override
	public void writeAlgorithmInvocation() {
		AlgorithmEnum algo = model.getTargetInfo().getAlgorithm();
		//StratificationEnum stratification = model.getValidation().getStratificationMethod();
		if (algo == AlgorithmEnum.DT) {
			append("fit <- rpart(survived~., data = data_train, method = 'class')");
			append("predict_unseen <-predict(fit, data_test, type = 'class')");
			append("table_mat <- table(data_test$survived, predict_unseen)\n");
			append("accuracy_Test <- sum(diag(table_mat)) / sum(table_mat)");
		}
//		else if(algo == AlgorithmEnum.SVR) {...}
		append("print(paste('Accuracy for test', accuracy_Test))");
	}

	@Override
	public void writeResultPrinting() {
		// TODO Auto-generated method stub
	}
}
