//import SKGraphics;
package com.android.Samkoonhmi.skgraphics.noplc.base;

import com.android.Samkoonhmi.skgraphics.SKGraphics;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * 不与plc通讯的控件
 * 
 * @author Administrator
 * 
 */
public abstract class SKGraphicsBase extends SKGraphics {

	public SKGraphicsBase() {
		// TODO put your implementation here.
	}

	/**
     * 控件是否显示
     * @return
     */
    public abstract boolean isShow();

	/**
	 * 不与plc通讯的控件 都不可以触控
	 * 
	 * @return
	 */
	public boolean isTouch() {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time=0;
		return false;
	}
}