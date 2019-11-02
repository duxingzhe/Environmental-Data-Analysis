package com.luxuan.rtmppusher.egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

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

    public EGLContext getEglContext(){
        if(lxEGLThread!=null){
            return lxEGLThread.getEglContext();
        }

        return null;
    }

    public void requestRender(){
        if(lxEGLThread!=null){
            lxEGLThread.requestRender();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        if(surface==null){
            surface=holder.getSurface();
        }

        lxEGLThread=new LxEGLThread(new WeakReference(LXEGLSurfaceView)(this));
        lxEGLThread.isCreated=true;
        lxEGLThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        lxEGLThread.width=width;
        lxEGLThread.height=height;
        lxEGLThread.isChanged=true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        lxEGLThread.onDestroy();
        lxEGLThread=null;
        surface=null;
        eglContext=null;
    }

    public interface LxGLRender{
        void onSurfaceCreated();
        void onSurfaceChanged(int width, int height);
        void onDrawFrame();
    }

    public static class LxEGLThread extends Thread{

        private WeakReference<LXEGLSurfaceView> lxEglSurfaceViewWeakReference;
        private EglHelper eglHelper=null;
        private Object object=null;

        private boolean isExit=false;
        private boolean isCreate=false;
        private boolean isChange=false;
        private boolean isStart=false;

        private int width;
        private int height;

        public LxEGLThread(WeakReference<LXEGLSurfaceView> lxEglSurfaceViewWeakReference){
            this.lxEglSurfaceViewWeakReference=lxEglSurfaceViewWeakReference;
        }

        @Override
        public void run(){
            super.run();

            isExit=false;
            isStart=false;
            object=new Object();
            eglHelper=new EglHelper();
            eglHelper.initEgl(lxEglSurfaceViewWeakReference.get().surface, lxEglSurfaceViewWeakReference.get().eglContext);

            while(true){
                if(isExit){
                    release();
                    break;
                }

                if(isStart){
                    if(lxEglSurfaceViewWeakReference.get().mRenderMode==RENDERMODE_WHEN_DIRTY){
                        synchronized(object){
                            try{
                                object.wait();
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }else if(lxEglSurfaceViewWeakReference.get().mRenderMode==RENDERMODE_CONTINUOUSLY){
                    try{
                        Thread.sleep(1000/50);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }else {
                    throw new RuntimeException("mRenderMode is wrong value");
                }
            }

            onCreate();
            onChange(width, heiht);
            onDraw();

            isStart=true;
        }
    }
}
