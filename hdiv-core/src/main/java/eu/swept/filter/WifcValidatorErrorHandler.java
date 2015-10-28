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
package eu.swept.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hdiv.config.HDIVConfig;
import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.util.HDIVErrorCodes;
import org.w3c.dom.Document;

/**
 * This error handler generates a WIFC-compliant XML response describing
 * the validation errors raised by HDIV.
 * 
 * @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 * @since 2.1.13
 */
public class WifcValidatorErrorHandler implements ValidatorErrorHandler {
	
	/*
	 * TODO This should go somewhere else because ValidatorErrorHandlers don't get invoked
	 * when everything went OK.
	 */
	private static final String RESPONSE_TYPE_ACCEPTED = "accepted";
	private static final String RESPONSE_TYPE_REJECTED = "rejected";
	
	/*
	 * MIME type for our XML response.
	 * TODO It'd be interesting if we could register a new MIME type in IANA for WIFC.
	 * Maybe "application/wifc+xml" ?
	 */
	private static final String XML_CONTENT_TYPE       = "text/xml";
	
	/**
	 * HDIV general configuration
	 */
	protected HDIVConfig config;
	
	public void handleValidatorError(HttpServletRequest request, HttpServletResponse response,
			List<ValidatorError> errors) {
		
		WifcElement doc = null,
				rootElem = null,
				attacksElem = null;
		
		try {
			doc = this.initXml();
			
			rootElem = doc.appendXmlTag(XmlTags.RESPONSE);
			rootElem.appendXmlTag(XmlTags.TYPE, RESPONSE_TYPE_REJECTED);
			
			attacksElem = rootElem.appendXmlTag(XmlTags.ATTACKS);
			
			this.printValidatorErrors(errors, attacksElem);
			
			this.printResponse(response, doc.toString());
			// TODO Use the full-fledged HDIV logger here
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the global HDIV configuration.
	 * This method is required for the Spring MVC framework.
	 * 
	 * @param config General HDIV configuration.
	 */
	public void setConfig(HDIVConfig config) {
		this.config = config;
	}
	
	/*
	 * TODO How do we handle all the attack types other than EDITABLE_VALIDATION_ERROR?
	 */
	protected void printValidatorErrors(List<ValidatorError> errors, WifcElement root) {
		WifcAttackBuilder attackBuilder = null;
		
		for (ValidatorError error : errors) {
			/*
			 * There's only one kind of editable validation error type.
			 * All the others are integrity errors.
			 */
			if (error.getType().equals(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR)) {
				attackBuilder = WifcAttackBuilder.newEditableAttack();
			} else {
				attackBuilder = WifcAttackBuilder.newIntegrityAttack();
			}
			
			attackBuilder
				.setRootElement(root)
				.setValidatorError(error);
			attackBuilder.build();
		}
	}
	
	protected WifcElement initXml() throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.newDocument();
		return new WifcElement(doc);
	}
	
	/*
	 * TODO Implement.
	 */
	protected void printResponse(HttpServletResponse servlet, String response) throws IOException {
		PrintWriter out = servlet.getWriter();
		
		servlet.setContentType(XML_CONTENT_TYPE);
		
		out.print(response);
		out.flush();
	}

}