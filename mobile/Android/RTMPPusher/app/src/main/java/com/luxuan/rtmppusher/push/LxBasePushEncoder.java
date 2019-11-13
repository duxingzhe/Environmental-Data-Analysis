package com.luxuan.rtmppusher.push;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import com.luxuan.rtmppusher.egl.EglHelper;
import com.luxuan.rtmppusher.egl.LXEGLSurfaceView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;

public abstract class LxBasePushEncoder {

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

    public LxBasePushEncoder(Context context){

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

            lxEGLMediaThread=new LxEGLMediaThread(new WeakReference<LxBasePushEncoder>(this));
            videoEncodecThread=new VideoEncodecThread(new WeakReference<LxBasePushEncoder>(this));
            audioEncodecThread=new AudioEncodecThread(new WeakReference<LxBasePushEncoder>(this));
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

    public void putPCMData(byte[] buffer, int size){
        if(audioEncodecThread!=null && !audioEncodecThread.isExit&&buffer!=null &&size>0){
            int inputBufferIndex=audioEncodec.dequeueInputBuffer(0);
            if(inputBufferIndex>=0){
                ByteBuffer byteBuffer=audioEncodec.getInputBuffers()[inputBufferIndex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts=getAudioPts(size, sampleRate);
                audioEncodec.queueInputBuffer(inputBufferIndex, 0, size, pts, 0);
            }
        }
    }

    public static class LxEGLMediaThread extends Thread{

        private WeakReference<LxBasePushEncoder> encoder;
        private EglHelper eglHelper;
        private Object object;

        private boolean isExit=false;
        private boolean isCreate=false;
        private boolean isChange=false;
        private boolean isStart=false;

        public LxEGLMediaThread(WeakReference<LxBasePushEncoder> encoder){
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

    public static class VideoEncodecThread extends Thread{

        private WeakReference<LxBasePushEncoder> encoder;

        private boolean isExit;

        private MediaCodec videoEncodec;
        private MediaCodec.BufferInfo videoBufferInfo;
        private MediaMuxer mediaMuxer;

        private int videoTrackIndex=-1;
        private long pts;

        public VideoEncodecThread(WeakReference<LxBasePushEncoder> encoder){
            this.encoder=encoder;
            videoEncodec=encoder.get().videoEncodec;
            videoBufferInfo=encoder.get().videoBufferInfo;
            mediaMuxer=encoder.get().mediaMuxer;
            videoTrackIndex=-1;
        }

        @Override
        public void run(){
            super.run();
            pts=0;
            videoTrackIndex=-1;
            isExit=false;
            videoEncodec.start();

            while(true) {
                if (isExit) {
                    videoEncodec.stop();
                    videoEncodec.release();
                    videoEncodec = null;
                    encoder.get().videoExit = true;

                    if (encoder.get().audioExit) {
                        mediaMuxer.stop();
                        mediaMuxer.release();
                        mediaMuxer = null;
                    }
                    Log.d("Lx", "录制完成");
                    break;
                }
            }

            int outputBufferIndex=videoEncodec.dequeueOutputBuffer(videoBufferInfo,0);

            if(outputBufferIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                videoTrackIndex=mediaMuxer.addTrack(videoEncodec.getOutputFormat());
                if(encoder.get().audioEncodecThread.audioTrackIndex!=-1){
                    mediaMuxer.start();
                    encoder.get().encodecStart=true;
                }
            }else{
                while(outputBufferIndex>=0){
                    if(encoder.get().encodecStart){
                        ByteBuffer outputBuffer=videoEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(videoBufferInfo.offset);
                        outputBuffer.limit(videoBufferInfo.offset+videoBufferInfo.size);

                        if(pts==0){
                            pts=videoBufferInfo.presentationTimeUs;
                        }

                        videoBufferInfo.presentationTimeUs=videoBufferInfo.presentationTimeUs-pts;
                        mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, videoBufferInfo);
                        if(encoder.get().onMediaInfoListener!=null){
                            encoder.get().onMediaInfoListener.onMediaTime((int)(videoBufferInfo.presentationTimeUs/1000000));
                        }
                    }
                    videoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                    outputBufferIndex=videoEncodec.dequeueOutputBuffer(videoBufferInfo, 0);
                }
            }
        }

        public void exit(){
            isExit=true;
        }
    }

    public static class AudioEncodecThread extends Thread{
        private WeakReference<LxBasePushEncoder> encoder;
        private boolean isExit;

        private MediaCodec audioEncodec;
        private MediaCodec.BufferInfo bufferInfo;
        private MediaMuxer mediaMuxer;

        private int audioTrackIndex=-1;
        private long pts;

        public AudioEncodecThread(WeakReference<LxBasePushEncoder> encoder){
            this.encoder=encoder;
            audioEncodec=encoder.get().audioEncodec;
            bufferInfo=encoder.get().audioBufferInfo;
            mediaMuxer=encoder.get().mediaMuxer;
            audioTrackIndex=-1;
        }

        @Override
        public void run(){
            super.run();
            pts=0;
            isExit=false;
            audioEncodec.start();
            while(true){
                if(isExit){
                    audioEncodec.stop();
                    audioEncodec.release();
                    audioEncodec=null;

                    encoder.get().audioExit=true;
                    if(encoder.get().videoExit){
                        mediaMuxer.stop();
                        mediaMuxer.release();
                        mediaMuxer=null;
                    }
                    break;
                }

                int outputBufferIndex=audioEncodec.dequeueOutputBuffer(bufferInfo, 0);
                if(outputBufferIndex==MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                    if(mediaMuxer!=null){
                        audioTrackIndex=mediaMuxer.addTrack(audioEncodec.getOutputFormat());
                        if(encoder.get().videoEncodecThread.videoTrackIndex!=-1){
                            mediaMuxer.start();
                            encoder.get().encodecStart=true;
                        }
                    }
                }else{
                    while(outputBufferIndex>=0){
                        if(encoder.get().encodecStart){
                            ByteBuffer outputBuffer=audioEncodec.getOutputBuffers()[outputBufferIndex];
                            outputBuffer.position(bufferInfo.offset);
                            outputBuffer.limit(bufferInfo.offset+bufferInfo.size);
                            if(pts==0){
                                pts=bufferInfo.presentationTimeUs;
                            }

                            bufferInfo.presentationTimeUs=bufferInfo.presentationTimeUs-pts;
                            mediaMuxer.writeSampleData(audioTrackIndex, outputBuffer, bufferInfo);
                        }

                        audioEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex=audioEncodec.dequeueOutputBuffer(bufferInfo, 0);
                    }
                }
            }
        }

        public void exit(){
            isExit=true;
        }
    }

    public interface OnMediaInfoListener{

        void onMediaTime(int times);
    }

    private long getAudioPts(int size, int sampleRate){
        audioPts+=(long)(1.0*size/(sampleRate*2*2)*1000000.0);
        return audioPts;
    }
}
