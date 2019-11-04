package org.xtext.example.mydsl.tests.group1;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.xtext.example.mydsl.mml.MMLModel;
import org.xtext.example.mydsl.tests.MmlInjectorProvider;

import com.google.inject.Inject;

@ExtendWith(InjectionExtension.class)
@InjectWith(MmlInjectorProvider.class)
public class MmlParsingPersoTest 
{
	@Inject
	ParseHelper<MMLModel> parseHelper;

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
