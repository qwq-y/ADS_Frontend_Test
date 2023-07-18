package com.example.screenshoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ww";
    private static final int REQUEST_CODE_SCREENSHOT = 1;
    private View rootView;
    private ImageView imageView;
    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = getWindow().getDecorView().getRootView();

        textView = findViewById(R.id.textView);

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.test_image);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断是否有权限，如果没有则请求读取外部存储器的权限（用于保存截图）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        takeScreenshot();
                    } else {
                        Log.d(TAG, "requesting");
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_SCREENSHOT);
                    }
                } else {
                    takeScreenshot();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "on request");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SCREENSHOT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予了外部存储器的权限
                takeScreenshot();
            }
        }
    }

    public void takeScreenshot() {
        Log.d(TAG, "taking");

        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        showScreenshot(bitmap);
    }

    private void showScreenshot(Bitmap bitmap) {
        Log.d(TAG, "saving");

        imageView.setImageBitmap(bitmap);
        textView.setText("已截图");

    }
}