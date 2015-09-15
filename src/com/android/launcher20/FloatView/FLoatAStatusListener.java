package com.android.launcher20.FloatView;

/**
 * 让FloatService向外承接来自activity的生命周期改变，并做出相应工作
 */
public interface FLoatAStatusListener{
    void onResume();
    void onPause();
}
