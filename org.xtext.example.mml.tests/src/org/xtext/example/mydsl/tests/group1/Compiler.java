package org.xtext.example.mydsl.tests.group1;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.xtext.example.mydsl.mml.MMLModel;

public class Compiler 
{
	private ParseHelper<MMLModel> parseHelper=new ParseHelper<MMLModel>();
	
	public static void main(String[] args) throws Exception
	{
		Compiler compiler = new Compiler();
		MMLModel result = compiler.parseHelper.parse("datainput \"Boston.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "mean_absolute_error\n"
				+ "");
		System.out.println(result.getAlgorithm().getAlgorithm());
	}

}
