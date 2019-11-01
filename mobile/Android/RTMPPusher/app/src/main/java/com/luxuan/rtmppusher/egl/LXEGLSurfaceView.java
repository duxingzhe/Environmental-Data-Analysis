package com.luxuan.rtmppusher.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGLContext;

public abstract class LXEGLSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private Surface surface;
    private EGLContext eglContext;

    private LxEGLThread lxEGLThread;
    private LxGLRender lxGLRender;

    public final static int RENDERMODE_WHEN_DIRTY=0;
    public final static int RENDERMODE_CONTINUOUSLY=1;

    private int mRenderMode=RENDERMODE_CONTINUOUSLY;

    public LXEGLSurfaceView(Context context) {
        this(context, null);
    }

    public LXEGLSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LXEGLSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);
    }

    public void setRender(LxRender lxGLRender){
        this.lxGLRender=lxGLRender;
    }

    public void setRenderMode(int mRenderMode){
        if(wlGLRender==null){
            throw new RuntimeException("must set render before");
        }

        this.mRenderMode=mRenderMode;
    }

    public void setSurfaceAndEglContext(Surface surface, EGLContext eglContext){
        this.surface=surface;
        this.eglContext=eglContext;
    }
}
