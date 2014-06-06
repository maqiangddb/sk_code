package com.android.Samkoonhmi.skgraphics.plc.show;

import java.util.Vector;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.Log;

import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.HISTORYSHOW_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.AKXYCurve.IXYDataChange;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.LineTypeUtil;

/**
 * XY曲线通道
 */
public class AKXYChannel {

	private final static String TAG="AKXYChannel";
	//显示范围，垂直方向最大值
	private double nVMax;
	//显示范围，垂直方向最小值
	private double nVMin;
	//显示范围，水平方向最大值
	private double nHMax;
	//显示范围，水平方向最小值
	private double nHMin;
	//源范围,垂直方向最大值
	private double nVTargetMax;
	//源范围,垂直方向最小值
	private double nVTargetMin;
	//源范围,水平方向最大值
	private double nHTargetMax;
	//源范围,水平方向最小值
	private double nHTargetMin;
	//宽
	private int nWidth;
	//高
	private int nHeigth;
	//垂直方向显示与源范围比例
	private double nVRatio=1;
	//水平方向显示与源范围比例
	private double nHRatio=1;
	//采样点
	private int nAddrLen;
	//x轴 地址数据
	private double[] nXData=null;
	//y轴 地址数据
	private double[] nYData=null;
	private ChannelGroupInfo mInfo=null;
	private int nSid;//场景id
	private DATA_TYPE eData_TYPE=null;//地址数据类型
	private boolean bShow=true;//是否显示
	private SKItems mItem;//刷新包信息
	//通道数据发生变化
	private IXYDataChange dataChange;
	private boolean bScaleChange;
	
	
	/**
	 * @param info-通道信息
	 * @param count-采集点数
	 * @param sid-场景id
	 * @param type-地址数据类型
	 * @param items-刷新包信息
	 */
	public AKXYChannel(ChannelGroupInfo info,int count,int sid,DATA_TYPE type,SKItems items){
		this.mInfo=info;
		this.nAddrLen=count;
		this.nXData=new double[count];
		this.nYData=new double[count];
		this.nSid=sid;
		this.eData_TYPE=type;
		this.mItem=items;
		
		for (int i = 0; i < count; i++) {
			nXData[i]=0;
			nYData[i]=0;
		}
		
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		
		if (mInfo.getmXAddrProp()!=null) {
			SKPlcNoticThread.getInstance().addNoticProp(
					mInfo.getmXAddrProp(), xCall, false,nSid);
		}
		
		if(mInfo.getmYAddrProp()!=null){
			SKPlcNoticThread.getInstance().addNoticProp(
					mInfo.getmYAddrProp(), yCall, false,nSid);
		}
		
		if (mInfo.getnDisplayCondition()!=HISTORYSHOW_TYPE.ALWAYS_SHOW) {
			bShow=false;
			if(mInfo.getnDisplayAddr()!=null){
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getnDisplayAddr(), show, true,nSid);
			}
		}else {
			bShow=true;
		}
		
	}
	
	/**
	 * 绘制
	 * @param canvas-画布
	 * @param left-左顶点X坐标
	 * @param top-左顶点Y坐标
	 */
	public void draw(Canvas canvas,int left,int top) {
		
		if (bShow) {
			
			if (nAddrLen<0) {
				return;
			}
			
			float xs=(float)(Math.abs(nHMax-nHMin)/nWidth);
			float ys=(float)(Math.abs(nVMax-nVMin)/nHeigth);
			
			if (nAddrLen==1) {
				float[] pts=new float[2];
				pts[0]=(int)getValues(nXData[0], true,xs,left,top);
				pts[1]=(int)getValues(nYData[0], false,xs,left,top);
				drawPoint(canvas,pts,mInfo.getnDisplayColor());
			}else {
				for (int i = 0; i < nAddrLen-1; i++) {
					int x1=getValues(nXData[i],true,xs,left,top);
					int y1=getValues(nYData[i], false,ys,left,top);
					int x2=getValues(nXData[i+1], true,xs,left,top);
					int y2=getValues(nYData[i+1],false,ys,left,top);
					//Log.d(TAG, "x1 = "+x1+", y1 = "+y1 +", x2 = "+x2+", y2 ="+y2+",left = "+left+",top = "+top);
					drawLine(canvas,x1,y1,x2,y2,mInfo.getnDisplayColor(),mInfo.getnLineType());
				}
			}
		}
	}
	
	/**
	 * 根据XY曲线源范围，获取对应的值
	 */
	private int getValues(double v,boolean isX,double salce,int left,int top){
		int result=0;
		scale();
		
		if (isX) {
			//x轴
			
			result=(int)((v*nHRatio-nHMin)/salce+left);
			
			if (result<=left) {
				result=left+1;//画笔的宽度
			}
			if (result>=(left+nWidth)) {
				result=left+nWidth-1;
			}
			
		}else{
			//y轴
			
			result=(int)(nHeigth-(v*nVRatio-nVMin)/salce+top);
			if (result<=top) {
				result=top+1;//画笔的宽度
			}
			
			if (result>=(top+nHeigth)) {
				result=top+nHeigth-1;
			}
			
		}
		return (int)result;
	}
	
	/**
	 * 比例计算
	 */
	private void scale(){
		if (bScaleChange) {
			bScaleChange=false;
			if (nVTargetMax-nVTargetMin!=0) {
				nVRatio=Math.abs(nVMax-nVMin)/Math.abs(nVTargetMax-nVTargetMin);
			}else {
				nVRatio=0;
			}
			
			if (nHTargetMax-nHTargetMin!=0) {
				nHRatio=Math.abs(nHMax-nHMin)/Math.abs(nHTargetMax-nHTargetMin);
			}else {
				nHRatio=0;
			}
		}
	}
	
	/**
	 * 绘制线条
	 * @param canvas-画布
	 * @param x1-起始点X坐标
	 * @param y1-起始点Y坐标
	 * @param x2-结束点X坐标
	 * @param y2-结束点Y坐标
	 * @param color-线颜色
	 * @param type-线类型
	 */
	private void drawLine(Canvas canvas,int x1,int y1,int x2,int y2,int color,LINE_TYPE type){
		Paint mLinePaint=new Paint();
		// 去锯齿
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		// 线的颜色
		mLinePaint.setColor(color); 
		// 线宽度
		mLinePaint.setStrokeWidth(1);
		PathEffect effect = LineTypeUtil.getPathEffect(type,1);
		// 设置线的样式
		mLinePaint.setPathEffect(effect);
		// 绘制线条
		canvas.drawLine(x1, y1, x2, y2, mLinePaint);
	}
	
	/**
	 * 绘制点
	 * @param canvas-画布
	 * @param pts-点坐标
	 * @param color-点颜色
	 */
	private void drawPoint(Canvas canvas,float[] pts,int color){
		Paint mLinePaint=new Paint();
		// 去锯齿
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Paint.Style.STROKE);
		// 线的颜色
		mLinePaint.setColor(color); 
		// 线宽度
		mLinePaint.setStrokeWidth(1);
		// 绘制线条
		canvas.drawPoint(pts[0], pts[1], mLinePaint);
	}
	
	/**
	 * X轴，数据回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack xCall=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				change(nStatusValue,nXData,true);
				
				if (bShow) {
					//数据发生变化，通知界面刷新
					if (dataChange!=null) {
						dataChange.onChange();
					}
				}
			}
		}
	};
	
	/**
	 * Y轴，数据回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack yCall=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				change(nStatusValue,nYData,false);
				
				if (bShow) {
					//数据发生变化，通知界面刷新
					if (dataChange!=null) {
						dataChange.onChange();
					}
				}
			}
		}
	};
	
	/**
	 * 监视地址值发生变化
	 */
	private Vector<Integer> mIDataList=null;
	private Vector<Short> mSDataList=null;
	private Vector<Long> mLDataList=null;
	private Vector<Float> mFDataList=null;
	private void change(Vector<Byte> nStatusValue,double[] data,boolean isX){
		
		if (eData_TYPE==null) {
			return;
		}
		boolean result=false;
		
		switch (eData_TYPE) {
		case INT_16:
			//16位整数
			if (mSDataList==null) {
				mSDataList=new Vector<Short>();
			}else {
				mSDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSDataList);
			if (result) {
				if (mSDataList.size()==nAddrLen) {
					for (int i = 0; i < mSDataList.size(); i++) {
						short value=mSDataList.get(i);
						data[i]=value;
					}
				}
			}
			break;
		case POSITIVE_INT_16:
			//16正整数
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				for (int i = 0; i < mIDataList.size(); i++) {
					int value=mIDataList.get(i);
					data[i]=value;
				}
			}
			break;
		case INT_32:
			//32整数
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToInts(nStatusValue, mIDataList);
			if (result) {
				for (int i = 0; i < mIDataList.size(); i++) {
					int value=mIDataList.get(i);
					data[i]=value;
				}
			}
			break;
		case POSITIVE_INT_32: 
			// 32位正整数
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				for (int i = 0; i < mLDataList.size(); i++) {
					long value=mLDataList.get(i);
					data[i]=value;
				}
			}
			break;
		case BCD_16:
			// 调用BCD码转换
			if (mIDataList==null) {
				mIDataList=new Vector<Integer>();
			}else {
				mIDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUShorts(nStatusValue, mIDataList);
			if (result) {
				for (int i = 0; i < mIDataList.size(); i++) {
					int nV=0;
					String s=DataTypeFormat.intToBcdStr((long) mIDataList.get(i), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nV=Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					data[i]=nV;
				}
			}
			break;
		case BCD_32:
			// 调用BCD码转换
			if (mLDataList==null) {
				mLDataList=new Vector<Long>();
			}else {
				mLDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToUInts(nStatusValue, mLDataList);
			if (result) {
				for (int i = 0; i < mLDataList.size(); i++) {
					int nV=0;
					String s=DataTypeFormat.intToBcdStr((long) mLDataList.get(i), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nV=Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					data[i]=nV;
				}
			}
			break;
		case FLOAT_32: 
			// 浮点数
			if (mFDataList==null) {
				mFDataList=new Vector<Float>();
			}else {
				mFDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToFloats(nStatusValue, mFDataList);
			if (result) {
				for (int i = 0; i <mFDataList.size(); i++) {
					float value=mFDataList.get(i);
					data[i]=value;
				}
			}
			break;
		}
		
	}
	
	
	/**
	 * 曲线是否显示
	 */
	SKPlcNoticThread.IPlcNoticCallBack show=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				if(nStatusValue.size()>0){
					int value = nStatusValue.get(0);
					if (mInfo.getnDisplayCondition()==HISTORYSHOW_TYPE.ON_SHOW) {
						if (value==1) {
							bShow=true;
						}else {
							bShow=false;
						}
					}else if (mInfo.getnDisplayCondition()==HISTORYSHOW_TYPE.OFF_SHOW) {
						if (value==1) {
							bShow=false;
						}else {
							bShow=true;
						}
					}
					//通知画面刷新
					SKSceneManage.getInstance().onRefresh(mItem);
				}
			}
		}
	};
	

	public void setnVMax(double nVMax) {
		this.nVMax = nVMax;
		bScaleChange=true;
	}

	public void setnVMin(double nVMin) {
		this.nVMin = nVMin;
		bScaleChange=true;
	}

	public void setnHMax(double nHMax) {
		this.nHMax = nHMax;
		bScaleChange=true;
	}

	public void setnHMin(double nHMin) {
		this.nHMin = nHMin;
		bScaleChange=true;
	}

	public void setnAddrLen(int nAddrLen) {
		this.nAddrLen = nAddrLen;
	}

	public void seteData_TYPE(DATA_TYPE eData_TYPE) {
		this.eData_TYPE = eData_TYPE;
	}

	public void setnWidth(int nWidth) {
		this.nWidth = nWidth;
	}

	public void setnHeigth(int nHeigth) {
		this.nHeigth = nHeigth;
	}
	
	public void setDataChange(IXYDataChange dataChange) {
		this.dataChange = dataChange;
	}
	
	public void setnVTargetMax(double nVTargetMax) {
		this.nVTargetMax = nVTargetMax;
		bScaleChange=true;
	}

	public void setnVTargetMin(double nVTargetMin) {
		this.nVTargetMin = nVTargetMin;
		bScaleChange=true;
	}

	public void setnHTargetMax(double nHTargetMax) {
		this.nHTargetMax = nHTargetMax;
		bScaleChange=true;
	}

	public void setnHTargetMin(double nHTargetMin) {
		this.nHTargetMin = nHTargetMin;
		bScaleChange=true;
	}
}
