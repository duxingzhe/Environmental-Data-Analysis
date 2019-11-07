package com.luxuan.rtmppusher.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

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
    private int vboid;
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
}
