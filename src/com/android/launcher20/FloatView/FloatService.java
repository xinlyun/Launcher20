package com.android.launcher20.FloatView;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;


public class FloatService extends Service
//        implements AMapNaviViewListener
{
    private boolean isMiss=false;
    ArrayList<FloatWindowsView> floatViews;
    private ArrayList<FLoatAStatusListener> floatAStatusListeners;
    public FloatService() {
    }

    /**
     *
     * @return 窗口是否隐藏
     */
    public boolean isMiss(){
        return isMiss;
    }

    /**
     * 对窗口进行隐藏操作
     */
    public void onDismiss(){
        Log.d("Service", "dismiss");
        if(floatViews.size()>0) {isMiss=true;
        floatViews.get(0).dismiss();}
    }

    /**
     * 将隐藏的窗口重现
     */
    public void reShow(){

        if(floatViews.size()>0){isMiss = false;floatViews.get(0).reshow();}
    }


    @Override
    public void onCreate() {
        super.onCreate();
        floatAStatusListeners = new ArrayList<>();
        floatViews = new ArrayList<FloatWindowsView>();
    }

    public FloatWindowsView addNewFloatView(int mFloatTitle,int mFloatWindow,Context context){
        FloatWindowsView f = new FloatWindowsView(context);
        f.createParames(0,null);
        f.createFloatView(mFloatTitle, mFloatWindow);
        f.setTag(floatViews.size());

        floatViews.add(f);
        return f;
    }

    public FloatWindowsView showDialot(){
        FloatWindowsView f= new FloatWindowsView(FloatService.this);
        f.showADialog();
        return f;
    }

    /**
     * 添加窗口的状态修改，让每个窗口能接受状态改变
     * @param fLoatAStatusListener
     */
    public void addStatusListener(FLoatAStatusListener fLoatAStatusListener){
        floatAStatusListeners.add(fLoatAStatusListener);
    }

    /**
     * 窗口重启
     */
    public void onResume(){
        for(FLoatAStatusListener f:floatAStatusListeners){
            f.onResume();
        }
    }

    /**
     * 窗口暂停
     */
    public void onPause(){
        for(FLoatAStatusListener f:floatAStatusListeners){
            f.onPause();
        }
    }

    /**
     * 窗口间的切换，包括窗口生成也由此完成
     * @return 新生成的窗口
     */
    public FloatWindowsView replace(int posi,int Tag,int mFloatTitle,int mFloatWindow,Context context1,IBinder iBinder){
        if(floatViews.size()>Tag){
            FloatWindowsView fwv = floatViews.get(Tag);
            FloatWindowsView f = new FloatWindowsView(context1);
            f.createParames(fwv.getWmParams());
            f.createFloatView(mFloatTitle, mFloatWindow);
            f.setTag(Tag);
//        f.setWinData(fwv.getWinData());
//        floatViews.remove(Tag);
            floatViews.add(Tag, f);
            f.refresh();
            f.addWindowsMovingListener(windowsMovingListener);

            fwv.close();
            return f;}
        else {
            FloatWindowsView f = new FloatWindowsView(context1);
            f.createParames(posi,iBinder);
            f.createFloatView(mFloatTitle,mFloatWindow);
            f.setTag(Tag);
            floatViews.add(Tag,f);
            f.addWindowsMovingListener(windowsMovingListener);
            return f;
        }
    }




    private WindowsMovingListener windowsMovingListener = new WindowsMovingListener() {
        @Override
        public void onClickDown(MotionEvent ev) {
            if(floatServiceListener!=null)
                floatServiceListener.onClickDown(ev);
        }

        @Override
        public void onLongClick(MotionEvent ev) {
            if(floatServiceListener!=null)
                floatServiceListener.onLongClick(ev);
        }

        @Override
        public void onMoving(MotionEvent ev) {
            if(floatServiceListener!=null)
                floatServiceListener.onMoving(ev);
        }

        @Override
        public void onClickUp(MotionEvent ev) {
            if(floatServiceListener!=null)
                floatServiceListener.onClickUp(ev);
        }
    };


    public void cnTouch(boolean flag){
        floatViews.get(0).cnTouch(flag);
    }

    public FloatWindowsView replacetry(int Tag,int mFloatTitle,int mFloatWindow,Context context1){
        if(floatViews.size()==0)return addNewFloatView(mFloatTitle, mFloatWindow,context1);
        else {
            floatViews.get(Tag).createNew(mFloatTitle, mFloatWindow);
            return floatViews.get(Tag);
        }
    }




    public FloatWindowsView replace(int Tag,int mFloatTitle,int mFloatWindow){
        return replace(Tag,0,mFloatTitle,mFloatWindow,FloatService.this,null);
    }

    public FloatWindowsView addNewFloatView(int mFloatTitle,int mFloatWindow){
        return addNewFloatView(mFloatTitle,mFloatWindow,FloatService.this);
    }

    public boolean remove(int tag){
        try{
            FloatWindowsView f = floatViews.remove(tag);
            f.close();
            for(int i =tag;i<floatViews.size();i++)floatViews.get(i).setTag(floatViews.get(i).getTag()-1);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public int getWidgetX(){
        return floatViews.get(0).getWmParams().x+floatViews.get(0).getWmParams().width*4/5;
    }
    public int getWidgetY(){
        return floatViews.get(0).getWmParams().y+floatViews.get(0).getWmParams().height/4;
    }



    @Override
    public IBinder onBind(Intent intent)
    {
        return new MyBinder();
    }
    @Override
    public void onDestroy() {
        for(FloatWindowsView floatWindowsView:floatViews){
            floatViews.remove(floatWindowsView);
            floatWindowsView.close();
        }
        super.onDestroy();
    }



    public class MyBinder extends Binder{
        public FloatService getService(){
            return FloatService.this;
        }
    }
    private FloatServiceListener floatServiceListener=null;
    public void setFloatServiceListener(FloatServiceListener floatServiceListener){
        this.floatServiceListener = floatServiceListener;
    }
    public void close(){
//        floatViews.get(0).close();
        if(floatViews.size()>0)floatViews.remove(0).close();
    }
    public void setFloatWindowPosition(int position){
//        floatViews.get(0)
        if(floatViews.size()>0)floatViews.get(0).MovePosiXY(position);
    }
    /**
     * 随时移动窗口位置
     * 主要用在窗口的被动移动上
     * @param x
     * @param y
     */
    public void setPosition(float x,float y){
        if(floatViews.size()>0)floatViews.get(0).setPosition(x,y);
    }
}
