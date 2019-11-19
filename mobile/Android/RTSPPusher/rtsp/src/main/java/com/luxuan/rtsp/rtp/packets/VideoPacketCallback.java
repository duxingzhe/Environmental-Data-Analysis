package com.luxuan.rtsp.rtp.packets;

import com.luxuan.rtsp.rtsp.RtpFrame;

public interface VideoPacketCallback {

    void onVideoFrameCreated(RtpFrame rtpFrame);
}
