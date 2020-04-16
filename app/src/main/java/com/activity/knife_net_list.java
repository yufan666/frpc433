package com.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.activity.greendao.DBSuiDaoHelper;
import com.activity.greendao.HttpUtil;
import com.activity.greendao.SuiDao;
import com.example.lifen.simplehttpserver.HttpTestHandler;
import com.example.lifen.simplehttpserver.ResourceInAssetsHandler;
import com.example.lifen.simplehttpserver.SimpleHttpServer;
import com.example.lifen.simplehttpserver.WebConfiguration;
import com.frpc.R;
import com.hoho.android.usbserial.BuildConfig;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.myactivity.DeviceListActivity;
import com.myactivity.SerialConsoleActivity;
import com.service.Sendservice;
import com.websocket.ServerManager;
import com.websocket.ServerSocket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import frpclib.Frpclib;

public class knife_net_list extends BaseRecyclerViewSwipeRefreshActivity<SuiDao> implements CompoundButton.OnCheckedChangeListener {
    private final String TAG = knife_net_list.class.getSimpleName();




    //    private ArrayAdapter<UsbSerialPort> mAdapter;
    @Bind(R.id.foot_bar)
    View footBarView;
    @Bind(R.id.edit)
    TextView editTv;
    @Bind(R.id.add)
    TextView addTv;
    @Bind(R.id.all_check)
    CheckBox mAllCheck;
    @Bind(R.id.start)
    TextView startTx;
    private List<SuiDao> SuiDaolist;
    long back_button_click_time;
    boolean bBackButtonClick = false;
    private UserLoginTask mAuthTask = null;

