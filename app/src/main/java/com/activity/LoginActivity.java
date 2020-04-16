package com.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activity.greendao.DBSuiDaoHelper;
import com.frpc.R;
import com.hoho.android.usbserial.BuildConfig;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.myactivity.SerialConsoleActivity;
import com.service.Sendservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
//    private final String TAG = LoginActivity.class.getSimpleName();
//    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
//    private UsbManager mUsbManager;
//    private UsbSerialPort mSerialPort;
////    private ListView mListView;
////    private TextView mProgressBarTitle;
////    private ProgressBar mProgressBar;
//
//    private static final int MESSAGE_REFRESH = 101;
//    private static final long REFRESH_TIMEOUT_MILLIS = 5000;
//    public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
//
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_REFRESH:
//                    refreshDeviceList();
//                    mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
//                    break;
//                default:
//                    super.handleMessage(msg);
//                    break;
//            }
//        }
//
//    };
//    private BroadcastReceiver mUsbReceiver;

    private EditText Exservice_ip;//服务器ip
    private EditText Exservice_port;//服务器端口号
    private EditText Exservice_token;//服务器登录token
    private Button Btsave;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Btsave.callOnClick();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();//初始化控件
        initEvent();//加载事件




//        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//
//        mUsbReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if(intent.getAction().equals(INTENT_ACTION_GRANT_USB)) {
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
////                        showConsoleActivity(mSerialPort);
//                    } else {
//                        Toast.makeText(context, "USB permission denied", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        };
        if(MyApplication.flag==1) {
            handler.sendEmptyMessageDelayed(0, 500);
            MyApplication.flag=2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.sendEmptyMessage(MESSAGE_REFRESH);
//        registerReceiver(mUsbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
    }
    @Override
    protected void onPause() {
        super.onPause();
//        mHandler.removeMessages(MESSAGE_REFRESH);
//        unregisterReceiver(mUsbReceiver);
    }
//    private void refreshDeviceList() {
////        showProgressBar();
//
//        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
//            @Override
//            protected List<UsbSerialPort> doInBackground(Void... params) {
//                Log.d(TAG, "Refreshing device list ...");
//                SystemClock.sleep(1000);
//
//                final List<UsbSerialDriver> drivers =
//                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);
//
//                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
//                for (final UsbSerialDriver driver : drivers) {
//                    final List<UsbSerialPort> ports = driver.getPorts();
//                    Log.d(TAG, String.format("+ %s: %s port%s",
//                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
//                    result.addAll(ports);
//                }
//
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(List<UsbSerialPort> result) {
//                mEntries.clear();
//                mEntries.addAll(result);
////                mAdapter.notifyDataSetChanged();
////                mProgressBarTitle.setText(
////                        String.format("%s device(s) found",Integer.valueOf(mEntries.size())));
////                hideProgressBar();
//                Log.d("--------------", "Done refreshing, " + mEntries.size() + " entries found.");
//                Toast.makeText(LoginActivity.this,"Done refreshing, " + mEntries.size() + " entries found.",Toast.LENGTH_SHORT).show();
//                if (mEntries.size() >= 1) {
//                    UsbSerialPort mSerialPort = mEntries.get(0);
//                    UsbDevice device = mSerialPort.getDriver().getDevice();
//                    if (!mUsbManager.hasPermission(device)) {
//                        PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(LoginActivity.this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
//                        mUsbManager.requestPermission(device, usbPermissionIntent);
//                    } else {
//                        MyApplication.sPort=mSerialPort;
//                        if(MyApplication.flag==1) {
//                            handler.sendEmptyMessageDelayed(0, 500);
//                            MyApplication.flag=2;
//                        }
//                    }
//
//                }
//            }
//
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
    private void initView(){
        Exservice_ip = findViewById(R.id.service_ip);
        Exservice_port = findViewById(R.id.service_port);
        Exservice_token = findViewById(R.id.service_token);
        Btsave = findViewById(R.id.save);
        Exservice_ip.setText("118.24.23.205");
        Exservice_port.setText("7001");
        Exservice_token.setText("12345678");
    }
    private void initEvent(){

        Btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = Exservice_ip.getText().toString();
                String port = Exservice_port.getText().toString();
                String token = Exservice_token.getText().toString();
                if(port.length()>0){
                    int int_port = Integer.parseInt(port);
                if(ip.length()>2 && 65535>=int_port &&int_port>=1024 && token.length()>2){
                    DBSuiDaoHelper.deleteALL();
                    copyToSD(ip,port,token);
                    Intent intent = new Intent(LoginActivity.this,knife_net_list.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this,"输入有误",Toast.LENGTH_SHORT).show();
                }

                }else{
                    Toast.makeText(LoginActivity.this,"输入有误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * @param service_ip
     * @param service_port
     * @param service_token
     * 向配置文件中写入FRP服务器的参数
     */
    private void copyToSD(String service_ip,String service_port,String service_token) {
        InputStream in = null;
        FileOutputStream out = null;

        //判断如果数据库已经拷贝成功，不需要再次拷贝
        File file = new File(this.getExternalFilesDir(null), MyApplication.FILENAME);
        if(file.exists()){
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                AssetManager assets = getAssets();
                //2.读取数据资源
                in = assets.open(MyApplication.FILENAME);
                out = new FileOutputStream(file);
                //3.读写操作
                byte[] b = new byte[1024];//缓冲区域
                int len = -1; //保存读取的长度
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out = new FileOutputStream(file,true);
                String common = "[common]\r\n";
                String server_addr = "server_addr ="+service_ip+"\r\n";
                String server_port = "server_port = "+ service_port + "\r\n";
                String privilege_token = "privilege_token = " + service_token + "\r\n";
                String admin_addr = "admin_addr = 0.0.0.0"+"\r\n";
                String admin_port = "admin_port = 7400"+"\r\n";
                String admin_user = "admin_user = admin"+"\r\n";
                String admin_pwd = "admin_pwd = admin"+"\r\n";
                String pool_count = "pool_count = 5"+"\r\n";
                String tcp_mux = "tcp_mux = true"+"\r\n";
                String login_fail_exit = "login_fail_exit = true"+"\r\n";
                String protocol = "protocol = tcp"+"\r\n";
                out.write(common.getBytes());
                out.write(server_addr.getBytes());
                out.write(server_port.getBytes());
                out.write(privilege_token.getBytes());
                out.write(admin_addr.getBytes());
                out.write(admin_port.getBytes());
                out.write(admin_user.getBytes());
                out.write(admin_pwd.getBytes());
                out.write(pool_count.getBytes());
                out.write(tcp_mux.getBytes());
                out.write(login_fail_exit.getBytes());
                out.write(protocol.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

