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
import java.util.Random;

import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.sample.WeightedCSVSampleGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.Encodings;

/**
 * Generates Brazilian CNPJ numbers.
 * CNPJ stands for <i>Cadastro Nacional da Pessoa Jurídica</i>
 * and is a tax payer number assigned to a 
 * legal person (Pessoa Jurídica).
 * @author Eric Chaves
 * @see "http://en.wikipedia.org/wiki/Cadastro_de_Pessoas_F%C3%ADsicas"
 */
public class CNPJGenerator extends WeightedCSVSampleGenerator<String> implements NonNullGenerator<String> {

    private static final String LOCAL = "/org/databene/domain/br/cnpj_sufix.csv";
	
    /** flag indicating should return CPF in numeric or formatted form. Defaults to true */
	private boolean formatted;
	
	private Random random;

	public CNPJGenerator() {
		this(false);
	}

	public CNPJGenerator(boolean formatted) {
		super(LOCAL, Encodings.UTF_8);
		this.random = new Random();
		this.formatted = formatted;
	}
	
	// Generator interface implementation ------------------------------------------------------------------------------

	public String generate() {
		return generate(getResultWrapper()).unwrap();
	}

	@Override
	public ProductWrapper<String> generate(ProductWrapper<String> wrapper) {
		String suffix = super.generate(wrapper).unwrap();
		if (suffix == null)
			suffix = "0000";
		return wrapper.wrap(generateCNPJ(suffix));
	}

	// private helpers -------------------------------------------------------------------------------------------------
	
	private String generateCNPJ(String sufix) {

		StringBuilder buf = new StringBuilder();
		ArrayList<Integer> digits = new ArrayList<Integer>();
		for (int i = 0; i < 8; i++)
			digits.add(random.nextInt(9));
		for (int i = 0; i < 4; i++)
			digits.add(Integer.parseInt(sufix.substring(i, i + 1)));
		addDigits(digits);

		for (int i = 0; i < digits.size(); i++)
			buf.append(digits.get(i));
		if (this.formatted) {
			buf.insert(2, '.');
			buf.insert(6, '.');
			buf.insert(10, '/');
			buf.insert(15, '-');
		}
		return buf.toString();
	}

	private void addDigits(ArrayList<Integer> digits) {
		int sum = 0;
		sum = (5 * digits.get(0)) + (4 * digits.get(1)) + (3 * digits.get(2)) + (2 * digits.get(3))
		        + (9 * digits.get(4)) + (8 * digits.get(5)) + (7 * digits.get(6)) + (6 * digits.get(7))
		        + (5 * digits.get(8)) + (4 * digits.get(9)) + (3 * digits.get(10)) + (2 * digits.get(11));
		digits.add((sum % 11 < 2) ? 0 : 11 - (sum % 11));

		sum = (6 * digits.get(0)) + (5 * digits.get(1)) + (4 * digits.get(2)) + (3 * digits.get(3))
		        + (2 * digits.get(4)) + (9 * digits.get(5)) + (8 * digits.get(6)) + (7 * digits.get(7))
		        + (6 * digits.get(8)) + (5 * digits.get(9)) + (4 * digits.get(10)) + (3 * digits.get(11))
		        + (2 * digits.get(12));
		digits.add((sum % 11 < 2) ? 0 : 11 - (sum % 11));
	}

}
