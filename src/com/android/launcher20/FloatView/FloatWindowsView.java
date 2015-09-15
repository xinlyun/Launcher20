package com.android.launcher20.FloatView;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.launcher20.R;

//import com.lin.floatmaptest.R;


/**
 * 封装好的浮动窗口类
 * 实质时对浮动窗口控制的集合类，完成窗口移动/切换/生成等工作
 */
public class FloatWindowsView {
    private LayoutParams wmParams;
    private WindowManager mWindowManager;
    private Context context;
    private int width,height,screenWidth;
    private FloatTitleGroup mTitleOne;
    private FloatWindowGroup mWindowOne;
    private LinearLayout mFloatLayout,mTitle,mWindow;
    private View mTitleLayout,mWindowLayout;
    private ImageView mFloatBtn;
    private Button btn;
    private int tag = -1;
    private boolean flagClick = false;
    private int saveX,saveHeight;
    private float toux,touy,testX,testY;
    private long timer=0;
    //------------------------------------
    private PosiAWH[] posiAWHs ;


    /**
     * 基本的构造方法，从这里获取屏幕宽高并初始化一些内容
     * @param context
     */
    public FloatWindowsView(Context context){
        this.context = context;
//        mWindowManager = (WindowManager) context.getApplicationContext()
//                .getSystemService(Context.WINDOW_SERVICE);
        mWindowManager = ((Activity)context).getWindowManager();
        width =mWindowManager.getDefaultDisplay().getWidth();
        screenWidth=width;
        height = mWindowManager.getDefaultDisplay().getHeight();
        posiAWHs = new PosiAWH[]{
                new PosiAWH((int)(width/40-5), (int) (35),(int)(width*38/40+5),(int)(height/3+60)),
                new PosiAWH(width/40-5,height/4-70,width*38/40+5,height/3+60),
                new PosiAWH(width/40-5,height/2-165,width*38/40+5,height/3+60),
                new PosiAWH(width/40-5,height*3/4-260,width*38/40+5,height/3+60),
                new PosiAWH(width/2+30,10,width/2-20,height/3-20)
        };

//        System.out.println("~~~~~~~~~~~~~~n");
        mTitleOne = new FloatTitleGroup(context);
        mWindowOne = new FloatWindowGroup(context);

    }

    public void MovePosiXY(int position){
        wmParams.x = posiAWHs[position].x;
        wmParams.y = posiAWHs[position].y;
        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }

    /**
     * 窗口聚焦情况，前者为可超出屏幕范围的窗口状态，后者为输入时的聚焦状态
     * @param in
     */
    public void inputStyle(boolean in){
        if(!in){
            wmParams.flags =
                    LayoutParams.FLAG_NOT_FOCUSABLE |
                            LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        }
        else{
            wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;

        }
        mWindowManager.updateViewLayout(mFloatLayout, wmParams);

    }

    /**
     * 可用于继承另一个窗口的LayoutParams，方便替换
     * @param params
     */
    public void createParames(LayoutParams params){
        wmParams = new LayoutParams();
        wmParams.type = LayoutParams.LAST_APPLICATION_WINDOW;
//        wmParams.token = params.token;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;

        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）

        wmParams.memoryType = LayoutParams.MEMORY_TYPE_HARDWARE;

        wmParams.flags =
//                LayoutParams.FLAG_SCALED
//          LayoutParams.FLAG_NOT_TOUCH_MODAL
                LayoutParams.FLAG_NOT_FOCUSABLE
//          LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = params.x;
        wmParams.y = params.y;

        // 设置悬浮窗口长宽数据
        wmParams.width = params.width;
        wmParams.height =params.height;
    }

    /**
     * 根据标签对LayoutParams进行初始化
     * @param Tag
     */
    public void createParames(int Tag,IBinder iBinder){
        System.out.println("~~~~~~~~~~~~~~c");
        wmParams = new LayoutParams();
        //获取WindowManagerImpl.CompatModeWrapper
//        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        //设置window type
        wmParams.type = LayoutParams.LAST_APPLICATION_WINDOW;
//        wmParams.token = iBinder;

        wmParams.memoryType = LayoutParams.MEMORY_TYPE_HARDWARE;

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags =
//                LayoutParams.FLAG_SCALED
//          LayoutParams.FLAG_NOT_TOUCH_MODAL
                LayoutParams.FLAG_NOT_FOCUSABLE
//          LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
//        wmParams.x = 0;
//        wmParams.y = 0;




//        wmParams.x = (int)(width/40-5);
//        wmParams.y = (int) (height/3);
        wmParams.x = posiAWHs[Tag].x;
        wmParams.y = posiAWHs[Tag].y;
//        wmParams.x = 0 ;
//        wmParams.y =


        // 设置悬浮窗口长宽数据
//        wmParams.width = (int) (width*0.867);
//        wmParams.height =(int) (height*0.675);
//        wmParams.height =(int) (height*0.867);
//        wmParams.width = (int)(width*38/40+5);
//
//
//        wmParams.height = (int)(width*2/3);
        wmParams.width = posiAWHs[Tag].width;
        wmParams.height = posiAWHs[Tag].height;
    }



