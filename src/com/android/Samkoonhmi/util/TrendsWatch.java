package com.android.Samkoonhmi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.Samkoonhmi.model.CallbackItem;
import com.android.Samkoonhmi.model.TrendsDataInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;

/**
 * 曲线开关检测
 */
public class TrendsWatch {

	private static final String TAG = "TrendsWatch";
	private static final int HANDLER_LOOPER = 1;
	private static final int HANDLER_INIT = 2;
	public static boolean flag = false;
	// 监视间隔时间
	private static int nTime = 200;
	// 监视线程
	private HandlerThread mThread;
	// 地址值
	private SKTrendHandler handler;
	private ArrayList<ITrendsCallback> mCallbacks = null;
	private HashMap<AddrProp, CallbackItem> mCallItem = new HashMap<AddrProp, CallbackItem>();
	/* 控制线程启动和停止的变量 */
	private boolean m_bThreadLoop = false;

	private static TrendsWatch sInstance = null;

	public static TrendsWatch getInstance() {
		if (sInstance == null) {
			sInstance = new TrendsWatch();
		}
		return sInstance;
	}

	private TrendsWatch() {
		mThread = new HandlerThread("TrendsWatchThread");
		mThread.start();
		handler = new SKTrendHandler(mThread.getLooper());
	}

	/**
	 * 线程启动
	 */
	public void start() {
		m_bThreadLoop = true;
	}

	public void stop() {
		clearAllNotic();
		m_bThreadLoop = false;
		// Log.d(TAG, "stop m_bThreadLoop "+m_bThreadLoop);

	}

	/**
	 * 清除所有通知
	 */
	private void clearAllNotic() {
		if (null != mCallItem) {
			mCallItem.clear();
		}
	}

	/**
	 * 初始化
	 */
	private void init() {

		if (flag == false) {
			flag = true;
			mCallbacks = new ArrayList<TrendsWatch.ITrendsCallback>();
		}
	}

	/**
	 * 增加通知地址属性函数接口
	 * 
	 * @param addr
	 * @param noticProp
	 */
	public void addNoticProp(AddrProp addr,int sid) {

		if (null == addr) {
			Log.e("addNoticProp", "地址为空，添加回调不成功");
			return;
		}

		if (!mCallItem.containsKey(addr)) {
			CallbackItem item=new CallbackItem();
			item.eDataType = DATA_TYPE.BIT_1;
			item.nType=1;
			item.onRegister(addr, true,sid);
			mCallItem.put(addr, item);
		}
		
		
		if (m_bThreadLoop == false){
			doWatch();
		}
	}

	/**
	 * 开始曲线监视
	 */
	public void startWatch() {
		handler.sendEmptyMessageDelayed(HANDLER_INIT, 10);
	}

	/**
	 * 报警地址监视
	 */
	private void doWatch() {
		m_bThreadLoop = true;
		try {
			
			for (Iterator<Entry<AddrProp, CallbackItem>> it = mCallItem.entrySet().iterator(); it.hasNext();) {
				Map.Entry<AddrProp, CallbackItem> entry = (Map.Entry<AddrProp, CallbackItem>) it.next();
				CallbackItem item=entry.getValue();
				if (item!=null) {
					if (item.isbChange()) {
						sendData(entry.getKey(), item.getnIValue());
						item.setbChange(false);
					}
				}
			}

			if (flag) {
				// 循环监视
				handler.sendEmptyMessageDelayed(HANDLER_LOOPER, nTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "TrendsWatch doWatch error!");
		}
	}

	/**
	 * 曲线消息
	 * 
	 * @param addr
	 *            -地址信息
	 * @param vector
	 *            -数据
	 */
	private void sendData(AddrProp addr, int value) {

		TrendsDataInfo data = new TrendsDataInfo();
		data.setnNumofAddr(addr);
		data.setnNumOfValue(value);
		if (mCallbacks != null) {
			for (int i = 0; i < mCallbacks.size(); i++) {
				// Log.d(TAG, "onNotice i:"+i);
				mCallbacks.get(i).onTrends(data);
			}
		}
	}

	class SKTrendHandler extends Handler {

		SKTrendHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == HANDLER_LOOPER) {
				// 监视曲线地址变化
				// Log.d(TAG, "handleMessage m_bThreadLoop "+m_bThreadLoop);
				if (m_bThreadLoop){
					doWatch();
				}
			} else if (msg.what == HANDLER_INIT) {
				// 初始化数据
				init();
			}
		}

	}

	public void destroy() {
		if (mCallbacks != null) {
			mCallbacks.clear();
		}
	}

	/**
	 * 获取绑定控件
	 */
	TrendsWatch.ITrendsBinder binder = new TrendsWatch.ITrendsBinder() {

		@Override
		public boolean onRegister(ITrendsCallback callback) {
			isRegister(callback);
			mCallbacks.add(callback);
			return true;
		}

		@Override
		public boolean onDestroy(ITrendsCallback callback) {
			mCallbacks.remove(callback);
			return true;
		}
	};

	/**
	 * 绑定接口
	 */
	public interface ITrendsBinder {
		// 注册回调
		boolean onRegister(ITrendsCallback callback);

		// 取消回调
		boolean onDestroy(ITrendsCallback callback);
	}

	/**
	 * 曲线显现触发，通知已注册的曲线控件
	 */
	public interface ITrendsCallback {
		/**
		 * 清除监测
		 */
		// void onClear();

		/**
		 * 监测发生
		 */
		void onTrends(TrendsDataInfo info);
	}

	/**
	 * 判断是否已经注册
	 */
	private void isRegister(ITrendsCallback callback) {
		if (mCallbacks == null) {
			mCallbacks = new ArrayList<TrendsWatch.ITrendsCallback>();
		}
		for (int i = 0; i < mCallbacks.size(); i++) {
			if (mCallbacks.get(i) == callback) {
				mCallbacks.remove(i);
				break;
			}
		}
	}

	/**
	 * 获取绑定对象
	 */
	public TrendsWatch.ITrendsBinder getBinder() {
		return binder;
	}
}
