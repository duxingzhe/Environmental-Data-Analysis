package com.luxuan.encoder.input.decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;

public class VideoDecoder {

    private final String TAG="VideoDecoder";

    private VideoDecoderInterface videoDecoderInterface;
    private LoopFileInterface loopFileInterface;
    private MediaExtractor videoExtractor;
    private MediaCodec videoDecoder;
    private MediaCodec.BufferInfo videoInfonew = new MediaCodec.BufferInfo();
    private boolean decoding;
    private Thread thread;
    private MediaFormat videoFormat;
    private String mime="";
    private int width;
    private int height;
    private long duration;
    private static boolean loopMode=false;
    private volatile long seekTime=0;
    private volatile long startMs=0;

    public VideoDecoder(VideoDecoderInterface videoDecoderInterface, LoopFileInterface loopFileInterface){
        this.videoDecoderInterface=videoDecoderInterface;
        this.loopFileInterface=loopFileInterface;
    }

    public boolean initExtractor(String filePath) throws IOException {
        decoding=false;
        videoExtractor=new MediaExtractor();
        videoExtractor.setDataSource(filePath);
        for(int i=0;i<videoExtractor.getTrackCount()&&!mime.startsWith("video/");i++){
            videoFormat=videoExtractor.getTrackFormat(i);
            mime=videoFormat.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith("video/")){
                videoExtractor.selectTrack(i);
            }else{
                videoFormat=null;
            }
        }
        if(videoFormat!=null){
            width=videoFormat.getInteger(MediaFormat.KEY_WIDTH);
            height=videoFormat.getInteger(MediaFormat.KEY_HEIGHT);
            duration=videoFormat.getLong(MediaFormat.KEY_DURATION);
            return true;
        }else{
            mime="";
            videoFormat=null;
            return false;
        }
    }

    public boolean prepareVideo(Surface surface){
        try{
            videoDecoder=MediaCodec.createDecoderByType(mime);
            videoDecoder.configure(videoFormat, surface, null, 0);
            return true;
        }catch(IOException e){
            Log.e(TAG, "Prepare decoder error:", e);
            return false;
        }
    }

    public void start(){
        decoding=true;
        videoDecoder.start();
        thread=new Thread(new Runnable(){

            @Override
            public void run(){
                decodeVideo();
            }
        });
        thread.start();
    }

    public void stop(){
        decoding=false;
        seekTime=0;
        if(thread!=null){
            thread.interrupt();
            try{
                thread.join(100);
            }catch(InterruptedException e){
                thread.interrupt();
            }
            thread=null;
        }

        if(videoDecoder!=null){
            videoDecoder.stop();
            videoDecoder.release();
            videoDecoder=null;
        }

        if(videoExtractor!=null){
            videoExtractor.release();
            videoExtractor=null;
        }
    }
}
