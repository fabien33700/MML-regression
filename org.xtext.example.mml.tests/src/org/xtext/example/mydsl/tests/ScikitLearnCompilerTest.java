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
		String source = 
				"datainput \"Boston.csv\" separator ,\n" + 
				"mlframework scikit-learn\n"+ 
				"algorithm DT\n" + 
				"TrainingTest { percentageTraining 70 }\n" + 
				"mean_absolute_error\n" + 
				"";
		
		String expected = 
				"import pandas as pd\n" + 
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
		execute(result,"test1");
	}
	
	@Test
	public void test_Rformula_predictive_columns() throws Exception {
		String source = 
				"datainput \"Boston.csv\" separator ,\n" + 
				"mlframework scikit-learn\n"+ 
				"algorithm DT\n" + 
				"formula \"medv\" ~\n" +
				"TrainingTest { percentageTraining 70 }\n" + 
				"mean_absolute_error\n" + 
				"";
		
		String expected = 
				"import pandas as pd\n" + 
				"from sklearn.tree import DecisionTreeRegressor\n" + 
				"from sklearn.model_selection import train_test_split\n" + 
				"from sklearn.metrics import mean_absolute_error\n" + 
				"df = pd.read_csv('Boston.csv', sep=',')\n" + 
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
		execute(result,"test2");
	}
	
	@Test
	public void test_Rformula_predictors_columns() throws Exception {
		String source = 
				"datainput \"Boston.csv\" separator ,\n" + 
				"mlframework scikit-learn\n"+ 
				"algorithm DT\n" + 
				"formula \"medv\" ~ \"crim\" + \"zn\" + \"chas\" + \"rm\"\n" +
				"TrainingTest { percentageTraining 70 }\n" + 
				"mean_absolute_error\n" + 
				"";
		
		String expected = 
				"import pandas as pd\n" + 
				"from sklearn.tree import DecisionTreeRegressor\n" + 
				"from sklearn.model_selection import train_test_split\n" + 
				"from sklearn.metrics import mean_absolute_error\n" + 
				"df = pd.read_csv('Boston.csv', sep=',')\n" + 
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
		execute(result,"test3");
	}
	
	@Test
	public void test_DescisionTree() throws Exception {
		String source = "datainput \"Boston.csv\"\n" + 
				"mlframework scikit-learn \n" + 
				"algorithm DT\n" +
				"formula \"medv\" ~ .\n" + 
				"CrossValidation { numRepetitionCross 6 } \n" + 
				"mean_squared_error mean_absolute_error"
				+ "";
		String expected=
				"import pandas as pd\n"+
				"from sklearn.tree import DecisionTreeRegressor\n"+
				"from sklearn.model_selection import cross_validate\n"+
				"from sklearn.metrics import mean_squared_error\n"+
				"from sklearn.metrics import mean_absolute_error\n"+
				"df = pd.read_csv('Boston.csv', sep=',')\n"+
				"df.head()\n"+
				"y = df[\"medv\"]\n"+
				"X = df.drop(columns=[\"medv\"])\n"+
				"clf = DecisionTreeRegressor()\n"+
				"scoring = ['neg_mean_absolute_error','neg_mean_squared_error']\n"+
				"results = cross_validate(clf, X, y, cv=6,scoring=scoring)\n" + 
				"print('mean_absolute_errors = '+str(results['test_neg_mean_absolute_error']))\n"+
				"print('mean_squared_errors = '+str(results['test_neg_mean_squared_error']))\n"+
				"print(df)\n"+
				"";
		
		String result = compile(source);
		execute(result,"DescisionTree");
		assertThat(result, containsSameText(expected));
	}
	
	@Test
	public void test_GradientBoosting() throws Exception
	{
		String source = generateSourceFromAlgorithm("GTB");
		String expected =
				"import pandas as pd\n"+
				"from sklearn.ensemble import GradientBoostingRegressor\n"+
				"from sklearn.model_selection import cross_validate\n"+
				"from sklearn.metrics import mean_squared_error\n"+
				"from sklearn.metrics import mean_absolute_error\n"+
				"df = pd.read_csv('Boston.csv', sep=',')\n"+
				"df.head()\n"+
				"y = df[\"medv\"]\n"+
				"X = df.drop(columns=[\"medv\"])\n"+
				"clf = GradientBoostingRegressor()\n"+
				"scoring = ['neg_mean_absolute_error','neg_mean_squared_error']\n"+
				"results = cross_validate(clf, X, y, cv=6,scoring=scoring)\n" + 
				"print('mean_absolute_errors = '+str(results['test_neg_mean_absolute_error']))\n"+
				"print('mean_squared_errors = '+str(results['test_neg_mean_squared_error']))\n"+
				"print(df)\n"+
				"";
		
		String result = compile(source);
		execute(result,"GradientBoosting");
		assertThat(result, containsSameText(expected));		
	}
	
	@Test
	public void test_RandomForest() throws Exception
	{
		String source = generateSourceFromAlgorithm("RandomForest");
		String expected = 
				"import pandas as pd\n"+
				"from sklearn.ensemble import RandomForestRegressor\n"+
				"from sklearn.model_selection import cross_validate\n"+
				"from sklearn.metrics import mean_squared_error\n"+
				"from sklearn.metrics import mean_absolute_error\n"+
				"df = pd.read_csv('Boston.csv', sep=',')\n"+
				"df.head()\n"+
				"y = df[\"medv\"]\n"+
				"X = df.drop(columns=[\"medv\"])\n"+
				"clf = RandomForestRegressor()\n"+
				"scoring = ['neg_mean_absolute_error','neg_mean_squared_error']\n"+
				"results = cross_validate(clf, X, y, cv=6,scoring=scoring)\n" + 
				"print('mean_absolute_errors = '+str(results['test_neg_mean_absolute_error']))\n"+
				"print('mean_squared_errors = '+str(results['test_neg_mean_squared_error']))\n"+
				"print(df)\n"+
				"";
		String result = compile(source);
		execute(result,"RandomForest");
		assertThat(result, containsSameText(expected));		
	}
	
	
	private void execute(String program,String fileName) throws IOException
	{
		Files.write(program.getBytes(), new File(fileName+".py"));
		Process p = Runtime.getRuntime().exec("python " +fileName+".py");
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line; 
		while ((line = in.readLine()) != null) 
		{
			LOGGER.info(line);
	    }
	}
	
	private String generateSourceFromAlgorithm(String algorithm)
	{
		String source="datainput \"Boston.csv\"\n" + 
				"mlframework scikit-learn \n" + 
				"algorithm "+algorithm+"\n" +
				"formula \"medv\" ~ .\n" + 
				"CrossValidation { numRepetitionCross 6 } \n" + 
				"mean_squared_error mean_absolute_error"
				+ "";
		return source;
	}
}