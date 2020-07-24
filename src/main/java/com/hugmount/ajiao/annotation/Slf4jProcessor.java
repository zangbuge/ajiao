package com.hugmount.ajiao.annotation;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Set;

/**
 * @Author: Li Huiming
 * @Date: 2020/6/5
 */

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.hugmount.ajiao.annotation.Slf4j")
@AutoService(Processor.class)
public class Slf4jProcessor extends AbstractBaseProcessor {

//    private static final Logger log = LoggerFactory.getLogger(Test.class);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Slf4j.class);
        for (Element element : elements) {
            ElementKind kind = element.getKind();
            if (ElementKind.CLASS != kind) {
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            Slf4j annotation = element.getAnnotation(Slf4j.class);
            String varName = annotation.varName();
            Name name = names.fromString(varName);

            ArrayList<JCTree.JCExpression> newArgs = new ArrayList<>();
            String curclazz = typeElement.getQualifiedName().toString() + ".class";
            JCTree.JCExpression jcExpression = memberAccess(curclazz);
            newArgs.add(jcExpression);

            JCTree.JCMethodInvocation apply = treeMaker.Apply(List.nil()
                    ,memberAccess(LoggerFactory.class.getCanonicalName() + ".getLogger")
                    , List.from(newArgs));

            // Flags的值是按二进制设计的 用法示例：treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL);
            JCTree.JCModifiers modifiers = treeMaker.Modifiers(Flags.PUBLIC + Flags.STATIC + Flags.FINAL);
            JCTree.JCIdent ident = treeMaker.Ident(elementUtils.getTypeElement(Logger.class.getCanonicalName()));
            JCTree jcVariableDecl = treeMaker.VarDef(modifiers, name, ident, apply);

            JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) elementUtils.getTree(element);
            ArrayList<JCTree> arrayList = new ArrayList<>();
            arrayList.add(jcVariableDecl);
            arrayList.addAll(jcClassDecl.defs);
            List<JCTree> from = List.from(arrayList);
            jcClassDecl.defs = from;

        }
        return true;
    }


    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }

}
