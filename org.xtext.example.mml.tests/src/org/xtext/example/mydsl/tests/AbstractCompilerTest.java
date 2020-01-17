package org.xtext.example.mydsl.tests;

import static org.junit.Assert.assertThat;
import static org.xtext.example.mydsl.tests.TextFileMatcher.containsSameText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.core.AbstractMmlCompiler;
import org.xtext.example.mydsl.tests.core.CompilerFactory;

import com.google.common.io.Files;
import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public abstract class AbstractCompilerTest {
	protected static final Logger LOGGER = Logger.getAnonymousLogger();

	@Inject
	protected ParseHelper<MMLModel> parseHelper;

	/**
	 * Appel le compilateur approprié au méta-modèle et retourne le code source MML
	 * compilé
	 * 
	 * @param mmlSourceCode Le code source MML
	 * @return Le code compilé à la cible
	 * @throws Exception Une erreur lors de l'analyse de code s'est produite
	 */
	protected final String compile(String mmlSourceCode) throws Exception {
		MMLModel model = parseHelper.parse(mmlSourceCode);
		AbstractMmlCompiler compiler = CompilerFactory.findTargetCompiler(model);
		compiler.writeProgram();
		return compiler.printCode();
	}

	protected String getExpectedProgram(String expectedProgramFilename) {
		try {
			File file = new File("resources/" + expectedProgramFilename);
			FileReader fr = new FileReader(file);

			try (BufferedReader br = new BufferedReader(fr)) {

				StringBuffer sb = new StringBuffer();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line).append("\n");
				}
				return sb.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void executeProgram(String programFileName) {
		try {
			StringBuilder output = new StringBuilder();
			Process p = Runtime.getRuntime().exec("C:\\Program Files\\Python37\\python.exe " + programFileName,
					new String[0], new File("resources/scikit"));
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			output.append(" === START Execution of " + programFileName + " === \n");
			long timestamp = System.currentTimeMillis();
			while ((line = in.readLine()) != null) {
				output.append(line).append("\n");
			}
			while ((line = err.readLine()) != null) {
				output.append("ERR: ").append(line).append("\n");
			}
			p.waitFor();
			LOGGER.info("exit : " + p.exitValue());
			long execTime = System.currentTimeMillis() - timestamp;
			output.append(" === END Execution of " + programFileName + " in " + execTime + " ms === \n\n");
			LOGGER.info(output.toString());
		} catch (Exception e) {
			LOGGER.severe("An error occurred while executing " + programFileName + " : " + e.toString());
		}
	}
}
