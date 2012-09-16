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

package org.databene.domain.organization;

import static org.databene.benerator.util.GeneratorUtil.*;

import java.util.HashMap;
import java.util.Map;

import org.databene.benerator.Generator;
import org.databene.benerator.GeneratorContext;
import org.databene.benerator.NonNullGenerator;
import org.databene.benerator.WeightedGenerator;
import org.databene.benerator.csv.WeightedDatasetCSVGenerator;
import org.databene.benerator.dataset.AbstractDatasetGenerator;
import org.databene.benerator.dataset.Dataset;
import org.databene.benerator.dataset.DatasetUtil;
import org.databene.benerator.primitive.RegexStringGenerator;
import org.databene.benerator.primitive.TokenCombiner;
import org.databene.benerator.sample.ConstantGenerator;
import org.databene.benerator.sample.SequencedCSVSampleGenerator;
import org.databene.benerator.util.ThreadSafeNonNullGenerator;
import org.databene.benerator.wrapper.AlternativeGenerator;
import org.databene.benerator.wrapper.MessageGenerator;
import org.databene.benerator.wrapper.ProductWrapper;
import org.databene.benerator.wrapper.WrapperFactory;
import org.databene.commons.ConfigurationError;
import org.databene.commons.Encodings;
import org.databene.commons.bean.PropertyAccessConverter;
import org.databene.domain.address.CityGenerator;
import org.databene.domain.address.Country;
import org.databene.domain.person.FamilyNameGenerator;
import org.databene.domain.person.Gender;
import org.databene.domain.person.GivenNameGenerator;
import org.databene.text.NameNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates company names.<br/><br/>
 * Created: 14.03.2008 08:26:44
 * @since 0.5.0
 * @author Volker Bergmann
 */
