package com.HiveView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
    private AlertDialog convertDialog;
    private AlertDialog downloadDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_viewer);
        vidView = (VideoView) findViewById(R.id.videoView);

        ftp = FTPSession.getInstance();
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("videoPath");
        new ConvertVideoFileTask(this).execute(videoPath);
        buildConvertDialog();
    }

    public void onVideoConverted(String videoFilename) {
        convertDialog.dismiss();
        new DownloadVideoTask(this, this).execute(videoFilename);
        buildDownloadDialog();
    }

    public void onDownloadCompleted(File downloadedFile) {
        downloadDialog.dismiss();
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vidView.seekTo(position);
                vidView.start();
            }
        });

        Log.v(TAG, "Path: " + downloadedFile.getPath());
        Log.i(TAG, "Absolute path: " + downloadedFile.getAbsolutePath());

        // Set the data source
        vidView.setVideoURI(Uri.parse(downloadedFile.getAbsolutePath()));

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

    private void buildConvertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status")
                .setMessage("Converting video on server.");
        convertDialog =  builder.create();
        convertDialog.show();
    }

    private void buildDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Status")
                .setMessage("Downloading video from server.");
        downloadDialog =  builder.create();
        downloadDialog.show();
    }
}
