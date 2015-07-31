package com.HiveView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.HiveView.AsyncNetwork.FindNearestVideoTask;
import com.HiveView.AsyncNetwork.OnNearestVideoFound;
import org.apache.commons.net.ftp.FTPClient;

import java.util.Calendar;

public class TimePickerActivity extends Activity implements OnNearestVideoFound {
    private static final String TAG = "TimePickerActivity";

    private FTPClient ftp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datetime_picker);
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

        new FindNearestVideoTask(this).execute(cal);
        view.setEnabled(false);
    }

    /**
     * Checks if the requested video exists on the server
     * @param cal The timestamp of the requested video
     */
    public boolean isVideoAvailable(Calendar cal) {
        // TODO: Return the value
        return false;
    }

    public void onNearestVideoFound(String videoPath) {
        if(!videoPath.equals("")) {
            Log.v(TAG, "Video found at " + videoPath);
            Intent intent = new Intent(this, VideoViewerActivity.class);
            intent.putExtra("videoPath", videoPath);
            startActivity(intent);
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
}