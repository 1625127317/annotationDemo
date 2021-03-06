package com.example.administrator.viewmodel_compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author ZSK
 * @date 2018/8/15
 * @function
 */
public class AnnotatedClass {
    /**
     * 类名
     */
    public TypeElement mClassElement;

    /**
     * 成员变量
     */
    public List<BindViewField> mFiled;

    /**
     * 方法
     */
    public List<OnClickMethod> mMethod;

    /**
     * 元素辅助类
     */
    public Elements mElementUtils;

    public AnnotatedClass(TypeElement mClassElement, Elements mElementUtils) {
        this.mClassElement = mClassElement;
        this.mElementUtils = mElementUtils;
        this.mFiled = new ArrayList<>();
        this.mMethod = new ArrayList<>();
    }

    //返回此类型元素的完全限定名称
    public String getFullClassName() {
        return mClassElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFiled.add(field);
    }

    public void addMethod(OnClickMethod method) {
        mMethod.add(method);
    }

    public JavaFile generateFinder() {
        /**
         * 构建方法
         */
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mClassElement.asType()), "host", Modifier.FINAL)
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeUtil.FINDER, "finder");

        /**
         * 遍历添加类成员
         */
        for (BindViewField field : mFiled) {
            methodBuilder.addStatement("host.$N=($T)finder.findView(source,$L)"
                    , field.getFileName(), ClassName.get(field.getFieldType()), field.getResId());
        }

        /**
         * 声明Listener
         */
        if (mMethod.size() > 0) {
            methodBuilder.addStatement("$T listener", TypeUtil.ONCLICK_LISTENER);
        }

        for (OnClickMethod method : mMethod) {
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtil.ONCLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override.class)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(TypeUtil.ANDROID_VIEW, "view")
                            .addStatement("host.$N()", method.getMethodName())
                            .build())
                    .build();
            methodBuilder.addStatement("listener = $L ",listener);
            for (int id : method.ids) {
                methodBuilder.addStatement("finder.findView(source,$L).setOnClickListener(listener)", id);
            }
        }

        String packageName = getPackageName(mClassElement);
        String className = getClassName(mClassElement,packageName);
        ClassName bindClassName = ClassName.get(packageName,className);

        /**
         * 构建类
         */
        TypeSpec finderClass = TypeSpec.classBuilder(bindClassName.simpleName()+"$$Injector")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJECTOR,TypeName.get(mClassElement.asType())))
                .addMethod(methodBuilder.build())
                .build();

        return JavaFile.builder(packageName,finderClass).build();
    }

    private String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace(".","$");
    }

    private String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

}
