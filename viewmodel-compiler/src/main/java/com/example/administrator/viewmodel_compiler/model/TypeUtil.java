package com.example.administrator.viewmodel_compiler.model;

import com.squareup.javapoet.ClassName;

/**
 * @author ZSK
 * @date 2018/8/18
 * @function
 */
public class TypeUtil {
    public static final ClassName FINDER = ClassName.get("com.example.administrator.viewmodel_api.finder", "Finder");
    public static final ClassName INJECTOR = ClassName.get("com.example.administrator.viewmodel_api", "Injector");
    public static final ClassName ONCLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
}
