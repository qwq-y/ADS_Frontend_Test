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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView recyclerView;
    private VideoView videoView;

    private List<Bitmap> bitmapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.imageRecyclerView);

        textView = findViewById(R.id.textView);

        imageView = findViewById((R.id.imageView));
        imageView.setImageResource(R.drawable.test_search_k);

//        videoView = findViewById(R.id.videoView);

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
                            testDINO();
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

//    private void testVideo() {
//
//        File videoFile = getVideoFileFromRaw(MainActivity.this, R.raw.test_video);
//
//        Map<String, String> params = new HashMap<>();
//        params.put("point_coord_0", "200");
//        params.put("point_coord_1", "100");
//        params.put("point_label", "1");
//        params.put("use_mask", "0");
//        params.put("mode", "0");
//
//        String url = "http://172.18.36.107:1200/video";
//
//        postVideo(url, params, null, videoFile)
//                .thenAccept(customResponse -> {
//                    File video = customResponse.getVideo();
//
//                    if (video != null) {
//                        runOnUiThread(() -> {
//                            VideoView videoView = findViewById(R.id.videoView);
//                            videoView.setVideoPath(video.getAbsolutePath());
//                            videoView.start();
//                        });
//                    }
//                })
//                .exceptionally(e -> {
//                    // 处理异常
//                    Log.d(TAG, "exception in interface: " + e.getMessage());
//                    return null;
//                });
//
//    }

    private void testDINO() {

        File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_search_k);

        Map<String, String> params = new HashMap<>();
        params.put("K", "11");

        String url = "http://172.18.36.107:5001/searchK";

        postHOP(url, params, imageFile)
                .thenAccept(customResponse -> {

                    Map<String, String> images = customResponse.getImages();
                    Map<String, String> links = customResponse.getLinks();
                    String message = customResponse.getMessage();
                    String status = customResponse.getStatus();

                    Log.d(TAG, "status: \n" + status);
                    Log.d(TAG, "message: \n" + message);
                    Log.d(TAG, "links: \n" + links.toString());
                    Log.d(TAG, "images: \n");
                    for (Map.Entry<String, String> entry : images.entrySet()) {
                        Log.d(TAG, entry.toString());
                    }

                    bitmapList = convertBase64ImagesToBitmaps(images);

                    runOnUiThread(() -> {
                        setTextView("搜索到相似商品");
                        displayImages();
                    });

                })
                .exceptionally(e -> {
                    // 处理异常
                    Log.d(TAG, "exception in interface: " + e.getMessage());
                    return null;
                });
    }

    private void testSAM() {
        File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_search_k);

        Map<String, String> params = new HashMap<>();
        params.put("point_coords", "230, 150");
        params.put("point_labels", "1");
        params.put("mode", "point");

        String url = "http://172.18.36.107:5001/sam";

        postHOP(url, params, imageFile)
                .thenAccept(customResponse -> {

                    Map<String, String> images = customResponse.getImages();
                    String message = customResponse.getMessage();
                    String status = customResponse.getStatus();

                    Log.d(TAG, "status: \n" + status);
                    Log.d(TAG, "message: \n" + message);
                    Log.d(TAG, "images: \n" + images.toString());

                    String imageStr = images.get("image4dino");
                    byte[] decodedBytes = Base64.decode(imageStr, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    runOnUiThread(() -> {
                        setTextView("image4dino");
                        imageView.setImageBitmap(bitmap);
                    });

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

    private List<Bitmap> convertBase64ImagesToBitmaps(Map<String, String> imageMap) {
        List<Bitmap> bitmapList = new ArrayList<>();
        for (String imageData : imageMap.values()) {
            byte[] decodedString = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            bitmapList.add(bitmap);
        }
        return bitmapList;
    }

    private void displayImages() {
        ImageAdapter imageAdapter = new ImageAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(imageAdapter);
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

                    // 获取消息
                    String message = jsonObject.getString("Message");
                    if (message != null) {
                        customResponse.setMessage(message);
                    }

                    // 获取图像 Map
                    JSONObject encodedImagesObject = jsonObject.getJSONObject("Content");
                    if (encodedImagesObject != null) {
                        Map<String, String> images = new HashMap<>();
                        Iterator<String> keys = encodedImagesObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String encodedImage = encodedImagesObject.getString(key);
                            images.put(key, encodedImage);
                        }
                        customResponse.setImages(images);
                    }

                    // 获取链接 Map
                    JSONObject linksObject = jsonObject.getJSONObject("Link");
                    if (linksObject != null) {
                        Map<String, String> links = new HashMap<>();
                        Iterator<String> keys = linksObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            String link = linksObject.getString(key);
                            links.put(key, link);
                        }
                        customResponse.setLinks(links);
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

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Bitmap bitmap = bitmapList.get(position);
            holder.imageView.setImageBitmap(bitmap);
        }

        @Override
        public int getItemCount() {
            return bitmapList.size();
        }

        private class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

}
