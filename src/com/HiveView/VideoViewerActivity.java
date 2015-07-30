package com.HiveView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import com.HiveView.AsyncNetwork.OnDownloadCompleted;

import java.io.File;

public class VideoViewerActivity extends Activity implements OnDownloadCompleted {
    private static final String TAG = "VideoViewerActivity";

    private VideoView vidView;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
    }

    public void onDownloadCompleted(File downloadedFile) {
        Log.d(TAG, "Download callback:" + downloadedFile.getName());

        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vidView.seekTo(position);
                vidView.start();
            }
        });
        Uri vidUri = Uri.parse(downloadedFile.getAbsolutePath());
        Log.i("", downloadedFile.getAbsolutePath());
//        vidView.setVideoURI(vidUri);
        vidView.setVideoURI(Uri.parse("android.resource://com.HiveView/r/" + R.raw.testmp4));
        MediaController vidControl = new MediaController(VideoViewerActivity.this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);

        vidView.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("Position", vidView.getCurrentPosition());
        vidView.pause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt("Position");
        vidView.seekTo(position);
    }
}
