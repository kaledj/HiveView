package com.HiveView.AsyncNetwork;

import android.os.AsyncTask;
import android.util.Log;
import com.HiveView.FTPSession;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class ConvertVideoFileTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "ConvertVideoFileTask";


    private OnVideoConverted onVideoConverted;

    public ConvertVideoFileTask(OnVideoConverted onVideoConverted) {
        this.onVideoConverted = onVideoConverted;
    }

    @Override
    protected String doInBackground(String... filenames) {
        String filename = filenames[0];
        JSch jsch = new JSch();
        // TODO: Initialize the ssh session with the ftp session and clear password after authentication
        try {
            Session session = jsch.getSession(FTPSession.username, "cs.appstate.edu", 22);
            session.setPassword(new String(FTPSession.password));
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ChannelExec channelSsh = (ChannelExec) session.openChannel("exec");
            channelSsh.setOutputStream(baos);
            String newVideoFile = filename.replaceFirst(".h264", ".mp4");
            String command = String.format("/usr/local/bee/bin/ffmpeg -y -i %s -f mp4 -vcodec libx264 -vprofile baseline %s", filename, newVideoFile);
//            String command = "/usr/local/bee/bin/ffmpeg -y -i " + filename + " " + newVideoFile;
//            String command = "/usr/local/bee/convert.sh " + filename;
            Log.v(TAG, "Executing: " + command);
            channelSsh.setCommand(command);
            channelSsh.connect();
            channelSsh.disconnect();
            Thread.sleep(10000);
            session.disconnect();
            return newVideoFile;
        } catch(Exception e) {
            Log.e(TAG, "Error when attempting to establish ssh connection.", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String newVideoFile) {
        onVideoConverted.onVideoConverted(newVideoFile);
    }
}
