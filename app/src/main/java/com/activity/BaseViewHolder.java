package com.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;


/**
 * Created by lyf on 2016/6/15.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements IItemView<T> {
    private int position;
    private View.OnClickListener mOnClickListener;
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBindView(View view) {
        ButterKnife.bind(this,view);
    }

    public void setPosition(int position){
        this.position = position;
    }

    @Override
    public int position(){
        return this.position;
    }

    @Override
    public View getConvertView(){
        return this.itemView;
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.mOnClickListener = listener;
    }

    public View.OnClickListener getOnClickListener(){
        return mOnClickListener;
    }

}
