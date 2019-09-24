package com.ldgd.ldstreetlightmanagement;

import com.ldgd.ldstreetlightmanagement.util.HttpConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ldgd on 2019/9/22.
 * 功能：
 * 说明：
 */

public class Check {


    public static void main(String[] args) {

       sendHttp2();




    }


    public static void sendHttp3() {

        //指定当前请求的 contentType 为 json 数据
        MediaType JSON = MediaType.parse("application/json");
        String postBody = "{\"where\":{\"PROJECT\":\"中科洛丁展示项目/深圳展厅\"},\"size\":5000}";

        /**
         * 创建请求的参数body
         */
     //   RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);

        RequestBody body= new FormBody.Builder().build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS).build();//设置读取超时时间;

        Request request = new Request.Builder()
                .addHeader("X-auth-token", "1ccf8430-dd1e-11e9-8c76-0b68964d4fc9")
                .header("Accept-Encoding", "deflate")
                .addHeader("content-type", "application/json")
                .url(HttpConfiguration.PROJECT_LIST_URL)
             //   .post(body)// post json提交
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("失败 json = ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                System.out.println("成功 json = " + json);
            }

        });
    }


    public static void sendHttp2() {

        //指定当前请求的 contentType 为 json 数据
        MediaType JSON = MediaType.parse("application/json");
        String postBody = "{\"where\":{\"PROJECT\":\"中科洛丁展示项目/深圳展厅\"},\"size\":5000}";


        /**
         * 创建请求的参数body
         */
        RequestBody body = FormBody.create(MediaType.parse("application/json"), postBody);


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS).build();//设置读取超时时间;

        Request request = new Request.Builder()
                .addHeader("X-auth-token", "1ccf8430-dd1e-11e9-8c76-0b68964d4fc9")
                .header("Accept-Encoding", "deflate")
                .addHeader("content-type", "application/json")
                .url(HttpConfiguration.DEVICE_LAMP_LIST_URL)
                .post(body)// post json提交
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("失败 json = ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                System.out.println("成功 json = " + json);
            }

        });
    }


    public void sendHttp() {

        RequestBody requestBody = new FormBody.Builder()
                //  .add("where","{\"PROJECT\":\"中科洛丁展示项目/重庆展厅\"},\"size\":5000")
                .build();


        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(20, TimeUnit.SECONDS).build();//设置读取超时时间;

        Request request = new Request.Builder()
                .addHeader("X-auth-token", "1ccf8430-dd1e-11e9-8c76-0b68964d4fc9")
                .header("Accept-Encoding", "deflate")
                .url(HttpConfiguration.DEVICE_LAMP_LIST_URL).post(requestBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("失败 json = ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                System.out.println("成功 json = " + json);
            }

        });
    }


}
