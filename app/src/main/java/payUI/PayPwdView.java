package payUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.example.car.R;

import java.util.ArrayList;

/**
 * 引用网上的一位朋友的类，具体出处找不到了
 *
 */
public class PayPwdView extends View {

    private ArrayList<String> result;//输入结果保存
    private int count;//密码位数
    private int size;//默认每一格的大小
    private Paint mBorderPaint;//边界画笔
    private Paint mDotPaint;//掩盖点的画笔
    private int mBorderColor;//边界颜色
    private int mDotColor;//掩盖点的颜色
    private RectF mRoundRect;//外面的圆角矩形
    private int mRoundRadius;//圆角矩形的圆角程度

    public PayPwdView(Context context) {
        super(context);
        init(null);
    }

    private InputCallBack inputCallBack;//输入完成的回调
    private PwdInputMethodView inputMethodView; //输入键盘


    public interface InputCallBack {
        void onInputFinish(String result);
    }

    public PayPwdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PayPwdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化相关参数
     */
    void init(AttributeSet attrs) {
        final float dp = getResources().getDisplayMetrics().density;
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        result = new ArrayList<>();
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PayPwdView);
            mBorderColor = ta.getColor(R.styleable.PayPwdView_border_color, Color.LTGRAY);
            mDotColor = ta.getColor(R.styleable.PayPwdView_dot_color, Color.BLACK);
            count = ta.getInt(R.styleable.PayPwdView_count, 6);
            ta.recycle();
        } else {
            mBorderColor = Color.LTGRAY;
            mDotColor = Color.GRAY;
            count = 6;//默认6位密码
        }
        size = (int) (dp * 30);//默认30dp一格
        //color
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStrokeWidth(3);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);

        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint.setStrokeWidth(3);
        mDotPaint.setStyle(Paint.Style.FILL);
        mDotPaint.setColor(mDotColor);
        mRoundRect = new RectF();
        mRoundRadius = (int) (5 * dp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = measureWidth(widthMeasureSpec);
        int h = measureHeight(heightMeasureSpec);
        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);
        //宽度没指定,但高度指定
        if (w == -1) {
            if (h != -1) {
                w = h * count;//宽度=高*数量
                size = h;
            } else {//两个都不知道,默认宽高
                w = size * count;
                h = size;
            }
        } else {//宽度已知
            if (h == -1) {//高度不知道
                h = w / count;
                size = h;
            }
        }
        setMeasuredDimension(Math.min(w, wsize), Math.min(h, hsize));
    }

    private int measureWidth(int widthMeasureSpec) {
        //宽度
        int wmode = MeasureSpec.getMode(widthMeasureSpec);
        int wsize = MeasureSpec.getSize(widthMeasureSpec);
        if (wmode == MeasureSpec.AT_MOST) {//wrap_content
            return -1;
        }
        return wsize;
    }

    private int measureHeight(int heightMeasureSpec) {
        //高度
        int hmode = MeasureSpec.getMode(heightMeasureSpec);
        int hsize = MeasureSpec.getSize(heightMeasureSpec);
        if (hmode == MeasureSpec.AT_MOST) {//wrap_content
            return -1;
        }
        return hsize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//点击控件弹出输入键盘
            requestFocus();
            inputMethodView.setVisibility(VISIBLE);
            return true;
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            inputMethodView.setVisibility(VISIBLE);
        } else {
            inputMethodView.setVisibility(GONE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int width = getWidth() - 2;
        final int height = getHeight() - 2;
        //先画个圆角矩形
        mRoundRect.set(0, 0, width, height);
        canvas.drawRoundRect(mRoundRect, 0, 0, mBorderPaint);
        //画分割线
        for (int i = 1; i < count; i++) {
            final int x = i * size;
            canvas.drawLine(x, 0, x, height, mBorderPaint);
        }
        //画掩盖点,
        // 这是前面定义的变量 private ArrayList<Integer> result;//输入结果保存
        int dotRadius = size / 8;//圆圈占格子的三分之一
        for (int i = 0; i < result.size(); i++) {
            final float x = (float) (size * (i + 0.5));
            final float y = size / 2;
            canvas.drawCircle(x, y, dotRadius, mDotPaint);
        }
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//输入类型为数字
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new MyInputConnection(this, false);
    }

    public void setInputCallBack(InputCallBack inputCallBack) {
        this.inputCallBack = inputCallBack;
    }

    public void clearResult() {
        result.clear();
        invalidate();
    }


    private class MyInputConnection extends BaseInputConnection {
        public MyInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            //这里是接受输入法的文本的，我们只处理数字，所以什么操作都不做
            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            //软键盘的删除键 DEL 无法直接监听，自己发送del事件
            if (beforeLength == 1 && afterLength == 0) {
                return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }


    /**
     * 设置输入键盘view
     *
     * @param inputMethodView
     */
    public void setInputMethodView(PwdInputMethodView inputMethodView) {
        this.inputMethodView = inputMethodView;
        this.inputMethodView.setInputReceiver(new PwdInputMethodView.InputReceiver() {
            @Override
            public void receive(String num) {
                if (num.equals("-1")) {
                    if (!result.isEmpty()) {
                        result.remove(result.size() - 1);
                        invalidate();
                    }
                } else {
                    if (result.size() < count) {
                        result.add(num);
                        invalidate();
                        ensureFinishInput();
                    }
                }


            }
        });
    }

    /**
     * 判断是否输入完成，输入完成后调用callback
     */
    void ensureFinishInput() {
        if (result.size() == count && inputCallBack != null) {//输入完成
            StringBuffer sb = new StringBuffer();
            for (String i : result) {
                sb.append(i);
            }
            inputCallBack.onInputFinish(sb.toString());
        }
    }

    /**
     * 获取输入文字
     *
     * @return
     */
    public String getInputText() {
        if (result.size() == count) {
            StringBuffer sb = new StringBuffer();
            for (String i : result) {
                sb.append(i);
            }
            return sb.toString();
        }
        return null;
    }
}
