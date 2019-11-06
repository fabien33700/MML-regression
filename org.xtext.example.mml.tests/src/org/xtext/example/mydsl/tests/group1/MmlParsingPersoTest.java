package org.xtext.example.mydsl.tests.group1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DT;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.MLAlgorithm;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingPersoTest 
{
	@Inject
	ParseHelper<MMLModel> parseHelper;
	private static String DEFAULT_COLUMN_SEPARATOR = ",";

	@Test
	public void simpleTest() throws Exception 
	{
		String pythonCode="";
		MMLModel result = parseHelper.parse(
				"datainput \"Boston.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT 2\n"
				+ "TrainingTest { percentageTraining 30 }\n"
				+ "mean_absolute_error\n"
				);
		DataInput dataInput = result.getInput();
		String fileLocation = dataInput.getFilelocation();
		MLAlgorithm algoML = result.getAlgorithm().getAlgorithm();
		String algo;
		String options="";
		if (algoML instanceof DT) 
		{
			algo="DecisionTreeRegressor";
			if(((DT)algoML).getMax_depth()!=0)
			{
				options="max_depth = "+((DT)algoML).getMax_depth();
			}
		}
		else
		{
			algo="CaVaPlanter";
		}
		
		String metric=result.getValidation().getMetric().get(0).getLiteral();
		String pythonImportPy = 
				"import pandas as pd\n"+
				"from sklearn.model_selection import train_test_split\n"+
				"from sklearn import tree \n"+
				"from sklearn.metrics import "+metric+"\n";//TODO j'en suis là
		pythonCode+=pythonImportPy;
		String csvReadingPy = csvReading(fileLocation,dataInput.getParsingInstruction());
		pythonCode+=csvReadingPy;
		
		//Split du tableau
		String splitPy =
				"X = mml_data.drop(columns=[\"mdev\"])\n"+
				"Y = mml_data['mdev']\n";
		pythonCode+=splitPy;
		
		String trainingSetPy = 
				"test_size="+result.getValidation().getStratification().getNumber()/100.0+"\n"+
				"X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)\n";
		pythonCode+=trainingSetPy;
		
		String algorithmPy = 
				"clf tree."+algo+"()\n";
		pythonCode+=algorithmPy;
		String endPy=
				"clf.fit(X_train, y_train)\n"+
				algo+"("+options+")\n"+
				"metric="+metric+"(y_test, clf.predict(X_test))\n"+
				"print(metric)";
		pythonCode+=endPy;
		
		Files.write(pythonCode.getBytes(), new File("test1.py"));
		/*
		 * Calling generated Python script (basic solution through systems call)
		 * we assume that "python" is in the path
		 */
		Process p = Runtime.getRuntime().exec("python test1.py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line; 
		while ((line = in.readLine()) != null) 
		{
			System.out.println(line);
	    }
		Assertions.fail("Test pas fini");
	}
	
	private String csvReading(String fileLocation,CSVParsingConfiguration parsingInstruction)
	{
		String csv_separator = DEFAULT_COLUMN_SEPARATOR;
		if(parsingInstruction!=null)
		{
			System.err.println("parsing instruction..."+parsingInstruction);
			csv_separator=parsingInstruction.getSep().toString();
		}
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep=" + mkValueInSingleQuote(csv_separator) + ")\n";
		return csvReading;
	}
	
	private String mkValueInSingleQuote(String val) 
	{
		return "'" + val + "'";
	}
}