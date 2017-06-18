package cn.edu.nuc.recommendme.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2017/6/18 0018.
 */

public class ToggleButton extends View {

    //定义开关背景图片
    private Bitmap toggleBackground;
    //定义滑动图片
    private Bitmap slideImage;

    public ToggleButton(Context context) {
        super(context);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //定义状态常量
    public enum ToggleState{
        west, east
    }

    //定义变量，记录当前toggleButton的state（状态）
    private ToggleState mState;

    /**
     * 设置toggleButton的状态
     * */
    public void setToggleState(ToggleState mState){
        this.mState = mState;
    }

    /**
     * 设置当前开关控件背景图片
     * */
    public void setToggleBackgroundResource(int resId){
        toggleBackground = BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 设置当前开关控件滑动图片
     * */
    public void setSlideImage(int resId){
        slideImage = BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 在该方法中设置view的宽高
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(toggleBackground.getWidth(), toggleBackground.getHeight());
    }

    //定义是否处于滑动状态
    private boolean isSliding = false;
    //定义当前按下的x坐标
    private float currentX;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.绘制背景图片
        canvas.drawBitmap(toggleBackground, 0, 0, null);
        //2.绘制滑动图片
        if (isSliding){

            //这个currentX是 滑动后currentX-按下时的currentX
            float left = currentX - slideImage.getWidth()/2;
            if (left < 0){
                //限制左边边界
                left = 0;
            }

            if (left > (toggleBackground.getWidth() - slideImage.getWidth())){
                //限制右边界
                left = toggleBackground.getWidth() - slideImage.getWidth();
            }

            //讲slideImage设置到背景
            canvas.drawBitmap(slideImage, left, 0, null);
        } else {
            //处理抬起后的处理事件
            if (mState == ToggleState.west){
                canvas.drawBitmap(slideImage, toggleBackground.getWidth() - slideImage.getWidth(), 0, null);

            }else {
                canvas.drawBitmap(slideImage, 0, 0, null);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isSliding = true;
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                isSliding = true;
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                isSliding = false;
                if (currentX < getWidth()/2){
                    if (mState != ToggleState.east){
                        mState = ToggleState.east;
                        if(listener != null){
                            listener.OnToggleStateChange(mState);
                        }
                    }
                }else {
                    if (mState != ToggleState.west){
                        mState = ToggleState.west;
                        if (listener != null){
                            listener.OnToggleStateChange(mState);
                        }
                    }
                }
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 定义接口回调，暴露自己的状态
     *
     * */
    public interface OnToggleStateChangeListener{
        void OnToggleStateChange(ToggleState mState);
    }


    /**
     * 设置状态改变监听器
     * */
    private OnToggleStateChangeListener listener;

    public void setOnToggleStateChangeListener(OnToggleStateChangeListener listener){
        this.listener = listener;
    }
}
