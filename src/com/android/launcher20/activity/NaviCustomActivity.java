package com.android.launcher20.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.view.RouteOverLay;
import com.android.launcher20.FloatView.FloatA;
import com.android.launcher20.util.TTSController;
import com.android.launcher20.util.Utils;
import com.android.launcher20.R;


/**
 * 实时导航界面
 *
 */
public class NaviCustomActivity extends FloatA implements
        AMapNaviViewListener,AMapNaviListener {
	private AMap aMap;
	private LinearLayout mAmapAMapNaviView_layout;
	private AMapNaviView mAmapAMapNaviView;
	// 导航可以设置的参数
	private boolean mDayNightFlag = Utils.DAY_MODE;// 默认为白天模式
	private boolean mDeviationFlag = Utils.YES_MODE;// 默认进行偏航重算
	private boolean mJamFlag = Utils.YES_MODE;// 默认进行拥堵重算
	private boolean mTrafficFlag = Utils.OPEN_MODE;// 默认进行交通播报
	private boolean mCameraFlag = Utils.OPEN_MODE;// 默认进行摄像头播报
	private boolean mScreenFlag = Utils.YES_MODE;// 默认是屏幕常亮
	private String TAG= "NaviCustom";
	// 导航界面风格
	private int mThemeStle;
	// 导航监听
	private AMapNaviListener mAmapNaviListener;
	private AMapNavi mAMapNavi;
	TextView tv ;
    private TextView hourtext,mintext,distancetext;
    private Button btnExit;
	private ImageView mNaviBtn;
	private float zoom = 19.0f;
	private AMapNaviViewOptions viewOptions;
	public NaviCustomActivity(Activity context1,FloatA floatA) {
		super(context1,floatA);
	}
	@Override
	protected void onCreate() {
		super.onCreate();
		setConentView(R.layout.activity_navicustom);


		//语音播报开始
		TTSController.getInstance(getContext()).startSpeaking();
		// 实时导航方式进行导航
		AMapNavi.getInstance(getContext()).startNavi(AMapNavi.GPSNaviMode);
//        AMapNavi.getInstance(getContext()).resumeNavi();

		mAMapNavi = AMapNavi.getInstance(getContext());
        hourtext = (TextView) findViewById(R.id.navi_text_hour);
        mintext = (TextView) findViewById(R.id.navi_text_min);
        distancetext = (TextView) findViewById(R.id.navi_text_distance);
		initView();
	}

	private void initView() {

        btnExit = (Button) findViewById(R.id.btn_custom_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AMapNavi.getInstance(getContext()).destroy();
                new NaviStart3Activity((Activity)getContext(),NaviCustomActivity.this).onStart(0);
            }
        });
		mNaviBtn = (ImageView) findViewById(R.id.navi_image_navi);
		mNaviBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				viewOptions.setCompassEnabled(false);
//				mAmapAMapNaviView.setViewOptions(viewOptions);
				mAmapAMapNaviView.onTouch(null);
				AMapNaviPath naviPath = mAMapNavi.getNaviPath();
				RouteOverLay mRouteOverLay = new RouteOverLay(aMap, null);
				mRouteOverLay.setRouteInfo(naviPath);
				mRouteOverLay.zoomToSpan();
			}
		});
//		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.customnavimap);
		mAmapAMapNaviView_layout = (LinearLayout) findViewById(R.id.customnavimap_group);
		mAmapAMapNaviView = new AMapNaviView(getContext());
		mAmapAMapNaviView_layout.addView(mAmapAMapNaviView);
		mAmapAMapNaviView.onCreate(((Activity)getContext()).getIntent().getExtras());

		aMap = mAmapAMapNaviView.getMap();

		setAmapNaviViewOptions();
