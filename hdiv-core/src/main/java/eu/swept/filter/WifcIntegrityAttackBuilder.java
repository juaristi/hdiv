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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 */
public class WifcIntegrityAttackBuilder extends WifcAttackBuilder {

	private Map<String, WifcElement> existingTags;
	
	protected WifcIntegrityAttackBuilder() {
		super();
		this.existingTags = new HashMap<String, WifcElement>();
	}
	
	@Override
	public void build() {
		WifcElement attackRoot = null, paramRoot = null;
		
		String target = this.error.getTarget();
		String paramName = this.error.getParameterName();
		String paramValue = this.error.getParameterValue();
		String paramOriginalValue = this.error.getOriginalParameterValue();
		
		if (target != null) {
			/*
			 * Try to get an existing <integrityAttack> key.
			 * If no such tag is found for the current target, then create a new one.
			 */
			if (this.existingTags.containsKey(target)) {
				attackRoot = this.existingTags.get(target);
			} else {
				attackRoot = this.root.appendXmlTag(XmlTags.INTEGRITY_ATTACK);
				attackRoot.appendXmlTag(XmlTags.URL, target);
				this.existingTags.put(target, attackRoot);
			}
			
			paramRoot = attackRoot.appendXmlTag(XmlTags.PARAMETER);
			
			if (paramName != null) {
				paramRoot.appendXmlTag(XmlTags.NAME, paramName);
			}
			if (paramOriginalValue != null) {
				paramRoot.appendXmlTag(XmlTags.ORIGINAL_VALUE, paramOriginalValue);
			}
			if (paramValue != null) {
				paramRoot.appendXmlTag(XmlTags.VALUE, paramValue);
			}
		}
	}

}
