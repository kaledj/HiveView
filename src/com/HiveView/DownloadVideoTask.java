package com.HiveView;

import android.os.AsyncTask;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import android.util.Log;
import java.io.IOException;

/**
 * DownloadVideoTask
 *
 * Asynchronously downloads the most recent video file
 */
public class DownloadVideoTask extends AsyncTask<FTPClient, Void, Boolean> {
    private static final String TAG = "DownloadVideoTask";

    @Override
    protected Boolean doInBackground(FTPClient... clients) {
        Log.i(TAG, "Doing DownloadVideoTask in background");
        FTPClient ftp = clients[0];
        try {
//            FTPFile[] files = ftp.listDirectories("/usr/local/bee/beemon/pit1");
            FTPFile[] files = ftp.listDirectories();
            for(FTPFile file : files) {
                System.out.println(file.getName());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
