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
import java.util.regex.Pattern;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.RecipeDataBiz;
import com.android.Samkoonhmi.databaseinterface.SKDataBaseInterface;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.RecipeOGprop;
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
import com.android.Samkoonhmi.skwindow.EmailOperDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.AddrPropArray;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcCmnDataCtlObj;
import com.android.Samkoonhmi.util.PlcCmnWriteCtlObj;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

public class RecipeDataCentre {
	
	private static final String TAG="RecipeDataCentre";
	/* 最小刷新频率，单位是毫秒 */
	private int nRefreashCycle = 300;

	/* 当前配方的所有属性 */
	private Vector<Double> nTmpDoubleList = new Vector<Double>();
	private Vector<Byte> nTmpRecipeDataList = new Vector<Byte>();
	private Vector<Byte> nOldRwiDataList = new Vector<Byte>();
	AddrProp mRWIAddr = new AddrProp();

	/* 与配方同步控件的回调接口集合 */
	private Vector<IRecipeCallBack> mCallbackList = new Vector<IRecipeCallBack>();

	/* 数据库操作临时变量 */
	private ContentValues m_tmpValues = new ContentValues();

	/* get data list */
	private Vector<Integer> tmpDataList = new Vector<Integer>();
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();

	/* 地址控制PLC值 */
	private Vector<Integer> nAddrCtlDataList = new Vector<Integer>();

	/* 当前配方的值 */
	private String[] sCurrRecipeValues = null;

	private boolean bChangeRecipe = false;
	private boolean bHaveRecipe = false;

	private RecipeDataCentre() {
	}

	/**
	 * 通知线程的单实例
	 */
	private static RecipeDataCentre m_mRecipeCentreObj = null;

	public synchronized static RecipeDataCentre getInstance() {
		if (null == m_mRecipeCentreObj) {
			m_mRecipeCentreObj = new RecipeDataCentre();
		}
		return m_mRecipeCentreObj;
	}

	/**
	 * 从配方数据库中 读取数据
	 * 
	 * @return
	 */
	private boolean initDataInfo() {
		/* 数据库中读取初始化数据，具体的配方数据不读取 */
		boolean bSuccess = RecipeDataBiz.select();

		/* 配方改变需要通知 */
		updateCallback(false, true);

		return bSuccess;
	}

