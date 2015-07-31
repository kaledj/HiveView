package com.HiveView.AsyncNetwork;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.net.ftp.FTP;
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
            boolean status = ftp.login(user, new String(password));
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
//            ftp.setFileTransferMode(FTP.BLOCK_TRANSFER_MODE);
            reply = ftp.getReplyCode();
            Log.v(TAG, "Reply code: " + reply);
            return status;
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
