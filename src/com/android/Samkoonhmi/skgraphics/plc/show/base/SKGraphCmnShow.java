//import SKGraphicsCmn;
package com.android.Samkoonhmi.skgraphics.plc.show.base;

import com.android.Samkoonhmi.skgraphics.plc.SKGraphicsCmn;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import android.view.MotionEvent;
/**
 * 与plc通讯，只有显示功能的控件基类
 * @author Administrator
 *
 */
public  abstract class SKGraphCmnShow extends SKGraphicsCmn{

	
    public SKGraphCmnShow() {
		// TODO Auto-generated constructor stub
	}

	
    public void addrNoticStatus(double nStatus){
        // TODO put your implementation here.	
    }
	/**
     * 控件是否显示
     * @return
     */
    public abstract boolean isShow();
    
    /**
     * 只有显示功能的控件 不能触控
     * @return
     */
    @Override
    public boolean isTouch()
    {
    	return false;
    }

   /**
    * 取得数据库里面的数据
    */
    public  abstract void getDataFromDatabase();
	/**
	 * 将数据写入数据库
	 */
    public abstract void setDataToDatabase();
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time=0;
		return false;
	}
}