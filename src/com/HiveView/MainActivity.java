package com.HiveView;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.net.PasswordAuthentication;

public class MainActivity extends Activity implements OnDownloadCompleted, OnFTPLogin {

    private static final String TAG = "MainActivity";
    private int position;
    private VideoView vidView;
    private FTPClient ftp;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_splash);
        vidView = (VideoView) findViewById(R.id.videoView);
//        new InitFTPTask(this, ).execute(ftp);
        ftp = new FTPClient();
        File f = new File("/storage/emulated/0/TestVideo/testout.mkv");
//        Log.d("", "" + f.exists());
//        onDownloadCompleted(f);
    }

    public void onDownloadCompleted(File downloadedFile) {
        Log.d(TAG, "Download callback:" + downloadedFile.getName());

        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                vidView.seekTo(position);
                vidView.start();
            }
        });
        Uri vidUri = Uri.parse(downloadedFile.getAbsolutePath());
        Log.i("", downloadedFile.getAbsolutePath());
//        vidView.setVideoURI(vidUri);
        vidView.setVideoURI(Uri.parse("android.resource://com.HiveView/r/" + R.raw.test));
        MediaController vidControl = new MediaController(MainActivity.this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);

        vidView.start();
    }

    public void onFTPLogin() {

    }

    public void connectClicked(View view) {
        // Get the username and password from the text fields
        EditText usernameInput = (EditText) findViewById(R.id.usernameInput);
        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
        String user = usernameInput.getText().toString();
        char[] password = new char[passwordInput.length()];
        passwordInput.getText().getChars(0, passwordInput.length(), password, 0);
        PasswordAuthentication loginInfo = new PasswordAuthentication(user, password);

        // Create the background login task and start switching the view out
        new InitFTPTask(this, ftp).execute(loginInfo);
        setContentView(R.layout.datetime_picker);
    }

    public void searchClicked(View view) {
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        // Grab date info
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        // Grab time info
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
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

