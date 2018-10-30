package Tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class BeautyTextView extends TextView {
    //控件的宽
    int mViewWidth;
    //平移量
    int mTransLate;
    private TextPaint paint;
    private LinearGradient mlinearGradient;
    private Matrix matrix;

    public BeautyTextView(Context context) {
        super(context);
    }

    public BeautyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BeautyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHigth(heightMeasureSpec));
    }

    /**
     * 测量宽的方法
     * @param measureSpec
     * @return
     */
    private int measureWidth(int measureSpec){
        int result = 0;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(measureMode == MeasureSpec.EXACTLY)
        {
            result = size;
        }else{
            result = 200;
            //当控件为warp_content时
            if(measureMode ==MeasureSpec.AT_MOST)
            {
                result = Math.min(result,size);
            }
        }
        return result;
    }

    /**
     * 测量控件高度的方法
     * @param measureSpec
     * @return
     */
    private int measureHigth(int measureSpec)
    {
        int result = 0;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if(measureMode == MeasureSpec.EXACTLY)
        {
            result = size;
        }else{
            result = 200;
            if(measureMode == MeasureSpec.AT_MOST)
            {
                result = Math.min(result,size);
            }
        }
        return result;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mViewWidth ==0)
        {
            mViewWidth = getMeasuredWidth();
        }
        if(mViewWidth>0)
        {
            //获取绘制当前TextView的paint对象
            paint = getPaint();
            //创建渲染渐变器
            mlinearGradient = new LinearGradient(0,0,mViewWidth,0,new int[]{Color.BLUE,0xffffffff,Color.BLUE},null, Shader.TileMode.CLAMP);
            paint.setShader(mlinearGradient);
            matrix = new Matrix();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(matrix!=null)
        {
            mTransLate+=mViewWidth/5;
            if(mTransLate>2*mViewWidth)
            {
                mTransLate = -mViewWidth;
            }
            matrix.setTranslate(mTransLate,0);
            mlinearGradient.setLocalMatrix(matrix);
            postInvalidateDelayed(100);
        }
    }
}

