package com.example.testapplication;

import android.content.Intent;
import android.content.SyncRequest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.File;
import java.io.FileOutputStream;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button sendButton;
    private TextView textView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        imageView = findViewById((R.id.imageView));

        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendButton) {
            textView.setText("sending ...");
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 处理按钮点击事件
//                            okhttpGet("http://10.25.6.55:80/posts");
//                            testLogin();
//                            testSignup();
                            testSam();

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

        String url = "http://10.25.6.55:80/users/login";

        okhttpGet(url, params);
    }

    private void testSignup() {
        Map<String, String> params = new HashMap<>();
        params.put("studentId", "12345673");
        params.put("name", "hiii");
        params.put("password", "password123");
        params.put("type", "user");

        String url = "http://10.25.6.55:80/users";

        okhttpPost(url, params, null);
    }

    private void testSam() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(R.drawable.test_image);
            }
        });

//        File imageFile = new File("test_image.png");
        File imageFile = getFileFromResource(R.drawable.test_image);

        Map<String, String> params = new HashMap<>();
        params.put("point_coord_0", "200");
        params.put("point_coord_1", "100");
        params.put("point_label", "1");
        params.put("use_mask", "0");
        params.put("mode", "0");

        String url = "http://172.18.36.107:5000/sam";

        okhttpPost(url, params, imageFile);
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        try {
            // 获取应用的私有存储目录
            File directory = getFilesDir();

            // 创建保存文件的目标路径
            File file = new File(directory, "received_image.jpg");

            // 创建文件输出流
            FileOutputStream outputStream = new FileOutputStream(file);

            // 将Bitmap写入输出流，并指定压缩格式和质量
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // 刷新输出流
            outputStream.flush();

            // 关闭输出流
            outputStream.close();

            Log.i("ww", "Bitmap saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getFileFromResource(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
        File tempFile = new File(getCacheDir(), "temp_image.jpg");
        try {
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
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
                    textView.setText("收到回复：" +responseBody);
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
                        textView.setText("收到回复：" +responseBody);
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
                        textView.setText("收到回复：" + responseBody);
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

    private void okhttpPost(String url, Map<String, String> params, File imageFile) {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加文本参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }

        // 添加图片文件
        if (imageFile != null) {
            multipartBuilder.addFormDataPart("image", imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imageFile));
        }

        RequestBody requestBody = multipartBuilder.build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                // 处理响应
                byte[] responseBodyBytes = response.body().bytes();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(responseBodyBytes, 0, responseBodyBytes.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Success!");
                        imageView.setImageBitmap(bitmap);
                    }
                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        saveBitmapToFile(bitmap);
//                    }
//                }).start();
                // 删除临时文件
                if (imageFile != null && imageFile.exists()) {
                    imageFile.delete();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 处理失败
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("Error: " + e.getMessage());
                    }
                });
                // 删除临时文件
                if (imageFile != null && imageFile.exists()) {
                    imageFile.delete();
                }
            }
        });
    }
}
