package com.luxuan.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;

import com.luxuan.encoder.util.CodecUtil;

public abstract class BaseEncoder {

    private static final String TAG="BaseEncoder";
    private MediaCodec.BufferInfo bufferInfo=new MediaCodec.BufferInfo();
    protected MediaCodec codec;
    protected long presentTimeUs;
    protected volatile boolean running=false;
    protected boolean isBufferMode=true;
    protected CodecUtil.Force force=CodecUtil.Force.FIRST_COMPATIABLE_FOUND;

    public void start(){
        start(true);
    }

    public abstract void start(boolean resetTs);

    protected abstract void stopImp();

    public void stop(){
        running=false;
        if(codec!=null){
            codec.stop();
            codec.release();
            codec=null;
        }
        stopImp();
    }

    protected abstract MediaCodecInfo chooseEncoder(String mime);

    protected void getDataFromEncoder(Frame frame) throws IllegalStateException {

    }
}
