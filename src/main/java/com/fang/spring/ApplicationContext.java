package com.fang.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import static sun.awt.image.PixelConverter.Argb.instance;

public class ApplicationContext {
    private Class appConfig;

    private ConcurrentHashMap<String, BeanDefinition> concurrenMap = new ConcurrentHashMap();

    private ConcurrentHashMap<String, Object> concurrenSingletonMap = new ConcurrentHashMap();

    public ApplicationContext(Class appConfig) throws Exception {
        appConfig = appConfig;
        //判断注解是否在该类上
        if (appConfig.isAnnotationPresent(ComponentScan.class)) {
            //获取注解
            ComponentScan annotation = (ComponentScan) appConfig.getAnnotation(ComponentScan.class);
            //获取注解的value得到扫描路径,但是该路径不是不是真正需要的路径
            String value = annotation.value();  //com.fang.service
            //将.替换为/添加到文件的路径后面找到编译后文件
            String replace = value.replace(".", "/");
            //获取类加载器
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            //获取该路径文件的绝对路径
            URL resource = classLoader.getResource(replace);
            //C:\Users\fang\Desktop\web%e6%a1%86%e6%9e%b6%e6%95%b4%e7%90%86.2022.4.24\spring\target\classes\com\fang\service
            File file = new File(resource.getFile());
            File[] files1 = file.listFiles();
            System.out.println();
            //判断是否是文件夹
            if (file.isDirectory()) {
                //获取文件里的全部内容
                File[] files = file.listFiles();
                for (File afile : files) {
                    //得到里面所有文件的绝对路径
                    String absolutePath = afile.getAbsolutePath();
                    //只需要后缀为class文件的
                    if (absolutePath.endsWith(".class")) {
                        //思路:判断一个类是否为bean,需要判断该类有没有component注解
                        //根据类加载器反射获取类的内容,信息
                        //而类加载器需要名为权限列明格式为:com.fang.service.UserService
                        String com = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                        String replace1 = com.replace("\\", ".");
                        try {
                            Class<?> aClass = classLoader.loadClass(replace1);
                            if (aClass.isAnnotationPresent(Component.class)) {
                                String componentValue = aClass.getAnnotation(Component.class).value();
                                if (componentValue.equals("")) {
                                    //如果为默认则将类名小写并写入beanName
                                    componentValue = Introspector.decapitalize(aClass.getName());
                                }
                                //Bean
                                BeanDefinition beanDefinition = new BeanDefinition();
                                //判断其作用域默认为单例模式
                                if (aClass.isAnnotationPresent(Scope.class)) {
                                    //多 例
                                    String scopeValue = aClass.getAnnotation(Scope.class).value();
                                    beanDefinition.setScope(scopeValue);
                                } else {
                                    //单例
                                    beanDefinition.setScope("singleton");
                                }
                                //通过DefinitionMap存储配置的Bean
                                BeanDefinition put = concurrenMap.put(componentValue, beanDefinition);

                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
            //单独获取出单例bean,将其放入单例池concurrenSingletonMap
            for (String beanName : concurrenMap.keySet()) {
                BeanDefinition definition = concurrenMap.get(beanName);
                if (definition.getScope().equals("singleton")) {
                    Object bean = createBean(beanName, definition);
                    concurrenSingletonMap.put(beanName, bean);
                }

            }
        }
    }

    private Object createBean(String beanName, BeanDefinition definition) throws Exception {
        //获取bean的类,再通过反射构造函数创建bean对象返回
        Class type = definition.getType();
        Object bean = type.getConstructor().newInstance();
        //依赖注入
        for (Field f : type.getDeclaredFields()) {
            //获取有Autowired注解的
            if (f.equals(Autowired.class)) {
                f.setAccessible(true);
                //给属性赋值
                f.set(bean, getBean(f.getName()));
            }
        }
        //回调,BeanName
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware)instance).setBeanName(beanName);
        }
        return bean;
    }

    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = concurrenMap.get(beanName);
        if (beanDefinition != null) {
            //判断是否是单例Bean
            if (beanDefinition.getScope().equals("singleton")) {
                //如果是则在单例池内获取
                Object bean = concurrenSingletonMap.get(beanName);
                if (bean == null) {
                    //再次实例化
                    Object singletonBean = createBean(beanName, beanDefinition);
                    concurrenSingletonMap.put(beanName, singletonBean);
                }
                //返回单例
                return bean;
            } else {
                //不为单例,直接重新实例化
                return createBean(beanName, beanDefinition);

            }
        }


        return null;
    }


}
