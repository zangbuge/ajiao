package com.ajiao.annotation.test;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * @Author Li Huiming
 * @Date 2020/1/17
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.ajiao.annotation.test.Hello")
@AutoService(Processor.class)
public class HelloProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;

    private static final String HELLO_TEMPLATE =
            "package %1$s;\n\npublic class %2$sHello {\n  public static void sayHello() {\n    System.out.println(\"Hello %3$s %4$s\");\n  }\n}\n";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(Hello.class)) {
            TypeElement typeElem = (TypeElement) element;
            String typeName = typeElem.getQualifiedName().toString();
            Filer filer = processingEnv.getFiler();
            try (Writer sw = filer.createSourceFile(typeName + "Hello").openWriter()) {
                log("Generating " + typeName + "Hello source code");
                note("Generating " + typeName + "Hello source code");
                StringBuilder sb = new StringBuilder();
                // 被注解对象的类型
                ElementKind kind = element.getKind();
                sb.append("kind: " + kind);
                // getQualifiedName()包全路径
                PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
                Name qualifiedName = packageElement.getQualifiedName();
                sb.append("qualifiedName: " + qualifiedName);
                //获取注解元数据
                Hello hello = element.getAnnotation(Hello.class);

                //编译期间在Gradle console可查看打印信息
                note(sb.toString());
                int lastIndex = typeName.lastIndexOf('.');
                sw.write(String.format(HELLO_TEMPLATE, typeName.substring(0, lastIndex), typeName.substring(lastIndex + 1), typeName, sb.toString()));
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }


        }
        return true;
    }

    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void log(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

}
