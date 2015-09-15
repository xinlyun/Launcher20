package com.android.launcher20.FloatView;

import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/8/25.
 */
public interface FloatServiceListener{
    void onClickDown(MotionEvent ev);
    void onLongClick(MotionEvent ev);
    void onMoving(MotionEvent ev);
    void onClickUp(MotionEvent ev);
}
