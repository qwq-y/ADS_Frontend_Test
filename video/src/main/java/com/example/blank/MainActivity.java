package com.example.blank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.ClippingMediaSource;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ww";

    private ExoPlayer player;
    private PlayerView playerView;
    private SeekBar seekBar;

    private long startPositionMs = 0;
    private long endPositionMs = 0;
    private long preStartPositionMs = 30000;
    private long preEndPositionMs = 50000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 播放器和视图
        player = new ExoPlayer.Builder(this).build();
        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);

        // 要播放的媒体文件
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse("https://media.w3.org/2010/05/sintel/trailer.mp4"));
        player.addMediaItem(mediaItem);

        // 创建和设置媒体源
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(this);
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(mediaItem);
        player.setMediaSource(mediaSource);

        player.prepare();

        player.setPlayWhenReady(true);

        clippingMediaSource();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放播放器资源
        player.release();
    }

    private MediaItem getMediaItemFromRawResource(int resourceId) {
        Uri uri = Uri.parse("rawresource://" + getPackageName() + "/" + resourceId);
        return MediaItem.fromUri(uri);
    }

    private void clippingMediaSource() {
        startPositionMs = preStartPositionMs;
        endPositionMs = preEndPositionMs;
        doClippingMediaSource();
    }

    private void doClippingMediaSource() {

        // 创建默认的MediaSourceFactory
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(this);

        // 从MediaItem创建媒体源
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(player.getMediaItemAt(0));

        // 创建裁剪媒体源
        ClippingMediaSource clippingMediaSource = new ClippingMediaSource(
                mediaSource,
                startPositionMs * 1000,
                endPositionMs * 1000
        );

        player.setMediaSource(clippingMediaSource);
        player.prepare();
        player.seekTo(startPositionMs);
        player.setPlayWhenReady(true);
    }
}