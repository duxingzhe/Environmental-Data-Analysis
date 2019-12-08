package com.luxuan.encoder.input.gl.render.filters.object;

import com.luxuan.encoder.input.gl.Sprite;
import com.luxuan.encoder.util.gl.StreamObjectBase;

import java.nio.FloatBuffer;

public abstract class BaseObjectFilterRender {

    //rotation matrix
    private final float[] squareVertexDataFilter = {
            // X, Y, Z, U, V
            -1f, -1f, 0f, 0f, 0f, //bottom left
            1f, -1f, 0f, 1f, 0f, //bottom right
            -1f, 1f, 0f, 0f, 1f, //top left
            1f, 1f, 0f, 1f, 1f, //top right
    };

    private int program=-1;
    private int aPositionHandle=-1;
    private int aTextureHandle=-1;
    private int aTextureObjectHandle=-1;
    private int uMVPMatrixHandle=-1;
    private int uSTMatrixHandle=-1;
    private int uSamplerHandler=-1;
    private int uObjectHandler=-1;
    protected int uAlphaHandle=-1;

    private FloatBuffer sqaureVertexObject;

    protected int[] streamObjectTextureId=new int[]{-1};
    protected TextureLoader textureLoader=new TextureLoader();
    protected StreamObjectBase streamObjectBase;
    private Sprite sprite;
    protected float alpah=1f;
    protected boolean shouldLoad=false;

}