public class CompanyNameGenerator extends AbstractDatasetGenerator<CompanyName> 
		implements NonNullGenerator<CompanyName> {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(CompanyNameGenerator.class);

    private static final String ORG = "/org/databene/domain/organization/";

    protected static Map<String, Generator<String>> locationGenerators = 
    	new HashMap<String, Generator<String>>();
    
    protected String datasetName;
    protected boolean sector;
    protected boolean location;
    protected boolean legalForm;
    
    public CompanyNameGenerator() {
    	this(true, true, true);
    }
    
    public CompanyNameGenerator(boolean sector, boolean location, boolean legalForm) {
    	this(sector, location, legalForm, Country.getDefault().getIsoCode());
    }

    public CompanyNameGenerator(String dataset) {
    	this(true, true, true, dataset);
    }

    public CompanyNameGenerator(boolean sector, boolean location, boolean legalForm, String datasetName) {
    	super(CompanyName.class, DatasetUtil.REGION_NESTING, datasetName, true);
    	LOGGER.debug("Creating instance of {} for dataset {}", getClass(), datasetName);
    	this.sector = sector;
    	this.location = location;
    	this.legalForm = legalForm;
        this.datasetName = datasetName;
        setDataset(datasetName);
    }
    
    @Override
	protected boolean isAtomic(Dataset dataset) {
		Country country = Country.getInstance(dataset.getName(), false);
		return (country != null);
    }
    
	@Override
	protected WeightedGenerator<CompanyName> createGeneratorForAtomicDataset(Dataset dataset) {
		Country country = Country.getInstance(dataset.getName(), false);
		return new CountryCompanyNameGenerator(country);
	}

	public CompanyName generate() {
		ProductWrapper<CompanyName> wrapper = generate(getResultWrapper());
		return (wrapper != null ? wrapper.unwrap() : null);
	}
	
	
	
	class CountryCompanyNameGenerator extends ThreadSafeNonNullGenerator<CompanyName> 
				implements WeightedGenerator<CompanyName> {
		
		private Country country;
	    private AlternativeGenerator<String> shortNameGenerator;
	    private SectorGenerator sectorGenerator;
	    private WeightedDatasetCSVGenerator<String> legalFormGenerator;
	    private Generator<String> locationGenerator;

		public CountryCompanyNameGenerator(Country country) {
			this.country = country;
		}

		public Class<CompanyName> getGeneratedType() {
		    return CompanyName.class;
	    }

	    @Override
	    public synchronized void init(GeneratorContext context) {
	        try {
	        	super.init(context);
				initWithDataset(country.getIsoCode(), context);
			} catch (Exception e) {
				String fallbackDataset = DatasetUtil.fallbackRegionName();
				LOGGER.warn("Error initializing location generator for dataset " + datasetName + ", falling back to " + fallbackDataset);
				initWithDataset(fallbackDataset, context);
			}
	    }

		public void initWithDataset(String datasetToUse, GeneratorContext context) {
			createAndInitLocationGenerator(datasetToUse);
			initLegalFormGenerator(datasetToUse);
			initSectorGenerator(datasetToUse);
			createAndInitShortNameGenerator(datasetToUse, context);
			super.init(context);
		}

		@Override
		public CompanyName generate() {
			CompanyName name = new CompanyName();
	        name.setShortName(generateNonNull(shortNameGenerator));
	        
	        if (sectorGenerator != null) {
				String sector = generateNullable(sectorGenerator);
		        if (sector != null)
		            name.setSector(sector);
	        }
	        if (locationGenerator != null) {
	        	String location = generateNullable(locationGenerator);
	        	if (location != null)
		            name.setLocation(location);
	        }
	        if (legalFormGenerator != null)
	        	name.setLegalForm(generateNullable(legalFormGenerator));
	        name.setDatasetName(datasetName);
	        return name;
		}

		public double getWeight() {
			return country.getPopulation();
		}

	    @Override
	    public String toString() {
	        return getClass().getSimpleName() + '[' + datasetName + ']';
	    }

	    // private helpers -------------------------------------------------------------------------------------------------
	    
	    private void createAndInitShortNameGenerator(String datasetToUse, GeneratorContext context) {
			shortNameGenerator = new AlternativeGenerator<String>(String.class);
			shortNameGenerator.addSource(createInitialsNameGenerator());
			addSourceIfNotNull(createPersonNameGenerator(datasetToUse), shortNameGenerator);
			addSourceIfNotNull(createArtificialNameGenerator(), shortNameGenerator);
			addSourceIfNotNull(createTechNameGenerator(), shortNameGenerator);
			shortNameGenerator.init(context);
		}
		
		private void addSourceIfNotNull(Generator<String> source, AlternativeGenerator<String> master) {
			if (source != null)
				master.addSource(source);
		}

		private RegexStringGenerator createInitialsNameGenerator() {
			return new RegexStringGenerator("[A-Z]{3}");
		}

		private MessageGenerator createTechNameGenerator() {
		    try {
	            return new MessageGenerator("{0}{1}", 
	                    new SequencedCSVSampleGenerator<String>(ORG + "tech1.csv"),
	                    new SequencedCSVSampleGenerator<String>(ORG + "tech2.csv")
	                );
	        } catch (Exception e) {
	        	LOGGER.info("Cannot create technical company name generator: " + e.getMessage());
	        	return null;
	        }
	    }

		private TokenCombiner createArtificialNameGenerator() {
		    try {
		    	return new TokenCombiner(ORG + "artificialName.csv", false, '-', Encodings.UTF_8, false);
	        } catch (Exception e) {
	        	LOGGER.info("Cannot create artificial company name generator: " + e.getMessage());
	        	return null;
	        }
	    }

		private MessageGenerator createPersonNameGenerator(String datasetToUse) {
		    try {
		        return new MessageGenerator("{0} {1}", 
		                GivenNameGenerator.sharedInstance(datasetToUse, Gender.MALE),
		                FamilyNameGenerator.sharedInstance(datasetToUse)
		            );
	        } catch (Exception e) {
	        	LOGGER.info("Cannot create person-based company name generator: " + e.getMessage());
	        	return null;
	        }
	    }

		private void initSectorGenerator(String datasetName) {
		    if (sector) {
	        	try {
	        		Country country = Country.getInstance(datasetName);
					sectorGenerator = new SectorGenerator(country.getDefaultLanguageLocale());
	        		sectorGenerator.init(context);
	        	} catch (Exception e) {
	        		if ("US".equals(datasetName))
	        			throw new ConfigurationError("Failed to initialize SectorGenerator with US dataset", e);
	        		LOGGER.info("Cannot create sector generator: " + e.getMessage() + ". Falling back to US");
	        		initSectorGenerator("US");
	        	}
	        }
	    }

		private void initLegalFormGenerator(String datasetName) {
		    if (legalForm) {
		    	
	        	try {
	        		legalFormGenerator = new LegalFormGenerator(datasetName);
	        		legalFormGenerator.init(context);
	        	} catch (Exception e) {
	        		LOGGER.error("Cannot create legal form generator: " + e.getMessage() + ". Falling back to US. ");
	        		initLegalFormGenerator("US");
	        	}
	        }
	    }

		@SuppressWarnings("unchecked")
	    private void createAndInitLocationGenerator(String datasetName) {
			locationGenerator = locationGenerators.get(datasetName);
			if (locationGenerator == null) {
				double nullQuota = 0.8;
			    Country country = Country.getInstance(datasetName);
			    Generator<String> locationBaseGen;
		        if (location && country != null) {
		        	try {
			            Generator<String> cityGen = WrapperFactory.applyConverter(
			            		new CityGenerator(country.getIsoCode()), 
			            		new PropertyAccessConverter("name"), 
			            		new NameNormalizer());
			            if (DatasetUtil.getDataset(DatasetUtil.REGION_NESTING, datasetName).isAtomic()) {
				            locationBaseGen = new AlternativeGenerator<String>(String.class, 
				                    			new ConstantGenerator<String>(country.getLocalName()), 
				                    			cityGen);
			            } else {
			            	locationBaseGen = cityGen;
			            }
		        	} catch (Exception e) {
		        		LOGGER.info("Cannot create location generator: " + e.getMessage());
		                locationBaseGen = new ConstantGenerator<String>(null);
		        	}
		        } else
		        	locationBaseGen = new ConstantGenerator<String>(null);
		        locationGenerator = WrapperFactory.injectNulls(locationBaseGen, nullQuota);
		        locationGenerator.init(context);
		        locationGenerators.put(datasetName, locationGenerator);
			}
	    }

	}

}
