package com.luxuan.rtsp.rtp.packets;

import com.luxuan.rtsp.rtsp.RtpFrame;

public interface AudioPacketCallback {

    void onAudioFrameCreated(RtpFrame rtpFrame);
}
