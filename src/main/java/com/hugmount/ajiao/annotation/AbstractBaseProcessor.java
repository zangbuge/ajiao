package com.hugmount.ajiao.annotation;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * @Author: Li Huiming
 * @Date: 2020/7/21
 */
public abstract class AbstractBaseProcessor extends AbstractProcessor {

    protected Context context;

    protected TreeMaker treeMaker;

    protected Names names;

    protected JavacElements elementUtils;

    protected Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        elementUtils = (JavacElements) processingEnv.getElementUtils();
        messager = processingEnvironment.getMessager();
    }

}
