package com.fang.service;

import com.fang.spring.ApplicationContext;

public class Test {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);

        Object userName = applicationContext.getBean("userName");

    }
}
