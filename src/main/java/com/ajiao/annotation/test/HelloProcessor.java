package com.ajiao.annotation.test;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @Author Li Huiming
 * @Date 2020/1/17
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.ajiao.annotation.test.Hello")
@AutoService(Processor.class)
public class HelloProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 获取所有被注解类(元素)
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Hello.class)) {
            // 被注解对象的类型
            ElementKind kind = element.getKind();
            if (ElementKind.CLASS == kind) {
                TypeElement typeElem = (TypeElement) element;
                dealTypeElement(typeElem);
            }
        }
        // 返回true ,表示已处理, 不会被其他注解处理器处理
        return true;
    }


    /**
     * 处理被注解类
     *
     * @param element
     */
    public void dealTypeElement(TypeElement element) {
        Filer filer = processingEnv.getFiler();
        try {
            StringBuilder sb = new StringBuilder();
            // getQualifiedName() 包全路径
            PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
            String qualifiedName = packageElement.getQualifiedName().toString();
            sb.append(" 包全路径 qualifiedName: " + qualifiedName);
            // 类名
            Name simpleName = element.getSimpleName();
            sb.append(" 类名 simpleName: " + simpleName);

            //获取注解元数据
            Hello hello = element.getAnnotation(Hello.class);

            // 创建main方法
            MethodSpec main = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "自动创建的" + sb.toString())
                    .build();

            // 创建HelloWorld类
            TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(main)
                    .build();

            String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
            JavaFile javaFile = JavaFile.builder(packageName, helloWorld)
                    .addFileComment(" This codes are generated automatically. Do not modify!")
                    .build();

            javaFile.writeTo(filer);

        } catch (Exception e) {
            // 打印
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

    }


}
