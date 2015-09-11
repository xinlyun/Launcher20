package com.android.launcher20.util;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

public class MainApplication extends Application {

    private ArrayList<Activity>activities=new ArrayList<Activity>();
    private static MainApplication instance;

    private MainApplication()
    {
    }
    //单例模式中获取唯一的MyApplication实例
    public static MainApplication getInstance()
    {
        if(null == instance)
        {
            instance = new MainApplication();
        }
        return instance;
    }
    //添加Activity到容器中
    public void addActivity(Activity activity)
    {
        activities.add(activity);
    }
    public void deleteActivity(Activity activity){
        activities.remove(activity);
    }
    //finish
    public void exit()
    {
        for(Activity activity:activities){
            activity.finish();
        }
        activities.clear();

    }
}
