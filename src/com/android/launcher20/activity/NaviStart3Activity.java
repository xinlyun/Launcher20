package com.android.launcher20.activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.android.launcher20.util.AMapUtil;
import com.android.launcher20.util.MainApplication;
import com.android.launcher20.util.TTSController;
import com.android.launcher20.util.ToastUtil;
import com.android.launcher20.R;
import com.lin.floatWindows.FloatA;
import com.lin.floatWindows.FloatManager;

import java.util.ArrayList;
import java.util.List;

//import com.amap.api.maps2d.AMap;
//import com.amap.api.maps2d.AMapUtils;
//import com.amap.api.maps2d.LocationSource;
//import com.amap.api.maps2d.MapView;
//import com.amap.api.maps2d.model.BitmapDescriptorFactory;
//import com.amap.api.maps2d.model.LatLng;
//import com.amap.api.maps2d.model.Marker;
//import com.amap.api.maps2d.model.MarkerOptions;
//import com.amap.api.maps2d.model.NaviPara;
//import com.amap.api.maps2d.overlay.PoiOverlay;

/**
 * Demo初始化界面，用于设置起点终点，发起路径计算导航等
 *
 */
public class NaviStart3Activity extends FloatA implements OnClickListener,
//        OnCheckedChangeListener,
        AMapLocationListener,LocationSource
        , AMap.InfoWindowAdapter
        ,TextWatcher,PoiSearch.OnPoiSearchListener,AMap.OnMarkerClickListener
        ,AMap.OnMapClickListener
        ,AMap.OnMapLoadedListener
