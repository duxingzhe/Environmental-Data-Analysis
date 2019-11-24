package com.luxuan.encoder.util.gl;

public abstract class StreamObjectBase {

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int getNumFrames();

    public abstract int updateFrame();

    public abstract void recycler();
}
