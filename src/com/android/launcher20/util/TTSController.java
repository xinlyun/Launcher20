package com.android.launcher20.util;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviInfo;
import com.android.launcher20.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;


/**
 * 语音播报组件
 *
 */
public class TTSController implements SynthesizerListener, AMapNaviListener {

	public static TTSController ttsManager;
	private String TAG = "TTSController";
	private Context mContext;
	// 合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;
	private SpeechRecognizer mSpeechRecognizer;
	TTSController(Context context) {
		mContext = context;
	}

	public static TTSController getInstance(Context context) {
		if (ttsManager == null) {
			ttsManager = new TTSController(context);

		}
		return ttsManager;
	}

	public void initSynthesizer() {
		SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=5608e250");

//		5608e250
//		SpeechUser.getUser().login(mContext, null, null,
//				"appid=" + mContext.getString(R.string.app_id), listener);
		// 初始化合成对象.


		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,mInitListener);
//		SpeechRecognizer mIat= SpeechRecognizer.createRecognizer(mContext, null);
		initSpeechSynthesizer();
	}






	//初始化监听器，只有在使用本地语音服务时需要监听（即安装讯飞语音+，通过语音+提供本地服务），初始化成功后才可进行本地操作。
	InitListener mInitListener = new InitListener() {
		public void onInit(int code) {
			if (code == ErrorCode.SUCCESS) {}}
	};


	/**
	 * 使用SpeechSynthesizer合成语音，不弹出合成Dialog.
	 *
	 * @param
	 */
	public void playText(String playText) {
		if (!isfinish) {
			return;
		}
		if (null == mSpeechSynthesizer) {
			// 创建合成对象.
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext,null);
			initSpeechSynthesizer();
		}
		// 进行语音合成.
		mSpeechSynthesizer.startSpeaking(playText, getSynthesizerListener());

	}

	public void stopSpeaking() {
		if (mSpeechSynthesizer != null)
			mSpeechSynthesizer.stopSpeaking();
	}
	public void startSpeaking() {
		 isfinish=true;
	}

	private void initSpeechSynthesizer() {
		// 设置发音人
//		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "50");//设置语速
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
		mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL); //设置云端

	}



	@Override
	public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(int i, int i1, int i2, Bundle bundle) {

	}

	boolean isfinish = true;

	@Override
	public void onCompleted(SpeechError arg0) {
		// TODO Auto-generated method stub
		isfinish = true;
	}

	@Override
	public void onSpeakBegin() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onSpeakBegin");
		isfinish = false;

	}

	@Override
	public void onSpeakPaused() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSpeakProgress(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onSpeakProgress");
	}

	@Override
	public void onSpeakResumed() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onSpeakResumed");
	}

	SynthesizerListener synthesizerListener;
	private SynthesizerListener getSynthesizerListener(){
		if(synthesizerListener==null){
			synthesizerListener = new SynthesizerListener() {
				@Override
				public void onSpeakBegin() {
					Log.d(TAG,"onSpeakBegin");
				}

				@Override
				public void onBufferProgress(int i, int i1, int i2, String s) {
					Log.d(TAG,"onBufferProgress");
				}

				@Override
				public void onSpeakPaused() {
					Log.d(TAG,"onSpeakPaused");
				}

				@Override
				public void onSpeakResumed() {
					Log.d(TAG,"onSpeakResumed");
				}

				@Override
				public void onSpeakProgress(int i, int i1, int i2) {
					Log.d(TAG,"onSpeakProgress");
				}

				@Override
				public void onCompleted(SpeechError speechError) {
					Log.d(TAG,"onCompleted");
				}

				@Override
				public void onEvent(int i, int i1, int i2, Bundle bundle) {
					Log.d(TAG,"onEvent");
				}
			};
		}
		return synthesizerListener;
	}



//	@Override
//	public void onCompleted(int i) throws RemoteException {
//
//	}

	public void destroy() {
		if (mSpeechSynthesizer != null) {
			mSpeechSynthesizer.stopSpeaking();
		}
	}

	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub
		this.playText("到达目的地");
	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		 this.playText("路径计算失败，请检查网络或输入参数");
	}

	@Override
	public void onCalculateRouteSuccess() {
		String calculateResult = "路径计算就绪";

		this.playText(calculateResult);
	}

	@Override
	public void onEndEmulatorNavi() {
		this.playText("导航结束");

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub
		this.playText(arg1);
	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReCalculateRouteForTrafficJam() {
		// TODO Auto-generated method stub
		this.playText("前方路线拥堵，路线重新规划");
	}

	@Override
	public void onReCalculateRouteForYaw() {

		this.playText("您已偏航");
	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
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

//	@Override
//	public IBinder asBinder() {
//		return null;
//	}



}
