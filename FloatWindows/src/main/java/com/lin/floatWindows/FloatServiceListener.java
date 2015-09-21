package com.lin.floatWindows;

import android.view.MotionEvent;

/**
 * FloatService向外部用户传递窗口触摸事件
 */
public interface FloatServiceListener{
    void onClickDown(MotionEvent ev);
    void onLongClick(MotionEvent ev);
    void onMoving(MotionEvent ev);
    void onClickUp(MotionEvent ev);
}
