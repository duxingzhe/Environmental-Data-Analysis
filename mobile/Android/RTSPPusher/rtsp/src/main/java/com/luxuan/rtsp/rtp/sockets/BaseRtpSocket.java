package com.luxuan.rtsp.rtp.sockets;

import com.luxuan.rtsp.rtsp.Protocol;
import com.luxuan.rtsp.rtsp.RtpFrame;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseRtpSocket {

    public final static String TAG="BaseRtpSocket";

    public static BaseRtpSocket getInstance(Protocol protocol, int videoSourcePort, int audioSourcePort){
        return protocol==Protocol.TCP? new RtpsocketTcp(): new RtpSocketUdp(videoSourcePort, audioSourcePort);
    }

    public abstract void setDataStream(OutputStream outputStream, String host);

    public abstract void sendFrame(RtpFrame rtpFrame) throws IOException;

    public abstract void close();
}
