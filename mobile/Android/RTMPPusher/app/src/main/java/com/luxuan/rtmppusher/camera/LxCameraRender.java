package com.luxuan.rtmppusher.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import com.luxuan.rtmppusher.R;
import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;
import com.luxuan.rtmppusher.egl.LxShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LxCameraRender implements LXEGLSurfaceView.LxGLRender, SurfaceTexture.OnFrameAvailableListener {

    private Context context;

    private float[] vertexData={
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,
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
    private int vboId;
    private int fboId;

    private int fboTextureId;
    private int cameraTextureId;

    private int uMatrix;
    private float[] matrix=new float[16];

    private SurfaceTexture surfaceTexture;
    private OnSurfaceCreateListener onSurfaceCreateListener;

    private LxCameraFboRender lxCameraFboRender;

    private int screenWidth;
    private int screenHeight;

    private int width;
    private int height;

    public LxCameraRendrer(Context context){
        this.context=context;
        screenWidth=DisplayUtils.getScreenWidth(context);
        screenHeight=DisplayUtils.getScreenHeight(context);

        lxCameraFboRender=new LxCameraFboRender(context);
        vertexBuffer= ByteBuffer.allocateDirect(vertexData.length *4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer=ByteBuffer.allocateDirect(fragmentData.length*4).order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(fragmentData);
        fragmentBuffer.position(0);
    }

    public void setOnSurfaceCreateListener(OnSurfaceCreateListener onSurfaceCreateListener){
        this.onSurfaceCreateListener=onSurfaceCreateListener;
    }

    @Override
    public void onSurfaceCreated(){

        lxCameraFboRender.onCreate();
        String vertexSource= LxShaderUtil.getRawResource(context, R.raw.vertex_shader);
        String fragmentSource=LxShaderUtil.getRawResource(context, R.raw.fragment_shader);

        program=LxShaderUtil.createProgram(vertexSource, fragmentSource);
        vPosition= GLES20.glGetAttribLocation(program, "v_Position");
        fPosition=GLES20.glGetAttribLocation(program, "f_position");
        uMatrix=GLES20.glGetUniformLocation(program, "u_Matrix");

        int[] vbos=new int[1];
        GLES20.glGenBuffers(1, vbos, 0);
        vboId=vbos[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length*4+fragmentData.length*4, null, GLES20.GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length*4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length*4, fragmentData.length *4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        int[] fbos=new int[1];
        GLES20.glGenBuffers(1, fbos, 0);
        fboId=fbos[0];
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        int[] textureIds=new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        fboTextureId=textureIds[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTextureId);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_REPEAT);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, screenWidth, screenHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTextureId, 0);
        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)!=GLES20.GL_FRAMEBUFFER_COMPLETE){
            Log.e("lx", "fbo wrong");
        }else{
            Log.e("lx", "fbo success");
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        int[] textureIdsEOS=new int[1];
        GLES20.glGenTextures(1, textureIdsEOS,0);
        cameraTextureId=textureIdsEOS[0];

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureId);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        surfaceTexture=new SurfaceTexture(cameraTextureId);
        surfaceTexture.setOnFrameAvailableListener(this);

        if(onSurfaceCreateListener!=null){
            onSurfaceCreateListener.onSurfaceCreate(surfaceTexture, fboTextureId);
        }

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }
}