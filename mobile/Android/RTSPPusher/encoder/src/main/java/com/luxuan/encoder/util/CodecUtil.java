package com.luxuan.encoder.util;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodecUtil {

    private static final String TAG="CodecUtil";

    public static final String H264_MIME="video/avc";
    public static final String H265_MIME="video/hevc";
    public static final String AAC_MIME="audio/mp4a-latm";

    public enum Force {
        FIRST_COMPATIABLE_FOUND, SOFTWARE, HARDWARE
    }

    public static List<String> showAllCodecsInfo(){
        List<MediaCodecInfo> mediaCodecInfoList=getAllCodecs(false);
        List<String> infos=new ArrayList<>();
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfoList){
            String info="----------------\n";
            info+="Name: "+mediaCodecInfo.getName()+"\n";
            for(String type: mediaCodecInfo.getSupportedTypes()){
                info+="Type: "+type+"\n";
                MediaCodecInfo.CodecCapabilities codecCapabilities=mediaCodecInfo.getCapabilitiesForType(type);
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                    info+="Max instances: "+codecCapabilities.getMaxSupportedInstances()+"\n";
                }
                if(mediaCodecInfo.isEncoder()){
                    info+="----- Encoder info -----\n";
                    MediaCodecInfo.EncoderCapabilities encoderCapabilities=null;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        encoderCapabilities=codecCapabilities.getEncoderCapabilities();
                        info+="Complexity range: "+encoderCapabilities.getComplexityRange().getLower()
                                +" - "+encoderCapabilities.getComplexityRange().getUpper()+"\n";
                    }
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
                        info+="Quality range: "+encoderCapabilities.getQualityRange().getLower()
                                +" - "+encoderCapabilities.getQualityRange().getUpper()+"\n";
                    }

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        info+="CBR supported: "+encoderCapabilities.isBitrateModeSupported(MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CBR)+"\n";
                        info+="VBR supported: "+encoderCapabilities.isBitrateModeSupported(MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR)+"\n";
                        info+="CQ supported: "+encoderCapabilities.isBitrateModeSupported(MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ)+"\n";
                    }

                    info+="----- -----\n";
                }else{
                    info += "----- Decoder info -----\n";
                    info += "----- -----\n";
                }

                if(codecCapabilities.colorFormats!=null&&codecCapabilities.colorFormats.length>0){
                    info += "----- Video info -----\n";
                    info += "Supported colors: \n";
                    for(int color:codecCapabilities.colorFormats) {
                        info += color + "\n";
                    }
                    for(MediaCodecInfo.CodecProfileLevel profile : codecCapabilities.profileLevels){
                        info+="Profile: "+profile.profile+", level: "+ profile.level+"\n";
                    }

                    MediaCodecInfo.VideoCapabilities videoCapabilities=null;
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        videoCapabilities=codecCapabilities.getVideoCapabilities();
                        info+="Bitrate range: "+videoCapabilities.getBitrateRange().getLower()
                                +" - "+videoCapabilities.getBitrateRange().getUpper()+"\n";
                        info+="Frame rate range: "+videoCapabilities.getSupportedFrameRates().getLower()
                                +" - "+videoCapabilities.getSupportedFrameRates().getUpper()+"\n";
                        info+="Width range: "+videoCapabilities.getSupportedWidths().getLower()
                                +" - "+videoCapabilities.getSupportedWidths().getUpper()+"\n";
                        info+="Height range: "+videoCapabilities.getSupportedHeights().getLower()
                                +" - "+videoCapabilities.getSupportedHeights().getUpper()+"\n";
                    }
                    info+="----- -----\n";
                }else {
                    info += "----- Audio info -----\n";
                    for(MediaCodecInfo.CodecProfileLevel profile: codecCapabilities.profileLevels){
                        info+="Profile: "+profile.profile+", level: "+profile.level+"\n";
                    }
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        MediaCodecInfo.AudioCapabilities audioCapabilities=codecCapabilities.getAudioCapabilities();
                        info+="Bitrate range: "+audioCapabilities.getBitrateRange().getLower()
                                +" - "+audioCapabilities.getBitrateRange().getUpper()+"\n";
                        info+="Channels supported: "+audioCapabilities.getMaxInputChannelCount()+"\n";
                        try{
                            if(audioCapabilities.getSupportedSampleRateRanges()!=null&&
                                    audioCapabilities.getSupportedSampleRates().length>0){
                                info+="Supported sample rate: \n";
                                for(int sampleRate: audioCapabilities.getSupportedSampleRates()) {
                                    info += sampleRate + "\n";
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    info+="----- -----\n";
                }

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    info+="Max instances: "+codecCapabilities.getMaxSupportedInstances();
                }
            }

            info+="----------------\n";
            infos.add(info);
        }

        return infos;
    }

    public static List<MediaCodecInfo> getAllCodecs(boolean filterBroken){
        List<MediaCodecInfo> mediaCodecInfoList=new ArrayList<>();
        if(Build.VERSION.SDK_INT>=21){
            MediaCodecList mediaCodecList=new MediaCodecList(MediaCodecList.ALL_CODECS);
            MediaCodecInfo[] mediaCodecInfos=mediaCodecList.getCodecInfos();
            mediaCodecInfoList.addAll(Arrays.asList(mediaCodecInfos));
        }else{
            int count=MediaCodecList.getCodecCount();
            for(int i=0;i<count;i++){
                MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
                mediaCodecInfoList.add(mediaCodecInfo);
            }
        }

        return filterBroken? filterBrokenCodecs(mediaCodecInfoList):mediaCodecInfoList;
    }

    public static List<MediaCodecInfo> getAllHardwareEncoders(String mime){
        List<MediaCodecInfo> mediaCodecInfoList=getAllEncoders(mime);
        List<MediaCodecInfo> mediaCodecInfoHardware=new ArrayList<>();
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfoList){
            String name=mediaCodecInfo.getName().toLowerCase();
            if(!name.contains("omx.google")&&!name.contains("sw")){
                mediaCodecInfoHardware.add(mediaCodecInfo);
            }
        }

        return mediaCodecInfoHardware;
    }

    public static List<MediaCodecInfo> getAllHardwareDecoders(String mime){
        List<MediaCodecInfo> mediaCodecInfoList=getAllDecoders(mime);
        List<MediaCodecInfo> mediaCodecInfoHardware=new ArrayList<>();
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfoList){
            String name=mediaCodecInfo.getName().toLowerCase();
            if(!name.contains("omx.google")&&!name.contains("sw")){
                mediaCodecInfoHardware.add(mediaCodecInfo);
            }
        }

        return mediaCodecInfoHardware;
    }

    public static List<MediaCodecInfo> getAllSoftwareEncoders(String mime){
        List<MediaCodecInfo> mediaCodecInfoList=getAllEncoders(mime);
        List<MediaCodecInfo> mediaCodecInfoSoftware=new ArrayList<>();
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfoList){
            String name=mediaCodecInfo.getName().toLowerCase();
            if(name.contains("omx.google") || name.contains("sw")){
                mediaCodecInfoSoftware.add(mediaCodecInfo);
            }
        }

        return mediaCodecInfoSoftware;
    }

    public static List<MediaCodecInfo> getAllSoftwareDecoders(String mime){
        List<MediaCodecInfo> mediaCodecInfoList=getAllDecoders(mime);
        List<MediaCodecInfo> mediaCodecInfoSoftware=new ArrayList<>();
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfoList){
            String name=mediaCodecInfo.getName().toLowerCase();
            if(name.contains("omx.google") || name.contains("sw")){
                mediaCodecInfoSoftware.add(mediaCodecInfo);
            }
        }

        return mediaCodecInfoSoftware;
    }

    public static List<MediaCodecInfo> getAllEncoders(String mime){
        List<MediaCodecInfo> mediaCodecInfoList=new ArrayList<>();
        List<MediaCodecInfo> mediaCodecInfos=getAllCodecs(true);
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfos){
            if(!mediaCodecInfo.isEncoder()){
                continue;
            }

            String[] types=mediaCodecInfo.getSupportedTypes();
            for(String type: types){
                if(type.equalsIgnoreCase(mime)){
                    mediaCodecInfoList.add(mediaCodecInfo);
                }
            }
        }

        return mediaCodecInfoList;
    }

    public static List<MediaCodecInfo> getAllDecoders(String mime){
        List<MediaCodecInfo> mediaCodecInfoList=new ArrayList<>();
        List<MediaCodecInfo> mediaCodecInfos=getAllCodecs(true);
        for(MediaCodecInfo mediaCodecInfo: mediaCodecInfos){
            if(!mediaCodecInfo.isEncoder()){
                continue;
            }

            String[] types=mediaCodecInfo.getSupportedTypes();
            for(String type: types){
                if(type.equalsIgnoreCase(mime)){
                    mediaCodecInfoList.add(mediaCodecInfo);
                }
            }
        }

        return mediaCodecInfoList;
    }

    private static List<MediaCodecInfo> filterBrokenCodecs(List<MediaCodecInfo> codecs){
        List<MediaCodecInfo> listFilter=new ArrayList<>();
        for(MediaCodecInfo mediaCodecInfo : codecs){
            if(isValid(mediaCodecInfo.getName())){
                listFilter.add(mediaCodecInfo);
            }
        }

        return listFilter;
    }

    private static boolean isValid(String name){
        if((name.equals("OMX.qcom.video.encoder.avc")||name.equals("c2.qti.avc.encoder"))
                &&Build.MODEL.equals("Pixel 3a")){
            return false;
        }else{
            return true;
        }
    }
}
