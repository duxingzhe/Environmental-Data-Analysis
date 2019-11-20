package com.luxuan.rtsp.rtp.packets;

import android.media.MediaCodec;

import com.luxuan.rtsp.rtsp.RtpFrame;
import com.luxuan.rtsp.utils.RtpConstants;

import java.nio.ByteBuffer;

public class AacPacket extends BasePacket {

    private AudioPacketCallback audioPacketCallback;

    public AacPacket(int sampleRate, AudioPacketCallback audioPacketCallback){
        super(sampleRate);
        this.audioPacketCallback=audioPacketCallback;
        channelIdentifier=(byte)0;
    }

    @Override
    public void createAndSendPacket(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo){
        int length=bufferInfo.size-byteBuffer.position();
        if(length>0){
            byte[] buffer=getBuffer(length+RtpConstants.RTP_HEADER_LENGTH+4);

            byteBuffer.get(buffer, RtpConstants.RTP_HEADER_LENGTH+4, length);
            long ts=bufferInfo.presentationTimeUs*1000;
            markPacket(buffer);
            updateTimeStamp(buffer, ts);

            buffer[RtpConstants.RTP_HEADER_LENGTH]=(byte)0;
            buffer[RtpConstants.RTP_HEADER_LENGTH+1]=(byte)0x10;

            buffer[RtpConstants.RTP_HEADER_LENGTH+2]=(byte)(length>>5);
            buffer[RtpConstants.RTP_HEADER_LENGTH+3]=(byte)(length<<3);

            buffer[RtpConstants.RTP_HEADER_LENGTH+3]&=0xF8;
            buffer[RtpConstants.RTP_HEADER_LENGTH+3]|=0x00;

            updateSeq(buffer);
            RtpFrame rtpFrame=new RtpFrame(buffer, ts, RtpConstants.RTP_HEADER_LENGTH+4, rtpPort, rtcpPort,
                    channelIdentifier);
            audioPacketCallback.onAudioFrameCreated(rtpFrame);
        }
    }
}
