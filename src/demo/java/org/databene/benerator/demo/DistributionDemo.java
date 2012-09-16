package org.databene.benerator.demo;

import static org.databene.benerator.util.GeneratorUtil.*;

import org.databene.benerator.Generator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.distribution.sequence.RandomWalkSequence;
import org.databene.benerator.distribution.sequence.ShuffleSequence;
import org.databene.benerator.distribution.sequence.StepSequence;
import org.databene.benerator.engine.DefaultBeneratorContext;
import org.databene.benerator.factory.StochasticGeneratorFactory;
import org.databene.model.data.Uniqueness;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Demonstrates the built-in Sequences of 'databene generator'.<br/>
 * <br/>
 * Created: 07.09.2006 21:13:33
 * @author Volker Bergmann
 */
public class DistributionDemo {

    /** The number of invocations */
    private static final int N = 128;

    /**
     * Instantiates a frame with a DistributionPane for reach built-in Sequence and usage mode.
     * @see DistributionPane
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("DistributionDemo");
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(2, 4));
        contentPane.setBackground(Color.WHITE);
        contentPane.add(createDistributionPane("random", SequenceManager.RANDOM_SEQUENCE));
        contentPane.add(createDistributionPane("cumulated", SequenceManager.CUMULATED_SEQUENCE));
        contentPane.add(createDistributionPane("randomWalk[0,2]", new RandomWalkSequence(BigDecimal.valueOf(0), BigDecimal.valueOf(2))));
        contentPane.add(createDistributionPane("randomWalk[-1,1]", new RandomWalkSequence(BigDecimal.valueOf(-1), BigDecimal.valueOf(1))));
        contentPane.add(createDistributionPane("step[1]", new StepSequence(BigDecimal.ONE)));
        contentPane.add(createDistributionPane("wedge", SequenceManager.WEDGE_SEQUENCE));
        contentPane.add(createDistributionPane("shuffle", new ShuffleSequence(BigDecimal.valueOf(8))));
        contentPane.add(createDistributionPane("bitreverse", SequenceManager.BIT_REVERSE_SEQUENCE));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static DistributionPane createDistributionPane(String label, Sequence sequence) {
    	Generator<Integer> generator = new StochasticGeneratorFactory().createNumberGenerator(Integer.class, 0, true, N - 1, true, 1, sequence, Uniqueness.NONE);
    	generator.init(new DefaultBeneratorContext());
		return new DistributionPane(label, generator);
    }
    
    /** Pane that displays a title and a visualization of the Sequence's products */
    private static class DistributionPane extends Component {

		private static final long serialVersionUID = -437124282866811738L;

		/** The title to display on top of the pane */
        private String title;

        /** The number generator to use */
        private Generator<Integer> generator;

        /** Initializes the pane's attributes */
        public DistributionPane(String title, Generator<Integer> generator) {
            this.title = title;
            this.generator = generator;
        }

        /** @see Component#paint(java.awt.Graphics) */
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString(title, 0, 10);
            for (int i = 0; i < N; i++) {
                Integer y = generateNonNull(generator);
                if (y != null)
                	g.fillRect(i, 16 + N - y, 2, 2);
            }
        }

        /** Returns the invocation count multiplied by the magnification factor (2) in each dimension */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(N * 2, N * 2);
        }
    }

}
