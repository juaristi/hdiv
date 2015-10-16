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
public class WIFCValidatorErrorHandler implements ValidatorErrorHandler {
	
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
		
		WIFCElement doc = null,
				rootElem = null,
				attacksElem = null;
		
		try {
			doc = this.initXml();
			
			rootElem = doc.appendXmlTag(XmlTags.RESPONSE);
			rootElem.appendXmlTag(XmlTags.TYPE, RESPONSE_TYPE_REJECTED);
			
			attacksElem = rootElem.appendXmlTag(XmlTags.ATTACKS);
			
			for (ValidatorError error : errors) {
				this.printValidatorError(attacksElem, error);
			}
			
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
	 * TODO Maybe we should add more attack types here.
	 */
	protected void printValidatorError(WIFCElement root, ValidatorError error) {
		WIFCElement attackRoot = null;
		
		if (error.getType().equals(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR)) {
			attackRoot = root.appendXmlTag(XmlTags.EDITABLE_ATTACK);
			this.printEditableAttack(attackRoot, error);
		} else if (error.getType().equals(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE)) {
			attackRoot = root.appendXmlTag(XmlTags.INTEGRITY_ATTACK);
			this.printIntegrityAttack(attackRoot, error);
		}
	}
	
	protected void printEditableAttack(WIFCElement root, ValidatorError error) {
		root.appendXmlTag(XmlTags.URL, error.getTarget());
		root.appendXmlTag(XmlTags.PARAMETER, error.getParameterName());
		root.appendXmlTag(XmlTags.VALUE, error.getParameterValue());
		root.appendXmlTag(XmlTags.REJECTED_PATTERN, error.getValidationRuleName());
	}
	
	protected void printIntegrityAttack(WIFCElement root, ValidatorError error) {
		root.appendXmlTag(XmlTags.URL, error.getTarget());
		
		WIFCElement paramRoot = root.appendXmlTag(XmlTags.PARAMETER);
		paramRoot.appendXmlTag(XmlTags.NAME, error.getParameterName());
		paramRoot.appendXmlTag(XmlTags.ORIGINAL_VALUE, error.getOriginalParameterValue());
		paramRoot.appendXmlTag(XmlTags.VALUE, error.getParameterValue());
	}
	
	protected WIFCElement initXml() throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.newDocument();
		return new WIFCElement(doc);
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
