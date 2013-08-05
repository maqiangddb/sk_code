package com.android.Samkoonhmi.util;

import java.util.Vector;
import com.android.Samkoonhmi.model.alarm.AlarmCallbackInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class AlarmSaveThread {

	private static final String TAG="AlarmSaveThread";
	public static int TASK_ID;
	// 带消息循环的线程
	private static HandlerThread mThread;
	// 保存已注册的控件
	private static Vector<AlarmCallbackInfo> mCallbacks = null;//当前正在使用的
	private AlarmHandler mHandler;
	private boolean debug = true;

	// 单例
	private static AlarmSaveThread sInstance = null;
	public synchronized static AlarmSaveThread getInstance() {
		if (sInstance == null) {
			sInstance = new AlarmSaveThread();
		}
		return sInstance;
	}
	
	public AlarmSaveThread(){
		if (mThread == null) {
			TASK_ID = 0;
			mThread = new HandlerThread("AlarmSaveThread");
			mThread.start();

			mHandler = new AlarmHandler(mThread.getLooper());
			mCallbacks = new Vector<AlarmCallbackInfo>();
			//Log.d(TAG, "alarm save thread id:"+mThread.getId());
		}
	} 
	
	class AlarmHandler extends Handler{
		
		public AlarmHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MODULE.ALARM:
				SKMsg skMsg=(SKMsg)msg.obj;
				if (skMsg!=null) {
					alarm(skMsg);
				}
				break;
			}
		}
		
		
	}
	
	private void alarm(SKMsg msg){
		
		if (msg.taskName==null||msg.taskName.equals("")) {
			return;
		}
		
		ICallback callback=getCallback(msg.taskName);
		
		if(callback==null){
			return;
		}
	
		switch (msg.nTaskId) {
		case TASK.ALARM_WRITE:
			//报警数据保存
			callback.onUpdate(msg.mParam, msg.nTaskId);
			break;
		case TASK.ALARM_SELECT:// 报警数据查询
			callback.onUpdate(0, msg.nTaskId);
			break;
		case TASK.ALARM_ADD_REFRESH:// 产生报警更新界面
			callback.onUpdate(msg.mParam, msg.nTaskId);
			break;
		case TASK.ALARM_CLEAR_REFRESH:// 报警消除更新界面
			callback.onUpdate(msg.mParam, msg.nTaskId);
			break;
		case TASK.ALARM_CONFIRM_REFRESH:// 报警确定更新界面
			if (msg.sParam.equals("") && msg.nParam == 0) {
				callback.onUpdate("", msg.nTaskId);
			} else {
				String[] temp = new String[] { msg.nParam + "",
						msg.sParam };
				callback.onUpdate(temp, msg.nTaskId);
			}
			break;
		case TASK.ALARM_EXPORT:
			callback.onUpdate(0, msg.nTaskId);
			break;
		case TASK.ALARM_HIS_READ:
			callback.onUpdate(msg.mParam, msg.nTaskId);
			break;
		}
		
	}
	
	/**
	 * 绑定控件
	 */
	AlarmSaveThread.IBinder binder = new AlarmSaveThread.IBinder() {

		@Override
		public String onRegister(ICallback callback) {
			TASK_ID++;
			doCallback(2,callback,TASK_ID + "");
			return TASK_ID + "";
		}

		@Override
		public boolean onDestroy(ICallback callback, String taskName) {
			boolean b = false;
			doCallback(3, callback, taskName);
			return b;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, String taskName) {
			SKMsg msg = new SKMsg();
			msg.taskName = taskName;
			msg.nTaskId = taskId;
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, String taskName,
				int param) {
			SKMsg msg = new SKMsg();
			msg.taskName = taskName;
			msg.nTaskId = taskId;
			msg.nParam = param;
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, String taskName,
				String param) {
			SKMsg msg = new SKMsg();
			msg.taskName = taskName;
			msg.nTaskId = taskId;
			msg.sParam = param;

			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, String taskName,
				int nparam, String sparam) {
			SKMsg msg = new SKMsg();
			msg.taskName = taskName;
			msg.nTaskId = taskId;
			msg.nParam = nparam;
			msg.sParam = sparam;

			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, String taskName,
				Object param) {
			SKMsg msg = new SKMsg();
			msg.taskName = taskName;
			msg.nTaskId = taskId;
			msg.mParam = param;
			mHandler.obtainMessage(moduleId, msg).sendToTarget();
			return false;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, Object object,
				ICallback callback, int time) {
			SKMsg msg = new SKMsg();
			msg.taskName = "";
			msg.nTaskId = taskId;
			msg.mParam = object;
			msg.callback = callback;
			if (time > 0) {
				Message msgs = new Message();
				msgs.what = moduleId;
				msgs.obj = msg;
				mHandler.sendMessageDelayed(msgs, time);
			} else {
				mHandler.obtainMessage(moduleId, msg).sendToTarget();
			}
			return false;
		}

		@Override
		public boolean onTask(int moduleId, int taskId, String taskName,
				Object param, int time) {
			SKMsg msg = new SKMsg();
			msg.taskName = taskName;
			msg.nTaskId = taskId;
			msg.mParam = param;
			Message message=new Message();
			message.what=moduleId;
			message.obj=msg;
			mHandler.sendMessageDelayed(message, time);
			return false;
		}

	};

	
	/**
	 * 绑定接口
	 */
	public interface IBinder {
		/**
		 * 注册回调接口
		 * @param callback,回调接口
		 * @return 返回nTaskId,返回值,作为任务名称
		 */
		String onRegister(ICallback callback);

		// 取消回调
		boolean onDestroy(ICallback callback, String taskName);

		/**
		 * @param moduleId,模块Id,例如 开关按钮是一个模块,图表是一个模块
		 * @param taskId ,任务Id,模块里面的任务
		 * @param taskName ,任务名称,注册时返回的值
		 */
		boolean onTask(int moduleId, int taskId, String taskName);

		// 执行任务,带int 参数
		boolean onTask(int moduleId, int taskId, String taskName, int param);

		// 执行任务,带String 参数
		boolean onTask(int moduleId, int taskId, String taskName, String param);

		// 执行任务,带int,String 参数
		boolean onTask(int moduleId, int taskId, String taskName, int nparam,
				String sparam);

		// 执行任务,带自定义对象
		boolean onTask(int moduleId, int taskId, String taskName, Object param);
		
		// 执行任务,带自定义对象
		boolean onTask(int moduleId, int taskId, String taskName, Object param,int time);

		// 执行任务，带回调接口，moduleId,必须是 MODULE.CALLBACK
		boolean onTask(int moduleId, int taskId, Object object,
				ICallback callback, int time);
	}

	/**
	 * 回调接口
	 */
	public interface ICallback {
		/**
		 * 回传的数据类型是 String
		 * 
		 * @param msg-回传数据
		 * @param taskId-任务Id,模块里面的任务
		 */
		void onUpdate(String msg, int taskId);

		/**
		 * 回传的数据类型是int or boolean
		 * 
		 * @param msg -回传数据
		 * @param taskId-任务Id,模块里面的任务
		 */
		void onUpdate(int msg, int taskId);

		/**
		 * 回传的数据类型是自定义对象
		 * @param msg-回传数据
		 * @param taskId-任务Id,模块里面的任务
		 */
		void onUpdate(Object msg, int taskId);

	}

	
	/**
	 * 任务信息对象
	 */
	class SKMsg {
		String taskName; // 任务名
		int nTaskId; // 任务ID
		int nParam; // int 类型参数
		String sParam; // String 类型参数
		Object mParam; // 自定义对象 类型参数
		ICallback callback;// 回调接口
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
				for (int i = 0; i < mCallbacks.size(); i++) {
					AlarmCallbackInfo info = mCallbacks.get(i);
					if (info.name.equals(name)) {
						return info.iCallback;
					}
				}
			}
			return iCallback;
		}else if (type==2) {
			//注册
            if (mCallbacks != null) {
				removeReigter(callback);
				AlarmCallbackInfo info = new AlarmCallbackInfo();
				info.name = TASK_ID + "";
				info.iCallback = callback;
				mCallbacks.add(info);
			}
		}else if (type==3) {
			//删除注册
			remove(name);
		}else if (type==4) {
			//清除
			if (mCallbacks != null) {
				mCallbacks.clear();
				TASK_ID = 0;
			}
		}
		return iCallback;
	}
	
	/**
	 * 根据名称获取回调接口
	 */
	private ICallback getCallback(String name) {
		return doCallback(1, null, name);
	}

	/**
	 * 删除已经注册的
	 */
	private void removeReigter(ICallback callback) {
		if (mCallbacks != null) {
			for (int i = 0; i < mCallbacks.size(); i++) {
				AlarmCallbackInfo info = mCallbacks.get(i);
				if (info.iCallback == callback) {
					mCallbacks.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * 删除已经注册的
	 */
	private void remove(String name) {
		if (mCallbacks != null) {
			if(name==null||name.equals(""))
				return;
			for (int i = 0; i < mCallbacks.size(); i++) {
				AlarmCallbackInfo info = mCallbacks.get(i);
				if (info.name.equals(name)) {
					mCallbacks.remove(i);
					break;
				}
			}
		}
	}

	/**
	 * 清除
	 */
	public void destory() {
		doCallback(4, null, "");
	}

	public AlarmSaveThread.IBinder getBinder() {
		return binder;
	}

}
