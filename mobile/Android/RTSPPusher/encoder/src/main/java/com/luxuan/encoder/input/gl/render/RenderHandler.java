package com.luxuan.encoder.input.gl.render;

public class RenderHandler {

    private int[] fboId=new int[] { 0 };
    private int[] rboId=new int[] { 0 };
    private int[] texId=new int[] { 0 };

    public int[] getFboId() {
        return fboId;
    }

    public void setFboId(int[] fboId) {
        this.fboId = fboId;
    }

    public int[] getRboId() {
        return rboId;
    }

    public void setRboId(int[] rboId) {
        this.rboId = rboId;
    }

    public int[] getTexId() {
        return texId;
    }

    public void setTexId(int[] texId) {
        this.texId = texId;
    }
}
