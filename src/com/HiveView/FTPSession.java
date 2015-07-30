package com.HiveView;

import org.apache.commons.net.ftp.FTPClient;

public class FTPSession {
    private static final FTPClient ftp = new FTPClient();

    public static FTPClient getInstance() {
        return ftp;
    }
}
