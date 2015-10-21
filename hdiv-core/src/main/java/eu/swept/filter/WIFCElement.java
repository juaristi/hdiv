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

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This is an internal class.
 * Thin wrapper around org.w3c.dom.Node. We consider two possible implementations:
 * 
 *  - org.w3c.dom.Document
 *  - org.w3c.dom.Element
 *  
 *  org.w3c.dom.Document is used only once within the WifcElement DOM tree.
 *  It's a reference to the root XML document being generated.
 *  
 *  @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 *  @since 2.1.13
 */
public class WIFCElement {
	private Document document;
	private Node element;
	
	public WIFCElement(Document document) {
		this.document = document;
		this.element = null;
	}
	
	public WIFCElement(Document document, Node element) {
		this.document = document;
		this.element = element;
	}
	
	public WIFCElement appendXmlTag(XmlTags xmlTag) {
		return this.appendXmlTag(xmlTag, null);
	}
	
	public WIFCElement appendXmlTag(XmlTags xmlTag, String content) {
		Node elem = (this.element == null ? this.document : this.element);
		Element childElem = this.document.createElement(xmlTag.toString());
		
		elem.appendChild(childElem);
		
		if (content != null && !content.isEmpty()) {
			childElem.appendChild(this.document.createTextNode(content));
		}
		
		return new WIFCElement(this.document, childElem);
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

	@Override
	public boolean equals(Object obj) {
		WIFCElement other = null;
		boolean equal = false;
		
		if (obj instanceof WIFCElement) {
			other = (WIFCElement) obj;
			equal = other.toString().equals(this.toString());
		}
		
		return equal;
	}
}
