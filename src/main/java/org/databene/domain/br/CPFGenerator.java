/*
  * (c) Copyright 2009-2011 by Eric Chaves & Volker Bergmann. All rights reserved.
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

package org.databene.domain.br;

import java.util.ArrayList;
import org.databene.benerator.util.ThreadSafeNonNullGenerator;

import java.util.Random;

/**
 * Generates Brazilian CPF numbers. CPF stands for 'Cadastro de Pessoa Fisica' 
 * and is a tax payer number assigned to an individual person (Pessoa Fisica).
 * @since 0.6.0
 * @author Eric Chaves
 * @author Volker Bergmann
 */
public class CPFGenerator extends ThreadSafeNonNullGenerator<String> {

    /**
     * flag indicating should return CPF in numeric or formatted form.
     * defaults to true
     */
    private boolean formatted;
    private Random random;    

    public CPFGenerator(){
        this(false);
    }

    public CPFGenerator(boolean formatted){
    	this.random = new Random();
    	this.formatted = formatted;
    }

	@Override
	public String generate() {
    	StringBuilder buf = new StringBuilder();
    	ArrayList<Integer> digits = new ArrayList<Integer>();
	
	    for (int i=0; i < 9; i++)
	        digits.add(random.nextInt(9));
	    addDigit(digits);
	    addDigit(digits);
	
	    for(int i=0; i < digits.size(); i++)
	        buf.append(digits.get(i));
	    if (this.formatted) {
	        buf.insert(3, '.');
			buf.insert(7, '.');
			buf.insert(11, '-');
	    }
	    return buf.toString();
    }
    
    public Class<String> getGeneratedType() {
        return String.class;
    }
    
    private void addDigit(ArrayList<Integer> digits){
    	int sum=0;
    	for (int i=0, j=digits.size()+1; i < digits.size(); i++,j-- )
    		sum += digits.get(i) * j;
    	digits.add((sum % 11 < 2) ? 0: 11-(sum % 11));
    }

}
