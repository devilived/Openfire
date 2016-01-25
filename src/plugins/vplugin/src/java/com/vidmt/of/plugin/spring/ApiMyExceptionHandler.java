package com.vidmt.of.plugin.spring;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.exceptoins.CodeException;

//@Service("org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#0")
public class ApiMyExceptionHandler extends ExceptionHandlerExceptionResolver {
	private Logger log = LoggerFactory.getLogger(ApiMyExceptionHandler.class);

	@Override
	protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response,
			HandlerMethod handlerMethod, Exception exception) {
		if (handlerMethod == null) {
			return null;
		}

		Method method = handlerMethod.getMethod();

		if (method == null) {
			return null;
		}
		ModelAndView mv = super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
		ResponseBody responseBodyAnn = AnnotationUtils.findAnnotation(method, ResponseBody.class);
		if (responseBodyAnn != null) {
			try {
				ResponseStatus responseStatusAnn = AnnotationUtils.findAnnotation(method, ResponseStatus.class);
				if (responseStatusAnn != null) {
					HttpStatus responseStatus = responseStatusAnn.value();
					String reason = responseStatusAnn.reason();
					if (!StringUtils.hasText(reason)) {
						response.setStatus(responseStatus.value());
					} else {
						try {
							response.sendError(responseStatus.value(), reason);
						} catch (IOException e) {
						}
					}
				}
				// 如果没有ExceptionHandler注解那么mv就为空
				if (mv == null) {
					JSONObject json = new JSONObject();
					json.put("c", CodeException.ERR_UNKOWN);
					json.put("m", exception.getMessage());
					handleResponseError(json, request, response);
					return new ModelAndView();
				}
				return handleResponseBody(mv, request, response);
			} catch (Exception e) {
				return null;
			}
		}

		// 如果没有ExceptionHandler注解那么returnValue就为空
		return mv;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	private ModelAndView handleResponseBody(ModelAndView mv, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		SpringUtils.getServletContext().get
		ModelMap modelMap = mv.getModelMap();
		HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
		Class<? extends ModelMap> modalClz = modelMap.getClass();
		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
		if (messageConverters != null) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				for (HttpMessageConverter converter : messageConverters) {
					if (converter.canWrite(modalClz, acceptedMediaType)) {
						converter.write(modelMap, acceptedMediaType, outputMessage);
						return new ModelAndView();
					}
				}
			}
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not find HttpMessageConverter that supports return type [" + modalClz + "] and "
					+ acceptedMediaTypes);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	private ModelAndView handleResponseError(JSONObject json, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
		List<MediaType> acceptedMediaTypes = inputMessage.getHeaders().getAccept();
		if (acceptedMediaTypes.isEmpty()) {
			acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
		}
		MediaType.sortByQualityValue(acceptedMediaTypes);
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(response);
		Class<?> returnValueType = json.getClass();
		List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
		if (messageConverters != null) {
			for (MediaType acceptedMediaType : acceptedMediaTypes) {
				for (HttpMessageConverter messageConverter : messageConverters) {
					if (messageConverter.canWrite(returnValueType, acceptedMediaType)) {
						messageConverter.write(json, acceptedMediaType, outputMessage);
						return new ModelAndView();
					}
				}
			}
		}
		logger.warn("Could not find HttpMessageConverter that supports return type [" + returnValueType + "] and "
				+ acceptedMediaTypes);
		return null;
	}
}
