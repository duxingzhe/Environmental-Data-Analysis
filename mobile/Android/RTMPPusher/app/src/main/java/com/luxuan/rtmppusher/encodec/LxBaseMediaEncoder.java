package com.luxuan.rtmppusher.encodec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLContext;

public abstract class LxBaseMediaEncoder {

    private Surface surface;
    private EGLContext eglContext;

    private int width;
    private int height;

    private MediaCodec videoEncodec;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferInfo;

    private MediaCodec audioEncodec;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo audioBufferInfo;
    private long audioPts=0;
    private int sampleRate;

    private MediaMuxer mediaMuxer;
    private boolean encodecStart;
    private boolean audioExit;
    private boolean videoExit;

    private LxEGLMediaThread lxEGLMediaThread;
    private VideoEncodecThread videoEncodecThread;
    private AudioEncodecThread audioEncodecThread;

    private LXEGLSurfaceView.LxGLRender lxGLRender;

    public final static int RENDERMODE_WHEN_DIRTY=0;
    public final static int RENDERMODE_CONTINUOUSLY=1;
    private int mRenderMode=RENDERMODE_CONTINUOUSLY;

    private OnMediaInfoListener onMediaInfoListener;

    public LxBaseMediaEncoder(Context context){

    }

    public void setRender(LXEGLSurfaceView.LxGLRender lxGLREnder){
        this.lxGLRender=lxGLRender;
    }

    public void setRenderMode(int mRenderMode){
        if(lxGLRender==null){
            throw new RuntimeException("must set render before");
        }
        this.mRenderMode=mRenderMode;
    }

    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener){
        this.onMediaInfoListener=onMediaInfoListener;
    }

    public void initEncodec(EGLContext eglContext, String savePath, int width, int height, int sampleRate, int channelCount){
        this.width=width;
        this.height=height;
        this.eglContext=eglContext;
        initMediaEncode(savePath, width, height, sampleRate, channelCount);
    }

    public void startRecord(){
        if(surface!=null && eglContext != null){
            audioPts=0;
            audioExit=false;
            audioExit=false;
            encodecStart=false;

            lxEGLMediaThread=new LxEncodecMediaThread(new WeakReference<LxBaseMediaEncoder>(this));
            videoEncodecThread=new VideoEncodecThread(new WeakReference<LxBaseMediaEncoder>(this));
            audioEncodecThread=new VideoEncodecThread(new WeakReference<LxBaseMediaEncoder>(this));
            lxEGLMediaThread.isCreate=true;
            lxEGLMediaThread.isChange=true;
            videoEncodecThread.start();
            audioEncodecThread.start();
        }
    }

    public void stopRecord(){
        if(lxEGLMediaThread!=null&&videoEncodecThread!=null&&audioEncodecThread!=null){
            videoEncodecThread.exit();
            audioEncodecThread.exit();
            lxEGLMediaThread.onDestroy();
            videoEncodecThread=null;
            lxEGLMediaThread=null;
            audioEncodecThread=null;
        }
    }

    public interface OnMediaInfoListener{

        void onMediaTime(int times);
    }
}
