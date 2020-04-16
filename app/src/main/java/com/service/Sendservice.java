package com.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.activity.MyApplication;
import com.hoho.android.usbserial.BuildConfig;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.ArrayList;
import java.util.List;

public class Sendservice extends Service {
    private final String TAG = Sendservice.class.getSimpleName();

    private UsbManager mUsbManager;
    private UsbSerialPort mSerialPort;

    private static final int MESSAGE_REFRESH = 101;
    private static final long REFRESH_TIMEOUT_MILLIS = 5000;
    public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    refreshDeviceList();
                    mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

    };
    private BroadcastReceiver mUsbReceiver;

    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
//    private ArrayAdapter<UsbSerialPort> mAdapter;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();


        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        mUsbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(INTENT_ACTION_GRANT_USB)) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                    } else {
                        Toast.makeText(context, "USB permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

//        mAdapter = new ArrayAdapter<UsbSerialPort>(this,
//                android.R.layout.simple_expandable_list_item_2, mEntries) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                final TwoLineListItem row;
//                if (convertView == null){
//                    final LayoutInflater inflater =
//                            (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
//                } else {
//                    row = (TwoLineListItem) convertView;
//                }
//
//                final UsbSerialPort port = mEntries.get(position);
//                final UsbSerialDriver driver = port.getDriver();
//                final UsbDevice device = driver.getDevice();
//
//                final String title = String.format("Vendor %4X Product %4X", device.getVendorId(), device.getProductId());
//                row.getText1().setText(title);
//
//                final String subtitle = driver.getClass().getSimpleName();
//                row.getText2().setText(subtitle);
//
//                return row;
//            }
//
//        };

        final List<UsbSerialDriver> drivers =
                UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

        final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
        for (final UsbSerialDriver driver : drivers) {
            final List<UsbSerialPort> ports = driver.getPorts();
            Log.d(TAG, String.format("+ %s: %s port%s",
                    driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
            result.addAll(ports);
        }
        mEntries.clear();
        mEntries.addAll(result);
        mHandler.sendEmptyMessage(MESSAGE_REFRESH);
        registerReceiver(mUsbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MESSAGE_REFRESH);
        unregisterReceiver(mUsbReceiver);
    }


    private void refreshDeviceList() {

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.d(TAG, "Refreshing device list ...");
                SystemClock.sleep(1000);

                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.d(TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
//                mAdapter.notifyDataSetChanged();
                Toast.makeText(Sendservice.this,""+String.format("%s device(s) found",Integer.valueOf(mEntries.size())),Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
                if (mEntries.size() >= 1) {
                    UsbSerialPort mSerialPort = mEntries.get(0);
                    UsbDevice device = mSerialPort.getDriver().getDevice();
                    if (!mUsbManager.hasPermission(device)) {
                        PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(Sendservice.this, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                        mUsbManager.requestPermission(device, usbPermissionIntent);
                    } else {
                        MyApplication.sPort=mSerialPort;
                        System.out.println("--------------------"+mEntries.size());
                    }

                }
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        System.out.println("--------------------"+mEntries.size());
        Toast.makeText(Sendservice.this,""+String.format("%s device(s) found",Integer.valueOf(mEntries.size())),Toast.LENGTH_SHORT).show();
    }



}
