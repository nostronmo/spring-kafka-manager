package com.nostronmo.manage.instancer.global;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.nostronmo.manage.instancer.exception.server.InternalException;

@Lazy(false)
@Component
public class ContextProvider implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static <T> T getBean(Class<T> beanClass) {
		ensureContext();
		return context.getBean(beanClass);
	}
	
	public static <T> T getBean(String name, Class<T> beanClass) {
		ensureContext();
		return context.getBean(name, beanClass);
	}

	public static <T> List<String> getBeanNamesOfType(Class<T> beanClass) {
		ensureContext();
		return List.of(context.getBeanNamesForType(beanClass));
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> beanClass) {
		ensureContext();
		return context.getBeansOfType(beanClass);
	}

	private static void ensureContext() {
		if (context == null) {
			throw new InternalException("ApplicationContext has not been initialized yet.");
		}
	}

}
