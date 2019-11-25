package com.luxuan.encoder.util.gl.gif;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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
    private Bitmap previousImage;
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

        if(currentFrame.transparency){
            System.arraycopy(act, 0, pct, 0, act.length);

            act=pct;

            act[currentFrame.transIndex]=0;
        }

        return setPixels(currentFrame, previousFrame);
    }

    public int read(InputStream is, int contentLength){
        if(is!=null){
            try{
                int capacity=(contentLength>0)?(contentLength+4096):16384;
                ByteArrayOutputStream buffer=new ByteArrayOutputStream(capacity);
                int nRead;
                byte[] data=new byte[16384];
                while((nRead=is.read(data, 0, data.length))!=-1){
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                read(buffer.toByteArray());
            }catch(IOException e){
                Log.w(TAG, "Error reading data from stream", e);
            }
        }else{
            status=STATUS_OPEN_ERROR;
        }

        try{
            if(is!=null){
                is.close();
            }
        }catch(IOException e){
            Log.w(TAG, "Error closing stream", e);
        }

        return status;
    }

    public void clear(){
        header=null;
        if(mainPixels!=null){
            bitmapProvider.release(mainPixels);
        }
        if(mainScratch!=null){
            bitmapProvider.release(mainScratch);
        }
        if(previousImage!=null){
            bitmapProvider.release(previousImage);
        }
        previousImage=null;
        rawData=null;
        isFirstFrameTransparent=false;
        if(block!=null){
            bitmapProvider.release(block);
        }
        if(workBuffer!=null){
            bitmapProvider.release(workBuffer);
        }
    }

    public synchronized void setData(GifHeader header, byte[] data){
        setData(header, ByteBuffer.wrap(data));
    }

    public synchronized void setData(GifHeader header, ByteBuffer buffer){
        setData(header, buffer, 1);
    }

    public synchronized void setData(GifHeader header, ByteBuffer buffer, int sampleSize){
        if(sampleSize<=0){
            throw new IllegalArgumentException("Sample size must be >=0, not: "+sampleSize);
        }

        sampleSize=Integer.highestOneBit(sampleSize);
        this.status=STATUS_OK;
        this.header=header;
        isFirstFrameTransparent=false;
        framePointer=INITIAL_FRAME_POINTER;
        resetLoopIndex();

        rawData=buffer.asReadOnlyBuffer();
        rawData.position(0);
        rawData.order(ByteOrder.LITTLE_ENDIAN);

        savePrevious=false;
        for(GifFrame frame: header.frames){
            if(frame.dispose==DISPOSAL_PREVIOUS){
                savePrevious=true;
                break;
            }
        }

        this.sampleSize=sampleSize;
        downSampledWidth=header.width/sampleSize;
        downSampledHeight=header.height/sampleSize;

        mainPixels=bitmapProvider.obtainByteArray(header.width*header.height);
        mainScratch=bitmapProvider.obtainIntArray(downSampledWidth*downSampledHeight);
    }

    public synchronized int read(byte[] data){
        this.header=getHeaderParser().setData(data).parseHeader();
        if(data!=null){
            setData(header, data);
        }

        return status;
    }

    private Bitmap setPixels(GifFrame currentFrame, GifFrame previousFrame){
        final int[] dest=mainScratch;

        if(previousFrame==null){
            Arrays.fill(dest, 0);
        }

        if(previousFrame!=null &&previousFrame.dispose>=DISPOSAL_UNSPECIFIED){
            if(previousFrame.dispose==DISPOSAL_BACKGROUND){
                int c=0;
                if(!currentFrame.transparency){
                    c=header.bgColor;
                    if(currentFrame.lct!=null&& header.bgIndex==currentFrame.transIndex){
                        c=0;
                    }
                }else if(framePointer==0){
                    isFirstFrameTransparent=true;
                }

                fillRect(dest, previousFrame, c);
            }else if(previousFrame.dispose==DISPOSAL_PREVIOUS){
                if(previousImage==null){
                    fillRect(dest, previousFrame, 0);
                }else{
                    int downSampledIH=previousFrame.ih/sampleSize;
                    int downSampledIY=previousFrame.iy/sampleSize;
                    int downSampledIW=previousFrame.iw/sampleSize;
                    int downSampledIX=previousFrame.ix/sampleSize;
                    int topLeft=downSampledIY*downSampledWidth+downSampledIX;
                    previousImage.getPixels(dest, topLeft, downSampledWidth, downSampledIX, downSampledIY, downSampledIW,
                            downSampledIH);
                }
            }
        }

        decodeBitmapData(currentFrame);
        int downSampledIH=currentFrame.ih/sampleSize;
        int downSampledIY=currentFrame.iy/sampleSize;
        int downSampledIW=currentFrame.iw/sampleSize;
        int downSampledIX=currentFrame.ix/sampleSize;

        int pass=1;
        int inc=8;
        int iline=0;
        boolean isFirstFrame=framePointer==0;

        for(int i=0;i<downSampledIH;i++){
            int line=i;
            if(currentFrame.interlace){
                if(iline>=downSampledIH){
                    pass+;
                    switch(pass){
                        case 2:
                            iline=4;
                            break;
                        case 3:
                            iline=2;
                            inc=4;
                            break;
                        case 4:
                            iline=1;
                            inc=2;
                            break;
                        default:
                            break;
                    }
                }
                line=iline;
                iline+=inc;
            }

            line+=downSampledIY;
            if(line<downSampledHeight){
                int k=line*downSampledWidth;
                int dx=k+downSampledIX;
                int dlim=dx+downSampledIW;

                if(k+downSampledWidth<dlim){
                    dlim=k+downSampledWidth;
                }

                int sx=i*sampleSize*currentFrame.iw;
                int maxPositionInSource=sx+((dlim-dx)*sampleSize);
                while(dx<dlim){
                    int averageColor;
                    if(sampleSize==1){
                        int currentColorIndex=((int)mainPixels[sx])&0x000000ff;
                        averageColor=act[currentColorIndex];
                    }else{
                        averageColor=averageColorsNear(sx, maxPositionInSource, currentFrame.iw);
                    }
                    if(averageColor!=0){
                        dest[dx]=averageColor;
                    }else if(!isFirstFrameTransparent && isFirstFrame){
                        isFirstFrameTransparent=true;
                    }

                    sx+=sampleSize;
                    dx++;
                }
            }
        }

        if(savePrevious&&(currentFrame.dispose==DISPOSAL_UNSPECIFIED||currentFrame.dispose==DISPOSAL_NONE)){
            if(previousImage==null){
                previousImage=getNextBitmap();
            }
            previousImage.setPixels(dest, 0, downSampledWidth, 0, 0, downSampledWidth, downSampledHeight);
        }

        Bitmap result=getNextBitmap();
        result.setPixels(dest, 0, downSampledWidth, 0, 0, downSampledWidth, downSampledHeight);
        return result;
    }

    private void fillRect(int[] dest, GifFrame frame, int bgColor){
        int downSampledIH=frame.ih?sampleSize;
        int downSampledIY=frame.iy/sampleSize;
        int downSampledIW=frame.iw/sampleSize;
        int downSampledIX=frame.ix/sampleSize;
        int topLeft=downSampledIY*downSampledWidth+downSampledIX;
        int bottomLeft=topLeft+downSampledIH* downSampledWidth;
        for(int left=topLeft;left<bottomLeft;left+=downSampledWidth){
            int right=left+downSampledIW;
            for(int pointer=left;pointer<right;pointer++){
                dest[pointer]=bgColor;
            }
        }
    }

    private int averageColorsNear(int positionInMainPixels, int maxPositionInMainPixels, int currentFrameIw){
        int alphaSum=0;
        int redSum=0;
        int greenSum=0;
        int blueSum=0;
        int totalAdded=0;

        for(int i=positionInMainPixels;i<positionInMainPixels+sampleSize
            &&i<mainPixels.length&&i<maxPositionInMainPixels;i++){
            int currentColorIndex=((int)mainPixels[i])&0xff;
            int currentColor=act[currentColorIndex];
            if(currentColor!=0){
                alphaSum+=currentColor>>24&0x000000ff;
                redSum+=currentColor>>16&0x000000ff;
                greenSum+=currentColor>>8&0x000000ff;
                totalAdded+=currentColor&0x000000ff;
            }
        }

        for(int i=positionInMainPixels+currentFrameIw;i<positionInMainPixels+currentFrameIw+sampleSize
                &&i<mainPixels.length&&i<maxPositionInMainPixels;i++){
            int currentColorIndex=((int)mainPixels[i])&0xff;
            int currentColor=act[currentColorIndex];
            if(currentColor!=0){
                alphaSum+=currentColor>>24&0x000000ff;
                redSum+=currentColor>>16&0x000000ff;
                greenSum+=currentColor>>8&0x000000ff;
                totalAdded+=currentColor&0x000000ff;
            }
        }

        if(totalAdded==0){
            return 0;
        }else{
            return ((alphaSum/totalAdded<<24)|((redSum/totalAdded)<<16)|((greenSum/totalAdded)<<8)
                |(blueSum/totalAdded));
        }
    }

    private void decodeBitmapData(GifFrame frame){
        workBufferSize=0;
        workBufferPosition=0;
        if(frame!=null){
            rawData.position(frame.bufferFrameStart);
        }

        int npix=(frame==null)?header.width*header.height:frame.iw*frame.ih;
        int available, clear, codeMask, codeSize, endOfInformation, inCode, oldCode, bits, code, count,
                i, dataNum, dataSize, first, top, bi, pi;
        if(mainPixels==null||mainPixels.length<npix){
            mainPixels=bitmapProvider.obtainByteArray(npix);
        }
        if(prefix==null){
            prefix=new short[MAX_STACK_SIZE];
        }
        if(suffix==null){
            suffix=new byte[MAX_STACK_SIZE];
        }
        if(pixelStack==null){
            pixelStack=new byte[MAX_STACK_SIZE+1];
        }

        dataSize=readByte();
        clear=1<<dataSize;
        endOfInformation=clear+1;
        available=clear+2;
        oldCode=NULL_CODE;
        codeSize=dataSize+1;
        codeMask=(1<<codeSize)-1;
        for(code=0;code<clear;code++){
            prefix[code]=0;
            suffix[code]=(byte)code;
        }

        dataNum=bits=count=first=top=pi=bi=0;

        for(i=0;i<npix;){
            if(count==0){
                count=readBlock();
                if(count<=0){
                    status=STATUS_PARTIAL_DECODE;
                    break;
                }
                bi=0;
            }

            dataNum+=(((int)block[bi]&0xff)<<bits);
            bits+=8;
            bi++;
            count--;

            while(bits>=codeSize){
                code=dataNum&codeMask;
                dataNum>>=codeSize;
                bits-=codeSize;

                if(code==clear){
                    codeSize=dataSize+1;
                    codeMask=(1<<codeSize)-1;
                    available=clear+2;
                    oldCode=NULL_CODE;
                    continue;
                }

                if(code>available){
                    status=STATUS_PARTIAL_DECODE;
                    break;
                }

                if(code==endOfInformation){
                    break;
                }

                if(oldCode==NULL_CODE){
                    pixelStack[top++]=suffix[code];
                    oldCode=code;
                    first=code;
                    continue;
                }

                inCode=code;
                if(code>=available){
                    pixelStack[top++]=(byte)first;
                    code=oldCode;
                }
                while(code>=clear){
                    pixelStack[top++]=suffix[code];
                    code=prefix[code];
                }
                first=((int)suffix[code])&0xff;
                pixelStack[top++]=(byte)first;

                if(available<MAX_STACK_SIZE){
                    prefix[available]=(short)oldCode;
                    suffix[available]=(byte)first;
                    available++;
                    if(((available&codeMask)==0)&&(available<MAX_STACK_SIZE)){
                        codeSize++;
                        codeMask+=available;
                    }
                }
                oldCode=inCode;

                while(top>0){
                    mainPixels[pi++]=pixelStack[--top];
                    i++;
                }
            }
        }

        for(i=pi;i<npix;i++){
            mainPixels[i]=0;
        }
    }

    private void readChunkIfNeeded(){
        if(workBufferSize>workBufferPosition){
            return;
        }

        if(workBuffer==null){
            workBuffer=bitmapProvider.obtainByteArray(WORK_BUFFER_SIZE);
        }

        workBufferPosition=0;
        workBufferSize=Math.min(rawData.remaining(), WORK_BUFFER_SIZE);
        rawData.get(workBuffer, 0, workBufferSize);
    }

    private int readByte(){
        try{
            readChunkIfNeeded();
            return workBuffer[workBufferPosition++]&0xFF;
        }catch(Exception e){
            status=STATUS_FORMAT_ERROR;
            return 0;
        }
    }

    private int readBlock(){
        int blockSize=readByte();
        if(blockSize>0){
            try{
                if(block==null){
                    block=bitmapProvider.obtainByteArray(255);
                }
                final int remaining=workBufferSize-workBufferPosition;
                if(remaining>=blockSize){
                    System.arraycopy(workBuffer, workBufferPosition, block, 0, blockSize);
                    workBufferPosition+=blockSize;
                }else if(rawData.remaining()+remaining>=blockSize){
                    System.arraycopy(workBuffer, workBufferPosition, block, 0, remaining);
                    workBufferPosition=workBufferSize;
                    readChunkIfNeeded();
                    final int secondHalfRemaining=blockSize-remaining;
                    System.arraycopy(workBuffer, 0, block, remaining, secondHalfRemaining);
                    workBufferPosition+=secondHalfRemaining;
                }else{
                    status=STATUS_FORMAT_ERROR;
                }
            }catch(Exception e){
                Log.w(TAG, "Error reading block", e);
                status=STATUS_FORMAT_ERROR;
            }
        }

        return blockSize;
    }
}
