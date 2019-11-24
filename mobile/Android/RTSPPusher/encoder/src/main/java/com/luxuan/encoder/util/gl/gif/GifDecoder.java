package com.luxuan.encoder.util.gl.gif;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.ByteBuffer;

public class GifDecoder {

    private static final String TAG=GifDecoder.class.getSimpleName();

    public static final int STATUS_OK=0;

    public static final int STATUS_FOMRAT_ERROR=1;

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
}
