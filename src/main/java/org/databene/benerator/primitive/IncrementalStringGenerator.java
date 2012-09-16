/*
 * (c) Copyright 2011 by Volker Bergmann. All rights reserved.
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

package org.databene.benerator.primitive;

import java.util.Set;

import org.databene.benerator.Generator;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.util.GeneratorUtil;
import org.databene.benerator.wrapper.GeneratorChain;
import org.databene.commons.ArrayBuilder;

/**
 * Creates Strings in an incremental manner.<br/><br/>
 * Created: 02.08.2011 10:34:08
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class IncrementalStringGenerator extends GeneratorChain<String> implements NonNullGenerator<String> {

	public IncrementalStringGenerator(Set<Character> chars, int minLength, int maxLength, int lengthGranularity) {
		super(String.class, false, createSources(chars, minLength, maxLength, lengthGranularity));
	}
	
	public String generate() {
		return GeneratorUtil.generateNonNull(this);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Generator<? extends String>[] createSources(Set<Character> chars, int minLength, int maxLength, 
			int lengthGranularity) {
		ArrayBuilder<Generator> builder = new ArrayBuilder<Generator>(Generator.class);
		for (int i = minLength; i <= maxLength; i += lengthGranularity)
			builder.add(new UniqueFixedLengthStringGenerator(chars, i, true));
		return builder.toArray();
	}

}
