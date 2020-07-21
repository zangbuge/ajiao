package com.hugmount.ajiao.annotation;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @Author: Li Huiming
 * @Date: 2020/6/5
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.hugmount.ajiao.annotation.Slf4j")
@AutoService(Processor.class)
public class Slf4jProcessor extends AbstractBaseProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Slf4j.class);
        for (Element element : elements) {
            ElementKind kind = element.getKind();
            if (ElementKind.CLASS != kind) {
                continue;
            }
            Slf4j annotation = element.getAnnotation(Slf4j.class);
            String varName = annotation.varName();
            JCTree.JCLiteral methodName = treeMaker.Literal("hello String 类型的值");
            Name name = names.fromString(varName);
            JCTree.JCIdent ident = treeMaker.Ident(elementUtils.getTypeElement(String.class.getCanonicalName()));
            JCTree.JCVariableDecl jcVariableDecl = treeMaker.VarDef(treeMaker.Modifiers(Flags.PUBLIC), name, ident, methodName);
            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) elementUtils.getTree(element);
            jcClassDecl.defs = List.of(jcVariableDecl);

        }
        return true;
    }



}
