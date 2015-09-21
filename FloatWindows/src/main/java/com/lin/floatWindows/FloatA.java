package com.lin.floatWindows;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

//import com.android.launcher20.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;



/**
 *
 */
public class FloatA
        extends Thread
        implements FLoatAStatusListener,WindowsMovingListener{
//    private FloatService mfloatService=null;
    private FloatWindowsView mfloatWindowsView;
    private static Activity context;
    private int where = 0;
    private FloatA floatA1 = null;
    private FloatWindowsView other = null;
    private  View ll;
    private ImageView mImage;
    private FloatWindowsView dialog;
    private int width=1080,height=1920;

//    /**
//     * 获得FloatService对象，并开始实际工作
//     */
//    private ServiceConnection connection =new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            mfloatService = ((FloatService.MyBinder)service).getService();
//
//            onCreate();
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//        }
//    };
    public FloatA(){

    }

    public void testWin(){
        WindowManager wm = context.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }
    public void setContext(Activity context){
        this.context = context;
    }
//    public FloatA(Activity context1){
//        this.context = context1;
//        WindowManager wm = context.getWindowManager();
//        width = wm.getDefaultDisplay().getWidth();
//        height = wm.getDefaultDisplay().getHeight();
//    }
//    public FloatA(Activity context1,FloatA floatA){
//        this.context = context1;
//        this.floatA1 = floatA;
//        if(floatA!=null)other = floatA.getFloatWindowsView();
//        WindowManager wm = context.getWindowManager();
//        width = wm.getDefaultDisplay().getWidth();
//        height = wm.getDefaultDisplay().getHeight();
//    }
//    public FloatService getFloatService(){
//        return this.mfloatService;
//    }
//    public FloatA setFloatService(FloatService floatService){
//        mfloatService = floatService;
//        return this;
//    }

    public FloatWindowsView getFloatWindowsView(){
        return  this.mfloatWindowsView;
    }
    protected void showDialog(){
        dialog =  new FloatWindowsView(getContext());
        dialog.showADialog();
    }
    protected void closeDialog(){
        dialog.close();
        dialog = null;
    }
    @Override
    public void run() {
        super.run();
//        Looper.prepare();
//        context.bindService(new Intent(context, FloatService.class), connection, context.BIND_AUTO_CREATE);
//        else onCreate();
//        onCreate();
//        Looper.loop();
    }



    protected void onCreate(){
//        setConentView(R.layout.myfloat_layout);
//        mfloatService.addStatusListener(this);
        FloatManager.getContext(context).addStatusListener(this);
        StopIt();
    }




    protected void setConentView(int layout){
//        mfloatWindowsView = mfloatService.replacetry(0,R.layout.map_title_layout,layout,context);
//        mfloatWindowsView = mfloatService.replace(where,0,R.layout.map_title_layout,layout,context,this.iBinder);
        mfloatWindowsView = FloatManager.getContext(context).replace(where,0,R.layout.map_title_layout,layout);
        mfloatWindowsView.addWindowsMovingListener(this);
        ll = mfloatWindowsView.getWindowView();
        mImage = mfloatWindowsView.getBottomImage(1);
//        mfloatWindowsView = new FloatWindowsView(context);
//        if(other!=null)mfloatWindowsView.createParames(other.getWmParams());
//        else
//            mfloatWindowsView.createParames(where);
//        mfloatWindowsView.createFloatView(R.layout.map_title_layout,layout);
//        if(other!=null)other.close();
    }


    public int getWhere(){
        return where;
    }

    public void onStart(int where){
        this.where = where;
        this.start();
        this.onCreate();

//        while(this.isAlive()){}
//        onCreate();
    }


    protected void inputAble(boolean in){
        mfloatWindowsView.inputStyle(in);
    }

    protected View findViewById(int Id){
//        View ll = mfloatWindowsView.getWindowView();
        return ll.findViewById(Id);
    }

    protected void StopIt(){
//        mfloatWindowsView.close();
        if(this.floatA1!=null){
            this.floatA1.finish();
//            this.floatA1.interrupt();
            this.floatA1 = null;
        }

    }
    public void stopm(){
        mfloatWindowsView.close();
    }

    public void finish(){

//        mfloatService=null;

        mfloatWindowsView=null;
    }

    protected void dismiss(){

    }


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


    @Override
    public void onClickDown(MotionEvent ev) {

    }
    @Override
    public void onLongClick(MotionEvent ev) {


    }

    @Override
    public void onMoving(MotionEvent ev) {
        if(ev.getRawY()<200){
            if(ev.getRawX()>width/3&&ev.getRawX()<width*2/3)
                mImage.setX(0);

        }else
            mImage.setX(1080);

    }

    @Override
    public void onClickUp(MotionEvent ev) {
//        mImage.setImageBitmap(null);
//        ll.setX(x);
//        if(bitmap!=null)bitmap.recycle();
        mImage.setX(1080);

    }

}
