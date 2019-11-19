package com.luxuan.rtsp.rtsp;

import com.luxuan.rtsp.rtcp.BaseSenderReport;
import com.luxuan.rtsp.rtp.packets.AudioPacketCallback;
import com.luxuan.rtsp.rtp.packets.VideoPacketCallback;
import com.luxuan.rtsp.utils.ConnectCheckerRtsp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RtspSender implements AudioPacketCallback, VideoPacketCallback {

    private final static String TAG="RtspSender";
    private BasePacket videoPacket;
    private AacPacket aacPacket;
    private BaseRtpSocket rtpSocket;
    private BaseSenderReport baseSenderReport;
    private volatile BlockingQueue<RtpFrame> rtpFrameBlockingQueue=
            new LinkedBlockingQueue<>(getDefaultCacheSize());
    private Thread thread;
    private ConnectCheckerRtsp connectCheckerRtsp;
    private long audioFramesSent=0;
    private long videoFramesSent=0;
    private long droppedAudioFrames=0;
    private long droppedVideoFrames=0;
    private BitrateManager bitrateManager;

    public RtspSender(ConnectCheckerRtsp connectCheckerRtsp){
        this.connectCheckerRtsp=connectCheckerRtsp;
        bitrateManager=new BitrateManager(connectCheckerRtsp);
    }
}
