package com.luxuan.rtsp.utils;

public class BitrateManager {

    private long bitrate;
    private long timeStamp=System.currentTimeMillis();
    private ConnectCheckerRtsp connectCheckerRtsp;

    public BitrateManager(ConnectCheckerRtsp connectCheckerRtsp){
        this.connectCheckerRtsp=connectCheckerRtsp;
    }

    public synchronized void calculateBirate(long size){
        bitrate+=size;
        if(System.currentTimeMillis()-timeStamp>=1000){
            connectCheckerRtsp.onNewBitrateRtsp(bitrate);
            timeStamp=System.currentTimeMillis();
            bitrate=0;
        }
    }
}
