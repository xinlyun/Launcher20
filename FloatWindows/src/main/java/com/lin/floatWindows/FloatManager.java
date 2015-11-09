package com.lin.floatWindows;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by lin on 15-9-19.
 */
public class FloatManager {
    private boolean isMiss=false;
    ArrayList<FloatWindowsView> floatViews;
    private ArrayList<FLoatAStatusListener> floatAStatusListeners;
    private FloatA floatA;
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
        for(FLoatAStatusListener f:floatAStatusListeners){
            f.onPause();
        }

    }

    /**
     * 将隐藏的窗口重现
     */
    public void reShow(){
        if(floatViews.size()>0){
            isMiss = false;floatViews.get(0).reshow();
            for(FLoatAStatusListener f:floatAStatusListeners){
                f.onResume();
            }
        }
    }

    private static FloatManager floatManager;
    private static Context context;
    private FloatManager(Context context1){
        this.context = context1;
        floatAStatusListeners = new ArrayList<>();
        floatViews = new ArrayList<FloatWindowsView>();
    }

    public static FloatManager getContext(Context context1){
        if(floatManager==null) {
            floatManager = new FloatManager(context1);
        }
//        context  = context1;
        return floatManager;
    }


    public FloatWindowsView addNewFloatView(int mFloatTitle,int mFloatWindow){
        FloatWindowsView f = new FloatWindowsView(context);
        f.createParames(0);
        f.createFloatView(mFloatTitle, mFloatWindow);
        f.setTag(floatViews.size());

        floatViews.add(f);
        return f;
    }

//    public FloatWindowsView showDialot(){
//        FloatWindowsView f= new FloatWindowsView(FloatService.this);
//        f.showADialog();
//        return f;
//    }

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
    public FloatWindowsView replace(int posi,int Tag,int mFloatTitle, int mFloatWindow){
        if(floatViews.size()>Tag){
            FloatWindowsView fwv =floatViews.remove(Tag);
//            FloatWindowsView fwv = floatViews.get(Tag);
            FloatWindowsView f = new FloatWindowsView(context);
            f.createParames(fwv.getWmParams());
            f.createFloatView(mFloatTitle, mFloatWindow);
            f.setTag(Tag);
//        f.setWinData(fwv.getWinData());
//        floatViews.remove(Tag);
            floatViews.add(Tag, f);
            f.refresh();
            f.addWindowsMovingListener(windowsMovingListener);

            fwv.waitAndclose();

            return f;}
        else {
            FloatWindowsView f = new FloatWindowsView(context);
            f.createParames(posi);
            f.createFloatView(mFloatTitle,mFloatWindow);
            f.setTag(Tag);
            floatViews.add(Tag,f);
            f.addWindowsMovingListener(windowsMovingListener);
            return f;
        }
    }


    public void startWindows(Class<?> tClass,int Tag){
        try {
            floatA = (FloatA) tClass.newInstance();
            floatA.setContext((Activity) context);
            floatA.testWin();
            floatA.onStart(Tag);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void startWindows(FloatA floatA1,Class<?> tClass){
        try {
            floatA = (FloatA) tClass.newInstance();
            floatA.setContext((Activity) context);
            floatA.testWin();
            floatA.onStart(floatA1.getWhere());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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

    public FloatWindowsView replacetry(int Tag,int mFloatTitle,int mFloatWindow){
        if(floatViews.size()==0)return addNewFloatView(mFloatTitle, mFloatWindow);
        else {
            floatViews.get(Tag).createNew(mFloatTitle, mFloatWindow);
            return floatViews.get(Tag);
        }
    }




    public FloatWindowsView replace(int Tag,int mFloatTitle,int mFloatWindow){
        return replace(Tag,0,mFloatTitle,mFloatWindow);
    }

//    public FloatWindowsView addNewFloatView(int mFloatTitle,int mFloatWindow){
//        return addNewFloatView(mFloatTitle,mFloatWindow,FloatService.this);
//    }

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
