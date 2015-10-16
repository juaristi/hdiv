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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This error handler generates a WIFC-compliant XML response describing
 * the validation errors raised by HDIV.
 * 
 * @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 * @since 2.1.13
 */
public class WIFCValidatorErrorHandler implements ValidatorErrorHandler {

	/*
	 * (no Javadoc)
	 * 
	 * This is an internal class.
	 * Thin wrapper around org.w3c.dom.Node. We consider two possible implementations:
	 * 
	 *  - org.w3c.dom.Document
	 *  - org.w3c.dom.Element
	 *  
	 *  org.w3c.dom.Document is used only once within the WifcElement DOM tree.
	 *  It's a reference to the root XML document being generated.
	 */
	private class WifcElement {
		private Document document;
		private Node element;
		
		public WifcElement(Document document) {
			this.document = document;
			this.element = null;
		}
		
		public WifcElement(Document document, Node element) {
			this.document = document;
			this.element = element;
		}
		
		public WifcElement appendXmlTag(XmlTags xmlTag) {
			return this.appendXmlTag(xmlTag, null);
		}
		
		public WifcElement appendXmlTag(XmlTags xmlTag, String content) {
			Node elem = (this.element == null ? this.document : this.element);
			Element childElem = this.document.createElement(xmlTag.toString());
			
			elem.appendChild(childElem);
			
			if (content != null && !content.isEmpty()) {
				childElem.appendChild(this.document.createTextNode(content));
			}
			
			return new WifcElement(this.document, childElem);
		}
	}
	
	private static final String RESPONSE_TYPE_ACCEPTED = "accepted";
	private static final String RESPONSE_TYPE_REJECTED = "rejected";
	
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
			
			for (ValidatorError error : errors) {
				this.printValidatorError(attacksElem, error);
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			// TODO Use the full-fledged HDIV logger here
			e.printStackTrace();
		}
	}
	
	/*
	 * TODO Implement.
	 */
	protected void printValidatorError(WifcElement root, ValidatorError error) {
		return;
	}
	
	protected WifcElement initXml() throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.newDocument();
		return new WifcElement(doc);
	}

}
