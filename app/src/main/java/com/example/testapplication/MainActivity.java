package com.example.testapplication;

import android.content.Intent;
import android.content.SyncRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    TextView textView;

    private String targetUrl = "http://10.25.6.55:80/users/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);

        sendButton = findViewById(R.id.btn_send);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send) {
            textView.setText("sending hello...");
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 处理按钮点击事件
//                            okhttpGet("http://10.25.6.55:80/posts");
                            testLogin();

                        } catch (Exception e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(e.getMessage());
                                }
                            });
                        }
                    }
                }).start();
            } catch (Exception e) {
                textView.setText(e.getMessage());
            }
        }
    }

    private void okhttpGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            final String responseBody = response.body().string();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(responseBody);
                }
            });
        }
    }


    private void testLogin() {
        Map<String, String> params = new HashMap<>();
        params.put("studentId", "12121212");
        params.put("password", "12121212");
        okhttpGet(targetUrl, params);
    }

    private void okhttpGet(String url, Map params) {
        OkHttpClient httpClient = new OkHttpClient();

        HttpUrl.Builder httpUrl = HttpUrl.parse(url).newBuilder();
        Map<String, String> map = new HashMap<String, String>(params);
        for(Map.Entry<String,String> entry : map.entrySet()){
            httpUrl.addQueryParameter(entry.getKey(),entry.getValue());
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(httpUrl.build())
                .build();
        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                // 处理响应
                String responseBody = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(responseBody);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Error: " + e.getMessage());
                    }
                });
            }
        });
    }

    private void volleyGet() {

//        ------------------------------------------------------------------------------
//      无参的GET方法
        RequestQueue queue = Volley.newRequestQueue(this);
        String urlTest = "http://10.25.6.55:80/posts";
//        String urlTest = "http://10.25.6.55:80/users/login?studentId=11435142&password=12345678";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlTest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        textView.setText("Response is: " + response.substring(0, 300));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorString = "Error: " + error.getMessage();
                        textView.setText(errorString);
                        error.printStackTrace();
                    }
        });

        queue.add(stringRequest);

//        ------------------------------------------------------------------------------

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            // 构建JSON请求体
//            jsonObject.put("studentId", 21212121L);
//            jsonObject.put("name", "gm");
//            jsonObject.put("password", "12345678");
//            jsonObject.put("type", "user");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
////        long studentId = 11435142;
////        String password = "12345678";
////        String url = targetUrl + "/login?studentId=" + studentId + "&password=" + password;
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, targetUrl, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // 请求成功，可以在这里处理返回的数据
//                        textView.setText("Response is: " + response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // 请求失败，可以在这里处理失败情况
//                        String errorString = "Error: " + error.getMessage();
//                        textView.setText(errorString);
//                        error.printStackTrace();
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                // 如果需要设置请求头，请在此方法中返回对应的键值对
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        requestQueue.add(jsonObjectRequest);

    }
}
