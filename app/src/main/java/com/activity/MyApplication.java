package com.activity;


import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;

import com.activity.greendao.DBSuiDaoHelper;
import com.activity.greendao.DaoMaster;
import com.activity.greendao.DaoSession;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MyApplication extends Application {

    public static int flag=1;
    public static UsbSerialPort sPort = null;
    public static Context mContext;
  public static String FILENAME = "config.ini";
    private static DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        copyToSD("config.ini");
        setupDatabase();
    }
    private void copyToSD(String dbName) {
        InputStream in=null;
        FileOutputStream out = null;
        //判断如果数据库已经拷贝成功，不需要再次拷贝
        File file = new File(this.getExternalFilesDir(null), dbName);
        if(file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //打开assets中保存的资源
        //1.获取assets目录的管理者
        AssetManager assets = getAssets();

        try {
            //2.读取数据资源
            in = assets.open(dbName);
            //getFilesDir() : data -> data -> 应用程序的包名 -> files
            //getCacheDir() : data -> data -> 应用程序的包名 -> cache
            out = new FileOutputStream(file);

            //3.读写操作
            byte[] b = new byte[1024];//缓冲区域
            int len = -1; //保存读取的长度
            while((len = in.read(b)) != -1){
                out.write(b, 0, len);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }finally{

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {

                    out.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }
    private void setupDatabase() {
        //创建数据库shop.db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "shop.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取dao对象管理者
        daoSession = daoMaster.newSession();
        DBSuiDaoHelper.queryAll();
        DBSuiDaoHelper.deleteALL();
    }
    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
