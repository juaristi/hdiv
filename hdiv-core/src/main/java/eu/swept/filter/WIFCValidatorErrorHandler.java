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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hdiv.filter.ValidatorError;
import org.hdiv.filter.ValidatorErrorHandler;
import org.hdiv.util.HDIVErrorCodes;
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

		@Override
		public String toString() {
			String xml = null;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Result result = new StreamResult(os);
			Source source = new DOMSource(this.document);
			Transformer transformer = null;
			
			try {
				
				transformer = TransformerFactory.newInstance().newTransformer();
				transformer.transform(source, result);
				
				xml = os.toString();
				// TODO Maybe we should use HDIV's full-fledged logger?
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return xml;
		}
	}
	
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
	
	/*
	 * TODO Maybe we should add more attack types here.
	 */
	protected void printValidatorError(WifcElement root, ValidatorError error) {
		WifcElement attackRoot = null;
		
		if (error.getType().equals(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR)) {
			attackRoot = root.appendXmlTag(XmlTags.EDITABLE_ATTACK);
			this.printEditableAttack(attackRoot, error);
		} else if (error.getType().equals(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE)) {
			attackRoot = root.appendXmlTag(XmlTags.INTEGRITY_ATTACK);
			this.printIntegrityAttack(attackRoot, error);
		}
	}
	
	protected void printEditableAttack(WifcElement root, ValidatorError error) {
		root.appendXmlTag(XmlTags.URL, error.getTarget());
		root.appendXmlTag(XmlTags.PARAMETER, error.getParameterName());
		root.appendXmlTag(XmlTags.VALUE, error.getParameterValue());
		root.appendXmlTag(XmlTags.REJECTED_VALUE, error.getValidationRuleName());
	}
	
	protected void printIntegrityAttack(WifcElement root, ValidatorError error) {
		root.appendXmlTag(XmlTags.URL, error.getTarget());
		
		WifcElement paramRoot = root.appendXmlTag(XmlTags.PARAMETER);
		paramRoot.appendXmlTag(XmlTags.NAME, error.getParameterName());
		paramRoot.appendXmlTag(XmlTags.ORIGINAL_VALUE, error.getOriginalParameterValue());
		paramRoot.appendXmlTag(XmlTags.VALUE, error.getParameterValue());
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
