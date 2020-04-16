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

public class HttpTestHandler implements IRsourceUriHandler {
    private static final String TAG = "UploadImageHandler";
    String acceptPrefix = "/test/";
    Activity activity;

    public HttpTestHandler(Activity activity){
        this.activity = activity;
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(acceptPrefix);
    }

    @Override
    public void postHandle(String uri, HttpContext httpContext) throws IOException {


        String html="<html> <head> <meta charset=\"utf-8\"><title>android http</title> </head> <body>android http</body> </html>";
        byte[] raw=html.getBytes();
        OutputStream outputStream = httpContext.getUnderlySocket().getOutputStream();//获取当前Socket的输出流
        PrintStream printWriter = new PrintStream(outputStream);//输出符合http协议的信息给浏览器
        printWriter.print("HTTP/1.1 200 OK");
//        printWriter.println("Content-Length:" + raw.length);
        printWriter.println("Content-Type: text/html");
        printWriter.println();
        printWriter.write(raw);
    }


}
