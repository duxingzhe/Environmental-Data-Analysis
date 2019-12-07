package com.luxuan.encoder.audio;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

public interface GetAacData {

    void getAacData(ByteBuffer aacBuffer, MediaCodec.BufferInfo info);

    void onAudioFormat(MediaFormat mediaFormat);

}
