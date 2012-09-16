package org.databene.domain.person;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.test.GeneratorClassTest;
import org.databene.measure.count.ObjectCounter;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * Tests the {@link GivenNameGenerator}.
 * Created: 09.06.2006 21:37:05
 * @since 0.1
 * @author Volker Bergmann
 */
public class GivenNameGeneratorTest extends GeneratorClassTest {

    public GivenNameGeneratorTest() {
        super(GivenNameGenerator.class);
    }

    @Test
    public void test() throws IllegalGeneratorStateException {
        ObjectCounter<String> counter = new ObjectCounter<String>(10);
        GivenNameGenerator generator = new GivenNameGenerator();
        generator.init(context);
        for (int i = 0; i < 10; i++)
            counter.count(generator.generate());
        assertTrue(counter.objectSet().size() >= 3);
    }
    
}
