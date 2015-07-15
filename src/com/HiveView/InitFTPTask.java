package com.HiveView;

import android.os.AsyncTask;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

/**
 * InitFTPTask
 *
 * Asynchronously initializes the FTP connection
 */
public class InitFTPTask extends AsyncTask<FTPClient, Void, Boolean> {

    private FTPClient ftp;
    private OnDownloadCompleted onDownloadCompleted;

    public InitFTPTask(OnDownloadCompleted onDownloadCompleted, FTPClient ftp) {
        this.onDownloadCompleted = onDownloadCompleted;
        this.ftp = ftp;
    }

    @Override
    protected Boolean doInBackground(FTPClient... clients) {
        try {
            ftp.connect("cs.appstate.edu");
            ftp.enterLocalPassiveMode();
            int reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.exit(1);
            }
            ftp.login("bee", "cs.13,bee");
            return true;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            new DownloadVideoTask(onDownloadCompleted).execute(ftp);
        }
    }
}
