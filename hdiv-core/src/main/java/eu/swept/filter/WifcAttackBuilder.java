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

import org.hdiv.filter.ValidatorError;

/**
 * WARNING: this class is not thread-safe.
 * @author Ander Juaristi &lt;ander.juaristi@tecnalia.com&gt;
 */
public abstract class WifcAttackBuilder {
	
	protected WifcAttackBuilder concreteWifcAttackBuilder;
	
	protected WIFCElement root;
	protected ValidatorError error;

	public static WifcAttackBuilder newEditableAttack() {
		return new WifcEditableAttackBuilder();
	}
	
	public static WifcAttackBuilder newIntegrityAttack() {
		return new WifcIntegrityAttackBuilder();
	}
	
	protected WifcAttackBuilder() {
	}
	
	public WifcAttackBuilder setRootElement(WIFCElement root) {
		this.root = root;
		return this;
	}
	
	public WifcAttackBuilder setValidatorError(ValidatorError error) {
		this.error = error;
		return this;
	}
	
	public abstract void build();
}
