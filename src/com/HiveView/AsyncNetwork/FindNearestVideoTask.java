package com.HiveView.AsyncNetwork;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.HiveView.FTPSession;
import com.HiveView.R;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Use a path class instead of manipulating strings- too error prone
public class FindNearestVideoTask extends AsyncTask<Calendar, Void, String> {
    private static final String TAG = "FindNearestVideoTask";
    private static final String BASE_DIR = "/usr/local/bee/beemon/pit1";

    private FTPClient ftp;
    private OnNearestVideoFound callback;
    private Context context;

    public FindNearestVideoTask(Context context, OnNearestVideoFound callback) {
        ftp = FTPSession.getInstance();
        this.callback = callback;
        this.context = context;
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

    private FTPFile findNearestVideo(FTPFile[] files, String videoNamePrefix) {
        // TODO: Get the regex to work
        Log.v(TAG, "Finding nearest video to: " + videoNamePrefix.trim());
//        Pattern p = Pattern.compile(context.getString(R.string.video_basename_prefix_pattern));
        // Extract time from filename
//        Matcher m = p.matcher(videoNamePrefix);
        String hour = videoNamePrefix.substring(11, 13);
        String min = videoNamePrefix.substring(14, 16);
        Log.v(TAG, "Substrings: " + hour + " " + min);
        int minutesFrom0 = (Integer.parseInt(hour) * 60) + (Integer.parseInt(min));

        int minDistance = Integer.MAX_VALUE;
        FTPFile nearest = null;
        for(FTPFile file : files) {
            hour = file.getName().substring(11, 13);
            min = file.getName().substring(14, 16);
            int fileMinutesFrom0 = (Integer.parseInt(hour) * 60) + (Integer.parseInt(min));
            int distance = Math.abs(fileMinutesFrom0 - minutesFrom0);
            if (distance < minDistance) {
                nearest = file;
                minDistance = distance;
            }
        }
        return nearest;
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
        // If theres a match, return it, otherwise return the nearest video
        return match == null ? findNearestVideo(files, videoNamePrefix) : match;
    }
}
