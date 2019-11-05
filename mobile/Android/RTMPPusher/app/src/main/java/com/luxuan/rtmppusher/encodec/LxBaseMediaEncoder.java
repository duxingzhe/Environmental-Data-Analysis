package com.luxuan.rtmppusher.encodec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

import java.io.IOException;
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
        initMediaEncodec(savePath, width, height, sampleRate, channelCount);
    }

    public void startRecord(){
        if(surface!=null && eglContext != null){
            audioPts=0;
            audioExit=false;
            audioExit=false;
            encodecStart=false;

            lxEGLMediaThread=new LxEGLMediaThread(new WeakReference<LxBaseMediaEncoder>(this));
            videoEncodecThread=new VideoEncodecThread(new WeakReference<LxBaseMediaEncoder>(this));
            audioEncodecThread=new AudioEncodecThread(new WeakReference<LxBaseMediaEncoder>(this));
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

    private void initMediaEncodec(String savePath, int width, int height, int sampleRate, int channelCount){
        try {
            mediaMuxer = new MediaMuxer(savePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            initVideoEncodec(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
            initAudioEncodec(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelCount);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void initVideoEncodec(String mimeType, int width, int height){
        try{
            videoBufferInfo=new MediaCodec.BufferInfo();
            videoFormat=MediaFormat.createVideoFormat(mimeType, width, height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width*height*4);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            videoEncodec=MediaCodec.createEncoderByType(mimeType);
            videoEncodec.configure(videoFormat, null,null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            surface=videoEncodec.createInputSurface();
        }catch(IOException e){
            e.printStackTrace();
            videoEncodec=null;
            videoFormat=null;
            videoBufferInfo=null;
        }
    }

    private void initAudioEncodec(String mimeType, int sampleRate, int channelCount){
        try{
            this.sampleRate=sampleRate;
            audioBufferInfo=new MediaCodec.BufferInfo();
            audioFormat= MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);

            audioEncodec=MediaCodec.createEncoderByType(mimeType);
            audioEncodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        }catch(IOException e){
            e.printStackTrace();
            audioBufferInfo=null;
            audioFormat=null;
            audioEncodec=null;
        }
    }

    public static class LxEGLMediaThread extends Thread{

        private WeakReference<LxBaseMediaEncoder> encoder;
        private EglHelper eglHelper;
        private Object object;

        private boolean isExit=false;
        private boolean isCreate=false;
        private boolean isChange=false;
        private boolean isStart=false;

        public LxEGLMediaThread(WeakReference<LxBaseMediaEncoder> encoder){
            this.encoder=encoder;
        }

        @Override
        public void run(){
            super.run();
            isExit=false;
            isStart=false;
            object=new Object();
            eglHelper=new EglHelper();
            eglHelper.initEgl(encoder.get().surface, encoder.get().eglContext);

            while(true){
                if(isExit){
                    release();
                    break;
                }

                if(isStart){
                    if(encoder.get().mRenderMode==RENDERMODE_WHEN_DIRTY){
                        synchronized(object){
                            try{
                                object.wait();
                            }catch(InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    }else if(encoder.get().mRenderMode==RENDERMODE_CONTINUOUSLY){
                        try{
                            Thread.sleep(1000/60);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                    }else{
                        throw new RuntimeException("mRenderMode has wrong value.");
                    }
                }

                onCreate();
                onChange(encoder.get().width, encoder.get().height);
                onDraw();
                isStart=true;
            }
        }

        private void onCreate(){

            if(isCreate&&encoder.get().lxGLRender!=null){
                isCreate=false;
                encoder.get().lxGLRender.onSurfaceCreated();
            }
        }

        private void onChange(int width, int height){

            if(isChange&&encoder.get().lxGLRender!=null){
                isChange=false;
                encoder.get().lxGLRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw(){

            if(encoder.get().lxGLRender!=null&&eglHelper!=null){
                encoder.get().lxGLRender.onDrawFrame();

                if(!isStart){
                    encoder.get().lxGLRender.onDrawFrame();
                }

                eglHelper.swapBuffers();
            }
        }

        private void requestRender(){
            if(object!=null){
                synchronized(object){
                    object.notifyAll();
                }
            }
        }

        private void onDestroy(){
            isExit=true;
            requestRender();
        }

        public void release(){
            if(eglHelper!=null){
                eglHelper.destroyEgl();
                eglHelper=null;
                object=null;
                encoder=null;
            }
        }
    }

    public interface OnMediaInfoListener{

        void onMediaTime(int times);
    }
}
