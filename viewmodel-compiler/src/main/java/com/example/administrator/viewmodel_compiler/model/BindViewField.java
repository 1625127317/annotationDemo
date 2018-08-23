package com.example.administrator.viewmodel_compiler.model;

import com.example.administrator.viewmodel_annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author ZSK
 * @date 2018/8/15
 * @function  被BindView注解标记的字段的模型类
 */
public class BindViewField {

    private VariableElement mFieldElement;

    private int mResId;

    public BindViewField(Element element) {
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only field can be annotated with @%s",
                    BindView.class.getSimpleName()));
        }
        mFieldElement = (VariableElement) element;
        BindView bindView = mFieldElement.getAnnotation(BindView.class);
        mResId = bindView.value();
        if (mResId < 0) {
            throw new IllegalArgumentException(String.format("value() in %s for field %s is not valid",
                    BindView.class.getSimpleName(),mFieldElement.getSimpleName()));
        }
    }

    public Name getFileName() {
        return mFieldElement.getSimpleName();
    }

    public int getResId() {
        return mResId;
    }

    //TypeMirror 指的是java中的对应的类型
    public TypeMirror getFieldType() {
        return mFieldElement.asType();
    }

}