//        ,AMap.OnMapLongClickListener
//        ,View.OnFocusChangeListener
{
    // --------------View基本控件---------------------
    private MapView mMapView;// 地图控件
    private AutoCompleteTextView mStartPointText;// 起点输入
    private EditText mWayPointText;// 途经点输入
    private EditText mEndPointText;// 终点输入
    private AutoCompleteTextView mStrategyText;// 行车策略输入
    private Button mRouteButton;// 路径规划按钮
    private Button mNaviButton;// 模拟导航按钮
    private ProgressDialog mProgressDialog;// 路径规划过程显示状态
    private ProgressDialog mGPSProgressDialog;// GPS过程显示状态
    private ImageView mStartImage;// 起点下拉按钮
    private ImageView mWayImage;// 途经点点击按钮
    private ImageView mEndImage;// 终点点击按钮
    private ImageView mStrategyImage;// 行车策略点击按钮
    //    private ImageView mbtnReturn; //返回上一页
    private ImageView mBtnOpen;  //打开侧栏
    private LinearLayout naviLinearlayout;  //侧栏布局
    private AutoCompleteTextView mSearchText;
    private ImageView mbtnSearch;
    // 地图和导航核心逻辑类
    private AMap mAmap;
    private AMapNavi mAmapNavi;
    // ---------------------变量---------------------
    private String[] mStrategyMethods;// 记录行车策略的数组
    private String[] mPositionMethods;// 记录起点我的位置、地图点选数组
    // 驾车路径规划起点，途经点，终点的list
    private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mWayPoints = new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
    // 记录起点、终点、途经点位置
    private NaviLatLng mStartPoint = new NaviLatLng();
    private NaviLatLng mEndPoint = new NaviLatLng();
    private NaviLatLng mWayPoint = new NaviLatLng();
    // 记录起点、终点、途经点在地图上添加的Marker
    private Marker mStartMarker;
    private Marker mWayMarker;
    private Marker mEndMarker;
    private Marker mGPSMarker;
    private boolean mIsGetGPS = false;// 记录GPS定位是否成功
    private boolean mIsStart = false;// 记录是否已我的位置发起路径规划
    private boolean mflag_openOrclose = false;
    private ArrayAdapter<String> mPositionAdapter;

    private AMapNaviListener mAmapNaviListener;

    // 记录地图点击事件相应情况，根据选择不同，地图响应不同
    private int mMapClickMode = MAP_CLICK_NO;
    private static final int MAP_CLICK_NO = 0;// 地图不接受点击事件
    private static final int MAP_CLICK_START = 1;// 地图点击设置起点
    private static final int MAP_CLICK_WAY = 2;// 地图点击设置途经点
    private static final int MAP_CLICK_END = 3;// 地图点击设置终点

    // 记录导航种类，用于记录当前选择是驾车还是步行
    private int mTravelMethod = DRIVER_NAVI_METHOD;
    private static final int DRIVER_NAVI_METHOD = 0;// 驾车导航
    private static final int WALK_NAVI_METHOD = 1;// 步行导航

    private int mNaviMethod;
    private static final int NAVI_METHOD = 0;// 执行模拟导航操作
    private static final int ROUTE_METHOD = 1;// 执行计算线路操作

    private int mStartPointMethod = BY_MY_POSITION;
    private static final int BY_MY_POSITION = 0;// 以我的位置作为起点
    private static final int BY_MAP_POSITION = 1;// 以地图点选的点为起点
    // 计算路的状态
    private final static int GPSNO = 0;// 使用我的位置进行计算、GPS定位还未成功状态
    private final static int CALCULATEERROR = 1;// 启动路径计算失败状态
    private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态


    //定点搜索相关
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private String keyWord = "";// 要输入的poi搜索关键字
    private String cityCode;
    private ImageView mfloatBtn;
    //定位
    private LocationManagerProxy mLocationManger;
    private String TAG = "NaviStart";
    private ArrayAdapter<String> aAdapter;
    private AMapLocationListener mLocationListener=new AMapLocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Log.d(TAG,"onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Log.d(TAG,"onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.d(TAG,"onProviderDisabled");
        }

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            Log.d(TAG,"onLocationChanged");
        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            Log.d(TAG,"onLocationChanged");
//            mMapView.getDrawingCache();
//            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//            mGPSOptions.position(new LatLng(location.getLatitude(),location.getLongitude()));
//            mAmap.addMarker(mGPSOptions);

            if (location!=null&&location.getAMapException().getErrorCode() == 0) {
                mIsGetGPS = true;
                mStartPoint =new NaviLatLng(location.getLatitude(), location.getLongitude());
                cityCode = location.getCityCode();

                mGPSMarker.setPosition(new LatLng(
                        mStartPoint.getLatitude(), mStartPoint
                        .getLongitude()));

                mStartPoints.clear();
                mStartPoints.add(mStartPoint);
                dissmissGPSProgressDialog();
                calculateDriverRoute();
            }
            else{
                showToast("定位出现异常");
                dissmissGPSProgressDialog();
                closeDialog();
            }
        }
    };



    //导航准备
    private RouteOverLay mRouteOverLay;
    private boolean mIsMapLoaded = false;
    private TextView mRouteDistanceView,mRouteTimeView,mRouteCostView;
    private Button mStartNaviButton;
    private LinearLayout mdownLayout;
    private int height;
    @Override
    protected void onCreate() {
        super.onCreate();
        setConentView(R.layout.activity_navistart3);

        height=((WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        // 初始化所需资源、控件、事件监听
        initResources();
        initView();
        initMapAndNavi();
        initListener();

        setUpMap();
        MainApplication.getInstance().addActivity((Activity) getContext());

        mMapView.onResume();
        // 以上两句必须重写
        // 以下两句逻辑是为了保证进入首页开启定位和加入导航回调

        TTSController ttsManager = TTSController.getInstance(getContext());// 初始化语音模块
        ttsManager.init();
        AMapNavi.getInstance(getContext()).setAMapNaviListener(ttsManager);// 设置语音模块播报
        AMapNavi.getInstance(getContext()).setAMapNaviListener(getAMapNaviListener());
        TTSController.getInstance(getContext()).startSpeaking();
    }



    /**
     * 初始化资源文件，主要是字符串
     */
    private void initResources() {
        Resources res = getContext().getResources();
        mStrategyMethods = new String[] {
                res.getString(R.string.navi_strategy_speed),
                res.getString(R.string.navi_strategy_cost),
                res.getString(R.string.navi_strategy_distance),
                res.getString(R.string.navi_strategy_nohighway),
                res.getString(R.string.navi_strategy_timenojam),
                res.getString(R.string.navi_strategy_costnojam) };
        mPositionMethods = new String[] { res.getString(R.string.mypoistion),
                res.getString(R.string.mappoistion) };
    }

    /**
     * 初始化界面所需View控件
     *
     * @param
     */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.map);

        mMapView.onCreate(null);
        mAmap=mMapView.getMap();

        mStartPointText = (AutoCompleteTextView) findViewById(R.id.navi_start_edit);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.navistart_auto_textview);
        mbtnSearch = (ImageView) findViewById(R.id.navistart_search);
        mfloatBtn = (ImageView) findViewById(R.id.navistart_float_action_btn);
        mBtnOpen = (ImageView) findViewById(R.id.navi_btn_open);

        naviLinearlayout = (LinearLayout) findViewById(R.id.navibarcontainer);

        mStartPointText
                .setDropDownBackgroundResource(R.drawable.whitedownborder);
        mWayPointText = (EditText) findViewById(R.id.navi_way_edit);
        mEndPointText = (EditText) findViewById(R.id.navi_end_edit);
