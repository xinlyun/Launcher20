package com.android.launcher20.FloatView;

import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/8/25.
 */
public interface FloatMaCint {
    void onLongClick(float x,float y);
    void onMoving(float x,float y);
    void onClickUp();
}


interface WindowsMovingListener{
    void onClickDown(MotionEvent ev);
    void onLongClick(MotionEvent ev);
    void onMoving(MotionEvent ev);
    void onClickUp(MotionEvent ev);
}
interface WindowGroupListener{
    void onClickDown(MotionEvent ev);
    void onLongClick(MotionEvent ev);
    void onMoving(MotionEvent ev);
    void onClickUp(MotionEvent ev);
}
