package org.hdiv.components.support;

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

public class OutputLinkComponentProcessor {

	private static Log log = LogFactory.getLog(OutputLinkComponentProcessor.class);

	private HDIVConfig hdivConfig;

	private LinkUrlProcessor urlProcessor;

	public void processOutputLink(FacesContext context, HtmlOutputLink component) {

		try {
			ExternalContext externalContext = context.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			ServletContext servletContext = request.getSession().getServletContext();

			if (this.hdivConfig == null) {
				this.hdivConfig = HDIVUtil.getHDIVConfig(servletContext);
			}
			if (this.urlProcessor == null) {
				this.urlProcessor = HDIVUtil.getLinkUrlProcessor(servletContext);
			}

			String url = component.getValue().toString();

			UrlData urlData = this.urlProcessor.createUrlData(url, request);
			if (this.urlProcessor.isHdivStateNecessary(urlData)) {

				boolean hasUIParams = UtilsJsf.hasUIParameterChild(component);

				// if url hasn't got parameters, we do not have to include HDIV's state
				if (!hdivConfig.isValidationInUrlsWithoutParamsActivated() && !urlData.containsParams() && !hasUIParams) {

					// Do nothing
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

					for (UIComponent comp : component.getChildren()) {
						if (comp instanceof UIParameter) {
							UIParameter param = (UIParameter) comp;
							String name = param.getName();
							String value = param.getValue().toString();

							dataComposer.compose(name, value, false);
						}
					}

					String stateParam = dataComposer.endRequest();

					url = this.urlProcessor.getProcessedUrl(urlData);

					component.setValue(url);

					// Add a children UIParam component with Hdiv's state
					UIParameter paramComponent = (UIParameter) context.getApplication().createComponent(
							UIParameter.COMPONENT_TYPE);

					String hdivParameter = (String) externalContext.getSessionMap().get(Constants.HDIV_PARAMETER);

					paramComponent.setName(hdivParameter);
					paramComponent.setValue(stateParam);
					component.getChildren().add(paramComponent);
				} else {

					String stateParam = dataComposer.endRequest();

					// Add state directly in the outputLink's value
					url = this.urlProcessor.getProcessedUrlWithHdivState(request, urlData, stateParam);
					component.setValue(url);
				}
			}
		} catch (FacesException e) {
			log.error("Error en HtmlOutputLinkExtension: " + e.getMessage());
			throw e;
		}

	}
}
