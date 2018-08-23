package com.example.administrator.viewmodel_api;

import android.support.annotation.UiThread;

/**
 * @author ZSK
 * @date 2018/8/18
 * @function
 */
public interface Unbinder {

    @UiThread
    void unbind();

    Unbinder EMPY = new Unbinder() {
        @Override
        public void unbind() {

        }
    };
}
