package com.luxuan.encoder.input.gl;

import android.graphics.PointF;

import com.luxuan.encoder.util.gl.TranslateTo;

public class Sprite {

    private final float[] squareVertexDataSprite={
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f,
    };

    private PointF scale;
    private PointF position;

    public Sprite(){
        reset();
    }

    public void translate(float deltaX, float deltaY){
        position.x=deltaX;
        position.y=deltaY;
    }

    public void translate(TranslateTo translation){
        switch(translation){
            case CENTER:
                this.position.x=50f-scale.x/2f;
                this.position.y=50f-scale.y/2f;
                break;
            case BOTTOM:
                this.position.x=50f-scale.x/2f;
                this.position.y=100f-scale.y;
                break;
            case TOP:
                this.position.x=50-scale.x/2f;
                this.position.y=0f;
                break;
            case LEFT:
                this.position.x=0f;
                this.position.y=50-scale.y/2f;
                break;
            case RIGHT:
                this.position.x=100-scale.x;
                this.position.y=50f-scale.y/2f;
                break;
            case TOP_LEFT:
                this.position.x=0f;
                this.position.y=0f;
                break;
            case TOP_RIGHT:
                this.position.x=100f-scale.x;
                this.position.y=0f;
                break;
            case BOTTOM_LEFT:
                this.position.x=0f;
                this.position.y=100f-scale.y;
                break;
            case BOTTOM_RIGHT:
                this.position.x=100f-scale.x;
                this.position.y=100f-scale.y;
                break;
            default:
                break;
        }
    }

    public void scale(float deltaX, float deltaY){
        position.x/=deltaX/scale.x;
        position.y/=deltaY/scale.y;

        scale=new PointF(deltaX, deltaY);
    }

    public PointF getScale(){
        return scale;
    }

    public PointF getTranslation(){
        return position;
    }

    public void reset(){
        scale=new PointF(100f, 100f);
        position=new PointF(0f, 0f);
    }

    public float[] getTransformedVertices(){
        PointF bottomRight=new PointF(squareVertexDataSprite[0], squareVertexDataSprite[1]);
        PointF bottomLeft=new PointF(squareVertexDataSprite[2], squareVertexDataSprite[3]);
        PointF topRight=new PointF(squareVertexDataSprite[4], squareVertexDataSprite[5]);
        PointF topLeft=new PointF(squareVertexDataSprite[6], squareVertexDataSprite[7]);

        float scaleX=scale.x/100f;
        float scaleY=scale.y/100f;

        bottomRight.x/=scaleX;
        bottomRight.y/=scaleY;

        bottomLeft.x/=scaleX;
        bottomLeft.y/=scaleY;

        topRight.x/=scaleX;
        topRight.y/=scaleY;

        topLeft.x/=scaleX;
        topLeft.y/=scaleY;

        float positionX=-position.x/scale.x;
        float positionY=-position.y/scale.y;

        bottomRight.x+=positionX;
        bottomRight.y+=positionY;

        bottomLeft.x+=positionX;
        bottomLeft.y+=positionY;

        topRight.x+=positionX;
        topRight.y+=positionY;

        topLeft.x+=positionX;
        topLeft.y+=positionY;

        return new float[]{
                bottomRight.x, bottomRight.y, bottomLeft.x, bottomLeft.y, topRight.x, topRight.y, topLeft.x,
                topLeft.x, topRight.y
        };
    }
}
