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

package org.databene.domain.organization;

import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.dataset.DatasetUtil;
import org.databene.commons.Encodings;

/**
 * Generates the abbreviated strings for legal forms of organizations.<br/><br/>
 * Created: 24.08.2011 00:41:07
 * @since 0.7.0
 * @author Volker Bergmann
 */
public class LegalFormGenerator extends WeightedDatasetCSVGenerator<String> {

    private static final String PATH_PATTERN = "/org/databene/domain/organization/legalForm_{0}.csv";

    public LegalFormGenerator(String dataset) {
		super(String.class, PATH_PATTERN, dataset, DatasetUtil.REGION_NESTING, true, Encodings.UTF_8);		
	}
	
}
