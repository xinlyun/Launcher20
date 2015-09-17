package com.android.launcher20.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
{
    private String TAG = "SearchPage";
    private String strStart ;
    private String cityCode;
//    private LatLonPoint startPoint;
//    private PoiSearch.Query startSearchQuery;
//    private PoiSearch.Query endSearchQuery;
    private RouteSearch routeSearch;
    private EditText mAutoText;
    private ListView mlistview;
    private LatLonPoint mLocation;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private ArrayAdapter<String> arrayAdapter;
    private MyAdapter myAdapter;
    private ArrayList<ListItem> listItems;
    private AMapNavi mAmapNavi;
    private List<NaviLatLng> startPoint;
    private List<NaviLatLng> endPoint,wayPoint;
    private AMapNaviListener mAmapNaviListener;
//    private String posi;
    private Stack<String> names;
    private List<PoiItem> poiItems;
    private boolean calueSuccess=false;
    private AMapNavi mapNavi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchpage);
        names = new Stack<>();
        initView();
    }

    private void initView(){
        mapNavi = AMapNavi.getInstance(getApplicationContext());
//        mapNavi.setAMapNaviListener(getAMapNaviListener());
        listItems = new ArrayList<ListItem>();
        myAdapter = new MyAdapter(this,R.layout.searchpage_listview_item);

        startPoint = new ArrayList<NaviLatLng>();
        endPoint = new ArrayList<NaviLatLng>();
        wayPoint = new ArrayList<NaviLatLng>();

        mAmapNavi = AMapNavi.getInstance(this);
        mAmapNavi.setAMapNaviListener(getAMapNaviListener());

        mAutoText = (EditText) findViewById(R.id.id_searchpage);
        mAutoText.addTextChangedListener(this);
        mlistview = (ListView) findViewById(R.id.id_search_listview);
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);

//        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
//        mlistview.setAdapter(arrayAdapter);
        mlistview.setAdapter(myAdapter);

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

        /**
         * 当被展示的地点被点击时，获取地点坐标，根据当前坐标重新开始计算路径，因为AMapNavi是单例对象，在其完成路径计算后即可关闭当前活动
         */
        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SearchPage", "onItemCLick");
                PoiItem poiItem = poiItems.get(position);

                endPoint.clear();
                endPoint.add(new NaviLatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()));

//                AMapNavi.getInstance(SearchPage.this).calculateDriveRoute(startPoint, endPoint,
//                        wayPoint, AMapNavi.DrivingDefault);
                mapNavi.calculateDriveRoute(startPoint,endPoint,wayPoint,AMapNavi.DrivingDefault);

//              SearchPage.this.finish();
                SearchPage.this.onBackPressed();
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

//                arrayAdapter.clear();
                myAdapter.clear();
                names.clear();
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
                for(PoiItem poiItem:poiItems){

//                    RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mLocation,poiItem.getLatLonPoint());
//                    RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,RouteSearch.DrivingDefault,null,null,"");
//
////                        routeSearch.calculateDriveRouteAsyn(query);
//
//                    names.push(poiItem.toString());
                    ListItem listItem = new ListItem(poiItem.toString(),poiItem.getTypeDes(),poiItem.getSnippet());
                    myAdapter.addItem(listItem);
                    myAdapter.notifyDataSetChanged();

                }

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

        Log.d("SearchPage", "Distance  " + driveRouteResult.getPaths().get(0).getDistance());
//        arrayAdapter.add(names.pop() + "  Distance  " + driveRouteResult.getPaths().get(0).getDistance());
//        arrayAdapter.notifyDataSetChanged();

            ListItem listItem = new ListItem(names.pop(), "" + driveRouteResult.getPaths().get(0).getDistance(),
                    "" + driveRouteResult.getPaths().get(0).getDuration());
            myAdapter.addItem(listItem);
            myAdapter.notifyDataSetChanged();

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
    /**
     * 导航回调函数

     */
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
                    Log.d(TAG,"onTrafficStatusUpdate");
                }

                @Override
                public void onStartNavi(int arg0) {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onStartNavi");
                }

                @Override
                public void onReCalculateRouteForYaw() {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onReCalculateRouteForYaw");
                }

                @Override
                public void onReCalculateRouteForTrafficJam() {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onReCalculateRouteForTrafficJam");
                }

                @Override
                public void onLocationChange(AMapNaviLocation location) {
                    Log.d(TAG,"onLocationChange");

                }

                @Override
                public void onInitNaviSuccess() {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onInitNaviSuccess");
                }

                @Override
                public void onInitNaviFailure() {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onInitNaviFailure");
                }

                @Override
                public void onGetNavigationText(int arg0, String arg1) {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onGetNavigationText");
                }

                @Override
                public void onEndEmulatorNavi() {
                    // TODO Auto-generated method stub
                    Log.d(TAG,"onEndEmulatorNavi");
                }

                @Override
                public void onCalculateRouteSuccess() {
                    SearchPage.this.finish();
                    Log.d(TAG, "onCalculateRouteSuccess");
                }

                @Override
                public void onCalculateRouteFailure(int arg0) {
                    Log.d(TAG, "onCalculateRouteFailure");
                }

                @Override
                public void onArrivedWayPoint(int arg0) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onArrivedWayPoint");
                }

                @Override
                public void onArriveDestination() {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onArriveDestination");
                }

                @Override
                public void onGpsOpenStatus(boolean arg0) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onGpsOpenStatus");
                }

                @Override
                public void onNaviInfoUpdated(AMapNaviInfo arg0) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onNaviInfoUpdated");
                }

                @Override
                public void onNaviInfoUpdate(NaviInfo arg0) {

                    // TODO Auto-generated method stub
                    Log.d(TAG, "onNaviInfoUpdate");
                }
            };
        }
        return mAmapNaviListener;
    }








//-------------------------以下为ListView的适配器使用类------------------------
    class ListItem {
        String name ;
        String distance;
        String time;
        int id;
        public ListItem(String name,String distance,String time){
            this.name = name ;
            this.distance = distance;
            this.time = time;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getTime() {
            return time;
        }

        public String getDistance() {
            return distance;
        }
    }

    /**
     * ListView适配器
     */
    class MyAdapter extends ArrayAdapter<ListItem>{

        List<ListItem> listItems;
        public MyAdapter(Context context, int resource,List<ListItem> listItems) {
            super(context, resource);
            this.listItems = listItems;
        }

        public MyAdapter(Context context,int resource){
            super(context,resource);
            listItems = new ArrayList<ListItem>();
        }

        public void setListItems(ArrayList<ListItem> listItems){
            this.listItems = listItems;
        }
        @Override
        public void clear() {
            super.clear();
            this.listItems.clear();
        }

        public void addItem(ListItem listItem){
            add(listItem);
            this.listItems.add(listItem);
        }






        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListItem listItem = listItems.get(position);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.searchpage_listview_item, null);
            TextView name,distance,time;
            name = (TextView) view.findViewById(R.id.id_search_listview_item_name);
            distance = (TextView) view.findViewById(R.id.id_search_listview_item_distance);
            time = (TextView) view.findViewById(R.id.id_search_listview_item_time);
            name.setText(listItem.getName());
            distance.setText(listItem.getDistance());
            time.setText(listItem.getTime());
            return view;
        }
    }


}
