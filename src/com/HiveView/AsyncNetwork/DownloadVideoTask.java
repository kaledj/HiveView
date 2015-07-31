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
        try {
//            File cacheDir = context.getExternalCacheDir();
//            File downloadedFile = new File(cacheDir.getPath(), "tempVid.mp4");
//            File dir = context.getExternalFilesDir(null);
//            File downloadedFile = new File(dir.getPath(), "tempVid.mp4");

            Log.v(TAG, "EXT Storage state: " + Environment.getExternalStorageState());
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File downloadedFile = new File(dir.getPath(), "tempVid.mp4");
            if(downloadedFile.exists()) {
                boolean status = downloadedFile.delete();
                Log.v(TAG, "File delete: " + status);
            }

//            downloadedFile.deleteOnExit();
//            File downloadedFile = File.createTempFile("tempVid", "mp4");
//            FileOutputStream fos = context.openFileOutput("tempVid.mp4", Context.MODE_WORLD_READABLE);
            FileOutputStream fos = new FileOutputStream(downloadedFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            Log.v(TAG, "Downloading: " + fileName);
            boolean status = ftp.retrieveFile(fileName, bos);
            Log.v(TAG, "FTP retr status: " + status);
            bos.close();
            Log.v(TAG, "Size of file: " + downloadedFile.length() + " bytes.");
            return downloadedFile;
        } catch(IOException e) {
            Log.e(TAG, "Error.", e);
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
