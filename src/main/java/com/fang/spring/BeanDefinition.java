package com.fang.spring;

public class BeanDefinition {
  private   Class  Type;
  private   String scope;

    public Class getType() {
        return Type;
    }

    public void setType(Class type) {
        Type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
