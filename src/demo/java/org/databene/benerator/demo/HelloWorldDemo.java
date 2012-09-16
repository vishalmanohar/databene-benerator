package org.databene.benerator.demo;

import java.util.List;

import static org.databene.benerator.util.GeneratorUtil.*;

import org.databene.benerator.engine.BeneratorContext;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.factory.GeneratorFactory;
import org.databene.benerator.Generator;
import org.databene.commons.CollectionUtil;

/**
 * Generates salutations using different salutation words for greeting different persons.
 * @author Volker Bergmann
 */
public class HelloWorldDemo {

    public static void main(String[] args) {
    	// first create a context
    	BeneratorContext context = new DefaultBeneratorContext();
    	
        // create and initialize the salutation generator
    	GeneratorFactory generatorFactory = context.getGeneratorFactory();
    	List<String> salutations = CollectionUtil.toList("Hi", "Hello", "Howdy");
		Generator<String> salutationGenerator = generatorFactory.createSampleGenerator(salutations, String.class, false);
        salutationGenerator.init(context);
        
        // create and initialize the name generator
        List<String> names = CollectionUtil.toList("Alice", "Bob", "Charly");
		Generator<String> nameGenerator = generatorFactory.createSampleGenerator(names, String.class, false);
        init(nameGenerator, context);
        
        // use the generators
        for (int i = 0; i < 5; i++) {
			String salutation = generateNonNull(salutationGenerator);
			String name = generateNonNull(nameGenerator);
			System.out.println(salutation + " " + name);
		}
        
        // in the end, close the generators
        close(salutationGenerator);
        close(nameGenerator);
    }

}
