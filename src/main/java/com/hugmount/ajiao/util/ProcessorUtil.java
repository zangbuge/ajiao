package com.hugmount.ajiao.util;

/**
 * @Author: Li Huiming
 * @Date: 2020/6/6
 */
public class ProcessorUtil {

    public static String getInterfaceName(String implName, String annotationName) {
        if (null == annotationName || "".equals(annotationName.trim())) {
            String interfaceName = null;
            if (implName.indexOf("Impl") > 0) {
                interfaceName = implName.replace("Impl", "");
            }
            else {
                interfaceName = "I" + implName;
            }
            return interfaceName;
        }
        else {
            return annotationName;
        }
    }


}
