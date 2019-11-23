package com.luxuan.encoder.util.yuv;

import com.luxuan.encoder.video.FormatVideoEncoder;

/**
 *
 * Example YUV images 4x4 px.
 *
 * NV21 example:
 *
 * Y1   Y2   Y3   Y4
 * Y5   Y6   Y7   Y8
 * Y9   Y10  Y11  Y12
 * Y13  Y14  Y15  Y16
 * U1   V1   U2   V2
 * U3   V3   U4   V4
 *
 *
 * YV12 example:
 *
 * Y1   Y2   Y3   Y4
 * Y5   Y6   Y7   Y8
 * Y9   Y10  Y11  Y12
 * Y13  Y14  Y15  Y16
 * U1   U2   U3   U4
 * V1   V2   V3   V4
 *
 *
 * YUV420 planar example (I420):
 *
 * Y1   Y2   Y3   Y4
 * Y5   Y6   Y7   Y8
 * Y9   Y10  Y11  Y12
 * Y13  Y14  Y15  Y16
 * V1   V2   V3   V4
 * U1   U2   U3   U4
 *
 *
 * YUV420 semi planar example (NV12):
 *
 * Y1   Y2   Y3   Y4
 * Y5   Y6   Y7   Y8
 * Y9   Y10  Y11  Y12
 * Y13  Y14  Y15  Y16
 * V1   U1   V2   U2
 * V3   U3   V4   U4
 */
public class YUVUtil {

    public static void preAllocateBuffers(int length){
        NV21Utils.preAllocateBuffers(length);
        YV12Utils.preAllocateBuffers(length);
    }

    public static byte[] NV21toYUV420byColor(byte[] input, int width, int height, FormatVideoEncoder formatVideoEncoder){
        switch(formatVideoEncoder){
            case YUV420PLANAR:
                return NV21Utils.toI420(input, width, height);
            case YUV420SEMIPLANAR:
                return NV21Utils.toNV12(input, width, height);
            default:
                return null;
        }
    }

    public static byte[] rotateNV21(byte[] data, int width, int height, int rotation){
        switch(rotation){
            case 0:
                return data;
            case 90:
                return NV21Utils.rotate90(data, width, height);
            case 180:
                return NV21Utils.rotate180(data, width, height);
            case 270:
                return NV21Utils.rotate270(data, width, height);
            default:
                return null;
        }
    }

    public static byte[] YV12toYUV420byColor(byte[] input, int width, int height, FormatVideoEncoder formatVideoEncoder){
        switch(formatVideoEncoder){
            case YUV420PLANAR:
                return YV12Utils.toI420(input, width, height);
            case YUV420SEMIPLANAR:
                return YV12Utils.toNV12(input, width, height);
            default:
                return null;
        }
    }

    public static byte[] rotateYV12(byte[] data, int width, int height, int rotation){
        switch(rotation){
            case 0:
                return data;
            case 90:
                return YV12Utils.rotate90(data, width, height);
            case 180:
                return YV12Utils.rotate180(data, width, height);
            case 270:
                return YV12Utils.rotate270(data, width, height);
            default:
                return null;
        }
    }
}
