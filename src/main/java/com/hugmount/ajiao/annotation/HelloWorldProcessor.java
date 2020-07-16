package com.hugmount.ajiao.annotation;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @Author: Li Huiming
 * @Date: 2020/5/31
 */
@SupportedAnnotationTypes("com.hugmount.ajiao.annotation.HelloWorld")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class HelloWorldProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        final TreeMaker treeMaker = TreeMaker.instance(context);
        final JavacElements elementUtils = (JavacElements) processingEnv.getElementUtils();

        for (Element element : roundEnv.getElementsAnnotatedWith(HelloWorld.class)) {
            // 获取原文件class的所有方法
            JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) elementUtils.getTree(element);
            // body即为方法体 利用treemaker的Block方法获取到一个新方法体将原来的替换掉。
            // 第一个参数访问标志, 重点要关注的是第二个参数，也就是具体的方法体内容
            // 它是一个List类型的参数，List里面每一个元素就代表一个语句块
            jcMethodDecl.body = treeMaker.Block(0, List.of(
                    // 织入代码块用treeMaker.Exec实现
                    treeMaker.Exec(
                            // Apply用于创建方法调用语法树节点参数:  typeargs：参数类型列表, fn：调用语句, args：参数列表
                            // 这里面用到了两个方法，一个是treeMaker.Select（生成具体的方法），一个是treeMaker.Literal（方法的参数）
                            // .treeMaker.Select里面套了很多层, 多级方法的第一级以treeMaker.Ident开始，然后一层套一层，直到整个方法结束。
                            treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(
                                            treeMaker.Select(
                                                    treeMaker.Ident(
                                                            elementUtils.getName("System")
                                                    ),
                                                    elementUtils.getName("out")
                                            ),
                                            elementUtils.getName("println")
                                    ),
                                    List.of(treeMaker.Literal("Hello World "))
                            )
                    ),
                    // 方法的原代码块
                    jcMethodDecl.body
            ));
        }
        return true;
    }
}
