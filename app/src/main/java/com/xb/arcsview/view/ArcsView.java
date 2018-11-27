package com.xb.arcsview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;


import com.xb.arcsview.R;
import com.xb.arcsview.data.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义饼状图
 *
 * @author xb
 * @time 2018/11/26 0026 19:01
 */
public class ArcsView extends View {

    private Paint mPaint;
    //小圆的画笔
    private Paint mSmallCirPaint;
    private Paint mSmallCenterCirPaint;
    //描述文字画笔
    private Paint mDescTextPaint;
    //内部圆的半径
    private int mCenterRadius = 200;
    //外部圆的半径
    private int mExternalRadius = 300;
    //中心点X坐标
    private int mCenterX;
    //中心点Y坐标
    private int mCenterY;
    //文字偏移量，默认为10
    private int mOffset = 20;
    //中间文字大小
    private int mCenterTextSize;
    //描述文字大小
    private int mDescTextSize;
    //中间文字颜色
    private int mCenterTextColor;
    //描述文字颜色
    private int mDescTextColor;
    //数据总和
    private float mTotalValue;
    //起始角度
    private float mStartAngle = 0f;
    //是否启动动画
    private boolean mIsStartAnim = true;
    //中间文字
    private String mCenterText = "ArcsView";
    private Rect r = new Rect();
    private RectF rectF = new RectF();
    //数据列表
    private List<Data> mData = new ArrayList<>();
    //动画
    private PieChartAnimation mPieChartAnimation;
    //动画时间
    private static final long ANIMATION_DURATION = 1000;

    /**
     * 自定义颜色
     */
    private static int[] mColors = {
            0xFF323232,
            0xFFCFB054,
            0xFFDA8A8A,
            0xFFC89FC2,
            0xFF8AD3D2,
            0xFF7DB3CB,
            0xFF78899E,
            0xFF62AD98,
            0xFFB8A690,
            0xFFA1B060,
            0xFFD08E76
    };

    public ArcsView(Context context) {
        this(context, null);
        init(context);
    }

