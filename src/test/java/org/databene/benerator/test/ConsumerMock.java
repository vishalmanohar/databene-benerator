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

package org.databene.benerator.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.databene.benerator.Consumer;
import org.databene.benerator.consumer.AbstractConsumer;

/**
 * Mock implementation of the {@link Consumer} interface to be used for testing.<br/><br/>
 * Created: 11.03.2010 12:51:40
 * @since 0.6.0
 * @author Volker Bergmann
 */
public class ConsumerMock extends AbstractConsumer {
	
	public static final String START_CONSUMING = "sc";
	public static final String FINISH_CONSUMING = "fc";
	public static final String FLUSH = "fl";
	public static final String CLOSE = "cl";

	public static Map<Integer, ConsumerMock> instances = new HashMap<Integer, ConsumerMock>();
	
	private int id;
	private final int minDelay;
	private final int delayDelta;
	
	private final boolean storeProducts;
	public List<Object> products;
	public List<String> invocations;

	public volatile AtomicInteger startConsumingCount = new AtomicInteger();
	public volatile AtomicInteger finishConsumingCount = new AtomicInteger();
	public volatile AtomicInteger flushCount = new AtomicInteger();
	public volatile AtomicInteger closeCount = new AtomicInteger();

	private Random random;
	
	public ConsumerMock(boolean storeProducts) {
	    this(storeProducts, 0, 0, 0);
    }

	public ConsumerMock(boolean storeProducts, int id) {
	    this(storeProducts, id, 0, 0);
    }

	public ConsumerMock(boolean storeProducts, int id, int minDelay, int maxDelay) {
	    this.storeProducts = storeProducts;
	    this.id = id;
	    this.minDelay = minDelay;
	    if (maxDelay > 0) {
	    	this.delayDelta = maxDelay - minDelay;
	    	random = new Random();
	    } else
	    	this.delayDelta = 0;
	    if (storeProducts)
	    	products = new ArrayList<Object>();
	    this.invocations = new ArrayList<String>();
	    instances.put(id, this);
    }
	
	public List<?> getProducts() {
    	return products;
    }

	@Override
	public void startProductConsumption(Object product) {
		invocations.add(START_CONSUMING);
	    startConsumingCount.incrementAndGet();
	    if (storeProducts) {
	    	synchronized (products) {
	            products.add(product);
            }
	    }
	    if (random != null) {
	    	try {
	    		Thread.sleep(minDelay + random.nextInt(delayDelta));
	    	} catch (InterruptedException e) {
	    		// nothing to do
	    	}
	    }
    }

	@Override
	public void finishProductConsumption(Object product) {
		invocations.add(FINISH_CONSUMING);
	    finishConsumingCount.incrementAndGet();
    }

	@Override
	public void flush() {
		invocations.add(FLUSH);
	    flushCount.incrementAndGet();
    }

	@Override
	public void close() {
		invocations.add(CLOSE);
	    closeCount.incrementAndGet();
    }
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + '[' + id + ']';
	}

}
