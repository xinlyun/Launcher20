package com.android.launcher20.FloatView;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;

import com.android.launcher20.R;

//import com.lin.floatmaptest.R;

/**
 * Created by root on 15-8-11.
 */
public class FloatA extends Thread implements FLoatAStatusListener{
    private FloatService mfloatService=null;
    private FloatWindowsView mfloatWindowsView;
    private static Activity context;
    private int where = 0;
    private ServiceConnection connection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mfloatService = ((FloatService.MyBinder)service).getService();

            onCreate();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private FloatA floatA1 = null;
    FloatWindowsView other = null;
    IBinder iBinder = null;
    public FloatA(Activity context1,IBinder iBinder){
        this.context = context1;
        this.iBinder = iBinder;
    }
    public FloatA(Activity context1,FloatA floatA){
        this.context = context1;
        this.floatA1 = floatA;
        this.iBinder = floatA.getiBinder();
        if(floatA!=null)other = floatA.getFloatWindowsView();
    }
    public FloatService getFloatService(){
        return this.mfloatService;
    }
    public FloatA setFloatService(FloatService floatService){
        mfloatService = floatService;
        return this;
    }
    private IBinder getiBinder(){
        return this.iBinder;
    }

    public FloatWindowsView getFloatWindowsView(){
        return  this.mfloatWindowsView;
    }
    private FloatWindowsView dialog;
    protected void showDialog(){
//        dialog=mfloatService.showDialot();
        dialog =  new FloatWindowsView(getContext());
//        FloatWindowsView f= new FloatWindowsView(getContext());
//        f.showADialog();
        dialog.showADialog();
    }
    protected void closeDialog(){
        dialog.close();
    }
    @Override
    public void run() {
        super.run();
//        Looper.prepare();
        context.bindService(new Intent(context, FloatService.class), connection, context.BIND_AUTO_CREATE);
//        else onCreate();
//        onCreate();
//        Looper.loop();
    }



    protected void onCreate(){
//        setConentView(R.layout.myfloat_layout);
        mfloatService.addStatusListener(this);
        StopIt();
    }




    protected void setConentView(int layout){
//        mfloatWindowsView = mfloatService.replacetry(0,R.layout.map_title_layout,layout,context);
        mfloatWindowsView = mfloatService.replace(where,0,R.layout.map_title_layout,layout,context,this.iBinder);


//        mfloatWindowsView = new FloatWindowsView(context);
//        if(other!=null)mfloatWindowsView.createParames(other.getWmParams());
//        else
//            mfloatWindowsView.createParames(where);
//        mfloatWindowsView.createFloatView(R.layout.map_title_layout,layout);
//        if(other!=null)other.close();
    }

//    public FloatA onStrat(Activity context1) {
//        context = context1;
//        FloatA fa = new FloatA();
//        fa.start();
//        return fa;
//    }


    public void onStart(int where){
        this.where = where;
        this.start();


//        while(this.isAlive()){}
//        onCreate();
    }


    protected void inputAble(boolean in){
        mfloatWindowsView.inputStyle(in);
    }

    protected View findViewById(int Id){
        View ll = mfloatWindowsView.getWindowView();
        return ll.findViewById(Id);
    }

    protected void StopIt(){
//        mfloatWindowsView.close();
        this.floatA1 = null;

    }
    public void stopm(){
        mfloatWindowsView.close();
    }



    protected void dismiss(){

    }

//    protected void startOther(FloatA fa){
//        fa = onStrat(context);
//        this.onStop();
//        this.stop();
//    }

    protected Context getContext(){
        return this.context;
    }
    protected Application getApplication(){
        return context.getApplication();
    }
    protected int getX(){
        return mfloatWindowsView.getWmParams().x;
    }
    protected int getY(){
        return mfloatWindowsView.getWmParams().y;
    }


    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}
