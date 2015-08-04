package com.HiveView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;

public class ViewSavedVideosActivity extends Activity {
    private static final String TAG = "ViewSavedVideosActivity";

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_files_viewer);
        listView = (ListView) findViewById(R.id.videoListView);

        File[] oldFiles = getFilesDir().listFiles();
        final File[] files = pruneOldFiles(oldFiles);
        final String[] filenames = new String[files.length];
        for(int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getName().split("\\.")[0];
        }
        Log.v(TAG, "Listing " + files.length + " saved files.");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filenames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewSavedVideosActivity.this, VideoViewerActivity.class);
                intent.putExtra("videoPath", files[i].getAbsolutePath());
                startActivity(intent);
            }
        });
    }

    protected File[] pruneOldFiles(File[] oldFiles) {
        ArrayList<File> newFiles = new ArrayList<File>(oldFiles.length);
        for(File oldFile : oldFiles) {
            if(!oldFile.getName().matches("cached_.*")) {
                oldFile.delete();
            } else {
                newFiles.add(oldFile);
            }
        }
        return newFiles.toArray(new File[newFiles.size()]);
    }
}