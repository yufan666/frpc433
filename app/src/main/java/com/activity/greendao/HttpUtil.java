package com.activity.greendao;

import android.util.Log;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;


public  class HttpUtil {
    public static void frpreload()  {
        final String basic = Credentials.basic("admin", "admin");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.authenticator(new okhttp3.Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                return response.request().newBuilder().header("Authorization", basic).build();
            }
        });
        OkHttpClient client = builder.build();
        Request request = new Request.Builder().url("http://127.0.0.1:7400/api/reload").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("google.sang", "onFailure: "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("google.sang", "onResponse: "+response.body().string());
                }
            }
        });
    }

}
