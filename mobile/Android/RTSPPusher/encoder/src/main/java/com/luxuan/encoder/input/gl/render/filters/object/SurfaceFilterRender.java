package com.luxuan.encoder.input.gl.render.filters.object;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;

import com.luxuan.encoder.R;
import com.luxuan.encoder.input.gl.Sprite;
import com.luxuan.encoder.util.gl.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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
    private int aPositionHandle=-1;
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

    public SurfaceFilterRender(){
        squareVertex= ByteBuffer.allocateDirect(squareVertexDataFilter.length*FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        squareVertex.put(squareVertexDataFilter).position(0);
        sprite=new Sprite();
        float[] vertices=sprite.getTransformedVertices();
        squareVertexSurface=ByteBuffer.allocateDirect(vertices.length*FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        squareVertexSurface.put(vertices).position(0);
        sprite.getTransformedVertices();

        Matrix.setIdentityM(MVPMatrix, 0);
        Matrix.setIdentityM(STMatrix, 0);
    }

    @Override
    protected void initGlFilter(Context context){
        String vertexShader= GlUtil.getStringFromRaw(context, R.raw.object_vertex);
        String fragmentShader=GlUtil.getStringFromRaw(context, R.raw.surface_fragment);

        program= GlUtil.createProgram(vertexShader, fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(program, "aPosition");
        aTextureHandle=GLES20.glGetAttribLocation(program, "aTextureCoord");
        aTextureObjectHandle=GLES20.glGetAttribLocation(program, "aTextureObjectCoord)");
        uMVPMatrixHandle=GLES20.glGetUniformLocation(program, "uMVPMatrix");
        uSTMatrixHandle=GLES20.glGetUniformLocation(program, "uSTMatrix");
        uSamplerHandle=GLES20.glGetUniformLocation(program, "uSampler");
        uSamplerSurfaceHandle=GLES20.glGetUniformLocation(program, "SamplerSurface");
        uAlphaHandle=GLES20.glGetUniformLocation(program, "uAlpha");

        GlUtil.createExternalTextures(1, surfaceId, 0);
        surfaceTexture=new SurfaceTexture(surfaceId[0]);
        surfaceTexture.setDefaultBufferSize(getWidth(), getHeight());
        surface=new Surface(surfaceTexture);
    }

    @Override
    protected void drawFilter(){
        surfaceTexture.updateTexImage();

        GLES20.glUseProgram(program);

        squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
    }

    @Override
    public void release() {
        if (surfaceId != null) {
            GLES20.glDeleteTextures(1, surfaceId, 0);
        }

        surfaceId=new int[]{-1};
        surfaceTexture.release();
        surface.release();
    }
}
