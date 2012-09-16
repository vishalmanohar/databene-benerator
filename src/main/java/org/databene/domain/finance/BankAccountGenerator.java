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
import org.databene.benerator.wrapper.CompositeGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.commons.LocaleUtil;
import org.databene.commons.StringUtil;
import org.databene.domain.address.Country;

/**
 * Generates German {@link BankAccount}s with low validity requirements.<br/><br/>
 * Created at 24.06.2008 08:36:32
 * @since 0.5.4
 * @author Volker Bergmann
 */
public class BankAccountGenerator extends CompositeGenerator<BankAccount> implements NonNullGenerator<BankAccount> {
	
	private String countryCode;
	private BankGenerator bankGenerator;
	private RandomVarLengthStringGenerator accountNumberGenerator;

	public BankAccountGenerator() {
		super(BankAccount.class);
		LocaleUtil.getFallbackLocale();
		this.countryCode = Country.getDefault().getIsoCode();
		this.bankGenerator = registerComponent(new BankGenerator());
		this.accountNumberGenerator = registerComponent(new RandomVarLengthStringGenerator("\\d", 10));
	}
	
    @Override
    public synchronized void init(GeneratorContext context) {
    	bankGenerator.init(context);
    	accountNumberGenerator.init(context);
        super.init(context);
    }
    
	public ProductWrapper<BankAccount> generate(ProductWrapper<BankAccount> wrapper) {
		return wrapper.wrap(generate());
	}

	public BankAccount generate() {
		Bank bank = bankGenerator.generate();
		String accountNumber = accountNumberGenerator.generate();
		String iban = createIban(bank, accountNumber);
		return new BankAccount(bank, accountNumber, iban);
	}

	private String createIban(Bank bank, String accountNumber) {
		StringBuilder builder = new StringBuilder(countryCode);
		builder.append("00");
		builder.append(bank.getBankCode());
		builder.append(StringUtil.padLeft(accountNumber, 10, '0'));
		return IBANUtil.fixChecksum(builder.toString());
	}

}
