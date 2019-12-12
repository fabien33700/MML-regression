package org.xtext.example.mydsl.tests.core.compilers;

import java.util.Arrays;
import java.util.List;

import org.xtext.example.mydsl.tests.core.AbstractMmlCompiler;
import org.xtext.example.mydsl.tests.model.MMLModelFacade;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.AlgorithmEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.FrameworkEnum;
import org.xtext.example.mydsl.tests.model.MMLModelFacade.MetricEnum;

public class TensorFlowCompiler extends AbstractMmlCompiler 
{
	public TensorFlowCompiler(MMLModelFacade model) 
	{
		super(model);
	}

	@Override
	public boolean targetFramework(FrameworkEnum framework) {
		return framework == FrameworkEnum.TENSOR_FLOW;
	}

	@Override
	public boolean supportAlgorithm(AlgorithmEnum algo) {
		// FIXME A changer dès qu'un nouvel algo est prêt
		List<AlgorithmEnum> supportedAlgo = Arrays.asList(/*AlgorithmEnum.DT*/);
		return supportedAlgo.contains(algo);
	}

	@Override
	public boolean supportMetric(MetricEnum metric) {
		// FIXME A changer dès qu'une nouvelle metrique est prête
		List<MetricEnum> supportedMetric = Arrays.asList(/*MetricEnum.MAE*/);
		return supportedMetric.contains(metric);
	}

	@Override
	public void writeImports() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeDataLoading() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeColumnsExtract() {
		// TODO Auto-generated method stub
		
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