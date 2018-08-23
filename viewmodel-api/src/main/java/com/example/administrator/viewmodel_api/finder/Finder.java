package com.example.administrator.viewmodel_api.finder;

import android.content.Context;
import android.view.View;

/**
 * @author ZSK
 * @date 2018/8/18
 * @function
 */
public interface Finder {
    /**
     * 根据source获取Context
     * @param source  Context来源，Activity,View
     * @return
     */
    Context getContext(Object source);

    /**
     * 根据id找控件
     * @param source
     * @param id
     * @return
     */
    View findView(Object source,int id);
}
