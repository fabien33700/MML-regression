package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertThat;
import static org.xtext.example.mydsl.tests.TextFileMatcher.containsSameText;

import java.util.logging.Logger;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.core.AbstractMmlCompiler;
import org.xtext.example.mydsl.tests.core.CompilerFactory;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class RCompilerTest {

	static Logger LOGGER = Logger.getAnonymousLogger();

	@Inject
	ParseHelper<MMLModel> parseHelper;
	
	private String compile(String mmlSourceCode) throws Exception {
		MMLModel model = parseHelper.parse(mmlSourceCode);
		AbstractMmlCompiler compiler = CompilerFactory.findTargetCompiler(model);
		compiler.writeProgram();
		return compiler.printCode();
	}
	
	@Test
	public void test_NoRformula() throws Exception {
		String source = "datainput \"titanic.csv\" separator ,\n" + 
				"mlframework R\n"+
				"algorithm DT\n" + 
				"TrainingTest { percentageTraining 70 }\n" + 
				"accuracy\n" + 
				"";
	
		String expected = "library(caret)\n" + 
				"library(e1071)\n" + 
				"library(rpart)\n" + 
				"library(rpart.plot)\n" + 
				"library(dplyr)\n" + 
				
				"set.seed(678)\n" + 
				"path <- './titanic.csv'\n" + 
				"titanic <-read.csv(path)\n" + 
				
				"shuffle_index <- sample(1:nrow(titanic))\n" + 
				"titanic <- titanic[shuffle_index, ]\n" + 
				
				"clean_titanic <- titanic % > %\n" + 
				"select(-c(home.dest, cabin, name, X, ticket)) % > % \n" + 
				"	mutate(pclass = factor(pclass, levels = c(1, 2, 3), labels = c('Upper', 'Middle', 'Lower')),\n" + 
				"	survived = factor(survived, levels = c(0, 1), labels = c('No', 'Yes'))) % > %\n" + 
				"na.omit()\n" +  
				"create_train_test <- function(data, size = 0.7, train = TRUE) {\n" + 
				"    n_row = nrow(data)\n" + 
				"    total_row = size * n_row\n" + 
				"    train_sample <- 1: total_row\n" + 
				"    if (train == TRUE) {\n" + 
				"        return (data[train_sample, ])\n" + 
				"    } else {\n" + 
				"        return (data[-train_sample, ])\n" + 
				"    }\n" + 
				"}\n" + 
				"data_train <- create_train_test(titanic, 0.7, train = TRUE)\n" + 
				"data_test <- create_train_test(titanic, 0.7, train = FALSE)\n" + 
				"fit <- rpart(survived~., data = data_train, method = 'class')\n" + 
				"predict_unseen <-predict(fit, data_test, type = 'class')\n" + 
				"table_mat <- table(data_test$survived, predict_unseen)\n" + 
				"accuracy_Test <- sum(diag(table_mat)) / sum(table_mat)\n" + 
				"print(paste('Accuracy for test', accuracy_Test))";

		String result = compile(source);
		LOGGER.info("Obtained: ---------------------\n" + result+ "\n\n");
		LOGGER.info("Expected: ---------------------\n" + expected);
		assertThat(result, containsSameText(expected));
	}
}
