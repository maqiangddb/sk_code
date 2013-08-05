package com.android.Samkoonhmi.macro;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import com.android.Samkoonhmi.databaseinterface.InitMacroBiz;
import com.android.Samkoonhmi.databaseinterface.MParamBiz;
import com.android.Samkoonhmi.macro.corba.PHolder;
import com.android.Samkoonhmi.model.InitMacroInfo;
import com.android.Samkoonhmi.model.MParamInfo;

import android.util.Log;

/**
 * 初始化宏指令项
 * */
public class InitMacroItem extends BaseMacroItem{
	private boolean       mDebug = false;
	private String        mTag   = "InitMacroItem";	

	private InitMacroInfo  mIMI;
	HashMap<String, PHolder> mPHMap = null;

	public InitMacroItem(short mid){

		super();  

		getDataFromDatabase(mid);

		if(null == mIMI){//数据实体类为空
			Log.e("InitMacroItem","InitMacroItem: mIMI is null");
			return;
		}
		setBMI(mIMI);
	}

	/**
	 * 从数据库读取宏指令信息
	 * */
	private void getDataFromDatabase(short mid) {

		InitMacroBiz tmpMBiz = new InitMacroBiz();

		mIMI = tmpMBiz.selectInitMacro(mid);
		if(null == mIMI){
			Log.e("InitMacroItem","getDataFromDatabase: Macro Information is null!");
			return;
		}

		//初始化参数列表
		MParamBiz  tmpMPBiz = new MParamBiz();
		mParamList  = tmpMPBiz.selectMacroParamList(mIMI.getMacroLibName());
		if(null == mParamList){
			Log.e("InitMacroItem","getDataFromDatabase: mParamList is null!");
			return;
		}		
		mPHMap = new HashMap<String, PHolder>();
		
		//参数设置
		ParamTool.setParam(mParamList, mPHMap, false,mIMI.getnSid());
	}

	/**
	 * 获得宏指令的参数列表
	 * */
	public ArrayList<MParamInfo> getParamList(){
		return mParamList;
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
			mMacroMethod.invoke(mJMLInstance,mPHMap);			
		} catch (IllegalArgumentException e) {
			Log.e("InitMacroItem","run:  Illegal Argument! MacroName: " + mIMI.getMacroName());			
			e.printStackTrace();
			return;
		} catch (IllegalAccessException e) {
			Log.e("InitMacroItem","run:  Illegall Access! MacroName: " + mIMI.getMacroName());
			e.printStackTrace();
			return;
		} catch (InvocationTargetException e) {
			Log.e("InitMacroItem","run:  Invocation Target Exception! MacroName: " + mIMI.getMacroName());		
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

		if(null == mIMI){
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
		return this.mIMI.getMacroType();
	}


}//End of class
