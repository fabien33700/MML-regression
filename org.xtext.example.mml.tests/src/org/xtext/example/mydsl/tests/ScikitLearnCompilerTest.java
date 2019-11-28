package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertThat;
import static org.xtext.example.mydsl.tests.TextFileMatcher.containsSameText;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.core.AbstractMmlCompiler;
import org.xtext.example.mydsl.tests.core.CompilerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;

// TODO Test manquant : CrossValidation
@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class ScikitLearnCompilerTest {
	
	static Logger LOGGER = Logger.getAnonymousLogger();

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	/**
	 * Appel le compilateur approprié au méta-modèle et retourne le code source MML compilé
	 * @param mmlSourceCode Le code source MML
	 * @return Le code compilé à la cible
	 * @throws Exception Une erreur lors de l'analyse de code s'est produite
	 */
	private String compile(String mmlSourceCode) throws Exception {
		MMLModel model = parseHelper.parse(mmlSourceCode);
		AbstractMmlCompiler compiler = CompilerFactory.findTargetCompiler(model);
		compiler.writeProgram();
		return compiler.printCode();
	}

	@Test
	public void test_NoRformula() throws Exception {
		String source = "datainput \"Boston.csv\" separator ,\n" + 
				"mlframework scikit-learn\n"+ 
				"algorithm DT\n" + 
				"TrainingTest { percentageTraining 70 }\n" + 
				"mean_absolute_error\n" + 
				"";
		
		String expected = "import pandas as pd\n" + 
				"from sklearn.tree import DecisionTreeRegressor\n" + 
				"from sklearn.model_selection import train_test_split\n" + 
				"from sklearn.metrics import mean_absolute_error\n" + 
				"df = pd.read_csv('Boston.csv', sep=',')\n" + 
				"df.head()\n" + 
				"y = df.iloc[:,-1]\n" + 
				"X = df[df.columns[:-1]]\n" + 
				"test_size = 0.70\n" + 
				"X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)\n" + 
				"clf = DecisionTreeRegressor()\n" + 
				"clf.fit(X_train, y_train)\n" + 
				"mae_accuracy = mean_absolute_error(y_test, clf.predict(X_test))\n" + 
				"print(\"mean_absolute_error = \" + str(mae_accuracy))\n" + 
				"print(df)\n" +
				"";

		String result = compile(source);
		assertThat(result, containsSameText(expected));
		execute(result);
	}
	
	@Test
	public void test_Rformula_predictive_columns() throws Exception {
		String source = "datainput \"foo2.csv\" separator ,\n" + 
				"mlframework scikit-learn\n"+ 
				"algorithm DT\n" + 
				"formula \"medv\" ~\n" +
				"TrainingTest { percentageTraining 70 }\n" + 
				"mean_absolute_error\n" + 
				"";
		
		String expected = "import pandas as pd\n" + 
				"from sklearn.tree import DecisionTreeRegressor\n" + 
				"from sklearn.model_selection import train_test_split\n" + 
				"from sklearn.metrics import mean_absolute_error\n" + 
				"df = pd.read_csv('foo2.csv', sep=',')\n" + 
				"df.head()\n" + 
				"y = df[\"medv\"]\n" + 
				"X = df.drop(columns=[\"medv\"])\n" + 
				"test_size = 0.70\n" + 
				"X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)\n" + 
				"clf = DecisionTreeRegressor()\n" + 
				"clf.fit(X_train, y_train)\n" + 
				"mae_accuracy = mean_absolute_error(y_test, clf.predict(X_test))\n" + 
				"print(\"mean_absolute_error = \" + str(mae_accuracy))\n" + 
				"print(df)\n" +
				"";

		String result = compile(source);
		assertThat(result, containsSameText(expected));
	}
	
	@Test
	public void test_Rformula_predictors_columns() throws Exception {
		String source = "datainput \"foo2.csv\" separator ,\n" + 
				"mlframework scikit-learn\n"+ 
				"algorithm DT\n" + 
				"formula \"medv\" ~ \"crim\" + \"zn\" + \"chas\" + \"rm\"\n" +
				"TrainingTest { percentageTraining 70 }\n" + 
				"mean_absolute_error\n" + 
				"";
		
		String expected = "import pandas as pd\n" + 
				"from sklearn.tree import DecisionTreeRegressor\n" + 
				"from sklearn.model_selection import train_test_split\n" + 
				"from sklearn.metrics import mean_absolute_error\n" + 
				"df = pd.read_csv('foo2.csv', sep=',')\n" + 
				"df.head()\n" + 
				"y = df[\"medv\"]\n" + 
				"X = df[[\"crim\", \"zn\", \"chas\", \"rm\"]]\n" + 
				"test_size = 0.70\n" + 
				"X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=test_size)\n" + 
				"clf = DecisionTreeRegressor()\n" + 
				"clf.fit(X_train, y_train)\n" + 
				"mae_accuracy = mean_absolute_error(y_test, clf.predict(X_test))\n" + 
				"print(\"mean_absolute_error = \" + str(mae_accuracy))\n" + 
				"print(df)\n" +
				"";

		String result = compile(source);
		assertThat(result, containsSameText(expected));
	}
	
	@Test
	public void test_Rformula_all_predictors_columns() throws Exception {
		String source = "datainput \"foo2.csv\"\n" + 
				"mlframework scikit-learn \n" + 
				"algorithm SVR C=5.0 kernel=linear\n" + 
				"formula \"medv\" ~ .\n" + 
				"CrossValidation { numRepetitionCross 6 } \n" + 
				"mean_squared_error mean_absolute_error"
				+ "";
		// TODO ...
	}
	
	private void execute(String program) throws IOException
	{
		Files.write(program.getBytes(), new File("program.py"));
		Process p = Runtime.getRuntime().exec("python program.py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line; 
		while ((line = in.readLine()) != null) 
		{
			// FIXME utiliser un logger
			System.out.println(line);
	    }
		
	}
}
