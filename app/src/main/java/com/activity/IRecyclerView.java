package com.activity;

/**
 * Created by lyf on 2016/6/17.
 */
public interface IRecyclerView<T> {
    BaseRecyclerAdapter<T> newAdapter();
    ItemViewCreator<T> configItemViewCreator();
}
