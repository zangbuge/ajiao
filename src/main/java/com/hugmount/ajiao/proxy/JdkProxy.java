package com.hugmount.ajiao.proxy;

/**
 * @author Li Huiming
 * @date 2022/3/12
 */
public class JdkProxy {

    public Object createProxy(Class<?> clazz) {
        System.out.println("test starter");
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
