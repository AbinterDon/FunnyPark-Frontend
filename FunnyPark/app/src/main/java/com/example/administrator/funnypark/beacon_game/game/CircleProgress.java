package com.example.administrator.funnypark.beacon_game.game;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.administrator.funnypark.BuildConfig;
import com.example.administrator.funnypark.R;

public class CircleProgress extends View {
    private static final String TAG = CircleProgress.class.getSimpleName();

    private Context mContext;
    //默認大小
    private int mDefaultSize;
    //是否開啓抗鋸齒
    private boolean antiAlias;
    //繪製提示
    private TextPaint mHintPaint;
    private CharSequence mHint;
    private int mHintColor;
    private float mHintSize;
    private float mHintOffset;

    //繪製單位
    private TextPaint mUnitPaint;
    private CharSequence mUnit;
    private int mUnitColor;
    private float mUnitSize;
    private float mUnitOffset;

    //繪製數值
    private TextPaint mValuePaint;
    private float mValue;
    private float mMaxValue;
    private float mValueOffset;
    private int mPrecision;
    private String mPrecisionFormat;
    private int mValueColor;
    private float mValueSize;

    //繪製圓弧
    private Paint mArcPaint;
    private float mArcWidth;
    private float mStartAngle, mSweepAngle;
    private RectF mRectF;
    //當前進度，[0.0f,1.0f]
    private float mPercent;
    //動畫時間
    private long mAnimTime;
    //屬性動畫
    private ValueAnimator mAnimator;

    //繪製背景圓弧
    private Paint mBgArcPaint;
    private int mBgArcColor;
    private int mArcColor;
    private float mBgArcWidth;

    //圓心座標，半徑
    private Point mCenterPoint;
    private float mRadius;
    private float mTextOffsetPercentInRadius;

    private int mArcCenterX;
    // 內部虛線的外部半徑
    private float mExternalDottedLineRadius;
    // 內部虛線的內部半徑
    private float mInsideDottedLineRadius;

