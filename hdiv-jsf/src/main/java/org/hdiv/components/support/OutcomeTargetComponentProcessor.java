package org.hdiv.components.support;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIOutcomeTarget;
import javax.faces.component.UIParameter;
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

public class OutcomeTargetComponentProcessor {

	private static Log log = LogFactory.getLog(OutcomeTargetComponentProcessor.class);

	private OutcomeTargetComponentHelper helper = new OutcomeTargetComponentHelper();

	private HDIVConfig hdivConfig;

	private LinkUrlProcessor urlProcessor;

	public void processOutcomeTargetLinkComponent(FacesContext context, UIOutcomeTarget component) {
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

			String url = this.helper.getUrl(context, component);

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

				String stateParam = dataComposer.endRequest();

				String hdivParameter = (String) externalContext.getSessionMap().get(Constants.HDIV_PARAMETER);

				// Add a children UIParam component with HDIV state
				UIParameter paramComponent = (UIParameter) context.getApplication().createComponent(
						UIParameter.COMPONENT_TYPE);
				paramComponent.setName(hdivParameter);
				paramComponent.setValue(stateParam);
				component.getChildren().add(paramComponent);
			}
		} catch (FacesException e) {
			log.error("Error in JsfLinkUrlProcessor.processOutcomeTargetLinkComponent: " + e.getMessage());
			throw e;
		}
	}

}