    float lengthOpen,catterOpen;
    /**
     * 创建浮动窗口,分别生成title栏和window栏
     * @param mFloatTitleId  title栏id
     * @param mFloatWindowId    窗口栏id
     */
    public void createFloatView(final int mFloatTitleId,final int mFloatWindowId)
    {
//
        //设置悬浮窗口长宽数据
//        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.myfloat_layout, null);
        //添加mFloatLayout

//        mWindowManager.addView(mFloatLayout, wmParams);
        mTitle = (LinearLayout) mFloatLayout.findViewById(R.id.title_layout);
        mWindow = (LinearLayout) mFloatLayout.findViewById(R.id.window_layout);
        mFloatBtn = (ImageView) mFloatLayout.findViewById(R.id.float_id_m);
        setmFloatBtn();

        mTitleLayout = inflater.inflate(mFloatTitleId,null);
        mWindowLayout = inflater.inflate(mFloatWindowId,null);


        mTitleOne.addView(mTitleLayout);
        mWindowOne.addView(mWindowLayout);
        mTitleOne.setTitle(wmParams, mWindowManager, mFloatLayout);
        mWindowOne.setTitle(wmParams, mWindowManager, mFloatLayout);
        mWindowOne.setWindowGroupListener(windowGroupListener);
//        mWindowOne.setAnimation(createAnimation());

        mTitle.addView(mTitleOne);
        mWindow.addView(mWindowOne);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });



        wmParams.flags =

                LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS;

//        mWindowManager.updateViewLayout(mFloatLayout, wmParams);

//        wmParams.

//        mFloatLayout.setAnimation(createAnimation());
        wmParams.windowAnimations = R.anim.openanim;
        mWindowManager.addView(mFloatLayout, wmParams);
//        mFloatWview.loadUrl("http://www.baidu.com");
        mFloatLayout.setX(0-width);
        android.os.Handler handler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what ==0){
                    catterOpen = 0-msg.arg1;
                    Log.d("bili",""+(1-catterOpen/lengthOpen));
                    mFloatLayout.setX((float) msg.arg1);
                    mFloatLayout.setAlpha(1-catterOpen/lengthOpen);
                }
                else {
                    Log.d("x",""+mFloatLayout.getX());
                    mFloatLayout.setAlpha(1);
                    wmParams.x = (int)(width/40-5);
                    wmParams.width =(int)(width*38/40+5);
//                    mWindowManager.updateViewLayout(mFloatLayout,wmParams);
                }
            }

        };
        lengthOpen = 0-mFloatLayout.getX();

        new Openning(handler,(int)mFloatLayout.getX(),0).start();
    }


    public void createNew(int mFloatTitleId,final int mFloatWindowId){
        mWindow.removeView(mWindowLayout);
        mTitle.removeView(mTitleOne);
        mTitleOne.removeView(mTitleLayout);

        mWindowManager.removeView(mFloatLayout);




        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.myfloat_layout, null);
        //添加mFloatLayout

//        mWindowManager.addView(mFloatLayout, wmParams);
        mTitle = (LinearLayout) mFloatLayout.findViewById(R.id.title_layout);
        mWindow = (LinearLayout) mFloatLayout.findViewById(R.id.window_layout);
        mFloatBtn = (ImageView) mFloatLayout.findViewById(R.id.float_id_m);
        setmFloatBtn();

        mTitleLayout = inflater.inflate(mFloatTitleId,null);
        mWindowLayout = inflater.inflate(mFloatWindowId,null);



        mTitleOne.addView(mTitleLayout);
        mTitleOne.setTitle(wmParams,mWindowManager,mFloatLayout);
        mTitle.addView(mTitleOne);
        mWindow.addView(mWindowLayout);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        mTitleLayout.setOnTouchListener(otl);
        mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
