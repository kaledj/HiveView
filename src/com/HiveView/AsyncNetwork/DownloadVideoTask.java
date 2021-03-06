package com.HiveView.AsyncNetwork;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.HiveView.FTPSession;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DownloadVideoTask
 * Asynchronously downloads the requested video file
 */
public class DownloadVideoTask extends AsyncTask<String, Void, File> {
    private static final String TAG = "DownloadVideoTask";

    private OnDownloadCompleted onDownloadCompleted;
    private FTPClient ftp;
    private Context context;

    public DownloadVideoTask(Context context, OnDownloadCompleted onDownloadCompleted) {
        this.onDownloadCompleted = onDownloadCompleted;
        this.context = context;
        this.ftp = FTPSession.getInstance();
    }

    /**
     * Downloads the file specified by the full filename
     * @param filenames Fully qualified filename
     * @return Handle for the file downloaded
     */
    @Override
    protected File doInBackground(String... filenames) {
        String fileName = filenames[0];
        String[] split = fileName.split("/");
        String localFilename = split[split.length-1];
        Log.v(TAG, "Filename: " + localFilename);
        try {
            // Create a new file in the app private directory
            File cacheDir = context.getFilesDir();
            File downloadedFile = new File(cacheDir.getPath(), localFilename);
            Log.v(TAG, "Saving file to: " + downloadedFile.getPath());
            if(downloadedFile.exists()) {
                boolean status = downloadedFile.delete();
                Log.v(TAG, "File deleted: " + status);
            }
            FileOutputStream fos = context.openFileOutput(localFilename, Context.MODE_WORLD_READABLE);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            Log.v(TAG, "Downloading: " + fileName);
            boolean status = ftp.retrieveFile(fileName, bos);
            Log.v(TAG, "FTP retr status: " + status);
            bos.close();
            Log.v(TAG, "Size of file: " + downloadedFile.length() + " bytes.");
            return context.getFileStreamPath(localFilename);
        } catch(IOException e) {
            Log.e(TAG, "Error.", e);
        } finally {
            new DeleteRemoteFilesTask().execute(fileName);
        }
        return null;
    }

    @Override
    protected void onPostExecute(File downloadedFile) {
        Log.d(TAG, "Download complete.");
        onDownloadCompleted.onDownloadCompleted(downloadedFile);
    }

    public void sortFilesByTimestamp(FTPFile[] files) {
        Arrays.sort(files, new Comparator<FTPFile>() {
            @Override
            public int compare(FTPFile lhs, FTPFile rhs) {
                return rhs.getTimestamp().compareTo(lhs.getTimestamp());
            }
        });
    }

    public File downloadMostRecent() {
        try {
            // Find the most recent directory
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

            File downloadedFile = File.createTempFile(latestFile, ".h264");
            downloadedFile.deleteOnExit();
            Log.i(TAG, downloadedFile.getAbsolutePath());

            FileOutputStream fos = new FileOutputStream(downloadedFile);
            ftp.retrieveFile(latestDir + "/" + latestFile, fos);
            fos.close();
            return downloadedFile;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sortFilesByName(FTPFile[] files) {
        Arrays.sort(files, new Comparator<FTPFile>() {
            @Override
            public int compare(FTPFile lhs, FTPFile rhs) {
                String lName = lhs.getName();
                String rName = rhs.getName();

                Pattern p = Pattern.compile("(\\d{2})-(\\d{2})-(\\d{4})_(\\d{2}):(\\d{2}):(\\d{2}).*");
                // Match lhs name
                Matcher m = p.matcher(lName);
                String lDay = m.group(0);
                String lMon = m.group(1);
                String lYear = m.group(2);
                String lHour = m.group(3);
                String lMin = m.group(4);
                String lSec = m.group(5);

                // Match rhs name
                m = p.matcher(rName);
                String rDay = m.group(0);
                String rMon = m.group(1);
                String rYear = m.group(2);
                String rHour = m.group(3);
                String rMin = m.group(4);
                String rSec = m.group(5);

                // TODO: Return comparison
                return 1;
            }
        });
    }

    public void sortDirsByName(FTPFile[] files) {
        Arrays.sort(files, new Comparator<FTPFile>() {
            @Override
            public int compare(FTPFile lhs, FTPFile rhs) {
                String lName = lhs.getName();
                String rName = rhs.getName();

                Pattern p = Pattern.compile("(\\d{2})-(\\d{2})-(\\d{4})");
                // Match lhs name
                Matcher m = p.matcher(lName);
                String lDay = m.group(0);
                String lMon = m.group(1);
                String lYear = m.group(2);

                // Match rhs name
                m = p.matcher(rName);
                String rDay = m.group(0);
                String rMon = m.group(1);
                String rYear = m.group(2);

                // TODO: Return comparison
                return 1;
            }
        });
    }
}
