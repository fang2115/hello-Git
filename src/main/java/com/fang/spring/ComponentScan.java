package com.fang.spring;

import jdk.nashorn.internal.ir.annotations.Reference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//该注解生效时间
@Retention(RetentionPolicy.RUNTIME)
//注解写的位置
@Target(ElementType.TYPE)
public @interface ComponentScan {

    String value() default "";
}