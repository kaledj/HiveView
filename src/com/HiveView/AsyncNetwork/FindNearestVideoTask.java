package com.HiveView.AsyncNetwork;

import android.os.AsyncTask;
import android.util.Log;
import com.HiveView.FTPSession;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

// TODO: Use a path class instead of manipulating strings- too error prone
public class FindNearestVideoTask extends AsyncTask<Calendar, Void, String> {
    private static final String TAG = "FindNearestVideoTask";
    private static final String BASE_DIR = "/usr/local/bee/beemon/pit1";

    private FTPClient ftp;
    private OnNearestVideoFound callback;

    public FindNearestVideoTask(OnNearestVideoFound callback) {
        ftp = FTPSession.getInstance();
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Calendar... calendars) {
        Calendar cal = calendars[0];

        // Build the file paths based on calendar
        Log.v(TAG, cal.get(Calendar.YEAR) + "");
        String dayDir = String.format("%02d-%02d-%4d", cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR));
        String videoDir = BASE_DIR + "/" + dayDir + "/video";
        String vidNamePrefix = dayDir + "_" + String.format("%02d:%02d:",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE));

        try {
            Log.v(TAG, "Listing " + videoDir);
            FTPFile[] files = ftp.listFiles(videoDir);
            FTPFile match = findMatchingVideo(files, vidNamePrefix);
            if(match != null) {
                return videoDir + "/" + match.getName();
            }
        } catch(IOException e) {
            Log.e(TAG, "Failed to list files.", e);
        }
        return "";
    }

    @Override
    protected void onPostExecute(String filePath) {
        callback.onNearestVideoFound(filePath);
    }

    private String  findNearestDay(FTPFile[] files, Calendar cal) {
        // TODO: Return closest day if there is no exact match
        return "";
    }

    private String findNearestMinute(FTPFile[] files, Calendar cal) {
        // TODO: Return closest hour/minute (HH:MM) if there is no exact match
        return "";
    }

    /**
     * Finds a video that matches the HH:MM:* prefix
     * @param files The files to search
     * @param videoNamePrefix The prefix to match
     * @return The matching file, or null
     */
    private FTPFile findMatchingVideo(FTPFile[] files, String videoNamePrefix) {
        FTPFile match = null;
        for(FTPFile file : files) {
            if (Pattern.matches(videoNamePrefix + ".*", file.getName())) {
                match = file;
            }
        }
        return match;
    }
}
