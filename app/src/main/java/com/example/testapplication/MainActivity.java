package com.example.testapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.testapplication.models.CustomResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CacheRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "ww";
    private String TEMP = "temp";
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
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 处理按钮点击事件
//                            okhttpGet("http://10.25.6.55:80/posts");
//                            testLogin();
//                            testSignup();
//                            testSam();
//                            testReceivePicture();
                            testPostImageInterface();
//                            testPostTextInterface();
                        } catch (Exception e) {
                            Log.d(TAG, "exception in click run: " + e.getMessage());
                        }
                    }
                }).start();
            } catch (Exception e) {
                Log.d(TAG, "exception in click: " + e.getMessage());
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
        params.put("studentId", "12345674");
        params.put("name", "hiiii");
        params.put("password", "password123");
        params.put("type", "user");

        String url = "http://10.25.6.55:80/users";

        okhttpPost(url, params);
    }

    private void testSam() {
//        File imageFile = new File("app/src/main/res/drawable/test_image.png");
        File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_image);

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
                textView.setText("准备发送上图");
            }
        });

        Map<String, String> params = new HashMap<>();
        params.put("point_coord_0", "200");
        params.put("point_coord_1", "100");
        params.put("point_label", "1");
        params.put("use_mask", "0");
        params.put("mode", "0");

        String url = "http://172.18.36.107:5000/sam";

        okhttpPost(url, params, imageFile);

    }

    private void testPostImageInterface() {
        File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_image);

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
                textView.setText("准备发送上图");
            }
        });

        Map<String, String> params = new HashMap<>();
        params.put("point_coord_0", "200");
        params.put("point_coord_1", "100");
        params.put("point_label", "1");
        params.put("use_mask", "0");
        params.put("mode", "0");

        String url = "http://172.18.36.107:5000/sam";
        boolean isImage = true;
        boolean isText = false;

        okhttpPost(url, params, imageFile, isImage, isText)
                .thenAccept(customResponse -> {
                    // 在这里处理修改后的customResponse
                    String text = customResponse.getText();
                    File image = customResponse.getImage();
                    Bitmap bitmapNew = BitmapFactory.decodeFile(image.getAbsolutePath());
                    if (text != null) {
                        setTextView(text);
                    }
                    if (image != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmapNew);
                                textView.setText("获取到上图");
                            }
                        });
                    }
                })
                .exceptionally(e -> {
                    // 处理异常
                    Log.d(TAG, "exception in interface: " + e.getMessage());
                    return null;
                });

    }

    private void testPostTextInterface() {
        Map<String, String> params = new HashMap<>();
        params.put("studentId", "12345673");
        params.put("name", "hiii");
        params.put("password", "password123");
        params.put("type", "user");

        String url = "http://10.25.6.55:80/users";

        okhttpPost(url, params, null, false, true)
                .thenAccept(customResponse -> {
                    // 在这里处理修改后的customResponse
                    String text = customResponse.getText();
                    File image = customResponse.getImage();
                    Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    if (text != null) {
                        setTextView(text);
                    }
                    if (image != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                                textView.setText("获取到上图");
                            }
                        });
                    }
                })
                .exceptionally(e -> {
                    // 处理异常
                    Log.d(TAG, "exception in interface: " + e.getMessage());
                    return null;
                });
    }

    private void testReceivePicture() {
        String url = "https://github.com/qwq-y/ChatRoom/blob/main/imgs/register.png?raw=true";
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                setTextView("收到回复！");
                try {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        String filePath = MainActivity.this.getFilesDir() + "image.jpg";
                        File imageFile = convertResponseBodyToImage(responseBody, filePath);
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                                textView.setText("获取到上图");
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d(TAG, "exception in handling response: \n" + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                setTextView("Error: \n" + e.getMessage());
            }
        });
    }

    private File getImageFileFromDrawable(Context context, int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test_image.png");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            Log.d(TAG, "exception in getting image from drawable: " + e.getMessage());
            imageFile = null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "exception in getting image from drawable (close stream)");
            }
        }
        return imageFile;
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

            Log.i(TAG, "bitmap saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.d(TAG, "exception in saving bitmap to file: " + e.getMessage());
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
            Log.d(TAG, "exception in getting file from resource: " + e.getMessage());
        }
        return tempFile;
    }

    private File convertResponseBodyToImage(ResponseBody responseBody, String filePath) throws IOException {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        File imageFile = null;

        try {
            inputStream = responseBody.byteStream();
            outputStream = new FileOutputStream(filePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                Log.d(TEMP, String.valueOf(totalBytesRead));
            }
            imageFile = new File(filePath);
            Log.d(TAG, "成功保存图片到：" + filePath);

        } catch (Exception e) {
            Log.d(TAG, "exception in saving image: \n" + e.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            responseBody.close();
        }

        return imageFile;
    }

    private File convertResponseBodyToVideo(ResponseBody responseBody, String filePath) throws IOException {
        File file = new File(filePath);
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
        bufferedSink.writeAll(responseBody.source());
        bufferedSink.close();
        return file;
    }

    private void setTextView(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private void okhttpGet(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        try (okhttp3.Response response = client.newCall(request).execute()) {
            final String responseBody = response.body().string();
            setTextView("收到回复：" + responseBody);
        }
    }

    private void okhttpGet(String url, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder httpUrl = HttpUrl.parse(url).newBuilder();
        Map<String, String> map = new HashMap<String, String>(params);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            httpUrl.addQueryParameter(entry.getKey(), entry.getValue());
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(httpUrl.build())
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                // 处理响应
                String responseBody = response.body().string();
                setTextView("收到回复：" + responseBody);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                setTextView("Error: " + e.getMessage());
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
                setTextView("收到回复：" + responseBody);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                setTextView("Error: " + e.getMessage());
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
                setTextView("收到回复！");
                try {
                    ResponseBody responseBody = response.body();

                    String filePath = MainActivity.this.getFilesDir() + File.separator + "image.jpg";
                    File imageFile = convertResponseBodyToImage(responseBody, filePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                            textView.setText("获取到上图");
                        }
                    });

                } catch (Exception e) {
                    Log.d(TAG, "exception in handling response: \n" + e.getMessage());
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                setTextView("Error: \n" + e.getMessage());
            }
        });
    }

    private CompletableFuture<CustomResponse> okhttpPost(String url, Map<String, String> params, File imageFile, Boolean isImage, Boolean isText) {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加文本参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
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

        CompletableFuture<CustomResponse> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {
                    CustomResponse customResponse = new CustomResponse();
                    ResponseBody responseBody = response.body();

                    if (isImage && !isText) {
                        String filePath = MainActivity.this.getFilesDir() + File.separator + "image.jpg";
                        File imageFile = convertResponseBodyToImage(responseBody, filePath);
//                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        customResponse.setImage(imageFile);
                    } else if (!isImage && isText) {
                        String responseString = responseBody.string();
                        customResponse.setText(responseString);
                    } else if (isImage && isText) {
                        // TODO: 处理返回体既需要图片又需要文本的情况
                    }

                    future.complete(customResponse);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    private CompletableFuture<CustomResponse> okhttpPost(String url, Map<String, String> params, File imageFile,  File videoFile, Boolean isImage, Boolean isText, Boolean isVideo) {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        // 添加文本参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        // 添加图片文件
        if (imageFile != null) {
            multipartBuilder.addFormDataPart("image", imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imageFile));
        }

        // 添加视频文件
        if (videoFile != null) {
            multipartBuilder.addFormDataPart("video", videoFile.getName(),
                    RequestBody.create(MediaType.parse("video/*"), videoFile));
        }

        RequestBody requestBody = multipartBuilder.build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        CompletableFuture<CustomResponse> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {
                    CustomResponse customResponse = new CustomResponse();
                    ResponseBody responseBody = response.body();

                    if (isImage && !isText && !isVideo) {
                        String filePath = MainActivity.this.getFilesDir() + File.separator + "image.jpg";
                        File imageFile = convertResponseBodyToImage(responseBody, filePath);
                        customResponse.setImage(imageFile);
                    } else if (!isImage && isText && !isVideo) {
                        String responseString = responseBody.string();
                        customResponse.setText(responseString);
                    } else if (!isImage && !isText && isVideo) {
                        String filePath = MainActivity.this.getFilesDir() + File.separator + "video.mp4";
                        File videoFile = convertResponseBodyToVideo(responseBody, filePath);
                        customResponse.setVideo(videoFile);
                    } else if (isImage && isText && !isVideo) {
                        // TODO: 处理返回体既需要图片又需要文本的情况
                    } else if (isImage && !isText && isVideo) {
                        // TODO: 处理返回体既需要图片又需要视频的情况
                    } else if (!isImage && isText && isVideo) {
                        // TODO: 处理返回体既需要文本又需要视频的情况
                    } else if (isImage && isText && isVideo) {
                        // TODO: 处理返回体既需要图片又需要文本又需要视频的情况
                    }

                    future.complete(customResponse);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

}
