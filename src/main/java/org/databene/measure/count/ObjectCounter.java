/*
 * (c) Copyright 2006-2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
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

package org.databene.measure.count;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counts objects.<br/><br/>
 * Created: 14.12.2006 18:03:47
 * @author Volker Bergmann
 */
public class ObjectCounter<E> {
	
    private Map<E, AtomicInteger> instances;
    long totalCount;

    public ObjectCounter(int initialCapacity) {
        instances = new HashMap<E, AtomicInteger>(initialCapacity);
        totalCount = 0;
    }

    // interface -------------------------------------------------------------------------------------------------------

    public void count(E instance) {
        AtomicInteger counter = instances.get(instance);
        if (counter == null)
            instances.put(instance, new AtomicInteger(1));
        else
            counter.incrementAndGet();
        totalCount++;
    }

    public Set<E> objectSet() {
        return instances.keySet();
    }

    public int getCount(E instance) {
        AtomicInteger counter = instances.get(instance);
        return (counter != null ? counter.intValue() : 0);
    }

    public double getRelativeCount(E instance) {
        return (double) getCount(instance) / totalCount;
    }

    public double averageCount() {
        return totalCount / instances.size();
    }

    public double totalCount() {
	    return totalCount();
    }
    
    public Map<E, AtomicInteger> getCounts() {
    	return instances;
    }

    public boolean equalDistribution(double tolerance) {
        double average = averageCount();
        Collection<AtomicInteger> counts = instances.values();
        for (AtomicInteger count : counts) {
            if (Math.abs((count.doubleValue() - average) / average) > tolerance)
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("[");
        Iterator<Map.Entry<E, AtomicInteger>> iterator = instances.entrySet().iterator();
        if (iterator.hasNext()) {
            Map.Entry<E, AtomicInteger> entry = iterator.next();
            buffer.append(entry.getKey()).append(':').append(entry.getValue());
        }
        while (iterator.hasNext()) {
            Map.Entry<E, AtomicInteger> entry = iterator.next();
            buffer.append(", ").append(entry.getKey()).append(':').append(entry.getValue());
        }
        buffer.append(']');
        return buffer.toString();
    }

}
