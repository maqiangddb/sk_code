package com.android.Samkoonhmi.skglobalcmn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.RecipeDataBiz;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.skglobalcmn.RecipeDataProp;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.ProtocolInterfaces;
import com.android.Samkoonhmi.plccommunicate.SKCommThread;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AddrPropArray;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcCmnDataCtlObj;
import com.android.Samkoonhmi.util.PlcCmnWriteCtlObj;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

public class RecipeDataCentre {
	/*最小刷新频率，单位是毫秒*/
	private int nRefreashCycle = 300;

	/*当前配方的所有属性*/
	private Vector<Double > nTmpDoubleList = new Vector<Double >();
	private Vector<Byte > nTmpRecipeDataList = new Vector<Byte >();
	private Vector<Byte > nOldRwiDataList = new Vector<Byte >();
	AddrProp mRWIAddr = new AddrProp();

	/*与配方同步控件的回调接口集合*/
	private Vector<IRecipeCallBack > mCallbackList = new Vector<IRecipeCallBack >();

	/*数据库操作临时变量*/
	private ContentValues m_tmpValues = new ContentValues();

	/*get data list*/
	private Vector<Integer > tmpDataList = new Vector<Integer >();
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();

	/*地址控制PLC值*/
	private Vector<Integer > nAddrCtlDataList = new Vector<Integer >();

	/*当前配方的值*/
	private String[] sCurrRecipeValues = null;
	
	private boolean bChangeRecipe = false;
	private boolean bHaveRecipe = false;

	private RecipeDataCentre() {
	}

	/**
	 * 通知线程的单实例
	 */
	private static RecipeDataCentre m_mRecipeCentreObj = null;
	public synchronized static RecipeDataCentre getInstance(){
		if (null == m_mRecipeCentreObj) {
			m_mRecipeCentreObj = new RecipeDataCentre();
		}
		return m_mRecipeCentreObj;
	}

	/**
	 * 从配方数据库中 读取数据
	 * @return
	 */
	private boolean initDataInfo()
	{
		/*数据库中读取初始化数据，具体的配方数据不读取*/
		boolean bSuccess = RecipeDataBiz.select();

		/*配方改变需要通知*/
		updateCallback(false, true);

		return bSuccess;
	}

	//	/**
	//	 * 获得当前配方属性
	//	 * 
	//	 * @return CurrentRecipe： 当前配方的属性对象
	//	 */
	//	public CurrentRecipe getCurrRecipeId() {
	//		return m_nCurrRecipeId;
	//	}
	//
	//	/**
	//	 * 设置当前配方属性
	//	 * 
	//	 * @param nCurrRecipeId ： 当前配方属性
	//	 */
	//	public void setCurrRecipeId(CurrentRecipe nCurrRecipeId) {
	//		this.m_nCurrRecipeId = nCurrRecipeId;
	//	}

	/**
	 * 获得配方的对象
	 * 
	 * @return
	 */
	public RecipeDataProp getRecipeDataProp() {
		return RecipeDataProp.getInstance();
	}

	/**
	 * 获得一组配方的属性
	 * @param nGroupID: 组ID
	 * @return
	 */
	public RecipeDataProp.recipeOGprop getOGRecipeData(int nGroupID)
	{
		/*判断组号是否存在*/
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for(int i = 0; i < nGroupSize; i++)
		{
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == nGroupID)
			{
				return RecipeDataProp.getInstance().getmRecipeGroupList().get(i);
			}
		}