    private SimpleHttpServer shs;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            add();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_my_suidao));
        ButterKnife.bind(this);
        mAllCheck.setOnCheckedChangeListener(this);
        WebConfiguration wc = new WebConfiguration();
        wc.setPort(8088);
        wc.setMaxParallels(50);

        shs = new SimpleHttpServer(wc);
        /*
        向 shs 中添加 IRsourceUriHander 实例
         */
        shs.registerResourceHandler(new ResourceInAssetsHandler(this));
        shs.registerResourceHandler(new HttpTestHandler(this));

        shs.startAsync();

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
        if(MyApplication.flag==2) {
            MyApplication.flag=3;
            handler.sendEmptyMessageDelayed(0, 500);

        }
        ServerManager serverManager=new ServerManager();
        serverManager.Start(8089);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(MyApplication.flag==4) {
            MyApplication.flag=5;
            handler.sendEmptyMessageDelayed(0, 500);

        }
        if(MyApplication.flag==6) {
            MyApplication.flag=7;
            start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        shs.stopAsync();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        requestData();

    }



    @Override
    protected void initView() {
        super.initView();
        Log.d("MyNewsActivity", "initView: " + getAdapter().getItemCount());
    }

    @Override
    protected void setupSwipeRefreshLayout() {
        super.setupSwipeRefreshLayout();
        mSwipeRefreshLayout.setEnabled(false);
    }

    @OnClick(R.id.start)
    public void start() {
        startTx.setText("已启动");
        Log.d("frpc", "frpc启动");
        frpStart();
        Intent intent=new Intent();
        intent.setClass(this, DeviceListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add)
    public void add() {
        Intent intent = new Intent(knife_net_list.this, knife_net_Activity.class);
        startActivity(intent);
    }

    public void onBackPressed() {

        if (false == bBackButtonClick) {

            back_button_click_time = System.currentTimeMillis();
            bBackButtonClick = true;

            ResetBackButtonThread backButtonThread = new ResetBackButtonThread();
            backButtonThread.start();
        } else {
            super.onBackPressed();
        }
    }

    class ResetBackButtonThread extends Thread {
        public void run() {
            try {

                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bBackButtonClick = false;
        }
    }

    @Override
    protected void requestData() {
        SuiDaolist = DBSuiDaoHelper.queryAll();
        for (int i = 0; i < SuiDaolist.size(); i++) {
            if (SuiDaolist.get(i).getIp().equals("127.0.0.1")) {
                MyApplication.getDaoInstant().getSuiDaoDao().delete(SuiDaolist.get(i));
            }
        }
        onRefreshSucceed(DBSuiDaoHelper.queryAll());
    }

    @Override
    public ItemViewCreator<SuiDao> configItemViewCreator() {
        return new ItemViewCreator<SuiDao>() {
            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(MySuiDaoViewHolder.LAYOUT_RES, parent, false);
            }

            @Override
            public IItemView<SuiDao> newItemView(View view, int viewType) {
                return new MySuiDaoViewHolder(view);
            }
        };
    }

    @Override
    public BaseRecyclerAdapter<SuiDao> newAdapter() {
        return new SuiDaoAdapter(this, configItemViewCreator(), mList);
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
//        if (!footBarView.isShown()) {
////            Intent intent = new Intent(this,NewsDetailActivity.class);
////            intent.putExtra("fal", (Parcelable) mList.get(position));
////            startActivity(intent);
//            mList.get(position).is_read = 1;
//            getAdapter().notifyItemChanged(position);
//            ActivityUtils.startActivity(this, NewsDetailActivity.class,mList.get(position).id);
//        }

    }

    @Override
    protected void configRecyclerView() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SuiDaoAdapter adapter = (SuiDaoAdapter) getAdapter();
        adapter.selectAllOrNot(isChecked);

    }

    class SuiDaoAdapter extends BaseRecyclerAdapter<SuiDao> {
        private int mode = 1;
        private SparseBooleanArray flags = new SparseBooleanArray();

        public SuiDaoAdapter(Context context, ItemViewCreator creator, List<SuiDao> list) {
            super(context, creator, list);
        }

        public void setMode(int m) {
            this.mode = m;
            notifyDataSetChanged();
        }

        public void selectAllOrNot(boolean flag) {
            for (int i = 0; i < mList.size(); i++) {
                flags.put(i, flag);
            }
            notifyDataSetChanged();
        }

        public void deleteSuiDao() {
            IniFile file = new IniFile(new File(knife_net_list.this.getExternalFilesDir(null), "config.ini"));
            for (int i = 0; i < flags.size(); i++) {
                if (flags.get(i)) {
                    if (file.isexists()) {
                        Long temp = mList.get(i).getId();
                        file.remove(DBSuiDaoHelper.quarySetionName(temp));
                        DBSuiDaoHelper.deletesuidao(temp);
//					Log.d("configini",(String) file.get("common").get("protocol"));
//					Log.d("configini",file.get("telnet_test").get("type").toString());
//
//					Log.d("configini",file.get("telnet_test").get("type").toString());
                    }
                }
            }
            file.save();
            mList.clear();
            requestData();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            super.onBindViewHolder(holder, position);
            if (holder instanceof MySuiDaoViewHolder) {
                final MySuiDaoViewHolder holder1 = (MySuiDaoViewHolder) holder;
                if (mode == 2) {
                    holder1.circleBox.setVisibility(View.VISIBLE);
                } else {
                    holder1.circleBox.setVisibility(View.GONE);
                }
                holder1.circleBox.setImageResource(flags.get(position) ? R.mipmap.ic_radio_btn_selected
                        : R.mipmap.ic_radio_btn_default);
                holder1.circleBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = flags.get(position);
                        flags.put(position, !flag);
                        holder1.circleBox.setImageResource(!flag ? R.mipmap.ic_radio_btn_selected
                                : R.mipmap.ic_radio_btn_default);
                    }
                });
            }
        }
    }

    @OnClick(R.id.edit)
    public void edit() {
        if (footBarView.isShown()) {
            footBarView.setVisibility(View.GONE);
            editTv.setText("编辑");
            ((SuiDaoAdapter) getAdapter()).setMode(1);
        } else {
            footBarView.setVisibility(View.VISIBLE);
            editTv.setText("取消");
            ((SuiDaoAdapter) getAdapter()).setMode(2);
            ((SuiDaoAdapter) getAdapter()).selectAllOrNot(false);
        }
    }

    @OnClick(R.id.remove)
    public void remove() {
        SuiDaoAdapter adapter = (SuiDaoAdapter) getAdapter();
        adapter.deleteSuiDao();
        HttpUtil.frpreload();
        footBarView.setVisibility(View.GONE);
        editTv.setText("编辑");
        ((SuiDaoAdapter) getAdapter()).setMode(1);
        ((SuiDaoAdapter) getAdapter()).selectAllOrNot(false);
    }

    private void frpStart() {
        if (mAuthTask != null) {
            return;
        }
        mAuthTask = new UserLoginTask(getExternalFilesDir(null) + "/config.ini");
        Log.e("path", getExternalFilesDir(null) + "/config.ini");//打印
        mAuthTask.execute((Void) null);//执行异步线程
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mConfigPath;

        UserLoginTask(String email) {
            mConfigPath = email;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Frpclib.run(mConfigPath);
            } catch (Throwable e) {
                if (e != null && e.getMessage() != null) {
                    Log.e("throwable", e.getMessage() + "");
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //由于Frpclib.run(mConfigPath)该方法保持了长连接，所以这些方法都走不进去，只是摆设
            if (success) {
            } else {
            }
            mAuthTask = null;
//            finish();
        }

        @Override
        protected void onCancelled() {
            //由于Frpclib.run(mConfigPath)该方法保持了长连接，所以这些方法都走不进去，只是摆设，但退出程序会走这个方法
            Log.e("onCancelled", "+++++++");
            mAuthTask = null;
        }
    }
}
