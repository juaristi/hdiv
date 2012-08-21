/**
 * Copyright 2005-2012 hdiv.org
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
package org.hdiv.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.urlProcessor.LinkUrlProcessor;
import org.hdiv.urlProcessor.UrlData;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.UtilsJsf;

/**
 * HtmlOutputLink component extension
 * 
 * @author Gotzon Illarramendi
 * 
 */
public class HtmlOutputLinkExtension extends HtmlOutputLink {

	private static Log log = LogFactory.getLog(HtmlOutputLinkExtension.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context. FacesContext)
	 */
	@SuppressWarnings("unchecked")
	public void encodeBegin(FacesContext context) throws IOException {

		try {
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			ServletContext servletContext = request.getSession().getServletContext();

			HDIVConfig hdivConfig = HDIVUtil.getHDIVConfig(servletContext);
			LinkUrlProcessor urlProcessor = HDIVUtil.getLinkUrlProcessor(servletContext);

			String url = this.getValue().toString();

			UrlData urlData = urlProcessor.createUrlData(url, request);
			if (urlProcessor.isHdivStateNecessary(urlData)) {

				boolean hasUIParams = UtilsJsf.hasUIParameterChild(this);

				// if url hasn't got parameters, we do not have to include HDIV's state
				if (!hdivConfig.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams() && !hasUIParams) {

					super.encodeBegin(context);
					return;
				}

				IDataComposer dataComposer = HDIVUtil.getDataComposer(request);
				dataComposer.beginRequest(urlData.getContextPathRelativeUrl());

				Map<String, String> params = urlData.getOriginalUrlParams();
				if (params != null) {
					// Process url params
					for (String key : params.keySet()) {
						String value = (String) params.get(key);
						String composedParam = dataComposer.compose(key, value, false, true, Constants.ENCODING_UTF_8);
						params.put(key, composedParam);
					}
					urlData.setProcessedUrlParams(params);
				}

				if (hasUIParams) {

					for (UIComponent comp : this.getChildren()) {
						if (comp instanceof UIParameter) {
							UIParameter param = (UIParameter) comp;
							String name = param.getName();
							String value = param.getValue().toString();

							dataComposer.compose(name, value, false);
						}
					}

					String stateParam = dataComposer.endRequest();

					url = urlProcessor.getProcessedUrl(urlData);

					this.setValue(url);

					// Add a children UIParam component with Hdiv's state
					UIParameter paramComponent = (UIParameter) context.getApplication().createComponent(
							UIParameter.COMPONENT_TYPE);

					String hdivParameter = (String) externalContext.getSessionMap().get(Constants.HDIV_PARAMETER);

					paramComponent.setName(hdivParameter);
					paramComponent.setValue(stateParam);
					this.getChildren().add(paramComponent);
				} else {

					String stateParam = dataComposer.endRequest();

					// Add state directly in the outputLink's value
					url = urlProcessor.getProcessedUrlWithHdivState(request, urlData, stateParam);
					this.setValue(url);
				}
			}
		} catch (FacesException e) {
			log.error("Error en HtmlOutputLinkExtension: " + e.getMessage());
			throw e;
		}

		super.encodeBegin(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
	 */
	public void encodeEnd(FacesContext context) throws IOException {

		super.encodeEnd(context);

		// Remove the component with the HDIV state, we don't want to store it in the state
		String hdivParameter = (String) context.getExternalContext().getSessionMap().get(Constants.HDIV_PARAMETER);

		// First we add to a list the components to remove
		// The list used by MyFaces has a problem with the iterator
		List<Integer> toRemoveList = new ArrayList<Integer>();
		for (UIComponent comp : this.getChildren()) {
			if (comp instanceof UIParameter) {
				UIParameter param = (UIParameter) comp;
				String name = param.getName();
				if (name != null && name.equals(hdivParameter)) {
					Integer index = this.getChildren().indexOf(param);
					toRemoveList.add(index);
				}
			}
		}
		// Remove the ones founded before
		for (Integer index : toRemoveList) {
			this.getChildren().remove(index.intValue());
		}
		// Deprecated method in 1.2, but necessary to work in 1.1
		if (this.getValueBinding("value") != null) {
			this.setValue(null);
		}
	}

}
