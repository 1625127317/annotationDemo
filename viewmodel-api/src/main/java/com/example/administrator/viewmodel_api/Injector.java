package com.example.administrator.viewmodel_api;

import com.example.administrator.viewmodel_api.finder.Finder;

/**
 * @author ZSK
 * @date 2018/8/18
 * @function
 */
public interface Injector<T> {

    /**
     * @param host    目标
     * @param source  来源
     * @param finder
     */
    void inject(T host, Object source, Finder finder);
}
