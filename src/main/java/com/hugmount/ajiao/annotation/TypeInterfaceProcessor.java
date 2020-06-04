package com.hugmount.ajiao.annotation;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 实现了自动生成接口
 * @Author Li Huiming
 * @Date 2020/1/17
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.hugmount.ajiao.annotation.TypeInterface")
@AutoService(Processor.class)
public class TypeInterfaceProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 获取所有被注解的类
        for (Element element : roundEnvironment.getElementsAnnotatedWith(TypeInterface.class)) {
            // 被注解对象的类型
            ElementKind kind = element.getKind();
            if (ElementKind.CLASS == kind) {
                TypeElement typeElem = (TypeElement) element;
                dealTypeElement(typeElem);
            }
        }
        // 返回true ,表示已处理, 将不再会被其他注解处理器处理
        return true;
    }


    /**
     * 处理被注解类
     *
     * @param element
     */
    public void dealTypeElement(TypeElement element) {
        final Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        final TreeMaker treeMaker = TreeMaker.instance(context);
        final JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();

        Filer filer = processingEnv.getFiler();
        try {
            StringBuilder sb = new StringBuilder();
            // getQualifiedName() 包全路径
            PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
            String qualifiedName = packageElement.getQualifiedName().toString();
            sb.append(" 包全路径 qualifiedName: " + qualifiedName);
            // 类名
            String simpleName = element.getSimpleName().toString();
            sb.append(" 类名 simpleName: " + simpleName);
            // 获取注解元数据
            TypeInterface annotation = element.getAnnotation(TypeInterface.class);
            String annotationName = annotation.name();
            if (null == annotationName || "".equals(annotationName.trim())) {
                if (simpleName.indexOf("Impl") > 0) {
                    simpleName = simpleName.replace("Impl","");
                }
                else {
                    simpleName = "I" + simpleName;
                }
            }
            else {
                simpleName = annotationName;
            }
            // 创建接口类
            TypeSpec typeSpec = TypeSpec.interfaceBuilder(simpleName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethods(getMethodSpecList(element))
                    .build();
            // 创建包路径
            String packagePath = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
            JavaFile javaFile1 = JavaFile.builder(packagePath, typeSpec)
                    .addFileComment(" This codes are generated automatically. Do not modify!")
                    .build();
            // 写入文件
            javaFile1.writeTo(filer);

            // 修改原始class, 添加实现接口
            String iName = packagePath + "." + simpleName;
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) elementUtils.getTree(element);
//            jcClassDecl.implementing = com.sun.tools.javac.util.List.of(treeMaker.Ident(elementUtils.getTypeElement("com.hugmount.helloboot.core.annotation.IHello")));
            jcClassDecl.implementing = com.sun.tools.javac.util.List.of(treeMaker.Ident(elementUtils.getTypeElement(iName)));

            // ----------------------demo----------------------
            // 创建main方法
            MethodSpec main = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(void.class)
                    .addParameter(String[].class, "args")
                    .addStatement("$T.out.println($S)", System.class, "自动创建的" + sb.toString())
                    .build();

            // 创建HelloWorld类
            TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld" + simpleName)
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


    /**
     * 获取方法列表
     * @param element
     * @return
     */
    public List<MethodSpec> getMethodSpecList(TypeElement element) throws ClassNotFoundException {
        List<MethodSpec> methodSpecList = new ArrayList<>();
        // 被注解类的所有元素
        List<? extends Element> enclosedElements = element.getEnclosedElements();
        for (Element item : enclosedElements) {
            if (ElementKind.METHOD == item.getKind()) {
                ExecutableElement executableElement = (ExecutableElement) item;
                // 修饰符
                Set<Modifier> modifiers = executableElement.getModifiers();
                // 返回值
                TypeMirror returnType = executableElement.getReturnType();
                // 方法名
                String methodName = executableElement.getSimpleName().toString();
                // 参数
                List<ParameterSpec> list = new ArrayList<>();
                List<? extends VariableElement> parameters = executableElement.getParameters();
                for (VariableElement variableElement : parameters) {
                    Element enclosingElement = variableElement.getEnclosingElement();
                    // 参数类型(全路径类型)
                    String classPath = enclosingElement.asType().toString();
                    int index = classPath.lastIndexOf(")");
                    String subPath = classPath.substring(index + 1);
                    // 参数变量名
                    String variableName = variableElement.getSimpleName().toString();
                    // 构建方法参数列表
                    ParameterSpec parameter = ParameterSpec.builder(Class.forName(subPath), variableName).build();
                    list.add(parameter);
                }

                // 创建方法
                MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(Class.forName(returnType.toString()))
                        .addParameters(list)
                        .build();

                // 构建方法列表
                methodSpecList.add(methodSpec);
            }
        }
        return methodSpecList;
    }


}