		return null;
	}

	/**
	 * 修改当前配方
	 * @param mCurrRecipe
	 */
	public synchronized void setCurrRecipe(int nGroupId, int nRecipeId)
	{
		if(null == getCurrRecipe()) return ;
		if(nGroupId == getCurrRecipe().getCurrentGroupRecipeId() 
				&& nRecipeId == getCurrRecipe().getCurrentRecipeId())
		{
			return ;
		}
		
		/*标示正在修改配方*/
		bChangeRecipe = true;
		
		CurrentRecipe mCurrRecipe = getCurrRecipe();
		mCurrRecipe.setCurrentGroupRecipeId(nGroupId);
		mCurrRecipe.setCurrentRecipeId(nRecipeId);
		SystemInfo.setCurrentRecipe(mCurrRecipe);

		int nSize = mCallbackList.size();
		for(int i = 0; i < nSize; i++)
		{
			mCallbackList.get(i).currRecipeUpdate();
		}
		
		/*修改当前配方地址*/
		mRWIAddr.eAddrRWprop = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
		mRWIAddr.eConnectType = 1;
		mRWIAddr.nPlcStationIndex = 1;
		mRWIAddr.nRegIndex = 2;
		mRWIAddr.nUserPlcId = 0;
		mRWIAddr.sPlcProtocol = "local";
		mRWIAddr.nAddrValue = 0;
		mRWIAddr.nAddrLen = 2;

		mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;


		/*写入内部地址RWI区*/
		Vector<Integer > nSetByteList = new Vector<Integer >();
		nSetByteList.add(nGroupId);
		nSetByteList.add(nRecipeId);
		PlcRegCmnStcTools.setRegIntData(mRWIAddr, nSetByteList, mSendData);

		/*更新当前配方的所有数据*/
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.REFRESH_CURR_RECIPE, (Object)true, mCtlDatabaseCback, 0);
	   
		//修改当前配方 将配方组号和配方号写入内部寄存器地址
		SystemVariable.getInstance().setCurrentRecipeGidToAddr();
		SystemVariable.getInstance().setCurrentRecipeidToAddr();
	}

	/**
	 * 获得当前配方
	 * @return
	 */
	public CurrentRecipe getCurrRecipe()
	{
		return SystemInfo.getCurrentRecipe();
	}

	/**
	 * 线程启动
	 */
	public void start()
	{
		/*设置线程标识符*/
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPER_COLLECT_INIT, (Object)null, mCtlDatabaseCback, 2000);
	}

	/**
	 * 停止线程
	 */
	public void stop()
	{
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.NOTIC_CLEAR_CALLBACK, (Object)null, mCtlDatabaseCback, 0);
	}

	/**
	 * 编辑配方的类
	 * @author Administrator
	 *
	 */
	public class EditRecipeInfo
	{
		public CurrentRecipe mRecipeInfo = null;
		public String[] sValueList = null;
		public RecipeOprop mRecipeData = null;
	}

	/**
	 * 新建和修改配方，新建的时候mRecipeInfo.nRecipeId = -1,其余是修改配方
	 * @param mRecipeInfo：当前配方对象，包括组号和配方号
	 * @param mRecipeData：配方的属性
	 */
	public synchronized void msgEditRecipeSave(EditRecipeInfo mEditInfo)
	{
		if(null == mEditInfo) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPER_EDIT_SAVE, mEditInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 删除配方
	 * @param mRecipeInfo：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgDeleteRecipe(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPER_DELETE, mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 删除配方组
	 * @param nGroupId：配方组号
	 */
	public synchronized void msgDeleteRecipeGroup(int nGroupId)
	{
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPER_GROUP_DELETE, nGroupId, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从PLC读配方
	 * @param mRecipeInfo：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgReadRecipeFromPlc(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.READ_RECIPE_FROM_PLC, mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从写配方到PLC
	 * @param mRecipeInfo：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgWriteRecipeToPlc(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.WRITE_RECIPE_TO_PLC, mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从写文件读配方，导入配方
	 * @param mRecipeInfo：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgReadRecipeFromFile(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.READ_RECIPE_FROM_FILE, mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从写配方到文件，导出配方
	 * @param mRecipeInfo：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgWriteRecipeToFile(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.WRITE_RECIPE_TO_FILE, mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 添加注册接口
	 * @param mCallback: 注册的接口
	 */
	public void msgRegisterUpdate(IRecipeCallBack mCallback)
	{
		if(null == mCallback) return ;

		mCallback.update();
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.ADD_CALL_BACK, mCallback, mCtlDatabaseCback, 0);
	}

	/**
	 * 注销配方更新接口
	 * @param mCallback
	 */
	public void msgDestoryCallback(IRecipeCallBack mCallback)
	{
		if(null == mCallback) return ;

		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.CANCEL_CALL_BACK, mCallback, mCtlDatabaseCback, 0);
	}

	/*回调接口*/
	SKThread.ICallback mCtlDatabaseCback = new SKThread.ICallback() {

		@Override
		public void onUpdate(Object msg, int taskId) {
			switch (taskId)
			{
			case MODULE.RECIPER_COLLECT_INIT:          // 配方数据库数据初始化消息 
			{
				long nCurrMillis = System.currentTimeMillis();
				boolean bSuccess = initDataInfo();
				System.out.println("init recipe database need time :" + (System.currentTimeMillis() - nCurrMillis) + "ms");
				
				if(bSuccess && RecipeDataProp.getInstance().getmRecipeGroupList().size() > 0)
				{
					bHaveRecipe = true;
					SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPER_COLLECT_NOTIC, (Object)null, mCtlDatabaseCback, nRefreashCycle);
				}
				break;
			}
			case MODULE.RECIPER_COLLECT_NOTIC:         // 配方刷新
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				/*时间刷新函数*/
				refreashRecipeData();
				refreashRecipeFromAddr();

				SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPER_COLLECT_NOTIC, (Object)null, mCtlDatabaseCback, nRefreashCycle);
				break;
			}
			case MODULE.RECIPER_EDIT_SAVE:              // 保存新建或当前编辑的配方，新建的配方，配方号为-1
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					editSaveRecipe((EditRecipeInfo)msg);
				}
				break;
			}
			case MODULE.RECIPER_DELETE:                 // 删除配方
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					deleteRecipe((CurrentRecipe)msg);
				}
				break;
			}
			case MODULE.RECIPER_GROUP_DELETE:           // 删除配方组
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					deleteRecipeGroup((Integer)msg);
				}
				break;
			}
			case MODULE.READ_RECIPE_FROM_PLC:           // 从PLC读配方
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					readRecipeFromPlc((CurrentRecipe)msg);
				}
				break;
			}
			case MODULE.WRITE_RECIPE_TO_PLC:            // 写配方到PLC
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					writeRecipeToPlc((CurrentRecipe)msg);
				}
				break;
			}
			case MODULE.WRITE_RECIPE_TO_FILE:            // 写配方到文件，导出配方
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					writeRecipeGroupToFile((CurrentRecipe)msg);
				}
				break;
			}
			case MODULE.READ_RECIPE_FROM_FILE:           // 从文件读配方，导入配方
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				if(null != msg)
				{
					readRecipeGroupFromFile((CurrentRecipe)msg);
				}
				break;
			}
			case MODULE.ADD_CALL_BACK:                   //注册所有跟配方相关的控件更新配方的回调接口
			{
				if(null != msg)
				{
					registerUpdate((IRecipeCallBack)msg);
				}
				break;
			}
			case MODULE.NOTIC_CLEAR_CALLBACK:            //通知所有跟配方相关的回调接口清空
			{
				if(mCallbackList != null)
				{
					mCallbackList.clear();
				}
				break;
			}
			case MODULE.CANCEL_CALL_BACK:
			{
				destoryCallback((IRecipeCallBack)msg);
				break;
			}
			case MODULE.REFRESH_READ_RECIPE:             //更新读得的数据
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				refreshReadPlc((CurrentRecipe)msg);
				break;
			}
			case MODULE.REFRESH_CURR_RECIPE:             //更新当前配方数据   
			{
				/*没有配方，返回*/
				if(!bHaveRecipe) break;
				
				updateCurrRecipeToAddr((Boolean)msg);
				break;
			}
			default:
			{
				break;
			}
			}
		}

		@Override
		public void onUpdate(int msg, int taskId) {

		}

		@Override
		public void onUpdate(String msg, int taskId) {

		}
	};

	/**
	 * 配方刷新函数 
	 */
	private void refreashRecipeData()
	{
		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();

		for(int i = 0; i < nGroupRecipeSize; i++)
		{
			/* get this recipe group recipe data */
			RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(i);
			if(null == mTmpRecipeGprop) continue;

			if(!mTmpRecipeGprop.isbNeedCtlAddr()) continue;
			AddrProp mCtlAddr = mTmpRecipeGprop.getmCtlAddr();
			if(mCtlAddr == null) continue;

			/* init send data struct */
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;
			mCtlAddr.nAddrLen = 2;

			/*get data from plc*/
			tmpDataList.clear();
			boolean bSuccess = PlcRegCmnStcTools.getRegIntData(mCtlAddr, tmpDataList, mSendData);
			if(bSuccess && tmpDataList.size() >= 1)
			{
				/*判断控制值是否重新变法了*/
				while(nAddrCtlDataList.size() <= i)
				{
					nAddrCtlDataList.add(0);
				}

				int nCtlValue = tmpDataList.get(0);
				if(nAddrCtlDataList.get(i) == nCtlValue) continue;
				nAddrCtlDataList.set(i, nCtlValue);

				int nRecipeGId = mTmpRecipeGprop.getnGRecipeID();
				switch(nCtlValue)
				{
				case 1:   //read recipe from plc 
				{
					int nRecipeId = 0;
					if(tmpDataList.size() > 1)
					{
						nRecipeId = tmpDataList.get(1);
						CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
						mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
						mRefreashRecipeId.setCurrentRecipeId(nRecipeId);
						msgReadRecipeFromPlc(mRefreashRecipeId);
					}

					break;
				}
				case 2:   //write recipe to plc 
				{
					int nRecipeId = 0;
					if(tmpDataList.size() > 1)
					{
						nRecipeId = tmpDataList.get(1);
						CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
						mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
						mRefreashRecipeId.setCurrentRecipeId(nRecipeId);
						msgWriteRecipeToPlc(mRefreashRecipeId);
					}
					break;
				}
				case 3:   //read recipe group from file 
				{
					CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
					mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
					mRefreashRecipeId.setCurrentRecipeId(-1);
					msgReadRecipeFromFile(mRefreashRecipeId);
					break;
				}
				case 4:   //write recipe group to file 
				{
					CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
					mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
					mRefreashRecipeId.setCurrentRecipeId(-1);
					msgWriteRecipeToFile(mRefreashRecipeId);
					break;
				}
				default:
				{
					break;
				}
				}
			}//if(bSuccess)
		}//for(nGroupRecipeSize)
	}


	/**
	 * 修改当前配方的数据到内部地址区
	 */
	private void updateCurrRecipeToAddr(boolean bGetFromDB)
	{
		/*标示已经修改配方*/
		bChangeRecipe = false;
		
		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if(null == nCurrRecipeObj) return ;
		
		int nGroupId = nCurrRecipeObj.getCurrentGroupRecipeId();
		int nRcipeId = nCurrRecipeObj.getCurrentRecipeId();
		
		/*如果当前配方组不存在，则返回*/
		RecipeDataProp.recipeOGprop mRGObj =  getOGRecipeData(nGroupId);
		if(mRGObj == null) return ;
		
		/*当前配方不存在，则返回*/
		String[] sValueList = getRecipeData(nGroupId, nRcipeId, bGetFromDB);
		if(sValueList == null)
		{
			return ;
		}
		sCurrRecipeValues = sValueList;

		int nElementSize = sValueList.length;
		int nAddrListSize = mRGObj.getnValueAddrList().size();
		int nEdataTypeSize = mRGObj.geteDataTypeList().size();

		if(nElementSize != nAddrListSize || nElementSize != nEdataTypeSize) return ;

		mRWIAddr.eAddrRWprop = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
		mRWIAddr.eConnectType = 1;
		mRWIAddr.nPlcStationIndex = 1;
		mRWIAddr.nRegIndex = 2;
		mRWIAddr.nUserPlcId = 0;
		mRWIAddr.sPlcProtocol = "local";
		mRWIAddr.nAddrValue = 0;
		mRWIAddr.nAddrLen = 2;

		mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
		mSendData.eDataType = DATA_TYPE.INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

		nTmpRecipeDataList.clear();
		doubleToBytes(nTmpRecipeDataList, String.valueOf(nGroupId), DATA_TYPE.INT_16);
		doubleToBytes(nTmpRecipeDataList, String.valueOf(nRcipeId), DATA_TYPE.INT_16);

		/*添加数据的值到bytes容器中*/
		for(int i = 0; i < nAddrListSize; i++) 
		{
			mRWIAddr.nAddrLen += mRGObj.getnValueAddrList().get(i).nAddrLen;
			doubleToBytes(nTmpRecipeDataList, sValueList[i], mRGObj.geteDataTypeList().get(i));
		}

		/*写入内部地址RWI区*/
		int nSize = nTmpRecipeDataList.size();
		byte[] nSetByteList = new byte[nSize];
		for(int i = 0; i < nSize; i++)
		{
			nSetByteList[i] = nTmpRecipeDataList.get(i);
		}
		PlcRegCmnStcTools.setRegBytesData(mRWIAddr, nSetByteList, mSendData);
	}

	/**
	 * 如果RWI区的值改变，则更新到当前配方去
	 */
	private void refreashRecipeFromAddr() 
	{
		/*如果人为修改配方未完成，则返回*/
		if(bChangeRecipe) return ;
		
		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if(null == nCurrRecipeObj) return ;
		
		int nGroupId = nCurrRecipeObj.getCurrentGroupRecipeId();
		int nRcipeId = nCurrRecipeObj.getCurrentRecipeId();

		mRWIAddr.eAddrRWprop = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;
		mRWIAddr.eConnectType = 1;
		mRWIAddr.nPlcStationIndex = 1;
		mRWIAddr.nRegIndex = 2;
		mRWIAddr.nUserPlcId = 0;
		mRWIAddr.sPlcProtocol = "local";
		mRWIAddr.nAddrValue = 0;
		mRWIAddr.nAddrLen = 2;

		mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
		mSendData.eDataType = DATA_TYPE.POSITIVE_INT_16;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;

		tmpDataList.clear();
		boolean bGetSuccess = PlcRegCmnStcTools.getRegIntData(mRWIAddr, tmpDataList, mSendData);
		if(!bGetSuccess || tmpDataList.size() < 2)
		{
			return ;
		}

		if(nGroupId != tmpDataList.get(0) || nRcipeId != tmpDataList.get(1))
		{
			/*更改当前配方*/
			setCurrRecipe(tmpDataList.get(0), tmpDataList.get(1));
			return ;
		}

		/*如果当前配方组不存在，则返回*/
		RecipeDataProp.recipeOGprop mRGObj =  getOGRecipeData(nGroupId);
		if(mRGObj == null) return ;

		/*当前配方不存在，则返回*/
		if(sCurrRecipeValues == null)
		{
			sCurrRecipeValues = getRecipeData(nGroupId, nRcipeId, false);
			if(sCurrRecipeValues == null)
			{
				return ;
			}
		}

		/*存在，则更新到RWI区*/
		int nElementSize = sCurrRecipeValues.length;
		int nAddrListSize = mRGObj.getnValueAddrList().size();
		int nEdataTypeSize = mRGObj.geteDataTypeList().size();

		if(nElementSize != nAddrListSize || nElementSize != nEdataTypeSize) return ;

		/*获取RWI的值*/
		mRWIAddr.nAddrValue = 2;
		mRWIAddr.nAddrLen = 0;
		for(int i = 0; i < nAddrListSize; i++)
		{
			mRWIAddr.nAddrLen += mRGObj.getnValueAddrList().get(i).nAddrLen;
		}

		nTmpRecipeDataList.clear();
		PlcRegCmnStcTools.getRegBytesData(mRWIAddr, nTmpRecipeDataList, mSendData);

		/*比较配方是否更改*/
		int nBytesLen = nTmpRecipeDataList.size();
		if(nOldRwiDataList.size() != nBytesLen)
		{
			nOldRwiDataList.clear();
			for(int i = 0; i < nBytesLen; i++)
			{
				nOldRwiDataList.add(nTmpRecipeDataList.get(i));
			}
		}
		else
		{
			/*判断数据是否相同*/
			bGetSuccess = false;
			for(int i = 0; i < nBytesLen; i++)
			{
				if(nOldRwiDataList.get(i).equals(nTmpRecipeDataList.get(i)) == false)
				{
					bGetSuccess = true;
					nOldRwiDataList.set(i, nTmpRecipeDataList.get(i));
				}
			}
			if(bGetSuccess == false) return ;
		}

		/*不同，则更新当前配方*/
		nTmpDoubleList.clear();
		for(int i = 0; i < nAddrListSize; i++)
		{
			sCurrRecipeValues[i] = bytesTodouble(nTmpRecipeDataList, mRGObj.geteDataTypeList().get(i));
		}

		updateCallback(false, false);

		//		nTmpRecipeId.setCurrentGroupRecipeId(nGroupId);
		//		nTmpRecipeId.setCurrentRecipeId(nRcipeId);
		//		EditRecipeInfo mEditInfo = new EditRecipeInfo();
		//		mEditInfo.mRecipeInfo = nTmpRecipeId;
		//		mEditInfo.mRecipeData = mRGObj.getmRecipePropList().get(nRcipeIndex);
		//		editSaveRecipe(mEditInfo);
	}

	/**
	 * 写当前配方到PLC
	 * 
	 * @param nRecipeGroupId
	 * @param nRecipeId
	 */
	private boolean writeRecipeToPlc(CurrentRecipe mRecipeInfo) {
		if(null == mRecipeInfo) return false;

		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		int nRcipeId = mRecipeInfo.getCurrentRecipeId();

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize) return false;

		/* get this recipe group recipe data */
		RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGroupId);
		if(null == mTmpRecipeGprop) return false;

		/* write data to plc */
		String[] sValueList = getRecipeData(nGroupId, nRcipeId, false);
		if(sValueList == null)
		{
			return false;
		}

		int nElementSize = sValueList.length;
		int nAddrListSize = mTmpRecipeGprop.getnValueAddrList().size();
		int nEdataTypeSize = mTmpRecipeGprop.geteDataTypeList().size();

		if(nElementSize != nAddrListSize || nElementSize != nEdataTypeSize) return false;

		/*显示进度条*/
		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";
		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.recipe_write);
		}
		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg + "...").sendToTarget();

		boolean bContinue = mTmpRecipeGprop.isbContinue();
		if(bContinue)
		{
			ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
			if (null != mProtocolObj && mTmpRecipeGprop.getnValueAddrList().size() > 0) 
			{
				AddrProp mTmpAddr = mTmpRecipeGprop.getnValueAddrList().firstElement();
				if(mTmpAddr == null) return false;
				
				PlcSampInfo mPlcInfo = new PlcSampInfo();
				mPlcInfo.eConnectType = mTmpAddr.eConnectType;
				mPlcInfo.nProtocolIndex = mTmpAddr.nUserPlcId;
				mPlcInfo.sProtocolName = mTmpAddr.sPlcProtocol;
				
				int nByteLen = 2;
				DATA_TYPE eDataType = DATA_TYPE.INT_16;
				if(!mTmpRecipeGprop.geteDataTypeList().isEmpty())
				{
					eDataType = mTmpRecipeGprop.geteDataTypeList().get(0);
				}
				
				if(eDataType == DATA_TYPE.BIT_1)
				{
					nByteLen = 1;
				}
				
				int nSVlen = sValueList.length;
				Vector<Byte > nByteList = new Vector<Byte >();
				for(int i = 0; i < nSVlen; i++)
				{
					double nDValue = 0;
					if(sValueList[i] != null && sValueList[i] != "")
					{
						try
						{
							nDValue = Double.valueOf(sValueList[i]);
						}
						catch (NumberFormatException  e){
							Log.e("doubleToBytes", "string to double error" + e.getMessage());
						}
					}
					
					switch(eDataType)
					{
					case BIT_1:
					{
						byte nTmpValue = (byte)nDValue;
						nByteList.add(nTmpValue);
						nByteList.add((byte)0);
						break;
					}
					case POSITIVE_INT_16:
					case HEX_16:
					case OTC_16:
					case BCD_16:
					{
						int nTmpValue = (int)nDValue;
						nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
						break;
					}
					case POSITIVE_INT_32:
					case HEX_32:
					case OTC_32:
					case BCD_32:
					{
						long nTmpValue = (long)nDValue;
						nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 16) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 24) & 0xff));
						break;
					}
					case INT_16:
					{
						int nTmpValue = (int)nDValue;
						nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
						break;
					}
					case INT_32:
					{
						int nTmpValue = (int)nDValue;
						nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 16) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 24) & 0xff));
						break;
					}
					case FLOAT_32:
					{
						int nTmpValue = Float.floatToIntBits((float)nDValue);
						nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 16) & 0xff));
						nByteList.add((byte)((nTmpValue >>> 24) & 0xff));

						break;
					}
					case ASCII_STRING:
					{
						break;
					}
					case OTHER_DATA_TYPE:
					default:
					{
						break;
					}
					}
				}
				
				/*如果地址类型不是主站*/
				PROTOCOL_TYPE ePlcType = mProtocolObj.getProtocolType(mPlcInfo);
				if(ePlcType == PROTOCOL_TYPE.MASTER_MODEL)
				{
					/* 求最大读写长度 */
					int nConnectSize = SystemInfo.getPlcConnectionList().size();
					PlcConnectionInfo mConnect = null;
					for (int i = 0; i < nConnectSize; i++) 
					{
						if (SystemInfo.getPlcConnectionList().get(i).geteConnectPort() == mPlcInfo.eConnectType) 
						{
							mConnect = SystemInfo.getPlcConnectionList().get(i);
						}
					}
					
					if (mConnect != null) 
					{
						int nMaxRWlen = -1;
						int nProtocolSize = mConnect.getPlcAttributeList().size();
						for (int i = 0; i < nProtocolSize; i++) 
						{
							if (mConnect.getPlcAttributeList().get(i).getnUserPlcId() == mTmpAddr.nUserPlcId) 
							{
								nMaxRWlen = mConnect.getPlcAttributeList().get(i).getnMaxRWLen();
							}
						}

						if (nMaxRWlen > 0) 
						{
							AddrPropArray nResultAddrList = new AddrPropArray();
							mProtocolObj.sortOutAddrList(mTmpRecipeGprop.getnValueAddrList(), nResultAddrList, mPlcInfo, nMaxRWlen, false);
							
							/*整理后的地址，重新赋值*/
							if (nResultAddrList.mSortAddrList != null)
							{
								int nDataPos = 0;
								int nDataSize = nByteList.size();
								int nAddrLen = nResultAddrList.mSortAddrList.length;
								for (int i = 0; i < nAddrLen; i++) 
								{
									nResultAddrList.mSortAddrList[i].eConnectType = (short) mPlcInfo.eConnectType;
									nResultAddrList.mSortAddrList[i].sPlcProtocol = mPlcInfo.sProtocolName;
									
									/*生成发送对象*/
									PlcCmnWriteCtlObj mCmnSubProp = new PlcCmnWriteCtlObj();
									mCmnSubProp.Icallback = mPlcCmnCallback;
									mCmnSubProp.mDataObj = mRecipeInfo;
									
									/*分配数据*/
									int nResultAddrLen = nResultAddrList.mSortAddrList[i].nAddrLen;
									nResultAddrLen *= nByteLen;
									byte[] nSetByteList = new byte[nResultAddrLen];
									for(int k = 0; k < nResultAddrLen; k++)
									{
										if(nDataPos < nDataSize)
										{
											nSetByteList[k] =  nByteList.get(nDataPos);
										}
										
										nDataPos++;
									}
									mCmnSubProp.nDataList = nSetByteList;
									mCmnSubProp.mAddrProp = nResultAddrList.mSortAddrList[i];

									if(i != nAddrLen -1)
									{
										mCmnSubProp.bCallback = false;
									}
									else
									{
										mCmnSubProp.bCallback = true;
									}
									
									/*发送消息写*/
									SkGlobalBackThread.getInstance().getGlobalBackHandler().obtainMessage(MODULE.USER_WRITE_PLC, mCmnSubProp).sendToTarget();
								}
							}
						}
					}
					return true;
				}// end master
			}
		}

		/*按照连接类型把所有地址分类添加到eConnectTypeMap容器中*/
		for(int i = 0; i < nAddrListSize; i++)
		{
			Vector<Byte > nDataList = null;
			AddrProp mTmpAddr = null;
			mTmpAddr = mTmpRecipeGprop.getnValueAddrList().get(i);
			if(null == mTmpAddr) continue;


			short eConnect = mTmpAddr.eConnectType;

			/*取得连接类型*/
			SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnect);
			if(null == mThreadObj)
			{
				Log.e("getRegBytesData", "查找连接类型的的接口:" + eConnect + " 失败，连接类型可能不对");
				continue;
			}

			/*在不同连接类型中，按照连接协议来取得连接协议的对象*/
			PlcCmnWriteCtlObj mCmnSubProp = new PlcCmnWriteCtlObj();
			mCmnSubProp.Icallback = mPlcCmnCallback;
			mCmnSubProp.mDataObj = mRecipeInfo;
			nDataList = new Vector<Byte >();
			nDataList.clear();
			doubleToBytes(nDataList, sValueList[i], 
					mTmpRecipeGprop.geteDataTypeList().get(i));

			int nSize = nDataList.size();
			byte[] nSetByteList = new byte[nSize];
			for(int k = 0; k < nSize; k++)
			{
				nSetByteList[k] = nDataList.get(k);
			}
			mCmnSubProp.nDataList = nSetByteList;
			mCmnSubProp.mAddrProp = mTmpAddr;

			if(i != nAddrListSize -1)
			{
				mCmnSubProp.bCallback = false;
			}
			else
			{
				mCmnSubProp.bCallback = true;
			}

			SkGlobalBackThread.getInstance().getGlobalBackHandler().obtainMessage(MODULE.USER_WRITE_PLC, mCmnSubProp).sendToTarget();
		}

		return true;
	}

	/**
	 * 从PLC中读配方
	 * 
	 * @param nRecipeGroupId
	 * @param nRecipeId
	 * @return
	 */
	private boolean readRecipeFromPlc(CurrentRecipe mRecipeInfo) 
	{
		if(null == mRecipeInfo) return false;

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		if (mRecipeInfo.getCurrentGroupRecipeId() < 0 || mRecipeInfo.getCurrentGroupRecipeId() >= nGroupRecipeSize) return false;

		RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(mRecipeInfo.getCurrentGroupRecipeId());
		if(null == mTmpRecipeGprop) return false;

		/*显示进度条*/
		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = " ";
		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.recipe_read);
		}
		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg).sendToTarget();

		/*发送消息*/
		PlcCmnDataCtlObj mTmpCmnCtlObj = new PlcCmnDataCtlObj();
		mTmpCmnCtlObj.Icallback = mPlcCmnCallback;
		mTmpCmnCtlObj.mDataObj = mRecipeInfo;
		mTmpCmnCtlObj.mAddrList = new AddrPropArray();

		int nAddrListSize = mTmpRecipeGprop.getnValueAddrList().size();
		mTmpCmnCtlObj.mAddrList.mSortAddrList = new AddrProp[nAddrListSize];
		for (int i = 0; i < nAddrListSize; i++)
		{
			mTmpCmnCtlObj.mAddrList.mSortAddrList[i] = mTmpRecipeGprop.getnValueAddrList().get(i);
		}
		SkGlobalBackThread.getInstance().getGlobalBackHandler().obtainMessage(MODULE.USER_READ_PLC, mTmpCmnCtlObj).sendToTarget();

		return true;
	}

	/**
	 * 刷新从PLC中读取的配方数据
	 */
	private void refreshReadPlc(CurrentRecipe mCurrRecipe)
	{
		if(null == mCurrRecipe) return ;

		int nGroupId = mCurrRecipe.getCurrentGroupRecipeId();
		int nRcipeId = mCurrRecipe.getCurrentRecipeId();

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize) return ;

		RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGroupId);
		if(null == mTmpRecipeGprop) return ;

		/* read data from plc */
		int nAddrListSize = mTmpRecipeGprop.getnValueAddrList().size();

		boolean bCurrRecipe = false;
		
		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if(null != nCurrRecipeObj)
		{
			if(nCurrRecipeObj.getCurrentGroupRecipeId() == nGroupId && nCurrRecipeObj.getCurrentRecipeId() == nRcipeId)
			{
				if(null == sCurrRecipeValues)
				{
					sCurrRecipeValues = new String[nAddrListSize];
				}

				if(sCurrRecipeValues.length != nAddrListSize)
				{
					return ;
				}
				bCurrRecipe = true;
			}
		}

		AddrProp addrProp = null;
		boolean bSuccess = false;
		final Vector<Double > nFinalVector = new Vector<Double >();
		for (int i = 0; i < nAddrListSize; i++) 
		{
			addrProp = mTmpRecipeGprop.getnValueAddrList().get(i);
			nTmpDoubleList.clear();

			/* init send data struct */
			if(mTmpRecipeGprop.geteDataTypeList().size() > i)
			{
				mSendData.eDataType = mTmpRecipeGprop.geteDataTypeList().get(i);
			}
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;

			/* get recipe data from plc address */
			bSuccess |= PlcRegCmnStcTools.getRegDoubleData(addrProp, nTmpDoubleList, mSendData);
			if (false == bSuccess)
				break;

			if(!nTmpDoubleList.isEmpty())
			{
				nFinalVector.add(nTmpDoubleList.get(0));
				if(bCurrRecipe)
				{
					sCurrRecipeValues[i] = String.valueOf(nTmpDoubleList.get(0));
				}
			}
		}

		/*The transfer is complete and notice*/
		transCompNotice(nGroupId);

		/*弹出提示确认对话框*/
		final int nCurrGId = nGroupId;
		final int nCurrId = nRcipeId;

		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";
		String sMsgY = "yes";
		String sMsgN = "no";
		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.recipe_issave);
			sMsgY = myActivity.getString(R.string.recipe_yes);
			sMsgN = myActivity.getString(R.string.recipe_no);
		}
		
		new AlertDialog.Builder(SKSceneManage.getInstance().getActivity())   
		.setMessage(sMsg)  
		.setPositiveButton(sMsgY, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialoginterface, int j){
				/* save data to database */
				SKSceneManage.getInstance().time=0;
				int nDataSize = nFinalVector.size();
				String[] sValueList = new String[nDataSize];
				for(int i = 0; i < nDataSize; i++)
				{
					sValueList[i] = nFinalVector.get(i) + "";
				}
				setRecipeData(nCurrGId, nCurrId, sValueList);
			}
		})  
		.setNegativeButton(sMsgN, null)
		.show(); 
		
		/*配方改变需要通知*/
		updateCallback(false, true);
	}

	/*读写回调*/
	SKCommThread.ICmnCompletedCallback mPlcCmnCallback = new SKCommThread.ICmnCompletedCallback() {

		@Override
		public void cmnWriteCompleted(boolean bSuccess, String sErrorInfo, Object mObjMsg) {

			Activity myActivity = SKSceneManage.getInstance().getActivity();
			String sMsg1 = " ";
			String sMsg2 = " ";
			if(myActivity != null)
			{
				sMsg1 = myActivity.getString(R.string.recipe_write_sucess);
				sMsg2 = myActivity.getString(R.string.recipe_write_fail);
			}
			
			/*通知回来, 如果失败则不修改*/
			SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			if(bSuccess)
			{
				sErrorInfo = sMsg1 + sErrorInfo;
			}
			else
			{
				sErrorInfo = sMsg2 + sErrorInfo;
			}
			SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sErrorInfo).sendToTarget();
			if(!bSuccess) return;

			/*The transfer is complete and notice*/
			transCompNotice(((CurrentRecipe)mObjMsg).getCurrentGroupRecipeId());
		}

		@Override
		public void cmnReadCompleted(boolean bSuccess, String sErrorInfo, Object mObjMsg) 
		{
			/*通知回来, 如果失败则不修改*/
			SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			//			if(bSuccess)
			//			{
			//				sErrorInfo = " 读配方成功\n read recipe success \n info:" + sErrorInfo;
			//			}
			//			else
			//			{
			//				sErrorInfo = " 读配方失败\n read recipe failed \n info:" + sErrorInfo;
			//			}
			//			SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sErrorInfo).sendToTarget();
			if(!bSuccess) return;

			/*发送消息刷新从PLC中读取的配方*/
			SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.REFRESH_READ_RECIPE, mObjMsg, mCtlDatabaseCback, 0);
		}
	};

	/**
	 * 从文件中读配方
	 * @param nRecipeGroupId
	 * @return
	 */
	private boolean readRecipeGroupFromFile(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return false;

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize) return false;

		/*get this recipe group recipe data*/
		RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGroupId);
		if(null == mTmpRecipeGprop) return false;

		STORAGE_MEDIA eSaveMedia = mTmpRecipeGprop.geteSaveMedia();

		//		String sFilePath = "recipe.csv";
		String sFilePath = mTmpRecipeGprop.getsRecipeGName() + ".csv";
		switch(eSaveMedia)
		{
		case INSIDE_DISH:
		{
			sFilePath = "/data/data/com.android.Samkoonhmi/formula/recipe/" + sFilePath;
			break;
		}
		case U_DISH:
		{
			sFilePath = "/mnt/usb2/" + sFilePath;
			break;
		}
		case SD_DISH:
		{
			sFilePath = "/mnt/sdcard/" + sFilePath;
			break;
		}
		default:
		{
			return false;
		}
		}
		
		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";

		/*如果配方文件不存在则创建文件*/
		//		try {
		//			sFilePath = new String (sFilePath.getBytes(), "GBK");
		//		} catch (UnsupportedEncodingException e1) {
		//			e1.printStackTrace();
		//		}
		File mCollectFile = new File(sFilePath);

		if(!mCollectFile.exists())
		{
			if(myActivity != null)
			{
				sMsg = myActivity.getString(R.string.recipe_path_noexist);
			}
			
			SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg + sFilePath).sendToTarget();
			return false;
		}

		/*显示进度条*/
		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.recipe_import);
		}
		
		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg + nGroupId + "...").sendToTarget();

		try{
			/*new read file hand*/
			//			FileReader fileReaderHand = new FileReader(sFilePath);
			//			BufferedReader readBuffer = new BufferedReader(fileReaderHand);
			DataInputStream fileReaderHand = new DataInputStream(new FileInputStream(mCollectFile));
			BufferedReader readBuffer = new BufferedReader(new InputStreamReader(fileReaderHand,"GBK"));
			String sTmpStr ;

			String[] sTmpBuf = null;
			sTmpStr = readBuffer.readLine();

			sTmpBuf = sTmpStr.split(",");

			/*check new address length is equal old address length*/
			int nBufLen = sTmpBuf.length;
			if(nBufLen != (mTmpRecipeGprop.getnRecipeLen() + 3))
			{
				if(myActivity != null)
				{
					sMsg = myActivity.getString(R.string.recipe_import_fail);
				}
				readBuffer.close();
				SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
				return false;
			}

			mTmpRecipeGprop.getsElemNameList().clear();

			for(int i = 3; i < nBufLen; i++)
			{
				Vector<String > sElemList = new Vector<String >();
				sElemList.add(sTmpBuf[i]);
				mTmpRecipeGprop.getsElemNameList().add(sElemList);
			}

			/*save csv format data to Memory*/
			RecipeOprop rcpProp = null;
			mTmpRecipeGprop.getmRecipePropList().clear();
			final Vector<String[] > sGroupValues = new Vector<String[] >();
			sGroupValues.clear();
			while((sTmpStr = readBuffer.readLine()) != null)
			{
				sTmpBuf = sTmpStr.split(",");
				nBufLen = sTmpBuf.length;
				if(nBufLen != (mTmpRecipeGprop.getnRecipeLen() + 3) || nBufLen <= 3)
				{
					if(myActivity != null)
					{
						sMsg = myActivity.getString(R.string.recipe_len_error);
					}
					readBuffer.close();
					SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
					SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
					return false;
				}

				rcpProp = new RecipeOprop();

				/*save recipe name*/
				Vector<String > sNameList = new Vector<String >();
				sNameList.add(sTmpBuf[0]);
				rcpProp.setsRecipeName(sNameList);

				/*save recipe id*/
				rcpProp.setnRecipeId(Integer.parseInt(sTmpBuf[1]));

				/*save recipe Description*/
				Vector<String > sDescriList = new Vector<String >();
				sDescriList.add(sTmpBuf[2]);
				rcpProp.setsRecipeDescri(sDescriList);

				/*save recipe value list*/
				String[] sValueList = new String[nBufLen -3];
				for(int i = 3; i < nBufLen; i++)
				{
					sValueList[i - 3] = sTmpBuf[i];
				}

				sGroupValues.add(sValueList);
				mTmpRecipeGprop.getmRecipePropList().add(rcpProp);
			}

			/*save data to recipe group list*/
			RecipeDataProp.getInstance().getmRecipeGroupList().set(nGroupId, mTmpRecipeGprop);

			/*close file */
			readBuffer.close();

			/*弹出提示确认对话框*/
			String sMsgY = "";
			String sMsgN = "";
			if(myActivity != null)
			{
				sMsg = myActivity.getString(R.string.recipe_cover);
				sMsgY = myActivity.getString(R.string.recipe_yes);
				sMsgN = myActivity.getString(R.string.recipe_no);
			}
			
			final int nFinalGroupId = nGroupId;
			final RecipeDataProp.recipeOGprop mSaveGRecipe = mTmpRecipeGprop.copyGroupRecipe();
			new AlertDialog.Builder(SKSceneManage.getInstance().getActivity())   
			.setMessage(sMsg)  
			.setPositiveButton(sMsgY, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialoginterface, int k){
					SKSceneManage.getInstance().time=0;
					/* save data to database */
					SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
					mSaveObj.beginTransaction();

					/*插入数据到recipeNameML表*/
					String sSqlStr = "";
					int nRecipeSize = mSaveGRecipe.getmRecipePropList().size();
					int nCurrLanguaId = SystemInfo.getCurrentLanguageId();
					try{
						
						sSqlStr = "delete from recipeNameML where nGroupId = " + nFinalGroupId;
						mSaveObj.execSql(sSqlStr);
						
						for(int i = 0; i < nRecipeSize; i++)
						{
							m_tmpValues.clear();
							int nRecipeId = mSaveGRecipe.getmRecipePropList().get(i).getnRecipeId();
							int nLanguaSize = mSaveGRecipe.getmRecipePropList().get(i).getsRecipeName().size();
							String sRecipeName = "";
							String sDescri = "";
							if(nCurrLanguaId < nLanguaSize)
							{
								sRecipeName = mSaveGRecipe.getmRecipePropList().get(i).getsRecipeName().get(nCurrLanguaId);
							}

							if(nCurrLanguaId < mSaveGRecipe.getmRecipePropList().get(i).getsRecipeDescri().size())
							{
								sDescri = mSaveGRecipe.getmRecipePropList().get(i).getsRecipeDescri().get(nCurrLanguaId);
							}

							/*保存配方ID、配方名称和描述*/
//							sSqlStr = "update recipeNameML set sRecipeName = '" + sRecipeName + 
//									"',  nRecipeId = " + nRecipeId + ", sRecipeDescri = '" + sDescri +
//									"' where  nGroupId = " + nFinalGroupId + " and  nRecipeId=" + nRecipeId + " and  nLanguageId=" + nCurrLanguaId;
							
							m_tmpValues.clear();
							m_tmpValues.put("nGroupId", nFinalGroupId);
							m_tmpValues.put("sRecipeName", sRecipeName);
							m_tmpValues.put("nRecipeId", nRecipeId);
							m_tmpValues.put("sRecipeDescri", sDescri);
							m_tmpValues.put("nLanguageId", nCurrLanguaId);
							mSaveObj.insertData("recipeNameML", m_tmpValues);

							if(i >= sGroupValues.size()) continue;

//							/*保存配方数据*/
							setRecipeData(nFinalGroupId, nRecipeId, sGroupValues.get(i));
						}
						mSaveObj.commitTransaction();
						
						/*配方改变需要通知*/
						updateCallback(false, true);
					}
					finally{
						mSaveObj.endTransaction();
					}
				}
			})  
			.setNegativeButton(sMsgN, new DialogInterface.OnClickListener() {  
                public void onClick(DialogInterface dialog, int whichButton) {  
                	SKSceneManage.getInstance().time=0;
                	/*配方改变需要通知*/
					updateCallback(false, true);
                }  
            }).show(); 

			/*release source*/
			readBuffer.close();
			fileReaderHand.close();
		}catch (Exception e) {
			e.printStackTrace();
			if(myActivity != null)
			{
				sMsg = myActivity.getString(R.string.recipe_import_fail);
			}
			SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
			return false;
		}

		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.recipe_success);
		}
		
		SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
		return true;
	}

	/**
	 * 写配方到文件中
	 * @param nRecipeGroupId
	 * @return
	 */
	private boolean writeRecipeGroupToFile(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo) return false;

		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize) return false;

		/*get this recipe group recipe data*/
		RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGroupId);
		if(null == mTmpRecipeGprop) return false;

		STORAGE_MEDIA eSaveMedia = mTmpRecipeGprop.geteSaveMedia();
		String sFilePath = mTmpRecipeGprop.getsRecipeGName() + ".csv";
		switch(eSaveMedia)
		{
		case INSIDE_DISH:
		{
			sFilePath = "/data/data/com.android.Samkoonhmi/formula/recipe/" + sFilePath;
			break;
		}
		case U_DISH:
		{
			sFilePath = "/mnt/usb2/" + sFilePath;
			break;
		}
		case SD_DISH:
		{
			sFilePath = "/mnt/sdcard/" + sFilePath;
			break;
		}
		default:
		{
			return false;
		}
		}

		/*如果配方文件不存在则创建文件*/
		//		try {
		//			sFilePath = URLDecoder.decode(sFilePath, "UTF-8");
		//		} catch (UnsupportedEncodingException e1) {
		//			e1.printStackTrace();
		//		}
		File mCollectFile = new File(sFilePath);
		File mParantFile = mCollectFile.getParentFile();
		if(null == mParantFile) return false;

		if(!mParantFile.exists()){
			mParantFile.mkdirs();
		}

		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";
		
		if(mCollectFile.exists())
		{
			boolean bDeleteOk = mCollectFile.delete();
			if(!bDeleteOk)
			{
				if(myActivity != null)
				{
					sMsg = myActivity.getString(R.string.recipe_file_used);
				}
				
				SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
				return false;
			}
		}

		try {
			if(!mCollectFile.createNewFile()) return false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*显示进度条*/
		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.recipe_writing_group);
		}
		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg + nGroupId).sendToTarget();

		try{
			/*new write file hand*/
			//			FileWriter fileWriteHand = new FileWriter(sFilePath, false);
			//			BufferedWriter writeBuffer = new BufferedWriter(fileWriteHand);

			DataOutputStream fileWriteHand = new DataOutputStream(new FileOutputStream(mCollectFile));
			BufferedWriter writeBuffer = new BufferedWriter(new OutputStreamWriter(fileWriteHand,"GBK"));

			//		writeBuffer.newLine();
			
			
			int nCurrLanguage = SystemInfo.getCurrentLanguageId();
			String sTitle = "recipe name"  + "," + "recipe ID"  + "," + "recipe disc";
			if(myActivity != null)
			{
				sTitle = myActivity.getString(R.string.recipe_Title);
			}
			
			int nElemSize = mTmpRecipeGprop.getsElemNameList().size();
			for(int i = 0; i < nElemSize; i++)
			{
				sTitle += ",";
				if(nCurrLanguage >= 0 && nCurrLanguage < mTmpRecipeGprop.getsElemNameList().get(i).size())
				{
					sTitle += mTmpRecipeGprop.getsElemNameList().get(i).get(nCurrLanguage);
				}
				else
				{
					sTitle += " ";
				}
			}
			writeBuffer.write(sTitle);

			int nRecipeNum = mTmpRecipeGprop.getnRecipeNum();
			RecipeOprop rcpProp = null;

			for(int i = 0; i < nRecipeNum; i++)
			{
				rcpProp = mTmpRecipeGprop.getmRecipePropList().get(i);
				writeBuffer.newLine();

				/*添加配方名称*/
				String sTmpWrite = "";
				if(nCurrLanguage >= 0 && nCurrLanguage < rcpProp.getsRecipeName().size())
				{
					sTmpWrite += rcpProp.getsRecipeName().get(nCurrLanguage) + "," ;
				}
				else
				{
					sTmpWrite += " ,";
				}

				/*添加配方ID*/
				sTmpWrite += String.valueOf(rcpProp.getnRecipeId())  + ",";

				/*添加配方描述*/
				if(nCurrLanguage >= 0 && nCurrLanguage < rcpProp.getsRecipeDescri().size())
				{
					sTmpWrite += rcpProp.getsRecipeDescri().get(nCurrLanguage);
				}
				else
				{
					sTmpWrite += " ";
				}

				String[] sValueList = getRecipeData(nGroupId, rcpProp.getnRecipeId(), false);
				if(sValueList == null)
				{
					return false;
				}

				/*添加配方值*/
				int nLen = sValueList.length;
				for(int j = 0; j < nLen; j++)
				{
					sTmpWrite += ",";
					sTmpWrite += sValueList[j];
				}

				writeBuffer.write(sTmpWrite);
			}

			/*close file */
			writeBuffer.flush();
			writeBuffer.close();
			fileWriteHand.close();

		}catch (Exception e) {
			e.printStackTrace();
			if(myActivity != null)
			{
				sMsg = myActivity.getString(R.string.recipe_fail_nomedium);
			}
			
			SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
			return false;
		}

		if(myActivity != null)
		{
			sMsg = myActivity.getString(R.string.save_success);
		}
		
		/*休眠2秒钟，等文件写入成功*/
		try {
			Thread.sleep(2000);  
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
		SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg + sFilePath).sendToTarget();
		return true;
	}

	/**
	 * 传输完成 通知
	 * @param nRecipeGroupId
	 * @return
	 */
	private boolean transCompNotice(int nRecipeGroupId)
	{
		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		if(nRecipeGroupId < 0 || nRecipeGroupId >= nGroupRecipeSize) return false;

		/*get this recipe group recipe data*/
		RecipeDataProp.recipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance().getmRecipeGroupList().get(nRecipeGroupId);
		if(null == mTmpRecipeGprop) return false;
		
		if(!mTmpRecipeGprop.isbCompleteNotic()) return false;

		/*notice data to plc*/
		tmpDataList.clear();
		tmpDataList.add(1);

		/* init send data struct */
		mSendData.eDataType = DATA_TYPE.BIT_1;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

		boolean bSuccess = PlcRegCmnStcTools.setRegIntData(mTmpRecipeGprop.getmComNoticAddr(), tmpDataList, mSendData);
		return bSuccess;
	}

	/**
	 * 删除配方
	 * @param mRecipeInfo
	 * @return
	 */
	private boolean deleteRecipe(CurrentRecipe mRecipeInfo)
	{
		if(null == mRecipeInfo || null == RecipeDataProp.getInstance()) return false;

		/*判断要删除的配方是否存在*/
		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		int nRecipeId = mRecipeInfo.getCurrentRecipeId();
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();

		int nGIndex = -1;
		for(int i = 0; i < nGroupSize; i++)
		{
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == nGroupId)
			{
				nGIndex = i;
				break;
			}
		}

		/*没找到，则返回false*/
		if(nGIndex == -1)
		{
			return false;
		}

		/*删除配方*/
		int nIndex = -1;
		int nSize = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getmRecipePropList().size();
		for(int i = 0; i < nSize; i++)
		{
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getmRecipePropList().get(i).getnRecipeId() == nRecipeId)
			{
				nIndex = i;
				break;
			}
		}

		/*没找到，则返回false*/
		if(nIndex == -1)
		{
			return false;
		}

		/*删除配方*/
		RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getmRecipePropList().remove(nIndex);
		
		/*如果是当前配方则更新当前配方*/
		CurrentRecipe mCurrRecipe = getCurrRecipe();
		if(null != mCurrRecipe)
		{
			if(mCurrRecipe.getCurrentGroupRecipeId() == nGroupId && mCurrRecipe.getCurrentRecipeId() == nRecipeId)
			{
				sCurrRecipeValues = null;
			}
		}

		/*配方数减1*/
		int nRecipeNum = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getnRecipeNum();
		RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).setnRecipeNum(nRecipeNum -1);

		/*删除数据库*/
		SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
		if(mSaveObj != null)
		{
			mSaveObj.beginTransaction();

			/*删除组的配方*/
			mSaveObj.execSql("delete from recipegroup" + nGroupId + " where nRecipeId = " + nRecipeId);

			/*删除组的配方*/
			mSaveObj.execSql("delete from recipeNameML where nGroupId = " + nGroupId + " and nRecipeId = " + nRecipeId);

			mSaveObj.commitTransaction();
			mSaveObj.endTransaction();
		}

		/*配方改变需要通知*/
		updateCallback(false, true);
		return true;
	}

	/**
	 * 删除配方组
	 * @param nRecipeGroupId:配方组号
	 * @return
	 */
	private boolean deleteRecipeGroup(int nRecipeGroupId)
	{
		if(null == RecipeDataProp.getInstance()) return false;

		/*判断要删除的配方是否存在*/
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();

		int nGIndex = -1;
		for(int i = 0; i < nGroupSize; i++)
		{
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == nRecipeGroupId)
			{
				nGIndex = i;
				break;
			}
		}

		/*没找到，则返回false*/
		if(nGIndex == -1)
		{
			return false;
		}

