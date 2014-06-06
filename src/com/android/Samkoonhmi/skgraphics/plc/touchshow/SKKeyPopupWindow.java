package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
//import android.renderscript.Element.DataType;
//import android.util.Log;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
//import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.android.Samkoonhmi.R;
//import com.android.Samkoonhmi.SKThread;
//import com.android.Samkoonhmi.SKTimer.ICallback;
//import com.android.Samkoonhmi.model.KeyBoardInfo;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.KeyBroadBiz;
import com.android.Samkoonhmi.model.KeyBoardInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
//import com.android.Samkoonhmi.util.MODULE;

public class SKKeyPopupWindow {

	private Context mContext;
	private View mview;
	private LayoutInflater mInflater;
	private PopupWindow mPopupWindow; // 显示的下拉窗口
	public static boolean keyFlagIsShow; // 窗口是否弹出
	private SKKeyBoard key;
	private KeyBoardInfo keyInfo;// 键盘实体类
	private KeyBroadBiz keys;// 键盘数据库
	private ICallback callback;
//	private boolean flag;
	private String showMax;
	private String showMin;
	private boolean check;
	private boolean isPassWord;
	private int nSceneId;
	private DATA_TYPE myDataType = DATA_TYPE.OTHER_DATA_TYPE;
	private String lastText;
	private static final int LOAD_DATA = 1;
	private MainUIHandler mHandler;
	private int nStartX;
	private int nStartY;
	private int nWidth;
	private int nHeight;
	private static ArrayList<Integer> mAllId;

	public SKKeyPopupWindow(Context context) {
		super();
		this.mContext = context;
//		flag = true;
	}

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param showMax
	 *            最大值
	 * @param showMin
	 *            最小值
	 * @param checkNum
	 *            true为纯数字键盘，false为ASCII键盘
	 */
	public SKKeyPopupWindow(Context context, boolean checkNum, int nSceneId,
			DATA_TYPE dataType) {
		super();
		this.mContext = context;
		this.check = checkNum;
		this.nSceneId = nSceneId;
		this.myDataType = dataType;
		
		//键盘实体类
		if (keyInfo == null) {
			keyInfo = new KeyBoardInfo();
			//数据库
			if (keys == null) {
				keys = new KeyBroadBiz();
			}
			keyInfo = keys.selectKeyBorad(nSceneId);
		}
		nWidth=keyInfo.getNkeyWidth();
		nHeight=keyInfo.getNkeyHeight();
		if(nWidth==0) nWidth=320;
		if(nHeight==0) nHeight=230;
//		flag = true;
		keyFlagIsShow=true;
	}

	/**
	 * 初始化弹出窗口
	 */
	public void initPopUpWindow() {
		mInflater = LayoutInflater.from(mContext);
		mview = mInflater.inflate(R.layout.keyboard, null);
		key = (SKKeyBoard) mview.findViewById(R.id.keyboard);
		key.setMax(getShowMax());// 最大值
		key.setMin(getShowMin());// 最小值
		key.setChecks(check);// 输入判断
		key.setInputType(inputType);
		key.setToaskStartX(getnStartX());
		key.setToaskStartY(getnStartY());
		key.setPass(isPassWord());
		key.setInputStr(getLastText());
		key.setnSceneId(nSceneId);
		key.setLister(lister);
		key.setDataType(myDataType);

		mPopupWindow = new PopupWindow(mview, nWidth, nHeight);
		
	}

