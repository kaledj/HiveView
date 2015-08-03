package com.HiveView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.nio.Buffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoViewerActivity extends Activity implements OnVideoConverted, OnDownloadCompleted {
    private static final String TAG = "VideoViewerActivity";

    private VideoView vidView;
    private int position;
    private FTPClient ftp;
    private AlertDialog convertDialog;
    private AlertDialog downloadDialog;
    private File currentvideo;
    private File nextVideo;
    private String currentVideoPath;
    private int hour;
    private int minute;
    private boolean isLocal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_viewer);
        vidView = (VideoView) findViewById(R.id.videoView);

        ftp = FTPSession.getInstance();
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("videoPath");
        Log.v(TAG, "Video path: " + videoPath);
        if(intent.hasExtra("isLocal")) {
            isLocal = intent.getBooleanExtra("isLocal", false);
        }
        if(isLocal) {
            playVideo(new File(getFilesDir().getPath(), videoPath));
        } else {
            new ConvertVideoFileTask(this).execute(videoPath);
            buildConvertDialog();
        }
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

    public void onVideoConverted(String videoFilename) {
        currentVideoPath = videoFilename;
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
        currentvideo = downloadedFile;
        playVideo(downloadedFile);
    }

    public void playVideo(final File videoFile) {
        Log.v(TAG, "Playing video: " + videoFile.getPath());

        // Set the data source
        vidView.setVideoURI(Uri.parse(videoFile.getAbsolutePath()));

        // Play the video
        MediaController vidControl = new MediaController(VideoViewerActivity.this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
        vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (videoFile.exists() && !isLocal) {
                    videoFile.delete();
                }
            }
        });
        vidView.start();
    }

    public void downloadNext() {
        // TODO: Somewhere else, create a sorted list of files on the server and just get the next
        minute += 1;
        if(minute > 59) {
            minute = 0;
            hour += 1;
        }
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

    private boolean saveCurrentVideo() {
        if(isLocal || currentvideo == null) {
            return false;
        } else if(currentvideo.exists()) {
            try {
                BufferedOutputStream bos = new BufferedOutputStream(
                        openFileOutput(currentvideo.getName(), Context.MODE_PRIVATE));
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(currentvideo));
                byte[] buffer = new byte[1024];
                int dataRead = bis.read(buffer);
                while (dataRead != -1) {
                    bos.write(buffer);
                    dataRead = bis.read(buffer);
                }
                bos.close();
                bis.close();
            } catch (IOException e) {
                Log.e(TAG, "Error saving file.", e);
                return false;
            } finally {
                currentvideo.delete();
            }
        }
        return true;
    }
}
