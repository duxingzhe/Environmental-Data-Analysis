package com.luxuan.encoder.util.gl.gif;

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

    }

}
