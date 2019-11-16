package com.luxuan.rtsp.rtsp;

public class RtpFrame {

    private byte[] buffer;
    private long timeStamp;
    private int length;
    private int rtpPort;
    private int rtcpPort;
    private byte channelIdentifier;

    public RtpFrame(byte[] buffer, long timeStamp, int length, int rtpPort, int rtcpPort, byte channelIdentifier){
        this.buffer=buffer;
        this.timeStamp=timeStamp;
        this.length=length;
        this.rtpPort=rtpPort;
        this.rtcpPort=rtcpPort;
        this.channelIdentifier=channelIdentifier;
    }

    public byte[] getBuffer(){
        return buffer;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setRtpPort(int rtpPort) {
        this.rtpPort = rtpPort;
    }

    public void setRtcpPort(int rtcpPort) {
        this.rtcpPort = rtcpPort;
    }

    public void setChannelIdentifier(byte channelIdentifier) {
        this.channelIdentifier = channelIdentifier;
    }

    public void setBuffer(byte[] buffer){
        this.buffer=buffer;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getLength() {
        return length;
    }

    public int getRtpPort() {
        return rtpPort;
    }

    public int getRtcpPort() {
        return rtcpPort;
    }

    public byte getChannelIdentifier() {
        return channelIdentifier;
    }

    public boolean isVideoFrame(){
        return channelIdentifier==(byte)2;
    }
}
