package com.luxuan.rtmppusher.egl;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LxShaderUtil {

    public static String getRawResource(Context context, int rawId){
        InputStream inputStream=context.getResources().openRawResource(rawId);
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb=new StringBuffer();
        String line;
        try{
            while((line=reader.readLine())!=null){
                sb.append(line).append("\n");
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return sb.toString();
    }

    private static int loadShader(int shaderType, String source){
        int shader= GLES20.glCreateShader(shaderType);
        if(shader!=0){
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);

            int[] compile=new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile, 0);
            if(compile[0]!=GLES20.GL_TRUE){
                GLES20.glDeleteShader(shader);
                shader=0;
            }
            return shader;
        }else{
            return 0;
        }
    }
}
