package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skenum.CALIBRATION_DIRECTION;
import com.android.Samkoonhmi.skwindow.SKSceneManage;



public class  MagnifierViewShow {
	private Context context;
	private Bitmap bitmap;
	private MagnifierView mv;
	private LayoutInflater mInflater;
	private PopupWindow mPopupWindow;// 窗口
	public boolean popIsShow = true; // 窗口是否弹出
	private View mview;
	private CALIBRATION_DIRECTION direction;
	private boolean flag;
	private int x;
	private int y;
	private int width;
	private int height;
	
	//构造方法
	public MagnifierViewShow(Context c,Bitmap b,int x,int y,int width,int height, CALIBRATION_DIRECTION direction){
		this.context=c;
		this.bitmap=b;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.direction=direction;
		flag=true;
	}
	
	
	public void init(){
		if(direction==CALIBRATION_DIRECTION.DIRECTION_UP){
			height=height/2;
		}else if(direction==CALIBRATION_DIRECTION.DIRECTION_DOWN){
			height=height/2;
			y=y+height;
		}else if(direction==CALIBRATION_DIRECTION.DIRECTION_LEFT){
			width=width/2;
		}else if(direction==CALIBRATION_DIRECTION.DIRECTION_RIGHT){
			width=width/2;
			x=x+width;
		}
		mInflater = LayoutInflater.from(context);
		mview = mInflater.inflate(R.layout.popmagnifier, null);
		mv=(MagnifierView)mview.findViewById(R.id.magnifier);
		mv.setBitmap(bitmap);
		mv.invalidate();
		popIsShow=true;
		mPopupWindow = new PopupWindow(mview, width,height);
		OnTouchListeners();
	}
	
	/**
	 * 显示弹出窗口
	 */
	public void showPopUpWindow() {
		if(flag){
		if (mPopupWindow == null) {
			init();
		}
		popIsShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
		
		mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.NO_GRAVITY, x, y);
		flag=false;
		}
	}
	
	/**
	 * 关闭pop
	 */
	public void closePop(){
		if(mPopupWindow!=null && mPopupWindow.isShowing()){//当pop不为空并且显示的时候
		mPopupWindow.dismiss();//关闭pop
		flag=true;
		mPopupWindow.setFocusable(false);
		popIsShow = false;
		}
	}
	
	public void OnTouchListeners(){
		mPopupWindow.getContentView().setOnTouchListener(
				new View.OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						SKSceneManage.getInstance().time = 0;
						System.out.println("/做一个不在焦点外的处理事件监听");
						mPopupWindow.setFocusable(false);
						mPopupWindow.dismiss();
						popIsShow = false;
						return true;
					}
				});
	}
}
