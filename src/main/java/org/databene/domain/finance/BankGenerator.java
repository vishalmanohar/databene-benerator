/*
 * (c) Copyright 2008-2011 by Volker Bergmann. All rights reserved.
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

package org.databene.domain.finance;

import org.databene.benerator.GeneratorContext;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.primitive.RandomVarLengthStringGenerator;
import org.databene.benerator.primitive.RegexStringGenerator;
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;

/**
 * Generates {@link BankAccount}s with low validity requirements.<br/><br/>
 * Created at 23.06.2008 11:08:48
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class BankGenerator extends CompositeGenerator<Bank> implements NonNullGenerator<Bank> {
	
	private RandomVarLengthStringGenerator bankCodeGenerator;
	private RegexStringGenerator nameGenerator;
	private RegexStringGenerator bicGenerator;
	private RandomVarLengthStringGenerator binGenerator;

	public BankGenerator() {
		super(Bank.class);
		this.bankCodeGenerator = registerComponent(new RandomVarLengthStringGenerator("\\d", 8));
		this.nameGenerator = registerComponent(new RegexStringGenerator("(Deutsche Bank|Dresdner Bank|Commerzbank|Spardabank|HVB)"));
		this.bicGenerator = registerComponent(new RegexStringGenerator("[A-Z]{4}DE[A-Z0-9]{2}"));
		this.binGenerator = registerComponent(new RandomVarLengthStringGenerator("\\d", 4));
	}
	
    @Override
    public synchronized void init(GeneratorContext context) {
    	bankCodeGenerator.init(context);
    	nameGenerator.init(context);
    	bicGenerator.init(context);
    	binGenerator.init(context);
        super.init(context);
    }
    
	public ProductWrapper<Bank> generate(ProductWrapper<Bank> wrapper) {
		return wrapper.wrap(generate());
	}

	public Bank generate() {
		String name = nameGenerator.generate();
		String bankCode = bankCodeGenerator.generate();
		String bic = bicGenerator.generate();
		String bin = binGenerator.generate();
		return new Bank(name, bankCode, bic, bin);
	}
	
}
