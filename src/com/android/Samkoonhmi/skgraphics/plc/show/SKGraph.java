package com.android.Samkoonhmi.skgraphics.plc.show;
import java.util.Vector;

import android.R.integer;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import com.android.Samkoonhmi.model.GraphBaseInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.GOTO_TYPE;
import com.android.Samkoonhmi.skenum.Graph.GRAPH_TYPE;
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
public class SKGraph extends SKGraphCmnShow implements ITimerUpdate ,IItem {

	private GraphBaseInfo mGrapInfo;
	private SKGraphShape shape;
	private int nItemId;
	private int nSceneId;
	private SKItems items;
	private double nStateValues;
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
		this.init=true;
		this.alarm=false;
		items=new SKItems();
		this.mGrapInfo=info;
		show = true;
		
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
			this.alarm=false;
			
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
			
			if (mGrapInfo.getnSourceRang()==0) {
				sMax=mGrapInfo.getnSourceMax();
				sMin=mGrapInfo.getnSourceMin();
			}else {
				sMax=0;
				sMin=0;
			}
			
			//注册通知
			registNotice();
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
	
		init=true;
		
		//显现
		itemIsShow();  
		
		SKSceneManage.getInstance().onRefresh(items);
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
					init=false;
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
	
				
		//注册显现地址
		if (showByAddr) {
			ADDRTYPE addrType = mGrapInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(mGrapInfo.getmShowInfo().getShowAddrProp(), showCall,true,nSceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(mGrapInfo.getmShowInfo().getShowAddrProp(), showCall,false,nSceneId);
			}

		}
		
		/**
		 * 注册源范围监视地址
		 */
		if (mGrapInfo.getnSourceRang()==1) {
			
			//源范围-最小值
			if (mGrapInfo.getmMinAddrProp()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mGrapInfo.getmMinAddrProp(), minCall, false,nSceneId);
			}
			
