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
 * @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 * @since 2.1.13
 */
public class WifcEditableAttackBuilder extends WifcAttackBuilder {
	
	@Override
	public void build() {
		String target = this.error.getTarget();
		String paramName = this.error.getParameterName();
		String paramValue = this.error.getParameterValue();
		String ruleName = this.error.getValidationRuleName();
		
		WifcElement root = this.root.appendXmlTag(XmlTags.EDITABLE_ATTACK);
		
		if (target != null) {
			root.appendXmlTag(XmlTags.URL, target);
		}
		
		if (paramName != null) {
			root.appendXmlTag(XmlTags.PARAMETER, paramName);
		}
		
		if (paramValue != null) {
			root.appendXmlTag(XmlTags.VALUE, paramValue);
		}
		
		if (ruleName != null) {
			root.appendXmlTag(XmlTags.REJECTED_PATTERN, ruleName);
		}
	}
}
