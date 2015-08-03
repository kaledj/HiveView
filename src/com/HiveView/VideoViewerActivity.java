package com.HiveView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;
import com.HiveView.AsyncNetwork.ConvertVideoFileTask;
import com.HiveView.AsyncNetwork.DownloadVideoTask;
import com.HiveView.AsyncNetwork.OnDownloadCompleted;
import com.HiveView.AsyncNetwork.OnVideoConverted;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;

public class VideoViewerActivity extends Activity  {
    private static final String TAG = "VideoViewerActivity";

    private VideoView vidView;
    private String currentVideoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.video_viewer);
        vidView = (VideoView) findViewById(R.id.videoView);
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("videoPath");
        Log.v(TAG, "Video path: " + videoPath);
        playVideo(videoPath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video_viewer_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.video_viewer_save_video:
                return saveCurrentVideo();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void playVideo(String videoFilePath) {
        currentVideoPath = videoFilePath;
        Log.v(TAG, "Playing video: " + videoFilePath);
        // Set the data source
        vidView.setVideoPath(videoFilePath);
        // Play the video
        MediaController vidControl = new MediaController(VideoViewerActivity.this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
        vidView.start();
    }

    public void downloadNext() {
        // TODO: Somewhere else, create a sorted list of files on the server and just get the next
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
        vidView.seekTo(savedInstanceState.getInt("Position"));
    }

    private boolean saveCurrentVideo() {
        String[] split = currentVideoPath.split("/");
        String currentVideoBasename = split[split.length - 1];
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    openFileOutput("cached_" + currentVideoBasename, Context.MODE_WORLD_READABLE));
            BufferedInputStream bis = new BufferedInputStream(
                    openFileInput(currentVideoBasename));
            byte[] buffer = new byte[1024];
            int dataRead = bis.read(buffer);
            while (dataRead != -1) {
                bos.write(buffer);
                dataRead = bis.read(buffer);
            }
            bos.close();
            bis.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving file.", e);
            return false;
        } finally {
            new File(currentVideoPath).delete();
        }
    }
}
