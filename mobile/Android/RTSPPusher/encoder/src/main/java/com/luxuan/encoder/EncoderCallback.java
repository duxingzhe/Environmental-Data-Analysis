package com.luxuan.encoder;

import android.media.MediaCodec;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

public interface EncoderCallback {

    void inputAvailable(@NonNull MediaCodec mediaCodec, int inputBufferIndex, Frame frame)
            throws IllegalStateException;

    void outputAvailable(@NonNull MediaCodec mediaCodec, int outputBufferIndex, @NonNull MediaCodec.BufferInfo bufferInfo)
            throws IllegalStateException;

    void formatChanged(@NonNull MediaCodec mediaCodec, @NonNull MediaFormat mediaFormat);

}