	// /**
	// * 获得当前配方属性
	// *
	// * @return CurrentRecipe： 当前配方的属性对象
	// */
	// public CurrentRecipe getCurrRecipeId() {
	// return m_nCurrRecipeId;
	// }
	//
	// /**
	// * 设置当前配方属性
	// *
	// * @param nCurrRecipeId ： 当前配方属性
	// */
	// public void setCurrRecipeId(CurrentRecipe nCurrRecipeId) {
	// this.m_nCurrRecipeId = nCurrRecipeId;
	// }

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
	 * 
	 * @param nGroupID
	 *            : 组ID
	 * @return
	 */
	public RecipeOGprop getOGRecipeData(int nGroupID) {
		/* 判断组号是否存在 */
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList()
				.size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i)
					.getnGRecipeID() == nGroupID) {
				return RecipeDataProp.getInstance().getmRecipeGroupList()
						.get(i);
			}
		}

		return null;
	}

	/**
	 * 修改当前配方
	 * 
	 * @param mCurrRecipe
	 */
	private boolean bSetCurrentRecipe=false;
	public synchronized void setCurrRecipe(int nGroupId, int nRecipeId) {
		if (null == getCurrRecipe()){
			return;
		}
		
		Log.d(TAG, "nGroupId="+nGroupId+",nRecipeId="+nRecipeId);
		
		if (nGroupId == getCurrRecipe().getCurrentGroupRecipeId()
				&& nRecipeId == getCurrRecipe().getCurrentRecipeId()) {
			return;
		}
		

		/* 标示正在修改配方 */
		bChangeRecipe = true;
		bSetCurrentRecipe=true;

		CurrentRecipe mCurrRecipe = getCurrRecipe();
		mCurrRecipe.setCurrentGroupRecipeId(nGroupId);
		mCurrRecipe.setCurrentRecipeId(nRecipeId);
		SystemInfo.setCurrentRecipe(mCurrRecipe);

		int nSize = mCallbackList.size();
		for (int i = 0; i < nSize; i++) {
			mCallbackList.get(i).currRecipeUpdate();
		}

		/* 修改当前配方地址 */
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

		/* 写入内部地址RWI区 */
		Vector<Integer> nSetByteList = new Vector<Integer>();
		nSetByteList.add(nGroupId);
		nSetByteList.add(nRecipeId);
		PlcRegCmnStcTools.setRegIntData(mRWIAddr, nSetByteList, mSendData);

		/* 更新当前配方的所有数据 */
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.REFRESH_CURR_RECIPE,
						(Object) true, mCtlDatabaseCback, 0);

		// 修改当前配方 将配方组号和配方号写入内部寄存器地址
		SystemVariable.getInstance().setCurrentRecipeGidToAddr();
		SystemVariable.getInstance().setCurrentRecipeidToAddr();
	}

	/**
	 * 获得当前配方
	 * 
	 * @return
	 */
	public CurrentRecipe getCurrRecipe() {
		return SystemInfo.getCurrentRecipe();
	}

	/**
	 * 线程启动
	 */
	public void start() {
		/* 设置线程标识符 */
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.RECIPER_COLLECT_INIT,
						(Object) null, mCtlDatabaseCback, 2000);
	}

	/**
	 * 停止线程
	 */
	public void stop() {
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.NOTIC_CLEAR_CALLBACK,
						(Object) null, mCtlDatabaseCback, 0);
	}

	/**
	 * 编辑配方的类
	 * 
	 * @author Administrator
	 * 
	 */
	public class EditRecipeInfo {
		public CurrentRecipe mRecipeInfo = null;
		public String[] sValueList = null;
		public RecipeOprop mRecipeData = null;
	}

	/**
	 * 新建和修改配方，新建的时候mRecipeInfo.nRecipeId = -1,其余是修改配方
	 * 
	 * @param mRecipeInfo
	 *            ：当前配方对象，包括组号和配方号
	 * @param mRecipeData
	 *            ：配方的属性
	 */
	public synchronized void msgEditRecipeSave(EditRecipeInfo mEditInfo) {
		if (null == mEditInfo)
			return;
		
		CurrentRecipe cc=mEditInfo.mRecipeInfo;
		if (cc!=null) {
			if (mEditInfo.mRecipeInfo.getCurrentRecipeId()!=-1) {
				Log.d(TAG, "save current recipe id="+mEditInfo.mRecipeInfo.getCurrentRecipeId());
				bSetCurrentRecipe=true;
			}
		}

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.RECIPER_EDIT_SAVE, mEditInfo,
						mCtlDatabaseCback, 0);
	}

	/**
	 * 删除配方
	 * 
	 * @param mRecipeInfo
	 *            ：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgDeleteRecipe(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return;

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.RECIPER_DELETE, mRecipeInfo,
						mCtlDatabaseCback, 0);
	}

	/**
	 * 删除配方组
	 * 
	 * @param nGroupId
	 *            ：配方组号
	 */
	public synchronized void msgDeleteRecipeGroup(int nGroupId) {
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.RECIPER_GROUP_DELETE, nGroupId,
						mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从PLC读配方
	 * 
	 * @param mRecipeInfo
	 *            ：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgReadRecipeFromPlc(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return;

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.READ_RECIPE_FROM_PLC,
						mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从写配方到PLC
	 * 
	 * @param mRecipeInfo
	 *            ：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgWriteRecipeToPlc(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return;

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.WRITE_RECIPE_TO_PLC,
						mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从写文件读配方，导入配方
	 * 
	 * @param mRecipeInfo
	 *            ：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgReadRecipeFromFile(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return;

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.READ_RECIPE_FROM_FILE,
						mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息从写配方到文件，导出配方
	 * 
	 * @param mRecipeInfo
	 *            ：当前配方对象，包括组号和配方号
	 */
	public synchronized void msgWriteRecipeToFile(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return;

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.WRITE_RECIPE_TO_FILE,
						mRecipeInfo, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息，把选中的配方组写入文件
	 * 
	 * @param groupList
	 *            -选中的配方列表。
	 */
	public synchronized void msgWriteRecipeSToFiles(ArrayList<Integer> groupList) {
		if (null == groupList || groupList.size() == 0)
			return;
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.WRITE_RECIPES_TO_FILE,
						groupList, mCtlDatabaseCback, 0);
	}

	/**
	 * 发送消息，把选中的配方组写入文件
	 * 
	 * @param groupList
	 *            -选中的配方列表。
	 */
	public synchronized void msgWriteAllRecipeSToFiles(String path) {

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.WRITE_ALL_RECIPES_TO_FILE,
						path, mCtlDatabaseCback, 0);
	}

	/**
	 * 添加注册接口
	 * 
	 * @param mCallback
	 *            : 注册的接口
	 */
	public void msgRegisterUpdate(IRecipeCallBack mCallback) {
		if (null == mCallback)
			return;

		mCallback.update();
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.ADD_CALL_BACK, mCallback,
						mCtlDatabaseCback, 0);
	}

	/**
	 * 注销配方更新接口
	 * 
	 * @param mCallback
	 */
	public void msgDestoryCallback(IRecipeCallBack mCallback) {
		if (null == mCallback)
			return;

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.CANCEL_CALL_BACK, mCallback,
						mCtlDatabaseCback, 0);
	}

	/* 回调接口 */
	SKThread.ICallback mCtlDatabaseCback = new SKThread.ICallback() {

		@Override
		public void onUpdate(Object msg, int taskId) {
			switch (taskId) {
			case MODULE.RECIPER_COLLECT_INIT: // 配方数据库数据初始化消息
			{
				long nCurrMillis = System.currentTimeMillis();
				boolean bSuccess = initDataInfo();
				System.out.println("init recipe database need time :"
						+ (System.currentTimeMillis() - nCurrMillis) + "ms");

				if (bSuccess
						&& RecipeDataProp.getInstance().getmRecipeGroupList()
								.size() > 0) {
					bHaveRecipe = true;
					SKThread.getInstance()
							.getBinder()
							.onTask(MODULE.CALLBACK,
									MODULE.RECIPER_COLLECT_NOTIC,
									(Object) null, mCtlDatabaseCback,
									nRefreashCycle);
				}
				break;
			}
			case MODULE.RECIPER_COLLECT_NOTIC: // 配方刷新
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				/* 时间刷新函数 */
				refreashRecipeData();
				refreashRecipeFromAddr();

				SKThread.getInstance()
						.getBinder()
						.onTask(MODULE.CALLBACK, MODULE.RECIPER_COLLECT_NOTIC,
								(Object) null, mCtlDatabaseCback,
								nRefreashCycle);
				break;
			}
			case MODULE.RECIPER_EDIT_SAVE: // 保存新建或当前编辑的配方，新建的配方，配方号为-1
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					editSaveRecipe((EditRecipeInfo) msg);
				}
				break;
			}
			case MODULE.RECIPER_DELETE: // 删除配方
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					deleteRecipe((CurrentRecipe) msg);
				}
				break;
			}
			case MODULE.RECIPER_GROUP_DELETE: // 删除配方组
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					deleteRecipeGroup((Integer) msg);
				}
				break;
			}
			case MODULE.READ_RECIPE_FROM_PLC: // 从PLC读配方
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					readRecipeFromPlc((CurrentRecipe) msg);
				}
				break;
			}
			case MODULE.WRITE_RECIPE_TO_PLC: // 写配方到PLC
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					writeRecipeToPlc((CurrentRecipe) msg);
				}
				break;
			}
			case MODULE.WRITE_RECIPE_TO_FILE: // 写配方到文件，导出配方
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					writeRecipeGroupToFile((CurrentRecipe) msg);
				}
				break;
			}
			case MODULE.WRITE_RECIPES_TO_FILE: // 写配方组到文件
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				ArrayList<Integer> groupList = (ArrayList<Integer>) msg;
				for (int i = 0; i < groupList.size(); i++) {
					RecipeOGprop oGprop = RecipeDataCentre.getInstance()
							.getOGRecipeData(groupList.get(i));
					if (oGprop != null) {
						oGprop.seteSaveMedia(STORAGE_MEDIA.OTHER_STORAGE_MEDIA);
					}
					writeRecipeGroupToFile(oGprop, false);
				}
				EmailOperDialog.CompleteCount += 1;
				break;
			}
			case MODULE.WRITE_ALL_RECIPES_TO_FILE: // 导出所有的配方
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe) {
					return;
				}
				String path=(String)msg;
				if (path==null||path.equals("")) {
					SKToast.makeText(ContextUtl.getInstance().getString(R.string.path_error), Toast.LENGTH_SHORT).show();
					return ;
				}
				STORAGE_MEDIA type = STORAGE_MEDIA.U_DISH;
				if (path.equals("/mnt/usb2/")) {
					type = STORAGE_MEDIA.U_DISH;
				}else if (path.equals("/mnt/sdcard/")) {
					type = STORAGE_MEDIA.SD_DISH;
				}

				// 进行导出配方
				int nGroupSize = RecipeDataProp.getInstance()
						.getmRecipeGroupList().size();
				for (int i = 0; i < nGroupSize; i++) {
					RecipeOGprop oGprop = RecipeDataCentre.getInstance()
							.getOGRecipeData(i);
					if (oGprop != null) {
						oGprop.seteSaveMedia(type);
						writeRecipeGroupToFile(oGprop, true);
					}
				}

			}
				break;
			case MODULE.READ_RECIPE_FROM_FILE: // 从文件读配方，导入配方
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				if (null != msg) {
					readRecipeGroupFromFile((CurrentRecipe) msg);
				}
				break;
			}
			case MODULE.ADD_CALL_BACK: // 注册所有跟配方相关的控件更新配方的回调接口
			{
				if (null != msg) {
					registerUpdate((IRecipeCallBack) msg);
				}
				break;
			}
			case MODULE.NOTIC_CLEAR_CALLBACK: // 通知所有跟配方相关的回调接口清空
			{
				if (mCallbackList != null) {
					mCallbackList.clear();
				}
				break;
			}
			case MODULE.CANCEL_CALL_BACK: {
				destoryCallback((IRecipeCallBack) msg);
				break;
			}
			case MODULE.REFRESH_READ_RECIPE: // 更新读得的数据
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;

				refreshReadPlc((CurrentRecipe) msg);
				break;
			}
			case MODULE.REFRESH_CURR_RECIPE: // 更新当前配方数据
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe)
					break;
				Log.d(TAG, "REFRESH_CURR_RECIPE .......");
				updateCurrRecipeToAddr((Boolean) msg);
				break;
			}
			case MODULE.RECIPE_INFO:
			{
				//配方导入
				break;
			}
			case MODULE.RECIPE_EXPORT:
			{
				//配方导出
				
				break;
			}
			case MODULE.RECIPE_GROUP_SAVE:
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe){
					break;
				}
				ArrayList<String[]> data=(ArrayList<String[]>)msg;
				if (data==null||data.size()==0) {
					break;
				}
				//配方组保存
				
				recipeGroupSave(data);
				
				break;
			}
			case MODULE.RECIPE_SAVE:
			{
				/* 没有配方，返回 */
				if (!bHaveRecipe){
					break;
				}
				ArrayList<String[]> data=(ArrayList<String[]>)msg;
				if (data==null||data.size()==0) {
					break;
				}
				recipeSave(data);
				break;
			}
			case MODULE.RECIPE_GROUP_COPY:
			{
				//配方组拷贝By name
				break;
			}
			case MODULE.RECIPE_COPY:
			{
				//配方拷贝by name
				break;
			}
			case MODULE.RECIPE_DELETE:
			{
				//配方删除by name
				break;
			}
			default: {
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
	 * 判断是否存在目录
	 * 
	 * @param path
	 * @return 存在 返回true
	 */
	private boolean isDirectory(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}

		file.mkdirs();
		if (file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 配方刷新函数
	 */
	private void refreashRecipeData() {
		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();

		for (int i = 0; i < nGroupRecipeSize; i++) {
			/* get this recipe group recipe data */
			RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
					.getmRecipeGroupList().get(i);
			if (null == mTmpRecipeGprop)
				continue;

			if (!mTmpRecipeGprop.isbNeedCtlAddr())
				continue;
			AddrProp mCtlAddr = mTmpRecipeGprop.getmCtlAddr();
			if (mCtlAddr == null)
				continue;

			/* init send data struct */
			mSendData.eDataType = DATA_TYPE.INT_16;
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;
			mCtlAddr.nAddrLen = 2;

			/* get data from plc */
			tmpDataList.clear();
			boolean bSuccess = PlcRegCmnStcTools.getRegIntData(mCtlAddr,
					tmpDataList, mSendData);
			if (bSuccess && tmpDataList.size() >= 1) {
				/* 判断控制值是否重新变法了 */
				while (nAddrCtlDataList.size() <= i) {
					nAddrCtlDataList.add(0);
				}

				int nCtlValue = tmpDataList.get(0);
				if (nAddrCtlDataList.get(i) == nCtlValue)
					continue;
				nAddrCtlDataList.set(i, nCtlValue);

				int nRecipeGId = mTmpRecipeGprop.getnGRecipeID();
				switch (nCtlValue) {
				case 1: // read recipe from plc
				{
					int nRecipeId = 0;
					if (tmpDataList.size() > 1) {
						nRecipeId = tmpDataList.get(1);
						CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
						mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
						mRefreashRecipeId.setCurrentRecipeId(nRecipeId);
						msgReadRecipeFromPlc(mRefreashRecipeId);
					}

					break;
				}
				case 2: // write recipe to plc
				{
					int nRecipeId = 0;
					if (tmpDataList.size() > 1) {
						nRecipeId = tmpDataList.get(1);
						CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
						mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
						mRefreashRecipeId.setCurrentRecipeId(nRecipeId);
						msgWriteRecipeToPlc(mRefreashRecipeId);
					}
					break;
				}
				case 3: // read recipe group from file
				{
					CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
					mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
					mRefreashRecipeId.setCurrentRecipeId(-1);
					msgReadRecipeFromFile(mRefreashRecipeId);
					break;
				}
				case 4: // write recipe group to file
				{
					CurrentRecipe mRefreashRecipeId = new CurrentRecipe();
					mRefreashRecipeId.setCurrentGroupRecipeId(nRecipeGId);
					mRefreashRecipeId.setCurrentRecipeId(-1);
					msgWriteRecipeToFile(mRefreashRecipeId);
					break;
				}
				default: {
					break;
				}
				}
			}// if(bSuccess)
		}// for(nGroupRecipeSize)
	}

	/**
	 * 修改当前配方的数据到内部地址区
	 */
	private void updateCurrRecipeToAddr(boolean bGetFromDB) {
		/* 标示已经修改配方 */
		bChangeRecipe = false;

		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if (null == nCurrRecipeObj)
			return;

		int nGroupId = nCurrRecipeObj.getCurrentGroupRecipeId();
		int nRcipeId = nCurrRecipeObj.getCurrentRecipeId();

		/* 如果当前配方组不存在，则返回 */
		RecipeOGprop mRGObj = getOGRecipeData(nGroupId);
		if (mRGObj == null)
			return;

		/* 当前配方不存在，则返回 */
		String[] sValueList = getRecipeData(nGroupId, nRcipeId, bGetFromDB);
		if (sValueList == null) {
			return;
		}
		sCurrRecipeValues = sValueList;
		for( int i = 0; i < sCurrRecipeValues.length; i++ ){
			sCurrRecipeValues[i] = subZeroAndDot(sCurrRecipeValues[i]);
		}

		int nElementSize = sValueList.length;
		int nAddrListSize = mRGObj.getnValueAddrList().size();
		int nEdataTypeSize = mRGObj.geteDataTypeList().size();

		if (nElementSize != nAddrListSize || nElementSize != nEdataTypeSize)
			return;

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
		doubleToBytes(nTmpRecipeDataList, String.valueOf(nGroupId),
				DATA_TYPE.INT_16);
		doubleToBytes(nTmpRecipeDataList, String.valueOf(nRcipeId),
				DATA_TYPE.INT_16);

		/* 添加数据的值到bytes容器中 */
		for (int i = 0; i < nAddrListSize; i++) {
			mRWIAddr.nAddrLen += mRGObj.getnValueAddrList().get(i).nAddrLen;
			doubleToBytes(nTmpRecipeDataList, sValueList[i], mRGObj
					.geteDataTypeList().get(i));
		}

		/* 写入内部地址RWI区 */
		int nSize = nTmpRecipeDataList.size();
		byte[] nSetByteList = new byte[nSize];
		for (int i = 0; i < nSize; i++) {
			nSetByteList[i] = nTmpRecipeDataList.get(i);
		}
		
		if (bSetCurrentRecipe) {
			//Log.d(TAG, "update current id="+nRcipeId);
			bSetCurrentRecipe=false;
			SystemVariable.getInstance().writeBitAddr(1, SystemAddress.getInstance().recipeChange());
			
		}
		
		PlcRegCmnStcTools.setRegBytesData(mRWIAddr, nSetByteList, mSendData);
	}

	/**
	 * 如果RWI区的值改变，则更新到当前配方去
	 */
	private synchronized void refreashRecipeFromAddr() {
		/* 如果人为修改配方未完成，则返回 */
		if (bChangeRecipe){
			Log.d(TAG, "bChangeRecipe=="+bChangeRecipe);
			return;
		}

		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if (null == nCurrRecipeObj)
			return;

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
		boolean bGetSuccess = PlcRegCmnStcTools.getRegIntData(mRWIAddr,
				tmpDataList, mSendData);
		if (!bGetSuccess || tmpDataList.size() < 2) {
			return;
		}

		if (nGroupId != tmpDataList.get(0) || nRcipeId != tmpDataList.get(1)) {
			/* 更改当前配方 */
			//Log.d(TAG, "========");
			setCurrRecipe(tmpDataList.get(0), tmpDataList.get(1));
			return;
		}
		
		//Log.d(TAG, "rid==="+tmpDataList.get(1));

		/* 如果当前配方组不存在，则返回 */
		RecipeOGprop mRGObj = getOGRecipeData(nGroupId);
		if (mRGObj == null)
			return;

		/* 当前配方不存在，则返回 */
		if (sCurrRecipeValues == null) {
			sCurrRecipeValues = getRecipeData(nGroupId, nRcipeId, false);
			if (sCurrRecipeValues == null) {
				return;
			}
			for(int i = 0; i< sCurrRecipeValues.length; i++){
				sCurrRecipeValues[i] = subZeroAndDot(sCurrRecipeValues[i]);
			}
		}

		/* 存在，则更新到RWI区 */
		int nElementSize = sCurrRecipeValues.length;
		int nAddrListSize = mRGObj.getnValueAddrList().size();
		int nEdataTypeSize = mRGObj.geteDataTypeList().size();

		if (nElementSize != nAddrListSize || nElementSize != nEdataTypeSize)
			return;

		/* 获取RWI的值 */
		mRWIAddr.nAddrValue = 2;
		mRWIAddr.nAddrLen = 0;
		for (int i = 0; i < nAddrListSize; i++) {
			mRWIAddr.nAddrLen += mRGObj.getnValueAddrList().get(i).nAddrLen;
		}

		nTmpRecipeDataList.clear();
		PlcRegCmnStcTools.getRegBytesData(mRWIAddr, nTmpRecipeDataList,
				mSendData);

		/* 比较配方是否更改 */
		int nBytesLen = nTmpRecipeDataList.size();
		if (nOldRwiDataList.size() != nBytesLen) {
			nOldRwiDataList.clear();
			for (int i = 0; i < nBytesLen; i++) {
				nOldRwiDataList.add(nTmpRecipeDataList.get(i));
			}
		} else {
			/* 判断数据是否相同 */
			bGetSuccess = false;
			for (int i = 0; i < nBytesLen; i++) {
				if (nOldRwiDataList.get(i).equals(nTmpRecipeDataList.get(i)) == false) {
					bGetSuccess = true;
					nOldRwiDataList.set(i, nTmpRecipeDataList.get(i));
				}
			}
			if (bGetSuccess == false)
				return;
		}

		/* 不同，则更新当前配方 */
		nTmpDoubleList.clear();
		for (int i = 0; i < nAddrListSize; i++) {
			sCurrRecipeValues[i] = bytesTodouble(nTmpRecipeDataList, mRGObj
					.geteDataTypeList().get(i));
			sCurrRecipeValues[i] = subZeroAndDot(sCurrRecipeValues[i]);
		}

		updateCallback(false, false);

		// nTmpRecipeId.setCurrentGroupRecipeId(nGroupId);
		// nTmpRecipeId.setCurrentRecipeId(nRcipeId);
		// EditRecipeInfo mEditInfo = new EditRecipeInfo();
		// mEditInfo.mRecipeInfo = nTmpRecipeId;
		// mEditInfo.mRecipeData = mRGObj.getmRecipePropList().get(nRcipeIndex);
		// editSaveRecipe(mEditInfo);
	}

	/**
	 * 写当前配方到PLC
	 * 
	 * @param nRecipeGroupId
	 * @param nRecipeId
	 */
	private boolean writeRecipeToPlc(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return false;

		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		int nRcipeId = mRecipeInfo.getCurrentRecipeId();

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize)
			return false;

		/* get this recipe group recipe data */
		RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
				.getmRecipeGroupList().get(nGroupId);
		if (null == mTmpRecipeGprop)
			return false;

		/* write data to plc */
		String[] sValueList = getRecipeData(nGroupId, nRcipeId, false);
		if (sValueList == null) {
			return false;
		}

		int nElementSize = sValueList.length;
		int nAddrListSize = mTmpRecipeGprop.getnValueAddrList().size();
		int nEdataTypeSize = mTmpRecipeGprop.geteDataTypeList().size();

		if (nElementSize != nAddrListSize || nElementSize != nEdataTypeSize)
			return false;

		/* 显示进度条 */
		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";
		if (myActivity != null) {
			sMsg = myActivity.getString(R.string.recipe_write);
		}
		SKPlcNoticThread.getInstance().getMainUIHandler()
				.obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg + "...")
				.sendToTarget();

		boolean bContinue = mTmpRecipeGprop.isbContinue();
		if (bContinue) {
			ProtocolInterfaces mProtocolObj = ProtocolInterfaces
					.getProtocolInterface();
			if (null != mProtocolObj
					&& mTmpRecipeGprop.getnValueAddrList().size() > 0) {
				AddrProp mTmpAddr = mTmpRecipeGprop.getnValueAddrList()
						.firstElement();
				if (mTmpAddr == null)
					return false;

				PlcSampInfo mPlcInfo = new PlcSampInfo();
				mPlcInfo.eConnectType = mTmpAddr.eConnectType;
				mPlcInfo.nProtocolIndex = mTmpAddr.nUserPlcId;
				mPlcInfo.sProtocolName = mTmpAddr.sPlcProtocol;

				int nByteLen = 2;
				DATA_TYPE eDataType = DATA_TYPE.INT_16;
				if (!mTmpRecipeGprop.geteDataTypeList().isEmpty()) {
					eDataType = mTmpRecipeGprop.geteDataTypeList().get(0);
				}

				if (eDataType == DATA_TYPE.BIT_1) {
					nByteLen = 1;
				}

				int nSVlen = sValueList.length;
				Vector<Byte> nByteList = new Vector<Byte>();
				for (int i = 0; i < nSVlen; i++) {
					double nDValue = 0;
					if (sValueList[i] != null && sValueList[i] != "") {
						try {
							nDValue = Double.valueOf(sValueList[i]);
						} catch (NumberFormatException e) {
							Log.e("doubleToBytes",
									"string to double error" + e.getMessage());
						}
					}

					switch (eDataType) {
					case BIT_1: {
						byte nTmpValue = (byte) nDValue;
						nByteList.add(nTmpValue);
						nByteList.add((byte) 0);
						break;
					}
					case POSITIVE_INT_16:
					case HEX_16:
					case OTC_16:
					case BCD_16: {
						int nTmpValue = (int) nDValue;
						nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
						break;
					}
					case POSITIVE_INT_32:
					case HEX_32:
					case OTC_32:
					case BCD_32: {
						long nTmpValue = (long) nDValue;
						nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 16) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 24) & 0xff));
						break;
					}
					case INT_16: {
						int nTmpValue = (int) nDValue;
						nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
						break;
					}
					case INT_32: {
						int nTmpValue = (int) nDValue;
						nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 16) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 24) & 0xff));
						break;
					}
					case FLOAT_32: {
						int nTmpValue = Float.floatToIntBits((float) nDValue);
						nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 16) & 0xff));
						nByteList.add((byte) ((nTmpValue >>> 24) & 0xff));

						break;
					}
					case ASCII_STRING: {
						break;
					}
					case OTHER_DATA_TYPE:
					default: {
						break;
					}
					}
				}

				/* 如果地址类型不是主站 */
				PROTOCOL_TYPE ePlcType = mProtocolObj.getProtocolType(mPlcInfo);
				if (ePlcType == PROTOCOL_TYPE.MASTER_MODEL) {
					/* 求最大读写长度 */
					int nConnectSize = SystemInfo.getPlcConnectionList().size();
					PlcConnectionInfo mConnect = null;
					for (int i = 0; i < nConnectSize; i++) {
						if (SystemInfo.getPlcConnectionList().get(i)
								.geteConnectPort() == mPlcInfo.eConnectType) {
							mConnect = SystemInfo.getPlcConnectionList().get(i);
						}
					}

					if (mConnect != null) {
						int nMaxRWlen = -1;
						int nProtocolSize = mConnect.getPlcAttributeList()
								.size();
						for (int i = 0; i < nProtocolSize; i++) {
							if (mConnect.getPlcAttributeList().get(i)
									.getnUserPlcId() == mTmpAddr.nUserPlcId) {
								nMaxRWlen = mConnect.getPlcAttributeList()
										.get(i).getnMaxRWLen();
							}
						}

						if (nMaxRWlen > 0) {
							AddrPropArray nResultAddrList = new AddrPropArray();
							mProtocolObj
									.sortOutAddrList(
											mTmpRecipeGprop.getnValueAddrList(),
											nResultAddrList, mPlcInfo,
											nMaxRWlen, true);

							/* 整理后的地址，重新赋值 */
							if (nResultAddrList.mSortAddrList != null) {
								int nDataPos = 0;
								int nDataSize = nByteList.size();
								int nAddrLen = nResultAddrList.mSortAddrList.length;
								for (int i = 0; i < nAddrLen; i++) {
									nResultAddrList.mSortAddrList[i].eConnectType = (short) mPlcInfo.eConnectType;
									nResultAddrList.mSortAddrList[i].sPlcProtocol = mPlcInfo.sProtocolName;

									/* 生成发送对象 */
									PlcCmnWriteCtlObj mCmnSubProp = new PlcCmnWriteCtlObj();
									mCmnSubProp.Icallback = mPlcCmnCallback;
									mCmnSubProp.mDataObj = mRecipeInfo;

									/* 分配数据 */
									int nResultAddrLen = nResultAddrList.mSortAddrList[i].nAddrLen;
									nResultAddrLen *= nByteLen;
									byte[] nSetByteList = new byte[nResultAddrLen];
									for (int k = 0; k < nResultAddrLen; k++) {
										if (nDataPos < nDataSize) {
											nSetByteList[k] = nByteList
													.get(nDataPos);
										}

										nDataPos++;
									}
									mCmnSubProp.nDataList = nSetByteList;
									mCmnSubProp.mAddrProp = nResultAddrList.mSortAddrList[i];

									if (i != nAddrLen - 1) {
										mCmnSubProp.bCallback = false;
									} else {
										mCmnSubProp.bCallback = true;
									}

									/* 发送消息写 */
									SkGlobalBackThread
											.getInstance()
											.getGlobalBackHandler()
											.obtainMessage(
													MODULE.USER_WRITE_PLC,
													mCmnSubProp).sendToTarget();
								}
							}
						}
					}
					return true;
				}// end master
			}
		}

		/* 按照连接类型把所有地址分类添加到eConnectTypeMap容器中 */
		for (int i = 0; i < nAddrListSize; i++) {
			Vector<Byte> nDataList = null;
			AddrProp mTmpAddr = null;
			mTmpAddr = mTmpRecipeGprop.getnValueAddrList().get(i);
			if (null == mTmpAddr)
				continue;

			short eConnect = mTmpAddr.eConnectType;

			/* 取得连接类型 */
			SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnect);
			if (null == mThreadObj) {
				Log.e("getRegBytesData", "查找连接类型的的接口:" + eConnect
						+ " 失败，连接类型可能不对");
				continue;
			}

			/* 在不同连接类型中，按照连接协议来取得连接协议的对象 */
			PlcCmnWriteCtlObj mCmnSubProp = new PlcCmnWriteCtlObj();
			mCmnSubProp.Icallback = mPlcCmnCallback;
			mCmnSubProp.mDataObj = mRecipeInfo;
			nDataList = new Vector<Byte>();
			nDataList.clear();
			doubleToBytes(nDataList, sValueList[i], mTmpRecipeGprop
					.geteDataTypeList().get(i));

			int nSize = nDataList.size();
			byte[] nSetByteList = new byte[nSize];
			for (int k = 0; k < nSize; k++) {
				nSetByteList[k] = nDataList.get(k);
			}
			mCmnSubProp.nDataList = nSetByteList;
			mCmnSubProp.mAddrProp = mTmpAddr;

			if (i != nAddrListSize - 1) {
				mCmnSubProp.bCallback = false;
			} else {
				mCmnSubProp.bCallback = true;
			}

			SkGlobalBackThread.getInstance().getGlobalBackHandler()
					.obtainMessage(MODULE.USER_WRITE_PLC, mCmnSubProp)
					.sendToTarget();
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
	private boolean readRecipeFromPlc(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return false;

        bSetCurrentRecipe=true;
		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();
		if (mRecipeInfo.getCurrentGroupRecipeId() < 0
				|| mRecipeInfo.getCurrentGroupRecipeId() >= nGroupRecipeSize)
			return false;

		RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
				.getmRecipeGroupList()
				.get(mRecipeInfo.getCurrentGroupRecipeId());
		if (null == mTmpRecipeGprop)
			return false;

		/* 显示进度条 */
		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = " ";
		if (myActivity != null) {
			sMsg = myActivity.getString(R.string.recipe_read);
		}
		SKPlcNoticThread.getInstance().getMainUIHandler()
				.obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg).sendToTarget();

		/* 发送消息 */
		PlcCmnDataCtlObj mTmpCmnCtlObj = new PlcCmnDataCtlObj();
		mTmpCmnCtlObj.Icallback = mPlcCmnCallback;
		mTmpCmnCtlObj.mDataObj = mRecipeInfo;
		mTmpCmnCtlObj.mAddrList = new AddrPropArray();

		int nAddrListSize = mTmpRecipeGprop.getnValueAddrList().size();
		mTmpCmnCtlObj.mAddrList.mSortAddrList = new AddrProp[nAddrListSize];
		for (int i = 0; i < nAddrListSize; i++) {
			mTmpCmnCtlObj.mAddrList.mSortAddrList[i] = mTmpRecipeGprop
					.getnValueAddrList().get(i);
		}
		SkGlobalBackThread.getInstance().getGlobalBackHandler()
				.obtainMessage(MODULE.USER_READ_PLC, mTmpCmnCtlObj)
				.sendToTarget();

		return true;
	}

	/**
	 * 刷新从PLC中读取的配方数据
	 */
	private void refreshReadPlc(CurrentRecipe mCurrRecipe) {
		if (null == mCurrRecipe)
			return;

		int nGroupId = mCurrRecipe.getCurrentGroupRecipeId();
		int nRcipeId = mCurrRecipe.getCurrentRecipeId();

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize)
			return;

		RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
				.getmRecipeGroupList().get(nGroupId);
		if (null == mTmpRecipeGprop)
			return;

		/* read data from plc */
		int nAddrListSize = mTmpRecipeGprop.getnValueAddrList().size();

		boolean bCurrRecipe = false;

		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if (null != nCurrRecipeObj) {
			if (nCurrRecipeObj.getCurrentGroupRecipeId() == nGroupId
					&& nCurrRecipeObj.getCurrentRecipeId() == nRcipeId) {
				if (null == sCurrRecipeValues) {
					sCurrRecipeValues = new String[nAddrListSize];
				}

				if (sCurrRecipeValues.length != nAddrListSize) {
					return;
				}
				bCurrRecipe = true;
			}
		}

		AddrProp addrProp = null;
		boolean bSuccess = false;
		final Vector<Double> nFinalVector = new Vector<Double>();
		for (int i = 0; i < nAddrListSize; i++) {
			addrProp = mTmpRecipeGprop.getnValueAddrList().get(i);
			nTmpDoubleList.clear();

			/* init send data struct */
			if (mTmpRecipeGprop.geteDataTypeList().size() > i) {
				mSendData.eDataType = mTmpRecipeGprop.geteDataTypeList().get(i);
			}
			mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;

			/* get recipe data from plc address */
			bSuccess |= PlcRegCmnStcTools.getRegDoubleData(addrProp,
					nTmpDoubleList, mSendData);
			if (false == bSuccess)
				break;
	
				if (!nTmpDoubleList.isEmpty()) {
					nFinalVector.add(nTmpDoubleList.get(0));
					if (bCurrRecipe) {
						sCurrRecipeValues[i] = String
							.valueOf(nTmpDoubleList.get(0));
						sCurrRecipeValues[i] = subZeroAndDot(sCurrRecipeValues[i]);
						
				}
			}
		}

		/* The transfer is complete and notice */
		transCompNotice(nGroupId);

		/* 弹出提示确认对话框 */
		final int nCurrGId = nGroupId;
		final int nCurrId = nRcipeId;

		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";
		String sMsgY = "yes";
		String sMsgN = "no";
		if (myActivity != null) {
			sMsg = myActivity.getString(R.string.recipe_issave);
			sMsgY = myActivity.getString(R.string.recipe_yes);
			sMsgN = myActivity.getString(R.string.recipe_no);
		}

		new AlertDialog.Builder(SKSceneManage.getInstance().getActivity())
				.setMessage(sMsg)
				.setPositiveButton(sMsgY,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int j) {
								/* save data to database */
								SKSceneManage.getInstance().time = 0;
								int nDataSize = nFinalVector.size();
								String[] sValueList = new String[nDataSize];
								for (int i = 0; i < nDataSize; i++) {
									sValueList[i] = nFinalVector.get(i) + "";
								}
								setRecipeData(nCurrGId, nCurrId, sValueList);
							}
						}).setNegativeButton(sMsgN, null).show();

		/* 配方改变需要通知 */
		updateCallback(false, true);
	}

	/* 读写回调 */
	SKCommThread.ICmnCompletedCallback mPlcCmnCallback = new SKCommThread.ICmnCompletedCallback() {

		@Override
		public void cmnWriteCompleted(boolean bSuccess, String sErrorInfo,
				Object mObjMsg) {

			Activity myActivity = SKSceneManage.getInstance().getActivity();
			String sMsg1 = " ";
			String sMsg2 = " ";
			if (myActivity != null) {
				sMsg1 = myActivity.getString(R.string.recipe_write_sucess);
				sMsg2 = myActivity.getString(R.string.recipe_write_fail);
			}

			/* 通知回来, 如果失败则不修改 */
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			if (bSuccess) {
				sErrorInfo = sMsg1 + sErrorInfo;
			} else {
				sErrorInfo = sMsg2 + sErrorInfo;
			}
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sErrorInfo)
					.sendToTarget();
			if (!bSuccess)
				return;

			/* The transfer is complete and notice */
			transCompNotice(((CurrentRecipe) mObjMsg).getCurrentGroupRecipeId());
		}

		@Override
		public void cmnReadCompleted(boolean bSuccess, String sErrorInfo,
				Object mObjMsg) {
			/* 通知回来, 如果失败则不修改 */
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			// if(bSuccess)
			// {
			// sErrorInfo = " 读配方成功\n read recipe success \n info:" +
			// sErrorInfo;
			// }
			// else
			// {
			// sErrorInfo = " 读配方失败\n read recipe failed \n info:" + sErrorInfo;
			// }
			// SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST,
			// sErrorInfo).sendToTarget();
			if (!bSuccess)
				return;

			/* 发送消息刷新从PLC中读取的配方 */
			SKThread.getInstance()
					.getBinder()
					.onTask(MODULE.CALLBACK, MODULE.REFRESH_READ_RECIPE,
							mObjMsg, mCtlDatabaseCback, 0);
		}
	};

	/**
	 * 从文件中读配方
	 * 
	 * @param nRecipeGroupId
	 * @return
	 */
	private boolean readRecipeGroupFromFile(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo)
			return false;

		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();
		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize)
			return false;

		/* get this recipe group recipe data */
		RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
				.getmRecipeGroupList().get(nGroupId);
		if (null == mTmpRecipeGprop)
			return false;

		STORAGE_MEDIA eSaveMedia = mTmpRecipeGprop.geteSaveMedia();

		// String sFilePath = "recipe.csv";
		String sFilePath = mTmpRecipeGprop.getsRecipeGName() + ".csv";
		if (!TextUtils.isEmpty(mTmpRecipeGprop.getmCopyRecipeName())) {// 不为空，使用功能开关的复制功能
			sFilePath = mTmpRecipeGprop.getmCopyRecipeName() + ".csv";
			mTmpRecipeGprop.setmCopyRecipeName(null);
		}
		String sShowPath = "";
		switch (eSaveMedia) {
		case INSIDE_DISH: {
			sFilePath = "/data/data/com.android.Samkoonhmi/formula/recipe/"
					+ sFilePath;
			sShowPath = sFilePath;
			break;
		}
		case U_DISH: {
			sShowPath = "U盘/" + sFilePath;
			sFilePath = "/mnt/usb2/" + sFilePath;
			break;
		}
		case SD_DISH: {
			sShowPath = "sdcard/" + sFilePath;
			sFilePath = "/mnt/sdcard/" + sFilePath;
			break;
		}
		default: {
			return false;
		}
		}

		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";

		/* 如果配方文件不存在则创建文件 */
		// try {
		// sFilePath = new String (sFilePath.getBytes(), "GBK");
		// } catch (UnsupportedEncodingException e1) {
		// e1.printStackTrace();
		// }
		File mCollectFile = new File(sFilePath);

		if (!mCollectFile.exists()) {
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.recipe_path_noexist);
			}

			SKPlcNoticThread.getInstance().getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg + sShowPath)
					.sendToTarget();
			return false;
		}

		/* 显示进度条 */
		if (myActivity != null) {
			sMsg = myActivity.getString(R.string.recipe_import);
		}

		SKPlcNoticThread
				.getInstance()
				.getMainUIHandler()
				.obtainMessage(MODULE.NOTIC_SHOW_PRESS, sMsg + nGroupId + "...")
				.sendToTarget();

		try {
			/* new read file hand */
			// FileReader fileReaderHand = new FileReader(sFilePath);
			// BufferedReader readBuffer = new BufferedReader(fileReaderHand);
			DataInputStream fileReaderHand = new DataInputStream(
					new FileInputStream(mCollectFile));
			BufferedReader readBuffer = new BufferedReader(
					new InputStreamReader(fileReaderHand, "GBK"));
			String sTmpStr;

			// 读出数据lvdalong
			String[] sHeadBuf = null;
			String[] sBodyBuf = null;
			ArrayList<String> elementBuf = new ArrayList<String>();
			// head
			sTmpStr = readBuffer.readLine();
			sHeadBuf = sTmpStr.split(",");

			// 读取body
			final Vector<String[]> sBodyValues = new Vector<String[]>();
			sBodyValues.clear();
			while ((sTmpStr = readBuffer.readLine()) != null) {
				sBodyBuf = sTmpStr.split(",");
				if (sBodyBuf.length != sHeadBuf.length) {
					if (myActivity != null) {
						sMsg = myActivity
								.getString(R.string.recipe_import_fail);
					}
					readBuffer.close();
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
							.sendToTarget();
					return false;
				}

				// element name
				elementBuf.add(sBodyBuf[0]);

				// //recipe num
				// int recipeNum = sBodyBuf.length - 1;// -element name
				// if (recipeNum != mTmpRecipeGprop.getnRecipeNum()) {// 配方个数
				// 不相等， 停止拷贝
				// if (myActivity != null) {
				// sMsg = myActivity
				// .getString(R.string.recipe_import_fail);
				// }
				// readBuffer.close();
				// SKPlcNoticThread.getInstance().getMainUIHandler()
				// .sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
				// SKPlcNoticThread.getInstance().getMainUIHandler()
				// .obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
				// .sendToTarget();
				// return false;
				// }

				// body
				String[] beanBuf = new String[sBodyBuf.length - 1];
				for (int i = 1; i < sBodyBuf.length; i++) {
					beanBuf[i - 1] = sBodyBuf[i];
				}
				sBodyValues.add(beanBuf);
			}

			// 进行映射新的表头
			mTmpRecipeGprop.getsElemNameList().clear();
			for (int i = 0; i < elementBuf.size(); i++) {
				Vector<String> sElemList = new Vector<String>();
				sElemList.add(elementBuf.get(i));
				mTmpRecipeGprop.getsElemNameList().add(sElemList);
			}

			// 转化为配方lvdalong
			RecipeOprop rcpProp = null;
			mTmpRecipeGprop.getmRecipePropList().clear();
			final Vector<String[]> sGroupValues = new Vector<String[]>();
			sGroupValues.clear();
			for (int i = 0; i < sHeadBuf.length - 1; i++) {
				rcpProp = new RecipeOprop();
				// recipe name
				Vector<String> sNameList = new Vector<String>();
				sNameList.add(sHeadBuf[i + 1]);
				rcpProp.setsRecipeName(sNameList);
				// recipe id
				rcpProp.setnRecipeId(i);
				/* save recipe Description */
				Vector<String> sDescriList = new Vector<String>();
				sDescriList.add(" ");
				rcpProp.setsRecipeDescri(sDescriList);

				/* save recipe value list */
				String[] sValueList = new String[sBodyValues.size()];
				for (int j = 0; j < sBodyValues.size(); j++) {
					String[] temp = sBodyValues.elementAt(j);
					sValueList[j] = temp[i].trim();
				}

				// 进行检查数据读取的数据是否合法
				if (!isValidData(nGroupId, sValueList)) { // 如果输入的数据不合法， 那么就退出

					if (myActivity != null) {
						sMsg = myActivity
								.getString(R.string.recipe_import_error);
					}
					readBuffer.close();
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
					SKPlcNoticThread.getInstance().getMainUIHandler()
							.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
							.sendToTarget();
					return false;
				}

				sGroupValues.add(sValueList);
				mTmpRecipeGprop.getmRecipePropList().add(rcpProp);
			}

			// /*check new address length is equal old address length*/
			// int nBufLen = sTmpBuf.length;
			// if(nBufLen != (mTmpRecipeGprop.getnRecipeLen() + 3))
			// {
			// if(myActivity != null)
			// {
			// sMsg = myActivity.getString(R.string.recipe_import_fail);
			// }
			// readBuffer.close();
			// SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			// SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST,
			// sMsg).sendToTarget();
			// return false;
			// }
			//
			// mTmpRecipeGprop.getsElemNameList().clear();
			//
			// for(int i = 3; i < nBufLen; i++)
			// {
			// Vector<String > sElemList = new Vector<String >();
			// sElemList.add(sTmpBuf[i]);
			// mTmpRecipeGprop.getsElemNameList().add(sElemList);
			// }
			//
			// /*save csv format data to Memory*/
			// RecipeOprop rcpProp = null;
			// mTmpRecipeGprop.getmRecipePropList().clear();
			// final Vector<String[] > sGroupValues = new Vector<String[] >();
			// sGroupValues.clear();
			// while((sTmpStr = readBuffer.readLine()) != null)
			// {
			// sTmpBuf = sTmpStr.split(",");
			// nBufLen = sTmpBuf.length;
			// if(nBufLen != (mTmpRecipeGprop.getnRecipeLen() + 3) || nBufLen <=
			// 3)
			// {
			// if(myActivity != null)
			// {
			// sMsg = myActivity.getString(R.string.recipe_len_error);
			// }
			// readBuffer.close();
			// SKPlcNoticThread.getInstance().getMainUIHandler().sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			// SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST,
			// sMsg).sendToTarget();
			// return false;
			// }
			//
			// rcpProp = new RecipeOprop();
			//
			// //去除
			// /*save recipe name*/
			// Vector<String > sNameList = new Vector<String >();
			// sNameList.add(sTmpBuf[0]);
			// rcpProp.setsRecipeName(sNameList);
			//
			// /*save recipe id*/
			// rcpProp.setnRecipeId(Integer.parseInt(sTmpBuf[1]));
			//
			// /*save recipe Description*/
			// Vector<String > sDescriList = new Vector<String >();
			// sDescriList.add(sTmpBuf[2]);
			// rcpProp.setsRecipeDescri(sDescriList);
			//
			// //关键
			// /*save recipe value list*/
			// String[] sValueList = new String[nBufLen -3];
			// for(int i = 3; i < nBufLen; i++)
			// {
			// sValueList[i - 3] = sTmpBuf[i];
			// }
			//
			// sGroupValues.add(sValueList);
			// mTmpRecipeGprop.getmRecipePropList().add(rcpProp);
			// }
			//

			/* save data to recipe group list */
			RecipeDataProp.getInstance().getmRecipeGroupList()
					.set(nGroupId, mTmpRecipeGprop);

			/* close file */
			readBuffer.close();

			/* 弹出提示确认对话框 */
			String sMsgY = "";
			String sMsgN = "";
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.recipe_cover);
				sMsgY = myActivity.getString(R.string.recipe_yes);
				sMsgN = myActivity.getString(R.string.recipe_no);
			}

			final int nFinalGroupId = nGroupId;
			final RecipeOGprop mSaveGRecipe = mTmpRecipeGprop.copyGroupRecipe();
			new AlertDialog.Builder(SKSceneManage.getInstance().getActivity())
					.setMessage(sMsg)
					.setPositiveButton(sMsgY,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int k) {
									SKSceneManage.getInstance().time = 0;
									/* save data to database */
									SKDataBaseInterface mSaveObj = SkGlobalData
											.getRecipeDatabase();
									mSaveObj.beginTransaction();

									/* 插入数据到recipeNameML表 */
									String sSqlStr = "";
									int nRecipeSize = mSaveGRecipe
											.getmRecipePropList().size();
									int nCurrLanguaId = SystemInfo
											.getCurrentLanguageId();
									try {

										sSqlStr = "delete from recipeNameML where nGroupId = "
												+ nFinalGroupId;
										mSaveObj.execSql(sSqlStr);

										for (int i = 0; i < nRecipeSize; i++) {
											m_tmpValues.clear();
											int nRecipeId = mSaveGRecipe
													.getmRecipePropList()
													.get(i).getnRecipeId();
											int nLanguaSize = mSaveGRecipe
													.getmRecipePropList()
													.get(i).getsRecipeName()
													.size();
											String sRecipeName = "";
											String sDescri = "";
											if (nCurrLanguaId < nLanguaSize) {
												sRecipeName = mSaveGRecipe
														.getmRecipePropList()
														.get(i)
														.getsRecipeName()
														.get(nCurrLanguaId);
											}

											if (nCurrLanguaId < mSaveGRecipe
													.getmRecipePropList()
													.get(i).getsRecipeDescri()
													.size()) {
												sDescri = mSaveGRecipe
														.getmRecipePropList()
														.get(i)
														.getsRecipeDescri()
														.get(nCurrLanguaId);
											}

											/* 保存配方ID、配方名称和描述 */
											// sSqlStr =
											// "update recipeNameML set sRecipeName = '"
											// + sRecipeName +
											// "',  nRecipeId = " + nRecipeId +
											// ", sRecipeDescri = '" + sDescri +
											// "' where  nGroupId = " +
											// nFinalGroupId +
											// " and  nRecipeId=" + nRecipeId +
											// " and  nLanguageId=" +
											// nCurrLanguaId;

											m_tmpValues.clear();
											m_tmpValues.put("nGroupId",
													nFinalGroupId);
											m_tmpValues.put("sRecipeName",
													sRecipeName);
											m_tmpValues.put("nRecipeId",
													nRecipeId);
											m_tmpValues.put("sRecipeDescri",
													sDescri);
											m_tmpValues.put("nLanguageId",
													nCurrLanguaId);
											mSaveObj.insertData("recipeNameML",
													m_tmpValues);

											if (i >= sGroupValues.size())
												continue;

											// /*保存配方数据*/
											setRecipeData(nFinalGroupId,
													nRecipeId,
													sGroupValues.get(i));
										}
										mSaveObj.commitTransaction();

										/* 配方改变需要通知 */
										updateCallback(false, true);
									} finally {
										mSaveObj.endTransaction();
									}
								}
							})
					.setNegativeButton(sMsgN,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									SKSceneManage.getInstance().time = 0;
									/* 配方改变需要通知 */
									updateCallback(false, true);
								}
							}).show();

			/* release source */
			readBuffer.close();
			fileReaderHand.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.recipe_import_fail);
			}
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
					.sendToTarget();
			return false;
		}

		if (myActivity != null) {
			sMsg = myActivity.getString(R.string.recipe_success);
		}

		SKPlcNoticThread.getInstance().getMainUIHandler()
				.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
		SKPlcNoticThread.getInstance().getMainUIHandler()
				.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg).sendToTarget();
		return true;
	}

	/**
	 * 写配方到文件中
	 * 
	 * @param nRecipeGroupId
	 * @return
	 */
	private boolean writeRecipeGroupToFile(CurrentRecipe mRecipeInfo) {

		if (null == mRecipeInfo)
			return false;

		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();
		if (nGroupId < 0 || nGroupId >= nGroupRecipeSize)
			return false;

		RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
				.getmRecipeGroupList().get(nGroupId);
		writeRecipeGroupToFile(mTmpRecipeGprop, true);

		return true;
	}

	/**
	 * 将配方写入文件
	 */
	private boolean writeRecipeGroupToFile(RecipeOGprop mTmpRecipeGprop,
			boolean showToast) {

		/* get this recipe group recipe data */
		if (null == mTmpRecipeGprop)
			return false;

		STORAGE_MEDIA eSaveMedia = mTmpRecipeGprop.geteSaveMedia();
		String sFilePath = mTmpRecipeGprop.getsRecipeGName() + ".csv";
		String sFilePath1 = "recipe_" + mTmpRecipeGprop.getnGRecipeID()
				+ ".csv";
		String sShowPath = "";

		switch (eSaveMedia) {
		case INSIDE_DISH: {
			sFilePath = "/data/data/com.android.Samkoonhmi/formula/recipe/"
					+ sFilePath;
			sShowPath = sFilePath;
			break;
		}
		case U_DISH: {
			sShowPath = "U盘/" + sFilePath;
			sFilePath = "/mnt/usb2/" + sFilePath;
			break;
		}
		case SD_DISH: {
			sShowPath = "sdcard/" + sFilePath;
			sFilePath = "/mnt/sdcard/" + sFilePath;
			break;
		}
		case OTHER_STORAGE_MEDIA: {
			// email 发送邮件
			sFilePath = "/data/data/com.android.Samkoonhmi/files/email_files/"
					+ sFilePath1;
			sShowPath = sFilePath;
			break;
		}
		default: {
			return false;
		}
		}

		/* 如果配方文件不存在则创建文件 */
		// try {
		// sFilePath = URLDecoder.decode(sFilePath, "UTF-8");
		// } catch (UnsupportedEncodingException e1) {
		// e1.printStackTrace();
		// }
		File mCollectFile = new File(sFilePath);
		File mParantFile = mCollectFile.getParentFile();
		if (null == mParantFile)
			return false;

		if (!mParantFile.exists()) {
			mParantFile.mkdirs();
		}

		Activity myActivity = SKSceneManage.getInstance().getActivity();
		String sMsg = "";

		if (mCollectFile.exists()) {
			boolean bDeleteOk = mCollectFile.delete();
			if (!bDeleteOk) {
				if (myActivity != null) {
					sMsg = myActivity.getString(R.string.recipe_file_used);
				}

				SKPlcNoticThread
						.getInstance()
						.getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_TOAST,
								sMsg + mTmpRecipeGprop.getnGRecipeID())
						.sendToTarget();
				return false;
			}
		}

		try {
			if (!mCollectFile.createNewFile())
				return false;
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* 显示进度条 */
		if (showToast) {
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.recipe_writing_group);
				SKPlcNoticThread
						.getInstance()
						.getMainUIHandler()
						.obtainMessage(MODULE.NOTIC_SHOW_PRESS,
								sMsg + mTmpRecipeGprop.getnGRecipeID())
						.sendToTarget();
			}

		}
		try {
			/* new write file hand */
			// FileWriter fileWriteHand = new FileWriter(sFilePath, false);
			// BufferedWriter writeBuffer = new BufferedWriter(fileWriteHand);

			DataOutputStream fileWriteHand = new DataOutputStream(
					new FileOutputStream(mCollectFile));
			BufferedWriter writeBuffer = new BufferedWriter(
					new OutputStreamWriter(fileWriteHand, "GBK"));

			// writeBuffer.newLine();

			int nRecipeNum = mTmpRecipeGprop.getnRecipeNum();
			RecipeOprop rcpProp = null;
			ArrayList<String[]> recipdeDate = getRecipeGroup(
					mTmpRecipeGprop.getnGRecipeID(), 1, nRecipeNum);//

			int nCurrLanguage = SystemInfo.getCurrentLanguageId();
			String sTitle = "recipe name,";
			if (myActivity != null) {
				sTitle = myActivity.getString(R.string.recipe_element_name);
			}

			// 表头
			for (int i = 0; i < nRecipeNum; i++) {
				/* 添加配方名称 */
				rcpProp = mTmpRecipeGprop.getmRecipePropList().get(i);
				if (nCurrLanguage >= 0
						&& nCurrLanguage < rcpProp.getsRecipeName().size()) {
					sTitle += rcpProp.getsRecipeName().get(nCurrLanguage) + ",";
				} else {
					sTitle += " ,";
				}

			}
			// 去除最后的逗号
			sTitle = sTitle.substring(0, sTitle.length() - 1);
			writeBuffer.write(sTitle);

			// 表中数据
			int nElemSize = mTmpRecipeGprop.getsElemNameList().size();
			for (int i = 0; i < nElemSize; i++) {
				writeBuffer.newLine();
				String sTmpWrite = "";

				// 元素名称
				if (nCurrLanguage >= 0
						&& nCurrLanguage < mTmpRecipeGprop.getsElemNameList()
								.get(i).size()) {
					sTmpWrite += mTmpRecipeGprop.getsElemNameList().get(i)
							.get(nCurrLanguage)
							+ ",";
				} else {
					sTmpWrite += " ,";
				}

				/* 添加配方值 */
				for (int j = 0; j < recipdeDate.size(); j++) {
					String bean[] = recipdeDate.get(j);
					if (bean != null) {
						if (i < bean.length) {
							sTmpWrite += bean[i] + ",";
						} else {
							sTmpWrite += 0 + ",";
						}
					}
				}

				// 去除最后的逗号
				sTmpWrite = sTmpWrite.substring(0, sTmpWrite.length() - 1);
				writeBuffer.write(sTmpWrite);
			}

			// int nCurrLanguage = SystemInfo.getCurrentLanguageId();
			// String sTitle = "recipe name" + "," + "recipe ID" + "," +
			// "recipe disc";
			// if(myActivity != null)
			// {
			// sTitle = myActivity.getString(R.string.recipe_Title);
			// }
			//
			// int nElemSize = mTmpRecipeGprop.getsElemNameList().size();
			// for(int i = 0; i < nElemSize; i++)
			// {
			// sTitle += ",";
			// if(nCurrLanguage >= 0 && nCurrLanguage <
			// mTmpRecipeGprop.getsElemNameList().get(i).size())
			// {
			// sTitle +=
			// mTmpRecipeGprop.getsElemNameList().get(i).get(nCurrLanguage);
			// }
			// else
			// {
			// sTitle += " ";
			// }
			// }
			// writeBuffer.write(sTitle);
			//
			// int nRecipeNum = mTmpRecipeGprop.getnRecipeNum();
			// RecipeOprop rcpProp = null;
			//
			// for(int i = 0; i < nRecipeNum; i++)
			// {
			// rcpProp = mTmpRecipeGprop.getmRecipePropList().get(i);
			// writeBuffer.newLine();
			//
			// /*添加配方名称*/
			// String sTmpWrite = "";
			// if(nCurrLanguage >= 0 && nCurrLanguage <
			// rcpProp.getsRecipeName().size())
			// {
			// sTmpWrite += rcpProp.getsRecipeName().get(nCurrLanguage) + "," ;
			// }
			// else
			// {
			// sTmpWrite += " ,";
			// }
			//
			// /*添加配方ID*/
			// sTmpWrite += String.valueOf(rcpProp.getnRecipeId()) + ",";
			//
			// /*添加配方描述*/
			// if(nCurrLanguage >= 0 && nCurrLanguage <
			// rcpProp.getsRecipeDescri().size())
			// {
			// sTmpWrite += rcpProp.getsRecipeDescri().get(nCurrLanguage);
			// }
			// else
			// {
			// sTmpWrite += " ";
			// }
			//
			// String[] sValueList = getRecipeData(nGroupId,
			// rcpProp.getnRecipeId(), false);
			// if(sValueList == null)
			// {
			// return false;
			// }
			//
			// /*添加配方值*/
			// int nLen = sValueList.length;
			// for(int j = 0; j < nLen; j++)
			// {
			// sTmpWrite += ",";
			// sTmpWrite += sValueList[j];
			// }
			//
			// Log.d("RecipData",
			// "sTmpWrite:"+sTmpWrite+",nRecipeNum:"+nRecipeNum);
			// writeBuffer.write(sTmpWrite);
			// }

			/* close file */
			writeBuffer.flush();
			writeBuffer.close();
			fileWriteHand.close();

		} catch (Exception e) {
			e.printStackTrace();
			if (myActivity != null) {
				sMsg = myActivity.getString(R.string.recipe_fail_nomedium);
			}
			if (showToast) {
				SKPlcNoticThread.getInstance().getMainUIHandler()
						.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			}

			SKPlcNoticThread.getInstance().getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg)
					.sendToTarget();
			return false;
		}

		if (myActivity != null) {
			sMsg = myActivity.getString(R.string.save_success);
		}

		/* 休眠2秒钟，等文件写入成功 */
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (showToast) {
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.sendEmptyMessage(MODULE.NOTIC_HIDE_PRESS);
			SKPlcNoticThread.getInstance().getMainUIHandler()
					.obtainMessage(MODULE.NOTIC_SHOW_TOAST, sMsg + sShowPath)
					.sendToTarget();
		}

		return true;

	}

	/**
	 * 传输完成 通知
	 * 
	 * @param nRecipeGroupId
	 * @return
	 */
	private boolean transCompNotice(int nRecipeGroupId) {
		/* get recipe group size */
		int nGroupRecipeSize = RecipeDataProp.getInstance()
				.getmRecipeGroupList().size();
		if (nRecipeGroupId < 0 || nRecipeGroupId >= nGroupRecipeSize)
			return false;

		/* get this recipe group recipe data */
		RecipeOGprop mTmpRecipeGprop = RecipeDataProp.getInstance()
				.getmRecipeGroupList().get(nRecipeGroupId);
		if (null == mTmpRecipeGprop)
			return false;

		if (!mTmpRecipeGprop.isbCompleteNotic())
			return false;

		/* notice data to plc */
		tmpDataList.clear();
		tmpDataList.add(1);

		/* init send data struct */
		mSendData.eDataType = DATA_TYPE.BIT_1;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;

		boolean bSuccess = PlcRegCmnStcTools.setRegIntData(
				mTmpRecipeGprop.getmComNoticAddr(), tmpDataList, mSendData);
		return bSuccess;
	}

	/**
	 * 删除配方
	 * 
	 * @param mRecipeInfo
	 * @return
	 */
	private boolean deleteRecipe(CurrentRecipe mRecipeInfo) {
		if (null == mRecipeInfo || null == RecipeDataProp.getInstance())
			return false;

		/* 判断要删除的配方是否存在 */
		int nGroupId = mRecipeInfo.getCurrentGroupRecipeId();
		int nRecipeId = mRecipeInfo.getCurrentRecipeId();
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList()
				.size();

		int nGIndex = -1;
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i)
					.getnGRecipeID() == nGroupId) {
				nGIndex = i;
				break;
			}
		}

		/* 没找到，则返回false */
		if (nGIndex == -1) {
			return false;
		}

		/* 删除配方 */
		int nIndex = -1;
		int nSize = RecipeDataProp.getInstance().getmRecipeGroupList()
				.get(nGIndex).getmRecipePropList().size();
		for (int i = 0; i < nSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex)
					.getmRecipePropList().get(i).getnRecipeId() == nRecipeId) {
				nIndex = i;
				break;
			}
		}

		/* 没找到，则返回false */
		if (nIndex == -1) {
			return false;
		}

		/* 删除配方 */
		RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex)
				.getmRecipePropList().remove(nIndex);

		/* 如果是当前配方则更新当前配方 */
		CurrentRecipe mCurrRecipe = getCurrRecipe();
		if (null != mCurrRecipe) {
			if (mCurrRecipe.getCurrentGroupRecipeId() == nGroupId
					&& mCurrRecipe.getCurrentRecipeId() == nRecipeId) {
				sCurrRecipeValues = null;
			}
		}

		/* 配方数减1 */
		int nRecipeNum = RecipeDataProp.getInstance().getmRecipeGroupList()
				.get(nGIndex).getnRecipeNum();
		RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex)
				.setnRecipeNum(nRecipeNum - 1);

		/* 删除数据库 */
		SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
		if (mSaveObj != null) {
			mSaveObj.beginTransaction();

			/* 删除组的配方 */
			mSaveObj.execSql("delete from recipegroup" + nGroupId
					+ " where nRecipeId = " + nRecipeId);

			/* 删除组的配方 */
			mSaveObj.execSql("delete from recipeNameML where nGroupId = "
					+ nGroupId + " and nRecipeId = " + nRecipeId);

			mSaveObj.commitTransaction();
			mSaveObj.endTransaction();
		}

		/* 配方改变需要通知 */
		updateCallback(false, true);
		return true;
	}

	/**
	 * 删除配方组
	 * 
	 * @param nRecipeGroupId
	 *            :配方组号
	 * @return
	 */
	private boolean deleteRecipeGroup(int nRecipeGroupId) {
		if (null == RecipeDataProp.getInstance())
			return false;

		/* 判断要删除的配方是否存在 */
		int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList()
				.size();

		int nGIndex = -1;
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i)
					.getnGRecipeID() == nRecipeGroupId) {
				nGIndex = i;
				break;
			}
		}

		/* 没找到，则返回false */
		if (nGIndex == -1) {
			return false;
		}

		// RecipeDataProp.getInstance().getmRecipeGroupList().remove(nGIndex);
		RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex)
				.getmRecipePropList().clear();

		/* 如果是当前配方则更新当前配方 */
		CurrentRecipe mCurrRecipe = getCurrRecipe();
		if (null != mCurrRecipe) {
			if (mCurrRecipe.getCurrentGroupRecipeId() == nRecipeGroupId) {
				sCurrRecipeValues = null;
			}
		}

		/* 删除数据库 */
		SKDataBaseInterface mSaveObj = SkGlobalData.getRecipeDatabase();
		if (mSaveObj != null) {
			mSaveObj.beginTransaction();

			/* 删除组的配方名称 */
			mSaveObj.execSql("delete from recipeNameML where nGroupId = "
					+ nRecipeGroupId);

			/* 删除组的配方 */
			mSaveObj.execSql("delete from recipegroup" + nRecipeGroupId);

			// /*删除组的属性*/
			// mSaveObj.execSql("delete from recipeCollectGroup where nGroupId = "
			// + nRecipeGroupId);
			//
			//
			// /*删除组的配方地址和属性*/
			// mSaveObj.execSql("delete from recipeElemML where nGroupId = " +
			// nRecipeGroupId);
			//
			// /*从地址表删除地址*/
			// mSaveObj.execSql("delete from addr where eItemType = 5 and nItemId = "
			// + nRecipeGroupId);

			mSaveObj.commitTransaction();
			mSaveObj.endTransaction();
		}

		/* 配方改变需要通知 */
		updateCallback(false, true);
		return true;
	}

	/**
	 * 新建和修改后保存配方，如果是新建，则配方号为-1
	 * 
	 * @param mRecipeInfo
	 * @return
	 */
	private synchronized boolean editSaveRecipe(EditRecipeInfo mEditRecipeInfo) {
		if (null == mEditRecipeInfo || null == mEditRecipeInfo.mRecipeInfo
				|| null == mEditRecipeInfo.mRecipeData
				|| null == RecipeDataProp.getInstance())
			return false;

		SKDataBaseInterface mSaveObj = null;
		try {

			/* 判断要删除的配方是否存在 */
			int nGroupId = mEditRecipeInfo.mRecipeInfo
					.getCurrentGroupRecipeId();
			int nRecipeId = mEditRecipeInfo.mRecipeInfo.getCurrentRecipeId();
			int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList()
					.size();

			int nGIndex = -1;
			for (int i = 0; i < nGroupSize; i++) {
				if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i)
						.getnGRecipeID() == nGroupId) {
					nGIndex = i;
					break;
				}
			}

			/* 没找到，则返回false */
			if (nGIndex == -1) {
				return false;
			}

			RecipeOGprop mGrecipe = getOGRecipeData(nGroupId);
			int nElemSize = mGrecipe.getsElemNameList().size();

			if (mEditRecipeInfo.sValueList == null
					|| mEditRecipeInfo.sValueList.length != nElemSize) {
				if (nRecipeId < 0) {
					Log.e("RecipeDataCentre",
							"add new recipe error, because recipe length error");
				} else {
					Log.e("RecipeDataCentre",
							"edit recipe error, because recipe length error");
				}
				return false;
			}

			/* 如果是当前配方则更新当前配方 */
			CurrentRecipe mCurrRecipe = getCurrRecipe();
			if (null != mCurrRecipe) {
				if (mCurrRecipe.getCurrentGroupRecipeId() == nGroupId
						&& mCurrRecipe.getCurrentRecipeId() == nRecipeId) {
					sCurrRecipeValues = null;
				}
			}

			/* 获得配方数量 */
			int nRecipeNum = RecipeDataProp.getInstance().getmRecipeGroupList()
					.get(nGIndex).getmRecipePropList().size();
			if (nRecipeId < 0) {
				/* 新增配方 */
				RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex)
						.getmRecipePropList().add(mEditRecipeInfo.mRecipeData);
				RecipeDataProp.getInstance().getmRecipeGroupList().get(nGIndex)
						.setnRecipeNum(nRecipeNum + 1);

				/* 添加数据到数据库 */
				mSaveObj = SkGlobalData.getRecipeDatabase();
				if (mSaveObj != null) {
					//mSaveObj.beginTransaction();

					/* 插入数据到recipeNameML表 */
					m_tmpValues.clear();
					m_tmpValues.put("nGroupId", nGroupId);
					m_tmpValues.put("nRecipeId",
							mEditRecipeInfo.mRecipeData.getnRecipeId());
					int nLanguaSize = mEditRecipeInfo.mRecipeData
							.getsRecipeName().size();
					for (int i = 0; i < nLanguaSize; i++) {
						m_tmpValues.put("nLanguageId", i);
						m_tmpValues.put("sRecipeName",
								mEditRecipeInfo.mRecipeData.getsRecipeName()
										.get(i));
						if (i < mEditRecipeInfo.mRecipeData.getsRecipeDescri()
								.size()) {
							m_tmpValues.put("sRecipeDescri",
									mEditRecipeInfo.mRecipeData
											.getsRecipeDescri().get(i));
						}
						mSaveObj.insertData("recipeNameML", m_tmpValues);
					}

					/* 插入数据到recipegroup表 */
					m_tmpValues.clear();
					m_tmpValues.put("nRecipeId",
							mEditRecipeInfo.mRecipeData.getnRecipeId());
					if (mEditRecipeInfo.sValueList != null) {
						String sValues = "";
						int nLen = mEditRecipeInfo.sValueList.length;
						for (int i = 0; i < nLen - 1; i++) {
							sValues += subZeroAndDot(mEditRecipeInfo.sValueList[i])
									+ ",";
						}
						if (nLen > 0) {
							sValues += subZeroAndDot(mEditRecipeInfo.sValueList[nLen - 1]);
						}

						m_tmpValues.put("elems", sValues);
					}
					mSaveObj.insertData("recipegroup" + nGroupId, m_tmpValues);

					//mSaveObj.commitTransaction();
					//mSaveObj.endTransaction();
				}

				/* 配方改变需要通知 */
				updateCallback(false, true);
			} else {
				/* 修改数据到数据库 */
				mSaveObj = SkGlobalData.getRecipeDatabase();
				if (mSaveObj != null) {
					//mSaveObj.beginTransaction();

					int nCurrLanguaId = SystemInfo.getCurrentLanguageId();

					/* 插入数据到recipeNameML表 */
					String sSqlStr = "";
					int nLanguaSize = mEditRecipeInfo.mRecipeData
							.getsRecipeName().size();
					if (nCurrLanguaId < nLanguaSize) {
						String sDescri = "";
						if (nCurrLanguaId < mEditRecipeInfo.mRecipeData
								.getsRecipeDescri().size()) {
							sDescri = mEditRecipeInfo.mRecipeData
									.getsRecipeDescri().get(nCurrLanguaId);
						}

						sSqlStr = "update recipeNameML set sRecipeName = '"
								+ mEditRecipeInfo.mRecipeData.getsRecipeName()
										.get(nCurrLanguaId)
								+ "',  nRecipeId = "
								+ mEditRecipeInfo.mRecipeData.getnRecipeId()
								+ ", sRecipeDescri = '" + sDescri
								+ "' where  nGroupId = " + nGroupId
								+ " and  nRecipeId=" + nRecipeId
								+ " and  nLanguageId=" + nCurrLanguaId;

						mSaveObj.execSql(sSqlStr);
					}

					/* 插入数据到recipegroup表 */
					setRecipeData(nGroupId, nRecipeId,
							mEditRecipeInfo.sValueList);

					//mSaveObj.commitTransaction();
					//mSaveObj.endTransaction();
				}

				/* 配方改变需要通知 */
				updateCallback(false, true);
				// SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK,
				// MODULE.REFRESH_CURR_RECIPE, (Object)false, mCtlDatabaseCback,
				// 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
			//mSaveObj.commitTransaction();
			//mSaveObj.endTransaction();
		}

		return true;
	}

	/**
	 * 配方回调接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface IRecipeCallBack {

		/* 更新通知 */
		public void update();

		/* 当前配方改变 */
		public void currRecipeUpdate();
	}

	/**
	 * 添加注册接口
	 * 
	 * @param mCallback
	 */
	private void registerUpdate(IRecipeCallBack mCallback) {
		if (null == mCallback)
			return;

		/* 回调 */
		mCallback.update();

		/* 添加回调接口 */
		if (mCallbackList != null) {
			if (!mCallbackList.contains(mCallback)) {
				mCallbackList.add(mCallback);
			}
		}
	}

	/**
	 * 烧毁回调
	 * 
	 * @param mCallBack
	 */
	private void destoryCallback(IRecipeCallBack mCallBack) {
		if (null == mCallBack || null == mCallbackList)
			return;

		/* 移除回调 */
		int nIndex = -1;
		int nSize = mCallbackList.size();
		for (int i = 0; i < nSize; i++) {
			if (mCallbackList.get(i).equals(mCallBack)) {
				nIndex = i;
				break;
			}
		}

		if (nIndex >= 0 && nIndex < nSize) {
			mCallbackList.removeElementAt(nIndex);
		}
	}

	/**
	 * 同步更新配方
	 */
	private synchronized void updateCallback(boolean bGetDB,
			boolean bRefCurrRecipe) {
		/* 更新当前配方的所有数据 */
		if (bRefCurrRecipe) {
			SKThread.getInstance()
					.getBinder()
					.onTask(MODULE.CALLBACK, MODULE.REFRESH_CURR_RECIPE,
							(Object) bGetDB, mCtlDatabaseCback, 0);
		}

		if (null == mCallbackList)
			return;

		int nSize = mCallbackList.size();
		for (int i = 0; i < nSize; i++) {
			mCallbackList.get(i).update();
		}
	}

	/**
	 * double 类型 转bytes 数组
	 * 
	 * @param nByteList
	 * @param nDValue
	 * @param eDataType
	 * @return
	 */
	private boolean doubleToBytes(Vector<Byte> nByteList, String sValueStr,
			DATA_TYPE eDataType) {
		double nDValue = 0;
		if (sValueStr != null && sValueStr != "") {
			try {
				nDValue = Double.valueOf(sValueStr);
			} catch (NumberFormatException e) {
				Log.e("doubleToBytes",
						"string to double error" + e.getMessage());
			}
		}

		switch (eDataType) {
		case BIT_1: {
			byte nTmpValue = (byte) nDValue;
			nByteList.add(nTmpValue);
			nByteList.add((byte) 0);
			break;
		}
		case POSITIVE_INT_16:
		case HEX_16:
		case OTC_16:
		case BCD_16: {
			int nTmpValue = (int) nDValue;
			nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
			break;
		}
		case POSITIVE_INT_32:
		case HEX_32:
		case OTC_32:
		case BCD_32: {
			long nTmpValue = (long) nDValue;
			nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 16) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 24) & 0xff));
			break;
		}
		case INT_16: {
			int nTmpValue = (int) nDValue;
			nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
			break;
		}
		case INT_32: {
			int nTmpValue = (int) nDValue;
			nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 16) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 24) & 0xff));
			break;
		}
		case FLOAT_32: {
			int nTmpValue = Float.floatToIntBits((float) nDValue);
			nByteList.add((byte) ((nTmpValue >>> 0) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 8) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 16) & 0xff));
			nByteList.add((byte) ((nTmpValue >>> 24) & 0xff));

			break;
		}
		case ASCII_STRING: {
			break;
		}
		case OTHER_DATA_TYPE:
		default: {
			break;
		}
		}
		return true;
	}

	/**
	 * double 类型 转bytes 数组
	 * 
	 * @param nByteList
	 * @param nDValue
	 * @param eDataType
	 * @return
	 */
	private String bytesTodouble(Vector<Byte> nByteList, DATA_TYPE eDataType) {
		String sResult = "";
		switch (eDataType) {
		case BIT_1: {
			if (nByteList.size() >= 2) {
				sResult = nByteList.get(0) + "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			} else {
				nByteList.clear();
			}
			break;
		}
		case INT_16: {
			if (nByteList.size() >= 2) {
				Vector<Byte> nBytes = new Vector<Byte>();
				nBytes.add(nByteList.get(0));
				nBytes.add(nByteList.get(1));

				Vector<Short> nShorts = new Vector<Short>();
				PlcRegCmnStcTools.bytesToShorts(nBytes, nShorts);

				if (!nShorts.isEmpty()) {
					sResult = nShorts.get(0) + "";
				}
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			} else {
				nByteList.clear();
			}
			break;
		}
		case HEX_16:
		case OTC_16:
		case BCD_16:
		case POSITIVE_INT_16: {
			if (nByteList.size() >= 2) {
				Vector<Byte> nBytes = new Vector<Byte>();
				nBytes.add(nByteList.get(0));
				nBytes.add(nByteList.get(1));

				Vector<Integer> nShorts = new Vector<Integer>();
				PlcRegCmnStcTools.bytesToUShorts(nBytes, nShorts);

				if (!nShorts.isEmpty()) {
					sResult = nShorts.get(0) + "";
				}
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			} else {
				nByteList.clear();
			}
			break;
		}
		case HEX_32:
		case OTC_32:
		case BCD_32:
		case POSITIVE_INT_32: {
			long nTmpValue0 = 0;
			long nTmpValue1 = 0;
			long nTmpValue2 = 0;
			long nTmpValue3 = 0;
			if (nByteList.size() >= 4) {
				nTmpValue0 = nByteList.get(0) & 0xff;
				nTmpValue1 = nByteList.get(1) & 0xff;
				nTmpValue2 = nByteList.get(2) & 0xff;
				nTmpValue3 = nByteList.get(3) & 0xff;
				sResult = ((nTmpValue3 << 24) + (nTmpValue2 << 16)
						+ (nTmpValue1 << 8) + (nTmpValue0))
						+ "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			} else {
				nByteList.clear();
			}
			break;
		}
		case INT_32: {
			int nTmpValue0 = 0;
			int nTmpValue1 = 0;
			int nTmpValue2 = 0;
			int nTmpValue3 = 0;
			if (nByteList.size() >= 4) {
				nTmpValue0 = nByteList.get(0) & 0xff;
				nTmpValue1 = nByteList.get(1) & 0xff;
				nTmpValue2 = nByteList.get(2) & 0xff;
				nTmpValue3 = nByteList.get(3) & 0xff;
				sResult = ((nTmpValue3 << 24) + (nTmpValue2 << 16)
						+ (nTmpValue1 << 8) + (nTmpValue0))
						+ "";
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			} else {
				nByteList.clear();
			}
			break;
		}
		case FLOAT_32: {
			int nTmpValue0 = 0;
			int nTmpValue1 = 0;
			int nTmpValue2 = 0;
			int nTmpValue3 = 0;
			if (nByteList.size() >= 4) {
				nTmpValue0 = nByteList.get(0) & 0xff;
				nTmpValue1 = nByteList.get(1) & 0xff;
				nTmpValue2 = nByteList.get(2) & 0xff;
				nTmpValue3 = nByteList.get(3) & 0xff;

				nTmpValue0 = ((nTmpValue3 << 24) + (nTmpValue2 << 16)
						+ (nTmpValue1 << 8) + (nTmpValue0));
				sResult = Float.intBitsToFloat(nTmpValue0) + "";
				sResult = subZeroAndDot(sResult);// 去除小数点后 多余的0

				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
				nByteList.removeElementAt(0);
			} else {
				nByteList.clear();
			}

			break;
		}
		case ASCII_STRING: {
			break;
		}

		case OTHER_DATA_TYPE:
		default: {
			break;
		}
		}

		return sResult;
	}

	/**
	 * 查询单个配方
	 * 
	 * @param nGroupId
	 * @param nRecipeId
	 * @return
	 */
	public synchronized String[] getRecipeData(int nGroupId, int nRecipeId,
			boolean bGetDatabase) {
		String[] sValueList = null;

		/* 查询配方是否存在 */
		if (!getRecipeIsExist(nGroupId, nRecipeId))
			return sValueList;

		/* 如果是当前配方，则返回当前配方值 */
		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if (null != nCurrRecipeObj) {
			if (!bGetDatabase
					&& nCurrRecipeObj.getCurrentGroupRecipeId() == nGroupId
					&& nCurrRecipeObj.getCurrentRecipeId() == nRecipeId) {
				if (null != sCurrRecipeValues) {
					return sCurrRecipeValues;
				}
			}
		}

		/* 从数据库查询当前配方 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return sValueList;

		Cursor dataProp = dbObj.getDatabaseBySql("select * from recipegroup"
				+ nGroupId + " where nRecipeId = " + nRecipeId, null);
		if (null == dataProp)
			return sValueList;

		if (dataProp.moveToNext()) {
			String sValues = dataProp.getString(dataProp
					.getColumnIndex("elems"));
			if (sValues != null) {
				sValueList = sValues.split("[,]");
			}
		}
		dataProp.close();

		return sValueList;
	}

	/**
	 * 设置单个配方
	 * 
	 * @param nGroupId
	 * @param nRecipeId
	 * @return
	 */
	public synchronized boolean setRecipeData(int nGroupId, int nRecipeId,
			String[] sValueList) {
		if (null == sValueList)
			return false;

		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return false;

		CurrentRecipe nCurrRecipeObj = getCurrRecipe();
		if (nCurrRecipeObj != null) {
			if (nCurrRecipeObj.getCurrentGroupRecipeId() == nGroupId
					&& nCurrRecipeObj.getCurrentRecipeId() == nRecipeId) {
				sCurrRecipeValues = sValueList;
				for(int i = 0; i < sCurrRecipeValues.length; i++){
					sCurrRecipeValues[i] = subZeroAndDot(sCurrRecipeValues[i]);
				}
			}
		}

		/* 更新当前配方 */
		String sValues = "";
		int nLen = sValueList.length;
		for (int i = 0; i < nLen - 1; i++) {
			sValues += sValueList[i] + ",";
		}
		if (nLen > 0) {
			sValues += sValueList[nLen - 1];
		}

		String sSqlStr = "delete from recipegroup" + nGroupId
				+ " where nRecipeId=" + nRecipeId;
		dbObj.execSql(sSqlStr);

		m_tmpValues.clear();
		m_tmpValues.put("nRecipeId", nRecipeId);
		m_tmpValues.put("elems", sValues);
		dbObj.insertData("recipegroup" + nGroupId, m_tmpValues);

		return true;
	}

	/**
	 * 获取配方组元素信息
	 * 
	 * @param gid
	 *            -配方组Id
	 * @param top
	 *            -起始行
	 * @param count
	 *            -获取多少行数据
	 */
	public synchronized ArrayList<String[]> getRecipeGroup(int gid, int top,
			int count) {
		ArrayList<String[]> data = null;

		/* 从数据库查询当前配方 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return data;

		Cursor cursor = dbObj.getDatabaseBySql("select * from recipegroup"
				+ gid + " order by nRecipeId asc limit " + (top - 1) + ","
				+ count, null);

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

	/**
	 * 获取配方组元素信息
	 * 
	 * @param gid
	 *            --配方组ID
	 * @return
	 */
	public synchronized ArrayList<String[]> getRecipeGroup(int gid) {
		ArrayList<String[]> data = null;

		/* 从数据库查询当前配方 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return data;

		Cursor cursor = dbObj.getDatabaseBySql("select * from recipegroup"
				+ gid + " order by nRecipeId asc ", null);

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

	public int getRecipeDataCount(int id) {
		int result = 0;
		/* 从数据库查询当前配方 */
		SKDataBaseInterface dbObj = SkGlobalData.getRecipeDatabase();
		if (null == dbObj)
			return result;

		Cursor cursor = dbObj.getDatabaseBySql(
				"select count(*) from recipegroup" + id, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				result = cursor.getInt(0);
			}
			cursor.close();
		}

		return result;
	}

	/**
	 * 配方导入
	 * @param name-配方组名称
	 * @param type-导入类型 type=0 U盘 type=1 SD卡
	 */
	public boolean recipeInfo(String name, int type) {
		if (name == null || name.equals("") || type < 0 || type > 1) {
			return false;
		}
		if (RecipeDataProp.getInstance().getmRecipeGroupList() == null) {
			return false;
		}
		
		int gid = -1;
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(name)){
				gid = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		
		RecipeOGprop oGprop= getOGRecipeData(gid);
		oGprop.setmCopyRecipeName(name.trim());
		if (type == 0) {
			oGprop.seteSaveMedia(STORAGE_MEDIA.U_DISH);
		}
		else if (type == 1) {
			oGprop.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
		}

		
		CurrentRecipe cinfo=new CurrentRecipe();
		cinfo.setCurrentGroupRecipeId(gid);
		cinfo.setCurrentRecipeId(-1);
		msgReadRecipeFromFile(cinfo);

		return true;
	}

	/**
	 * 配方导出
	 * @param name-配方组名称
	 * @param type-导出类型 type=0 U盘 type=1 SD卡
	 */
	public boolean recipeExport(String name, int type) {
		if (name == null || name.equals("") || type < -1 || type > 1) {
			return false;
		}
		if (RecipeDataProp.getInstance().getmRecipeGroupList() == null) {
			return false;
		}
		
		int gid = -1;
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(name)){
				gid = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(gid);
		if (oGprop != null) {
			if(type == 0){
				oGprop.seteSaveMedia(STORAGE_MEDIA.U_DISH);
			}
			else {
				oGprop.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
			}
		}
		
		CurrentRecipe cinfo=new CurrentRecipe();
		cinfo.setCurrentGroupRecipeId(gid);
		cinfo.setCurrentRecipeId(-1);
	    msgWriteRecipeToFile(cinfo);
	
		return true;
	}

	/**
	 * 配方组保存
	 * @param name-配方组名称
	 * @param data-配方组数据 ArrayList<E> 存储该组所有配方 String[] 存储某一个配方数据
	 */
	public boolean recipeGroupSave(String name, ArrayList<String[]> data) {
		if (name == null || name.equals("") || data == null || data.size() == 0) {
			return false;
		}

		Vector<RecipeOGprop> list = RecipeDataProp.getInstance().getmRecipeGroupList();
		if (list == null || list.size() == 0) {
			return false;
		}

		name=name.trim();
		boolean result = false;
		int gid=0;
		RecipeOGprop mGprop = null;
		for (int i = 0; i < list.size(); i++) {
			RecipeOGprop item = list.get(i);
			String gname=item.getsRecipeGName();
			if (gname!=null) {
				if (gname.equals(name)) {
					result = true;
					mGprop = item;
					gid=item.getnGRecipeID();
					break;
				}
			}
		}

		if (result) {
			// 配方组存在
			if (data == null || mGprop == null) {
				return false;
			}

			for (int i = 0; i < data.size(); i++) {
				String temp[] = data.get(0);
				if (temp == null) {
					return false;
				}

				if (mGprop.getnRecipeLen() != temp.length) {
					return false;
				}
			}
		}

		// 组号id存储 在最后一个位置
		String[] ss=new String[]{gid+""};
		data.add(ss);
				
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPE_GROUP_SAVE, data,
				mCtlDatabaseCback, 0);
		
		return true;
	}

	/**
	 * 配方组保存
	 * @param gid-配方组id
	 * @param data -配方组数据 ArrayList<E> 存储该组所有配方 String[] 存储某一个配方数据
	 */
	public boolean recipeGroupSave(int gid, ArrayList<String[]> data) {
		if (gid < -1 || data == null || data.size() == 0) {
			return false;
		}

		Vector<RecipeOGprop> list = RecipeDataProp.getInstance().getmRecipeGroupList();
		if (list == null || list.size() == 0) {
			return false;
		}

		boolean result = false;
		RecipeOGprop mGprop = null;
		for (int i = 0; i < list.size(); i++) {
			RecipeOGprop item = list.get(i);
			if (item.getnGRecipeID() == gid) {
				result = true;
				mGprop = item;
				break;
			}
		}

		if (!result) {
			return false;
		}
		
		// 配方组存在
		if (data == null || mGprop == null) {
			return false;
		}

		for (int i = 0; i < data.size(); i++) {
			String temp[] = data.get(0);
			if (temp == null) {
				return false;
			}

			if (mGprop.getnRecipeLen() != temp.length) {
				return false;
			}
		}
		
		// 组号id存储 在最后一个位置
		String[] ss=new String[]{gid+""};
		data.add(ss);
		
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, MODULE.RECIPE_GROUP_SAVE, data,
				mCtlDatabaseCback, 0);
		return result;
	}

	/**
	 * 配方保存
	 * @param name-配方组名称
	 * @param 配方id,即配方在组里面的序号，从0开始
	 * @param data-配方数据
	 */
	public boolean recipeSave(String name, int id, String[] data) {
		if (name == null || name.equals("") || data == null || data.length == 0
				|| id < -1) {
			return false;
		}
		
		Vector<RecipeOGprop> list = RecipeDataProp.getInstance().getmRecipeGroupList();
		if (list == null || list.size() == 0) {
			return false;
		}

		boolean result = false;
		RecipeOGprop mGprop = null;
		int gid=0;
		for (int i = 0; i < list.size(); i++) {
			RecipeOGprop item = list.get(i);
			if (item.getsRecipeGName().equals(name)) {
				result = true;
				mGprop = item;
				gid=item.getnGRecipeID();
				break;
			}
		}
		
		if (!result) {
			return false;
		}
		
		// 配方组存在
		if (data == null || mGprop == null) {
			return false;
		}

		if (mGprop.getnRecipeLen()!=data.length) {
			return false;
		}

		//
		String [] ss=new String[]{gid+"",id+""};
		
		ArrayList<String[]> temp=new ArrayList<String[]>();
		temp.add(data);
		temp.add(ss);

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.RECIPE_SAVE, temp,
						mCtlDatabaseCback, 0);

		return true;
	}

	/**
	 * 配方保存
	 * @param gid-配方组id
	 * @param 配方id,即配方在组里面的序号，从0开始
	 * @param data-配方数据
	 */
	public boolean recipeSave(int gid, int id, String[] data) {
		if (gid < -1 || data == null || data.length == 0 || id < -1) {
			return false;
		}
		
		Log.d(TAG, "recipeSave gid="+gid+",id="+id);
		Vector<RecipeOGprop> list = RecipeDataProp.getInstance().getmRecipeGroupList();
		if (list == null || list.size() == 0) {
			return false;
		}

		boolean result = false;
		RecipeOGprop mGprop = null;
		for (int i = 0; i < list.size(); i++) {
			RecipeOGprop item = list.get(i);
			if (item.getnGRecipeID()==gid) {
				result = true;
				mGprop = item;
				break;
			}
		}
		
		if (!result) {
			return false;
		}
		
		// 配方组存在
		if (data == null || mGprop == null) {
			return false;
		}

		if (mGprop.getnRecipeLen()!=data.length) {
			return false;
		}

		//
		String [] ss=new String[]{gid+"",id+""};
		
		ArrayList<String[]> temp=new ArrayList<String[]>();
		temp.add(data);
		temp.add(ss);

		
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.CALLBACK, MODULE.RECIPE_SAVE, temp,
						mCtlDatabaseCback, 0);

		return true;
	}
	

	/**
	 * 配方组数据复制
	 * @param fromName-源配方组名称
	 * @param toName-目标配方组名称
	 * @param type-源配方组文件位置 type=0 U盘 type=1 SD卡 type=2 内部数据库
	 */
	public boolean recipeGroupCopy(String fromName, String toName, int type) {
		if (fromName == null || fromName.equals("") || toName == null
				|| toName.equals("") || type < -1 || type > 2) {
			return false;
		}
		if(RecipeDataProp.getInstance().getmRecipeGroupList()  == null){
			return false;
		}
		int gid = -1;
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(toName)){
				gid = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		if(gid < 0 ){
			return false;
		}
		
		
		RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(gid);
		oGprop.setmCopyRecipeName(fromName.trim());
		if (type == 0) {
			oGprop.seteSaveMedia(STORAGE_MEDIA.U_DISH);
		}
		else if (type == 1) {
			oGprop.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
		}
		else {
			oGprop.seteSaveMedia(STORAGE_MEDIA.INSIDE_DISH);
		}
		
		
		CurrentRecipe cinfo=new CurrentRecipe();
		cinfo.setCurrentGroupRecipeId(gid);
		cinfo.setCurrentRecipeId(-1);
		RecipeDataCentre.getInstance().msgReadRecipeFromFile(cinfo);

		return true;
	}

	
	/**
	 * 配方组数据复制   只能 正对
	 * @param fromId-源配方组id
	 * @param toId-目标配方组id
	 * @param type-源配方组文件位置 type=0 U盘 type=1 SD卡 type=2 内部数据库
	 */
	public boolean recipeGroupCopy(int fromId, int toId, int type) {
		if (fromId < -1 || toId < -1 ||  type != 2) {// 暂时支持 内部数据中的拷贝
			return false;
		}
		if(RecipeDataProp.getInstance().getmRecipeGroupList()  == null){
			return false;
		}
		
		String fromName = "";
		String toName = "";
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == fromId){
				fromName = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName();
			}
			else if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == toId){
				toName = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName();
			}
		}
		
		recipeGroupCopy(fromName, toName, type);

		return true;
	}
	
	

	/**
	 * 配方数据复制
	 * @param name-配方组名称
	 * @param fromId-源配方id
	 * @param toId-目标配方id
	 * @param type-源配方文件位置 type=0 U盘 type=1 SD卡 type=2 内部数据库
	 */
	public boolean recipeCopy(String name, int fromId, int toId, int type) {
		if (name == null || name.equals("") || fromId < -1 || toId < -1
				|| type < -1 || type > 2) {
			return false;
		}
		
		String [] recipeData = getRecipeDate(type, name,fromId);
		recipeSave(name, toId, recipeData);
		
		return true;
	}
	

	/**
	 * 配方数据复制
	 * @param id-配方组id
	 * @param fromId-源配方id
	 * @param toId -目标配方id
	 * @param type-源配方文件位置 type=0 U盘 type=1 SD卡 type=2 内部数据库
	 */
	public boolean recipeCopy(int id, int fromId, int toId, int type) {
		if (id < -1 || fromId < -1 || toId < -1 || type < -1 || type > 2) {
			return false; 
		}
		if(RecipeDataProp.getInstance().getmRecipeGroupList()  == null){
			return false;
		}
		
		String recipeGroupName = null;
		for(int i = 0 ; i < RecipeDataProp.getInstance().getmRecipeGroupList().size(); i++){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID() == id){
				recipeGroupName = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName();
				break;
			}
		}
		
		recipeCopy(recipeGroupName, fromId, toId, type);

		return true;
	}

	/**
	 * 删除配方组
	 * @param gid-配方组id
	 */
	public boolean recipeGroupDelete(int gid){
		
		deleteRecipeGroup(gid);
		return true;
	}
	
	/**
	 * 删除配方组
	 * @param gid-配方组名称
	 */
	public boolean recipeGroupDelete(String name){
		if(RecipeDataProp.getInstance().getmRecipeGroupList()  == null){
			return false;
		}
		
		int gid = -1;
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(name)){
				gid = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		deleteRecipeGroup(gid);
		return true;
	}
	
	/**
	 * 删除配方
	 * @param gid-配方组id
	 * @param rid-配方id
	 */
	public boolean recipeDelete(int gid,int rid){
		CurrentRecipe recipe =  new CurrentRecipe();
		recipe.setCurrentGroupRecipeId(gid);
		recipe.setCurrentRecipeId(rid);
		
		deleteRecipe(recipe);
		return true;
	}
	
	/**
	 * 删除配方
	 * @param gid-配方组名称
	 * @param rid-配方id
	 */
	public boolean recipeDelete(String name,int rid){
		if(RecipeDataProp.getInstance().getmRecipeGroupList() == null){
			return false;
		}
		
		int gid = -1;
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(name)){
				gid = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		recipeDelete(gid, rid);
		
		return true; 
	}
	
	/**
	 * 
	 * @param type-源配方文件位置 type=0 U盘 type=1 SD卡 type=2 内部数据库
	 * @param recipeName - 配方组名称
	 * @param recipeID - 配方ID
	 * @return 配方数据
	 */
	private String[] getRecipeDate(int type, String recipeName, int recipeID){
		if(type == 2){// 内部数据库
			return getMemoryRecipeData(recipeName, recipeID);
		}
		else { // U盘、SD卡
			return getOutDeviceRecipeData(recipeName, type, recipeID);
		}
	}
	
	
	/**
	 * 
	 * @param recipeName  -配方组名称
	 * @param recipeID    -- 配方ID
	 * @return 配方数据
	 */
	private String[] getMemoryRecipeData(String recipeName, int recipeID){
		if(TextUtils.isEmpty(recipeName) || recipeID < 0 ){
			return null;
		}
		if(RecipeDataProp.getInstance().getmRecipeGroupList() == null){
			return null;
		}
		int gid = -1; //配方组ID
		for(int i = 0;  i < RecipeDataProp.getInstance().getmRecipeGroupList().size() ; i++ ){
			if(RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(recipeName)){
				gid = RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		
		return getRecipeData(gid, recipeID, false);
	}
	
	
	/**
	 * 
	 * @param type-源配方文件位置 type=0 U盘 type=1 SD卡 type=2 内部数据库
	 * @param recipeName - 配方组名称
	 * @param recipeID - 配方ID
	 * @return 配方数据
	 */
	private String[] getOutDeviceRecipeData(String recipeName, int type, int recipeID){
		if(TextUtils.isEmpty(recipeName) || recipeID < 0){
			return null;
		}
		
		String path = null;
		if(type == 0){
			path = "/mnt/usb2/";
		}else {
			path = "/mnt/sdcard/";
		}
		
		if(existsFile(path, recipeName)){
			try {
				DataInputStream fileReaderHand = new DataInputStream(new FileInputStream(path + recipeName));
				BufferedReader readBuffer = new BufferedReader(new InputStreamReader(fileReaderHand, "GBK"));
				String sTmpStr;

				String[] sHeadBuf = null;
				String[] sBodyBuf = null;
				ArrayList<String> elementBuf = new ArrayList<String>();
				// head
				sTmpStr = readBuffer.readLine();
				sHeadBuf = sTmpStr.split(",");

				// 读取body
				final Vector<String[]> sBodyValues = new Vector<String[]>();
				sBodyValues.clear();
				while ((sTmpStr = readBuffer.readLine()) != null) {
					sBodyBuf = sTmpStr.split(",");
					if (sBodyBuf.length != sHeadBuf.length) {
						readBuffer.close();
						return null;
					}
					// element name
					elementBuf.add(sBodyBuf[0]);
					// body
					String[] beanBuf = new String[sBodyBuf.length - 1];
					for (int i = 1; i < sBodyBuf.length; i++) {
						beanBuf[i - 1] = sBodyBuf[i];
					}
					sBodyValues.add(beanBuf);
				}


				 Vector<String[]> sGroupValues = new Vector<String[]>();
				sGroupValues.clear();
				for (int i = 0; i < sHeadBuf.length - 1; i++) {

					String[] sValueList = new String[sBodyValues.size()];
					for (int j = 0; j < sBodyValues.size(); j++) {
						String[] temp = sBodyValues.elementAt(j);
						sValueList[j] = temp[i].trim();
					}

					// 进行检查数据读取的数据是否合法
					if (!isValidData(SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId(), sValueList)) { // 如果输入的数据不合法， 那么就退出
						readBuffer.close();
						return null;
					}

					sGroupValues.add(sValueList);
				}

				
				readBuffer.close();
				String[] elemet = sGroupValues.get(recipeID);
				sGroupValues = null; 
				return elemet;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	/**
	 *  检查是否存在文件
	 * @param path --  文件路径
	 * @param fileName -- 文件名称
	 * @return 存在/不存在  ---  true/false
	 */
	
	public static boolean existsFile(String path,  String fileName){
		if(TextUtils.isEmpty(path) || TextUtils.isEmpty(fileName)){
			return false;
		}
		
		// 检查文件路径是否存在
		File filePath = new File(path);
		if (!filePath.exists()) {
			if(!filePath.mkdirs()){
				return false;
			}
		}
		
		//检查 文件是否存在
		try {
			File file  = new File(path + fileName);
			if(file.exists()){
				return true;
			}
			else {
				return file.createNewFile();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}
		
	/**
	 * 当前配方写入plc
	 */
	public void currentRecipeWriteToPlc(){
		RecipeDataCentre.getInstance().msgWriteRecipeToPlc(SystemInfo.getCurrentRecipe());
	}
	
	/**
	 * 从plc读取当前配方
	 */
    public void readCurrentRecipeFromPlc(){
    	RecipeDataCentre.getInstance().msgReadRecipeFromPlc(SystemInfo.getCurrentRecipe());
	}
	
    /**
     * 保存当前配方
     */
    public void saveCurrentRecipe(){
    	CurrentRecipe cRecipe=RecipeDataCentre.getInstance().getCurrRecipe();
		RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(cRecipe.getCurrentGroupRecipeId());
		if (oGprop==null) {
			return;
		}
		
		RecipeOprop data=null;
		
		Vector<RecipeOprop>  mRecipeLists=oGprop.getmRecipePropList();
		if (mRecipeLists!=null) {
			for (int i = 0; i < mRecipeLists.size(); i++) {
				if (mRecipeLists.get(i).getnRecipeId()==cRecipe.getCurrentRecipeId()) {
					data=mRecipeLists.get(i);
					break;
				}
			}
		}
		
		if (data==null) {
			return;
		}
        EditRecipeInfo eInfo=new EditRecipeInfo();
        eInfo.mRecipeData=data;
		eInfo.mRecipeInfo=cRecipe;
		eInfo.sValueList=RecipeDataCentre.getInstance().getRecipeData(cRecipe.getCurrentGroupRecipeId(), 
				cRecipe.getCurrentRecipeId(), false);;
		RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
    }
    
    /**
     * 添加配方
     * @param gName-配方组名称
     * @param rName-配方名称
     */
    public boolean addRecipe(String gName,String rName){
    	boolean result=false;
    	if (RecipeDataProp.getInstance().getmRecipeGroupList()==null) {
			return result;
		}
    	gName=gName.trim();
    	rName=rName.trim();
    	boolean r=false;
    	int len=0;
    	int gid=0;
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				r=true;
				len=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnRecipeLen();
				gid=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		
		if (r) {
			double [] data=new double[len];
			result=insert(gid, data,rName);
		}
    	
    	return result;
    }
    
    /**
     * 添加配方
     * @param gName-配方组名称
     * @param rName-配方名称
     * @param data-配方元素数据
     */
    public boolean addRecipe(String gName,String rName,double[] data){
    	boolean result=false;
    	if (RecipeDataProp.getInstance().getmRecipeGroupList()==null) {
			return result;
		}
    	gName=gName.trim();
    	rName=rName.trim();
    	boolean r=false;
    	int len=0;
    	int gid=0;
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				r=true;
				len=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnRecipeLen();
				gid=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				break;
			}
		}
		
		if (r) {
			if (len!=data.length) {
				return false;
			}
			if (!isValidData(gid, data)) {
				return false;
			}
			result=insert(gid, data,rName);
		}
		
    	return result;
    }
    
    /**
     * 获取配方名称
     * @param gName=配方组名称
     * @param lId=语言序号 ，从0开始
     */
    public String[] getRecipeName(String gName,int lId){
    	if (RecipeDataProp.getInstance().getmRecipeGroupList()==null) {
			return null;
		}
    	
    	String[] reuslt=null;
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				//获取配方组
				Vector<RecipeOprop> list=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList();
				if (list!=null) {
					//获取改组所有配方
					reuslt=new String[list.size()];
					for (int j = 0; j < list.size(); j++) {
						//获取配方名称，多语言
						Vector<String> names=list.get(j).getsRecipeName();
						if (names!=null) {
							if (names.size()>lId) {
								reuslt[j]=names.get(j);
							}else {
								reuslt[j]="";
							}
						}
					}
				}
				break;
			}
		}
    	return reuslt;
    }
    
    /**
     * 获取配方元素名称
     * @param gName=配方组名称
     * @param rId=配方序号，从0开始
     * @param lId=语言序号，从0开始
     */
    public String[] getRecipeElement(String gName,int rId,int lId){
    	if (RecipeDataProp.getInstance().getmRecipeGroupList()==null) {
			return null;
		}
    	
    	String[] reuslt=null;
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				
				//改组所有元素
				Vector<Vector<String>> list=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsElemNameList();
				
				if (list!=null) {
					reuslt=new String[list.size()];
					for (int j = 0; j < list.size(); j++) {
						Vector<String> Element=list.get(j);
						if (Element!=null) {
							if (Element.size()>lId) {
								reuslt[j]=Element.get(j);
							}else {
								reuslt[j]="";
							}
						}
					}
				}
				break;
			}
		}
    	return reuslt;
    }
    
    /**
     * 获取配方数据
     * @param gName=配方组名称
     */
    public ArrayList<String[]> getRecipeData(String gName){
    	ArrayList<String[]> data=null;
    	
    	gName=gName.trim();
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
    	for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				data=new ArrayList<String[]>();
				int gid=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				int len=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnRecipeNum();
				for (int j = 0; j < len; j++) {
					String [] ss=RecipeDataCentre.getInstance().getRecipeData(gid, j, false);
					data.add(ss);
				}
				break;
			}
		}
    	return data;
    }
    
    /**
     * 修改配方名称
     * @param gName=配方组名称
     * @param rId=配方序号，从0开始
     * @param newName=修改后的配方名称
     * @param lId=语言序号，从0开始
     */
    public boolean updateRecipeName(String gName,int rId,String newName,int lId){
    	boolean reulst=false;
    	if (RecipeDataProp.getInstance().getmRecipeGroupList()==null) {
			return false;
		}
    	
    	boolean r=false;
    	newName=newName.trim();
    	RecipeOprop recipe=null;
    	int gid=0;
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				
				gid=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				
				//获取配方组
				Vector<RecipeOprop> list=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getmRecipePropList();
				if (list!=null) {
					for (int j = 0; j < list.size(); j++) {
						if(j==rId){
							recipe=list.get(j);
							Vector<String> name=list.get(j).getsRecipeName(); 
							if (name!=null) {
								if (name.size()>lId) {
									name.set(lId, newName);
									r=true;
								}
							}
						}
					}
				}
				break;
			}
		}
		
		if (r) {
			reulst=true;
			CurrentRecipe cInfo=new CurrentRecipe();
			cInfo.setCurrentGroupRecipeId(gid);
			cInfo.setCurrentRecipeId(rId);
			
			EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
			
			eInfo.mRecipeData=recipe;
			eInfo.mRecipeInfo=cInfo;
			eInfo.sValueList=RecipeDataCentre.getInstance().getRecipeData(gid, rId, false);;
			
			RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
			
		}
		
    	return reulst;
    }
    
    /**
     * 修改配方元素名称
     * @param gName=配方组名称
     * @param eId=元素序号，从0开始
     * @param eName=修改后的元素名称
     * @param lId=语言序号，从0开始
     */
    public boolean updateRecipeElement(String gName,int eId,String eName,int lId){
    	boolean reulst=false;
    	if (RecipeDataProp.getInstance().getmRecipeGroupList()==null) {
			return false;
		}
    	
    	boolean r=false;
    	eName=eName.trim();
    	RecipeOprop recipe=null;
    	int gid=0;
    	int rId=0;
    	int nGroupSize = RecipeDataProp.getInstance().getmRecipeGroupList().size();
		for (int i = 0; i < nGroupSize; i++) {
			if (RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsRecipeGName().equals(gName)) {
				
				gid=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getnGRecipeID();
				
				Vector<Vector<String>> data=RecipeDataProp.getInstance().getmRecipeGroupList().get(i).getsElemNameList();
				if (data!=null) {
					for (int j = 0; j < data.size(); j++) {
						if (eId==j) {
							Vector<String> ee=data.get(j);
							if (ee.size()>lId) {
								ee.set(lId, eName);
							}
						}
					}
				}
				break;
			}
		}
		
		if (r) {
			reulst=true;
			CurrentRecipe cInfo=new CurrentRecipe();
			cInfo.setCurrentGroupRecipeId(gid);
			cInfo.setCurrentRecipeId(rId);
			
			EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
			
			eInfo.mRecipeData=recipe;
			eInfo.mRecipeInfo=cInfo;
			eInfo.sValueList=RecipeDataCentre.getInstance().getRecipeData(gid, rId, false);;
			
			RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
			
		}
    	return reulst;
    }
    
	/**
	 * 配方组信息保存
	 * @param name-配方组名称
	 * @param id-配方组id
	 * @param data-配方组数据
	 */
	private void recipeGroupSave(ArrayList<String[]> data){
		//Log.d(TAG, "data="+data.size());
		try {
			//获取配方组id,data最后一个存储配方组id
			String [] temp=data.get(data.size()-1);
			if (temp==null||temp.length==0) {
				return;
			}
			int gid=Integer.valueOf(temp[0]);
			for (int i = 0; i < data.size()-1; i++) {
				String value[]=data.get(i);
				if (value!=null&&value.length!=0) {
					setRecipeData(gid, i, value);
				}
			}
			updateCallback(false, true);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "AK Macro recipeGroupSave error!!!");
		}
		
	}
	
	/**
	 * 配方组信息保存
	 * @param name-配方组名称
	 * @param id-配方组id
	 * @param data-配方组数据
	 */
	private void recipeSave(ArrayList<String[]> data){
		//Log.d(TAG, "data="+data.size());
		try {
			//获取配方组id,data最后一个存储配方组id
			String[] temp=data.get(1);
			if (temp==null||temp.length<1) {
				return;
			}
			
			int gid=Integer.valueOf(temp[0]);
			int rid=Integer.valueOf(temp[1]);
			
			setRecipeData(gid, rid, data.get(0));
			updateCallback(false, true);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "AK Macro recipeGroupSave error!!!");
		}
		
	}
	
	
	/**
	 * 查找配方是否存在
	 * 
	 * @param nGroupId
	 * @param nRecipeId
	 * @return
	 */
	public boolean getRecipeIsExist(int nGroupId, int nRecipeId) {
		RecipeOGprop mGrecipe = getOGRecipeData(nGroupId);
		if (null == mGrecipe)
			return false;

		int nRecipeSize = mGrecipe.getmRecipePropList().size();
		for (int i = 0; i < nRecipeSize; i++) {
			//Log.d(TAG, "id="+mGrecipe.getmRecipePropList().get(i).getnRecipeId()+",rid="+nRecipeId);
			if (mGrecipe.getmRecipePropList().get(i).getnRecipeId() == nRecipeId) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * 添加一个新配方
	 * @param gid-配方组id
	 * @param data-配方元素数据
	 * @param name-配方名称
	 */
	private synchronized boolean insert(int gid, double[] data,String name) {

		// 创建一个配方
		RecipeOprop recipe = new RecipeOprop();

		int count = SystemInfo.getLanguageNumber();
		String values = "";

		
		// 配方名称
		boolean bname = DBTool.getInstance().getmRecipeDataBiz().existRecipeName(name, gid);
		if (bname) {
			return false;
		} else {
			Vector<String> names = new Vector<String>();
			for (int j = 0; j < count; j++) {
				names.add(name);
			}
			recipe.setsRecipeName(names);
		}
		
		int size = data.length;
		for (int i = 0; i < size; i++) {
			// 配方元素
			double temp=data[i];
			if (i < size - 1) {
				values += temp + ",";
			} else {
				values += temp + "";
			}
		}

		// 设置配方ID
		int id = DBTool.getInstance().getmRecipeDataBiz().getRecipeId(gid);
		recipe.setnRecipeId(id);
		// 设置配方描述
		Vector<String> desList = new Vector<String>();
		desList.add("");
		recipe.setsRecipeDescri(desList);

		EditRecipeInfo eInfo = RecipeDataCentre.getInstance().new EditRecipeInfo();

		CurrentRecipe info = new CurrentRecipe();
		info.setCurrentGroupRecipeId(gid);
		info.setCurrentRecipeId(-1);

		String elemsValue[] = null;
		if (values != null) {
			elemsValue = values.split(",");
		}
		eInfo.mRecipeData = recipe;
		eInfo.mRecipeInfo = info;
		eInfo.sValueList = elemsValue;
		RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
		return true;

	}

	/**
	 * 检查从文件中读取的数据是否是合法的数据 不合法 return false
	 * 
	 * @return
	 */
	private boolean isValidData(int groupId, String[] valueList) {
		boolean ret = false;
		RecipeOGprop recipeData = RecipeDataCentre.getInstance()
				.getOGRecipeData(groupId);
		Vector<DATA_TYPE> eDataTypeList;// 配方元素类型
		if (recipeData == null || recipeData.geteDataTypeList() == null) {
			return false;
		}

		eDataTypeList = recipeData.geteDataTypeList();
		if (eDataTypeList.size() != valueList.length) {
			return false;
		}

		for (int i = 0; i < valueList.length; i++) {
			valueList[i] = valueList[i].trim();

			if (!valueList[i].equals("")) {
				double temp = Double.valueOf(valueList[i]);
				String value = valueList[i];
				DATA_TYPE type = eDataTypeList.get(i);

				switch (type) {
				case INT_16:
					if (!isInt(value) || temp < -32768 || temp > 32767) {
						return false;
					}
					break;
				case POSITIVE_INT_16:
					if (!isPosInt(value) || temp < 0 || temp > 65535) {
						return false;
					}
					break;
				case INT_32:
					if (!isInt(value) || temp < -2147483648
							|| temp > 2147483647) {
						return false;
					}
					break;
				case POSITIVE_INT_32:
					if (!isPosInt(value) || temp < 0 || temp > 4294967295L) {
						return false;
					}
					break;
				case FLOAT_32:
					if (temp < -2147483648 || temp > 2147483647) {
						return false;
					}
					break;
				case BCD_16:
					if (!isInt(value) || temp < 0 || temp > 9999) {
						return false;
					}
					break;
				case BCD_32:
					if (!isInt(value) || temp < 0 || temp > 99999999) {
						return false;
					}
					break;
				case BIT_1:
					if (!isInt(value) || temp < 0 || temp > 1) {
						return false;
					}
					break;
				}

			} else {
				valueList[i] = 0 + "";
			}
		}
		return true;
	}
	
	/**
	 * 检查从文件中读取的数据是否是合法的数据 不合法 return false
	 * 
	 * @return
	 */
	private boolean isValidData(int groupId, double[] valueList) {
		RecipeOGprop recipeData = RecipeDataCentre.getInstance()
				.getOGRecipeData(groupId);
		Vector<DATA_TYPE> eDataTypeList;// 配方元素类型
		if (recipeData == null || recipeData.geteDataTypeList() == null) {
			return false;
		}

		eDataTypeList = recipeData.geteDataTypeList();
		if (eDataTypeList.size() != valueList.length) {
			return false;
		}

		for (int i = 0; i < valueList.length; i++) {

			double temp = valueList[i];
			String value = temp+"";
			DATA_TYPE type = eDataTypeList.get(i);

			switch (type) {
			case INT_16:
				if (!isInt(value) || temp < -32768 || temp > 32767) {
					return false;
				}
				break;
			case POSITIVE_INT_16:
				if (!isPosInt(value) || temp < 0 || temp > 65535) {
					return false;
				}
				break;
			case INT_32:
				if (!isInt(value) || temp < -2147483648
						|| temp > 2147483647) {
					return false;
				}
				break;
			case POSITIVE_INT_32:
				if (!isPosInt(value) || temp < 0 || temp > 4294967295L) {
					return false;
				}
				break;
			case FLOAT_32:
				if (temp < -2147483648 || temp > 2147483647) {
					return false;
				}
				break;
			case BCD_16:
				if (!isInt(value) || temp < 0 || temp > 9999) {
					return false;
				}
				break;
			case BCD_32:
				if (!isInt(value) || temp < 0 || temp > 99999999) {
					return false;
				}
				break;
			case BIT_1:
				if (!isInt(value) || temp < 0 || temp > 1) {
					return false;
				}
				break;
			}
		}
		return true;
	}

	/**
	 * 是否是正整数
	 */
	public boolean isPosInt(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet = false;
		char first = str.charAt(0);
		if(first != '-'){
			resulet = true;
		}
		return resulet;
	}

	/**
	 * 是否是整数
	 */
	public boolean isInt(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet = false;
		Pattern pattern = Pattern.compile("[-_0-9]*");
		resulet = pattern.matcher(str).matches();
		return resulet;
	}

	/**
	 * 正则表达式，去除浮点数，小数点后面多余的0
	 * 
	 * @param s
	 *            输入
	 * @return
	 */
	public static String subZeroAndDot(String s) {
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// 去掉多余的0
			s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return s;
	}

	
	public void upLoadFile(int gid,SKThread.ICallback call){
		//写文件
		CurrentRecipe cinfo=new CurrentRecipe();
		cinfo.setCurrentGroupRecipeId(gid);
		cinfo.setCurrentRecipeId(-1);
		
		writeRecipeGroupToFile(cinfo);
		//回调发送文件
		SKThread.getInstance()
		.getBinder()
		.onTask(MODULE.CALLBACK, MODULE.WRITE_RECIPES_TO_FILE,
				cinfo, call, 0);
	}
}
