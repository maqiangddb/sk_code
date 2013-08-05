package com.android.Samkoonhmi.skgraphics.plc.show;
import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.model.GraphBaseInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skgraphics.ITimerUpdate;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

/**
 * 图表-界面,包括普通，统计，仪表
 * @author 刘伟江
 * @version v1.0.0.1 
 * 创建时间  2012-4-23
 * 最后修改时间 2012-4-23
 * 
 */
public class SKGraph extends SKGraphCmnShow implements ITimerUpdate {

	private GraphBaseInfo mGrapInfo;
	private SKGraphShape shape;
	private int nItemId;
	private int nSceneId;
	private SKItems items;
	private double nStateValues;
	private String sTaskName;
	private boolean show;         // 是否可显现
	private boolean showByAddr;   // 是否注册显现地址
	private boolean showByUser;   // 是否受用户权限控件
	private boolean init;
	private double sMax;
	private double sMin;
	private boolean alarm;
	
	public SKGraph(int itemId,int sceneId,GraphBaseInfo info){
		this.nItemId=itemId;
		this.nSceneId=sceneId;
		this.sTaskName="";
		this.init=true;
		this.alarm=false;
		items=new SKItems();
		this.mGrapInfo=info;
		
		if (info!=null) {
			Rect rect=new Rect(mGrapInfo.getnLeftTopX()-2, mGrapInfo.getnLeftTopY()-2,
					mGrapInfo.getnLeftTopX()+mGrapInfo.getnWidth()+2, 
					mGrapInfo.getnLeftTopY()+mGrapInfo.getnHeigth()+2);
			items.itemId=nItemId;
			items.sceneId=nSceneId;
			items.rect=rect;
			items.nZvalue=mGrapInfo.getnZvalue();
			items.nCollidindId=mGrapInfo.getnColidindId();
			items.mGraphics=this;
		}
		
	}

	@Override
	public void getDataFromDatabase() {
		
	}
	
	@Override
	public void initGraphics() {
		if (mGrapInfo==null) {
			return;
		}
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
	
		show = true;
		init=true;
		alarm=false;
		nStateValues=0;
		
		
		if (mGrapInfo.getnSourceRang()==0) {
			sMax=mGrapInfo.getnSourceMax();
			sMin=mGrapInfo.getnSourceMin();
		}else {
			sMax=0;
			sMin=0;
		}
		
		//显现
		itemIsShow(); 
		
		//注册通知
		registNotice();
		
	}

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			if ((itemId==nItemId)&&(null!=mGrapInfo)) {
				//Log.d("SKScene", "itemId:"+itemId+",show:"+show);
				if (shape==null) {
					shape=new SKGraphShape(mGrapInfo);
					shape.setReset(true);
					shape.drawShape(true,(float)nStateValues,canvas,alarm);
				}else {
					shape.setReset(false);
					shape.drawShape(init,(float)nStateValues,canvas,alarm);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 注册监听
	 */
	private void registNotice(){
		
		// 显现权限
		if (mGrapInfo.getmShowInfo() != null) {
			if (mGrapInfo.getmShowInfo().getShowAddrProp()!=null) {
				// 受地址控制
				showByAddr = true;
			}
			if (mGrapInfo.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
		}
				
		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mGrapInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(mGrapInfo.getmShowInfo().getShowAddrProp(), showCall,true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(mGrapInfo.getmShowInfo().getShowAddrProp(), showCall,false);
			}

		}
		
		/**
		 * 注册源范围监视地址
		 */
		if (mGrapInfo.getnSourceRang()==1) {
			
			//源范围-最小值
			if (mGrapInfo.getmMinAddrProp()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mGrapInfo.getmMinAddrProp(), minCall, false);
			}
			
			//源范围-最大值
			if (mGrapInfo.getmMaxAddrProp()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mGrapInfo.getmMaxAddrProp(), maxCall, false);
			}
		}
		
		/**
		 * 注册报警监视地址
		 */
		if (mGrapInfo.isbAlarm()) {
			if (mGrapInfo.getnType()==1) {
				if (mGrapInfo.getmAlarmMinAddr()!=null) {
					SKPlcNoticThread.getInstance().addNoticProp(
							mGrapInfo.getmAlarmMinAddr(), alarmMinCall, false);
				}
				
				if (mGrapInfo.getmAlarmMaxAddr()!=null) {
					SKPlcNoticThread.getInstance().addNoticProp(
							mGrapInfo.getmAlarmMaxAddr(), alarmMaxCall, false);
				}
			}
		}
		
		// 注册监视地址
		if (mGrapInfo.getmAddress() != null) {
			SKPlcNoticThread.getInstance().addNoticProp(
					mGrapInfo.getmAddress(), watchCall, false);
		}
		
		SKSceneManage.getInstance().onRefresh(items);
	}
	
