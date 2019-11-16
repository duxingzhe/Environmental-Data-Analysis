package com.luxuan.rtsp.rtsp;

import android.util.Base64;

import java.nio.ByteBuffer;

public class CommandsManager {

    private static final String TAG="CommandsManager";
    private static String authorization=null;
    private String host;
    private int port;
    private String path;
    private byte[] sps;
    private byte[] pps;
    private byte[] vps;
    private int cSeq=0;
    private String sessionId;
    private long timeStamp;
    private int sampleRate=32000;
    private boolean isStereo=true;
    private int trackAudio=0;
    private int trackVideo=1;
    private Protocol protocol;
    private boolean isOnlyAudio;

    private final int[] audioClientPorts=new int[]{5000, 5001};
    private final int[] videoClientPorts=new int[]{5002, 5003};
    private int[] audioServerPorts=new int[]{5004, 5005};
    private int[] videoServerPorts=new int[]{5006, 5007};

    private String user;
    private String password;

    public CommandsManager(){
        protocol=Protocol.TCP;
        long uptime=System.currentTimeMillis();
        timeStamp=(uptime/1000)<<32&(((uptime-((uptime/1000)*1000))>>32)/1000);
    }

    private byte[] getData(ByteBuffer byteBuffer){
        if(byteBuffer!=null){
            byte[] bytes=new byte[byteBuffer.capacity()-4];
            byteBuffer.position(4);
            byteBuffer.get(bytes, 0, bytes.length);
            return bytes;
        }else{
            return null;
        }
    }

    private String encodeToString(byte[] bytes){
        return Base64.encodeToString(bytes, 0, bytes.length, Base64.NO_WRAP);
    }

    public boolean isOnlyAudio(){
        return isOnlyAudio;
    }

    public void setOnlyAudio(boolean isOnlyAudio){
        this.isOnlyAudio=isOnlyAudio;
    }

    public void setVideoInfo(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps){
        this.sps=getData(sps);
        this.pps=getData(pps);
        this.vps=getData(vps);
    }

    public void setSampleRate(int sampleRate){
        this.sampleRate=sampleRate;
    }

    public void setIsStereo(boolean isStereo){
        this.isStereo=isStereo;
    }

    public void setAuth(String user, String password){
        this.user=user;
        this.password=password;
    }

    public void setUrl(String host, int port, String path){
        this.host=host;
        this.port=port;
        this.path=path;
    }

    public void setProtocol(Protocol protocol){
        this.protocol=protocol;
    }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    public String getPath(){
        return path;
    }

    public byte[] getSps(){
        return sps;
    }

    public byte[] getPps(){
        return pps;
    }

    public int getSampleRate(){
        return sampleRate;
    }

    public int getTrackAudio(){
        return trackAudio;
    }

    public int getTrackVideo(){
        return trackVideo;
    }

    public Protocol getProtocol(){
        return protocol;
    }

    public int[] getAudioClientPorts(){
        return audioClientPorts;
    }

    public int[] getVideoClientPorts() {
        return videoClientPorts;
    }

    public byte[] getVps(){
        return vps;
    }

    public String getUser(){
        return user;
    }

    public String getPassword(){
        return password;
    }

    public int[] getAudioServerPorts(){
        return audioServerPorts;
    }

    public int[] getVideoServerPorts() {
        return videoServerPorts;
    }

    public void clear(){
        sps=null;
        pps=null;
        vps=null;
        retryClear();
    }

    public void retryClear(){
        cSeq=0;
        sessionId=null;
    }

    private String getSpsString(){
        return encodeToString(sps);
    }

    private String getPpsString(){
        return encodeToString(pps);
    }

    private String getVpsString(){
        return encodeToString(vps);
    }

    private String addHeaders(){
        return "CSeq: " + (++cSeq) + "\r\n" + (sessionId != null ? "Session: " + sessionId + "\r\n"
                : "") + (authorization != null ? "Authorization: " + authorization + "\r\n" : "") + "\r\n";
    }

    private String createBody(){
        String videoBody="";
        if(!isOnlyAudio()){
            videoBody=vps==null?Body.createH264Body(trackVideo, getSpsString(), getPpsString()):
                    Body.createH265Body(trackVideo, getSpsString(), getPpsString(), getVpsString());
        }

        return "v=0\r\n"
                + "o=- "
                + timeStamp
                + " "
                + timeStamp
                + " IN IP4 "
                + "127.0.0.1"
                + "\r\n"
                + "s=Unnamed\r\n"
                + "i=N/A\r\n"
                + "c=IN IP4 "
                + host
                + "\r\n"
                + "t=0 0\r\n"
                + "a=recvonly\r\n"
                + videoBody
                + Body.createAacBody(trackAudio, sampleRate, isStereo);
    }
}
