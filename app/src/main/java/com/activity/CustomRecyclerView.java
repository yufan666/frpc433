package com.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


/**
 * Created by lyf on 2016/6/13.
 */
public class CustomRecyclerView extends RecyclerView {
    private LinearLayoutManager mLinearLayoutManager;
//    private DividerItemDecoration mDividerItemDecoration;
    private int lastEndItem;
    private LoadNextPageListener mLoadNextPageListener;
//    private BaseRecyclerAdapter adapter;
    public CustomRecyclerView(Context context) {
        this(context,null);
    }
    public CustomRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLinearLayoutManager = new LinearLayoutManager(context);
        setLayoutManager(mLinearLayoutManager);
//        mDividerItemDecoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL_LIST);
//		addItemDecoration(mDividerItemDecoration);
        addOnScrollListener(mOnScrollListener);
    }

    public RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener(){
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView,
                                         int newState) {
            int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            if (newState == RecyclerView.SCROLL_STATE_IDLE && getAdapter() != null){
                int lastPosition = getAdapter().getItemCount() - 1;
                if(lastVisiblePosition == lastPosition){
                    if(getAdapter() instanceof BaseRecyclerAdapter){
                        BaseRecyclerAdapter adapter = (BaseRecyclerAdapter)getAdapter();
                        if(mLoadNextPageListener != null  && lastVisiblePosition != lastEndItem && adapter.isShowFooter()){
                            mLoadNextPageListener.onLoadNextPage();
                        }
                    }


                }
                lastEndItem = lastVisiblePosition;
            }
        }
    };

    public void setLoadNextPageListener(LoadNextPageListener l){
        this.mLoadNextPageListener = l;
    }
    public interface LoadNextPageListener {
        void onLoadNextPage();
    }
}
