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

/**
 * This enum defines all the XML tags allowed in a WIFC response.
 * Each element represents a given XML tag, and bound to its string
 * counterpart. One should call <code>toString()</code> to retrieve
 * the name of the tag without the enclosing <code>&lt</code> and
 * <code>&gt;</code> characters.
 * 
 * @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 * @since  2.1.13
 */
public enum XmlTags {

	RESPONSE("response"),
	TYPE("type"),
	ATTACKS("attacks"),
	INTEGRITY_ATTACK("integrityAttack"),
	EDITABLE_ATTACK("editableAttack"),
	URL("url"),
	PARAMETER("parameter"),
	NAME("name"),
	VALUE("value"),
	ORIGINAL_VALUE("originalValue"),
	REJECTED_VALUE("rejectedValue");
	
	private String tagName;
	private XmlTags(String tagName) {
		this.tagName = tagName;
	}
	
	@Override
	public String toString() {
		return this.tagName;
	}
}
