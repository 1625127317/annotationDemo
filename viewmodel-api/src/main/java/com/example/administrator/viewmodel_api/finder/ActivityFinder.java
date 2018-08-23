package com.example.administrator.viewmodel_api.finder;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * @author ZSK
 * @date 2018/8/18
 * @function
 */
public class ActivityFinder implements Finder {
    @Override
    public Context getContext(Object source) {
        return (Activity)source;
    }

    @Override
    public View findView(Object source, int id) {
        return ((Activity)source).findViewById(id);
    }
}
