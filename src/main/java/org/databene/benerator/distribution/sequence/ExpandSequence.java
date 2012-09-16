/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.distribution.sequence;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.distribution.Sequence;
import org.databene.benerator.distribution.SequenceManager;
import org.databene.benerator.engine.BeneratorOpts;
import org.databene.benerator.wrapper.WrapperFactory;

import static org.databene.commons.NumberUtil.*;

/**
 * {@link Sequence} implementation that makes use of Benerator's {@link ExpandGeneratorProxy} 
 * for distributing data of unlimited volume in a unique or non-unique manner.<br/>
 * <br/>
 * Created: 13.12.2009 08:59:34
 * @since 0.6.0
 * @author Volker Bergmann
 * @see ExpandGeneratorProxy
 */
public class ExpandSequence extends Sequence {

	private Integer cacheSize;
	private Integer bucketSize;
	private Float duplicationQuota;
	
	// construction ----------------------------------------------------------------------------------------------------
	
    public ExpandSequence() {
	    this(ExpandGeneratorProxy.DEFAULT_DUPLICATION_QUOTA);
    }

    public ExpandSequence(Float duplicationQuota) {
	    this(BeneratorOpts.getCacheSize(), 
	    	 duplicationQuota, 
	    	 ExpandGeneratorProxy.defaultBucketSize(BeneratorOpts.getCacheSize()));
    }

	public ExpandSequence(Integer cacheSize, Integer bucketSize) {
	    this(cacheSize, ExpandGeneratorProxy.DEFAULT_DUPLICATION_QUOTA, bucketSize);
    }

	public ExpandSequence(Integer cacheSize, Float duplicationQuota, Integer bucketSize) {
	    this.cacheSize = cacheSize;
	    this.duplicationQuota = duplicationQuota;
	    this.bucketSize = bucketSize;
    }
	
	// Distribution interface implementation ---------------------------------------------------------------------------

	public <T extends Number> NonNullGenerator<T> createNumberGenerator(
			Class<T> numberType, T min, T max, T granularity, boolean unique) {
		NonNullGenerator<T> source = SequenceManager.STEP_SEQUENCE.createNumberGenerator(numberType, min, max, granularity, unique);
		int cacheSize = cacheSize(min, max, granularity);
		return WrapperFactory.asNonNullGenerator(
				new ExpandGeneratorProxy<T>(source, duplicationQuota(unique), cacheSize, bucketSize(cacheSize)));
	}

	@Override
	public <T> Generator<T> applyTo(Generator<T> source, boolean unique) {
	    int cacheSize = cacheSize();
		return new ExpandGeneratorProxy<T>(source, duplicationQuota(unique), cacheSize, bucketSize(cacheSize));
	}
	
	// helpers ---------------------------------------------------------------------------------------------------------
	
	private float duplicationQuota(boolean unique) {
		if (unique)
			return 0;
		return (duplicationQuota != null ? duplicationQuota : ExpandGeneratorProxy.DEFAULT_DUPLICATION_QUOTA);
    }

	private int bucketSize(int cacheSize) {
		return (bucketSize != null ? bucketSize : ExpandGeneratorProxy.defaultBucketSize(cacheSize));
    }

	private int cacheSize() {
		return (cacheSize != null ? cacheSize : BeneratorOpts.getCacheSize());
    }
	
	private <T extends Number> int cacheSize(T min, T max, T granularity) {
		if (cacheSize != null)
			return cacheSize;
		long volume = volume(toLong(min), toLong(max), toLong(granularity));
		return (int) Math.min(BeneratorOpts.getCacheSize(), volume);
	}

	private static <T extends Number> long volume(long min, long max, long granularity) {
	    return (max - min + granularity - 1) / granularity;
    }
    
}
