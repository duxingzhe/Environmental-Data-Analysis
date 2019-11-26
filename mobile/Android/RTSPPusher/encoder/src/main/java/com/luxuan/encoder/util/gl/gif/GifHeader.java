package com.luxuan.encoder.util.gl.gif;

import java.util.ArrayList;
import java.util.List;

public class GifHeader {

    public int[] gct=null;
    public int status=GifDecoder.STATUS_OK;
    public int frameCount=0;
    public GifFrame currentFrame;
    public List<GifFrame> frames=new ArrayList<>();

    public int width;
    public int height;
    public boolean gctFlag;
    public int gctSize;
    public int bgIndex;
    public int pixelAspect;
    public int bgColor;
    public int loopCount=0;

    public int getHeight(){
        return height;
    }

    public int getWidth(){
        return width;
    }

    public int getNumFrames(){
        return frameCount;
    }

    public int getStatus(){
        return status;
    }
}
