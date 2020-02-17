package com.luxuan.encoder.input.gl.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;

import com.luxuan.encoder.R;
import com.luxuan.encoder.input.video.CameraHelper;
import com.luxuan.encoder.util.gl.GlUtil;
import com.luxuan.encoder.util.gl.PreviewSizeCalculator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SimpleCameraRender {

    protected FloatBuffer squareVertex;

    public static final int FLOAT_SIZE_BYTES=4;
    public static final int SQUARE_VERTEX_DATA_STRIDE_BYTES=5*FLOAT_SIZE_BYTES;
    public static final int SQUARE_VERTEX_DATA_POS_OFFSET=0;
    public static final int SQUARE_VERTEX_DATA_UV_OFFSET=3;

    protected float[] MVPMatrix=new float[16];
    protected float[] STMatrix=new float[16];
    private float[] rotationMatrix=new float[16];
    private float[] scaleMatrix=new float[16];

    private int[] texturesID=new int[1];

    private int program=-1;
    private int textureID=-1;
    private int uMVPMatrixHandle=-1;
    private int uSTMatrixHandle=-1;
    private int aPositionHandle=-1;
    private int aTextureCoordHandle=-1;

    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private int streamWidth;
    private int streamHeight;

    public SimpleCameraRender(){
        Matrix.setIdentityM(MVPMatrix, 0);
        Matrix.setIdentityM(STMatrix, 0);
        float[] vertex= CameraHelper.getVerticesData();
        squareVertex= ByteBuffer.allocateDirect(vertex.length*FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        squareVertex.put(vertex).position(0);
        setRotation(0);
        setFlip(false, false);
    }

    public void drawFrame(int width, int height, boolean keepAspectRatio){
        GlUtil.checkGlError("drawCamera start");

        surfaceTexture.getTransformMatrix(STMatrix);
        PreviewSizeCalculator.calculateViewPort(keepAspectRatio, width, height, streamWidth, streamHeight);

        GLES20.glUseProgram(program);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);

        squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
        GLES20.glEnableVertexAttribArray(aPositionHandle);

        squareVertex.position(SQUARE_VERTEX_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(aTextureCoordHandle, 2, GLES20.GL_FLOAT, false,
                SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MVPMatrix, 0);
        GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, STMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureID);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GlUtil.checkGlError("drawCamera end");
    }

    public void initGl(Context context, int streamWidth, int streamHeight){
        this.streamWidth=streamWidth;
        this.streamHeight=streamHeight;
        String vertexShader= GlUtil.getStringFromRaw(context, R.raw.simple_vertex);
        String fragmentShader=GlUtil.getStringFromRaw(context, R.raw.camera_fragment);

        program=GlUtil.createProgram(vertexShader, fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(program, "aPosition");
        aTextureCoordHandle= GLES20.glGetAttribLocation(program, "aTextureCoord");
        uMVPMatrixHandle= GLES20.glGetUniformLocation(program, "uMVPMatrix");
        uSTMatrixHandle= GLES20.glGetUniformLocation(program, "uSTMatrix");

        GlUtil.createExternalTextures(1, texturesID, 0);
        textureID=texturesID[0];
        surfaceTexture=new SurfaceTexture(textureID);
        surfaceTexture.setDefaultBufferSize(streamWidth, streamHeight);
        surface=new Surface(surfaceTexture);
        GlUtil.checkGlError("initGl end");
    }

    public void release(){
        GLES20.glDeleteProgram(program);
        surfaceTexture=null;
        surface=null;
    }

    public int getTextureID() {
        return textureID;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public Surface getSurface() {
        return surface;
    }

    public void updateFrame(){
        surfaceTexture.updateTexImage();
    }

    public void setRotation(int rotation){
        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, rotation, 0, 0, -1f);
        update();
    }

    public void setFlip(boolean isFlipHorizontal, boolean isFlipVertical){
        Matrix.setIdentityM(scaleMatrix, 0);
        Matrix.scaleM(scaleMatrix, 0, isFlipHorizontal?-1f:1f, isFlipVertical?-1:1f, 1f);
        update();
    }

    public void update(){
        Matrix.setIdentityM(MVPMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, scaleMatrix, 0, MVPMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, rotationMatrix, 0, MVPMatrix, 0);
    }
}
