package com.fang.service;

import com.fang.spring.ComponentScan;
//通过注解获得扫描路径
@ComponentScan("com.fang.service")
public class AppConfig {
    public static void main(String[] args) {
        System.out.println("66666");
    }
}
