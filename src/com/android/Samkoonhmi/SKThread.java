/**
 * 李镇,2012/05/08,这个是系统的后台处理线程,控件以及系统比较复杂的计算在此完成
 */

package com.android.Samkoonhmi;

import java.util.Vector;

import com.android.Samkoonhmi.model.SKThreadCallbackInfo;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.TASK;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class SKThread {

	private static final String TAG = "SKThread";
	public static int TASK_ID;
	// 带消息循环的线程
	private static HandlerThread mThread;
	// 保存已注册的控件
	private static Vector<SKThreadCallbackInfo> mCallbacks = null;//当前正在使用的
	private SKHandler mHandler;
	private boolean debug = true;
	private static ICallback mSceneCallback;

	// 单例
	private static SKThread sInstance = null;

	public synchronized static SKThread getInstance() {
		if (sInstance == null) {
			sInstance = new SKThread();
		}
		return sInstance;
	}

	private SKThread() {
		if (mThread == null) {
			TASK_ID = 0;
			mThread = new HandlerThread("backThread");
			mThread.start();

			mHandler = new SKHandler(mThread.getLooper());
			mCallbacks = new Vector<SKThreadCallbackInfo>();
		}
	}

	public void startThread() {
		if (mThread != null) {
		}
	}

	public void stopThread() {
		if (mThread != null) {
			mThread.stop();
		}
	}

	public HandlerThread getSkThread() {
		return mThread;
	}

	class SKHandler extends Handler {

		public SKHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mCallbacks == null || msg == null || msg.obj == null) {
				return;
			}
			if (debug) {
				Log.d(TAG, "SKThread id:" + Thread.currentThread().getId());
				debug = false;
			}
			switch (msg.what) {
			case MODULE.MODULE_TEST:
				// 测试用的,具体
				test((SKMsg) msg.obj);
				break;
			case MODULE.IMAGEVIEWER:
				// 图片显示器
				imageviewer((SKMsg) msg.obj);
				break;
			case MODULE.SKANIMATION:
				skanimation((SKMsg) msg.obj);
				break;
			case MODULE.SCENE:
				// 画面
				scene((SKMsg) msg.obj);
				break;
			case MODULE.CAL_CURVE:
				// 曲线
				curve((SKMsg) msg.obj);
				break;
			case MODULE.SKDYNAMICRECT:
				dynamicrect((SKMsg) msg.obj);
				break;
			case MODULE.SKDYNAMICCIRCLE:
				dynamiccircle((SKMsg) msg.obj);
				break;
			case MODULE.SKGIFVIEWER:
				gifviewer((SKMsg) msg.obj);
				break;
			case MODULE.NUMBER_INPUT_SHOW:
				num((SKMsg) msg.obj);
				break;
			case MODULE.BASESHAP:
				// 基本模块，控件的信息读取
				baseShap((SKMsg) msg.obj);
				break;
			case MODULE.ALARM:
				alarm((SKMsg) msg.obj);
				break;
			case MODULE.HISTORY:
				// 历史数据显示器
				history((SKMsg) msg.obj);
				break;
			case MODULE.CALLBACK:
				// 带回调接口的任务
				SKMsg sMsg = (SKMsg) msg.obj;
				if (sMsg != null) {
					ICallback callback = sMsg.callback;
					if (callback != null) {
						callback.onUpdate(sMsg.mParam, sMsg.nTaskId);
					}
				}
				break;
			case MODULE.RECIPER_COLLECT_NOTIC:
				recipe((SKMsg) msg.obj);
				break;
				
			}
		}

	}

	/**
	 * 图片显示器模块任务分发
	 * */
	public void skanimation(SKMsg msg) {
		if (null != msg) {
			ICallback callback = getCallback(msg.taskName);
			if (callback != null) {
				callback.onUpdate(0, msg.nTaskId);
			}
		}// End of: if(null != msg)
	}

	/**
	 * 图片显示器模块任务分发
	 * */
	public void imageviewer(SKMsg msg) {
		if (null != msg) {
			ICallback callback = getCallback(msg.taskName);
			if (callback != null) {
				callback.onUpdate(0, msg.nTaskId);
			}
		}// End of: if(null != msg)
	}

	/**
	 * GIF显示器模块任务分发
	 * */
	public void gifviewer(SKMsg msg) {
		if (null != msg) {
			switch (msg.nTaskId) {
			case TASK.READ_ITEM_DATA:
			case TASK.GIFVIEWER_CHANGE: // 图片显示器闪烁任务
				ICallback icb = getCallback(msg.taskName);
				if (null != icb) {
					icb.onUpdate(0, msg.nTaskId);
				}
				break;
			}// End of:switch(msg.nTaskId)
		}// End of: if(null != msg)
	}

	/**
	 * 测试模块
	 */
	private void test(SKMsg msg) {
		ICallback callback = getCallback(msg.taskName);
		if (callback != null) {
			callback.onUpdate("回调测试", msg.nTaskId);
		}
	}

	/**
	 * 动态矩形
	 * */

	private void dynamicrect(SKMsg msg) {
		if (msg==null) {
			return;
		}
		ICallback icb = getCallback(msg.taskName);
		if (icb != null) {
			icb.onUpdate(0, msg.nTaskId);
		}
	}

	/**
	 * 动态圆形
	 * */

	private void dynamiccircle(SKMsg msg) {
		if (msg==null) {
			return;
		}
		ICallback icb = getCallback(msg.taskName);
		if (icb != null) {
			icb.onUpdate(0, msg.nTaskId);
		}
	}

	/**
	 * 报警模块
	 */
	// private AlarmBiz alarmBiz;
	private void alarm(SKMsg msg) {
		if (msg != null) {
			ICallback callback = getCallback(msg.taskName);
			switch (msg.nTaskId) {
			case TASK.ALARM_SELECT:// 报警数据查询
				if (callback != null) {
					callback.onUpdate(0, msg.nTaskId);
				}
				break;
			case TASK.ALARM_ADD_REFRESH:// 产生报警更新界面
				if (callback != null) {
					callback.onUpdate(msg.mParam, msg.nTaskId);
				}
				break;
			case TASK.ALARM_CLEAR_REFRESH:// 报警消除更新界面
				if (callback != null) {
					callback.onUpdate(msg.mParam, msg.nTaskId);
				}
				break;
			case TASK.ALARM_CONFIRM_REFRESH:// 报警确定更新界面
				if (callback != null) {
					if (msg.sParam.equals("") && msg.nParam == 0) {
						callback.onUpdate("", msg.nTaskId);
					} else {
						String[] temp = new String[] { msg.nParam + "",
								msg.sParam };
						callback.onUpdate(temp, msg.nTaskId);
					}
				}
				break;
			case TASK.ALARM_ALL_COUNT:
				if (callback != null) {
					callback.onUpdate(msg.nParam, msg.nTaskId);
				}
				break;
			case TASK.ALARM_HIS_READ:
				if (callback != null) {
					callback.onUpdate(msg.mParam,msg.nTaskId);
				}
				break;
			case TASK.HISTORY_REFRESH:// 清除历史报警，更新界面
				if (callback != null) {
					callback.onUpdate(0, msg.nTaskId);
				}
				break;
			case TASK.ALARM_DELETE://清除报警， 删除nClear ==0 的报警
				if (callback != null) {
					callback.onUpdate(msg.mParam, msg.nTaskId);
				}
				break;
			case TASK.ALARM_UPDATE://报警确定，将nClear=0的报警改为nClear = 1
				if (callback != null) {
					callback.onUpdate(msg.mParam, msg.nTaskId);
				}
				break;
			case TASK.ALARM_DELETE_HIS:
				if (callback != null) {
					callback.onUpdate(msg.mParam, msg.nTaskId);
				}
			}
		}
	}

	/**
	 * 历史数据显示器
	 */
	private void history(SKMsg msg) {
		if (msg == null) {
			return;
		}
		ICallback callback = getCallback(msg.taskName);
		switch (msg.nTaskId) {
		case TASK.HISTORY_SELECT:// 初始化获取历史数据
			if (callback != null) {
				callback.onUpdate(msg.nParam, msg.nTaskId);
			}
			break;
		case TASK.HISTORY_COUNT:// 查询历史数据总数
			if (callback != null) {
				callback.onUpdate(0, msg.nTaskId);
			}
			break;
		case TASK.HISTORY_LOAD:// 预加载历史数据
			if (callback != null) {
				callback.onUpdate(msg.mParam, msg.nTaskId);
			}
			break;
		case TASK.HISTORY_ADD:// 新增历史数据
			if (callback != null) {
				callback.onUpdate(msg.mParam, msg.nTaskId);
			}
			break;
		}

	}

	/**
	 * 配方
	 */
	private void recipe(SKMsg msg) {
		if (msg == null) {
			return;
		}
		ICallback callback = getCallback(msg.taskName);
		if (msg.nTaskId == TASK.RECIPE_READ) {
			if (callback != null) {
				callback.onUpdate(0, msg.nTaskId);
			}
		}
	}

	/**
	 * 画面
	 */
	private void scene(SKMsg msg) {
		if (msg != null) {
			switch (msg.nTaskId) {
			case TASK.ALL_SCENE: {
				// 所有画面
				ICallback callback = getCallback(msg.taskName);
				if (callback != null) {
					callback.onUpdate("", msg.nTaskId);
				}
				break;
			}
			case TASK.SCENE_AND_ITEM: {
				// 画面信息和控件信息
				if (mSceneCallback != null) {
					mSceneCallback.onUpdate(msg.mParam, msg.nTaskId);
				}
				break;
			}
			case TASK.SCENE_INFO:{
				//加载画面信息
				if (mSceneCallback != null) {
					mSceneCallback.onUpdate(0, msg.nTaskId);
				}
			}
			}
		}
	}

	/**
	 * 曲线
	 */
	private void curve(SKMsg msg) {
		if (msg != null) {
			ICallback callback = getCallback(msg.taskName);
			switch (msg.nTaskId) {
			case TASK.CURVE_CAL_DATA: {
				// 曲线计算
				if (callback != null) {
					callback.onUpdate(0, msg.nTaskId);
				}
				break;
			}
			default:
				break;
			}
		}
	}

	/**
	 * 数值显示器
	 * 
	 * @param msg
	 */
	private void num(SKMsg msg) {
		if (msg == null) {
			return;
		}
		ICallback callback = getCallback(msg.taskName);
		if (callback != null) {
			callback.onUpdate(0, msg.nTaskId);
		}
	}

	/**
	 * 基本图形
	 * 
	 * @param msg
	 */
	private void baseShap(SKMsg msg) {
		if (msg == null) {
			return;
		}
		ICallback callback = getCallback(msg.taskName);
		if (callback != null) {
			callback.onUpdate(0, msg.nTaskId);
		}
	}

	/**
	 * 绑定控件
	 */
	SKThread.IBinder binder = new SKThread.IBinder() {

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
		public String onRegisterForScene(ICallback callback) {
			String result = "";
			if (callback != null) {
				mSceneCallback = callback;
				result = "mSceneCallback";
				Log.d(TAG, "register for secne");
			}
			return result;
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

	};

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
	 * 绑定接口
	 */
	public interface IBinder {
		/**
		 * 注册回调接口
		 * 
		 * @param callback
		 *            ,回调接口
		 * @return 返回nTaskId,返回值,作为任务名称
		 */
		String onRegister(ICallback callback);

		/**
		 * 主要用于画面的回调，由于画面比较特殊， 它需要预加载下一个画面，所以它需要回调一直存在，
		 */
		String onRegisterForScene(ICallback callback);

		// 取消回调
		boolean onDestroy(ICallback callback, String taskName);

		/**
		 * @param moduleId
		 *            ,模块Id,例如 开关按钮是一个模块,图表是一个模块
		 * @param taskId
		 *            ,任务Id,模块里面的任务
		 * @param taskName
		 *            ,任务名称,注册时返回的值
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
		 * @param msg
		 *            -回传数据
		 * @param taskId
		 *            -任务Id,模块里面的任务
		 */
		void onUpdate(String msg, int taskId);

		/**
		 * 回传的数据类型是int or boolean
		 * 
		 * @param msg
		 *            -回传数据
		 * @param taskId
		 *            -任务Id,模块里面的任务
		 */
		void onUpdate(int msg, int taskId);

		/**
		 * 回传的数据类型是自定义对象
		 * 
		 * @param msg
		 *            -回传数据
		 * @param taskId
		 *            -任务Id,模块里面的任务
		 */
		void onUpdate(Object msg, int taskId);

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
					SKThreadCallbackInfo info = mCallbacks.get(i);
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
				SKThreadCallbackInfo info = new SKThreadCallbackInfo();
				info.name = TASK_ID + "";
				info.iCallback = callback;
				mCallbacks.add(info);
				//Log.d(TAG, "register name:"+info.name);
				//Log.d(TAG, "mCallbacks size:"+mCallbacks.size());
			}
		}else if (type==3) {
			//删除注册
			remove(name);
		}else if (type==4) {
			//清除
			if (mCallbacks != null) {
				mCallbacks.clear();
				TASK_ID = 0;
				//Log.d(TAG, "destory size:"+mCallbacks.size());
				//Log.d(TAG, "skthread destory");
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
				SKThreadCallbackInfo info = mCallbacks.get(i);
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
				SKThreadCallbackInfo info = mCallbacks.get(i);
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

	public SKThread.IBinder getBinder() {
		return binder;
	}
}