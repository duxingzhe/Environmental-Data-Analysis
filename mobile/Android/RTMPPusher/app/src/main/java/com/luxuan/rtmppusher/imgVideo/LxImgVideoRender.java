package com.luxuan.rtmppusher.imgVideo;

import android.content.Context;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LxImgVideoRender implements LXEGLSurfaceView.LxGLRender {

    private Context context;
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };
    private FloatBuffer vertexBuffer;
    private float[] fragmentData = {
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int textureId;

    private int vboId;
    private int fboId;

    private int imgTextureId;

    private OnRenderCreateListener onRenderCreateListener;

    private LxImgFboRender lxImgFboRender;

    private int srcImg=0;

    public LxImgVideoRender(Context context){
        this.context=context;

        vertexBuffer= ByteBuffer.allocateDirect(vertexData.length *4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer=ByteBuffer.allocateDirect(fragmentData.length *4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(fragmentData);
        fragmentBuffer.position(0);
    }

    public interface OnRenderCreateListener{
        void onCreate(int textureId);
    }

    public void setCurrentImgSrc(int srcImg){
        this.srcImg=srcImg;
    }
}
