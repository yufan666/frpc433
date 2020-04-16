package com.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lyf on 2016/6/15.
 */
public interface ItemViewCreator<T> {
    View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType);

    IItemView<T>  newItemView(View view, int viewType);
}
