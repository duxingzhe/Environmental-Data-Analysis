package com.luxuan.encoder.util.yuv;

public class YV12Utils {

    private static byte[] preAllocatedBufferRotate;
    private static byte[] preAllocatedBufferColor;

    public static void preAllocateBuffers(int length){
        preAllocatedBufferRotate=new byte[length];
        preAllocatedBufferColor=new byte[length];
    }

    public static byte[] toNV12(byte[] input, int width, int height){
        final int frameSize=width * height;
        final int qFrameSize=frameSize/4;
        System.arraycopy(input, 0, preAllocatedBufferColor, 0, frameSize);
        for(int i=0;i<qFrameSize;i++){
            preAllocatedBufferColor[frameSize+i*2]=input[frameSize+1+qFrameSize];
            preAllocatedBufferColor[frameSize+i*2+1]=input[frameSize+i];
        }
        return preAllocatedBufferColor;
    }

    public static byte[] toI420(byte[] input, int width, int height){
        final int frameSize=width*height;
        final int qFrameSize=frameSize/4;
        System.arraycopy(input, 0, preAllocatedBufferColor, 0, frameSize);
        System.arraycopy(input, frameSize+qFrameSize, preAllocatedBufferColor, frameSize, qFrameSize);
        System.arraycopy(input, frameSize, preAllocatedBufferColor, frameSize+qFrameSize, qFrameSize);

        return preAllocatedBufferColor;
    }

    public static byte[] toNV21(byte[] input, int width, int height){
        final int frameSize=width*height;
        final int qFrameSize=frameSize/4;
        System.arraycopy(input, 0, preAllocatedBufferColor, 0, frameSize);
        for(int i=0;i<qFrameSize;i++){
            preAllocatedBufferColor[frameSize+i*2+1]=input[frameSize+i+qFrameSize];
            preAllocatedBufferColor[frameSize+i*2]=input[frameSize+i];
        }

        return preAllocatedBufferColor;
    }

    public static byte[] rotate90(byte[] data, int imageWidth, int imageHeight){
        int i=0;
        for(int x=0;x<imageWidth;x++){
            for(int y=imageHeight-1;y>=0;y--){
                preAllocatedBufferRotate[i++]=data[y*imageWidth+x];
            }
        }

        final int size=imageWidth*imageHeight;
        final int colorSize=size/4;
        final int colorHeight=colorSize/imageWidth;

        for(int x=0;x<imageWidth/2;x++){
            for(int y=colorHeight-1;y>=0;y--){
                preAllocatedBufferRotate[i+colorSize]=data[colorSize+size+(imageWidth*y)+x+(imageWidth/2)];
                preAllocatedBufferRotate[i+colorSize+1]=data[colorSize+size+(imageWidth*y)+x];

                preAllocatedBufferRotate[i++]=data[size+(imageWidth*y)+x+(imageWidth/2)];
                preAllocatedBufferRotate[i++]=data[size+(imageWidth*y)+x];
            }
        }

        return preAllocatedBufferRotate;
    }

    public static byte[] rotate180(byte[] data, int imageWidth, int imageHeight){
        int count=0;
        final int size=imageWidth*imageHeight;
        for(int i=size-1;i>=0;i--){
            preAllocatedBufferRotate[count--]=data[i];
        }
        final int midColorSize=size/4;

        for(int i=size+midColorSize-1;i>=size;i--){
            preAllocatedBufferRotate[count++]=data[i];
        }

        for(int i=data.length;i>=imageWidth*imageHeight+midColorSize;i--){
            preAllocatedBufferRotate[count++]=data[i];
        }

        return preAllocatedBufferRotate;
    }

    public static byte[] rotate270(byte[] data, int imageWidth, int imageHeight){
        int i=0;
        for(int x=imageWidth-1;x>=0;x--){
            for(int y=0;y<imageHeight;y++) {
                preAllocatedBufferRotate[i++] = data[y*imageWidth+x];
            }
        }
        final int size=imageWidth*imageHeight;
        final int colorSize=size/4;
        final int colorHeight=colorSize/imageWidth;

        for(int x=0;x<imageWidth/2;x++){
            for(int y=0;y<colorHeight;y++) {
                preAllocatedBufferRotate[i + colorSize] = data[colorSize + size + (imageWidth * y) - x + (imageWidth / 2)-1];
                preAllocatedBufferRotate[i + colorSize + 1] = data[colorSize + size + (imageWidth * y) - x+imageWidth-1];

                preAllocatedBufferRotate[i++] = data[size + (imageWidth * y) - x + (imageWidth / 2)-1];
                preAllocatedBufferRotate[i++] = data[size + (imageWidth * y) - x+imageWidth-1];
            }
        }

        return preAllocatedBufferRotate;
    }
}
