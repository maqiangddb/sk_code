package com.android.Samkoonhmi.skwindow;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.android.Samkoonhmi.R;

public class SKSwitchOperDialog {

	private View view;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private Context mContext;
	public boolean isShow;
	private Button mBtnOk;
	private Button mBtnCancel;
	private IOperCall iOperCall;
	
	public SKSwitchOperDialog(Context context){
		mContext=context;
		inflater=LayoutInflater.from(context);
	}
	
	/**
	 * 初始化
	 */
	private void initPopWindow(){
		isShow=false;
		view = inflater.inflate(R.layout.oper_dialog, null);
		mBtnOk=(Button)view.findViewById(R.id.btn_ok);
		mBtnCancel=(Button)view.findViewById(R.id.btn_cancel);
		mBtnOk.setOnTouchListener(listener);
		mBtnCancel.setOnTouchListener(listener);
		mPopupWindow=new PopupWindow(view,200, 150);
	}
	
	/**
	 * 显示
	 */
	public void showPopWindow(){
		if (!SKSceneManage.getInstance().isbWindowFocus()) {
			//窗口未获取焦点
			Log.e("AKPopupWindow", "no window forcus ...");
			return ;
		}
		if(isShow){ 
			return;
		}
		if (mPopupWindow==null) {
			initPopWindow();
		}
		
		isShow=true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER, 0, 0);
	}
	
	View.OnTouchListener listener=new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			SKSceneManage.getInstance().time=0;
			if(v.equals(mBtnOk)){
				//确定
				if (iOperCall!=null) {
					iOperCall.onConfirm(event.getAction());
				}
				if (event.getAction()==MotionEvent.ACTION_DOWN) {
					if (mPopupWindow!=null) {
						mPopupWindow.dismiss();
						isShow=false;
					}
				}
			}else if (v.equals(mBtnCancel)) {
				//取消
				if (event.getAction()==MotionEvent.ACTION_DOWN) {
					if (iOperCall!=null) {
						iOperCall.onCancel();
					}
					if (mPopupWindow!=null) {
						mPopupWindow.dismiss();
						isShow=false;
					}
				}
			}
			return true;
		}
	};
	
	/**
	 * 关闭对话框
	 */
	public void hidePopWindow(){
		if(mPopupWindow!=null){
			mPopupWindow.dismiss();
			isShow=false;
		}
	}
	
	/**
	 * 操作确定回调接口
	 */
	public interface IOperCall{
		//确定
		void onConfirm(int action);
		//取消
		void onCancel();
		//执行宏指令
		void onStartMacro(int action);
		
		public enum CALLTYPE{
			OPER,//执行功能
			MACRO;//执行宏指令
		}
	}
	
	public void setiOperCall(IOperCall iOperCall) {
		this.iOperCall = iOperCall;
	}

}
