package com.activity;

import android.view.View;

/**
 * Created by lyf on 2016/6/15.
 */
public interface IItemView<T> {

    void onBindView(View view);

    void onBindData(T bean, int position);

//    int size();

    int position();

    View getConvertView();
}
