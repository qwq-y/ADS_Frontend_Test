package com.example.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private String TAG = "ww";

    private GestureDetector gestureDetector;

    int imageViewX, imageViewY, imageViewWidth, imageViewHeight;

    private Button sendButton;
    private TextView textView;
    private TextView textViewExtra;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureDetector = new GestureDetector(this, new MyGestureListener());

        textView = findViewById(R.id.textView);

        textViewExtra = findViewById(R.id.textViewExtra);

        imageView = findViewById((R.id.imageView));
        imageView.setImageResource(R.drawable.test_image);
        imageView.setOnTouchListener(this);

        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setImageViewInfo();
        }
    }

    private void setImageViewInfo() {
        int[] location = new int[2];
        imageView.getLocationOnScreen(location);
        imageViewX = location[0];
        imageViewY = location[1];
        imageViewWidth = imageView.getWidth();
        imageViewHeight = imageView.getHeight();
        Log.d(TAG, "image view info: \n" + imageViewX + "\n" + imageViewY + "\n" + imageViewWidth + "\n" + imageViewHeight);
        Log.d(TAG, "[" + imageViewX + ", " + (imageViewX + imageViewWidth) + "][" + imageViewY + ", " + (imageViewY + imageViewHeight) + "]");
    }

    private boolean isCoordinateInsideImage(int x, int y) {
//        return x >= imageViewX && x <= imageViewX + imageViewWidth &&
//                y >= imageViewY && y <= imageViewY + imageViewHeight;
        return x >= 0 && x <= imageViewWidth && y >= 0 && y <= imageViewHeight;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendButton) {
            textView.setText("完成！");
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.imageView) {
            gestureDetector.onTouchEvent(event);
            int x = (int) event.getX();
            int y = (int) event.getY();

            Log.d(TAG, "点击坐标：x = " + x + ", y = " + y);
            if (isCoordinateInsideImage(x, y)) {
                textView.setText("点击坐标：x = " + x + ", y = " + y);
            } else {
                textView.setText("点击坐标不在图片范围内");
            }
            return true;
        }
        return false;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            textViewExtra.setText("长按事件");
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            textViewExtra.setText("短按事件");
            return true;
        }
    }

}