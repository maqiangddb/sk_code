package com.android.Samkoonhmi.util;

import java.util.ArrayList;
import java.util.HashMap;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

public class UsbScan {

	private static final String TAG="USBSCAN";
	private static final int SCAN_ADD=1;
	//已经输入编码数
	private HandlerThread mThread;
	private ScanHandler mHandler=null;
	private StringBuffer mOneBuffer=null;
	private StringBuffer mTwoBuffer=null;
	private int nBufferIndex=0;
	private ArrayList<String> mList=null;
	private static HashMap<Integer, String>  mKeyMap=new HashMap<Integer, String>();
	
	// 单例
	private static UsbScan sInstance = null;
	public synchronized static UsbScan getInstance() {
		if (sInstance == null) {
			sInstance = new UsbScan();
		}
		return sInstance;
	}
	
	public UsbScan(){
		mThread=new HandlerThread("UsbScanThread");
		mThread.start();
		mHandler=new ScanHandler(mThread.getLooper());
		mOneBuffer=new StringBuffer();
		mTwoBuffer=new StringBuffer();
		mList=new ArrayList<String>();
	}
	
	/**
	 * 获取条形码
	 */
	private String temp="";
	public String getScanCode(){
		temp="";
		if (mList.size()>0) {
			temp=mList.get(0);
			mList.remove(0);
		}
		return temp;
	}
	
	/**
	 * 获取所有未读条形码
	 */
	public ArrayList<String> getScanCodes() {
		ArrayList<String> temp=new ArrayList<String>();
		for (int i = 0; i < mList.size(); i++) {
			temp.add(mList.get(i));
		}
		mList.clear();
		return temp;
	}
	
	
	/**
	 * 接收键盘输入
	 */
	public void onKeydown(int keyCode, KeyEvent event){
		
		//Log.d(TAG, "code="+keyCode+",time="+event.getDownTime());
		mHandler.obtainMessage(SCAN_ADD, event).sendToTarget();
		
	}

	class ScanHandler extends Handler{
		
		public ScanHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO 自动生成的方法存根
			super.handleMessage(msg);
			if (msg.what==SCAN_ADD) {
				KeyEvent event=(KeyEvent)msg.obj;
				if (event==null) {
					return;
				}
				int code=event.getKeyCode();
				if (code==KeyEvent.KEYCODE_ENTER||code==KeyEvent.KEYCODE_SPACE) {
					//回车或者空格,则条形码扫描结束
					addCode();
					return;
				}
				
				if (mKeyMap.containsKey(code)) {
					String num=mKeyMap.get(code);
					
					if (nBufferIndex==0) {
						mOneBuffer.append(num);
					}else{
						mTwoBuffer.append(num);
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 添加条形码
	 */
	public synchronized void addCode(){
		String temp="";
		if (nBufferIndex==0) {
			nBufferIndex=1;
			temp=mOneBuffer.toString();
			mOneBuffer.setLength(0);
		}else {
			nBufferIndex=0;
			temp=mTwoBuffer.toString();
			mTwoBuffer.setLength(0);
		}
		if (temp==null||temp.equals("")) {
			Log.d(TAG, "scan code null ... ");
			return;
		}
		mList.add(temp);
		
	}
	
	static{
		mKeyMap.put(KeyEvent.KEYCODE_0, "0");
		mKeyMap.put(KeyEvent.KEYCODE_1, "1");
		mKeyMap.put(KeyEvent.KEYCODE_2, "2");
		mKeyMap.put(KeyEvent.KEYCODE_3, "3");
		mKeyMap.put(KeyEvent.KEYCODE_4, "4");
		mKeyMap.put(KeyEvent.KEYCODE_5, "5");
		mKeyMap.put(KeyEvent.KEYCODE_6, "6");
		mKeyMap.put(KeyEvent.KEYCODE_7, "7");
		mKeyMap.put(KeyEvent.KEYCODE_8, "8");
		mKeyMap.put(KeyEvent.KEYCODE_9, "9");
		mKeyMap.put(KeyEvent.KEYCODE_A, "A");
		mKeyMap.put(KeyEvent.KEYCODE_B, "B");
		mKeyMap.put(KeyEvent.KEYCODE_C, "C");
		mKeyMap.put(KeyEvent.KEYCODE_D, "V");
		mKeyMap.put(KeyEvent.KEYCODE_E, "E");
		mKeyMap.put(KeyEvent.KEYCODE_F, "F");
		mKeyMap.put(KeyEvent.KEYCODE_G, "G");
		mKeyMap.put(KeyEvent.KEYCODE_H, "H");
		mKeyMap.put(KeyEvent.KEYCODE_I, "I");
		mKeyMap.put(KeyEvent.KEYCODE_J, "J");
		mKeyMap.put(KeyEvent.KEYCODE_K, "K");
		mKeyMap.put(KeyEvent.KEYCODE_L, "L");
		mKeyMap.put(KeyEvent.KEYCODE_M, "M");
		mKeyMap.put(KeyEvent.KEYCODE_N, "N");
		mKeyMap.put(KeyEvent.KEYCODE_O, "O");
		mKeyMap.put(KeyEvent.KEYCODE_P, "P");
		mKeyMap.put(KeyEvent.KEYCODE_Q, "Q");
		mKeyMap.put(KeyEvent.KEYCODE_R, "R");
		mKeyMap.put(KeyEvent.KEYCODE_S, "S");
		mKeyMap.put(KeyEvent.KEYCODE_T, "T");
		mKeyMap.put(KeyEvent.KEYCODE_U, "U");
		mKeyMap.put(KeyEvent.KEYCODE_V, "V");
		mKeyMap.put(KeyEvent.KEYCODE_W, "W");
		mKeyMap.put(KeyEvent.KEYCODE_X, "X");
		mKeyMap.put(KeyEvent.KEYCODE_Y, "Y");
		mKeyMap.put(KeyEvent.KEYCODE_Z, "Z");
		
	}
	
}