    public ArcsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public ArcsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArcsView);
        mCenterText = array.getString(R.styleable.ArcsView_centerText);
        mCenterTextSize = array.getDimensionPixelSize(R.styleable.ArcsView_centerTextSize, 20);
        mCenterTextColor = array.getColor(R.styleable.ArcsView_centerTextColor, ContextCompat.getColor(context, R.color.white));
        mDescTextSize = array.getDimensionPixelSize(R.styleable.ArcsView_descTextSize, 12);
        mDescTextColor = array.getColor(R.styleable.ArcsView_descTextColor, ContextCompat.getColor(context, R.color.white));
        mIsStartAnim = array.getBoolean(R.styleable.ArcsView_isStartAnim, true);

        array.recycle();
        init(context);
    }

    /**
     * 初始化画笔
     *
     * @param context 上下文对象
     */
    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FFFFFF"));
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);//设置填充
        mPaint.setDither(true);//防止抖动
        mPaint.setAntiAlias(true);

        //外部小圆画笔
        mSmallCirPaint = new Paint();
        mSmallCirPaint.setColor(Color.parseColor("#33FFFFFF"));
        mSmallCirPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);//防止抖动
        mSmallCirPaint.setAntiAlias(true);

        //内部小圆画笔
        mSmallCenterCirPaint = new Paint();
        mSmallCenterCirPaint.setColor(Color.parseColor("#FFFFFF"));
        mSmallCenterCirPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);//防止抖动
        mSmallCenterCirPaint.setAntiAlias(true);

        //描述文字画笔
        mDescTextPaint = new Paint();
        mDescTextPaint.setColor(mDescTextColor);
        mDescTextPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);//防止抖动
        mDescTextPaint.setAntiAlias(true);
        mDescTextPaint.setTextSize(mDescTextSize);

        //初始化动画
        mPieChartAnimation = new PieChartAnimation();
        mPieChartAnimation.setDuration(ANIMATION_DURATION);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判空
        if (mData == null || mData.size() == 0) return;

        mCenterX = (getRight() - getLeft()) / 2;
        mCenterY = (getBottom() - getTop()) / 2;

        //画外部圆
        drawArc(canvas);
        //画内部圆
        drawCenterCircleAndText(canvas);
        //画线
        drawLines(canvas);
    }

    /**
     * 画指示线
     *
     * @param canvas 画板
     */
    private void drawLines(Canvas canvas) {
        int start = 0;
        canvas.translate(mCenterX, mCenterY);//平移画布到中心
        double totalAmount = mTotalValue;
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.parseColor("#FFFFFF"));

        for (int i = 0; i < mData.size(); i++) {
            float angles = (float) ((mData.get(i).getAmount() / totalAmount) * 360);
            drawLine(canvas, start, angles, mData.get(i).getAmountText());
            start += angles;
        }
    }

    /**
     * 画直线
     *
     * @param canvas     画板
     * @param start      开始角度
     * @param angles     角度
     * @param amountText 描述文字
     */
    private void drawLine(Canvas canvas, int start, float angles, String amountText) {
        float stopX;
        float stopY;
        float startX;
        float startY;
        stopX = (float) ((mExternalRadius + 60) * Math.cos((2 * start + angles) / 2 * Math.PI / 180));
        stopY = (float) ((mExternalRadius + 100) * Math.sin((2 * start + angles) / 2 * Math.PI / 180));
        startX = (float) ((mExternalRadius + 20) * Math.cos((2 * start + angles) / 2 * Math.PI / 180));
        startY = (float) ((mExternalRadius + 20) * Math.sin((2 * start + angles) / 2 * Math.PI / 180));
        canvas.drawLine(startX, startY, stopX, stopY, mPaint);

        //画横线
        int dx;//判断横线是画在左边还是右边
        float endX;
        if (stopX > 0) {
            endX = stopX + 150;
        } else {
            endX = stopX - 150;
        }
        dx = (int) (endX - stopX);
        //画横线
        canvas.drawLine(stopX, stopY,
                endX, stopY, mPaint
        );
        //画外部小圆
        canvas.drawCircle(endX, stopY, 30, mSmallCirPaint);
        //画内部小圆
        canvas.drawCircle(endX, stopY, 15, mSmallCenterCirPaint);
        //画文字
        float x;
        float y;
        int width;
        int height;
        //TODO 如果文字从中心点的右边画，不用考虑android文字是从右到左开始绘制
        //获取文字的宽度
        mDescTextPaint.getTextBounds(amountText, 0, amountText.length(), r);
        width = r.width();
        height = r.height();

        if (dx > 0) {
            //30为最外层小圆的宽度
            x = endX + 30 + mOffset;
        } else {
            x = endX - (width + 30 + mOffset);
        }
        y = stopY + (height / 2);
        canvas.drawText(amountText, x, y, mDescTextPaint);
    }


    /**
     * 画内部圆以及内容
     *
     * @param canvas 画板
     */
    private void drawCenterCircleAndText(Canvas canvas) {
        //画内部圆
        mPaint.setColor(Color.parseColor("#6FA2FD"));
        canvas.drawCircle(mCenterX, mCenterY, mCenterRadius, mPaint);

        //画中间文字
        mPaint.setColor(mCenterTextColor);
        mPaint.setTextSize(mCenterTextSize);
        mPaint.setStyle(Paint.Style.FILL);

        //测量文字的宽度
        mPaint.getTextBounds(mCenterText, 0, mCenterText.length(), r);
        int textWidth = r.width();

        int x = mCenterX - textWidth / 2;
        int y = mCenterY;

        canvas.drawText(mCenterText, x, y, mPaint);
    }

    /**
     * 画外部圆
     *
     * @param canvas 画板
     */
    private void drawArc(Canvas canvas) {
        rectF.set((float) mCenterX - mExternalRadius, (float) mCenterY - mExternalRadius, (float) mCenterX + mExternalRadius, (float) mCenterY + mExternalRadius);

        //2.设置当前起始角度
        float currentStartAngle = mStartAngle;
        for (int i = 0; i < mData.size(); i++) {
            Data data = mData.get(i);
            mPaint.setColor(mData.get(i).getColor());
            canvas.drawArc(rectF, currentStartAngle, data.getAngle(), true, mPaint);
            currentStartAngle += data.getAngle();
        }
    }


    /**
     * 初始化数据集
     *
     * @param data 用户数据集
     */
    private void initData(List<Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mTotalValue += data.get(i).getAmount();
            //颜色值为0的话，自动为其设置随机颜色值
            if (data.get(i).getColor() == 0) {
                data.get(i).setColor(mColors[i % mColors.length]);
            }
        }

        float currentStartAngle = mStartAngle;
        for (int i = 0; i < data.size(); i++) {
            Data data1 = data.get(i);
            data1.setCurrentStartAngle(currentStartAngle);
            //通过总和来计算百分比
            float percentage = (float) (data1.getAmount() / mTotalValue);
            //通过百分比来计算对应的角度
            float angle = percentage * 360;
            //设置用户数据
            data1.setPercentage(percentage);
            data1.setAngle(angle);
            currentStartAngle += angle;
        }
    }

    /**
     * 自定义动画类
     */
    public class PieChartAnimation extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                for (int i = 0; i < mData.size(); i++) {
                    Data data = mData.get(i);
                    //通过总和来计算百分比
                    float percentage = (float) (data.getAmount() / mTotalValue);
                    //通过百分比来计算对应的角度
                    float angle = percentage * 360;
                    //根据插入时间来计算角度
                    angle = angle * interpolatedTime;
                    data.setAngle(angle);
                }
            } else {//默认显示效果
                for (int i = 0; i < mData.size(); i++) {
                    //通过总和来计算百分比
                    Data data = mData.get(i);
                    float percentage = (float) (data.getAmount() / mTotalValue);
                    //通过百分比来计算对应的角度
                    float angle = percentage * 360;
                    data.setAngle(angle);
                }
            }
            invalidate();
        }
    }

    /**
     * 设置数据信息
     *
     * @param data 数据集
     */
    public void setData(List<Data> data) {
        mTotalValue = 0;
        this.mData = data;
        initData(mData);
        if (mIsStartAnim) startAnimation(mPieChartAnimation);
        invalidate();
    }

    /**
     * 设置起始角度
     *
     * @param startAngle
     */
    public void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
        invalidate();//刷新
    }

    /**
     * 是否显示动画
     *
     * @param isStartAnim 是否开启动画
     */
    public void isStartAnim(boolean isStartAnim) {
        this.mIsStartAnim = isStartAnim;
        invalidate();
    }
}
