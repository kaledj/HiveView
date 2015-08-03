package com.HiveView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.HiveView.AsyncNetwork.*;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.Calendar;

public class TimePickerActivity extends Activity implements OnNearestVideoFound, OnVideoConverted, OnDownloadCompleted {
    private static final String TAG = "TimePickerActivity";

    private FTPClient ftp;
    private AlertDialog convertDialog;
    private AlertDialog downloadDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datetime_picker);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ftp = FTPSession.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        Button b = (Button) findViewById(R.id.searchButton);
        b.setEnabled(true);
    }

    public void searchClicked(View view) {
        Log.i(TAG, "Search button clicked");
        // Get the date and time
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        Log.v(TAG, datePicker.getYear() + " picked");

        new FindNearestVideoTask(this, this).execute(cal);
        view.setEnabled(false);
    }

    public void onNearestVideoFound(String videoPath) {
        if(!videoPath.equals("")) {
            new ConvertVideoFileTask(this).execute(videoPath);
            buildConvertDialog();
            Log.v(TAG, "Video found at " + videoPath);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Video not found")
                    .setMessage("No video found for the selected date and time.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert =  builder.create();
            alert.show();
            findViewById(R.id.searchButton).setEnabled(true);
        }
    }

    public void onVideoConverted(String videoFilename) {
        convertDialog.dismiss();
        new DownloadVideoTask(this, this).execute(videoFilename);
        buildDownloadDialog();
    }

    public void onDownloadCompleted(File downloadedFile) {
        downloadDialog.dismiss();
        Intent intent = new Intent(this, VideoViewerActivity.class);
        intent.putExtra("videoPath", downloadedFile.getAbsolutePath());
        startActivity(intent);
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

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//    }
}