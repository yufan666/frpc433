package com.activity;

import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;

import com.frpc.R;


/**
 * Created by lyf on 2016/6/22.
 */

public abstract class BaseRecyclerViewSwipeRefreshActivity<T> extends BaseRecyclerViewActivity<T> implements
       SwipeRefreshLayout.OnRefreshListener{
//    @Bind(R.id.swipe_refresh_widget)
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
//        initView();
    }

    protected void initView(){
        super.initView();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_widget);
        setupSwipeRefreshLayout();
    }

    protected void setupSwipeRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        requestData();
    }

    protected void onRefreshFinish() {
        if(pageIndex == 1){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
