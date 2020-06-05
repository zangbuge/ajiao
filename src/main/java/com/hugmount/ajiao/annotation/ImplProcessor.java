package com.hugmount.ajiao.annotation;

import com.google.auto.service.AutoService;
import com.hugmount.ajiao.util.ProcessorUtil;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @Author: Li Huiming
 * @Date: 2020/6/5
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.hugmount.ajiao.annotation.Impl")
@AutoService(Processor.class)
public class ImplProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        final TreeMaker treeMaker = TreeMaker.instance(context);
        final JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Impl.class);
        for (Element element : elements) {
            ElementKind kind = element.getKind();
            if (ElementKind.CLASS != kind) {
                continue;
            }
            Impl annotation = element.getAnnotation(Impl.class);
            String annotationName = annotation.interfaceName();
            PackageElement packagePath = elementUtils.getPackageOf(element);
            String simpleName = element.getSimpleName().toString();
            String interfaceName = ProcessorUtil.getInterfaceName(simpleName, annotationName);
            String interfacePath = packagePath + "." + interfaceName;
            // 添加实现类
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) elementUtils.getTree(element);
            jcClassDecl.implementing = List.of(treeMaker.Ident(elementUtils.getTypeElement(interfacePath)));
        }
        return true;
    }

}
