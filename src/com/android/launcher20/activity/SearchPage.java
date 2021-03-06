package com.android.launcher20.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.speech.SpeechRecognizer;
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
import android.widget.Toast;

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
import com.android.launcher20.util.ApkInstaller;
import com.android.launcher20.util.FucUtil;
import com.android.launcher20.util.JsonParser;
import com.android.launcher20.util.TTSController;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

/**
 * Created by root on 15-9-12.
 */
public class SearchPage extends Activity implements PoiSearch.OnPoiSearchListener, RouteSearch.OnRouteSearchListener,TextWatcher
{
    //-----语音识别-----
    private static String TAG = SearchPage.class.getSimpleName();
    // 语音听写对象
    private com.iflytek.cloud.SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

//    private EditText mResultText;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
//     语记安装助手类
    ApkInstaller mInstaller;

    //-----------------v1.0----
//    private String TAG = "SearchPage";
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

//
        initView();
//        readytoRecognizer();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(SearchPage.this, mInitListener);
        mSharedPreferences = getSharedPreferences("com.android.launcher20",
                Activity.MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        mResultText = ((EditText) findViewById(R.id.iat_text));
        mInstaller = new ApkInstaller(SearchPage.this);
        mAutoText.setText(null);// 清空显示内容
        mIatResults.clear();
        // 设置参数
        setParam();
        boolean isShowDialog = mSharedPreferences.getBoolean(
                "iat_show", true);

            // 不显示听写对话框
            int ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：" + ret);
            } else {
                showTip("请开始说话");
            }


    }
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(SearchPage.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mAutoText.setText(resultBuffer.toString());
//        mAutoText.setSelection(mResultText.length());
    }

    /**
     * 参数设置
     *
     * @param param
     * @return
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
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
