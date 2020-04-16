/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.myactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.activity.MyApplication;
import com.frpc.R;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.util.HexDump;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.websocket.ServerSocket._serverManager;

/**
 * Monitors a single {@link UsbSerialPort} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

    private final String TAG = SerialConsoleActivity.class.getSimpleName();

    /**
     * Driver instance, passed in statically via
     * {@link #show(Context, UsbSerialPort)}.
     *
     * <p/>
     * This is a devious hack; it'd be cleaner to re-create the driver using
     * arguments passed in with the {@link #startActivity(Intent)} intent. We
     * can get away with it because both activities will run in the same
     * process, and this is a simple demo.
     */
    private static UsbSerialPort sPort = null;

    private TextView mTitleTextView;
    private TextView mDumpTextView;
    private ScrollView mScrollView;
//    private CheckBox chkDTR;
//    private CheckBox chkRTS;
    Button bt1_0,bt2_0,bt3_0,bt4_0,bt1_1,bt2_1,bt3_1,bt4_1;
    int flag=0;
    public  static SharedPreferences sharedPreferences;

    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
            SerialConsoleActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SerialConsoleActivity.this.updateReceivedData(data);
                }
            });
        }
    };
    private View mPopView;
    private PopupWindow mPopupWindow;

    public static String statue="close";
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mPopupWindow.showAtLocation(mPopView, Gravity.CENTER, 0, 0);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial_console);
        mTitleTextView = (TextView) findViewById(R.id.demoTitle);
        mDumpTextView = (TextView) findViewById(R.id.consoleText);
        mScrollView = (ScrollView) findViewById(R.id.demoScroller);
        bt1_0=(Button)findViewById(R.id.bt1_0);
        bt2_0=(Button)findViewById(R.id.bt2_0);
        bt3_0=(Button)findViewById(R.id.bt3_0);
        bt4_0=(Button)findViewById(R.id.bt4_0);
        bt1_1=(Button)findViewById(R.id.bt1_1);
        bt2_1=(Button)findViewById(R.id.bt2_1);
        bt3_1=(Button)findViewById(R.id.bt3_1);
        bt4_1=(Button)findViewById(R.id.bt4_1);
