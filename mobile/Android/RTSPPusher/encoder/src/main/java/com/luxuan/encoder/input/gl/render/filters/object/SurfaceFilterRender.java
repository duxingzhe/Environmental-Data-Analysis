package com.luxuan.encoder.input.gl.render.filters.object;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.luxuan.encoder.input.gl.Sprite;

import java.nio.FloatBuffer;

public class SurfaceFilterRender extends BaseObjectFilterRender {

    //rotation matrix
    private final float[] squareVertexDataFilter = {
            // X, Y, Z, U, V
            -1f, -1f, 0f, 0f, 0f, //bottom left
            1f, -1f, 0f, 1f, 0f, //bottom right
            -1f, 1f, 0f, 0f, 1f, //top left
            1f, 1f, 0f, 1f, 1f, //top right
    };

    private int program=-1;
    private int aPositionHndle=-1;
    private int aTextureHandle=-1;
    private int uMVPMatrixHandle=-1;
    private int uSTMatrixHandle=-1;
    private int uSamplerHandle=-1;
    private int uSamplerSurfaceHandle=-1;
    private int aTextureObjectHandle=-1;
    private int uAlphaHandle=-1;

    private int[] surfaceId=new int[]{-1};
    private Sprite sprite;
    private FloatBuffer squareVertexSurface;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private float alpha=1f;
}
