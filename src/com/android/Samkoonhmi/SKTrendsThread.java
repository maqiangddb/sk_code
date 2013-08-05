/**
 * 李镇,2012/06/19,这个是曲线的后台处理线程,曲线的计算在此完成
 */


package com.android.Samkoonhmi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;



public class SKTrendsThread {
	private static final String TAG="SKTrendsThread";
	public static int TASK_ID;

	//带消息循环的线程
	private static HandlerThread mThread;
	//保存已注册的控件
	private HashMap<String, ICallback> mCallbacks=null;
	private SKHandler mHandler;
	
	//单例
	private static SKTrendsThread sInstance=null;
	public synchronized static SKTrendsThread getInstance(){
		if (sInstance==null) {
			sInstance=new SKTrendsThread();
		}
		return sInstance;
	}

	private SKTrendsThread() {
		if (mThread==null) {
			TASK_ID=0;
			mThread=new HandlerThread("TrendsbackThread");
			mThread.start();
			mHandler=new SKHandler(mThread.getLooper());
			mCallbacks=new HashMap<String, SKTrendsThread.ICallback>();
		}
	}

	public void startThread() {
		if (mThread!=null) {
		}
	}

	public void stopThread() {
		if(mThread!=null){
			mThread.stop();
		}
	}

	class SKHandler extends Handler{

