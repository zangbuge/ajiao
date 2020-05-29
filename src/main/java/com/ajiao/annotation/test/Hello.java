package com.ajiao.annotation.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 1. SOURCE：在原文件中有效，被编译器丢弃。 2. CLASS：在class文件有效
 *  SOURCE一般用于标记，比如javadoc，或为了其他人易于理解你的程序，如@Override。面向应用层开发者
 * @Author Li Huiming
 * @Date 2020/1/17
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Hello {

    String value();

}
