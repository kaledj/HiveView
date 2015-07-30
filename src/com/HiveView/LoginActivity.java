package com.HiveView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.HiveView.AsyncNetwork.InitFTPTask;
import com.HiveView.AsyncNetwork.OnFTPLogin;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.net.PasswordAuthentication;

public class LoginActivity extends Activity implements OnFTPLogin {
    private static final String TAG = "LoginActivity";

    private FTPClient ftp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_splash);
        ftp = FTPSession.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ftp.isConnected()) {
            try {
                Log.v(TAG, "Resumed " + TAG + ", closing FTP. Log in again.");
                ftp.disconnect();
            } catch(IOException e) {
                Log.e(TAG, "Disconnect failed.", e);
            }
        }
    }

    /**
     * Called when the FTP connection is established (or fails)
     * @param status The status of connection
     */
    public void onFTPLogin(Boolean status) {
        if(status) {
            Log.d(TAG, ftp.isConnected() + "");
            Intent intent = new Intent(this, TimePickerActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("FTP Login failed")
                   .setTitle("Error")
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });
            AlertDialog alert =  builder.create();
            alert.show();
        }
    }

    /**
     * Handler for the connect button on the login screen
     * @param view The button that was clicked
     */
    public void connectClicked(View view) {
        // Get the username and password from the text fields
        EditText usernameInput = (EditText) findViewById(R.id.usernameInput);
        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
        String user = usernameInput.getText().toString();
        char[] password = new char[passwordInput.length()];
        passwordInput.getText().getChars(0, passwordInput.length(), password, 0);
        PasswordAuthentication loginInfo = new PasswordAuthentication(user, password);

        // Create the background login task
        new InitFTPTask(this, ftp).execute(loginInfo);
    }
}

