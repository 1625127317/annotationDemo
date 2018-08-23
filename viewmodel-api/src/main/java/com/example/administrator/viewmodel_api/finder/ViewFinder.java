package com.example.administrator.viewmodel_api.finder;

import android.content.Context;
import android.view.View;

/**
 * @author ZSK
 * @date 2018/8/18
 * @function
 */
public class ViewFinder implements Finder {
    @Override
    public Context getContext(Object source) {
        return ((View) source).getContext();
    }

    @Override
    public View findView(Object source, int id) {
        return ((View) source).findViewById(id);
    }
}
