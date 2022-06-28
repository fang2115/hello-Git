package com.fang.service;

import com.fang.spring.BeanNameAware;
import com.fang.spring.Component;

@Component("userServiceBean")
public class UserService implements BeanNameAware {
    private String beanName;

    public void setBeanName(String beanName) {
         this.beanName=beanName;
    }
}
