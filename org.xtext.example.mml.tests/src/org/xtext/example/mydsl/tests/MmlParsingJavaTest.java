package org.xtext.example.mydsl.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.CSVParsingConfiguration;
import org.xtext.example.mydsl.mml.DataInput;
import org.xtext.example.mydsl.mml.MMLModel;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingJavaTest {

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	@Test
	public void loadModel() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"Boston.csv\"\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "mean_absolute_error\n"
				+ "");
		Assertions.assertNotNull(result);
		EList<Resource.Diagnostic> errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors");			
		Assertions.assertEquals("Boston.csv", result.getInput().getFilelocation());			
		
	}		
	
	@Test
	public void compileDataInput() throws Exception {
		MMLModel result = parseHelper.parse("datainput \"Boston.csv\" separator ;\n"
				+ "mlframework scikit-learn\n"
				+ "algorithm DT\n"
				+ "TrainingTest { percentageTraining 70 }\n"
				+ "mean_absolute_error\n"
				+ "");
		DataInput dataInput = result.getInput();
		String fileLocation = dataInput.getFilelocation();
		String pythonImport = "import pandas as pd\n"; 
		String DEFAULT_COLUMN_SEPARATOR = ","; // by default
		String csv_separator = DEFAULT_COLUMN_SEPARATOR;
		CSVParsingConfiguration parsingInstruction = dataInput.getParsingInstruction();
		if (parsingInstruction != null) {			
			System.err.println("parsing instruction..." + parsingInstruction);
			csv_separator = parsingInstruction.getSep().toString();
		}
		String csvReading = "mml_data = pd.read_csv(" + mkValueInSingleQuote(fileLocation) + ", sep=" + mkValueInSingleQuote(csv_separator) + ")";						
		String pandasCode = pythonImport + csvReading;
		
		pandasCode += "\nprint (mml_data)\n"; 
		
		Files.write(pandasCode.getBytes(), new File("mml.py"));
		// end of Python generation
		
		
		/*
		 * Calling generated Python script (basic solution through systems call)
		 * we assume that "python" is in the path
		 */
		Process p = Runtime.getRuntime().exec("python mml.py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line; 
		while ((line = in.readLine()) != null) 
		{
			System.out.println(line);
	    }
	}

	private String mkValueInSingleQuote(String val) 
	{
		return "'" + val + "'";
	}


}
