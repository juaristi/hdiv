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
package org.hdiv.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurer;
import org.hdiv.config.annotation.configuration.HdivWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Import;

/**
 * Add this annotation to an {@code @Configuration} class to have the HDIV Security
 * configuration defined in any {@link HdivWebSecurityConfigurer} or more likely by extending the
 * {@link HdivWebSecurityConfigurerAdapter} base class and overriding individual methods:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableHdivWebSecurity
 * public class HdivSecurityConfig extends HdivWebSecurityConfigurerAdapter {
 * 
 * 	&#064;Override
 * 	public void configure(SecurityConfigBuilder builder) {
 * 
 * 		builder
 * 			.sessionExpired()
 * 			.homePage("/").loginPage("/login.html").and()
 * 			.debugMode(false);
 * 	}
 * 	
 * 	&#064;Override
 * 	public void addExclusions(ExclusionRegistry registry) {
 * 
 * 		registry.addUrlExclusions("/", "/login.html", "/logout.html").method("GET");
 * 		registry.addUrlExclusions("/j_spring_security_check").method("POST");
 * 		registry.addUrlExclusions("/attacks/.*");
 * 		
 * 		registry.addParamExclusions("param1", "param2").forUrls("/attacks/.*");
 * 	}
 * 
 * 	&#064;Override
 * 	public void addRules(RuleRegistry registry) {
 * 
 * 		registry.addRule("safeText").acceptedPattern("^[a-zA-Z0-9&#064;.\\-_]*$");
 * 	}
 * 
 * 	&#064;Override
 * 	public void configureEditableValidation(ValidationConfigurer validationConfigurer) {
 * 
 * 		validationConfigurer.addValidation("/secure/.*");
 * 		validationConfigurer.addValidation("/safetext/.*").rules("safeText").disableDefaults();
 * 	}
 *  // Possibly more overridden methods ...
 * }
 * </pre>
 *
 * @see HdivWebSecurityConfigurer
 * @see HdivWebSecurityConfigurerAdapter
 *
 * @author Gotzon Illarramendi
 * @since 2.1.7
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingHdivWebSecurityConfiguration.class)
public @interface EnableHdivWebSecurity {

}
