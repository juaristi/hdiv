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
import java.util.List;

/**
 * Default {@link EditableDataValidationProvider} implementation based on validations defined in hdiv-config.xml file.
 * 
 * @since HDIV 2.1.10
 */
public class DefaultEditableDataValidationProvider implements EditableDataValidationProvider, Serializable {

	private static final long serialVersionUID = 2276666823731793620L;

	protected ValidationRepository validationRepository;

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable parameter <code>parameter</code>, using the
	 * validations defined in the hdiv-config.xml configuration file of Spring.
	 * </p>
	 * 
	 * @param url
	 *            target url
	 * @param parameter
	 *            parameter name
	 * @param values
	 *            parameter's values
	 * @param dataType
	 *            editable data type
	 * @return True if the values <code>values</code> are valid for the parameter <code>parameter</code>.
	 */
	public EditableDataValidationResult validate(String url, String parameter, String[] values, String dataType) {

		if (this.validationRepository == null) {
			return EditableDataValidationResult.VALID;
		}

		List<IValidation> validations = this.validationRepository.findValidations(url, parameter);

		for (IValidation currentValidation : validations) {

			if (!currentValidation.validate(parameter, values, dataType)) {

				EditableDataValidationResult result = new EditableDataValidationResult(false,
						currentValidation.getName());
				return result;
			}
		}
		return EditableDataValidationResult.VALID;
	}

	/**
	 * @param validationRepository
	 *            the validationRepository to set
	 */
	public void setValidationRepository(ValidationRepository validationRepository) {
		this.validationRepository = validationRepository;
	}

	/**
	 * @return the validationRepository
	 */
	public ValidationRepository getValidationRepository() {
		return validationRepository;
	}

}