//        mbtnReturn = (ImageView) findViewById(R.id.navistart_btn_return);

        mStrategyText = (AutoCompleteTextView) findViewById(R.id.navi_strategy_edit);
        mStrategyText.setDropDownBackgroundResource(R.drawable.whitedownborder);
        mStartPointText.setInputType(InputType.TYPE_NULL);
        mWayPointText.setInputType(InputType.TYPE_NULL);
        mEndPointText.setInputType(InputType.TYPE_NULL);
        mStrategyText.setInputType(InputType.TYPE_NULL);

        ArrayAdapter<String> strategyAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.strategy_inputs, mStrategyMethods);
        mStrategyText.setAdapter(strategyAdapter);

        mPositionAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.strategy_inputs, mPositionMethods);
        mStartPointText.setAdapter(mPositionAdapter);

        mRouteButton = (Button) findViewById(R.id.navi_route_button);
        mNaviButton = (Button) findViewById(R.id.navi_navi_button);
//        mNaviMethodGroup = (RadioGroup) findViewById(R.id.navi_method_radiogroup);
        mStartImage = (ImageView) findViewById(R.id.navi_start_image);
        mWayImage = (ImageView) findViewById(R.id.navi_way_image);
        mEndImage = (ImageView) findViewById(R.id.navi_end_image);
        mStrategyImage = (ImageView) findViewById(R.id.navi_strategy_image);


        mRouteOverLay = new RouteOverLay(mAmap, null);
        mRouteDistanceView = (TextView) findViewById(R.id.navi_route_distance);
        mRouteTimeView = (TextView) findViewById(R.id.navi_route_time);
        mRouteCostView = (TextView) findViewById(R.id.navi_route_cost);
        mStartNaviButton = (Button) findViewById(R.id.routestartnavi);
        mdownLayout = (LinearLayout) findViewById(R.id.navistart_down_llayout);

    }

    /**
     * 初始化地图和导航相关内容
     */
    MarkerOptions mGPSOptions ,mStartOptions,mEndOptions,mWayOptions;
    private void initMapAndNavi() {
        // 初始语音播报资源

        ((Activity)getContext()).setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制
        //语音播报开始
        mGPSOptions = new MarkerOptions();
        mStartOptions = new MarkerOptions();
        mEndOptions = new MarkerOptions();
        mWayOptions = new MarkerOptions();


        mAmapNavi = AMapNavi.getInstance(getContext());// 初始化导航引擎
        // 初始化Marker添加到地图
        mStartMarker = mAmap.addMarker(new MarkerOptions()
                .setFlat(true).perspective(true).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getContext().getResources(), R.drawable.start))));
        mWayMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getContext().getResources(), R.drawable.way))));
        mEndMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getContext().getResources(), R.drawable.end))));
        mGPSMarker = mAmap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getContext().getResources(),
                                R.drawable.location_marker))).setFlat(true).perspective(true));


        mGPSOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getContext().getResources(),
                        R.drawable.location_marker)));
