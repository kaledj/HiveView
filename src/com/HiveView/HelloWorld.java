package com.HiveView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class HelloWorld extends Activity {

    private VideoView vidView;
    private FTPClient ftp;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ftp = new FTPClient();
        new InitFTPTask().execute(ftp);

//        setContentView(R.layout.main);
//        vidView = (VideoView) findViewById(R.id.videoView);
//
//        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
//        Uri vidUri = Uri.parse(vidAddress);
//
//        vidView.setVideoURI(vidUri);
//
//        MediaController vidControl = new MediaController(this);
//        vidControl.setAnchorView(vidView);
//        vidView.setMediaController(vidControl);
//
//        vidView.start();
    }
}
