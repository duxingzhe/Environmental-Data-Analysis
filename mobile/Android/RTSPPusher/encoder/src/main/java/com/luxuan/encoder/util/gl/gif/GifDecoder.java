package com.luxuan.encoder.util.gl.gif;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;

public class GifDecoder {

    private static final String TAG=GifDecoder.class.getSimpleName();

    public static final int STATUS_OK=0;

    public static final int STATUS_FORMAT_ERROR=1;

    public static final int STATUS_OPEN_ERROR=2;

    public static final int STATUS_PARTIAL_DECODE=3;

    public static final int MAX_STACK_SIZE=4096;

    private static final int DISPOSAL_UNSPECIFIED=0;

    private static final int DISPOSAL_NONE=1;

    private static final int DISPOSAL_BACKGROUND=2;

    private static final int DISPOSAL_PREVIOUS=3;

    private static final int NULL_CODE=-1;

    private static final int INITIAL_FRAME_POINTER=-1;

    public static final int LOOP_FOREVER=-1;

    private static final int BYTES_PER_INTEGER=4;

    private int[] act;

    private final int[] pct=new int[256];

    private ByteBuffer rawData;

    private byte[] block;

    private static final int WORK_BUFFER_SIZE=16384;

    @Nullable
    private byte[] workBuffer;

    private int workBufferSize=0;
    private int workBufferPosition=0;

    private GifHeaderParser parser;

    private short[] prefix;
    private byte[] suffix;
    private byte[] pixelStack;
    private byte[] mainPixels;
    private int[] mainScratch;

    private int framePointer;
    private int loopIndex;
    private GifHeader header;
    private BitmapProvider bitmapProvider;
    private Bitmap preivousImage;
    private boolean savePrevious;
    private int status;
    private int sampleSize;
    private int downSampledHeight;
    private int downSampledWidth;
    private boolean isFirstFrameTransparent;

    public interface BitmapProvider{

        @NonNull
        Bitmap obtain(int width, int height, Bitmap.Config config);

        void release(Bitmap bitmap);

        byte[] obtainByteArray(int size);

        void release(int[] array);
    }

    public GifDecoder(BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData){
        this(provider, gifHeader, rawData, 1);
    }


    public GifDecoder(BitmapProvider provider, GifHeader gifHeader, ByteBuffer rawData, int sampleSzie){
        this(provider);
        setData(gifHeader, rawData, sampleSize);
    }

    public GifDecoder(BitmapProvider provider){
        this.bitmapProvider=provider;
        header=new GifHeader();
    }

    public GifDecoder(){
        this(new SimpleBitmapProvider());
    }

    public int getWidth(){
        return header.width;
    }

    public int getHeight(){
        return header.height;
    }

    public ByteBuffer getData(){
        return rawData;
    }

    public int getStatus(){
        return status;
    }

    public boolean advance(){
        if(header.frameCount<=0){
            return false;
        }

        if(framePointer==getFrameCount()-1){
            loopIndex++;
        }

        if(header.loopCount!=LOOP_FOREVER&&loopIndex>header.loopCount){
            return false;
        }

        framePointer=(framePointer+1)%header.frameCount;

        return true;
    }

    public int getDelay(int n){
        int delay=-1;
        if((n>=0)&&(n<header.frameCount)){
            delay=header.frames.get(n).delay;
        }

        return delay;
    }

    public int getNextDelay(){
        if(header.frameCount<=0||framePointer<0){
            return 0;
        }

        return getDelay(framePointer);
    }

    public int getFrameCount(){
        return header.frameCount;
    }

    public int getCurrentFrameIndex(){
        return framePointer;
    }

    public boolean setFrameIndex(int frame){
        if (frame < INITIAL_FRAME_POINTER || frame >= getFrameCount()){
            return false;
        }

        framePointer=frame;
        return true;
    }

    public void resetFrameIndex(){
        framePointer=INITIAL_FRAME_POINTER;
    }

    public void resetLoopIndex(){
        loopIndex=0;
    }

    public int getLoopCount(){
        return header.loopCount;
    }

    public int getLoopIndex(){
        return loopIndex;
    }

    public int getByteSize(){
        return rawData.limit()+mainPixels.length+(mainScratch.length*BYTES_PER_INTEGER);
    }

    public synchronized Bitmap getNextFrame(){
        if(header.frameCount<=0||framePointer<0){
            if(Log.isLoggable(TAG, Log.DEBUG)){
                Log.d(TAG, "unable to decode frame, frameCount="
                        + header.frameCount
                        + " framePointer="
                        + framePointer);
            }
            status=STATUS_FORMAT_ERROR;
        }
        if(status==STATUS_FORMAT_ERROR||status==STATUS_OPEN_ERROR){
            if(Log.isLoggable(TAG, Log.DEBUG)){
                Log.d(TAG,"Unable to decode frame, status="+status);
            }

            return null;
        }

        status=STATUS_OK;

        GifFrame currentFrame=header.frames.get(framePointer);
        GifFrame previous=null;
        int previousFrame=framePointer-1;
        if(previousFrame>=0){
            previousFrame=header.frames.get(previousFrame);
        }

        act=currentFrame.lct!=null?currentFrame.lct:header.gct;
        if(act==null){
            if(Log.isLoggable(TAG, Log.DEBUG)){
                Log.d(TAG, "No Valid Color Table for frame #"+framePointer);
            }

            status=STATUS_FORMAT_ERROR;

            return null;
        }
    }
}
