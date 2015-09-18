package com.android.launcher20.FloatView;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/8/14.
 */
public class FloatWindowGroup extends LinearLayout {


    public FloatWindowGroup(Context context) {
        super(context);
        this.wmParams = wmParams;
        this.mWindowManager = mWindowManager;
        this.mFloatLayout = mFloatLayout;
    }

    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    //    float touy,toux,testX,testY;
    LinearLayout mFloatLayout;
    public void setTitle(WindowManager.LayoutParams wmParams,WindowManager mWindowManager,LinearLayout mFloatLayout){
        this.wmParams = wmParams;
        this.mWindowManager = mWindowManager;
        this.mFloatLayout = mFloatLayout;
    }

    boolean layoutmoving=false;
    boolean layoutout=false;
    MotionEvent downpoint,savePoint;
    //    MotionEvent ge = new MotionEvent();
    long currtime;
    int currx,curry;
    float saveX,saveY;
    float mx=0,my=0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                saveX = ev.getX();
                saveY = ev.getY();
//                downpoint = new MotionEvent();
                currtime = System.currentTimeMillis();
                windowGroupListener.onClickDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if(!layoutmoving) {
//                    System.out.println("posi:"+ev.getRawX()+" "+ev.getX());
                    if (Math.abs(ev.getY() - saveY) > 10 || Math.abs(ev.getX() - saveX) > 10)
                        layoutout = true;
                    if (!layoutout &&
                            Math.abs(ev.getX() - saveX) < 10 && Math.abs(ev.getY() -saveY) < 10 &&
                            System.currentTimeMillis() - currtime > 678)
                    {
                        canMove();
                        layoutmoving = true;
                        windowGroupListener.onLongClick(ev);
                        Log.d("move","can move");
                    }
//                    System.out.println("out?"+layoutout+" X:"+ev.getX() +" "+ downpoint.getX()
//                            +" Y"+(ev.getY() - downpoint.getY())+" time?"+(System.currentTimeMillis() - currtime));
                }else {
                    if(mx!=0||mx!=0)
                    {
                        wmParams.x =wmParams.x+(int)ev.getRawX()-(int)mx;
                        wmParams.y =wmParams.y+(int)ev.getRawY()-(int)my;
                        mx=ev.getRawX();
                        my=ev.getRawY();
//                        wmParams.y =currx+(int)(ev.getY()-my);
//                        wmParams.y = wmParams.y+(int)(ev.getRawY()-savePoint.getRawY());
                    }else {
//                        savePoint=ev;
                        currx=wmParams.x;
                        curry=wmParams.y;
                        mx=ev.getRawX();
                        my=ev.getRawY();
                    }
                    windowGroupListener.onMoving(ev);
                    mWindowManager.updateViewLayout(mFloatLayout,wmParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (layoutmoving)
                    putDown();
                layoutmoving =false;
                layoutout = false;
//                savePoint = null;
                mx=0;
                my=0;
                windowGroupListener.onClickUp(ev);

                break;
        }
//        if(layoutmoving)return false;
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 窗口内缩
     */
    private void canMove(){
        wmParams.x+=18;
        wmParams.y+=18;
        wmParams.width-=36;
        wmParams.height-=36;
//        wmParams.alpha=0.4f;
        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }

    /**
     * 窗口略扩大
     */
    private void putDown(){
        wmParams.x-=18;
        wmParams.y-=18;
        wmParams.width+=36;
        wmParams.height+=36;
//        wmParams.alpha=1f;
        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }
    private WindowGroupListener windowGroupListener;
    public void setWindowGroupListener(WindowGroupListener windowGroupListener){
        this.windowGroupListener = windowGroupListener;
    }

}
