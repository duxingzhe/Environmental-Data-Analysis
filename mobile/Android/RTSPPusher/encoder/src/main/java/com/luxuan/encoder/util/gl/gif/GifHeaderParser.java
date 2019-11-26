package com.luxuan.encoder.util.gl.gif;

import android.util.Log;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class GifHeaderParser {

    public static final String TAG="GifHeaderParser";

    public static final int MIN_FRAME_DELAY=2;
    public static final int DEFAULT_FRAME_DELAY=10;
    private static final int MAX_BLOCK_SIZE=256;

    private final byte[] block=new byte[MAX_BLOCK_SIZE];

    private ByteBuffer rawData;
    private GifHeader header;
    private int blockSize=0;

    public GifHeaderParser setData(ByteBuffer data){
        reset();
        rawData=data.asReadOnlyBuffer();
        rawData.position(0);
        rawData.order(ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public GifHeaderParser setData(byte[] data){
        if(data!=null){
            setData(ByteBuffer.wrap(data));
        }else{
            rawData=null;
            header.status=GifDecoder.STATUS_OPEN_ERROR;
        }
        return this;
    }

    public void clear(){
        rawData=null;
        header=null;
    }

    private void reset(){
        rawData=null;
        Arrays.fill(block, (byte)0);
        header=new GifHeader();
        blockSize=0;
    }

    public GifHeader parseHeader(){
        if(rawData==null){
            throw new IllegalStateException("You must call setData() before parseHeader()");
        }
        if(err()){
            return header;
        }

        readHeader();
        if(!err()){
            readContents();
            if(header.frameCount<0){
                header.status=GifDecoder.STATUS_FORMAT_ERROR;
            }
        }

        return header;
    }

    public boolean isAnimated(){
        readHeader();
        if(!err()){
            readContents(2);
        }

        return header.frameCount>1;
    }

    private void readContents(){
        readContents(Integer.MAX_VALUE);
    }

    private void readContents(int maxFrames){

        boolean done=false;
        while(!(done||err()||header.frameCount>maxFrames)){
            int code=read();
            switch(code){
                case 0x2C:
                    if(header.currentFrame==null){
                        header.currentFrame=new GifFrame();
                    }
                    readBitmap();
                    break;
                case 0x21:
                    code=read();
                    switch(code){

                        case 0xf9:
                            header.currentFrame=new GifFrame();
                            readGraphicControlExt();
                            break;
                        case 0xFF:
                            readBlock();
                            String app="";
                            for(int i=0;i<11;i++){
                                app+=(char)block[i];
                            }
                            if(app.equals("NETSCAPE2.0")){
                                readNetscapeExt();
                            }else{
                                skip();
                            }
                            break;
                        case 0x01:
                            skip();
                            break;
                        default:
                            skip();
                    }
                    break;
                case 0x3b:
                    done=true;
                    break;
                case 0x00:
                default:
                    header.status=GifDecoder.STATUS_FORMAT_ERROR;
            }
        }
    }

    private void readGraphicControlExt(){
        read();

        int packed=read();

        header.currentFrame.dispose=(packed&0x1c)>>2;
        if(header.currentFrame.dispose==0){

            header.currentFrame.dispose=1;
        }

        header.currentFrame.transparency=(packed&1)!=0;

        int delayInHundredthsOfASecond=readShort();

        if(delayInHundredthsOfASecond<MIN_FRAME_DELAY){
            delayInHundredthsOfASecond=DEFAULT_FRAME_DELAY;
        }

        header.currentFrame.delay=delayInHundredthsOfASecond*10;
        header.currentFrame.transIndex=read();

        read();
    }

    private void readBitmap(){
        header.currentFrame.ix=readShort();
        header.currentFrame.iy=readShort();
        header.currentFrame.iw=readShort();
        header.currentFrame.ih=readShort();

        int packed=read();

        boolean lctFlag=(packed&0x80)!=0;
        int lctSize=(int)Math.pow(2, (packed&0x07)+1);

        header.currentFrame.interlace=(packed&0x40)!=0;

        if(lctFlag){
            header.currentFrame.lct=readColorTable(lctSize);
        }else{
            header.currentFrame.lct=null;
        }

        header.currentFrame.bufferFrameStart=rawData.position();

        skipImageData();

        if(err()){
            return;
        }

        header.frameCount++;

        header.frames.add(header.currentFrame);
    }

    private void readNetscapeExt(){
        do{
            readBlock();
            if(block[0]==1){
                int b1=((int)block[1])&0xff;
                int b2=((int)block[2])&0xff;

                header.loopCount=(b2<<8)|b1;
                if(header.loopCount==0){
                    header.loopCount=GifDecoder.LOOP_FOREVER;
                }
            }
        }while((blockSize>0)&&!err());
    }

    private void readHeader(){
        String id="";
        for(int i=0;i<6;i++){
            id+=(char)read();
        }
        if(!id.startsWith("GIF")){
            header.status=GifDecoder.STATUS_FORMAT_ERROR;
            return;
        }
        readLSD();
        if(header.gctFlag&&!err()){
            header.gct=readColorTable(header.gctSize);
            header.bgColor=header.gct[header.bgIndex];
        }
    }

    private void readLSD(){
        header.width=readShort();
        header.height=readShort();

        int packed=read();
        header.gctFlag=(packed&0x80)!=0;

        header.gctSize=2<<(packed&7);
        header.bgIndex=read();
        header.pixelAspect=read();
    }

    private int read(){
        int currentByte=0;
        try{
            currentByte=rawData.get()&0xFF;
        }catch(Exception e){
            header.status=GifDecoder.STATUS_FORMAT_ERROR;
        }
        return currentByte;
    }

    private int[] readColorTable(int ncolors){
        int nbytes=3*ncolors;
        int[] tab=null;
        byte[] c=new byte[nbytes];

        try{
            rawData.get(c);

            tab=new int[MAX_BLOCK_SIZE];
            int i=0;
            int j=0;
            while(i<ncolors){
                int r=((int)c[j++])&0xff;
                int g=((int)c[j++])&0xff;
                int b=((int)c[j++])&0xff;

                tab[i++]=0xff000000|(r<<16)|(g<<8)|b;
            }
        }catch(BufferUnderflowException e){
            if(Log.isLoggable(TAG, Log.DEBUG)){
                Log.d(TAG,"Format Error Reading Color Table", e);
            }
            header.status=GifDecoder.STATUS_FORMAT_ERROR;
        }

        return tab;
    }

    private void skipImageData(){
        read();
        skip();
    }

    private void skip(){
        try{
            int blockSize;
            do{
                blockSize=read();
                rawData.position(rawData.position()+blockSize);
            }while(blockSize>0);
        }catch(IllegalArgumentException e){

        }
    }

    private int readBlock(){
        blockSize=read();
        int n=0;
        if(blockSize>0){
            int count=0;
            try{
                while(n<blockSize){
                    count=blockSize-n;
                    rawData.get(block, n, count);
                    n+=count;
                }
            }catch(Exception e){
                if(Log.isLoggable(TAG, Log.DEBUG)){
                    Log.d(TAG,"Error Reading Block n: "+n+" count: "+count+" blockSize: "+blockSize, e);
                }
                header.status=GifDecoder.STATUS_FORMAT_ERROR;
            }
        }

        return n;
    }

    private int readShort(){
        return rawData.getShort();
    }

    private boolean err(){
        return header.status!=GifDecoder.STATUS_OK;
    }

}
