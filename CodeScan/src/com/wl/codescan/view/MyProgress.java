package com.wl.codescan.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;


/**
 * @类描述： 背景文字进度条
 * @创建人：雷纯官
 * @创建时间：2014-05-29
 * @修改人：
 * @修改时间：
 * @修改备注：
 * @version
 */
public class MyProgress extends ProgressBar{
    private String text;
    private String size;
    private Paint mPaint;
     
    public MyProgress(Context context) {
        super(context);
        initText(); 
    }
     
    public MyProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }
 
 
    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }
     
    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
         
    }
 
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.setText();
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
        int x = (getWidth() / 2) - rect.centerX();  
        int y = (getHeight() / 2) - rect.centerY();  
        canvas.drawText(this.text, x, y, this.mPaint);  
    }
     
    //初始化，画笔
    private void initText(){
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setTextSize(20);
    }
     
    public void setText(){
        setText(this.getProgress());
    }
     
    //设置文字内容
    private void setText(int progress){
//        int i = (progress * 100)/this.getMax();
//        this.text = "  进度："+String.valueOf(i) + "%";
    	this.text="进度："+progress + "%";
    	invalidate();
    }

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
     
    
    
     
}