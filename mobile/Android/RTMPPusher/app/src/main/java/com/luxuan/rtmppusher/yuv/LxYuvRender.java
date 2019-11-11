package com.luxuan.rtmppusher.yuv;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.luxuan.rtmppusher.R;
import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;
import com.luxuan.rtmppusher.egl.LxShaderUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    private int[] texture_yuv;

    private int fboId;
    private int textureId;

    public int w;
    public int h;

    public Buffer y;
    public Buffer u;
    public Buffer v;

    private LxYuvFboRender lxYuvFboRender;

    private float[] matrix=new float[16];
    private int u_matrix;

    public LxYuvRender(Context context){
        this.context=context;
        lxYuvFboRender=new LxYuvFboRender(context);

        vertexBuffer= ByteBuffer.allocateDirect(vertexData.length *4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        textureBuffer=ByteBuffer.allocateDirect(textureVertexData.length *4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(textureVertexData);
        textureBuffer.position(0);

        // Matrix E or I
        Matrix.setIdentityM(matrix, 0);

    }

    @Override
    public void onSurfaceCreated(){
        lxYuvFboRender.onCreate();
        String vertexSource= LxShaderUtil.getRawResource(context, R.raw.vertex_shader_yuv);
        String fragmentSource=LxShaderUtil.getRawResource(context, R.raw.fragment_shader_yuv);

        program=LxShaderUtil.createProgram(vertexSource, fragmentSource);
        vPosition= GLES20.glGetAttribLocation(program, "v_Position");
        fPosition=GLES20.glGetAttribLocation(program, "f_Position");
        u_matrix=GLES20.glGetUniformLocation(program, "u_Matrix");

        sampler_y=GLES20.glGetUniformLocation(program, "sampler_y");
        sampler_u=GLES20.glGetUniformLocation(program, "sampler_u");
        sampler_v=GLES20.glGetUniformLocation(program, "sampler_v");

        texture_yuv=new int[3];
        GLES20.glGenTextures(3, texture_yuv, 0);

        for(int i=0;i<3;i++){
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_yuv[i]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        int[] fbos=new int[1];
        GLES20.glGenBuffers(1, fbos, 0);
        fboId=fbos[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        int[] textureIds=new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        textureId=textureIds[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 720, 500, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureId, 0);
        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)!=GLES20.GL_FRAMEBUFFER_COMPLETE){
            Log.e("lx", "fbo wrong");
        }else{
            Log.e("lx", "fbo success");
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }
}