//        mGPSOptions.set
        mStartOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getContext().getResources(), R.drawable.start)));
        mEndOptions .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getContext().getResources(), R.drawable.end)));
        mWayOptions .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getContext().getResources(), R.drawable.way)));

    }

    /**
     * 初始化所需监听
     */
    private void initListener() {
        // 控件点击事件
        mStartPointText.setOnClickListener(this);
        mWayPointText.setOnClickListener(this);
        mEndPointText.setOnClickListener(this);
        mStrategyText.setOnClickListener(this);
        mRouteButton.setOnClickListener(this);
        mNaviButton.setOnClickListener(this);
        mStartImage.setOnClickListener(this);
        mWayImage.setOnClickListener(this);
        mEndImage.setOnClickListener(this);
        mStrategyImage.setOnClickListener(this);
        mfloatBtn.setOnClickListener(this);
//        mNaviMethodGroup.setOnCheckedChangeListener(this);
        // 设置地图点击事件
        mAmap.setOnMapClickListener(this);
        // 起点下拉框点击事件监听
        mStartPointText.setOnItemClickListener(getOnItemClickListener());
//        mAmap.setOnMapClickListener(this);
        mAmap.setInfoWindowAdapter(this);
        mAmap.setOnMarkerClickListener(this);
        mSearchText.addTextChangedListener(this);
        mSearchText.setOnClickListener(this);
//        mSearchText.setOnFocusChangeListener(this);
        mSearchText.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    inputAble(false);
                    searchButton();
            }
        });

