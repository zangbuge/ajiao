package com.hugmount.ajiao;

/**
 * @Author Li Huiming
 * @Date 2020/1/20
 */
public class Test {
    public static void main(String[] args) {
        try {
            String [] str = new String[1];
            Class<? extends String[]> aClass = str.getClass();
            System.out.println("数组"+String[].class);
            System.out.println("字符串"+String.class);
            System.out.println(aClass.getName());
            Class<String[]> aClass1 = String[].class;
            System.out.println("hello: " + aClass1.getName());

            String type = "java.lang.String[]";
            String substring = type.substring(type.indexOf("["));
            String substring1 = type.substring(0, type.indexOf("["));

            System.out.println(substring);
            System.out.println(substring1);
            Class<?> aClass2 = Class.forName("[Ljava.lang.String;");
            System.out.println(aClass2.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
