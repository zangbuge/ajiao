package com.ajiao.annotation.test;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
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

            List<MethodSpec> methodSpecList = new ArrayList<>();
            List<? extends Element> enclosedElements = element.getEnclosedElements();
            for (Element item : enclosedElements) {
                if (ElementKind.METHOD == item.getKind()) {
                    ExecutableElement executableElement = (ExecutableElement) item;
                    List<? extends VariableElement> parameters = executableElement.getParameters();
                    TypeMirror returnType = executableElement.getReturnType();
                    Set<Modifier> modifiers = executableElement.getModifiers();
                    // 修饰符
                    for (Modifier modifier : modifiers) {
                        String s = modifier.name().toString();
                        sb.append(s).append(" ");
                    }
                    // 返回值
                    sb.append(returnType.toString()).append(" ");

                    // 方法名
                    String methodName = executableElement.getSimpleName().toString();
                    sb.append(methodName).append(" ");

                    List<ParameterSpec> list = new ArrayList<>();
                    // 参数
                    for (VariableElement variableElement : parameters) {
                        Element enclosingElement = variableElement.getEnclosingElement();

                        // 参数类型(全路径类型)
                        String name = enclosingElement.asType().toString();
                        sb.append(name).append(" ");

                        // 参数变量名
                        String simpleName1 = variableElement.getSimpleName().toString();
                        sb.append(simpleName1).append(" ");

                        String classPath = enclosingElement.asType().toString();
                        int index = classPath.lastIndexOf(")");
                        String subPath = classPath.substring(index + 1);
                        ParameterSpec parameterSpec = ParameterSpec.builder(Class.forName(subPath), simpleName1).build();
                        list.add(parameterSpec);

                    }

                    // 创建方法
                    MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(Class.forName(returnType.toString()))
                            .addParameters(list)
                            .build();

                    methodSpecList.add(methodSpec);
                }
                sb.append("    ");
            }
            // 创建IHelloWorld类
            TypeSpec iHelloWorld = TypeSpec.interfaceBuilder("IHelloWorld")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethods(methodSpecList)
                    .build();

            String packageName1 = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
            JavaFile javaFile1 = JavaFile.builder(packageName1, iHelloWorld)
                    .addFileComment(" This codes are generated automatically. Do not modify!")
                    .build();
            javaFile1.writeTo(filer);

            // ----------------------demo----------------------
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
