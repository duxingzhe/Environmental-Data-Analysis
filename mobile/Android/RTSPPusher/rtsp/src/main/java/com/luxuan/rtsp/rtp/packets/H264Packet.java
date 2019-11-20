package com.luxuan.rtsp.rtp.packets;

import android.media.MediaCodec;

import com.luxuan.rtsp.rtsp.RtpFrame;
import com.luxuan.rtsp.utils.RtpConstants;

import java.nio.ByteBuffer;

public class H264Packet extends BasePacket {

    private byte[] header=new byte[5];
    private byte[] stapA;
    private VideoPacketCallback videoPacketCallback;

    public H264Packet(byte[] sps, byte[] pps, VideoPacketCallback videoPacketCallback){
        super(RtpConstants.clockVideoFrequency);
        this.videoPacketCallback=videoPacketCallback;
        channelIdentifier=(byte)2;
        setSpsPps(sps, pps);
    }

    @Override
    public void createAndSendPacket(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo){
        byteBuffer.rewind();
        byteBuffer.get(header, 0, 5);
        long ts=bufferInfo.presentationTimeUs;
        int naluLength=bufferInfo.size-byteBuffer.position()+1;
        int type=header[4]&0x1F;
        if(type==RtpConstants.IDR){
            byte[] buffer=getBuffer(stapA.length+RtpConstants.RTP_HEADER_LENGTH);
            updateTimeStamp(buffer, ts);

            markPacket(buffer);
            System.arraycopy(stapA, 0, buffer, RtpConstants.RTP_HEADER_LENGTH, stapA.length);

            updateSeq(buffer);
            RtpFrame rtpFrame=new RtpFrame(buffer, ts, stapA.length+RtpConstants.RTP_HEADER_LENGTH, rtpPort, rtcpPort,
                    channelIdentifier);
            videoPacketCallback.onVideoFrameCreated(rtpFrame);
        }

        if(naluLength<=maxPacketSize-RtpConstants.RTP_HEADER_LENGTH-2){
            int count=naluLength-1;
            int length=count<bufferInfo.size-byteBuffer.position()?count:bufferInfo.size-byteBuffer.position();
            byte[] buffer=getBuffer(length+RtpConstants.RTP_HEADER_LENGTH+1);

            buffer[RtpConstants.RTP_HEADER_LENGTH]=header[4];
            byteBuffer.get(buffer, RtpConstants.RTP_HEADER_LENGTH+1, length);

            updateTimeStamp(buffer, ts);
            markPacket(buffer);

            updateSeq(buffer);
            RtpFrame rtpFrame=new RtpFrame(buffer, ts, naluLength+RtpConstants.RTP_HEADER_LENGTH, rtpPort, rtcpPort,
                    channelIdentifier);
            videoPacketCallback.onVideoFrameCreated(rtpFrame);
        }else{

            header[1]=(byte)(header[4]&0x1F);
            header[1]+=0x80;

            header[0]=(byte)((header[4]&0x60)&0xFF);
            header[0]+=28;

            int sum=1;
            while(sum<naluLength){
                int count=naluLength-sum>maxPacketSize-RtpConstants.RTP_HEADER_LENGTH-2?maxPacketSize
                        -RtpConstants.RTP_HEADER_LENGTH-2:naluLength-sum;
                int length=count<bufferInfo.size-byteBuffer.position()?count:bufferInfo.size-byteBuffer.position();
                byte[] buffer=getBuffer(length+RtpConstants.RTP_HEADER_LENGTH+2);

                buffer[RtpConstants.RTP_HEADER_LENGTH]=header[0];
                buffer[RtpConstants.RTP_HEADER_LENGTH+1]=header[1];
                updateTimeStamp(buffer, ts);
                byteBuffer.get(buffer, RtpConstants.RTP_HEADER_LENGTH, length);
                sum+=length;

                if(sum>=naluLength){
                    buffer[RtpConstants.RTP_HEADER_LENGTH+1]=0x40;
                    markPacket(buffer);
                }

                updateSeq(buffer);
                RtpFrame rtpFrame=new RtpFrame(buffer, ts, length+RtpConstants.RTP_HEADER_LENGTH-2, rtpPort, rtcpPort,
                        channelIdentifier);
                videoPacketCallback.onVideoFrameCreated(rtpFrame);

                header[1]=(byte)(header[1]&0x7F);
            }
        }
    }
}
