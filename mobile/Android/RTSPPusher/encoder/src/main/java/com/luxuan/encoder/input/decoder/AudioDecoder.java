package com.luxuan.encoder.input.decoder;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.luxuan.encoder.input.audio.GetMicrophoneData;

import java.io.IOException;

public class AudioDecoder {

    private final String TAG="AudioDecoder";

    private AudioDecoderInterface audioDecoderInterface;
    private LoopFileInterface loopFileInterface;
    private MediaExtractor audioExtractor;
    private MediaCodec audioDecoder;
    private MediaCodec.BufferInfo audioInfo=new MediaCodec.BufferInfo();
    private boolean decoding;
    private Thread thread;
    private GetMicrophoneData getMicrophoneData;
    private MediaFormat audioFormat;
    private String mime="";
    private int sampleRate;
    private boolean isStereo;
    private int channels=2;
    private int size=4096;
    private byte[] pcmBuffer=new byte[size];
    private byte[] pcmBufferMued=new byte[11];
    private static boolean loopMode=false;
    private boolean muted=false;
    private long duration;
    private volatile long seekTime=0;
    private volatile long startMs=0;

    public AudioDecoder(AudioDecoderInterface audioDecoderInterface, LoopFileInterface loopFileInterface, GetMicrophoneData getMicrophoneData) {
        this.audioDecoderInterface = audioDecoderInterface;
        this.loopFileInterface = loopFileInterface;
        this.getMicrophoneData = getMicrophoneData;
    }

    public boolean initExtractor(String filePath) throws IOException {
        decoding=false;
        audioExtractor=new MediaExtractor();
        audioExtractor.setDataSource(filePath);
        for(int i=0;i<audioExtractor.getTrackCount()&&!mime.startsWith("audio/");i++){
            audioFormat=audioExtractor.getTrackFormat(i);
            mime=audioFormat.getString(MediaFormat.KEY_MIME);
            if(mime.startsWith("audio/")){
                audioExtractor.selectTrack(i);
            }else{
                audioFormat=null;
            }
        }
        if(audioFormat!=null){
            channels=audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            isStereo=channels>=2;
            sampleRate=audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            duration=audioFormat.getLong(MediaFormat.KEY_DURATION);
            if(channels>2){
                pcmBuffer=new byte[2048*channels];
            }
            return true;
        }else{
            mime="";
            audioFormat=null;
            return false;
        }
    }

    public boolean prepareAudio(){
        try{
            audioDecoder=MediaCodec.createDecoderByType(mime);
            audioDecoder.configure(audioFormat, null, null, 0);
            return true;
        }catch(IOException e){
            Log.e(TAG, "Prepare decoder error: ", e);
            return false;
        }
    }
}
