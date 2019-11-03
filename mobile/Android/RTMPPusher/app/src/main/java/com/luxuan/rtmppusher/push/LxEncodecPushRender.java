package com.luxuan.rtmppusher.push;

import android.content.Context;
import android.graphics.Bitmap;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LxEncodecPushRender implements LXEGLSurfaceView.LxGLRender {

    private Context context;
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int textureId;

    private int vboId;

    private Bitmap bitmap;
    private int bitmapTextureId;

    public LxEncodecPushRender(Context context, int textureId){
        this.context=context;
        this.textureId=textureId;

        bitmap=LxShaderUtil.creatTextImage("Environment", 50, "#000000", "#00000000", 0);

        float r=1.0f*bitmap.getWidth()/bitmap.getHeight();
        float w=r*0.1f;

        vertexData[8]=0.8f-w;
        vertexData[9]=-0.8f;

        vertexData[10]=0.8f;
        vertexData[11]=-0.8f;

        vertexData[12]=0.8f-w;
        vertexData[13]=-0.7f;

        vertexData[14]=0.8f;
        vertexData[15]=-0.7f;

        vertexBuffer= ByteBuffer.allocateDirect(vertexData.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer= ByteBuffer.allocateDirect(fragmentData.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(fragmentData);
        fragmentBuffer.position(0);
    }
}