//        chkDTR = (CheckBox) findViewById(R.id.checkBoxDTR);
//        chkRTS = (CheckBox) findViewById(R.id.checkBoxRTS);
//
//        chkDTR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                try {
//                    sPort.setDTR(isChecked);
//                }catch (IOException x){}
//            }
//        });
//
//        chkRTS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                try {
//                    sPort.setRTS(isChecked);
//                }catch (IOException x){}
//            }
//        });
        bt1_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1_0.setText("学习中");
                bt2_0.setText("学习2");
                bt3_0.setText("学习3");
                bt4_0.setText("学习4");
                flag=1;
            }
        });
        bt2_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt2_0.setText("学习中");
                bt1_0.setText("学习1");
                bt3_0.setText("学习3");
                bt4_0.setText("学习4");
                flag=2;
            }
        });
        bt3_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt3_0.setText("学习中");
                bt1_0.setText("学习1");
                bt2_0.setText("学习2");
                bt4_0.setText("学习4");
                flag=3;
            }
        });
        bt4_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt4_0.setText("学习中");
                bt1_0.setText("学习1");
                bt2_0.setText("学习2");
                bt3_0.setText("学习3");
                flag=4;
            }
        });
        sharedPreferences=getSharedPreferences("btdata",MODE_PRIVATE);
        bt1_1.setText(sharedPreferences.getString("bt1_1",""));
        bt2_1.setText(sharedPreferences.getString("bt2_1",""));
        bt3_1.setText(sharedPreferences.getString("bt3_1",""));
        bt4_1.setText(sharedPreferences.getString("bt4_1",""));
        bt1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sPort.write(hexToByteArray(sharedPreferences.getString("bt1_1","")),1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bt2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sPort.write(hexToByteArray(sharedPreferences.getString("bt2_1","")),1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bt3_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sPort.write(hexToByteArray(sharedPreferences.getString("bt3_1","")),1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bt4_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sPort.write(hexToByteArray(sharedPreferences.getString("bt4_1","")),1000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        initpopwindow();

        mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.showAtLocation(mPopView, Gravity.CENTER, 0, 0);
            }
        });
        handler.sendEmptyMessageDelayed(0,1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.flag=9;
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.flag=10;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIoManager();
        if (sPort != null) {
            try {
                sPort.close();
            } catch (IOException e) {
                // Ignore.
            }
            sPort = null;
        }
        finish();
    }

    void showStatus(TextView theTextView, String theLabel, boolean theValue){
        String msg = theLabel + ": " + (theValue ? "enabled" : "disabled") + "\n";
        theTextView.append(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Resumed, port=" + sPort);
        if (sPort == null) {
            mTitleTextView.setText("No serial device.");
        } else {
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

            UsbDeviceConnection connection = usbManager.openDevice(sPort.getDriver().getDevice());
            if (connection == null) {
                mTitleTextView.setText("Opening device failed");
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                showStatus(mDumpTextView, "CD  - Carrier Detect", sPort.getCD());
                showStatus(mDumpTextView, "CTS - Clear To Send", sPort.getCTS());
                showStatus(mDumpTextView, "DSR - Data Set Ready", sPort.getDSR());
                showStatus(mDumpTextView, "DTR - Data Terminal Ready", sPort.getDTR());
                showStatus(mDumpTextView, "DSR - Data Set Ready", sPort.getDSR());
                showStatus(mDumpTextView, "RI  - Ring Indicator", sPort.getRI());
                showStatus(mDumpTextView, "RTS - Request To Send", sPort.getRTS());

            } catch (IOException e) {
                Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                mTitleTextView.setText("Error opening device: " + e.getMessage());
                try {
                    sPort.close();
                } catch (IOException e2) {
                    // Ignore.
                }
                sPort = null;
                return;
            }
            mTitleTextView.setText("Serial device: " + sPort.getClass().getSimpleName());
        }
        onDeviceStateChange();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (sPort != null) {
            Log.i(TAG, "Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        final String message = "Read " + data.length + " bytes: \n"
                + HexDump.dumpHexString(data) + "\n\n";
        mDumpTextView.append(message);
        mDumpTextView.append(bytesToHex(data));
        mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
        String btdata="fd03"+bytesToHex(data).substring(2);
        if(flag==1){
            bt1_0.setText("学习1");
            bt1_1.setText(btdata);
            flag=0;
            sharedPreferences.edit().putString("bt1_1",btdata).commit();
        }
        if(flag==2){
            bt2_0.setText("学习2");
            bt2_1.setText(btdata);
            flag=0;
            sharedPreferences.edit().putString("bt2_1",btdata).commit();
        }
        if(flag==3){
            bt3_0.setText("学习3");
            bt3_1.setText(btdata);
            flag=0;
            sharedPreferences.edit().putString("bt3_1",btdata).commit();
        }
        if(flag==4){
            bt4_0.setText("学习4");
            bt4_1.setText(btdata);
            flag=0;
            sharedPreferences.edit().putString("bt4_1",btdata).commit();
        }
        _serverManager.SendMessageToAll(bytesToHex(data));
        if(btdata.substring(0,10).equals(sharedPreferences.getString("bt1_1","").substring(0,10))){
            _serverManager.SendMessageToAll("open");
            mTitleTextView.setText("open");
            statue="open";
        }
        if(btdata.substring(0,10).equals(sharedPreferences.getString("bt2_1","").substring(0,10))) {
            _serverManager.SendMessageToAll("close");
            mTitleTextView.setText("close");
            statue="close";
        }
    }


    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }


    /**
     * Starts the activity, using the supplied driver instance.
     *
     * @param context
     * @param driver
     */
    public static void show(Context context, UsbSerialPort port) {
        sPort = port;
        final Intent intent = new Intent(context, SerialConsoleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static void mopen(){
        try {
            sPort.write(hexToByteArray(sharedPreferences.getString("bt1_1","")),1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void mclose(){
        try {
            sPort.write(hexToByteArray(sharedPreferences.getString("bt2_1","")),1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initpopwindow() {
        mPopView = getLayoutInflater().inflate(R.layout.home_popwindow_date_layout, null);
        // 将转换的View放置到 新建一个popuwindow对象中
        mPopupWindow= new PopupWindow(mPopView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // 点击popuwindow外让其消失
        mPopupWindow.setOutsideTouchable(true);
        mPopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
    }
}