	/**
	 * 后台线程
	 */
	SKThread.ICallback callback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {

		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			
		}
	};
	
	/**
	 * 显现地址改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			show=isShow();
		}

	};
	
	/**
	 * 监视地址改变
	 */
	private double nWatchValue=0;
	SKPlcNoticThread.IPlcNoticCallBack watchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			
			double value=getValue(nStatusValue);
			nWatchValue=value;
			//Log.d("SKScene", "nWatchValue:"+nWatchValue+",id:"+nItemId+",sid:"+nSceneId);
			updateView(value);
		}

	};
	
	
	
	/**
	 * 数据源-最大值
	 */
	SKPlcNoticThread.IPlcNoticCallBack maxCall=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			sMax=getValue(nStatusValue);
			//Log.d("SKScene", "sMax:"+sMax);
			updateView(nWatchValue);
		}
	};
	
	/**
	 * 数据源-最小值
	 */
    SKPlcNoticThread.IPlcNoticCallBack minCall=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			sMin=getValue(nStatusValue);
			//Log.d("SKScene", "sMin:"+sMin);
			updateView(nWatchValue);
		}
	};

	/**
	 * 报警下限
	 */
	SKPlcNoticThread.IPlcNoticCallBack alarmMinCall=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (mGrapInfo!=null) {
				mGrapInfo.setnMin(getValue(nStatusValue));
				updateView(nWatchValue);
			}
		}
	};
	
	/**
	 * 报警上限
	 */
   SKPlcNoticThread.IPlcNoticCallBack alarmMaxCall=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (mGrapInfo!=null) {
				mGrapInfo.setnMax(getValue(nStatusValue));
				updateView(nWatchValue);
			}
		}
	};
	
	
	/**
	 * 从plc读取值，并根据类型转换
	 */
	private Vector<Integer> mIData=null;
	private Vector<Long> mLData=null;
	private Vector<Short> mSData=null;
	private Vector<Float> mFData=null;
	private double getValue(Vector<Byte> nStatusValue){
		double value=0;
		boolean result;
		switch (mGrapInfo.geteDataType()) {
		case INT_16: // 16位整数
			if (mSData==null) {
				mSData=new Vector<Short>();
			}else {
				mSData.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue,mSData);
			if (!result||mSData.size()==0) {
				return 0;
			}
			value =mSData.get(0);
			break;
		case POSITIVE_INT_16: // 16位正整数
			if (mIData==null) {
				mIData=new Vector<Integer>();
			}else {
				mIData.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue,mIData);
			if (!result||mIData.size()==0) {
				return 0;
			}
			value = mIData.get(0);
			break;
		case INT_32: // 32位整数
			if (mIData==null) {
				mIData=new Vector<Integer>();
			}else {
				mIData.clear();
			}
			result= PlcRegCmnStcTools.bytesToInts(nStatusValue,mIData);
			if (!result||mIData.size()==0) {
				return 0;
			}
			value = mIData.get(0);
			break;
		case POSITIVE_INT_32: // 32位正整数
			if (mLData==null) {
				mLData=new Vector<Long>();
			}else {
				mLData.clear();
			}
			result= PlcRegCmnStcTools.bytesToUInts(nStatusValue,mLData);
			if (!result||mLData.size()==0) {
				return 0;
			}
			value = mLData.get(0);
			break;
		case FLOAT_32: // 浮点数
			if (mFData==null) {
				mFData=new Vector<Float>();
			}else {
				mFData.clear();
			}
			result= PlcRegCmnStcTools.bytesToFloats(nStatusValue,mFData);
			if (!result||mFData.size()==0) {
				return 0;
			}
			value = mFData.get(0);
			break;
		}
		return value;
	}
	
	/**
	 * 更新界面
	 */
	private void updateView(double value){
		if (mGrapInfo!=null) {
			//源范围
			if(value<=sMin){
				//源范围最大最小值相等
				value=0;
				if (value!=nStateValues) {
					nStateValues=value;
					init=false;
					//SKSceneManage.getInstance().onRefresh(items);
				}
			}else{
				if (value>sMax) {
					value=sMax;
				}
				if (sMax-sMin!=0) {
					if (mGrapInfo.isbAlarm()) {
						if (value<mGrapInfo.getnMin()||value>mGrapInfo.getnMax()) {
							alarm=true;
						}else {
							alarm=false;
						}
					}
					if (value-sMax==0) {
						if (mGrapInfo.getnShowMax()-mGrapInfo.getnShowMin()==0) {
							value=mGrapInfo.getnShowMax();
						}else {
							value=mGrapInfo.getnShowMax()-mGrapInfo.getnShowMin();
						}
					}else {
						value=((mGrapInfo.getnShowMax()-mGrapInfo.getnShowMin())
								/(sMax-sMin))*(value-sMin);
					}
					value=Math.abs(value);
					
					if (value!=nStateValues) {
						nStateValues=value;
						init=false;
					}
				}
			}
			SKSceneManage.getInstance().onRefresh(items);
		}
	}
	
	@Override
	public boolean isShow() {
		itemIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return show;
	}
	
	/**
	 * 控件是否可以显现
	 */
	private void itemIsShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(mGrapInfo.getmShowInfo());
		}
	}
	
	@Override
	public void realseMemeory() {
		SKThread.getInstance().getBinder().onDestroy(callback, sTaskName);
		SKPlcNoticThread.getInstance().destoryCallback(alarmMaxCall);
		SKPlcNoticThread.getInstance().destoryCallback(alarmMinCall);
		SKPlcNoticThread.getInstance().destoryCallback(maxCall);
		SKPlcNoticThread.getInstance().destoryCallback(minCall);
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(watchCall);
		
		//nStateValues=0;
		sTaskName="";
		init=true;
		alarm=false;
	}

	@Override
	public void updateStatus() {

	}

	@Override
	public void setDataToDatabase() {

	}
}
