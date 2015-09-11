package com.android.launcher20.FloatView;


import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/7/28.
 */
public class FloatTitleGroup extends LinearLayout {

    public FloatTitleGroup(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_MOVE){
            wmParams.y= (int) ev.getRawY()+wmParams.y -(int)touy;
            wmParams.x= (int) ev.getRawX()+wmParams.x -(int)toux;
            toux = ev.getRawX();
            touy = ev.getRawY();
            wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        }
        else if(ev.getAction()==MotionEvent.ACTION_DOWN){
            toux=ev.getRawX();
            touy=ev.getRawY();
            testX = ev.getRawX();
            testY = ev.getRawY();

        }else if(ev.getAction()==MotionEvent.ACTION_UP){
            wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


            mWindowManager.updateViewLayout(mFloatLayout, wmParams);

        }
        return super.dispatchTouchEvent(ev);
    }
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    float touy,toux,testX,testY;
    LinearLayout mFloatLayout;
    public void setTitle(WindowManager.LayoutParams wmParams,WindowManager mWindowManager,LinearLayout mFloatLayout){
        this.wmParams = wmParams;
        this.mWindowManager = mWindowManager;
        this.mFloatLayout = mFloatLayout;

    }



    OnTouchListener otl = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_MOVE){
                wmParams.y= (int) event.getRawY()+wmParams.y -(int)touy;
                wmParams.x= (int) event.getRawX()+wmParams.x -(int)toux;
                toux = event.getRawX();
                touy = event.getRawY();
                wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }
            else if(event.getAction()==MotionEvent.ACTION_DOWN){
                toux=event.getRawX();
                touy=event.getRawY();
                testX = event.getRawX();
                testY = event.getRawY();

            }else if(event.getAction()==MotionEvent.ACTION_UP){
                wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                if(Math.abs(event.getRawX()-testX)<5&&Math.abs(event.getRawY()-testY)<5) {
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);

                    return true;
                }

                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }
            return true;
        }
    };
}
