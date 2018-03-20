package com.somnus.gateway.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

public class ApiStore {
	
	private ApplicationContext applicationContext;
	
	private HashMap<String, ApiRunnable> apiMap = new HashMap<String, ApiRunnable>();
	
	public ApiStore(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext);
		this.applicationContext = applicationContext;
	}
	
	public void loadApiFormSpringBeans() {
		String[] names = applicationContext.getBeanDefinitionNames();
		Class<?> type;
		for(String name:names) {
			type = applicationContext.getType(name);
			for(Method m:type.getDeclaredMethods()) {
				ApiMapping apiMapping = m.getAnnotation(ApiMapping.class);
				if(apiMapping !=null) {
					addApiItem(apiMapping, name, m);
				}
			}
		}
	}
	
	private void addApiItem(ApiMapping apiMapping, String beanName, Method method ) {
		ApiRunnable apiRun = new ApiRunnable();
		apiRun.apiName = apiMapping.value();
		apiRun.targerMethod = method;
		apiRun.targerName = beanName;
		apiMap.put(apiMapping.value(), apiRun);
	}
	
	public ApiRunnable findApiRunnable(String apiName) {
		return apiMap.get(apiName);
	}
	
	public List<ApiRunnable> findApiRunnables(String apiName){
		Assert.notNull(apiName, "api name must not null");
		List<ApiRunnable> list = new ArrayList<ApiRunnable>();
		for(ApiRunnable api : apiMap.values()) {
			if(api.apiName.equals(apiName)) {
				list.add(api);
			}
		}
		return list;
	}
	
	public class ApiRunnable{
		private String apiName;
		
		/** ioc bean名称 */
		private String targerName;
		
		/** 目标方法 */
		private Method targerMethod;
		
		/** UserServiceImpl实例 */
		private Object target;
		
		public Object run(Object... args) throws Exception {
			if(target == null) {
				target = applicationContext.getBean(targerName);
			}
			return targerMethod.invoke(target, args);
		}
		
		public Class<?>[] getParamTypes(){ return targerMethod.getParameterTypes(); }

		public String getApiName() { return apiName; }

		public String getTargerName() { return targerName; }

		public Method getTargerMethod() {
			return targerMethod;
		}

		public Object getTarget() { return target; }
		
		
	}
	
	
}