//		mAMapNavi.setAMapNaviListener(getAMapNaviListener());
		mAMapNavi.setAMapNaviListener(this);
        mAmapAMapNaviView.setAMapNaviViewListener(this);
		mAmapAMapNaviView.onResume();
		
	}

	/**
	 * 设置导航的参数
	 */
	private void setAmapNaviViewOptions() {
		if (mAmapAMapNaviView == null) {
			return;
		}
		viewOptions = new AMapNaviViewOptions();
		viewOptions.setSettingMenuEnabled(true);// 设置导航setting可用
        viewOptions.setLayoutVisible(true);
		viewOptions.setNaviNight(mDayNightFlag);// 设置导航是否为黑夜模式
		viewOptions.setReCalculateRouteForYaw(mDeviationFlag);// 设置导偏航是否重算
		viewOptions.setReCalculateRouteForTrafficJam(mJamFlag);// 设置交通拥挤是否重算
		viewOptions.setTrafficInfoUpdateEnabled(mTrafficFlag);// 设置是否更新路况
		viewOptions.setCameraInfoUpdateEnabled(mCameraFlag);// 设置摄像头播报
		viewOptions.setScreenAlwaysBright(mScreenFlag);// 设置屏幕常亮情况
//		viewOptions.setNaviViewTopic(1);// 设置导航界面主题样式
		viewOptions.setLeaderLineEnabled(1248);
		viewOptions.setRouteListButtonShow(true);
//		aMap.getUiSettings().setZoomControlsEnabled(true);

//		Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.ic_launcher);
//		viewOptions.setMonitorCameraEnabled(true);

//		viewOptions.setWayPointBitmap(bitmap);
//		viewOptions.setMonitorCamerBitmap(bitmap);

		mAmapAMapNaviView.setViewOptions(viewOptions);

	}

	private AMapNaviListener getAMapNaviListener() {
//		if (mAmapNaviListener == null) {

			mAmapNaviListener = new AMapNaviListener() {

				@Override
				public void onTrafficStatusUpdate() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStartNavi(int arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReCalculateRouteForYaw() {
					// 可以在频繁重算时进行设置,例如五次之后
					// i++;
					// if (i >= 5) {
					// AMapNaviViewOptions viewOptions = new
					// AMapNaviViewOptions();
					// viewOptions.setReCalculateRouteForYaw(false);
					// mAmapAMapNaviView.setViewOptions(viewOptions);
					// }
				}

				@Override
				public void onReCalculateRouteForTrafficJam() {

				}

				@Override
				public void onLocationChange(AMapNaviLocation location) {

				}

				@Override
				public void onInitNaviSuccess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onInitNaviFailure() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetNavigationText(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onEndEmulatorNavi() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onCalculateRouteSuccess() {

				}

				@Override
				public void onCalculateRouteFailure(int arg0) {

				}

				@Override
				public void onArrivedWayPoint(int arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onArriveDestination() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGpsOpenStatus(boolean arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onNaviInfoUpdated(AMapNaviInfo arg0) {
					// TODO Auto-generated method stub
                    System.out.println("hour:" + arg0.getPathRemainTime() + "  distance:" + arg0.getPathRemainDistance());
                    hourtext.setText("" + arg0.getPathRemainTime() / 60 / 60);

                    mintext.setText(""+(arg0.getPathRemainTime()/60)%60);
                    distancetext.setText(""+((float)arg0.getPathRemainDistance())/1000);
				}

				@Override
				public void onNaviInfoUpdate(NaviInfo arg0) {

					// TODO Auto-generated method stub
					tv.setText("Road:"+arg0.getCurrentRoadName()+" CurLink:"
							+arg0.getCurLink()+" Direction"+arg0.getDirection()+
							" CurStepDistance"+arg0.getCurStepRetainDistance());

				}
			};
//		}
		return mAmapNaviListener;
	}

	// -------处理
	/**
	 * 导航界面返回按钮监听
	 * */
	@Override
	public void onNaviCancel() {
//		Intent intent = new Intent(NaviCustomActivity.this,
//				MainStartActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		startActivity(intent);
//		finish();
//		new NaviStartActivity((Activity)getContext(),this).onStart(0);
		Toast.makeText(getApplication(),"CLick",Toast.LENGTH_SHORT).show();

	}

	/**
	 * 点击设置按钮的事件
	 */
	@Override
	public void onNaviSetting() {
//		Bundle bundle = new Bundle();
//		bundle.putInt(Utils.THEME, mThemeStle);
//		bundle.putBoolean(Utils.DAY_NIGHT_MODE, mDayNightFlag);
//		bundle.putBoolean(Utils.DEVIATION, mDeviationFlag);
//		bundle.putBoolean(Utils.JAM, mJamFlag);
//		bundle.putBoolean(Utils.TRAFFIC, mTrafficFlag);
//		bundle.putBoolean(Utils.CAMERA, mCameraFlag);
//		bundle.putBoolean(Utils.SCREEN, mScreenFlag);
//		Intent intent = new Intent(NaviCustomActivity.this,
//				NaviSettingActivity.class);
//		intent.putExtras(bundle);
//		startActivity(intent);

	}

	@Override
	public void onNaviMapMode(int arg0) {

	}
	@Override
	public void onNaviTurnClick() {


	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}
	private void processBundle(Bundle bundle) {

		if (bundle != null) {
			mDayNightFlag = bundle.getBoolean(Utils.DAY_NIGHT_MODE,
					mDayNightFlag);
			mDeviationFlag = bundle.getBoolean(Utils.DEVIATION, mDeviationFlag);
			mJamFlag = bundle.getBoolean(Utils.JAM, mJamFlag);
			mTrafficFlag = bundle.getBoolean(Utils.TRAFFIC, mTrafficFlag);
			mCameraFlag = bundle.getBoolean(Utils.CAMERA, mCameraFlag);
			mScreenFlag = bundle.getBoolean(Utils.SCREEN, mScreenFlag);
			mThemeStle = bundle.getInt(Utils.THEME);

		}
	}


	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		super.onPause();
		mAMapNavi.pauseNavi();
		mAmapAMapNaviView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mAMapNavi.resumeNavi();
		mAmapAMapNaviView.onResume();
	}
	//-----------------------------------------------------

	/**
	 * 导航回调函数
	 *
	 * @return
	 */
	@Override
	public void onInitNaviFailure() {
		Log.d(TAG,"onInitNaviFailure");
	}

	@Override
	public void onInitNaviSuccess() {
		Log.d(TAG,"onInitNaviSuccess");
	}

	@Override
	public void onStartNavi(int i) {
		Log.d(TAG,"onStartNavi");
	}

	@Override
	public void onTrafficStatusUpdate() {
		Log.d(TAG,"onTrafficStatusUpdate");
	}

	@Override
	public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
		Log.d(TAG,"onLocationChange");
	}

	@Override
	public void onGetNavigationText(int i, String s) {
		Log.d(TAG,"onGetNavigationText");
	}

	@Override
	public void onEndEmulatorNavi() {
		Log.d(TAG,"onEndEmulatorNavi");
	}

	@Override
	public void onArriveDestination() {
		Log.d(TAG,"onArriveDestination");
	}

	@Override
	public void onCalculateRouteSuccess() {
		Log.d(TAG,"onCalculateRouteSuccess");
	}

	@Override
	public void onCalculateRouteFailure(int i) {
		Log.d(TAG,"onCalculateRouteFailure");
	}

	@Override
	public void onReCalculateRouteForYaw() {
		Log.d(TAG,"onReCalculateRouteForYaw");
	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		Log.d(TAG,"onReCalculateRouteForTrafficJam");
	}

	@Override
	public void onArrivedWayPoint(int i) {
		Log.d(TAG,"onArrivedWayPoint");
	}

	@Override
	public void onGpsOpenStatus(boolean b) {
		Log.d(TAG,"onGpsOpenStatus");
	}

	@Override
	public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {
		Log.d(TAG,"onNaviInfoUpdated");
		hourtext.setText("" + aMapNaviInfo.getPathRemainTime() / 60 / 60);

		mintext.setText(""+(aMapNaviInfo.getPathRemainTime()/60)%60);
		distancetext.setText("" + ((float) aMapNaviInfo.getPathRemainDistance())/1000);
	}

	@Override
	public void onNaviInfoUpdate(NaviInfo naviInfo) {
		Log.d(TAG,"onNaviInfoUpdate");
	}


	//------------------------------------------
}
