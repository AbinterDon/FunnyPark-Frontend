package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class RadarCircleView extends View {

    //畫筆
    private Paint mPaint;
    //圖片
    private Bitmap mBitmap;
    //半徑
    private float radius = dp2px(getContext(),7);
    //位置X
    private float disX;
    //位置Y
    private float disY;
    //旋轉的角度
    private float angle;
    //距離的不同，計算應該得到多少半徑
    private float proportion;


    public RadarCircleView(Context context) {
        this(context,null);
    }
    public RadarCircleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RadarCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#00000000")); //預設為透明色
        mPaint.setAntiAlias(true);
    }

    //View的尺寸測量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }
    //測量寬高
    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = dp2px(getContext(),18);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    //將dp值轉換為px值
    public  int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //畫圓
        canvas.drawCircle(radius, radius, radius, mPaint);

        /*
        //如果mBitmap不等於空值 畫小人圖
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, null, new Rect(0, 0, 2 * (int) radius, 2 * (int) radius), mPaint);
        }
*/

    }

    //設定畫筆顏色
    public void setPaintColor(int resId) {
        mPaint.setColor(resId);
        invalidate();
    }

    /*
    //設定小人的icon
    public void setPortraitIcon(int resId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resId);
        invalidate();
    }

    //清除小人icon
    public void clearPortaitIcon(){
        mBitmap = null;
        invalidate();
    }
    */


    //比例
    public float getProportion() {
        return proportion;
    }

    public void setProportion(float proportion) {
        this.proportion = proportion;
    }

    //角度
    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    // X軸
    public float getDisX() {
        return disX;
    }

    public void setDisX(float disX) {
        this.disX = disX;
    }

    // Y軸
    public float getDisY() {
        return disY;
    }

    public void setDisY(float disY) {
        this.disY = disY;
    }

}