		public SKHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mCallbacks==null) {
				return;
			}
			switch (msg.what) {
			case MODULE.MODULE_TEST:
				//测试用的,具体
				test((SKMsg)msg.obj);
				break;
			case MODULE.CAL_CURVE:
				//曲线
				curve((SKMsg)msg.obj);
				break;
			}
		}
		
	}
	

	
	/**
	 * 测试模块
	 */
	private void test(SKMsg msg){
		if (mCallbacks.containsKey(msg.taskName)) {
			mCallbacks.get(msg.taskName).onUpdate("回调测试",msg.nTaskId);
		}
	}
	
	
	/**
	 * 曲线
	 */
	private void curve(SKMsg msg){
		if (msg!=null) {
			switch (msg.nTaskId) {
			case TASK.READ_ITEM_DATA:
			case TASK.CURVE_CAL_DATA:
			{
				//曲线计算
				ICallback callback=doCallback(1,null,msg.taskName);
				if (callback!=null) {
					callback.onUpdate(0,msg.nTaskId);
				}
				break;
			}
			default:
				break;
			}
		}
	}
	
	/**
	 * 绑定控件
	 */
	SKTrendsThread.IBinder binder=new SKTrendsThread.IBinder() {

		@Override
		public String onRegister(ICallback callback) {
			
			TASK_ID++;
			doCallback(2,callback,TASK_ID + "");
			return TASK_ID+"";
		}

		@Override
		public boolean onDestroy(ICallback callback,String taskName) {
			boolean b=false;
			doCallback(3,callback,taskName);
			return b;
		}

		@Override
		public boolean onTask(int moduleId,int taskId, String taskName) {
			SKMsg msg=new SKMsg();
			msg.taskName=taskName;
			msg.nTaskId=taskId;
			mHandler.removeMessages(moduleId);
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId,int taskId, String taskName, int param) {
			SKMsg msg=new SKMsg();
			msg.taskName=taskName;
			msg.nTaskId=taskId;
			msg.nParam=param;
			mHandler.removeMessages(moduleId);
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId,int taskId, String taskName, String param) {
			SKMsg msg=new SKMsg();
			msg.taskName=taskName;
			msg.nTaskId=taskId;
			msg.sParam=param;
			mHandler.removeMessages(moduleId);
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId,int taskId, String taskName, int nparam,
				String sparam) {
			SKMsg msg=new SKMsg();
			msg.taskName=taskName;
			msg.nTaskId=taskId;
			msg.nParam=nparam;
			msg.sParam=sparam;
			mHandler.removeMessages(moduleId);
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}
		
		@Override
		public boolean onTask(int moduleId,int taskId, String taskName, Object param) {
			SKMsg msg=new SKMsg();
			msg.taskName=taskName;
			msg.nTaskId=taskId;
			msg.mParam=param;
			mHandler.removeMessages(moduleId);
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		
	};
	
	/**
	 * 任务信息对象
	 */
	class SKMsg{
		String taskName;  //任务名
		int nTaskId;      //任务ID
		int nParam;       //int 类型参数
		String sParam;    //String 类型参数
		Object mParam;    //自定义对象 类型参数
	}
	
	/**
	 * 绑定接口
	 */
	public interface IBinder{
		/**
		 * 注册回调接口
		 * @param callback,回调接口
		 * @return 返回nTaskId,返回值,作为任务名称
		 */
		String onRegister(ICallback callback);
		
		//取消回调
		boolean onDestroy(ICallback callback,String taskName);
		
		/**
		 * @param moduleId,模块Id,例如 开关按钮是一个模块,图表是一个模块
		 * @param taskId,任务Id,模块里面的任务
		 * @param taskName,任务名称,注册时返回的值
		 */
		boolean onTask(int moduleId,int taskId,String taskName);
		
		//执行任务,带int 参数
		boolean onTask(int moduleId,int taskId,String taskName,int param);
		
		//执行任务,带String 参数
		boolean onTask(int moduleId,int taskId,String taskName,String param);
		
		//执行任务,带int,String 参数
		boolean onTask(int moduleId,int taskId,String taskName,int nparam,String sparam);
		
		//执行任务,带自定义对象
		boolean onTask(int moduleId,int taskId,String taskName,Object param);
	}
	
	/**
	 * 回调接口
	 */
	public interface ICallback{
		/**
		 * 回传的数据类型是 String
		 * @param msg-回传数据
		 * @param taskId-任务Id,模块里面的任务
		 */
		void onUpdate(String msg,int taskId);
		
		/**
		 * 回传的数据类型是int or boolean
		 * @param msg-回传数据
		 * @param taskId-任务Id,模块里面的任务
		 */
		void onUpdate(int msg,int taskId);
		
		/**
		 * 回传的数据类型是自定义对象
		 * @param msg-回传数据
		 * @param taskId-任务Id,模块里面的任务
		 */
		void onUpdate(Object msg,int taskId);
		
	}
	
	/**
	 * 删除已经注册的
	 */
	private void removeReigter(ICallback callback){
		if (mCallbacks!=null) {
			try {
				
				for(Iterator<Entry<String, ICallback>> iters = mCallbacks.entrySet()
						.iterator(); iters.hasNext();) {
					Map.Entry<String, ICallback> entrys = iters.next();
					String key=entrys.getKey();
					if (entrys.getValue()==callback) {
						mCallbacks.remove(key);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void destory(){
		doCallback(4,null,null);
	}

	/**
	 * 处理回调
	 * @param type 1=获取
	 *        type 2=注册
	 *        type 3=删除注册
	 *        type 4=清除
	 */
	private synchronized ICallback doCallback(int type,ICallback callback,String name){
		ICallback iCallback = null;
		if (type==1) {
			//获取
			if (name == null || name.equals("")) {
				return null;
			}
			if (mCallbacks != null) {
				if(mCallbacks.containsKey(name)){
					return mCallbacks.get(name);
				}
			}
			return iCallback;
		}else if (type==2) {
			//注册
            if (mCallbacks != null) {
            	if (mCallbacks!=null) {
    				removeReigter(callback);
    				mCallbacks.put(name, callback);
    			}
			}
		}else if (type==3) {
			//删除注册
			if (mCallbacks!=null) {
				mCallbacks.remove(name);
			}
		}else if (type==4) {
			//清除
			if (mCallbacks!=null) {
				mCallbacks.clear();
				TASK_ID=0;
			}
		}
		return iCallback;
	}
	
	public SKTrendsThread.IBinder getBinder() {
		return binder;
	}
}

