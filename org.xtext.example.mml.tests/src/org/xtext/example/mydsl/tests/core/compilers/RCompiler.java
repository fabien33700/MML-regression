package org.xtext.example.mydsl.tests.core.compilers;

import java.util.Arrays;
import java.util.List;

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
		List<MetricEnum> metrics = model.getValidation().getMetrics();
		StratificationEnum stratification = model.getValidation().getStratificationMethod();
		if (algo == AlgorithmEnum.DT) {
			append("library(caret)\n"
					+ "library(e1071)\n"
					+ "library(rpart)\n"
					+ "library(rpart.plot)\n"
					+ "library(dplyr)\n");
		} else System.out.println("Algorithme non pris en charge.");
	}

	@Override
	public void writeDataLoading() {
		append("set.seed(678)\n" + 
				"path <- './titanic.csv'");
		
		DataInputFacade dataInput = model.getDataInput();

		String csvReading = String.format("titanic <-read.csv(path)", dataInput.getFileLocation(),
				dataInput.getCsvSeparator().orElse(","));
		append(csvReading);
	}

	@Override
	public void writeShuffling() {
		append("shuffle_index <- sample(1:nrow(titanic))\n" + 
				"titanic <- titanic[shuffle_index, ]");
	}

	@Override
	public void writeColumnsExtract() {
		
		
	}

	@Override
	public void writeStratification() {
		// TODO Auto-generated method stub
	}

	@Override
	public void writeAlgorithmInvocation() {
		// TODO Auto-generated method stub
	}

	@Override
	public void writeResultPrinting() {
		// TODO Auto-generated method stub
	}


}
