package com.HiveView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.VideoView;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.net.PasswordAuthentication;

public class LoginActivity extends Activity implements OnFTPLogin {

    private static final String TAG = "LoginActivity";
    private VideoView vidView;
    private int position;
    private FTPClient ftp;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_splash);
//        setContentView(R.layout.video_viewer);
//        vidView = (VideoView) findViewById(R.id.videoView);
//        new InitFTPTask(this, ).execute(ftp);
        ftp = new FTPClient();
        File f = new File("/storage/emulated/0/TestVideo/testout.mkv");
//        Log.d("", "" + f.exists());
//        onDownloadCompleted(f);
    }


    public void onFTPLogin(Boolean status) {
        Intent intent = new Intent(this, TimePickerActivity.class);
        startActivity(intent);
    }

    public void connectClicked(View view) {
        // Get the username and password from the text fields
        EditText usernameInput = (EditText) findViewById(R.id.usernameInput);
        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
        String user = usernameInput.getText().toString();
        char[] password = new char[passwordInput.length()];
        passwordInput.getText().getChars(0, passwordInput.length(), password, 0);
        PasswordAuthentication loginInfo = new PasswordAuthentication(user, password);

        // Create the background login task
        new InitFTPTask(this, ftp).execute(loginInfo);
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


}

