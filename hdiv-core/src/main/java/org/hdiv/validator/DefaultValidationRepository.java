/**
 * Copyright 2005-2015 hdiv.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hdiv.regex.PatternMatcher;

/**
 * Validation rules container based in validations defined in hdiv-config.xml file.
 * 
 * @since HDIV 2.1.10
 */
public class DefaultValidationRepository implements ValidationRepository, Serializable {

	private static final long serialVersionUID = 467553775965908017L;

	/**
	 * Map containing the urls and parameters to which the user wants to apply validation for the editable parameters.
	 */
	protected Map<ValidationTarget, List<IValidation>> validations;

	/**
	 * All default editable validations.
	 */
	protected List<IValidation> defaultValidations;

	/**
	 * Returns the validation rules for a concrete url and parameter name.
	 * 
	 * @param url
	 *            request url
	 * @param parameter
	 *            parameter name
	 * @return Selected validations
	 */
	public List<IValidation> findValidations(String url, String parameter) {

		for (ValidationTarget target : this.validations.keySet()) {

			PatternMatcher urlMatcher = target.getUrl();

			if (urlMatcher.matches(url)) {

				List<PatternMatcher> paramMatchers = target.getParams();
				boolean paramMatch = false;

				if (paramMatchers != null && paramMatchers.size() > 0) {
					for (PatternMatcher paramMatcher : paramMatchers) {
						if (paramMatcher.matches(parameter)) {
							paramMatch = true;
							break;
						}
					}
				} else {
					paramMatch = true;
				}

				if (paramMatch) {

					return this.validations.get(target);
				}
			}
		}
		return new ArrayList<IValidation>();
	}

	/**
	 * Returns default validation rules.
	 * 
	 * @return Default validations
	 */
	public List<IValidation> findDefaultValidations() {
		return this.defaultValidations;
	}

	/**
	 * @param validations
	 *            the validations to set
	 */
	public void setValidations(Map<ValidationTarget, List<IValidation>> validations) {
		this.validations = validations;
	}

	/**
	 * @return the validations
	 */
	public Map<ValidationTarget, List<IValidation>> getValidations() {
		return validations;
	}

	/**
	 * @return the defaultValidations
	 */
	public List<IValidation> getDefaultValidations() {
		return defaultValidations;
	}

	/**
	 * @param defaultValidations
	 *            the defaultValidations to set
	 */
	public void setDefaultValidations(List<IValidation> defaultValidations) {
		this.defaultValidations = defaultValidations;
	}

}
