package com.luxuan.rtmppusher.egl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    public static int createProgram(String vertexSource, String fragmentSource){
        int vertexShader=loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragmentShader=loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        if(vertexShader!=0&&fragmentShader!=0){
            int program=GLES20.glCreateProgram();

            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);

            GLES20.glLinkProgram(program);
            return program;
        }

        return 0;
    }

    public static Bitmap createTextImage(String text, int textSize, String textColor, String bgColor, int padding){
        Paint paint=new Paint();
        paint.setColor(Color.parseColor(textColor));
        paint.setTextSize(textSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        float width=paint.measureText(text, 0, text.length());

        float top=paint.getFontMetrics().top;
        float bottom=paint.getFontMetrics().bottom;

        Bitmap bitmap=Bitmap.createBitmap((int)(width+padding*2), (int)((bottom-top)+padding*2), Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);

        canvas.drawColor(Color.parseColor(bgColor));
        canvas.drawText(text, padding, -top+padding, paint);

        return bitmap;
    }

}
