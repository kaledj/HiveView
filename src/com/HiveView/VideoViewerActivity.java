package com.HiveView;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import com.HiveView.AsyncNetwork.ConvertVideoFileTask;
import com.HiveView.AsyncNetwork.DownloadVideoTask;
import com.HiveView.AsyncNetwork.OnDownloadCompleted;
import com.HiveView.AsyncNetwork.OnVideoConverted;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

public class VideoViewerActivity extends Activity implements OnVideoConverted, OnDownloadCompleted {
    private static final String TAG = "VideoViewerActivity";

    private VideoView vidView;
    private int position;
    private FTPClient ftp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_viewer);
        vidView = (VideoView) findViewById(R.id.videoView);

        ftp = FTPSession.getInstance();
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("videoPath");
        new ConvertVideoFileTask(this).execute(videoPath);
    }

    public void onVideoConverted(String videoFilename) {
        new DownloadVideoTask(this).execute(videoFilename);
    }

    public void onDownloadCompleted(File downloadedFile) {
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vidView.seekTo(position);
                vidView.start();
            }
        });
        Uri vidUri = Uri.parse(downloadedFile.getAbsolutePath());
        Log.i(TAG, downloadedFile.getAbsolutePath());
//        vidView.setVideoURI(vidUri);
        vidView.setVideoURI(Uri.parse("android.resource://com.HiveView/r/" + R.raw.vid_29_07));
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
