package com.luxuan.rtmppusher.yuv;

import android.content.Context;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

import java.nio.Buffer;
import java.nio.FloatBuffer;

public class LxYuvRender implements LXEGLSurfaceView.LxGLRender {

    private Context context;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f,1f,
            -1f,1f,
            1f,-1f,
            -1f,-1f
    };

    private FloatBuffer textureBuffer;
    private final float[] textureVertexData = {
            1f,0f,
            0f,0f,
            1f,1f,
            0f,1f
    };

    private int program;
    private int vPosition;
    private int fPosition;

    private int sampler_y;
    private int sampler_u;
    private int sampler_v;

    private int[] texture_yunv;
    private int textureId;

    public int w;
    public int h;

    public Buffer y;
    public Buffer u;
    public Buffer v;

    private LxYuvFboRender lxYuvFboRender;

    private float[] matrix=new float[16];
    private int u_matrix;


}
