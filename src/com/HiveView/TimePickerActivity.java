package com.HiveView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.HiveView.AsyncNetwork.FindNearestVideoTask;
import com.HiveView.AsyncNetwork.OnNearestVideoFound;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
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

    }
}