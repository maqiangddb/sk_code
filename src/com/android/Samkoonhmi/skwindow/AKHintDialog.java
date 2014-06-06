package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 提示信息界面
 */
public class AKHintDialog {

	private View view;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	public boolean isShow;
	private UIHandler mHandler;
	private static final int SHOW=1;
	private static final int HIDE=2;
	private TextView mTextView;
	private static final String TAG="AKHintDialog";
	
	public AKHintDialog(Context context){
		inflater=LayoutInflater.from(context);
	}
	
	/**
	 * 初始化
	 */
	private void initPopWindow(){
		isShow=false;
		view = inflater.inflate(R.layout.ak_hint_dialog, null);
		mTextView=(TextView)view.findViewById(R.id.txt_msg);
		mPopupWindow=new PopupWindow(view,200, 150);
	}
	
	/**
	 * 显示
	 */
	private String sMsg="";
	public synchronized void showPopWindow(String msg){
		if(isShow){
			return;
		}
		sMsg=msg;
		if(mHandler==null){
			mHandler=new UIHandler(Looper.getMainLooper());
		}
		mHandler.sendEmptyMessage(SHOW);
	}
	
	class UIHandler extends Handler{
		public UIHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==SHOW) {
				try {
					if (!SKSceneManage.getInstance().isbWindowFocus()) {
						//窗口未获取焦点
						Log.e("AKPopupWindow", "no window forcus ...");
						return ;
					}
					Activity mActivity=SKSceneManage.getInstance().getActivity();
					if (mActivity==null) {
						return;
					}
					//View view=mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
					View view=SKSceneManage.getInstance().getCurrentScene();
					if (mPopupWindow==null) {
						initPopWindow();
					}
					if (mTextView!=null) {
						mTextView.setText(sMsg);
					}
					isShow=true;
					mPopupWindow.setFocusable(true);
					mPopupWindow.setOutsideTouchable(true);
					mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
					mPopupWindow.update();
					mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
				} catch (Exception e) {
					isShow=false;
					e.printStackTrace();
					Log.d("AKHintDialog", "ak hint dialog error!");
				}
			}else if (msg.what==HIDE) {
				if(mPopupWindow!=null){
					try{
						mPopupWindow.dismiss();
						isShow=false;
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	/**
	 * 关闭对话框
	 */
	public void hidePopWindow(){
		if (mHandler!=null) {
			mHandler.sendEmptyMessage(HIDE);
		}
	}
}
