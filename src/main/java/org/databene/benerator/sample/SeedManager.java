/*
 * (c) Copyright 2009-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.sample;

import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.IllegalGeneratorStateException;
import org.databene.benerator.InvalidGeneratorSetupException;
import org.databene.benerator.util.WrapperProvider;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.BeanUtil;

/**
 * Counts frequencies of atoms and provides random atoms with the same frequency.<br/>
 * <br/>
 * Created at 12.07.2009 07:51:04
 * @since 0.6.0
 * @author Volker Bergmann
 */

public class SeedManager<E> {
	
	private Map<E, SeedManager<E>> successors;
	private int weight;
	private int depth;
	private boolean initialized;
	private AttachedWeightSampleGenerator<E> helper;
	private Class<E> generatedType;
	private WrapperProvider<E> wrapperProvider;
	
	// constructor and properties --------------------------------------------------------------------------------------
	
	public SeedManager(Class<E> generatedType, int depth) {
		this.generatedType = generatedType;
		this.weight = 0;
		this.depth = depth;
	    this.successors = new HashMap<E, SeedManager<E>>();
	    this.wrapperProvider = new WrapperProvider<E>();
    }

    public int getDepth() {
	    return depth;
    }

    public double getWeight() {
	    return weight;
    }

    // functional interface --------------------------------------------------------------------------------------------
    
    public Class<E> getGeneratedType() {
    	return generatedType;
    }
    
	public void addSequence(int startIndex, E... sequence) {
		weight++;
		if (depth > 0)
			getSuccessor(sequence[startIndex]).addSequence(startIndex + 1, sequence);
	}
	
    public void init() {
    	if (initialized)
    		throw new IllegalGeneratorStateException("Already initialized: " + this);
	    if (getWeight() == 0)
	    	throw new InvalidGeneratorSetupException(getClass().getSimpleName() + " is empty");
    	helper = new AttachedWeightSampleGenerator<E>(generatedType);
    	for (Map.Entry<E, SeedManager<E>> entry : successors.entrySet())
    		helper.addSample(entry.getKey(), entry.getValue().getWeight());
	    helper.init(null);
    }
    
	public E randomAtom() {
		if (!initialized)
			init();
		return helper.generate(getWrapper()).unwrap();
	}

	public SeedManager<E> getSuccessor(E atom) {
	    SeedManager<E> result = successors.get(atom);
	    if (result == null) {
	    	result = new SeedManager<E>(generatedType, depth - 1);
	    	successors.put(atom, result);
	    }
	    return result;
    }

    public void printState() {
    	printState("");
    }
    
    public void printState(String indent) {
    	for (Map.Entry<E, SeedManager<E>> entry : successors.entrySet()) {
	        SeedManager<E> successor = entry.getValue();
	        System.out.println(indent + entry.getKey() + '[' + successor.getWeight() + ']');
	        successor.printState("  " + indent);
        }
    }

    private ProductWrapper<E> getWrapper() {
		return wrapperProvider.get();
	}

    // java.lang.Object overrides --------------------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return BeanUtil.toString(this, true);
    }

}
