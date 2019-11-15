package com.luxuan.rtsp.rtcp;

import java.util.Random;

public abstract class BaseSenderReport {

    protected static final String TAG="BaseSenderReport";
    protected static final int PACKET_LENGTH=28;
    private static final int MTU=1500;
    private final long itnerval=3000;

    private final byte[] videoBuffer=new byte[MTU];
    private final byte[] audioBuffer=new byte[MTU];

    private long videoTime;
    private long audioTime;
    private int videoPacketCount;
    private int videoOctetCount;
    private int audioPacketCount;
    private int audioOctetCount;

    public BaseSenderReport(){

        videoBuffer[0]=(byte)Integer.parseInt("10000000", 2);
        audioBuffer[0]=(byte)Integer.parseInt("10000000", 2);

        videoBuffer[1]=(byte)200;
        audioBuffer[1]=(byte)200;

        setLong(videoBuffer, PACKET_LENGTH/4-1, 2, 4);
        setLong(audioBuffer, PACKET_LENGTH/4-1, 2, 4);

        setLong(videoBuffer, new Random().nextInt(), 4, 8);
        setLong(audioBuffer, new Random().nextInt(), 4, 8);
    }
}
