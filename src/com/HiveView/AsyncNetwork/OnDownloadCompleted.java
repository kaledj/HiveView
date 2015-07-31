package com.HiveView.AsyncNetwork;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by David on 7/14/2015.
 */
public interface OnDownloadCompleted {
    void onDownloadCompleted(File downloadedFile);
}
