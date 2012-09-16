/*
 * (c) Copyright 2010 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License (GPL).
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.databene.benerator.distribution;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.distribution.sequence.BitReverseSequence;
import org.databene.benerator.distribution.sequence.CumulatedSequence;
import org.databene.benerator.distribution.sequence.ExpandSequence;
import org.databene.benerator.distribution.sequence.HeadSequence;
import org.databene.benerator.distribution.sequence.RandomSequence;
import org.databene.benerator.distribution.sequence.RandomWalkSequence;
import org.databene.benerator.distribution.sequence.ShuffleSequence;
import org.databene.benerator.distribution.sequence.StepSequence;
import org.databene.benerator.distribution.sequence.WedgeSequence;
import org.databene.commons.ConfigurationError;
import org.databene.domain.math.FibonacciSequence;
import org.databene.domain.math.PadovanSequence;

/**
 * Manages {@link Sequence}s.<br/><br/>
 * Created: 17.02.2010 13:36:17
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class SequenceManager {

    private static Map<String, Sequence> instances = new HashMap<String, Sequence>();

    public static final Sequence RANDOM_SEQUENCE      = register("random", new RandomSequence());
    public static final Sequence SHUFFLE_SEQUENCE     = register("shuffle", new ShuffleSequence());
    public static final Sequence CUMULATED_SEQUENCE   = register("cumulated", new CumulatedSequence());
    public static final Sequence RANDOM_WALK_SEQUENCE = register("randomWalk", new RandomWalkSequence());
    public static final Sequence STEP_SEQUENCE        = register("step", new StepSequence());
    public static final Sequence INCREMENT_SEQUENCE   = register("increment", new StepSequence(BigDecimal.ONE));
    public static final Sequence WEDGE_SEQUENCE       = register("wedge", new WedgeSequence());
    public static final Sequence BIT_REVERSE_SEQUENCE = register("bitreverse", new BitReverseSequence());
    public static final Sequence EXPAND_SEQUENCE      = register("expand", new ExpandSequence());
    public static final Sequence FIBONACCI_SEQUENCE   = register("fibonacci", new FibonacciSequence());
    public static final Sequence PADOVAN_SEQUENCE     = register("padovan", new PadovanSequence());
    public static final Sequence SINGLE_SEQUENCE      = register("head", new HeadSequence());
    
    // Construction & lookup -------------------------------------------------------------------------------------------
    
    public synchronized static Sequence getRegisteredSequence(String name, boolean required) {
        Sequence sequence = instances.get(name);
        if (sequence == null && required)
            throw new ConfigurationError("Sequence not registered: " + name);
        return sequence;
    }

    public synchronized static Sequence register(String name, Sequence sequence) {
    	instances.put(name, sequence);
	    return sequence;
    }

	public synchronized static Collection<Sequence> registeredSequences() {
        return instances.values();
    }
    
}
