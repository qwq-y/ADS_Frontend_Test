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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    TextView textView;

    String message = "hello";

    private String targetUrl = "localhosst:8787/users/test";

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
            Log.d("ww", "send message: " + message);
//            new SendRequestTask().execute();
            sendMsg(message);
        }
    }

    private void sendMsg(String message) {

        String url = "localhost:8787/users/test";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

//        ------------------------------------------------------------------------------

        RequestQueue queue = Volley.newRequestQueue(this);
        String urlTest = "https://www.baidu.com/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlTest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        textView.setText("Response is: " + response.substring(0, 100));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorString = "Error: " + error.getMessage();
                        textView.setText(errorString);
                        error.printStackTrace();
                    }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

//        ------------------------------------------------------------------------------

//        JSONObject jsonObject = new JSONObject();
//        try {
//            // 构建JSON请求体
//            jsonObject.put("message", message);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObject,
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

//    private class SendRequestTask extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... voids) {
//            HttpURLConnection connection = null;
//            StringBuilder response = new StringBuilder();
//            try {
//                URL url = new URL(targetUrl);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("POST");
//
//                // 可选：设置请求头部参数
//                // connection.setRequestProperty("Content-Type", "application/json");
//                // connection.setRequestProperty("Authorization", "Bearer YOUR_TOKEN");
//
//                connection.setDoOutput(true);
//                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
//                // 可选：如果需要在请求中发送数据，则在此处写入请求体的内容
//                // outputStream.writeBytes("data");
//
//                outputStream.flush();
//                outputStream.close();
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    reader.close();
//                } else {
//                    // 处理错误情况，如连接失败等
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (connection != null) {
//                    connection.disconnect();
//                }
//            }
//
//            return response.toString();
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            // 在请求完成后，可以在此处处理响应结果
//        }
//    }
}
