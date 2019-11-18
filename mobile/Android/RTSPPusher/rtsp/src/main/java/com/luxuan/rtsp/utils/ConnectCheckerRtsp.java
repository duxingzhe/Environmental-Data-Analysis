package com.luxuan.rtsp.utils;

public interface ConnectCheckerRtsp {

    void onConnectionSuccessRtsp();

    void onConnectionFailedRtsp(String reason);

    void onNewBitrateRtsp(long bitrate);

    void onDisconnectRtsp();

    void onAutherrorRtsp();

    void onAuthSuccessRtsp();
}
