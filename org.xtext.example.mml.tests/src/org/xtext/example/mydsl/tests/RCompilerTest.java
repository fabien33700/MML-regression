package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertThat;
import static org.xtext.example.mydsl.tests.TextFileMatcher.containsSameText;

import org.junit.jupiter.api.Test;

public class RCompilerTest extends AbstractCompilerTest {
	@Test
	public void test_NoRformula() throws Exception {
		String source = "datainput \"titanic.csv\" separator ,\n" + 
				"mlframework R\n"+
				"algorithm DT\n" + 
				"TrainingTest { percentageTraining 70 }\n" + 
				"accuracy\n" + 
				"";
		String expected = getExpectedProgram("rlang/DecisionTree.r");

		String result = compile(source);
		assertThat(result, containsSameText(expected));
	}
}
