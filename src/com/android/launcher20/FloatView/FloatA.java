package com.android.launcher20.FloatView;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.launcher20.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

//import com.lin.floatmaptest.R;

/**
 *
 */
public class FloatA extends Thread implements FLoatAStatusListener,WindowsMovingListener{
    private FloatService mfloatService=null;
    private FloatWindowsView mfloatWindowsView;
    private static Activity context;
    private int where = 0;
    private FloatA floatA1 = null;
    private FloatWindowsView other = null;
    private IBinder iBinder = null;
    private  View ll;
    private ImageView mImage;

    /**
     * 获得FloatService对象，并开始实际工作
     */
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
        mfloatWindowsView.addWindowsMovingListener(this);
        ll = mfloatWindowsView.getWindowView();
        mImage = mfloatWindowsView.getBottomImage();
//        mfloatWindowsView = new FloatWindowsView(context);
//        if(other!=null)mfloatWindowsView.createParames(other.getWmParams());
//        else
//            mfloatWindowsView.createParames(where);
//        mfloatWindowsView.createFloatView(R.layout.map_title_layout,layout);
//        if(other!=null)other.close();
    }




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
//        View ll = mfloatWindowsView.getWindowView();
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

    }

    @Override
    public void onClickUp(MotionEvent ev) {
//        mImage.setImageBitmap(null);
//        ll.setX(x);
//        if(bitmap!=null)bitmap.recycle();

    }

    /**

     * 截屏

     * @param activity

     * @return

     */
    public Bitmap captureScreen(Activity activity) {

        // 获取屏幕大小：

        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager WM = (WindowManager) activity

                .getSystemService(Context.WINDOW_SERVICE);

        Display display = WM.getDefaultDisplay();

        display.getMetrics(metrics);

        int height = metrics.heightPixels; // 屏幕高

        int width = metrics.widthPixels; // 屏幕的宽

        // 获取显示方式

        int pixelformat = display.getPixelFormat();

        PixelFormat localPixelFormat1 = new PixelFormat();

        PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);

        int deepth = localPixelFormat1.bytesPerPixel;// 位深

        byte[] piex = new byte[height * width * deepth];

        try {

            Runtime.getRuntime().exec(

                    new String[]{"/system/bin/su", "-c",

                            "chmod 777 /dev/graphics/fb0"});
            Runtime.getRuntime().exec(

                    new String[]{"/system/bin/su", "-c",

                            "chmod","777", "/dev/graphics/fb0"});



        } catch (IOException e) {

            e.printStackTrace();

        }

        try {

            // 获取fb0数据输入流

            InputStream stream = new FileInputStream(new File(

                    "/dev/graphics/fb0"));

            DataInputStream dStream = new DataInputStream(stream);

            dStream.readFully(piex);

        } catch (Exception e) {

            e.printStackTrace();

        }

        // 保存图片

        int[] colors = new int[height * width];

        for (int m = 0; m < colors.length; m++) {

            int r = (piex[m * 4] & 0xFF);

            int g = (piex[m * 4 + 1] & 0xFF);

            int b = (piex[m * 4 + 2] & 0xFF);

            int a = (piex[m * 4 + 3] & 0xFF);

            colors[m] = (a << 24) + (r << 16) + (g << 8) + b;

        }

        // piex生成Bitmap

        Bitmap bitmap= Bitmap.createBitmap(colors, width, height,
                Bitmap.Config.RGB_565);

        return bitmap;

    }


}
