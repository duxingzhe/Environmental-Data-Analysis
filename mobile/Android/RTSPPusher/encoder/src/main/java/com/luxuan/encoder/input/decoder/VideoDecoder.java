package com.luxuan.encoder.input.decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

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
}