    // 線條數
    private int mDottedLineCount = 100;
    // 圓弧跟虛線之間的距離
    private int mLineDistance = 20;
    // 線條寬度
    private float mDottedLineWidth = 40;
    //是否使用漸變
    protected boolean useGradient=true;
    //前景色起始顏色
    private int foreStartColor;
    //前景色結束顏色
    private int foreEndColcor;
    protected int mWidth;
    protected int mHeight;

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        mDefaultSize = dipToPx(mContext, 150);
        mAnimator = new ValueAnimator();
        mRectF = new RectF();
        mCenterPoint = new Point();
        initAttrs(attrs);
        initPaint();
        setValue(mValue);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar);

        antiAlias = typedArray.getBoolean(R.styleable.CircleProgressBar_antiAlias, false);

        mHint = typedArray.getString(R.styleable.CircleProgressBar_hint);
        mHintColor = typedArray.getColor(R.styleable.CircleProgressBar_hintColor, Color.BLACK);
        mHintSize = typedArray.getDimension(R.styleable.CircleProgressBar_hintSize, 15);

        mValue = typedArray.getFloat(R.styleable.CircleProgressBar_value, 50);
        mMaxValue = typedArray.getFloat(R.styleable.CircleProgressBar_maxValue, 50);
        //內容數值精度格式
        mPrecision = typedArray.getInt(R.styleable.CircleProgressBar_precision, 0);
        mPrecisionFormat = getPrecisionFormat(mPrecision);
        mValueColor = typedArray.getColor(R.styleable.CircleProgressBar_valueColor, Color.BLACK);
        mValueSize = typedArray.getDimension(R.styleable.CircleProgressBar_valueSize, 15);

        mUnit = typedArray.getString(R.styleable.CircleProgressBar_unit);
        mUnitColor = typedArray.getColor(R.styleable.CircleProgressBar_unitColor, Color.BLACK);
        mUnitSize = typedArray.getDimension(R.styleable.CircleProgressBar_unitSize, 30);

        mArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_arcWidth, 15);
        mStartAngle = typedArray.getFloat(R.styleable.CircleProgressBar_startAngle, 270);
        mSweepAngle = typedArray.getFloat(R.styleable.CircleProgressBar_sweepAngle, 360);

        mBgArcColor = typedArray.getColor(R.styleable.CircleProgressBar_bgArcColor, Color.WHITE);
        mArcColor = typedArray.getColor(R.styleable.CircleProgressBar_arcColors, Color.RED);
        mBgArcWidth = typedArray.getDimension(R.styleable.CircleProgressBar_bgArcWidth, 15);
        mTextOffsetPercentInRadius = typedArray.getFloat(R.styleable.CircleProgressBar_textOffsetPercentInRadius, 0.33f);
        mAnimTime = typedArray.getInt(R.styleable.CircleProgressBar_animTime, 200);
        mDottedLineCount = typedArray.getInteger(R.styleable.CircleProgressBar_dottedLineCount, mDottedLineCount);
        mLineDistance = typedArray.getInteger(R.styleable.CircleProgressBar_lineDistance, mLineDistance);
        mDottedLineWidth = typedArray.getDimension(R.styleable.CircleProgressBar_dottedLineWidth, mDottedLineWidth);
        foreStartColor = typedArray.getColor(R.styleable.CircleProgressBar_foreStartColor, Color.BLUE);
        foreEndColcor = typedArray.getColor(R.styleable.CircleProgressBar_foreEndColor, Color.BLUE);
        typedArray.recycle();
    }

    private void initPaint() {
        mHintPaint = new TextPaint();
        // 設置抗鋸齒,會消耗較大資源，繪製圖形速度會變慢。
        mHintPaint.setAntiAlias(antiAlias);
        // 設置繪製文字大小
        mHintPaint.setTextSize(mHintSize);
        // 設置畫筆顏色
        mHintPaint.setColor(mHintColor);
        // 從中間向兩邊繪製，不需要再次計算文字
        mHintPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new TextPaint();
        mValuePaint.setAntiAlias(antiAlias);
        mValuePaint.setTextSize(mValueSize);
        mValuePaint.setColor(mValueColor);
        // 設置Typeface對象，即字體風格，包括粗體，斜體以及襯線體，非襯線體等
        mValuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mValuePaint.setTextAlign(Paint.Align.CENTER);

        mUnitPaint = new TextPaint();
        mUnitPaint.setAntiAlias(antiAlias);
        mUnitPaint.setTextSize(mUnitSize);
        mUnitPaint.setColor(mUnitColor);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(antiAlias);
        // 設置畫筆的樣式，爲FILL，FILL_OR_STROKE，或STROKE
        mArcPaint.setStyle(Paint.Style.STROKE);
        // 設置畫筆粗細
        mArcPaint.setStrokeWidth(mArcWidth);
        // 當畫筆樣式爲STROKE或FILL_OR_STROKE時，設置筆刷的圖形樣式，如圓形樣式
        // Cap.ROUND(圓形樣式)或Cap.SQUARE(方形樣式)
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);

        mBgArcPaint = new Paint();
        mBgArcPaint.setAntiAlias(antiAlias);
        mBgArcPaint.setColor(mBgArcColor);
        mBgArcPaint.setStyle(Paint.Style.STROKE);
        mBgArcPaint.setStrokeWidth(mBgArcWidth);
        mBgArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureView(widthMeasureSpec, mDefaultSize),
                measureView(heightMeasureSpec, mDefaultSize));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcCenterX = (int) (w / 2.f);
        Log.d(TAG, "onSizeChanged: w = " + w + "; h = " + h + "; oldw = " + oldw + "; oldh = " + oldh);
        //求圓弧和背景圓弧的最大寬度
        float maxArcWidth = Math.max(mArcWidth, mBgArcWidth);
        //求最小值作爲實際值
        int minSize = Math.min(w - getPaddingLeft() - getPaddingRight() - 2 * (int) maxArcWidth,
                h - getPaddingTop() - getPaddingBottom() - 2 * (int) maxArcWidth);
        //減去圓弧的寬度，否則會造成部分圓弧繪製在外圍
        mRadius = minSize / 2;
        //獲取圓的相關參數
        mCenterPoint.x = w / 2;
        mCenterPoint.y = h / 2;
        //繪製圓弧的邊界
        mRectF.left = mCenterPoint.x - mRadius - maxArcWidth / 2;
        mRectF.top = mCenterPoint.y - mRadius - maxArcWidth / 2;
        mRectF.right = mCenterPoint.x + mRadius + maxArcWidth / 2;
        mRectF.bottom = mCenterPoint.y + mRadius + maxArcWidth / 2;
        //計算文字繪製時的 baseline
        //由於文字的baseline、descent、ascent等屬性只與textSize和typeface有關，所以此時可以直接計算
        //若value、hint、unit由同一個畫筆繪製或者需要動態設置文字的大小，則需要在每次更新後再次計算
        mValueOffset = mCenterPoint.y + getBaselineOffsetFromY(mValuePaint);
        mHintOffset = mCenterPoint.y - mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mHintPaint);
        mUnitOffset = mCenterPoint.y + mRadius * mTextOffsetPercentInRadius + getBaselineOffsetFromY(mUnitPaint);

        if (useGradient) {
            LinearGradient gradient = new LinearGradient(0, 0, w, h, foreEndColcor, foreStartColor, Shader.TileMode.CLAMP);
            mArcPaint.setShader(gradient);
        } else {
            mArcPaint.setColor(mArcColor);
        }

        Log.d(TAG, "onSizeChanged: 控件大小 = " + "(" + w + ", " + h + ")"
                + "圓心座標 = " + mCenterPoint.toString()
                + ";圓半徑 = " + mRadius
                + ";圓的外接矩形 = " + mRectF.toString());

        // 虛線的外部半徑
        mExternalDottedLineRadius = (int) (mRectF.width() / 2)+mLineDistance;
        // 虛線的內部半徑
        mInsideDottedLineRadius = mExternalDottedLineRadius - mDottedLineWidth;
    }

    private float getBaselineOffsetFromY(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return ((Math.abs(fontMetrics.ascent) - fontMetrics.descent))/ 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawArc(canvas);
    }

    /**
     * 繪製內容文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        canvas.drawText(String.format(mPrecisionFormat, mValue), mCenterPoint.x, mValueOffset, mValuePaint);

        if (mHint != null) {
            canvas.drawText(mHint.toString(), mCenterPoint.x, mHintOffset, mHintPaint);
        }

        if (mUnit != null) {
            canvas.drawText(mUnit.toString(), mCenterPoint.x, mUnitOffset, mUnitPaint);
        }
    }

    private void drawArc(Canvas canvas) {
        // 繪製背景圓弧
        // 從進度圓弧結束的地方開始重新繪製，優化性能
        canvas.save();

        // 360 * Math.PI / 180
        float evenryDegrees = (float) (2.0f * Math.PI / mDottedLineCount);
        float startDegress = (float) (135 * Math.PI / 180);
        float endDegress = (float) (225 * Math.PI / 180);
        for (int i = 0; i < mDottedLineCount; i++) {
            float degrees = i * evenryDegrees;
            // 過濾底部90度的弧長
            if (degrees > startDegress && degrees < endDegress) {
                continue;
            }
            float startX = mArcCenterX + (float) Math.sin(degrees) * mInsideDottedLineRadius;
            float startY = mArcCenterX - (float) Math.cos(degrees) * mInsideDottedLineRadius;

            float stopX = mArcCenterX + (float) Math.sin(degrees) * mExternalDottedLineRadius;
            float stopY = mArcCenterX - (float) Math.cos(degrees) * mExternalDottedLineRadius;

            canvas.drawLine(startX, startY, stopX, stopY, mBgArcPaint);
        }

        canvas.rotate(mStartAngle, mCenterPoint.x, mCenterPoint.y);

        // 第一個參數 oval 爲 RectF 類型，即圓弧顯示區域
        // startAngle 和 sweepAngle  均爲 float 類型，分別表示圓弧起始角度和圓弧度數
        // 3點鐘方向爲0度，順時針遞增
        // 如果 startAngle < 0 或者 > 360,則相當於 startAngle % 360
        // useCenter:如果爲True時，在繪製圓弧時將圓心包括在內，通常用來繪製扇形
        float currentAngle = mSweepAngle * mPercent;
        canvas.drawArc(mRectF, 2, currentAngle, false, mArcPaint);
        canvas.restore();
    }

    public boolean isAntiAlias() {
        return antiAlias;
    }

    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        mHint = hint;
    }

    public CharSequence getUnit() {
        return mUnit;
    }

    public void setUnit(CharSequence unit) {
        mUnit = unit;
    }

    public float getValue() {
        return mValue;
    }

    /**
     * 設置當前值
     *
     * @param value
     */
    public void setValue(float value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        float start = mPercent;
        float end = value / mMaxValue;
        startAnimator(start, end, mAnimTime);
    }

    private void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                mValue = mPercent * mMaxValue;
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onAnimationUpdate: percent = " + mPercent
                            + ";currentAngle = " + (mSweepAngle * mPercent)
                            + ";value = " + mValue);
                }
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 獲取最大值
     *
     * @return
     */
    public float getMaxValue() {
        return mMaxValue;
    }

    /**
     * 設置最大值
     *
     * @param maxValue
     */
    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

    /**
     * 獲取精度
     *
     * @return
     */
    public int getPrecision() {
        return mPrecision;
    }

    public void setPrecision(int precision) {
        mPrecision = precision;
        mPrecisionFormat = getPrecisionFormat(precision);
    }

    public long getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(long animTime) {
        mAnimTime = animTime;
    }

    /**
     * 重置
     */
    public void reset() {
        startAnimator(mPercent, 0.0f, 1000L);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //釋放資源
    }

    /**
     * 測量 View
     *
     * @param measureSpec
     * @param defaultSize View 的默認大小
     * @return
     */
    private static int measureView(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    /**
     * dip 轉換成px
     *
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, float dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 獲取數值精度格式化字符串
     *
     * @param precision
     * @return
     */
    public static String getPrecisionFormat(int precision) {
        return "%." + precision + "f";
    }
}
