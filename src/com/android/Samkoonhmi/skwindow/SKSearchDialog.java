package com.android.Samkoonhmi.skwindow;

import android.content.Context;
import android.graphics.Rect;
//import android.content.Intent;
//import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.Samkoonhmi.R;
/**
 * 
 * @author Administrator
 * @see SKSwitchOperDialog.java
 * @purpose to show a searching bar & return the String to be searched
 */

public class SKSearchDialog implements OnEditorActionListener ,OnTouchListener{

	private PopupWindow mPopupWindow;	//the pop win to show search bar
	private LayoutInflater inflater;	//the inflater to inflate view
	private View view;					//the view 
	private EditText mEditText;		//the searching content
	private Context mContext;			//the context of this
	public boolean isShow;
	//private int mIndex;				//
	private Rect popRect;			//弹出该窗口的表格的位置
	private String sHint;//提示信息
	private String mSearchContent;	//搜索信息
	private com.android.Samkoonhmi.skwindow.SKSearchDialog.IOperCall iOperCall;	//外部接口
	
	/**
	 * 实列对象
	 * @param context
	 * 
	 * @param rect 搜索框位置
	 */
	public SKSearchDialog(Context context, Rect rect) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		//this.mIndex = Index;
		inflater=LayoutInflater.from(context);
		this.popRect=new Rect(rect);
	}

	/**
	 * 初始化
	 */
	private void initPopWindow(){
		isShow=false;
		view = inflater.inflate(R.layout.searchwindow, null);
		view.setOnTouchListener(this);
		mEditText = (EditText)view.findViewById(R.id.search_content);
		mEditText.setOnEditorActionListener(this);
		mSearchContent = null;
		if(null == popRect){
			mPopupWindow = new PopupWindow(view,206,36);//默认显示背景大小
		}else{
			mPopupWindow = new PopupWindow(view,(this.popRect.right-this.popRect.left), (popRect.bottom - popRect.top));
		}
		
		mEditText.setHint(sHint);
	}
	
	/**
	 * 显示弹出框
	 */
	public void showPopWindow() {
		// TODO Auto-generated method stub
		
		if(isShow){
			return;
		}
		
		if (mPopupWindow==null) {
			initPopWindow();
		}
		
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		//判断SKScene 是否存在，以免死机
		View parent = SKSceneManage.getInstance().getCurrentScene();
		if(parent == null){
			return;
		}
		
		if(null != popRect){
			mPopupWindow.showAtLocation(parent,
					Gravity.NO_GRAVITY, popRect.left, popRect.top);
		}else{
			mPopupWindow.showAtLocation(parent,
					Gravity.CENTER, 0, 0);			
		}
		isShow=true;
	}

	/**
	 * 关闭弹出框
	 */
	public void hidePopWindow() {
		// TODO Auto-generated method stub
		if(null != iOperCall){
			iOperCall.onCancel();
		}
		
		if(mPopupWindow!=null){
			mPopupWindow.dismiss();
			isShow=false;
		}
	}

	/**
	 * 输入框输入完成时回调函数
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		//if(v.equals(mEditText) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
		{
			mSearchContent = (String)mEditText.getText().toString();

			if(iOperCall != null){
				iOperCall.onConfirm(0, mSearchContent);
			}
			hidePopWindow();
		}
		
		return false;
	}
	
	/**
	 * 
	 * @author Administrator
	 * 操作接口
	 */
	public interface IOperCall{
		/**
		 * 输入完成后所做操作
		 * @param index 预留参数，提供行列信息
		 * @param Scontent String 类型，返回的搜索字符
		 */
		public void onConfirm(int index,String Scontent);
		
		/**
		 * 关闭POPWIN时所做操作
		 *
		 */
		void onCancel();
	}
	
	/**
	 * 设置回调接口
	 * @param iOperCall 回调接口
	 */
	public void setiOperCall(IOperCall iOperCall) {
		this.iOperCall = iOperCall;
	}
	
	/**
	 * 触摸响应
	 * 触摸后关闭弹出框
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		//Log.v("sksearchDialog", "onTouch:getHeight="+this.mPopupWindow.getHeight()+";getWidth="+this.mPopupWindow.getWidth());
		
		hidePopWindow();
		
		return true;
	}
	
	/**
	 * 设置提示信息
	 * @param sHint 提示信息
	 */
	public void setHintInfo(String sHint){
		this.sHint = sHint;
	}
}
