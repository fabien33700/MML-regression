package org.xtext.example.mydsl.tests.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.core.compilers.RCompiler;
import org.xtext.example.mydsl.tests.core.compilers.ScikitLearnCompiler;
import org.xtext.example.mydsl.tests.core.compilers.TensorFlowCompiler;
import org.xtext.example.mydsl.tests.model.MMLModelFacade;

/**
 * Cette classe de fabrique a pour rôle de trouver automatiquement les instances
 * des compilateurs en fonction de la cible de compilation spécifiée.
 * @author Fabien
 *
 */
public class CompilerFactory {
	
	/**
	 * Représente une cible de compilation du langage MML
	 * @author Fabien
	 *
	 */
	//public static interface MMLTarget extends Compiler<MMLModelFacade> {}
	
	/**
	 * Liste des implémentations de compilateurs disponibles
	 */
	private static final List<Class<? extends AbstractMmlCompiler>> availableTargetTypes = Arrays.asList(
		ScikitLearnCompiler.class,
		RCompiler.class,
		TensorFlowCompiler.class
	);

	/**
	 * Trouve une instance de compilateur en fonction du méta-modèle
	 * @param model Une instance du méta-modèle
	 * @return Une instance du compilateur
	 */
	public static AbstractMmlCompiler findTargetCompiler(MMLModel model) {
		MMLModelFacade modelFacade =  new MMLModelFacade(model);

		for (Class<? extends Compiler<MMLModelFacade>> targetType : availableTargetTypes) {
			try {
				AbstractMmlCompiler compiler = (AbstractMmlCompiler) targetType
						.getConstructor(MMLModelFacade.class)
						.newInstance(modelFacade);
				
				if (compiler.isCompatibleWithModel()) {
					return compiler;
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException ex) {
				throw new IllegalStateException("Could not instanciate the compiler of type " + targetType, ex);
			}
		}
		throw new NoSuchElementException("Could not find a compiler compatible with given model : " + modelFacade.describeTarget());
	}
}