//		RecipeDataProp.getInstance().getmRecipeGroupList().remove(nGIndex);
		RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getmRecipePropList().clear();
		
		/*如果是当前配方则更新当前配方*/
		CurrentRecipe mCurrRecipe = getCurrRecipe();
		if(null != mCurrRecipe)
		{
			if(mCurrRecipe.getCurrentGroupRecipeId() == nRecipeGroupId)
			{
				sCurrRecipeValues = null;
			}
		}

		/*删除数据库*/
		SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
		if(mSaveObj != null)
		{
			mSaveObj.beginTransaction();

			/*删除组的配方名称*/
			mSaveObj.execSql("delete from recipeNameML where nGroupId = " + nRecipeGroupId);

			/*删除组的配方*/
			mSaveObj.execSql("delete from recipegroup" + nRecipeGroupId); 

			//			/*删除组的属性*/
			//			mSaveObj.execSql("delete from recipeCollectGroup where nGroupId = " + nRecipeGroupId);
			//
			//
			//			/*删除组的配方地址和属性*/
			//			mSaveObj.execSql("delete from recipeElemML where nGroupId = " + nRecipeGroupId);
			//
			//			/*从地址表删除地址*/
			//			mSaveObj.execSql("delete from addr where eItemType = 5 and nItemId = " + nRecipeGroupId);

			mSaveObj.commitTransaction();
			mSaveObj.endTransaction();
		}

		/*配方改变需要通知*/
		updateCallback(false, true);
		return true;
	}

	/**
	 * 新建和修改后保存配方，如果是新建，则配方号为-1
	 * @param mRecipeInfo
	 * @return
	 */
	private boolean editSaveRecipe(EditRecipeInfo mEditRecipeInfo)
	{
		if(null == mEditRecipeInfo || null == mEditRecipeInfo.mRecipeInfo || null == mEditRecipeInfo.mRecipeData || null == RecipeDataProp.getInstance()) return false;

		/*判断要删除的配方是否存在*/
		int nGroupId = mEditRecipeInfo.mRecipeInfo.getCurrentGroupRecipeId();
		int nRecipeId = mEditRecipeInfo.mRecipeInfo.getCurrentRecipeId();
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();

		int nGIndex = -1;
		for(int i = 0; i < nGroupSize; i++)
		{
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == nGroupId)
			{
				nGIndex = i;
				break;
			}
		}

		/*没找到，则返回false*/
		if(nGIndex == -1)
		{
			return false;
		}
		
		RecipeDataProp.recipeOGprop mGrecipe = getOGRecipeData(nGroupId);
		int nElemSize = mGrecipe.getsElemNameList().size();
		
		if(mEditRecipeInfo.sValueList == null || mEditRecipeInfo.sValueList.length != nElemSize)
		{
			if(nRecipeId < 0)
			{
				Log.e("RecipeDataCentre", "add new recipe error, because recipe length error");
			}
			else
			{
				Log.e("RecipeDataCentre", "edit recipe error, because recipe length error");
			}
			return false;
		}
		
		/*如果是当前配方则更新当前配方*/
		CurrentRecipe mCurrRecipe = getCurrRecipe();
		if(null != mCurrRecipe)
		{
			if(mCurrRecipe.getCurrentGroupRecipeId() == nGroupId && mCurrRecipe.getCurrentRecipeId() == nRecipeId)
			{
				sCurrRecipeValues = null;
			}
		}

		/*获得配方数量*/
		int nRecipeNum = RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getmRecipePropList().size();
		if(nRecipeId < 0)
		{
			/*新增配方*/
			RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).getmRecipePropList().add(mEditRecipeInfo.mRecipeData);
			RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex).setnRecipeNum(nRecipeNum +1);

			/*添加数据到数据库*/
			SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
			if(mSaveObj != null)
			{
				mSaveObj.beginTransaction();

				/*插入数据到recipeNameML表*/
				m_tmpValues.clear();
				m_tmpValues.put("nGroupId", nGroupId);
				m_tmpValues.put("nRecipeId", mEditRecipeInfo.mRecipeData.getnRecipeId());
				int nLanguaSize = mEditRecipeInfo.mRecipeData.getsRecipeName().size();
				for(int i = 0; i < nLanguaSize; i++)
				{
					m_tmpValues.put("nLanguageId", i);
					m_tmpValues.put("sRecipeName", mEditRecipeInfo.mRecipeData.getsRecipeName().get(i));
					if(i < mEditRecipeInfo.mRecipeData.getsRecipeDescri().size())
					{
						m_tmpValues.put("sRecipeDescri", mEditRecipeInfo.mRecipeData.getsRecipeDescri().get(i));
					}
					mSaveObj.insertData("recipeNameML", m_tmpValues);
				}

				/*插入数据到recipegroup表*/
				m_tmpValues.clear();
				m_tmpValues.put("nRecipeId", mEditRecipeInfo.mRecipeData.getnRecipeId());
				if(mEditRecipeInfo.sValueList != null)
				{
					String sValues = "";
					int nLen = mEditRecipeInfo.sValueList.length;
					for(int i = 0; i < nLen -1; i++)
					{
						sValues += mEditRecipeInfo.sValueList[i] + ",";
					}
					if(nLen > 0)
					{
						sValues += mEditRecipeInfo.sValueList[nLen -1];
					}

					m_tmpValues.put("elems", sValues);
				}
				mSaveObj.insertData("recipegroup" + nGroupId, m_tmpValues);

				mSaveObj.commitTransaction();
				mSaveObj.endTransaction();
			}
			
			/*配方改变需要通知*/
			updateCallback(false, true);
		}
		else
		{
			/*修改数据到数据库*/
			SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
			if(mSaveObj != null)
			{
				mSaveObj.beginTransaction();

				int nCurrLanguaId = SystemInfo.getCurrentLanguageId();

				/*插入数据到recipeNameML表*/
				String sSqlStr = "";
				int nLanguaSize = mEditRecipeInfo.mRecipeData.getsRecipeName().size();
				if(nCurrLanguaId < nLanguaSize)
				{
					String sDescri = "";
					if(nCurrLanguaId < mEditRecipeInfo.mRecipeData.getsRecipeDescri().size())
					{
						sDescri = mEditRecipeInfo.mRecipeData.getsRecipeDescri().get(nCurrLanguaId);
					}

					sSqlStr = "update recipeNameML set sRecipeName = '" + mEditRecipeInfo.mRecipeData.getsRecipeName().get(nCurrLanguaId) + 
							"',  nRecipeId = " + mEditRecipeInfo.mRecipeData.getnRecipeId() + ", sRecipeDescri = '" + sDescri +
							"' where  nGroupId = " + nGroupId + " and  nRecipeId=" + nRecipeId + " and  nLanguageId=" + nCurrLanguaId;

					mSaveObj.execSql(sSqlStr);
				}

				/*插入数据到recipegroup表*/ 
				setRecipeData(nGroupId, nRecipeId, mEditRecipeInfo.sValueList);

				mSaveObj.commitTransaction();
				mSaveObj.endTransaction();
			}
			
			/*配方改变需要通知*/
			updateCallback(false, true);
			//SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.REFRESH_CURR_RECIPE, (Object)false, mCtlDatabaseCback, 0);
		}

		return true;
	}

	/**
	 * 配方回调接口
	 * @author Administrator
	 *
	 */
	public interface IRecipeCallBack{

		/*更新通知*/
		public void update();

		/*当前配方改变*/
		public void currRecipeUpdate();
	}

	/**
	 * 添加注册接口
	 * @param mCallback
	 */
	private void registerUpdate(IRecipeCallBack mCallback)
	{
		if(null == mCallback) return ;

		/*回调*/
		mCallback.update();
		
		/*添加回调接口*/
		if(mCallbackList != null)
		{
			if(!mCallbackList.contains(mCallback))
			{
				mCallbackList.add(mCallback);
			}
		}
	}

	/**
	 * 烧毁回调
	 * @param mCallBack
	 */
	private void destoryCallback(IRecipeCallBack mCallBack)
	{
		if(null == mCallBack || null == mCallbackList) return ;

		/*移除回调*/
		int nIndex = -1;
		int nSize = mCallbackList.size();
		for(int i = 0; i < nSize; i++)
		{
			if(mCallbackList.get(i).equals(mCallBack))
			{
				nIndex = i;
				break;
			}
		}

		if(nIndex >= 0 && nIndex < nSize)
		{
			mCallbackList.removeElementAt(nIndex);
		}
	}

	/**
	 * 同步更新配方
	 */
	private synchronized void updateCallback(boolean bGetDB, boolean bRefCurrRecipe)
	{
		/*更新当前配方的所有数据*/ 
		if(bRefCurrRecipe)
		{
			SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.REFRESH_CURR_RECIPE, (Object)bGetDB, mCtlDatabaseCback, 0);
		}

		if(null == mCallbackList) return ;

		int nSize = mCallbackList.size();
		for(int i = 0; i < nSize; i++)
		{
			mCallbackList.get(i).update();
		}
	}

	/**
	 * double 类型 转bytes 数组
	 * @param nByteList
	 * @param nDValue
	 * @param eDataType
	 * @return
	 */
	private boolean doubleToBytes(Vector<Byte > nByteList, String sValueStr, DATA_TYPE eDataType)
	{
		double nDValue = 0;
		if(sValueStr != null && sValueStr != "")
		{
			try
			{
				nDValue = Double.valueOf(sValueStr);
			}
			catch (NumberFormatException  e){
				Log.e("doubleToBytes", "string to double error" + e.getMessage());
			}
		}

		switch(eDataType)
		{
		case BIT_1:
		{
			byte nTmpValue = (byte)nDValue;
			nByteList.add(nTmpValue);
			nByteList.add((byte)0);
			break;
		}
		case POSITIVE_INT_16:
		case HEX_16:
		case OTC_16:
		case BCD_16:
		{
			int nTmpValue = (int)nDValue;
			nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
			break;
		}
		case POSITIVE_INT_32:
		case HEX_32:
		case OTC_32:
		case BCD_32:
		{
			long nTmpValue = (long)nDValue;
			nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 16) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 24) & 0xff));
			break;
		}
		case INT_16:
		{
			int nTmpValue = (int)nDValue;
			nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
			break;
		}
		case INT_32:
		{
			int nTmpValue = (int)nDValue;
			nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 16) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 24) & 0xff));
			break;
		}
		case FLOAT_32:
		{
			int nTmpValue = Float.floatToIntBits((float)nDValue);
			nByteList.add((byte)((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 8) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 16) & 0xff));
			nByteList.add((byte)((nTmpValue >>> 24) & 0xff));

			break;
		}
		case ASCII_STRING:
		{
			break;
		}
		case OTHER_DATA_TYPE:
		default:
		{
			break;
		}
		}
		return true;
	}

	/**
	 * double 类型 转bytes 数组
	 * @param nByteList
	 * @param nDValue
	 * @param eDataType
	 * @return
	 */
	private String bytesTodouble(Vector<Byte > nByteList, DATA_TYPE eDataType)
	{
		String sResult = "";
		switch(eDataType)
		{
		case BIT_1:
		{
			if(nByteList.size() >= 2)
			{
				sResult = nByteList.get(0) + "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			}
			else
			{
				nByteList.clear();
			}
			break;
		}
		case INT_16:
		{
			if(nByteList.size() >= 2)
			{
				Vector<Byte > nBytes = new Vector<Byte>();
				nBytes.add(nByteList.get(0));
				nBytes.add(nByteList.get(1));
				
				Vector<Short> nShorts = new Vector<Short >();
				PlcRegCmnStcTools.bytesToShorts(nBytes, nShorts);
				
				if(!nShorts.isEmpty())
				{
					sResult = nShorts.get(0) + "";
				}
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			}
			else
			{
				nByteList.clear();
			}
			break;
		}
		case HEX_16:
		case OTC_16:
		case BCD_16:
		case POSITIVE_INT_16:
		{
			if(nByteList.size() >= 2)
			{
				Vector<Byte > nBytes = new Vector<Byte>();
				nBytes.add(nByteList.get(0));
				nBytes.add(nByteList.get(1));
				
				Vector<Integer> nShorts = new Vector<Integer >();
				PlcRegCmnStcTools.bytesToUShorts(nBytes, nShorts);
				
				if(!nShorts.isEmpty())
				{
					sResult = nShorts.get(0) + "";
				}
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			}
			else
			{
				nByteList.clear();
			}
			break;
		}
		case HEX_32:
		case OTC_32:
		case BCD_32:
		case POSITIVE_INT_32:
		{
			long nTmpValue0 = 0;
			long nTmpValue1 = 0;
			long nTmpValue2 = 0;
			long nTmpValue3 = 0;
			if(nByteList.size() >= 4)
			{
				nTmpValue0 = nByteList.get(0) & 0xff;
				nTmpValue1 = nByteList.get(1) & 0xff;
				nTmpValue2 = nByteList.get(2) & 0xff;
				nTmpValue3 = nByteList.get(3) & 0xff;
				sResult = ((nTmpValue3 << 24) + (nTmpValue2 << 16) + (nTmpValue1 << 8) + (nTmpValue0)) + "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			}
			else
			{
				nByteList.clear();
			}
			break;
		}
		case INT_32:
		{
			int nTmpValue0 = 0;
			int nTmpValue1 = 0;
			int nTmpValue2 = 0;
			int nTmpValue3 = 0;
			if(nByteList.size() >= 4)
			{
				nTmpValue0 = nByteList.get(0) & 0xff;
				nTmpValue1 = nByteList.get(1) & 0xff;
				nTmpValue2 = nByteList.get(2) & 0xff;
				nTmpValue3 = nByteList.get(3) & 0xff;
				sResult = ((nTmpValue3 << 24) + (nTmpValue2 << 16) + (nTmpValue1 << 8) + (nTmpValue0)) + "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			}
			else
			{
				nByteList.clear();
			}
			break;
		}
		case FLOAT_32:
		{
			int nTmpValue0 = 0;
			int nTmpValue1 = 0;
			int nTmpValue2 = 0;
			int nTmpValue3 = 0;
			if(nByteList.size() >= 4)
			{
				nTmpValue0 = nByteList.get(0) & 0xff;
				nTmpValue1 = nByteList.get(1) & 0xff;
				nTmpValue2 = nByteList.get(2) & 0xff;
				nTmpValue3 = nByteList.get(3) & 0xff;
				nTmpValue0 = ((nTmpValue3 << 24) + (nTmpValue2 << 16) + (nTmpValue1 << 8) + (nTmpValue0));
				sResult = Float.intBitsToFloat(nTmpValue0) + "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			}
			else
			{
				nByteList.clear();
			}

			break;
		}
		case ASCII_STRING:
		{
			break;
		}

		case OTHER_DATA_TYPE:
		default:
		{
			break;
		}
		}

		return sResult;
	}

	/**
	 * 查询单个配方
	 * @param nGroupId
	 * @param nRecipeId
	 * @return
	 */
	public synchronized String[] getRecipeData(int nGroupId, int nRecipeId, boolean bGetDatabase)
	{
		String[] sValueList = null;
		
		/*查询配方是否存在*/
		if(!getRecipeIsExist(nGroupId, nRecipeId)) return sValueList;

		/*如果是当前配方，则返回当前配方值*/
		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if(null != nCurrRecipeObj)
		{
			if(!bGetDatabase && nCurrRecipeObj.getCurrentGroupRecipeId() == nGroupId && nCurrRecipeObj.getCurrentRecipeId() == nRecipeId)
			{
				if(null != sCurrRecipeValues)
				{
					return sCurrRecipeValues;
				}
			}
		}
	
		/*从数据库查询当前配方*/
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if(null == dbObj) return sValueList;

		Cursor dataProp = dbObj.getDatabaseBySql("select * from recipegroup" + nGroupId + " where nRecipeId = " + nRecipeId , null);
		if (null == dataProp) return sValueList;

		if(dataProp.moveToNext())
		{
			String sValues = dataProp.getString(dataProp.getColumnIndex("elems"));
			if(sValues != null )
			{
				sValueList = sValues.split("[,]");
			}
		}
		dataProp.close();

		return sValueList;
	}

	/**
	 * 设置单个配方
	 * @param nGroupId
	 * @param nRecipeId
	 * @return
	 */
	public synchronized boolean setRecipeData(int nGroupId, int nRecipeId, String[] sValueList)
	{
		if(null == sValueList) return false;

		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if(null == dbObj) return false;

		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if(nCurrRecipeObj != null)
		{
			if(nCurrRecipeObj.getCurrentGroupRecipeId() == nGroupId && nCurrRecipeObj.getCurrentRecipeId() == nRecipeId)
			{
				sCurrRecipeValues = sValueList;
			}
		}

		/*更新当前配方*/
		String sValues = "";
		int nLen = sValueList.length;
		for(int i = 0; i < nLen -1; i++)
		{
			sValues += sValueList[i]+",";
		}
		if(nLen > 0)
		{
			sValues += sValueList[nLen -1];
		}

		String sSqlStr = "delete from recipegroup" + nGroupId + " where nRecipeId=" + nRecipeId;
		dbObj.execSql(sSqlStr);

		m_tmpValues.clear();
		m_tmpValues.put("nRecipeId", nRecipeId);
		m_tmpValues.put("elems", sValues);
		dbObj.insertData("recipegroup" + nGroupId, m_tmpValues);
		
		return true;
	}

	/**
	 * 获取配方组元素信息
	 * @param gid-配方组Id
	 * @param top-起始行
	 * @param count-获取多少行数据
	 */
	public synchronized ArrayList<String[]> getRecipeGroup(int gid,int top,int count){
		ArrayList<String[]> data = null;

		/* 从数据库查询当前配方 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return data;

		Cursor cursor = dbObj.getDatabaseBySql("select * from recipegroup"+ gid + " order by nRecipeId asc limit "+(top-1)+","+count, null);

		if (null == cursor)
			return null;

		data = new ArrayList<String[]>();
		int pid = -1;
		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("nRecipeId"));
			if (id != pid) {
				pid = id;
				String values = cursor
						.getString(cursor.getColumnIndex("elems"));
				if (values != null) {
					String elems[] = values.split("[,]");
					data.add(elems);
				}
			}
		}
		cursor.close();

		return data;
	}
	
	public int getRecipeDataCount(int id){
		int result=0;
		/* 从数据库查询当前配方 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return result;

		Cursor cursor = dbObj.getDatabaseBySql("select count(*) from recipegroup"+ id ,null);
		if(cursor!=null){
			while (cursor.moveToNext()) {
				result=cursor.getInt(0);
			}
			cursor.close();
		}

		return result;
	}
	
	/**
	 * 查找配方是否存在
	 * @param nGroupId
	 * @param nRecipeId
	 * @return
	 */
	public boolean getRecipeIsExist(int nGroupId, int nRecipeId)
	{
		RecipeDataProp.recipeOGprop mGrecipe = getOGRecipeData(nGroupId);
		if(null == mGrecipe) return false;
		
		int nRecipeSize = mGrecipe.getmRecipePropList().size();
		for(int i = 0; i < nRecipeSize; i++)
		{
			if(mGrecipe.getmRecipePropList().get(i).getnRecipeId() == nRecipeId)
			{
				return true;
			}
		}
		
		return false;
	}
}