	/**
	 * 显示弹出窗口
	 */
	public void showPopUpWindow() {
		try {
			if (!SKSceneManage.getInstance().isbWindowFocus()) {
				//窗口未获取焦点
				Log.e("AKPopupWindow", "no window forcus ...");
				return ;
			}
			if (mPopupWindow == null) {
				initPopUpWindow();
			}
			keyFlagIsShow = false;
			mPopupWindow.setFocusable(true);
			mPopupWindow.update();
			key.setbFirstClick(true);
			//第一次弹出键盘界面，不包含按键信息
			mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.NO_GRAVITY, getnStartX(), getnStartY());
			if (key!=null) {
				if(lastText==null||lastText.equals("")){
					if (check) {
						key.setInputStr("0");
					}else {
						key.setInputStr("");
					}
				}else {
					key.setInputStr(lastText);
				}
			}
			
			if (mHandler == null) {
				mHandler = new MainUIHandler(Looper.getMainLooper());
			}
			mHandler.sendEmptyMessageDelayed(LOAD_DATA, 5);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SKKeyPopupWindow", "showPopUpWindow error !!!");
		}
		
	}

	/**
	 * 关闭pop
	 */
	public void closePop() {
		try {
			if (mPopupWindow != null && mPopupWindow.isShowing()) {// 当pop不为空并且显示的时候
				mPopupWindow.dismiss();// 关闭pop
				mPopupWindow.setFocusable(false);
				keyFlagIsShow = true;
				mPopupWindow = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SKKeyPopupWindow", "closePop !!! ");
		}
		
	}

	public class MainUIHandler extends Handler {
		public MainUIHandler(Looper looper) {
			
			super(looper);

		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == LOAD_DATA) {
				key.init();
				try {
					key.invalidate();
				} catch (Exception e) {
					// TODO: handle exception
					key.postInvalidate();
				}
				
				if (mPopupWindow==null) {
					return;
				}
				mPopupWindow.setFocusable(true);
//				mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
				mPopupWindow.update();
				View v =SKSceneManage.getInstance().getCurrentScene();
				if(v != null)
				{
					//刷新界面 第一次弹出是弹出键盘的空壳
					mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, getnStartX(), getnStartY());
				}
				
			}
		}
	}

	SKKeyBoard.IClickLister lister = new SKKeyBoard.IClickLister() {

		@Override
		public void close() {
			closePop();
			if (callback != null) {
				callback.onResult(key.getInputStr(),KEYBOARD_OPERATION.ENTER);
			}
		}

		@Override
		public void closeEsc() {
			// TODO Auto-generated method stub
			closePop();
			if (callback != null) {
				callback.onResult(key.getInputStr(),KEYBOARD_OPERATION.ESC);
			}
		}
	};
	
	/**
	 * 获取该工程所有的键盘id
	 */
	private static boolean init=false;
	public static void getKeyBroadId(){
		init=true;
		mAllId=DBTool.getInstance().getmKeyBroadBiz().getAllKeyBroadId();
	}
	
	/**
	 * @param id-键盘id
	 * @return true-键盘存在，false-键盘不存在
	 * 判断键盘是否存在
	 */
	public static boolean existKeyBroad(int id){
		if (init==false) {
			getKeyBroadId();
		}
		if (mAllId==null||mAllId.size()==0) {
			return false;
		}
		for (int i = 0; i < mAllId.size(); i++) {
			if (mAllId.get(i)==id) {
				return true;
			}
		}
		return false;
	}

	// 接口
	public interface ICallback {
		void onResult(String result,KEYBOARD_OPERATION type);
	}

	public ICallback getCallback() {
		return callback;
	}

	public void setCallback(ICallback callback) {
		this.callback = callback;
	}

	public String getLastText() {
		return lastText;
	}

	public void setLastText(String lastText) {
		this.lastText = lastText;
	}

	

	public String getShowMax() {
		return showMax;
	}

	public void setShowMax(String showMax) {
		this.showMax = showMax;
	}

	public String getShowMin() {
		return showMin;
	}

	public void setShowMin(String showMin) {
		this.showMin = showMin;
	}

	public int getnStartX() {
		return nStartX;
	}

	public void setnStartX(int nStartX) {
		this.nStartX = nStartX;
	}

	public int getnStartY() {
		return nStartY;
	}

	public void setnStartY(int nStartY) {
		this.nStartY = nStartY;
	}

	public boolean isPassWord() {
		return isPassWord;
	}

	public void setPassWord(boolean isPassWord) {
		this.isPassWord = isPassWord;
	}
	
	public int getKeyboardWidth(){
		return nWidth;
		
	}
	
	public int getKeyboardHeigh(){
		return  nHeight;
	}
	/**
	 * -- 输入类型判断
	 * @param type  true/false: 数字键盘/带字母的键盘
	 */
	public void setKeyType(boolean type){
		check = type;
		if(key != null){
			key.setChecks(check);
		}
	}
	
	private boolean inputType=true;
	/**
	 * 设置输入类型
	 * @param type-true,数字； type-false 字符串
	 */
	public void setInputType(boolean type){
		inputType=type;
		if (key!=null) {
			key.setInputType(type);
		}
	}
}
