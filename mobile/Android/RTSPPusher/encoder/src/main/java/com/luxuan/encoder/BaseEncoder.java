package com.luxuan.encoder;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import com.luxuan.encoder.util.CodecUtil;

import java.nio.ByteBuffer;

public abstract class BaseEncoder implements EncoderCallback {

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
        if(isBufferMode){
            int inputBufferIndex=codec.dequeueInputBuffer(0);
            if(inputBufferIndex>=0){
                inputAvailable(codec, inputBufferIndex, frame);
            }
        }

        for(; running; ){
            int outputBufferIndex=codec.dequeueOutputBuffer(bufferInfo, 0);
            if(outputBufferIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                MediaFormat mediaFormat=codec.getOutputFormat();
                formatChanged(codec, mediaFormat);
            }else if(outputBufferIndex>=0){
                outputAvailable(codec,outputBufferIndex, bufferInfo);
            }else{
                break;
            }
        }
    }

    protected abstract Frame getInputFrame() throws InterruptedException;

    private void processInput(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec mediaCodec,
                              int inputBufferIndex, Frame frame) throws IllegalStateException {
        try{
            if(frame==null){
                frame=getInputFrame();
            }
            byteBuffer.clear();
            byteBuffer.put(frame.getBuffer(), frame.getOffset(), frame.getSize());
            long pts=System.nanoTime()/1000-presentTimeUs;
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, frame.getSize(), pts, 0);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void checkBuffer(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec.BufferInfo bufferInfo);

    protected abstract void sendBuffer(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec.BufferInfo bufferInfo);

    private void processOutput(@NonNull ByteBuffer byteBuffer, @NonNull MediaCodec mediaCodec, int outputBufferIndex,
                               @NonNull MediaCodec.BufferInfo bufferInfo) throws IllegalStateException {
        checkBuffer(byteBuffer, bufferInfo);
        sendBuffer(byteBuffer, bufferInfo);
        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
    }

    public void setForce(CodecUtil.Force force){
        this.force=force;
    }

    public boolean isRunning(){
        return running;
    }

    @Override
    public void inputAvailable(@NonNull MediaCodec mediaCodec, int inputBufferIndex, Frame frame)
        throws IllegalStateException {
        ByteBuffer byteBuffer=mediaCodec.getInputBuffer(inputBufferIndex);
        processInput(byteBuffer, mediaCodec, inputBufferIndex, frame);
    }

    @Override
    public void outputAvailable(@NonNull MediaCodec mediaCodec, int outputBufferIndex, @NonNull MediaCodec.BufferInfo bufferInfo )
            throws IllegalStateException {
        ByteBuffer byteBuffer=mediaCodec.getOutputBuffer(outputBufferIndex);
        processOutput(byteBuffer, mediaCodec, outputBufferIndex, bufferInfo);
    }
}
