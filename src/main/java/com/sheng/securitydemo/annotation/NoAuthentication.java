package com.sheng.securitydemo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明不用拦截的接口
 **/
@Target({ElementType.TYPE, ElementType.METHOD}) //该注解可以用在类上或者方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAuthentication {
}