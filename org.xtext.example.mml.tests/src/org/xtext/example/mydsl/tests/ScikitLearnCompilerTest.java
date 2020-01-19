package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertThat;
import static org.xtext.example.mydsl.tests.TextFileMatcher.containsSameText;

import org.junit.jupiter.api.Test;

public class ScikitLearnCompilerTest extends AbstractCompilerTest {

    @Test
    public void test_NoRformula() throws Exception {
        String source = 
                "datainput \"Boston.csv\" separator ,\n" + 
                "mlframework scikit-learn\n" + "algorithm DT\n" + 
                "TrainingTest { percentageTraining 70 }\n" + 
                "mean_absolute_error\n" ;

        String expected = getExpectedProgram("scikit/test1.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
    }

    @Test
    public void test_Rformula_predictive_columns() throws Exception {
        String source = 
                "datainput \"Boston.csv\" separator ,\n" + 
                "mlframework scikit-learn\n" + 
                "algorithm DT\n" +
                "formula \"medv\" ~\n" +
                "TrainingTest { percentageTraining 70 }\n" +
                "mean_absolute_error\n";

        String expected = getExpectedProgram("scikit/test2.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
    }

    @Test
    public void test_Rformula_predictors_columns() throws Exception {
        String source = 
                "datainput \"Boston.csv\" separator ,\n" +
                "mlframework scikit-learn\n" +
                "algorithm DT\n" +
                "formula \"medv\" ~ \"crim\" + \"zn\" + \"chas\" + \"rm\"\n"+
                "TrainingTest { percentageTraining 70 }\n" +
                "mean_absolute_error\n";

        String expected = getExpectedProgram("scikit/test3.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));

    }

    @Test
    public void test_DecisionTree() throws Exception {
        String source = generateSourceFromAlgorithm("Boston.csv", "DT");
        String expected = getExpectedProgram("scikit/DecisionTree.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
    }

    @Test
    public void test_GradientBoosting() throws Exception {
        String source = generateSourceFromAlgorithm("Boston.csv", "GTB");
        String expected = getExpectedProgram("scikit/GradientBoosting.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
    }

    @Test
    public void test_RandomForest() throws Exception {
        String source = generateSourceFromAlgorithm("Boston.csv", "RandomForest");
        String expected = getExpectedProgram("scikit/RandomForest.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
    }

    private String generateSourceFromAlgorithm(String inputFilename, String algorithm) {
        String source = 
                "datainput \"" + inputFilename + "\"\n" +
                "mlframework scikit-learn \n" + 
                "algorithm "+ algorithm + "\n" +
                "formula \"medv\" ~ .\n" +
                "CrossValidation { numRepetitionCross 6 } \n"
                + "mean_squared_error mean_absolute_error";
        return source;
    }
}