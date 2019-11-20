package com.luxuan.rtsp.rtp.packets;

import android.media.MediaCodec;

import com.luxuan.rtsp.rtsp.RtpFrame;
import com.luxuan.rtsp.utils.RtpConstants;

import java.nio.ByteBuffer;

public class H265Packet extends BasePacket {

    private byte[] header=new byte[6];
    private byte[] stapA;
    private VideoPacketCallback videoPacketCallback;

    public H265Packet(byte[] sps, byte[] pps, byte[] vps, VideoPacketCallback videoPacketCallback){
        super(RtpConstants.clockVideoFrequency);
        this.videoPacketCallback=videoPacketCallback;
        channelIdentifier=(byte)2;
        setSpsPpsVps(sps, pps, vps);
    }

    @Override
    public void createAndSendPacket(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo){

        byteBuffer.rewind();
        byteBuffer.get(header, 0, 0);
        long ts=bufferInfo.presentationTimeUs*1000L;
        int naluLength=bufferInfo.size-byteBuffer.position()+1;
        int type=(header[4]>>1)&0x3F;
        if(type==RtpConstants.IDR_N_LP||type==RtpConstants.IDR_W_DLP){
            byte[] buffer=getBuffer(stapA.length+RtpConstants.RTP_HEADER_LENGTH);
            updateTimeStamp(buffer, ts);

            markPacket(buffer);
            System.arraycopy(stapA, 0, buffer, RtpConstants.RTP_HEADER_LENGTH, stapA.length);

            updateSeq(buffer);
            RtpFrame rtpFrame=new RtpFrame(buffer, ts, stapA.length+RtpConstants.RTP_HEADER_LENGTH, rtpPort, rtcpPort,
                    channelIdentifier);
            videoPacketCallback.onVideoFrameCreated(rtpFrame);
        }

        if(naluLength<=maxPacketSize-RtpConstants.RTP_HEADER_LENGTH-3){
            int count=naluLength-1;
            int length=count<bufferInfo.size-byteBuffer.position()?count:bufferInfo.size-byteBuffer.position();
            byte[] buffer=getBuffer(length+RtpConstants.RTP_HEADER_LENGTH+2);

            buffer[RtpConstants.RTP_HEADER_LENGTH]=header[4];
            buffer[RtpConstants.RTP_HEADER_LENGTH+1]=header[5];
            byteBuffer.get(buffer, RtpConstants.RTP_HEADER_LENGTH+2, length);

            updateTimeStamp(buffer, ts);
            markPacket(buffer);

            updateSeq(buffer);
            RtpFrame rtpFrame=new RtpFrame(buffer, ts, naluLength+RtpConstants.RTP_HEADER_LENGTH, rtpPort, rtcpPort,
                    channelIdentifier);
            videoPacketCallback.onVideoFrameCreated(rtpFrame);
        }else{

            header[0]=49<<1;
            header[1]=1;

            header[2]=(byte)type;
            header[2]+=0x80;

            int sum=1;
            while(sum<naluLength){
                int count=naluLength-sum>maxPacketSize-RtpConstants.RTP_HEADER_LENGTH-3?maxPacketSize-RtpConstants.RTP_HEADER_LENGTH-3:
                        naluLength-sum;
                int length=count<bufferInfo.size-byteBuffer.position()?count:bufferInfo.size-byteBuffer.position();
                byte[] buffer=getBuffer(length+RtpConstants.RTP_HEADER_LENGTH+3);

                buffer[RtpConstants.RTP_HEADER_LENGTH]=header[0];
                buffer[RtpConstants.RTP_HEADER_LENGTH+1]=header[1];
                buffer[RtpConstants.RTP_HEADER_LENGTH+2]=header[2];
                updateTimeStamp(buffer, ts);
                byteBuffer.get(buffer, RtpConstants.RTP_HEADER_LENGTH+3, length);
                sum+=length;
                if(sum>=naluLength){
                    buffer[RtpConstants.RTP_HEADER_LENGTH+2]+=0x40;
                    markPacket(buffer);
                }
                updateSeq(buffer);
                RtpFrame rtpFrame=new RtpFrame(buffer, ts, length+RtpConstants.RTP_HEADER_LENGTH+3, rtpPort, rtcpPort,
                        channelIdentifier);
                videoPacketCallback.onVideoFrameCreated(rtpFrame);

                header[2]=(byte)(header[2]&0x7F);
            }
        }
    }

    private void setSpsPpsVps(byte[] sps, byte[] pps, byte[] vps){
        stapA=new byte[sps.length+pps.length+6];

        stapA[0]=48<<1;
        stapA[1]=1;

        stapA[2]=(byte)(sps.length&0xFF);
        stapA[3]=(byte)(sps.length&0xFF);

        stapA[sps.length+4]=(byte)(pps.length>>8);
        stapA[sps.length+5]=(byte)(pps.length&0xFF);

        System.arraycopy(sps, 0, stapA, 5, sps.length);
        System.arraycopy(pps, 0, stapA, 6+sps.length, pps.length);
    }
}
