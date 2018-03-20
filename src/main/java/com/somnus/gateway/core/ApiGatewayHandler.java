package com.somnus.gateway.core;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import com.somnus.gateway.comon.ApiException;
import com.somnus.gateway.core.ApiStore.ApiRunnable;

public class ApiGatewayHandler implements InitializingBean, ApplicationContextAware{
	
	private transient Logger	logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String METHOD = "method";
	
	private static final String PARAMS = "params";
	
	private ApiStore apiStore;
	
	private final ParameterNameDiscoverer parameterUtil;
	
	public ApiGatewayHandler() { this.parameterUtil = new LocalVariableTableParameterNameDiscoverer(); }

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		apiStore = new ApiStore(context);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		apiStore.loadApiFormSpringBeans();
	}
	
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		String params = request.getParameter(PARAMS);
		String method = request.getParameter(METHOD);
		Object result;
		
		ApiRunnable apiRun = null;
		
		/*try {
			apiRun = sysParamsValidate(request);
			logger.info("请求接口={{0}}参数={1}", method, params);
			Object[] args = buildParams(apiRun, params, request, response);
			result = apiRun.run(args);
		} catch (ApiException e) {
			response.setStatus(500);
			logger.info("调用接口={{0}}异常 参数={1}", method, params, e);
			result = handleError(e);
		} catch (InvocationTargetException e) {
			response.setStatus(500);
			logger.info("调用接口={{0}}异常 参数={1}", method, params, e.getTargetException());
			result = handleError(e);
		} catch (Exception e) {
			response.setStatus(500);
			logger.info("系统异常", e);
			result = handleError(e);
		}
		returnResult(result, response);*/
	}

	private Object[] buildParams(ApiRunnable apiRun, String paramJson, HttpServletRequest request,
			HttpServletResponse response) {
		
		return null;
	}

	private ApiRunnable sysParamsValidate(HttpServletRequest request) {
		String json = request.getParameter(PARAMS);
		String apiName = request.getParameter(METHOD);
		
		ApiRunnable api;
		
		if(StringUtils.isEmpty(apiName)) {
			throw new ApiException("调用失败:参数'method'为空");
		}else if(StringUtils.isEmpty(json)) {
			throw new ApiException("调用失败:参数'params'为空");
		}else if((api = apiStore.findApiRunnable(apiName)) == null ) {
			throw new ApiException("调用失败:指定API不存在，API:" + apiName);
		}
		return api;
	}
	

}
