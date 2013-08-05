package com.android.Samkoonhmi.macro;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Timer;
import com.android.Samkoonhmi.databaseinterface.CompMacroBiz;
import com.android.Samkoonhmi.databaseinterface.MParamBiz;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.CompMacroInfo;


import android.util.Log;

/**
 * 控件宏指令项
 * */
public class CompMacroItem extends BaseMacroItem{
	private boolean       mDebug = false;
	private String        mTag   = "CompMacroItem";

	private CompMacroInfo    mCMI;  //宏指令数据实体类
	HashMap<String, PHolder> mPHMap = null;

	public CompMacroItem(short mid){
		super();  	   	
		getDataFromDatabase(mid);//从数据库获取数据实体
		if(null == mCMI){//数据实体类为空
			Log.e("CompMacroItem","CompMacroItem: mCMI is null, macro id :" + mid);
			return;
		}
		setBMI(mCMI);	
	}
	/**
	 * 从数据库读取宏指令信息
	 * */
	public void getDataFromDatabase(short mid) {

		CompMacroBiz tmpMBiz = new CompMacroBiz();

		mCMI = tmpMBiz.selectCompMacro(mid);
		if(null == mCMI){
			Log.e("CompMacroItem","getDataFromDatabase: mCMI is null!");
			return;
		}

		//初始化参数列表
		MParamBiz  tmpMPBiz = new MParamBiz();
		mParamList  = tmpMPBiz.selectMacroParamList(mCMI.getMacroLibName());
		if(null == mParamList){
			Log.e("CompMacroItem","getDataFromDatabase: mParamList is null!");
			return;
		}

		mPHMap = new HashMap<String, PHolder>();

		//参数设置
		ParamTool.setParam(mParamList, mPHMap, false,mCMI.getnSid());
	}

	/**
	 * 获得数据实体类
	 * @return 
	 * */
	public CompMacroInfo getCMI(){
		return mCMI;
	}

	@Override
	public void run() {
		if(mDebug){
			Log.i(mTag,"run: try to call pullParams");
		}
		//每次都需同步地址控件的数据到本地缓存
		ParamHelper.pullParams(mParamList, mPHMap);  		
		//调用宏指令
		try {
			mMacroMethod.invoke(mJMLInstance, mPHMap);			
		} catch (IllegalArgumentException e) {
			Log.e("CompMacroItem","run:  Illegal Argument! MacroName: " + mCMI.getMacroName());			
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			Log.e("CompMacroItem","run:  Illegall Access! MacroName: " + mCMI.getMacroName());
			e.printStackTrace();
			return;
		} catch (InvocationTargetException e) {
			Log.e("CompMacroItem","run:  Invocation Target Exception! MacroName: " + mCMI.getMacroName());		
			e.printStackTrace();
			return;
		}
		if(mDebug){
			Log.i(mTag,"run: try to call pushParams");
		}
		//将参数推送到地址空间
		ParamHelper.pushParams(mParamList, mPHMap);	
	}

	@Override
	public int execute(Timer timer) {
		if(null == timer){
			return -1;
		}

		if( null == mMacroMethod){
			return -2;
		}

		if(null == mCMI){
			return -3;
		}

		timer.schedule(this,0);
		return 0;
	}
	@Override
	public int cancle(Timer timer) {
		return 0;
	}
	@Override
	public short getType() {
		return this.mCMI.getMacroType();
	}

}//End of class
