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
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    TextView textView;

//    private String targetUrl = "http://10.25.6.55:80/users/login";

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
//                            testLogin();
                            testSignup();

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

    private void testLogin() {
        Map<String, String> params = new HashMap<>();
        params.put("studentId", "12121212");
        params.put("password", "12121212");
        okhttpGet("http://10.25.6.55:80/users/login", params);
    }

    private void testSignup() {
        Map<String, String> params = new HashMap<>();
        params.put("studentId", "12345671");
        params.put("name", "hihi");
        params.put("password", "password123");
        params.put("type", "user");
        okhttpPost("http://10.25.6.55:80/users", params);
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

    private void okhttpGet(String url, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpUrl = HttpUrl.parse(url).newBuilder();
        Map<String, String> map = new HashMap<String, String>(params);
        for(Map.Entry<String,String> entry : map.entrySet()){
            httpUrl.addQueryParameter(entry.getKey(),entry.getValue());
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(httpUrl.build())
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
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

    private void okhttpPost(String url, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            formBodyBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBodyBuilder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
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

}
