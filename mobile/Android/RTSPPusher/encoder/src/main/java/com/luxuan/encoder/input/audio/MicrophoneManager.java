package com.luxuan.encoder.input.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.luxuan.encoder.Frame;

import java.nio.ByteBuffer;

public class MicrophoneManager {

    private final String TAG="MicrophoneManager";
    private static final int BUFFER_SIZE=4096;
    private AudioRecord audioRecord;
    private GetMicrophoneData getMicrophoneData;
    private ByteBuffer pcmBuffer=ByteBuffer.allocateDirect(BUFFER_SIZE);
    private byte[] pcmBufferMuted=new byte[BUFFER_SIZE];
    private boolean running=false;
    private boolean created=false;

    private int sampleRate=32000;
    private int audioFormat= AudioFormat.ENCODING_PCM_16BIT;
    private int channel=AudioFormat.CHANNEL_IN_STEREO;
    private boolean muted=false;
    private AudioPostProcessEffect audioPostProcessEffect;
    private HandlerThread handlerThread;

    public MicrophoneManager(GetMicrophoneData getMicrophoneData){
        this.getMicrophoneData=getMicrophoneData;
    }

    public void createMicrophone(){
        createMicrophone(sampleRate, true, false, false);
        Log.i(TAG, "Microphoe created, "+sampleRate+"hz, Stereo");
    }

    public void createMicrophone(int sampleRate, boolean isStereo, boolean echoCanceler, boolean noiseSuppressor){
        this.sampleRate=sampleRate;
        if(!isStereo){
            channel=AudioFormat.CHANNEL_IN_MONO;
        }
        audioRecord=new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channel, audioFormat,
                getPcmBufferSize());
        audioPostProcessEffect=new AudioPostProcessEffect(audioRecord.getAudioSessionId());
        if(echoCanceler){
            audioPostProcessEffect.enableEchoCanceler();
        }
        if(noiseSuppressor){
            audioPostProcessEffect.enableNoiseSuppressor();
        }
        String chl=(isStereo)?"Stereo":"Mono";
        Log.i(TAG, "Microphoe created, "+sampleRate+"hz, "+chl);
        created=true;
    }

    public synchronized void start(){
        init();
        handlerThread=new HandlerThread(TAG);
        handlerThread.start();
        Handler handler=new Handler(handlerThread.getLooper());
        handler.post(new Runnable(){

            @Override
            public void run(){
                Frame frame=read();
                if(frame!=null){
                    getMicrophoneData.inputPCMData(frame);
                }else{
                    running=false;
                }
            }
        });
    }

    private void init(){
        if(audioRecord!=null){
            audioRecord.startRecording();
            running=true;
            Log.i(TAG, "Microphone started");
        }else{
            Log.e(TAG, "Starting microphone failed, microphone was stopped or not created, "+
                    "use createMicrophone() before start()");
        }
    }

    public void mute(){
        muted=true;
    }

    public void unmute(){
        muted=false;
    }

    public boolean isMuted(){
        return muted;
    }

    private Frame read(){
        pcmBuffer.rewind();
        int size=audioRecord.read(pcmBuffer, pcmBuffer.remaining());
        if(size<=0){
            return null;
        }
        return new Frame(muted?pcmBufferMuted:pcmBuffer.array(), muted?0:pcmBuffer.arrayOffset(), size);
    }

    public synchronized void stop(){
        running=false;
        created=false;
        handlerThread.quitSafely();
        if(audioRecord!=null){
            audioRecord.setRecordPositionUpdateListener(null);
            audioRecord.stop();
            audioRecord.release();
            audioRecord=null;
        }
        if(audioPostProcessEffect!=null){
            audioPostProcessEffect.releaseEchoCanceler();
            audioPostProcessEffect.releaseNoiseSuppressor();
        }
        Log.i(TAG, "Microphone stopped");
    }

    private int getPcmBufferSize(){
        int pcmBufferSize=AudioRecord.getMinBufferSize(sampleRate, channel, AudioFormat.ENCODING_PCM_16BIT);
        return pcmBufferSize*5;
    }

    public int getMaxInputSize() {
        return BUFFER_SIZE;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public int getChannel() {
        return channel;
    }

    public boolean isRunning(){
        return running;
    }

    public boolean isCreated(){
        return created;
    }
}
