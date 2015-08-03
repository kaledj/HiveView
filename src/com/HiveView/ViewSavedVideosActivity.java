package com.HiveView;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.File;

public class ViewSavedVideosActivity extends Activity {
    private static final String TAG = "ViewSavedVideosActivity";

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_files_viewer);
        listView = (ListView) findViewById(R.id.videoListView);

        final File[] files = getFilesDir().listFiles();
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
}