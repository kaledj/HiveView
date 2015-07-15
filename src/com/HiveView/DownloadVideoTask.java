package com.HiveView;

import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.VideoView;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import android.content.Context;

/**
 * DownloadVideoTask
 *
 * Asynchronously downloads the most recent video file
 */
public class DownloadVideoTask extends AsyncTask<FTPClient, Void, File> {
    private static final String TAG = "DownloadVideoTask";

    private OnDownloadCompleted onDownloadCompleted;
    private FTPClient ftp;

    public DownloadVideoTask(OnDownloadCompleted onDownloadCompleted) {
        this.onDownloadCompleted = onDownloadCompleted;
    }

    @Override
    protected File doInBackground(FTPClient... clients) {
        Log.i(TAG, "Doing DownloadVideoTask in background");
        ftp = clients[0];
        try {
            FTPFile[] dirs = ftp.listDirectories("/usr/local/bee/beemon/pit1");
            int reply = ftp.pwd();
            Log.i(TAG, ftp.getReplyString());
            Log.i(TAG, "" + dirs.length);
            sortFilesByTimestamp(dirs);

            String latestDir = "/usr/local/bee/beemon/pit1/" + dirs[0].getName() + "/video";
            Log.i(TAG, latestDir);
            FTPFile[] files = ftp.listFiles(latestDir);
            sortFilesByTimestamp(files);
            String latestFile = files[0].getName();

            File temp = File.createTempFile(latestFile, ".h264");
            temp.deleteOnExit();
            Log.i(TAG, temp.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(temp);
            ftp.retrieveFile(latestDir + "/" + latestFile, fos);
            fos.close();
            return temp;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(File downloadedFile) {
        try {
            Log.d(TAG, "Async task complete.");
            ftp.disconnect();
            onDownloadCompleted.onDownloadCompleted(downloadedFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void sortFilesByTimestamp(FTPFile[] files) {
        Arrays.sort(files, new Comparator<FTPFile>() {
            @Override
            public int compare(FTPFile lhs, FTPFile rhs) {
                return rhs.getTimestamp().compareTo(lhs.getTimestamp());
            }
        });
    }
}
