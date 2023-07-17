package com.example.blank;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "ww";

    private StyledPlayerView playerView;
    private SimpleExoPlayer player;
    private Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        playButton = findViewById(R.id.btn_play);

        // 创建ExoPlayer实例
        player = new SimpleExoPlayer.Builder(this).build();

        // 将播放器与播放器视图关联
        playerView.setPlayer(player);

        // 准备要播放的媒体资源
        Uri mediaUri = Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4");
        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        player.setMediaItem(mediaItem);

        // 准备播放器
        player.prepare();

        // 设置播放按钮的点击事件
        playButton.setOnClickListener(this);

        Log.d(TAG, "Prepared");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放播放器资源
        player.release();
        Log.d(TAG, "Released");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "Clicked");
        if (view.getId() == R.id.btn_play) {
            if (player.isPlaying()) {
                player.pause();
                playButton.setText("Play");
                Log.d(TAG, "Play");
            } else {
                player.play();
                playButton.setText("Pause");
                Log.d(TAG, "Pause");
            }
        }
    }
}