			//源范围-最大值
			if (mGrapInfo.getmMaxAddrProp()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mGrapInfo.getmMaxAddrProp(), maxCall, false,nSceneId);
			}
		}
		
		/**
		 * 注册报警监视地址
		 */
		if (mGrapInfo.isbAlarm()) {
			if (mGrapInfo.getnType()==1) {
				if (mGrapInfo.getmAlarmMinAddr()!=null) {
					SKPlcNoticThread.getInstance().addNoticProp(
							mGrapInfo.getmAlarmMinAddr(), alarmMinCall, false,nSceneId);
				}
				
				if (mGrapInfo.getmAlarmMaxAddr()!=null) {
					SKPlcNoticThread.getInstance().addNoticProp(
							mGrapInfo.getmAlarmMaxAddr(), alarmMaxCall, false,nSceneId);
				}
			}
		}
		
		// 注册监视地址
		if (mGrapInfo.getmAddress() != null) {
			SKPlcNoticThread.getInstance().addNoticProp(
					mGrapInfo.getmAddress(), watchCall, false,nSceneId);
		}
		
	}
	
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
				//value=0;
				if (value!=nStateValues) {
					nStateValues=0;
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
		
	}

	@Override
	public void updateStatus() {

	}

	@Override
	public void setDataToDatabase() {

	}
	/**
	 * 获取控件属性接口
	 */

	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if(mGrapInfo!=null){
			return mGrapInfo.getnLeftTopX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if(mGrapInfo!=null){
			return mGrapInfo.getnLeftTopY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if(mGrapInfo!=null){
			return mGrapInfo.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if(mGrapInfo!=null){
			return mGrapInfo.getnHeigth();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		if (mGrapInfo!=null) {
			return getColor(mGrapInfo.getnDesignColor());
		}
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		if(mGrapInfo!=null){
			return getColor(mGrapInfo.getnBackColor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getItemVisible(int id) {
		// TODO Auto-generated method stub
		return show;
	}

	@Override
	public boolean getItemTouchable(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		if (mGrapInfo!= null) {
			if (x == mGrapInfo.getnLeftTopX()) {
				return true;
			}
			if (x < 0
					|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int len=x-mGrapInfo.getnLeftTopX();
			
			if (mGrapInfo.geteGraphType()==GRAPH_TYPE.COMMON) {
				/*普通图形*/
				
				//控件所在位置
				mGrapInfo.setnLeftTopX((short) x);
				//显示位置
				mGrapInfo.setnShowLeftTopX((short)(mGrapInfo.getnShowLeftTopX()+len));
				//刻度显示位置
				if (mGrapInfo.isHasRuler()) {
					mGrapInfo.setnRulerLeftTopX((short)(mGrapInfo.getnRulerLeftTopX()+len));
				}
			}else if (mGrapInfo.geteGraphType()==GRAPH_TYPE.METER) {
				/*仪表*/
				
				//控件所在位置
				mGrapInfo.setnLeftTopX((short) x);
				//显示位置
				mGrapInfo.setnShowLeftTopX((short)(mGrapInfo.getnShowLeftTopX()+len));
			}
			
			items.rect.left=items.rect.left+len;
			items.rect.right=items.rect.right+len;
			items.mMoveRect = new Rect();
			init=true;
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (mGrapInfo!= null) {
			if (y == mGrapInfo.getnLeftTopY()) {
				return true;
			}
			if (y < 0
					|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int len=y-mGrapInfo.getnLeftTopY();
			if (mGrapInfo.geteGraphType()==GRAPH_TYPE.COMMON){
				/*普通图形*/
				//控件所在位置
				mGrapInfo.setnLeftTopY((short) y);
				//显示位置
				mGrapInfo.setnShowLeftTopY((short)(mGrapInfo.getnShowLeftTopY()+len));
				//刻度显示位置
				if (mGrapInfo.isHasRuler()) {
					mGrapInfo.setnRulerLeftTopY((short)(mGrapInfo.getnRulerLeftTopY()+len));
				}
			}else if (mGrapInfo.geteGraphType()==GRAPH_TYPE.METER) {
				/*仪表*/
				//控件所在位置
				mGrapInfo.setnLeftTopY((short) y);
				//显示位置
				mGrapInfo.setnShowLeftTopY((short)(mGrapInfo.getnShowLeftTopY()+len));
			}
			items.rect.top = items.rect.top+len;
			items.rect.bottom = items.rect.bottom+len;
			items.mMoveRect = new Rect();
			init=true;
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (mGrapInfo!= null) {
			if (w == mGrapInfo.getnWidth()) {
				return true;
			}
			if (w < 0
					|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int len=w-mGrapInfo.getnWidth();
			int l=len;
			//控件所在位置
			mGrapInfo.setnWidth((short)w);
			if (mGrapInfo.geteGraphType()==GRAPH_TYPE.COMMON){
				/*普通图形*/
				if (mGrapInfo.isHasRuler()) {
					if (mGrapInfo.geteDirection()==DIRECTION.TOWARD_TOP||
							mGrapInfo.geteDirection()==DIRECTION.TOWARD_BOTTOM) {
						l=l/2;
						mGrapInfo.setnShowLeftTopX((short)(mGrapInfo.getnShowLeftTopX()+l));
					}
				}
				//显示位置
				mGrapInfo.setnShowWidth((short)(mGrapInfo.getnShowWidth()+l));
				//刻度显示位置
				if (mGrapInfo.isHasRuler()) {
					mGrapInfo.setnRulerWidth((short)(mGrapInfo.getnRulerWidth()+l));
				}
			}else if (mGrapInfo.geteGraphType()==GRAPH_TYPE.METER) {
				/*仪表*/
				float temp=len/(mGrapInfo.getnShowWidth()+mGrapInfo.getnRulerWidth());
				Log.d("SKGraph", "widht temp="+temp+",len="+len+",show width="+mGrapInfo.getnShowWidth()+",ruler width="+mGrapInfo.getnRulerWidth());
				//显示位置
				mGrapInfo.setnShowWidth((short)(mGrapInfo.getnShowWidth()*(1+temp)));
				//刻度显示位置
				if (mGrapInfo.isHasRuler()) {
					mGrapInfo.setnRulerWidth((short)(mGrapInfo.getnRulerWidth()*(1+temp)));
				}
			}
			
			items.rect.right = len + items.rect.right;
			items.mMoveRect = new Rect();
			init=true;
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (mGrapInfo!= null) {
			if (h == mGrapInfo.getnHeigth()) {
				return true;
			}
			if (h < 0
					|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int len=h-mGrapInfo.getnHeigth();
			int l=len;
			//控件所在位置
			mGrapInfo.setnHeigth((short) h);
			if (mGrapInfo.geteGraphType()==GRAPH_TYPE.COMMON){
				/*普通图形*/
				
				if (mGrapInfo.isHasRuler()) {
					if (mGrapInfo.isHasRuler()) {
						if (mGrapInfo.geteDirection()==DIRECTION.TOWARD_LEFT||
								mGrapInfo.geteDirection()==DIRECTION.TOWARD_RIGHT) {
							l=l/2;
							mGrapInfo.setnShowLeftTopY((short)(mGrapInfo.getnShowLeftTopY()+l));
						}
					}
				}
				//显示位置
				mGrapInfo.setnShowHigth((short)(mGrapInfo.getnShowHigth()+l));
				//刻度显示位置
				if (mGrapInfo.isHasRuler()) {
					mGrapInfo.setnRulerHigth((short)(mGrapInfo.getnRulerHigth()+l));
				}
				
			}else if (mGrapInfo.geteGraphType()==GRAPH_TYPE.METER) {
				/*仪表*/
				float temp=len/(mGrapInfo.getnShowHigth()+mGrapInfo.getnRulerHigth());
				Log.d("SKGraph", "height temp="+temp);
				//显示位置
				mGrapInfo.setnShowHigth((short)(mGrapInfo.getnShowHigth()*(1+temp)));
				//刻度显示位置
				if (mGrapInfo.isHasRuler()) {
					mGrapInfo.setnRulerHigth((short)(mGrapInfo.getnRulerHigth()*(1+temp)));
				}
			}
			
			items.rect.bottom = len + items.rect.bottom;
			items.mMoveRect = new Rect();
			init=true;
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mGrapInfo==null) {
			return false;
		}
		int color=Color.rgb(r, g, b);
		if (mGrapInfo.getnDesignColor()==color) {
			return true;
		}
		mGrapInfo.setnDesignColor(color);
		init=true;
		return true;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		
		if (mGrapInfo==null) {
			return false;
		}
		int color=Color.rgb(r, g, b);
		if (mGrapInfo.getnBackColor()==color) {
			return true;
		}
		init=true;
		mGrapInfo.setnBackColor(color);
		return true;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if(v==show){
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemPageUp(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemPageDown(int id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemFlick(int id, boolean v, int time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemHroll(int id, int w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemText(int id, int lid, String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO Auto-generated method stub nPointerType
		return false;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * 颜色取反
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
}