//        btn= (Button) mTitleLayout.findViewById(R.id.btn_seturl);
//        btn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                return false;
//            }
//        });


        wmParams.flags =

                LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        mWindowManager.addView(mFloatLayout, wmParams);

    }





    /**
     *
     * @return 获得title的view对象,通过该对象获取title中的子视图
     */

    public View getTitleView(){
        return mTitleLayout;
    }

    /**
     *
     * @return 获得window的view对象，通过该对象获得window
     */
    public View getWindowView(){
        return mWindowLayout;
    }


    public void showADialog(){
        wmParams = new LayoutParams();
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags =
                LayoutParams.FLAG_NOT_FOCUSABLE
        ;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x=width/2-136;
        wmParams.y = height/2-160;
        wmParams.width = 272;
        wmParams.height = 320;
        LayoutInflater inflater = LayoutInflater.from(context);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.myfloat_layout, null);
        //添加mFloatLayout

//        mWindowManager.addView(mFloatLayout, wmParams);
        mTitle = (LinearLayout) mFloatLayout.findViewById(R.id.title_layout);
        mWindow = (LinearLayout) mFloatLayout.findViewById(R.id.window_layout);
        mFloatBtn = (ImageView) mFloatLayout.findViewById(R.id.float_id_m);
        setmFloatBtn();

        mTitleLayout = inflater.inflate(R.layout.map_title_layout,null);


        mWindowLayout = inflater.inflate(R.layout.activity_main,null);


        mTitleOne.addView(mTitleLayout);
        mTitleOne.setTitle(wmParams, mWindowManager, mFloatLayout);
        mTitle.addView(mTitleOne);
        mWindow.addView(mWindowLayout);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });



        wmParams.flags =

                LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS;

//        mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        mWindowManager.addView(mFloatLayout, wmParams);
    }


    private void setmFloatBtn() {
        mFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        mFloatBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!flagClick) {
                    saveX = wmParams.x;
                    saveHeight = wmParams.height;
                    wmParams.flags =
                            LayoutParams.FLAG_NOT_FOCUSABLE |
                                    LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                    wmParams.x = screenWidth - 86;
                    wmParams.height = 120;
                    mFloatBtn.setImageResource(R.drawable.fanhui);
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    flagClick = true;
                } else if (flagClick) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touy = event.getRawY();
                            testX = event.getRawX();
                            testY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (Math.abs(event.getRawY() - testY) <= 5) {
                                wmParams.x = (int) saveX;
                                wmParams.height = (int) saveHeight;
                                mFloatBtn.setImageResource(R.drawable.qianjin);
                                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                                flagClick = false;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if(System.currentTimeMillis()-timer<100) {
                                float f = Math.abs(event.getRawY() - touy);
//                                System.out.println("y:" + event.getRawY() + " touy:" + touy);

                                wmParams.y = wmParams.y + (int) event.getRawY() - (int) touy;

                                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                            }
                            timer = System.currentTimeMillis();
                            touy = event.getRawY();
                            break;
                    }
                }
                return false;
            }
        });
    }


    /**
     * 设置拖动窗口的动作
     */
//    View.OnTouchListener otl = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            if(event.getAction()==MotionEvent.ACTION_MOVE){
//                wmParams.y= (int) event.getRawY()+wmParams.y -(int)touy;
//                wmParams.x= (int) event.getRawX()+wmParams.x -(int)toux;
//                toux = event.getRawX();
//                touy = event.getRawY();
//                wmParams.flags = LayoutParams.FLAG_LAYOUT_NO_LIMITS|LayoutParams.FLAG_NOT_TOUCH_MODAL;
//
//                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//            }
//            else if(event.getAction()==MotionEvent.ACTION_DOWN){
//                toux=event.getRawX();
//                touy=event.getRawY();
//                testX = event.getRawX();
//                testY = event.getRawY();
//
//            }else if(event.getAction()==MotionEvent.ACTION_UP){
//                wmParams.flags = LayoutParams.FLAG_LAYOUT_NO_LIMITS|LayoutParams.FLAG_NOT_FOCUSABLE;
//                if(Math.abs(event.getRawX()-testX)<5&&Math.abs(event.getRawY()-testY)<5) {
//                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//
//                    return true;
//                }
//
//                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
//            }
//            return true;
//        }
//    };
    float length;
    float catter;
    public void close(){
        length = width - wmParams.x;
        catter = length;
        android.os.Handler handler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0){
//                    wmParams.x = msg.arg1;
//                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    catter = msg.arg1-wmParams.x;
                    Log.d("biliclose",""+(1-catter/length));
                    mFloatLayout.setX((float)msg.arg1);
                    mFloatLayout.setAlpha(1-catter/length);
                }else {
                    mWindowManager.removeView(mFloatLayout);
                }
            }
        };



//        Closing closing = new Closing(handler,wmParams.x,width);
//        closing.start();
        wmParams.x = width;

        mWindowManager.updateViewLayout(mFloatLayout,wmParams);

