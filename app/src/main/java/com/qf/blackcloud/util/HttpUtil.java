package com.qf.blackcloud.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/10/6 0006.
 */

public class HttpUtil {
    //发送网络请求
    public static void sendHttpRequest(final String addr,final IHttpCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn=null;
                InputStream in=null;
                try {
                    URL url=new URL(addr);
                    conn= (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(7000);
                    in = conn.getInputStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(in));
                    StringBuilder result=new StringBuilder();
                    String line;
                    while((line=br.readLine())!=null){
                        result.append(line);
                    }
                    if(callBack!=null){
                        callBack.finish(result.toString());
                    }
                } catch (Exception e) {
                    if(callBack!=null){
                        callBack.error();
                    }
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    conn.disconnect();
                }
            }
        }).start();
    }

    public interface  IHttpCallBack{
        public void finish(String result);
        public void error();
    }
}
