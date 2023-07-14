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
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    private Button sendButton;
    private TextView textView;
    private ImageView imageView;
    private VideoView videoView;

    int imageNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        imageView = findViewById((R.id.imageView));

        videoView = findViewById(R.id.videoView);

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
                            testHop();
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

    private void testVideo() {

        File videoFile = getVideoFileFromRaw(MainActivity.this, R.raw.test_video);

        Map<String, String> params = new HashMap<>();
        params.put("point_coord_0", "200");
        params.put("point_coord_1", "100");
        params.put("point_label", "1");
        params.put("use_mask", "0");
        params.put("mode", "0");

        String url = "http://172.18.36.107:1200/video";

        postVideo(url, params, null, videoFile)
                .thenAccept(customResponse -> {
                    File video = customResponse.getVideo();

                    if (video != null) {
                        runOnUiThread(() -> {
                            VideoView videoView = findViewById(R.id.videoView);
                            videoView.setVideoPath(video.getAbsolutePath());
                            videoView.start();
                        });
                    }
                })
                .exceptionally(e -> {
                    // 处理异常
                    Log.d(TAG, "exception in interface: " + e.getMessage());
                    return null;
                });

    }

    private void testHop() {
        File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_image);

        Map<String, String> params = new HashMap<>();
        params.put("point_coord_0", "200");
        params.put("point_coord_1", "100");
        params.put("point_label", "1");
        params.put("use_mask", "0");
        params.put("mode", "0");

        String url = "http://192.168.3.124:80/imagelist";

        postHOP(url, params, imageFile)
                .thenAccept(customResponse -> {

                    List<String> encodedImages = customResponse.getImages();
                    List<Map<String, Object>> messages = customResponse.getMessages();

                    String image = encodedImages.get(imageNo);
                    byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    runOnUiThread(() -> {
                        setTextView("image number: " + imageNo);
                        imageView.setImageBitmap(bitmap);
                    });

                    for (Map<String, Object> message : messages) {
                        Log.d(TAG, message.toString());
                    }

                })
                .exceptionally(e -> {
                    // 处理异常
                    Log.d(TAG, "exception in interface: " + e.getMessage());
                    return null;
                });
    }

    public static File getVideoFileFromRaw(Context context, int resourceId) {
        File videoFile = new File(context.getFilesDir(), "test_video.mp4");

        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            OutputStream outputStream = new FileOutputStream(videoFile);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return videoFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    private void setTextView(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private CompletableFuture<CustomResponse> postVideo(String url, Map<String, String> params, File imageFile,  File videoFile) {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        if (imageFile != null) {
            multipartBuilder.addFormDataPart("image", imageFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imageFile));
        }
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

                    String filePath = MainActivity.this.getFilesDir() + File.separator + "video.mp4";
                    File videoFile = new File(filePath);
                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(videoFile));
                    bufferedSink.writeAll(responseBody.source());
                    bufferedSink.close();

                    customResponse.setVideo(videoFile);

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

    private CompletableFuture<CustomResponse> postHOP(String url, Map<String, String> params, File imageFile) {
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
                    String responseString = responseBody.string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    // 获取状态码
                    String status = jsonObject.getString("Status");
                    if (status != null) {
                        customResponse.setStatus(status);
                    }

                    // 获取图像列表
                    JSONArray encodedImagesArray = jsonObject.getJSONArray("ImageBytes");
                    if (encodedImagesArray != null) {
                        List<String> images = new ArrayList<>();
                        for (int i = 0; i < encodedImagesArray.length(); i++) {
                            String encodedImage = encodedImagesArray.getString(i);
                            images.add(encodedImage);
                        }
                        customResponse.setImages(images);
                    }

                    // 获取消息列表
                    JSONArray messageListArray = jsonObject.getJSONArray("Message");
                    if (messageListArray != null) {
                        List<Map<String, Object>> messageList = new ArrayList<>();
                        for (int i = 0; i < messageListArray.length(); i++) {
                            JSONObject messageDict = messageListArray.getJSONObject(i);
                            Map<String, Object> messageMap = new HashMap<>();

                            Iterator<String> keys = messageDict.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                Object value = messageDict.get(key);
                                messageMap.put(key, value);
                            }
                            messageList.add(messageMap);
                        }
                        customResponse.setMessages(messageList);
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
