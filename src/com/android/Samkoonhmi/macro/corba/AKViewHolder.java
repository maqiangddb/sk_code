package com.android.Samkoonhmi.macro.corba;

import android.graphics.Bitmap;

import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;

/**
 * 对外接口
 * 显示窗口
 */
public class AKViewHolder {

	/**
	 * 设置点击事件
	 * @param mListener-点击回调接口
	 */
	public void setIListener(IListener mListener){
		SKSceneManage.getInstance().setIListener(mListener);
	}
	
	/**
	 * 设置背景图片
	 * @param bitmap-背景图片
	 */
	public void setBackground(Bitmap bitmap){
		SKSceneManage.getInstance().setBackground(bitmap);
	}
	
	/**
	 * 设置背景颜色
	 * @param color-颜色
	 */
	public void setBackground(int color){
		SKSceneManage.getInstance().setBackground(color);
	}
	
	/**
	 * 对外接口
	 * 通知刷新
	 */
	public void refresh(){
		SKSceneManage.getInstance().refresh();
	}
	
	/**
	 * 对外接口
	 * 回调绘制
	 * @param idraw-刷新回调接口
	 */
	public void IAKDraw(IAKDraw idraw){
		SKSceneManage.getInstance().setIAKDraw(idraw);
	}
	
	/**
	 * 对外接口
	 * 获取图片
	 * @param name-图片名称
	 */
	public Bitmap getBitmap(String name){
		return ImageFileTool.getBitmap(name);
	}
	
	public Bitmap getSceneView(){
		return SKSceneManage.getInstance().getSceneView();
	}
}