//        mbtnReturn.setOnClickListener(this);
        mBtnOpen.setOnClickListener(this);
        mbtnSearch.setOnClickListener(this);
        mStartNaviButton.setOnClickListener(this);

    }
    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        mAmap.setLocationSource(this);// 设置定位监听
        mAmap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAmap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        mAmap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
    }



    // ----------具体处理方法--------------
    /**
     * 算路的方法，根据选择可以进行行车和步行两种方式进行路径规划
     */
    private void calculateRoute() {
        AMapNavi.getInstance(getContext()).startGPS();
        if(mStartPointMethod==BY_MY_POSITION&&!mIsGetGPS){
            mLocationManger= LocationManagerProxy.getInstance(getContext());
            //进行一次定位
            mLocationManger.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 15, mLocationListener);
            showGPSProgressDialog();


            return;

        }
        mIsGetGPS=false;
        switch (mTravelMethod) {
            // 驾车导航
            case DRIVER_NAVI_METHOD:
                int driverIndex = calculateDriverRoute();
                if (driverIndex == CALCULATEERROR) {
                    showToast("路线计算失败,检查参数情况");
                    return;
                } else if (driverIndex == GPSNO) {
                    return;
                }
                break;
            // 步行导航
            case WALK_NAVI_METHOD:
                int walkIndex = calculateWalkRoute();
                if (walkIndex == CALCULATEERROR) {
                    showToast("路线计算失败,检查参数情况");
                    return;
                } else if (walkIndex == GPSNO) {
                    return;
                }
                break;
        }
        // 显示路径规划的窗体
        showProgressDialog();
    }

    /**
     * 对行车路线进行规划
     */
    private int calculateDriverRoute() {
        int driveMode = getDriveMode();
        int code = CALCULATEERROR;

        if (mAmapNavi.calculateDriveRoute(mStartPoints, mEndPoints,
                mWayPoints, driveMode)) {
            code = CALCULATESUCCESS;
        } else {

            code = CALCULATEERROR;
        }


        return code;
    }

    /**
     * 对步行路线进行规划
     */
    private int calculateWalkRoute() {
        int code = CALCULATEERROR;
        if (mAmapNavi.calculateWalkRoute(mStartPoint, mEndPoint)) {
            code = CALCULATESUCCESS;
        } else {

            code = CALCULATEERROR;
        }

        return code;
    }



    /**
     * 根据选择，获取行车策略
     */
    private int getDriveMode() {
        String strategyMethod = mStrategyText.getText().toString();
        // 速度优先
        if (mStrategyMethods[0].equals(strategyMethod)) {
            return AMapNavi.DrivingDefault;
        }
        // 花费最少
        else if (mStrategyMethods[1].equals(strategyMethod)) {
            return AMapNavi.DrivingSaveMoney;

        }
        // 距离最短
        else if (mStrategyMethods[2].equals(strategyMethod)) {
            return AMapNavi.DrivingShortDistance;
        }
        // 不走高速
        else if (mStrategyMethods[3].equals(strategyMethod)) {
            return AMapNavi.DrivingNoExpressways;
        }
        // 时间最短且躲避拥堵
        else if (mStrategyMethods[4].equals(strategyMethod)) {
            return AMapNavi.DrivingFastestTime;
        } else if (mStrategyMethods[5].equals(strategyMethod)) {
            return AMapNavi.DrivingAvoidCongestion;
        } else {
            return AMapNavi.DrivingDefault;
        }
    }

    // ----------------------事件处理---------------------------

    /**
     * 控件点击事件监听
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 路径规划按钮处理事件
            case R.id.navi_route_button:
                mNaviMethod = ROUTE_METHOD;
                showDialog();
                calculateDriverRoute();
                break;
            // 模拟导航处理事件
            case R.id.navi_navi_button:
                mNaviMethod = NAVI_METHOD;
                calculateRoute();
                break;
            // 起点点击事件
            case R.id.navi_start_image:
            case R.id.navi_start_edit:
                setTextDescription(mStartPointText, null);
                mPositionAdapter = new ArrayAdapter<String>(getContext(),
                        R.layout.strategy_inputs, mPositionMethods);
                mStartPointText.setAdapter(mPositionAdapter);
                mStartPoints.clear();
                mStartMarker.setPosition(null);
                mStartPointText.showDropDown();
                break;
            // 途经点点击事件
            case R.id.navi_way_image:
            case R.id.navi_way_edit:
                mMapClickMode = MAP_CLICK_WAY;
                mWayPoints.clear();
                mWayMarker.setPosition(null);
                setTextDescription(mWayPointText, "点击地图设置途经点");
                showToast("点击地图添加途经点");
                break;
            // 终点点击事件
            case R.id.navi_end_image:
            case R.id.navi_end_edit:
                mMapClickMode = MAP_CLICK_END;
                mEndPoints.clear();
                mEndMarker.setPosition(null);
                setTextDescription(mEndPointText, "点击地图设置终点");
                showToast("点击地图添加终点");
                break;
            // 策略点击事件
            case R.id.navi_strategy_image:
            case R.id.navi_strategy_edit:
                mStrategyText.showDropDown();
                break;
            case R.id.navi_btn_open:

                if(!mflag_openOrclose){
                    openLeftLayout();
                }else {
                    closeLeftLayout();
                }
                break;
            case R.id.navistart_search:
                inputAble(false);
//                mAmap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//                mSearchText.setText("梅州曾宪梓中学");
//                searchButton();
                Intent intent = new Intent(getContext(),SearchPage.class);
                Bundle bundle = new Bundle();
                if(mLocation!=null)
                {
                    bundle.putParcelable("myown",mLocation);
                    bundle.putString("city",cityCode);
                }
                intent.putExtra("myown", bundle);
                getContext().startActivity(intent);


                break;
            case R.id.navistart_auto_textview:
                inputAble(true);
                break;
            case R.id.routestartnavi:
//                NaviCustomActivity nca = new NaviCustomActivity((Activity)getContext(),this);
                FloatManager.getContext(getContext()).startWindows(this,NaviCustomActivity.class);
//                nca.onStart(0);
                break;
            case R.id.navistart_float_action_btn:
                searchButton();
                break;
        }
    }

    private Handler lefthandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                naviLinearlayout.setX(msg.arg1);
                mBtnOpen.setX(msg.arg1+236);
            }
        }
    };
    private void openLeftLayout(){
        new moveThread(lefthandler,(int)naviLinearlayout.getX(),-10).start();
        mflag_openOrclose = true;
    }
    private void closeLeftLayout(){
        new moveThread(lefthandler,(int)naviLinearlayout.getX(),-240).start();
        mflag_openOrclose = false;
    }


    /**
     * 点击搜索按钮
     */
    public void searchButton() {
        keyWord = AMapUtil.checkEditText(mSearchText);
        if ("".equals(keyWord)) {
            ToastUtil.show(getApplication(), "请输入搜索关键字");
            return;
        } else {
            doSearchQuery("");
        }
    }
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String cityCode) {
        showProgressDialog();// 显示进度框
//        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * 地图点击事件监听
     * */
    @Override
    public void onMapClick(LatLng latLng) {
        // 默认不接受任何操作
        if (mMapClickMode == MAP_CLICK_NO) {

            return;
        }
        // 其他情况根据起点、途经点、终点不同逻辑处理不同
        addPointToMap(latLng);

    }
    /**
     * 地图点击事件核心处理逻辑
     * @param position
     */
    private void addPointToMap(LatLng position) {
        NaviLatLng naviLatLng = new NaviLatLng(position.latitude,
                position.longitude);
        switch (mMapClickMode) {
            //起点
            case MAP_CLICK_START:

                mStartMarker.setPosition(position);
                mStartPoint = naviLatLng;
                mStartPoints.clear();
                mStartPoints.add(mStartPoint);
                setTextDescription(mStartPointText, "已成功设置起点");
                break;
            //途经点
            case MAP_CLICK_WAY:
                mWayMarker.setPosition(position);
                mWayPoints.clear();
                mWayPoint = naviLatLng;
                mWayPoints.add(mWayPoint);
                setTextDescription(mWayPointText, "已成功设置途经点");
                break;
            //终点
            case MAP_CLICK_END:
//                mEndMarker.setF
                mEndMarker.setPosition(position);
                mEndPoints.clear();
                mEndPoint = naviLatLng;
                mEndPoints.add(mEndPoint);
                setTextDescription(mEndPointText, "已成功设置终点");
                break;
        }

    }
    /**
     * 起点下拉框点击事件监听
     * */
    private OnItemClickListener getOnItemClickListener() {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long arg3) {
                switch (index) {
                    // 我的位置为起点进行导航或路径规划
                    case 0:
                        mStartPointMethod = BY_MY_POSITION;
                        break;
                    // 地图点选起点进行导航或路径规划
                    case 1:
                        mStartPointMethod = BY_MAP_POSITION;
                        mMapClickMode = MAP_CLICK_START;
                        showToast("点击地图添加起点");
                        break;
                }

            }

        };
    }

    /**
     * 导航回调函数
     *
     * @return
     */
    private AMapNaviListener getAMapNaviListener() {
        if (mAmapNaviListener == null) {

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
                    // TODO Auto-generated method stub

                }

                @Override
                public void onReCalculateRouteForTrafficJam() {
                    // TODO Auto-generated method stub

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
                    Log.d(TAG,"onCalculateRouteSuccess");
                    try{
                    dissmissProgressDialog();
                    closeDialog();}
                    catch (Exception e){

                    }
//                    NaviRouteActivity nra = new NaviRouteActivity((Activity)getContext(),NaviStart3Activity.this);
//                    nra.onStart(0);
                    mAmap.clear();
                    initNavi();
                    closeLeftLayout();
                    System.out.println("now "+mdownLayout.getY());
                    new moveThread(BottomHandler,(int)mdownLayout.getY(),height/3-100).start();

                }

                @Override
                public void onCalculateRouteFailure(int arg0) {
                    dissmissProgressDialog();
                    closeDialog();
                    showToast("路径规划出错");
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

                }

                @Override
                public void onNaviInfoUpdate(NaviInfo arg0) {

                    // TODO Auto-generated method stub

                }
            };
        }
        return mAmapNaviListener;
    }




    // ---------------UI操作----------------
    private void showToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    private void setTextDescription(TextView view, String description) {
        view.setText(description);
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null)
            mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("线路规划中");
        mProgressDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 显示GPS进度框
     */
    private void showGPSProgressDialog() {
        if (mGPSProgressDialog == null)
            mGPSProgressDialog = new ProgressDialog(getContext());
        mGPSProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mGPSProgressDialog.setIndeterminate(false);
        mGPSProgressDialog.setCancelable(true);
        mGPSProgressDialog.setMessage("定位中...");
        mGPSProgressDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissGPSProgressDialog() {
        if (mGPSProgressDialog != null) {
            mGPSProgressDialog.dismiss();
        }

    }



    // -------------生命周期必须重写方法----------------







    private OnLocationChangedListener mListener;
    private LatLonPoint mLocation = null;
    private SharedPreferences mshare;
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {

            if (aMapLocation!=null&&aMapLocation.getAMapException().getErrorCode() == 0) {

                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                cityCode = aMapLocation.getCityCode();
                mIsGetGPS = true;
                mStartPoint =new NaviLatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mLocation = new LatLonPoint(mStartPoint.getLatitude(),mStartPoint.getLongitude());
//                mGPSMarker.setPosition(new LatLng(
//                        mStartPoint.getLatitude(), mStartPoint
//                        .getLongitude()));
                mshare = getContext().getSharedPreferences("myown",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mshare.edit();
                editor.putString("x", Double.toString(mLocation.getLatitude()));
                editor.putString("y", Double.toString(mLocation.getLongitude()));
                editor.putString("city",cityCode);

                editor.commit();
                mStartPoints.clear();
                mStartPoints.add(mStartPoint);
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationManger == null) {
            mLocationManger = LocationManagerProxy.getInstance(getContext());
            //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            //注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
            //在定位结束后，在合适的生命周c期调用destroy()方法
            //其中如果间隔时间为-1，则定位只定一次
            //在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
            mLocationManger.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2*1000, 20, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationManger != null) {
            mLocationManger.removeUpdates(this);
            mLocationManger.destroy();
        }
        mLocationManger = null;
    }




//    View view;
    /**
     * Marker标记点击事件,展开一个内容框
     * @param marker
     * @return
     */
    @Override
    public View getInfoWindow(final Marker marker) {
        Log.d("open","i was be click");
        View view = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                null);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());

        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        // 调起高德地图app
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                startAMapNavi(marker);
//                mMapClickMode=MAP_CLICK_END;
//                addPointToMap(marker.getPosition());
//                mMapClickMode = MAP_CLICK_NO;

                marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getContext().getResources(),
                                R.drawable.end)) );
                NaviLatLng naviLatLng = new NaviLatLng(marker.getPosition().latitude,marker.getPosition().longitude);
                mEndPoint = naviLatLng;
                mEndPoints.add(mEndPoint);
                setTextDescription(mEndPointText, "已成功设置终点");
                showDialog();
                calculateDriverRoute();
            }
        });
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
     * 这里没用上了，可为合作时保留使用
     */
    public void startAMapNavi(Marker marker) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
