package com.example.lifen.simplehttpserver;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;


/**
 * Created by Unchangedman on 2017/12/30.
 */

class UploadImageHandler implements IRsourceUriHandler {
    private static final String TAG = "UploadImageHandler";
    String acceptPrefix = "/image/";
    Activity activity;

    public UploadImageHandler(Activity activity){
        this.activity = activity;
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void postHandle(String uri, HttpContext httpContext) throws IOException {
        long totalLength = Long.parseLong(httpContext.getRequestHeaderValue("Content-Length").trim());
        /*
        将 接收到的 图片存储到本机 SD卡目录下
         */
        File file = new File(Environment.getExternalStorageDirectory(),
                "tmpFile.jpg");
        Log.i(TAG, "postHandle: " +"totalLength=" + totalLength + " file    getPath=" + file.getPath() );
        if(file.exists()){
            file.delete();
        }
//        byte[] buffer = new byte[10240];
//        int read;
//        long nLeftLength = totalLength;
//        FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());//文件输出流
        InputStream inputStream = httpContext.getUnderlySocket().getInputStream();//从当前 Socket 中得到文件输入流
        extractRFCFile(inputStream);
//        while (nLeftLength > 0 && (read = inputStream.read(buffer)) > 0){//写到文件输出流中，即存储到SD 卡路径下
//            fileOutputStream.write(buffer,0,read);
//            nLeftLength -= read;
//        }
//        Log.i(TAG, "postHandle: close");
//        fileOutputStream.close();

        /*
        从当前 Socket 中得到 文件输出流，将 符合http协议的信息 返回给浏览器
         */
        OutputStream outputStream = httpContext.getUnderlySocket().getOutputStream();
        PrintStream printWriter = new PrintStream(outputStream);
        printWriter.print("HTTP/1.1 200 OK");
        printWriter.println("Content-Type: text/html");
        printWriter.println();
        printWriter.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        /*
        记录图片存储的位置
         */
        onImageLoaded(file.getPath());
    }

    public void onImageLoaded(String path){
        Log.d(TAG, "onImageLoaded() called with: path = [" + path + "]");
    }
    /**
     * 提取http上传的文件
     * @return
     */
    private String extractRFCFile(InputStream inputStream) {
        byte[] boundary = this.readLine(inputStream).getBytes();// 根据RFC协议第一行是边界
        Log.d(TAG, "上传数据，边界为:" + new String(boundary));
        String line;
        while ((line = this.readLine(inputStream)) != null) {
            Log.d(TAG, line);
            if (line.startsWith("Content-Disposition")) {
                //获取上传的文件名
            }
            if (line.equals("")) {// 空行表示头结束
                break;
            }
        }
//        String savePath=Environment.getExternalStorageDirectory()+"/upload.tmp";//tmpFile.jpg
        String savePath=Environment.getExternalStorageDirectory()+"/tmpFile.jpg";
        try {
            FileOutputStream fos = new FileOutputStream(savePath);
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = this.readLine(buffer,inputStream)) > 0) {
                if (this.startsWith(buffer, boundary)) {
                    Log.d(TAG, "文件读取结束");
                    break;
                }
                fos.write(buffer, 0, count);//会导致多一个换行符
            }
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            Log.e(TAG,"提取HTTP上传的文件出错"+ ex);
            return null;
        }
        return savePath;
    }
    /**
     * 读取一行
     * @return
     */
    private String readLine(InputStream mStream) {
        try {
            byte[] buffer = new byte[1024];//默认缓存1024
            int b = 0, pos = 0;
            while ((b = mStream.read()) >= 0) {
                buffer[pos] = (byte) b;
                if (pos > 0 && buffer[pos] == 0x0A && buffer[pos - 1] == 0x0D) {//读取到换行符0d 0a或13 10 表示换行
                    return new String(buffer, 0, pos - 1);
                }
                pos++;//当前坐标+1
                if (pos == buffer.length) {//缓冲区已满则扩充缓存区
                    byte[] old = buffer;
                    buffer = new byte[old.length + 1024];//每次扩充1024
                    System.arraycopy(old, 0, buffer, 0, old.length);
                }
            }
            return new String(buffer, 0, pos);
        } catch (Exception ex) {
            return null;
        }
    }
    /**
     * 读取一行byte，返回读取的长度
     * @param buffer
     * @return
     */
    private int readLine(byte[] buffer,InputStream mStream) {
        int b = 0, pos = 0;
        try {
            while (pos<buffer.length&&(b = mStream.read()) !=-1) {
                buffer[pos] = (byte) b;
                if (pos > 0 && buffer[pos] == 0x0A && buffer[pos - 1] == 0x0D) {//读取到换行符0d 0a或13 10 表示换行
                    return pos+1;//返回读取的长度
                }
                pos++;//当前坐标+1
            }
        } catch (Exception ex) {
        }
        return pos;
    }
    /**
     * 比较byte数组
     *
     * @param src
     * @param des
     * @return
     */
    private boolean startsWith(byte[] src, byte[] des) {
        if (src.length < des.length) {
            return false;
        }
        for (int i = 0; i < des.length; i++) {
            if (src[i] != des[i]) {
                return false;
            }
        }
        return true;
    }

}
