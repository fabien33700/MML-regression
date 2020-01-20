package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertThat;
import static org.xtext.example.mydsl.tests.TextFileMatcher.containsSameText;

import org.junit.jupiter.api.Test;


public class ScikitLearnCompilerTest extends AbstractCompilerTest {

    @Test
    public void test_NoRformula() throws Exception {
        String source = 
                "datainput \"Boston.csv\" separator ,\n" + 
                "mlframework scikit-learn\n"+ 
                "algorithm DT\n" + 
                "TrainingTest { percentageTraining 70 }\n" + 
                "mean_absolute_error\n" + 
                "";
        
        String expected = getExpectedProgram("scikit/test1.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
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
        
        String expected = getExpectedProgram("scikit/test2.py");

        String result = compile(source);
        assertThat(result, containsSameText(expected));
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
    public void test_GradientBoosting() throws Exception
    {
        String source = generateSourceFromAlgorithm("Boston.csv", "GTB");
        String expected = getExpectedProgram("scikit/GradientBoosting.py");
        
        String result = compile(source);
        assertThat(result, containsSameText(expected));        
    }
    
    @Test
    public void test_RandomForest() throws Exception
    {
        String source = generateSourceFromAlgorithm("Boston.csv", "RandomForest");
        String expected = getExpectedProgram("scikit/RandomForest.py");
        
        String result = compile(source);
        assertThat(result, containsSameText(expected));        
    }
    
    //Test de performances d'algorithme => génération de code
    
    @Test
    public void test_DecisionTree_boston() throws Exception
    {
        String source =  generateSourceForComparativeTest("Boston.csv","DecisionTree");
        String expected = getExpectedProgram("scikit/DecisionTree_Boston.py");
        String result = compile(source);
        assertThat(result, containsSameText(expected));    
    }
    
    @Test
    public void test_GradientBoosting_boston() throws Exception
    {
        String source =  generateSourceForComparativeTest("Boston.csv","GTB");
        String expected = getExpectedProgram("scikit/GradientBoosting_Boston.py");
        String result = compile(source);
        assertThat(result, containsSameText(expected));    
    }
    
    @Test 
    public void test_RandomForest_boston() throws Exception
    {
        String source =  generateSourceForComparativeTest("Boston.csv","RandomForest");
        String expected = getExpectedProgram("scikit/RandomForest_Boston.py");
        String result = compile(source);
        assertThat(result, containsSameText(expected));    
    }
    
    @Test
    public void test_DecisionTree_titanic() throws Exception
    {
        String source =  generateSourceForComparativeTest("titanic.csv","DecisionTree");
        String expected = getExpectedProgram("scikit/DecisionTree_Titanic.py");
        String result = compile(source);
        assertThat(result, containsSameText(expected));    
    }
    
    @Test
    public void test_GradientBoosting_titanic() throws Exception
    {
        String source =  generateSourceForComparativeTest("titanic.csv","GTB");
        String expected = getExpectedProgram("scikit/GradientBoosting_Titanic.py");
        String result = compile(source);
        assertThat(result, containsSameText(expected));    
    }
    
    @Test
    public void test_RandomForest_titanic() throws Exception
    {
        String source =  generateSourceForComparativeTest("titanic.csv","RandomForest");
        String expected = getExpectedProgram("scikit/RandomForest_Titanic.py");
        String result = compile(source);
        assertThat(result, containsSameText(expected));    
    }
    
    //Bout de code souvent réécris
    private String generateSourceFromAlgorithm(String inputFilename, String algorithm)
    {
        String source="datainput \"" + inputFilename + "\"\n" + 
                "mlframework scikit-learn \n" + 
                "algorithm " + algorithm + "\n" +
                "formula \"medv\" ~ .\n" + 
                "CrossValidation { numRepetitionCross 6 } \n" + 
                "mean_squared_error mean_absolute_error"
                + "";
        return source;
    }
    
    private String generateSourceForComparativeTest(String inputFilename, String algorithm)
    {
        String source="datainput \"" + inputFilename + "\"\n" + 
                "mlframework scikit-learn \n" + 
                "algorithm " + algorithm + "\n" ;
        
        if(inputFilename=="titanic.csv")
        {
            source+="formula \"survived\" ~ \"pclass\" + \"age\" + \"sibsp\" + \"parch\" + \"fare\"\n";
        }
        else if (inputFilename=="Boston.csv")
        {
            source+="formula \"medv\" ~ .\n";
        }
        source +="TrainingTest { percentageTraining 70 } \n";
        source +="mean_absolute_percentage_error";
        return source;
    }
}