//        try {
//            AMapUtils.openAMapNavi(naviPara, getContext().getApplicationContext());
//        } catch (AMapException e) {
//
//            // 如果没安装会进入异常，调起下载页面
//            AMapUtils.getLatestAMapApp(getContext().getApplicationContext());
//
//        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    /**
     * 搜索栏文字修改时的响应事件，通过amap提供的services进行查询服务
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        Inputtips inputTips = new Inputtips(getContext(),
                new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                        if (rCode == 0) {// 正确返回
                            List<String> listString = new ArrayList<String>();
                            for (int i = 0; i < tipList.size(); i++) {
                                listString.add(tipList.get(i).getName());
                            }
                            aAdapter = new ArrayAdapter<String>(
                                    getContext().getApplicationContext(),
//                                    R.layout.route_inputs, listString);
                                    R.layout.autotextstyle, listString);
                            mSearchText.setAdapter(aAdapter);

                            aAdapter.notifyDataSetChanged();
                        }
                    }
                });
        try {
            inputTips.requestInputtips(newText, cityCode);// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号


        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 关键词搜索反馈
     * @param result
     * @param rCode
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框

        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        mAmap.clear();// 清理之前的图标
                        initMapAndNavi();
                        PoiOverlay poiOverlay = new PoiOverlay(mAmap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                        boolean flag = false;
                        for(SuggestionCity city:suggestionCities){
                            if(city.getCityCode().equals(cityCode)){
                                doSearchQuery(cityCode);
                                flag=true;
                                break;
                            }
                        }
                        if(!flag)doSearchQuery(suggestionCities.get(0).getCityCode());
                    } else {
                        Toast.makeText(getContext(),"no_result",Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getContext(),"no_result",Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(getContext(),"error_network",Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(getContext(),"error_key",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),"error_other",Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
//        ToastUtil.show(PoiKeywordSearchActivity.this, infomation);
        Toast.makeText(getContext(),infomation,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        getInfoWindow(marker);
        return false;
    }

    /**
     * 地图加载时根据路径缩放地图产生全览效果
     */
    @Override
    public void onMapLoaded() {
        mIsMapLoaded = true;
        if (mRouteOverLay != null) {
            mRouteOverLay.zoomToSpan();

        }
    }
    /**
     * 初始化路线描述信息和加载线路
     */
    private void initNavi() {
        Log.d(TAG,"initNavi");

        mAmapNavi = AMapNavi.getInstance(getContext());
        mAmapNavi.setAMapNaviListener(getAMapNaviListener());
        AMapNaviPath naviPath = mAmapNavi.getNaviPath();
        if (naviPath == null) {
            return;
        }
        // 获取路径规划线路，显示到地图上
//        PoiOverlay poiOverlay = new PoiOverlay(mAmap,null);

        mRouteOverLay.setRouteInfo(naviPath);

        mRouteOverLay.addToMap();
//        if (mIsMapLoaded) {
        mRouteOverLay.zoomToSpan();
//        }

        double length = ((int) (naviPath.getAllLength() / (double) 1000 * 10))
                / (double) 10;
        // 不足分钟 按分钟计
        int time = (naviPath.getAllTime() + 59) / 60;
        int cost = naviPath.getTollCost();
        mRouteDistanceView.setText(String.valueOf(length));
        mRouteTimeView.setText(String.valueOf(time));
        mRouteCostView.setText(String.valueOf(cost));
    }

//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if(v.getId()==R.id.navistart_auto_textview){
//            inputAble(false);
//        searchButton();
//        }
//    }

    /**
     * 模拟拖动线程，实现侧栏窗口移动效果
     */
    public class moveThread extends Thread{
        private Handler handler;
        private int cur,poi;
        private Message message;
        public moveThread(Handler handler,int x, int y){
            this.handler = handler;
            this.cur=x;
            this.poi=y;
        }

        @Override
        public void run() {
            super.run();
            if(cur<poi){
                while (cur<poi){
                    message = handler.obtainMessage();
                    cur=cur+8;
                    message.what = 0;
                    message.arg1 = cur;
                    handler.sendMessage(message);
                    try {
                        sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }else {
                while(cur>poi){
                    message = handler.obtainMessage();
                    cur = cur-8;
                    message.what= 0;
                    message.arg1 = cur;
                    handler.sendMessage(message);
                    try {
                        sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);
        }



    }


    private Handler BottomHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                mdownLayout.setY(msg.arg1);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        initNavi();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mMapView.onPause();
    }
}
