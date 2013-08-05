package com.android.Samkoonhmi.skwindow;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKScene;

import android.app.Activity;
import android.content.Context;
import android.drm.DrmStore.Action;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 等待圈
 */
public class SKProgress {
	
	private static PopupWindow mPopupWindow;
	private static LayoutInflater mInflater;
	private static View mView;
	private static ProgressBar mProgressBar;
	private static boolean show=false;
	public static boolean onResume=false;
	private static TextView mTextView;

	
	/**
	 * @param context-上下文
	 * @param width-显示宽度
	 * @param height-显示高度
	 * @param x-左顶点X坐标
	 * @param y-左顶点Y坐标
	 */
	public static void show(Context context,int width,int height,int x,int y,ShowStyle tyle){
		show(context, width, height, x, y, tyle, "", false);
	}
	
	/**
	 * @param context-上下文
	 * @param width-显示宽度
	 * @param height-显示高度
	 * @param x-左顶点X坐标
	 * @param y-左顶点Y坐标
	 */
	private synchronized static void show(Context context,int width,int height,int x,int y,ShowStyle tyle,String text,boolean display){
		if (show||context==null||!onResume) {
			return;
		}
		init(context,width,height,x,y);
		show=true;
		switch (tyle) {
		case ROUND:
			Drawable drawable=context.getResources().getDrawable(R.drawable.sk_progress_1);
			mProgressBar.setIndeterminateDrawable(drawable);
			break;
		case POINT:
			break;
		}
		
		if (SKSceneManage.getInstance().getCurrentScene()==null) {
			return;
		}
		
		Activity activity=SKSceneManage.getInstance().getActivity();
		if (activity==null) {
			return;
		}
		
		if (display) {
			mTextView.setVisibility(View.VISIBLE);
			mTextView.setText(text);
		}else{
			mTextView.setVisibility(View.GONE);
		}
		
		try {
			mPopupWindow.setFocusable(false);
			mPopupWindow.update();
			
			mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.NO_GRAVITY, x, y);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	/**
	 * 显示在屏幕中间
	 */
	public static void show(Context context,int width,int height,ShowStyle tyle){
		show(context, width, height, SKSceneManage.nSceneWidth/2-width/2,
				SKSceneManage.nSceneHeight/2-height/2, tyle,"",false);
	}
	
	/**
	 * 显示在屏幕中间
	 * @param width-显示宽度
	 * @param height-显示高度
	 */
	public static void show(String text){
		show(SKSceneManage.getInstance().mContext, 120, 120, SKSceneManage.nSceneWidth/2-60,
				SKSceneManage.nSceneHeight/2-50, ShowStyle.DEFAULT,text,true);
	}
	
	/**
	 * 显示在屏幕中间,
	 * 默认大小是120*120
	 * 模式样式
	 */
	public static void show(Context context){
		show(context, 120, 120, SKSceneManage.nSceneWidth/2-60,
				SKSceneManage.nSceneHeight/2-50, ShowStyle.DEFAULT,"",false);
	}
	
	/**
	 * 隐藏
	 */
	public synchronized static void hide(){
		try {
			if (show) {
				if (mTextView!=null) {
					mTextView.setVisibility(View.GONE);
				}
				if (mPopupWindow!=null) {
					mPopupWindow.dismiss();
					show=false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static void init(Context context,int width,int height,int x,int y){
		mInflater=LayoutInflater.from(context.getApplicationContext());
		mView=mInflater.inflate(R.layout.sk_progress, null);
		mTextView=(TextView)mView.findViewById(R.id.sk_progress_text);
		mTextView.setTextColor(Color.BLACK);
		mProgressBar=(ProgressBar)mView.findViewById(R.id.sk_loaging);
		mPopupWindow=new PopupWindow(mView,width,height);
	}
	
	/**
	 * 等待界面的样式
	 */
	public enum ShowStyle{
		DEFAULT,//默认系统样式
		ROUND,//圆圈
		POINT //点
	}
}
