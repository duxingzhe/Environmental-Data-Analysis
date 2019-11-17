package com.luxuan.rtsp.rtsp;

import android.util.Base64;
import android.util.Log;

import com.luxuan.rtsp.utils.AuthUtil;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String createAuth(String authResponse){
        Pattern authPattern= Pattern.compile("realm=\"(.+)\",\\s+nonce=\"(\\w+)\"", Pattern.CASE_INSENSITIVE);

        Matcher matcher=authPattern.matcher(authResponse);

        if(matcher.find()){
            Log.i(TAG, "using digest auth");
            String realm=matcher.group(1);
            String nonce=matcher.group(2);
            String hash1= AuthUtil.getMd5Hash(user+":"+realm+":"+password);
            String hash2=AuthUtil.getMd5Hash("ANNOUNCE:rtsp://"+host+":"+port+path);
            String hash3=AuthUtil.getMd5Hash(hash1+":"+nonce+":"+hash2);

            return "Digest username=\""
                    + user
                    + "\",realm=\""
                    + realm
                    + "\",nonce=\""
                    + nonce
                    + "\",uri=\"rtsp://"
                    + host
                    + ":"
                    + port
                    + path
                    + "\",response=\""
                    + hash3
                    + "\"";
        }else{
            //basic auth
            Log.i(TAG, "using basic auth");
            String data=user+":"+password;
            String base64Data=Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
            return "Basic "+base64Data;
        }
    }

    public String createOptions(){
        String options = "OPTIONS rtsp://" + host + ":" + port + path + " RTSP/1.0\r\n" + addHeaders();
        Log.i(TAG, options);
        return options;
    }

    public String createSetup(int track){
        int[] udpPorts=track==trackVideo?videoClientPorts:audioClientPorts;

        String params=(protocol==Protocol.UDP)?("UDP;unicast;client_port=" + udpPorts[0] + "-" + udpPorts[1] + ";mode=record")
                : ("TCP;interleaved=" + 2 * track + "-" + (2 * track + 1) + ";mode=record");
        String setup="SETUP rtsp://"
                + host
                + ":"
                + port
                + path
                + "/trackID="
                + track
                + " RTSP/1.0\r\n"
                + "Transport: RTP/AVP/"
                + params
                + "\r\n"
                + addHeaders();
        Log.i(TAG, setup);
        return setup;
    }

    public String createRecord(){
        String record="RECORD rtsp://"
                + host
                + ":"
                + port
                + path
                + " RTSP/1.0\r\n"
                + "Range: npt=0.000-\r\n"
                + addHeaders();
        Log.i(TAG, record);
        return record;
    }
}
