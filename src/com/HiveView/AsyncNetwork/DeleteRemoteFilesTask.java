package com.HiveView.AsyncNetwork;

import android.os.AsyncTask;
import android.util.Log;
import com.HiveView.FTPSession;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class DeleteRemoteFilesTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "DeleteRemoteFilesTask";

    private FTPClient ftp;

    public DeleteRemoteFilesTask() {
        ftp = FTPSession.getInstance();
    }

    @Override
    public Void doInBackground(String... filenames) {
        String filename = filenames[0];
        try {
            ftp.deleteFile(filename);
        } catch(IOException e) {
            Log.e(TAG, "Error deleting remote file: " + filename, e);
        }
        return null;
    }
}
