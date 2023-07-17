package com.example.blank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ww";

    private ExoPlayer player;
    private PlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = new ExoPlayer.Builder(this).build();
        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4"));

        player.addMediaItem(mediaItem);

        player.prepare();

        player.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放播放器资源
        player.release();
    }
}