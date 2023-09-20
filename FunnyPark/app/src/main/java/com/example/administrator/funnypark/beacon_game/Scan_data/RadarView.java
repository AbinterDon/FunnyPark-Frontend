package com.example.administrator.funnypark.beacon_game.Scan_data;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.example.administrator.funnypark.R;


public class RadarView extends View {

    //圓圈顏色
    public int mRingColor;
    //掃描背景顏色
    public int mScanBgColor;
    //圓圈寬度
    public float mRingWidth;
    //圓圈數量
    public int mRingNum;
    //掃瞄速度 越小越快  毫秒值
    public int mScanSpeed;
    //掃描角度
    private int mScanAngle;
    //圓圈畫筆
    private Paint mRingPaint;
    //中間圖片畫筆
    private Paint mCicleIconPaint;
    //中間圖片圓圈
    private Paint center_cicle;
    //掃描畫筆
    private Paint mScanPaint;
    //中間圖片
    private Bitmap mCenterIcon;
    //寬
    private int mWidth;
    //高
    private int mHeight;
    //圓圈比例
    private float mRingScale = 1 / 10f;

    private SweepGradient mScanShader;
    //旋轉需要的矩陣
    private Matrix matrix = new Matrix();

    private OnScanningListener mOnScanningListener;

    //是否開始掃描
    private boolean startScan;
    //掃描次數
    private int currentScanningCount;
    //掃描顯示的列表
    private int currentScanningItem;
    //最大掃描次數
    private int maxScanItemCount;
    //當前掃描角度
    private float currentScanAngle;

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView);
        //mRingColor = typedArray.getColor(R.styleable.RadarView_mRingColor, Color.parseColor("#bbbbbb"));//圓圈顏色
        mScanBgColor = typedArray.getColor(R.styleable.RadarView_mScanBgColor, Color.parseColor("#3e3e3e"));//掃描背景顏色
        //mRingWidth = typedArray.getDimensionPixelSize(R.styleable.RadarView_mRingWidth, 0);//圓圈線條寬度
        mRingNum = typedArray.getInteger(R.styleable.RadarView_mRingNum, 6);//圓圈數量
        mScanSpeed = typedArray.getColor(R.styleable.RadarView_mScanSpeed, 35);//掃瞄速度 越小越快  毫秒值
        mScanAngle = typedArray.getColor(R.styleable.RadarView_mScanAngle, 5);//掃描角度
        typedArray.recycle();

        /*
        //中間圖片
        mCenterIcon = BitmapFactory.decodeResource(getResources(), mBitmap);
        */

        //設定多個圓圈畫筆
        mRingPaint = new Paint();
        mRingPaint.setColor(mRingColor);
        mRingPaint.setAntiAlias(true);
        mRingPaint.setStrokeWidth(mRingWidth);
        mRingPaint.setStyle(Paint.Style.STROKE);

        //設定中間圖片畫筆
        mCicleIconPaint = new Paint();
        mCicleIconPaint.setColor(Color.WHITE);
        mCicleIconPaint.setAntiAlias(true);

        //設定中間圓圈畫筆
        center_cicle = new Paint();
        center_cicle.setColor(Color.WHITE);
        center_cicle.setAntiAlias(true);


        //掃描畫筆
        mScanPaint = new Paint();
        mScanPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //啟動掃描
        post(mRunnable);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
    }


    private int measureSize(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 400;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 这里设置宽高  主要思想是因为是方形 所以取最小的作为宽高值
        mWidth=getMeasuredWidth();
        mHeight=getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth, mHeight);

        // 绘制圆环
       // drawRing(canvas);
        // 绘制扫描
        drawScan(canvas);
        if (mCenterIcon != null) {
            // 绘制中间icon
            drawCenterIcon(canvas);
        }
    }


    //繪製中間 icon
    private void drawCenterIcon(Canvas canvas) {
        //以最中間的圓為區域設置這中間圖片 中間去除padding
        float scale=0.7f; //中間icon大小
        float radius=(mWidth-getPaddingLeft()-getPaddingRight())*mRingScale*scale;
        Rect rect=new Rect((int)(mWidth/2-radius),(int)(mHeight/2-radius),(int)(mWidth/2+radius),(int)(mHeight/2+radius));

        //canvas.drawCircle(mWidth/2,mHeight/2,radius,center_cicle);//中間圓圈

        //如果mBitmap不等於空值 畫小人圖
        if (mCenterIcon != null) {
            canvas.drawBitmap(mCenterIcon,null,rect,mCicleIconPaint); //中間icon
        }
    }

    //設定小人的icon
    public void setCenterIcon(int resId) {
        mCenterIcon = BitmapFactory.decodeResource(getResources(), resId);
        invalidate();
    }


    //繪製圓圈
    private void drawRing(Canvas canvas) {
        for (int i=0;i<mRingNum;i++){
            mRingPaint.setAlpha(getAlpha(mRingNum,i));
            // 繪製小圓
            canvas.drawCircle(mWidth / 2 , mHeight / 2,(mWidth-getPaddingLeft()-getPaddingRight()) * (1+i)*mRingScale, mRingPaint);
        }
    }

    private void drawScan(Canvas canvas) {
        mScanShader = new SweepGradient(mWidth / 2, mHeight / 2, new int[]{Color.TRANSPARENT, mScanBgColor}, null);
        //保存畫布當前的狀況
        canvas.save();
        mScanPaint.setShader(mScanShader);
        //canvas.concat可以理解成对matrix的变换应用到canvas上的所有对象
        canvas.concat(matrix);
        //繪製圖
        canvas.drawCircle(mWidth / 2, mHeight / 2, (mWidth-getPaddingLeft()-getPaddingRight()) * (mRingNum-1) * mRingScale, mScanPaint);
        //取出之前保存过的状态 和save成对出现 为了不影响其他部分的绘制
        canvas.restore();
    }

    private int getAlpha(int halfCount, int index) {
        int MAX_ALPHA_VALUE = 255;
        int alpha= MAX_ALPHA_VALUE / halfCount * (halfCount - index);
        return index==0?0:alpha-25;
    }

    //掃描
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            matrix.postRotate(mScanAngle, getMeasuredWidth()/2,getMeasuredWidth()/2);
            invalidate();
            postDelayed(mRunnable, mScanSpeed);
            currentScanAngle = (currentScanAngle + mScanAngle) % 360;

            if (startScan && currentScanningCount <= (360 / mScanAngle))
            {
                if (mOnScanningListener != null && currentScanningCount % mScanAngle == 0 && currentScanningItem < maxScanItemCount)
                {
                    mOnScanningListener.onScanning(currentScanningItem, currentScanAngle);
                    currentScanningItem++;
                }
                else if (mOnScanningListener != null && currentScanningItem == maxScanItemCount)
                {
                    mOnScanningListener.onScanSuccess();
                }
                currentScanningCount++;
            }
        }
    };


    //開始掃描
    public void startScan(){
        this.startScan=true;
    }

    public void setOnScanningListener(OnScanningListener mOnScanningListener){
        this.mOnScanningListener=mOnScanningListener;
    }

    public interface  OnScanningListener {
        void onScanning(int position, float scanAngle);
        void onScanSuccess();
    }

    //設定最大掃描數量
    public void setMaxScanItemCount(int maxScanItemCount) {
        this.maxScanItemCount = maxScanItemCount;
    }
}
