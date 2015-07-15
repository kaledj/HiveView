package com.HiveView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;

public class HelloWorld extends Activity implements OnDownloadCompleted {

    private VideoView vidView;
    int position;
    private FTPClient ftp;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ftp = new FTPClient();
//        new InitFTPTask(this, ftp).execute(ftp);

        File f = new File("/storage/emulated/0/TestVideo/testout.mkv");
//        Log.d("", "" + f.exists());
        onDownloadCompleted(f);
    }

    public void onDownloadCompleted(File downloadedFile) {
        Log.i("HelloWorld", "Download callback:" + downloadedFile.getName());

        setContentView(R.layout.main);
        vidView = (VideoView) findViewById(R.id.videoView);
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
        MediaController vidControl = new MediaController(HelloWorld.this);
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

