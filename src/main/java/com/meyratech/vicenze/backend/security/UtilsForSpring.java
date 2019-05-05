package com.meyratech.vicenze.backend.security;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * enbiya on 15.11.2018
 */

@Component
public class UtilsForSpring implements ApplicationContextAware {

    private static ApplicationContext context;

    public static <T> T getSingleBeanOfType(Class<T> beanClass) {
        return context.getBeansOfType(beanClass).values().iterator().next();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        UtilsForSpring.context = context;
    }
}
