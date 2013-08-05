package com.android.Samkoonhmi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.Samkoonhmi.model.TrendsDataInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;

/**
 * 曲线开关检测
 */
public class TrendsWatch {

	private static final String TAG="TrendsWatch";
	private static final int HANDLER_LOOPER=1;
	private static final int HANDLER_INIT=2;
	public static boolean flag=false;
	// 监视间隔时间
	private static int nTime = 500;
	//监视线程
	private HandlerThread mThread;
	//要监视的地址信息
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
	//地址值
	private Vector<Integer> tmpDataList = null;
	private SKTrendHandler handler;
	private ArrayList<ITrendsCallback> mCallbacks=null;

	/*地址的值的一个映射*/
	private HashMap<AddrProp, AddrNoticProp> addrMapIndexList = new HashMap<AddrProp, AddrNoticProp>();

	/*控制线程启动和停止的变量*/
	private boolean m_bThreadLoop = false;
		
	private static TrendsWatch sInstance=null;
	public static TrendsWatch getInstance(){
		if (sInstance==null) {
			sInstance=new TrendsWatch();
		}
		return sInstance;
	}

	private TrendsWatch() {
		mThread =new HandlerThread("TrendsWatchThread");
		mThread.start();
		handler=new SKTrendHandler(mThread.getLooper());
	}

/**
	 * 线程启动
	 */
	public void start()
	{
		m_bThreadLoop = true;
	}
	
	public void stop()
	{
		clearAllNotic();
		m_bThreadLoop = false; 
//		Log.d(TAG, "stop m_bThreadLoop "+m_bThreadLoop);

	}
	
	/**
	 * 清除所有通知
	 */
	private void clearAllNotic()
	{		
		if(null != addrMapIndexList)
		{
			addrMapIndexList.clear();
		}
	}

	/**
	 * 初始化
	 */
	private void init(){
		
		if(flag==false)
		{
			flag = true;
			tmpDataList = new Vector<Integer>();
			mCallbacks=new ArrayList<TrendsWatch.ITrendsCallback >();
		}
	}

/**
	 * 增加通知地址属性函数接口
	 * @param addr
	 * @param noticProp
	 */
	public void addNoticProp(AddrProp addr)
	{
		
		if(null == addr)
		{
			Log.e("addNoticProp", "地址为空，添加回调不成功");
			return ;
		}
		
	/*添加地址*/
		if(null == addrMapIndexList)
		{
			addrMapIndexList = new HashMap<AddrProp, AddrNoticProp>();
		}
				
		/*是否已经存在这个地址*/
		if(addrMapIndexList.containsKey(addr))
		{
		}
		else
		{
			AddrNoticProp tmpANP = new AddrNoticProp();
			tmpANP.bBitAddr = true;
			addrMapIndexList.put(addr,tmpANP);
		}
		if(m_bThreadLoop==false)
			doWatch();
	}


	/**
	 * 地址通知结构体
	 *
	 */
	private class AddrNoticProp{
		boolean bBitAddr = false;
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
//		Log.d(TAG, "doWatch flag "+flag+ "m_bThreadLoop "+m_bThreadLoop);
		try {
			
				/*取得地址的集合*/
					Set<AddrProp> addrList = addrMapIndexList.keySet();
					
					for(Iterator<AddrProp> it = addrList.iterator();  it.hasNext(); )
					{
						AddrProp addr=it.next();
						mSendData.eDataType = DATA_TYPE.BIT_1;
						mSendData.eReadWriteCtlType=READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
						tmpDataList.clear();
					 	boolean bSuccess =PlcRegCmnStcTools.getRegIntData(addr,tmpDataList,mSendData);
//						Log.d(TAG, "onNotice bSuccess:"+bSuccess+"tmpDataList "+tmpDataList);
					 	if (bSuccess) {
						 sendData(addr,tmpDataList);
						}
					}
				
			if (flag) {
				//循环监视
	//			Log.d(TAG, "HANDLER_LOOPER m_bThreadLoop "+m_bThreadLoop);
				handler.sendEmptyMessageDelayed(HANDLER_LOOPER,nTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "TrendsWatch doWatch error!");
		}
	}
	

	/**
	 * 曲线消息
	 * @param addr-地址信息
	 * @param vector-数据
	 */
	private void sendData(AddrProp addr,Vector<Integer> vector){
		
		TrendsDataInfo data=new TrendsDataInfo();
		data.setnNumofAddr(addr);
		data.setnNumOfValue(vector.get(0));
		if (mCallbacks!=null) {
			for (int i = 0; i < mCallbacks.size(); i++) {
	//			Log.d(TAG, "onNotice i:"+i);
				mCallbacks.get(i).onTrends(data);
			}
		}
	}


	class SKTrendHandler extends Handler{

		SKTrendHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==HANDLER_LOOPER) {
				//监视曲线地址变化
//			Log.d(TAG, "handleMessage m_bThreadLoop "+m_bThreadLoop);
				if(m_bThreadLoop)
						doWatch();
			}else if (msg.what==HANDLER_INIT) {
				//初始化数据
				init();
			}
		}
		
	}
	
	
	public void destroy(){
		if (mCallbacks!=null) {
			mCallbacks.clear();
		}
	}
	
	/**
	 * 获取绑定控件
	 */
	TrendsWatch.ITrendsBinder binder=new TrendsWatch.ITrendsBinder() {
		
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
	public interface ITrendsBinder{
		//注册回调
        boolean onRegister(ITrendsCallback callback);
		//取消回调
		boolean onDestroy(ITrendsCallback callback);
	}
	
	/**
	 * 曲线显现触发，通知已注册的曲线控件
	 */
	public interface ITrendsCallback{
		/**
		 * 清除监测
		 */
	//	void onClear();
		
		/**
		 * 监测发生
		 */
		void onTrends(TrendsDataInfo info);
	}
	
	/**
	 * 判断是否已经注册
	 */
	private void isRegister(ITrendsCallback callback){
		if (mCallbacks==null) {
			mCallbacks=new ArrayList<TrendsWatch.ITrendsCallback>();
		}
		for (int i = 0; i < mCallbacks.size(); i++) {
			if (mCallbacks.get(i)==callback) {
				mCallbacks.remove(i);
				break;
			}
		}
	}
	
	/**
	 * 获取绑定对象
	 */
	public TrendsWatch.ITrendsBinder getBinder(){
		return binder;
	}
}
