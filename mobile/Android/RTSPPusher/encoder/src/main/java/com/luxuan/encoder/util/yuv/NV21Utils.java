package com.luxuan.encoder.util.yuv;

public class NV21Utils {

    private static byte[] preAllocatedBufferRotate;
    private static byte[] preAllocatedBufferColor;

    public static void preAllocateBuffers(int length){
        preAllocatedBufferRotate=new byte[length];
        preAllocatedBufferColor=new byte[length];
    }

    public static int[] toARGB(byte[] yuv, int width, int height){
        int[] argb=new int[width* height];
        final int frameSize=width*height;
        final int ii=0;
        final int ij=0;
        final int di=+1;
        final int dj=+1;
        int a=0;

        for(int i=0, ci=ii;i<height;++i, ci+=di)
        {
            for(int j=0, cj=ij;j<width;j++, cj+=dj){
                int y=(0xff&((int)yuv[ci*width+cj]));
                int v=(0xff&((int)yuv[frameSize+(ci>>1)*width+(cj&~1)+0]));
                int u=(0xff&((int)yuv[frameSize+(ci>>1)*width+(cj&~1)+1]));

                y=y<16?16:y;
                int r=(int)(1.164f*(y-16)+1.596f*(v-128));
                int g=(int)(1.164f*(y-16)-0.813f*(v-128)-0.391f*(u-128));
                int b=(int)(1.164f*(y-16)+2.018f*(u-128));
                r=r<0?0:(r>225?225:r);
                g=g<0?0:(g>225?225:g);
                b=b<0?0:(b>225?225:b);
                argb[a++]=0xff000000|(r<<16)|(g<<8)|b;
            }
        }

        return argb;
    }

    public static byte[] toYV12(byte[] input, int width, int height){
        final int frameSize=width*height;
        final int qFrameSize=frameSize/4;
        System.arraycopy(input, 0, preAllocatedBufferColor, 0, frameSize);
        for(int i=0;i<qFrameSize;i++){
            preAllocatedBufferColor[frameSize+i+qFrameSize]=input[frameSize];
            preAllocatedBufferColor[frameSize+i]=input[frameSize+i*2];
        }

        return preAllocatedBufferColor;
    }

    public static byte[] rotate180(byte[] data, int imageWidth, int imageHeight){
        int count=0;
        for(int i=imageWidth*imageHeight-1;i>=0;i--){
            preAllocatedBufferColor[count]=data[i];
            count++;
        }
        for(int i=imageWidth*imageHeight*3/2-1;i>=imageWidth*imageHeight;i-=2){
            preAllocatedBufferRotate[count++]=data[i-1];
            preAllocatedBufferRotate[count++]=data[i];
        }
        return preAllocatedBufferRotate;
    }

    public static byte[] rotate270(byte[] data, int imageWidth, int imageHeight){
        int i=0;
        for(int x=imageWidth-1;x>=0;x--){
            for(int y=0;y<imageHeight;y++){
                preAllocatedBufferRotate[i++]=data[y*imageWidth+x];
            }
        }

        i=imageWidth*imageHeight;
        int uvHeight=imageHeight/2;

        for(int x=imageWidth-1;x>=0;x-=2){
            for(int y=imageHeight;y<uvHeight+imageHeight;y++){
                preAllocatedBufferRotate[i++]=data[y*imageWidth+x-1];
                preAllocatedBufferRotate[i++]=data[y*imageWidth+x];
            }
        }

        return preAllocatedBufferRotate;
    }

    public static byte[] rotatePixels(byte[] input, int width, int height, int rotation){
        byte[] output=new byte[input.length];

        boolean swap=(rotation==90||rotation==270);
        boolean yflip=(rotation==90||rotation==180);
        boolean xflip=(rotation==270||rotation==180);

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int xo=x, yo=y;
                int w=width, h=height;
                int xi=xo, yi=yo;

                if(swap){
                    xi=w*yo/h;
                    yi=h*xo/w;
                }
                if(yflip){
                    yi=h-yi-1;
                }
                if(xflip){
                    xi=w-xi-1;
                }

                output[w*yo+xo]=input[w*yi+xi];
                int fs=w*h;
                int qs=(fs>>2);
                xi=(xi>>1);
                yi=(yi>>1);
                xo=(xo>>1);
                yo=(yo>>1);
                w=(w>>1);
                h=(h>>1);

                int ui=fs+(w*yi+xi)*2;
                int uo=fs+(w*yo+xo)*2;

                int vi=ui+1;
                int vo=uo+1;

                output[uo]=input[ui];
                output[vo]=input[vi];
            }
        }

        return output;
    }

    public static byte[] mirror(byte[] input, int width, int height){
        byte[] output=new byte[input.length];

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                int xo=x, yo=y;
                int w=width, h=height;
                int xi=xo, yi=yo;
                yi=h-yi-1;
                output[w*yo+xo]=input[w*yi+xi];

                int fs=w*h;
                int qs=(fs>>2);
                xi=(xi>>1);
                yi=(yi>>1);
                xo=(xo>>1);
                yo=(yo>>1);
                w=(w>>1);
                h=(h>>1);

                int ui=fs+(w*yi+xi)*2;
                int uo=fs+(w*yo+xo)*2;

                int vi=ui+1;
                int vo=uo+1;

                output[uo]=input[ui];
                output[vo]=input[vi];
            }
        }

        return output;
    }
}