//        mWindowManager.removeView(mFloatLayout);
        mWindowManager.removeViewImmediate(mFloatLayout);
//        mWindowManager.removeView(mFloatLayout);

//        wmParams.x = width;
//        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
//        mWindowManager.removeView(mFloatLayout);


    }

    public void setTag(int m){
        tag = m;
    }
    public int getTag(){
        return tag;
    }
    public void setWinData(WinData winData){
        wmParams.x=winData.x;
        wmParams.y=winData.y;
        wmParams.height = winData.height;
        wmParams.width = winData.width;
    }
    public WinData getWinData(){
        WinData winData = new WinData();
        winData.x=wmParams.x;
        winData.y = wmParams.y;
        winData.height = wmParams.height;
        winData.width = wmParams.width;
        return winData;
    }
    public void refresh(){
        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }

    public class WinData{
        int x;
        int y;
        int width;
        int height;
    }


    private int currentX,currentY,currentH,currentW;
    public void dismiss(){
        Log.d("m","dismiss");
        if(wmParams.x<=width) {
            currentX = wmParams.x;
            currentY = wmParams.y;
            currentH = wmParams.height;
            currentW = wmParams.width;


//        wmParams.height=0;
//        wmParams.width=0;
            wmParams.x = 1200;
            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        }
    }
    public void reshow(){
        wmParams.x=currentX;
//        wmParams.y=currentY;
//        wmParams.height=currentH;
//        wmParams.width=currentW;

        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }



    public LayoutParams getWmParams(){
        return wmParams;
    }


    private class Closing extends Thread{
        android.os.Handler handler;
        int x,width;
        public Closing(android.os.Handler handler,int x,int width){
            this.handler = handler;
            this.x = x;
            this.width = width;
        }
        Message message;
        @Override
        public void run() {
            while(x<width){
                x=x+20;
                message = handler.obtainMessage();
                message.what = 0;
                message.arg1 = x;
                handler.sendMessage(message);
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);

        }
    }
    private class Openning extends Thread{
        android.os.Handler handler ;
        int x1,x2 ;
        public Openning(android.os.Handler handler,int x1,int x2){
            this.handler = handler ;
            this.x1 = x1;
            this.x2 = x2;
        }

        Message message;
        @Override
        public void run() {
            super.run();

            while(x1<x2){
                message = handler.obtainMessage();
                x1=x1+20;
                message.what = 0;
                message.arg1 = x1;
                handler.sendMessage(message);
//
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);
        }
    }
    public int getX(){
        return wmParams.x;
    }
    public int getY(){
        return wmParams.y;
    }


    public class PosiAWH{
        public int x;
        public int y;
        public int width;
        public int height;
        public PosiAWH(int x,int y,int width,int height){
            this.x= x;
            this.y = y;
            this.width =width;
            this.height = height;
        }
    }

    private AnimationSet createAnimation(){
        AnimationSet as  = new AnimationSet(true);
        as.setDuration(687);
        AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(687);
        TranslateAnimation ta = new TranslateAnimation(-width,0,0,0);
        ta.setDuration(687);
        as.addAnimation(ta);
        as.addAnimation(aa);
        return as;
    }
    private WindowGroupListener windowGroupListener = new WindowGroupListener() {
        @Override
        public void onClickDown(MotionEvent ev) {
            if(windowsMovingListener!=null)windowsMovingListener.onClickDown(ev);
        }

        @Override
        public void onLongClick(MotionEvent ev) {
            if(windowsMovingListener!=null){
//                wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                mWindowManager.updateViewLayout(mFloatLayout,wmParams);
                windowsMovingListener.onLongClick(ev);

            }
        }
        @Override
        public void onMoving(MotionEvent ev) {
            if(windowsMovingListener!=null)
                windowsMovingListener.onMoving(ev);
        }
        @Override
        public void onClickUp(MotionEvent ev) {
            if(windowsMovingListener!=null)
                windowsMovingListener.onClickUp(ev);
        }
    };
    private WindowsMovingListener windowsMovingListener=null;
    public void setWindowsMovingListener(WindowsMovingListener windowsMovingListener){
        this.windowsMovingListener = windowsMovingListener;
    }

    public void cnTouch(boolean flag){
        if(flag){
            wmParams.flags = LayoutParams.FLAG_LAYOUT_NO_LIMITS|LayoutParams.FLAG_NOT_TOUCH_MODAL;
        }else {
            wmParams.flags = LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }
    public void setPosition(float x, float y){
        wmParams.x = (int) (x+4);
        wmParams.y = (int) (y+38);
        mWindowManager.updateViewLayout(mFloatLayout,wmParams);
    }
}
