package com.luxuan.encoder.util.gl;

import android.graphics.Bitmap;
import android.util.Log;

import com.luxuan.encoder.util.gl.gif.GifDecoder;

import java.io.IOException;
import java.io.InputStream;

public class GifStreamObject extends StreamObjectBase {

    private static final String TAG="GifStreamObject";

    private int numFrames;
    private Bitmap[] gifBitmaps;
    private int[] gifDelayFrames;
    private long startDelayFrame;
    private int currentGifFrame;

    public GifStreamObject(){

    }

    @Override
    public int getWidth(){
        return gifBitmaps!=null?gifBitmaps[0].getWidth():0;
    }

    @Override
    public int getHeight(){
        return gifBitmaps!=null?gifBitmaps[0].getHeight():0;
    }

    public void load(InputStream inputStreamGif) throws IOException {
        GifDecoder gifDecoder=new GifDecoder();

        if(gifDecoder.read(inputStreamGif, inputStreamGif.available())==0){
            Log.i(TAG, "read gif ok");

            numFrames=gifDecoder.getFrameCount();
            gifDelayFrames=new int[numFrames];
            gifBitmaps=new Bitmap[numFrames];
            for(int i=0;i<numFrames;i++){
                gifDecoder.advance();
                gifBitmaps[i]=gifDecoder.getNextFrame();
                gifDelayFrames[i]=gifDecoder.getNextDelay();
            }

            Log.i(TAG, "finish load gif frames");
        }else{
            throw new IOException("Read gif error");
        }
    }

    @Override
    public void recycle(){
        if(gifBitmaps!=null){
            for(int i=0;i<numFrames;i++){
                gifBitmaps[i].recycle();
            }
        }
    }

    @Override
    public int getNumFrames(){
        return numFrames;
    }

    public int[] getGifDelayFrames(){
        return gifDelayFrames;
    }

    public Bitmap[] getGifBitmaps(){
        return gifBitmaps;
    }

    public int updateFrame(int size){
        return size<=1?0:updateFrame();
    }

    @Override
    public int updateFrame(){
        if(startDelayFrame==0){
            startDelayFrame=System.currentTimeMillis();
        }

        if(System.currentTimeMillis()-startDelayFrame>=gifDelayFrames[currentGifFrame]){
            if(currentGifFrame>=numFrames-1){
                currentGifFrame=0;
            }else{
                currentGifFrame++;
            }
            startDelayFrame=0;
        }

        return currentGifFrame;
    }
}
