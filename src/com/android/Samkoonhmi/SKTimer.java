/**
 * 李镇,2012/05/08,这个是系统的定时器,暂时定位100ms
 * 说明
 * 1.通过SKTimer单列获取binder对象,通过binder绑定控件
 * 2.退出取消绑定
 * 3.控件需实现ICallback 接口
 * 4.注册时,需要放在UI初始化后
 */

package com.android.Samkoonhmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Map.Entry;

import com.android.Samkoonhmi.SKScene.DrawThread;

import android.content.Loader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SKTimer {
	
	//更新时间,100Ms作为基数,自己根据需要,筛选信息
	private static final String TAG="SKTimer";
	public static final int UPDATE_TIME=100;
	//保存注册控件
	private HashMap<Integer, ArrayList<ICallback>> mCallBack;
	//定时时间
	private Vector<Integer> mTimes;
	public static boolean flag;
	private static int nTimeCount=0;
	private AKTimer mAkTimer;
	
	//单例
	private static SKTimer sInstance=null;
	public synchronized static SKTimer getInstance(){
		if (sInstance==null) {
			sInstance=new SKTimer();
		}
		return sInstance;
	}
	
	private SKTimer() {
		mTimes=new Vector<Integer>();
		mCallBack=new HashMap<Integer, ArrayList<ICallback>>();
	}
	
	/**
	 * 启动定时器
	 */
	public void startTimer(){
		flag=true;
		mAkTimer = new AKTimer();
		mAkTimer.setName("AkTimer");
		mAkTimer.start();
	}
	
	
	
	/**
	 * 定时器
	 */
	class AKTimer extends Thread{

		@Override
		public void run() {
			super.run();
			try {
				while (flag) {
					long start=System.currentTimeMillis();
					nTimeCount++;
					if (mCallBack!=null) {
						if (mTimes!=null) {
							for (int i = 0; i < mTimes.size(); i++) {
								if (nTimeCount%mTimes.get(i)==0) {
									if (mCallBack.containsKey(mTimes.get(i))) {
										ArrayList<ICallback> list=mCallBack.get(mTimes.get(i));
										if (list!=null) {
											//Log.d(TAG, "...........size:"+list.size()+",time:"+nTimeCount);
											for (int j = 0; j < list.size(); j++) {
												list.get(j).onUpdate();
											}
										}
									}
								}
							}
						}
					}
					
					if (nTimeCount>=2147483640) {
						nTimeCount=0;
					}
					
					long time=System.currentTimeMillis()-start;
					time=UPDATE_TIME-time;
					if (time<5) {
						time=5;
					}
					sleep(time);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "AKTimer error!");
			}
		}
		

	}

	/**
	 * 绑定控件
	 */
	private IBinder binder=new IBinder() {
		
		@Override
		public boolean onRegister(ICallback callback) {
			boolean b=false;
			int time=1;
			if (mCallBack!=null) {
				if (mCallBack.containsKey(time)) {
					deleteRegiter(callback,time);
					if (mCallBack.containsKey(time)) {
						ArrayList<ICallback> list=mCallBack.get(time);
						if (list!=null) {
							list.add(callback);
							b=true;
						}
					}
				}else {
					addTime(time);
					ArrayList<ICallback> list=new ArrayList<SKTimer.ICallback>();
					list.add(callback);
					mCallBack.put(time, list);
					b=true;
				}
			}
			return b;
		}
		
		@Override
		public boolean onDestroy(ICallback callback) {
			boolean b=false;
			int time=1;
			if (mCallBack!=null) {
				if (mCallBack.containsKey(time)) {
					ArrayList<ICallback> list=mCallBack.get(time);
					if (list!=null) {
						list.remove(callback);
						b=true;
					}
				}
			}
			return b;
		}
		
		@Override
		public boolean onRegister(ICallback callback, int time) {
			boolean b=false;
			if (time<0) {
				return b;
			}
			if (mCallBack!=null) {
				if (mCallBack.containsKey(time)) {
					deleteRegiter(callback,time);
					if (mCallBack.containsKey(time)) {
						ArrayList<ICallback> list=mCallBack.get(time);
						if (list!=null) {
							list.add(callback);
							//Log.d(TAG, "time="+time+",size="+list.size()+",callback="+callback);
							b=true;
						}
					}
				}else {
					addTime(time);
					ArrayList<ICallback> list=new ArrayList<SKTimer.ICallback>();
					list.add(callback);
					//Log.d(TAG, "time="+time+",size="+list.size()+",callback="+callback);
					mCallBack.put(time, list);
					b=true;
				}
			}
			return b;
		}
		
		@Override
		public boolean onDestroy(ICallback callback, int time) {
			boolean b=false;
			if (mCallBack!=null) {
				if (mCallBack.containsKey(time)) {
					ArrayList<ICallback> list=mCallBack.get(time);
					if (list!=null) {
						list.remove(callback);
						b=true;
					}
				}
			}
			return b;
		}

		@Override
		public boolean isRegister(ICallback callback) {
			if (mCallBack!=null) {
				for (Iterator<Entry<Integer, ArrayList<ICallback>>> iters = mCallBack
						.entrySet().iterator(); iters.hasNext();) {
					
					Map.Entry<Integer,  ArrayList<ICallback>> entrys = iters.next();

					 ArrayList<ICallback> list = entrys.getValue();
					 if (list!=null) {
						for (int i = 0; i < list.size(); i++) {
							if (list.get(i)==callback) {
								return true;
							}
						}
					}
				}
			}
			return false;
		}

	};

	/**
	 * 删除已经注册的
	 */
	private void deleteRegiter(ICallback callback,int time){
		if(mCallBack!=null){
			if (mCallBack.containsKey(time)) {
				ArrayList<ICallback> list=mCallBack.get(time);
				if (list!=null) {
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i)==callback) {
							list.remove(i);
							break;
						}
					}
				}
			}
		}
	}
	
	private void addTime(int time){
		if (mTimes!=null) {
			mTimes.add(time);
		}
	}
	
	/**
	 * 销毁
	 */
	public void destroy(){
		flag=false;
		if (mCallBack!=null) {
			mCallBack.clear();
		}
		if (mTimes!=null) {
			mTimes.clear();
		}
	}
	
	public void clear(){
		if (mCallBack!=null) {
			mCallBack.clear();
		}
		if (mTimes!=null) {
			mTimes.clear();
		}
	}

	/**
	 * 绑定接口
	 */
	public interface IBinder{
		//注册回调接口
		boolean onRegister(ICallback callback);
		/**
		 * 注册回调接口，带时间单位
		 * time-以100毫秒位单位
		 * lg:time=3,表示每隔300毫秒回调一次
		 * 使用该注册，onDestroy也需要带时间
		 */
		boolean onRegister(ICallback callback,int time);
		//取消回调
		boolean onDestroy(ICallback callback);
		//取消回调
		boolean onDestroy(ICallback callback,int time);
		//是否已经注册
		boolean isRegister(ICallback callback);
	}
	
	/**
	 * 回调接口
	 */
	public interface ICallback{
		//更新
		void onUpdate();
	}
	
	
	public IBinder getBinder() {
		return binder;
	}
	
}