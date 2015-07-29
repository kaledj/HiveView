package com.HiveView;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.PasswordAuthentication;

/**
 * InitFTPTask
 * Asynchronously initializes the FTP connection
 */
public class InitFTPTask extends AsyncTask<PasswordAuthentication, Void, Boolean> {

    private static final String TAG = "InitFTPTask";
    private FTPClient ftp;
    private OnFTPLogin onFTPLogin;

    public InitFTPTask(OnFTPLogin onFTPLogin, FTPClient ftp) {
        this.onFTPLogin = onFTPLogin;
        this.ftp = ftp;
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
        onFTPLogin.onFTPLogin(result);
    }
}
