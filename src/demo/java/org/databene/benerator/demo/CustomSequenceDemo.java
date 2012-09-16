package org.databene.benerator.demo;

import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.factory.StochasticGeneratorFactory;
import static org.databene.benerator.util.GeneratorUtil.*;
import org.databene.benerator.util.UnsafeNonNullGenerator;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.model.data.Uniqueness;

/**
 * Demonstrates definition and use of the custom Sequence 'odd'
 * by an example that generates a sequence of odd numbers:
 * 3, 5, 7, ...<br/>
 * <br/>
 * Created: 13.09.2006 20:27:54
 * @author Volker Bergmann
 */
public class CustomSequenceDemo {

	/** Defines the Sequence 'odd', creates an Integer generator that acceses it and invokes the generator 10 times */
    public static void main(String[] args) {
        Sequence odd = new OddNumberSequence();
        Generator<Integer> generator = new StochasticGeneratorFactory().createNumberGenerator(Integer.class, 3, true, Integer.MAX_VALUE, true, 2, odd, Uniqueness.NONE);
        init(generator);
        for (int i = 0; i < 10; i++)
            System.out.println(generateNonNull(generator));
        close(generator);
    }

    /** The custom Sequence implementation */
    public static class OddNumberSequence extends Sequence {

		public <T extends Number> NonNullGenerator<T> createNumberGenerator(Class<T> numberType, T min, T max, T granularity,
                boolean unique) {
        	OddNumberGenerator doubleGenerator = new OddNumberGenerator(min.doubleValue(), max.doubleValue());
			return WrapperFactory.asNonNullNumberGeneratorOfType(numberType, doubleGenerator, min, granularity);
        }
    }

    public static class OddNumberGenerator extends UnsafeNonNullGenerator<Double> {
    	
    	private double min;
    	private double max;
    	private double granularity;
    	
    	private double next;
    	
		public OddNumberGenerator(double min, double max) {
	        this(min, max, null);
        }

		public OddNumberGenerator(double min, double max, Double granularity) {
	        this.min = min;
	        this.max = max;
	        this.granularity = (granularity != null ? granularity : 2);
	        this.next = min;
        }
		
		// Generator interface implementation --------------------------------------------------------------------------

        public Class<Double> getGeneratedType() {
	        return Double.class;
        }
    	
		@Override
		public Double generate() {
        	if (next >= max)
        		return null;
	        double result = next;
	        next += granularity;
	        return result;
        }

        @Override
        public void reset() {
            next = min;
        }

    }

}
