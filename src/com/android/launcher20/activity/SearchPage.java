package com.android.launcher20.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.android.launcher20.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by root on 15-9-12.
 */
public class SearchPage extends Activity implements PoiSearch.OnPoiSearchListener, RouteSearch.OnRouteSearchListener,TextWatcher
    ,AMapNaviListener

{
    private String strStart ;
    private String cityCode;
//    private LatLonPoint startPoint;
    private PoiSearch.Query startSearchQuery;
    private PoiSearch.Query endSearchQuery;
    private RouteSearch routeSearch;
    private EditText mAutoText;
    private ListView mlistview;
    private LatLonPoint mLocation;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private ArrayAdapter<String> arrayAdapter;
    private AMapNavi mAmapNavi;
    private List<NaviLatLng> startPoint;
    private List<NaviLatLng> endPoint,wayPoint;
    private String posi;
    private Stack<String> names;
    private List<PoiItem> poiItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchpage);
        names = new Stack<>();
        initView();
    }

    private void initView(){
        startPoint = new ArrayList<NaviLatLng>();
        endPoint = new ArrayList<NaviLatLng>();
        wayPoint = new ArrayList<NaviLatLng>();

        mAmapNavi = AMapNavi.getInstance(this);
        mAmapNavi.setAMapNaviListener(this);

        mAutoText = (EditText) findViewById(R.id.id_searchpage);
        mAutoText.addTextChangedListener(this);
        mlistview = (ListView) findViewById(R.id.id_search_listview);
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mlistview.setAdapter(arrayAdapter);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("myown");
        try {
            mLocation = bundle.getParcelable("myown");
            cityCode = bundle.getString("city");
            startPoint.add(new NaviLatLng(mLocation.getLatitude(),mLocation.getLongitude()));
            Log.d("SearchPage",mLocation.getLatitude()+" "+mLocation.getLongitude()+ " "+cityCode);
        }catch (Exception e){
            Log.d("SearchPage",e.toString());
        }
        if(mLocation == null){
            SharedPreferences mshare = getSharedPreferences("myown", Context.MODE_PRIVATE);
            String x = mshare.getString("x", "0");
            String y = mshare.getString("y","0");
            cityCode = mshare.getString("city","");
            Log.d("SearchPage",x+" "+y+ " "+cityCode);
            mLocation = new LatLonPoint(Double.valueOf(x),Double.valueOf(y));
            startPoint.add(new NaviLatLng(mLocation.getLatitude(),mLocation.getLongitude()));
        }

        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SearchPage","onItemCLick");
                PoiItem poiItem = poiItems.get(position);
                endPoint.clear();
                endPoint.add(new NaviLatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()));
                AMapNavi.getInstance(SearchPage.this).calculateDriveRoute(startPoint, endPoint,
                        wayPoint, AMapNavi.DrivingDefault) ;
                SearchPage.this.finish();
            }
        });
    }



    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.d("SearchPage","onPoiSearched");
        if (poiResult != null && poiResult.getQuery() != null
                && poiResult.getPois() != null && poiResult.getPois().size() > 0) {// 搜索poi的结果
//            if (poiResult.getQuery().equals(startSearchQuery)) {
                poiItems = poiResult.getPois();// 取得poiitem数据

                arrayAdapter.clear();
                names.clear();
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
                for(PoiItem poiItem:poiItems){

                    RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mLocation,poiItem.getLatLonPoint());
                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,RouteSearch.DrivingDefault,null,null,"");
                    names.push(poiItem.toString());
                    routeSearch.calculateDriveRouteAsyn(query);

//                    endPoint.clear();
//                    endPoint.add(new NaviLatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude()));
//                    if(mAmapNavi.calculateDriveRoute(startPoint,endPoint,
//                            wayPoint, AMapNavi.DrivingDefault)) {
//                        Log.d("SearchPage", "calculate success");
//                        initNavi();
//                        mAmapNavi.resumeNavi();

//                    }

//                    arrayAdapter.add(poiItem.toString());
                }
//                mlistview.setAdapter(arrayAdapter);

//                arrayAdapter.notifyDataSetChanged();
//            }
        }
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }





    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

        Log.d("SearchPage","Distance  "+driveRouteResult.getPaths().get(0).getDistance());
        arrayAdapter.add(names.pop() + "  Distance  " + driveRouteResult.getPaths().get(0).getDistance());
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString();
        Log.d("SearchPage","text changed");
        query = new PoiSearch.Query(newText, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }





    /**
     * 初始化路线描述信息和加载线路
     */
    private void initNavi() {
        Log.d("SearchPage","initNavi");

        AMapNaviPath naviPath = mAmapNavi.getNaviPath();
        if (naviPath == null) {
            Log.d("SearchPage","initNavi  naviPath null");
            return;
        }
        // 获取路径规划线路，显示到地图上
//        PoiOverlay poiOverlay = new PoiOverlay(mAmap,null);



        double length = ((int) (naviPath.getAllLength() / (double) 1000 * 10))
                / (double) 10;
        // 不足分钟 按分钟计
        int time = (naviPath.getAllTime() + 59) / 60;
        int cost = naviPath.getTollCost();
//        mRouteDistanceView.setText(String.valueOf(length));
//        mRouteTimeView.setText(String.valueOf(time));
//        mRouteCostView.setText(String.valueOf(cost));
        Log.d("SearchPage",String.valueOf(length));
    }




    //---------------\
    /**
     * 导航回调函数

     */
    @Override
    public void onInitNaviFailure() {
        Log.d("SearchPage","onInitNaviFailure");
    }

    @Override
    public void onInitNaviSuccess() {
        Log.d("SearchPage","onInitNaviSuccess");
    }

    @Override
    public void onStartNavi(int i) {
        Log.d("SearchPage","onStartNavi");
    }

    @Override
    public void onTrafficStatusUpdate() {
        Log.d("SearchPage","onTrafficStatusUpdate");
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        Log.d("SearchPage","onLocationChange");
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        Log.d("SearchPage","onGetNavigationText");
    }

    @Override
    public void onEndEmulatorNavi() {
        Log.d("SearchPage","onEndEmulatorNavi");
    }

    @Override
    public void onArriveDestination() {
        Log.d("SearchPage","onArriveDestination");
    }

    @Override
    public void onCalculateRouteSuccess() {
        Log.d("SearchPage","onCalculateRouteSuccess");
        initNavi();
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        Log.d("SearchPage","onCalculateRouteFailure");
    }

    @Override
    public void onReCalculateRouteForYaw() {
        Log.d("SearchPage","onReCalculateRouteForYaw");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        Log.d("SearchPage","onReCalculateRouteForTrafficJam");
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }
}
