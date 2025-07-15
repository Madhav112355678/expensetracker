package com.example.trackmymoney;

import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class WaveView extends View {
    private Paint wavePaint;
    private Path wavePath;
    private float waveHeight=10;
    private float waveLength;
    private float waveShift=0;
    private float percentage=0;

    private int waveColor = Color.argb(128, 255, 0, 0); // 50% opacity

     //waveView constructor
    public WaveView(Context context, AttributeSet attrs){
       super(context,attrs);
       init();
   }
private void init(){
       wavePaint = new Paint();
       wavePaint.setColor(waveColor);

       wavePaint.setStyle(Paint.Style.FILL);
       wavePaint.setAntiAlias(true);
       wavePath = new Path();
       post(waveAnimator);
}

private final Runnable waveAnimator = new Runnable(){
    @Override
    public void run() {
        waveShift+=10;
        if(waveShift>getWidth()){
            waveShift=0;
        }
        invalidate();
        postDelayed(this,42);
    }
};
   protected void onDraw(Canvas canvas){
       super.onDraw(canvas);
       float width = getWidth();
       float height = getHeight();



       waveLength = width/2;
       wavePath.reset();
       float centerY = height*(1-percentage);
       wavePath.moveTo(0,centerY);

       for(float x=0;x<=width;x++){
           float y = (float)(waveHeight*Math.sin((x+waveShift)*2*Math.PI/waveLength));
           wavePath.lineTo(x,centerY+y);
       }
   wavePath.lineTo(width,height);
       wavePath.lineTo(0,height);
       wavePath.close();

       canvas.drawPath(wavePath,wavePaint);

   }

    public void setPercentage(float v) {
       this.percentage = v;
       invalidate();
    }
}
