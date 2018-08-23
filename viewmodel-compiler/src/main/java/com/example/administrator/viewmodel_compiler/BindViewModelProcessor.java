package com.example.administrator.viewmodel_compiler;

import com.example.administrator.viewmodel_annotation.BindView;
import com.example.administrator.viewmodel_annotation.OnClick;
import com.example.administrator.viewmodel_compiler.model.AnnotatedClass;
import com.example.administrator.viewmodel_compiler.model.BindViewField;
import com.example.administrator.viewmodel_compiler.model.OnClickMethod;
import com.google.auto.service.AutoService;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author ZSK
 * @date 2018/8/15
 * @function  注解处理器 ：调用时机是在javac开始编译之前介入的
 *   1、运行时注解处理器：借助反射机制实现
 *   2、编译时注解处理器：借助APT实现
 *   APT : Advanced Packaging Tool，包管理工具，用于在编译时期扫描和处理注解信息
 *
 *   为了使自定义的注解处理器生效，让java编译器或能够找到自定义的注解处理器我们需要对其进行注册和打包：
 *
 *    方式一、自定义的处理器需要被打成一个jar，并且需要在jar包的META-INF/services路径下中创建一个固定的文件javax.annotation.processing.Processor,
 *   在javax.annotation.processing.Processor文件中需要填写自定义处理器的完整路径名，有几个处理器就需要填写几个
 *
 *   方式二、使用google提供的！AutoService
 *    1》添加依赖compile 'com.google.auto.service:auto-service:1.0-rc2'
 *    2》直接在注解处理器上添加@AutoService注解即可
 */
@AutoService(Processor.class)
public class BindViewModelProcessor extends AbstractProcessor {

    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;

    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;

    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;

    /**
     * 针对每一个类生成一个代理类，例如MainActivity回生成MainActivity$$ViewInjector，则有如下
     *  1、一个类对象，代表具体某个类的代理类生成的全部信息，本例中为AnnotatedClass
     *  2、一个集合，存放上述类对象（用作遍历生成代理类），即是Map<String,AnnotatedClass>
     */
    private Map<String,AnnotatedClass> mAnnotatedClassMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        //返回该注解处理器支持的注解集合
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //返回支持的源码办法
        return SourceVersion.latestSupported();
    }

    /**
     *  1、收集信息
     *  2、生成代理类，就是编译时生成的类
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        //清理一下，防止因为process的多次调用，生成重复的代理类
        mAnnotatedClassMap.clear();
        try {
            processBindView(roundEnv);
            processOnClick(roundEnv);
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
            return true;
        }
        try {
            for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
                info("generating file for %s", annotatedClass.getFullClassName());
                annotatedClass.generateFinder().writeTo(mFiler);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("Generate file failed,reason:%s", e.getMessage());
        }
        return true;
    }

    private void processOnClick(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)){
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            OnClickMethod method = new OnClickMethod(element);
            annotatedClass.addMethod(method);
        }
    }

    private void processBindView(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)){
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            BindViewField field = new BindViewField(element);
            annotatedClass.addField(field);
            System.out.print("p_element=" + element.getSimpleName() + ",p_set=" + element.getModifiers());
        }
    }

    private AnnotatedClass getAnnotatedClass(Element element) {
        TypeElement encloseElement = (TypeElement) element.getEnclosingElement();
        String fullClassName = encloseElement.getQualifiedName().toString();
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(fullClassName);
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(encloseElement,mElementUtils);
            mAnnotatedClassMap.put(fullClassName,annotatedClass);
        }
        return annotatedClass;
    }

    private void error(String msg,Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR,String.format(msg,args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
