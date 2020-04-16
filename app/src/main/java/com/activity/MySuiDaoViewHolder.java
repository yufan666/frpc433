package com.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.greendao.SuiDao;
import com.frpc.R;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Administrator on 2018/7/18.
 */

public class MySuiDaoViewHolder extends BaseViewHolder<SuiDao> {
    public static final int LAYOUT_RES = R.layout.item_my_suidao;
    @Bind(R.id.circle)public ImageView circleBox;
    @Bind(R.id.name)public TextView nameTv;
    @Bind(R.id.time)public TextView timeTv;
    @Bind(R.id.info)public TextView infoTv;
    @Bind(R.id.link)public Button linkBt;
    private String link;
    private String type;
    public MySuiDaoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBindData(SuiDao bean, int position) {
        nameTv.setText(bean.name);
        timeTv.setText(bean.user+"："+bean.getIp()+":"+bean.getPort());
        if(bean.getUser().equals("tcp") || bean.getUser().equals("udp")){
            infoTv.setText("远程端口号："+bean.getType());
        }else{
            infoTv.setText("域名："+bean.getLink());
        }
        link = bean.getLink();
        type = bean.getUser();
    }


    @OnClick(R.id.link)
    public void link(){
        String remote_string="";
        remote_string = nameTv.getText()+"\r\n"+timeTv.getText()+"\r\n"+infoTv.getText();
        ClipboardManager cm = (ClipboardManager) MyApplication.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", remote_string);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(MyApplication.mContext,"复制成功",Toast.LENGTH_SHORT).show();
    }
}
