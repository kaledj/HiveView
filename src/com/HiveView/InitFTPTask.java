package com.HiveView;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.PasswordAuthentication;

/**
 * InitFTPTask
 *
 * Asynchronously initializes the FTP connection
 */
public class InitFTPTask extends AsyncTask<PasswordAuthentication, Void, Boolean> {
    private static final String TAG = "InitFTPTask";
    private FTPClient ftp;
    private OnDownloadCompleted onDownloadCompleted;

    public InitFTPTask(OnDownloadCompleted onDownloadCompleted, FTPClient ftp) {
        this.onDownloadCompleted = onDownloadCompleted;
        this.ftp = new FTPClient();
    }

    @Override
    protected Boolean doInBackground(PasswordAuthentication... authentications) {
        PasswordAuthentication login = authentications[0];
        String user = login.getUserName();
        char[] password = login.getPassword();
        try {
            ftp.connect("cs.appstate.edu");
            ftp.enterLocalPassiveMode();
            int reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                Log.e(TAG, ftp.getReplyString());
                System.exit(1);
            }
            ftp.login(user, new String(password));
            return true;
        } catch(IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result) {
            new DownloadVideoTask(onDownloadCompleted).execute(ftp);
        } else {
            System.exit(0);
        }
    }
}
