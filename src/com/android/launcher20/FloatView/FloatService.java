package com.android.launcher20.FloatView;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

//import com.amap.api.maps.AMap;
//import com.amap.api.maps.MapView;

public class FloatService extends Service
//        implements AMapNaviViewListener
{
    private boolean isMiss=false;
    ArrayList<FloatWindowsView> floatViews;
    public FloatService() {
    }

    public boolean isMiss(){
        return isMiss;
    }
    public void onDismiss(){
        Log.d("Service", "dismiss");
        if(floatViews.size()>0) {isMiss=true;
        floatViews.get(0).dismiss();}
    }
    public void reShow(){

        if(floatViews.size()>0){isMiss = false;floatViews.get(0).reshow();}
    }


    @Override
    public void onCreate() {
        super.onCreate();
        floatViews = new ArrayList<FloatWindowsView>();
    }

    public FloatWindowsView addNewFloatView(int mFloatTitle,int mFloatWindow,Context context){
        FloatWindowsView f = new FloatWindowsView(context);
        f.createParames(0);
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

    public FloatWindowsView replace(int posi,int Tag,int mFloatTitle,int mFloatWindow,Context context1){
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
            f.setWindowsMovingListener(windowsMovingListener);
            fwv.close();
            return f;}
        else {
            FloatWindowsView f = new FloatWindowsView(context1);
            f.createParames(posi);
            f.createFloatView(mFloatTitle,mFloatWindow);
            f.setTag(Tag);
            floatViews.add(Tag,f);
            f.setWindowsMovingListener(windowsMovingListener);
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
        return replace(Tag,0,mFloatTitle,mFloatWindow,FloatService.this);
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

    public void setPosition(float x,float y){
        if(floatViews.size()>0)floatViews.get(0).setPosition(x,y);
    }
}
