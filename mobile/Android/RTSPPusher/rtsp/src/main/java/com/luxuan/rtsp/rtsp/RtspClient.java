package com.luxuan.rtsp.rtsp;

import android.os.Handler;
import android.os.Looper;

import com.luxuan.rtsp.utils.ConnectCheckerRtsp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;

public class RtspClient {

    private final String TAG="RtspClient";
    private static final Pattern rtspUrlPattern= Pattern.compile("rtsps?://([^/:]+)(?::(\\d+))*/([^/]+)/?([^*]*)$");

    private ConnectCheckerRtsp connectCheckerRtsp;

    private Socket connectionSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread thread;

    private OutputStream outputStream;
    private volatile boolean streaming=false;

    private boolean tlsEnabled=false;
    private RtspSender rtspSender;
    private String url;
    private CommandsManager commandsManager;
    private int numRetry;
    private int retry;
    private Handler handler;
    private Runnable runnable;

    public RtspClient(ConnectCheckerRtsp connectCheckerRtsp){
        this.connectCheckerRtsp=connectCheckerRtsp;
        commandsManager=new CommandsManager();
        rtspSender=new RtspSender(connectCheckerRtsp);
        handler=new Handler(Looper.getMainLooper());
    }

    public void setOnlyAudio(boolean onlyAudio){
        commandsManager.setOnlyAudio(onlyAudio);
    }

    public void setProtocol(Protocol protocol){
        commandsManager.setProtocol(protocol);
    }

    public void setAuthorization(String user, String password){
        commandsManager.setAuth(user, password);
    }

    public void setRetry(int retry){
        numRetry=retry;
        this.retry=retry;
    }

    public boolean shouldRetry(String reason){
        boolean validReason=!reason.contains("Endpoint malformed");
        return validReason&& retry>0;
    }

    public boolean isStreaming(){
        return streaming;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public void setSampleRate(int sampleRate){
        commandsManager.setSampleRate(sampleRate);
    }

    public String getHost(){
        return commandsManager.getHost();
    }

    public int getPort(){
        return commandsManager.getPort();
    }

    public String getPath(){
        return commandsManager.getPath();
    }

    public ConnectCheckerRtsp getConnectCheckerRtsp(){
        return connectCheckerRtsp;
    }

    public void setSPSandPPS(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps){
        commandsManager.setVideoInfo(sps, pps, vps);
    }

    public void setIsStereo(boolean isStereo){
        commandsManager.setIsStereo(isStereo);
    }
}
