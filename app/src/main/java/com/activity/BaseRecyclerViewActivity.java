package com.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;


import com.frpc.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by lyf on 2016/7/3.
 */

public abstract class BaseRecyclerViewActivity<T> extends BaseActivity implements
        IRecyclerView<T>,AdapterView.OnItemClickListener,CustomRecyclerView.LoadNextPageListener{
//    @Bind(R.id.recycleview)
    public CustomRecyclerView mRecyclerView;
//    @Bind(R.id.titleTextView)
    public TextView titleTv;
    protected BaseRecyclerAdapter adapter;
    protected int pageIndex;
    protected List<T> mList = new ArrayList<>();
    private BaseRecyclerAdapter<T> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        initView();
    }

    protected void initView(){
        mRecyclerView = (CustomRecyclerView) findViewById(R.id.recycleview);
        titleTv = (TextView)findViewById(R.id.titleTextView);
        pageIndex = 1;
        mAdapter = newAdapter();
        configRecyclerView();
        mRecyclerView.setLoadNextPageListener(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    protected abstract void requestData();

    protected void configRecyclerView(){}

    public BaseRecyclerAdapter<T> getAdapter(){
        return mAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public BaseRecyclerAdapter<T> newAdapter() {
        return new BaseRecyclerAdapter<T>(this,configItemViewCreator(),mList);
    }

    @Override
    public void onLoadNextPage() {
        requestData();
    }

//    @OnClick(R.id.back)
//    public void back(){
//        finish();
//    }
    protected void onRefreshFinish(){}

    protected void onRefreshSucceed(List<T> list){
        onRefreshFinish();
        if(pageIndex == 1){
            mList.clear();
        }
        mList.addAll(list);
        if(list.size() < 20){
            mAdapter.isShowFooter(false);
        }
        mAdapter.notifyDataSetChanged();
        pageIndex++;
    }

    protected void onRefreshFailed(){}

    protected void showEmptyView(){}

    protected void showNetErroView(){}



}
