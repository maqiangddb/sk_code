package com.android.Samkoonhmi.skgraphics.plc.show;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.XYCurveInfo;
import com.android.Samkoonhmi.model.sk_historytrends.ChannelGroupInfo;
import com.android.Samkoonhmi.plccommunicate.PlcRegCmnStcTools;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.LINE_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.show.base.SKGraphCmnShow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.LineTypeUtil;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

/**
 * XY 曲线
 */
public class AKXYCurve extends SKGraphCmnShow implements IItem{

	//控件id
	private int nItemId;
	//场景id
	private int nSid;
	//控件数据对象
	private XYCurveInfo mInfo;
	private Bitmap mBackBitmap;//背景图片
	private Paint mPaint;
	private double nVShowMax;//显示范围，水平方向，最大显示值
	private double nVShowMin;//显示范围，水平方向，最新显示值
	private double nHShowMax;//显示范围，垂直方向,最大显示值
	private double nHShowMin;//显示范围，垂直方向,最小显示值
	private double nVTargetMax;//源范围，水平方向，最大显示值
	private double nVTargetMin;//源范围，水平方向，最新显示值
	private double nHTargetMax;//源范围，垂直方向,最大显示值
	private double nHTargetMin;//源范围，垂直方向,最小显示值
	private boolean show; // 是否可显现
	private boolean showByAddr; // 是否注册显现地址
	private boolean showByUser; // 是否受用户权限控件
	private SKItems item;//刷新数据包
	//通道信息
	private ArrayList<AKXYChannel> mChannels=null;
	private boolean bRuning;//是否一直运行
	private int nShowData;//数据显示是否触发,0-不绘制，1-绘制，2-重新绘制
	private static final String TAG="AKXYCurve";
	private boolean isDraw;//是否绘制过一次数据
	private boolean isResetBack;//是否重新刷新背景
	
	/**
	 * @param id-控件id
	 * @param sid-场景id
	 * @param info-控件对象信息
	 */
	public AKXYCurve(int id,int sid,XYCurveInfo info){
		this.nItemId=id;
		this.nSid=sid;
		this.mInfo=info;
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		
		Rect rect=new Rect();
		rect.set(mInfo.getnTopLeftX(), mInfo.getnTopLeftY(),
				mInfo.getnTopLeftX()+mInfo.getnWidth(),
				mInfo.getnTopLeftY()+mInfo.getnHeight());
		
		item=new SKItems();
		item.itemId=nItemId;
		item.sceneId=nSid;
		item.nZvalue=mInfo.getnZvalue();
		item.nCollidindId=mInfo.getnCollidindId();
		item.rect=rect;
		item.mGraphics=this;
		
		nVShowMax=mInfo.getnYShowMax();
		nVShowMin=mInfo.getnYShowMin();
		nHShowMax=mInfo.getnXShowMax();
		nHShowMin=mInfo.getnXShowMin();
		
		if (mInfo.getnXShowType()==1) {
			//地址
			if (mInfo.getmXShowMaxAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmXShowMaxAddr(), showXMax, false,nSid);
			}
			
			if (mInfo.getmXShowMinAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmXShowMinAddr(), showXMin, false,nSid);
			}
			nHShowMax=0;
			nHShowMin=0;
		}
		
		if (mInfo.getnYShowType()==1) {
			//地址
			if (mInfo.getmYShowMaxAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmYShowMaxAddr(), showYMax, false,nSid);
			}
			
			if (mInfo.getmYShowMinAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmYShowMinAddr(), showYMin, false,nSid);
			}
			nVShowMax=0;
			nVShowMin=0;
		}
		
		if (!mInfo.isbScale()) {
			nVTargetMax=nVShowMax;
			nVTargetMin=nVShowMin;
			nHTargetMax=nHShowMax;
			nHTargetMin=nHShowMin;
		}else {
			nVTargetMax=mInfo.getnYTargetMax();
			nVTargetMin=mInfo.getnYTargetMin();
			nHTargetMax=mInfo.getnXTargetMax();
			nHTargetMin=mInfo.getnXTargetMin();
		}
		nShowData=0;
		bRuning=true;
		show=true;
		isDraw=false;
		isResetBack=false;
		
		// 显现权限
		if (info.getmShowInfo() != null) {
			if (info.getmShowInfo().getShowAddrProp() != null) {
				// 受地址控制
				showByAddr = true;
			}

			if (info.getmShowInfo().isbShowByUser()) {
				// 受用户权限控制
				showByUser = true;
			}
		}
		
		register();
		
		mChannels=new ArrayList<AKXYChannel>();
		//通道数
		if(mInfo.getmChannelList()!=null){
			for (int i = 0; i < mInfo.getmChannelList().size(); i++) {
				ChannelGroupInfo cinfo=mInfo.getmChannelList().get(i);
				AKXYChannel channel=new AKXYChannel(cinfo, mInfo.getnSampleCount(), sid, mInfo.geteDataType(),item);
				channel.setnHMax(nHShowMax);
				channel.setnHMin(nHShowMin);
				channel.setnVMax(nVShowMax);
				channel.setnVMin(nVShowMin);
				channel.setnVTargetMax(nVTargetMax);
				channel.setnVTargetMin(nVTargetMin);
				channel.setnHTargetMax(nHTargetMax);
				channel.setnHTargetMin(nHTargetMin);
				channel.setnWidth(mInfo.getnCurveWidth());
				channel.setnHeigth(mInfo.getnCurveHeight());
				channel.setDataChange(dataChange);
				channel.init();
				mChannels.add(channel);
			}
		}
		
	}
	
	
	/**
	 * 注册地址回调通知
	 */
	private void register(){
		
		//注册显现地址通知
		if (showByAddr) {
			ADDRTYPE addrType = mInfo.getmShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmShowInfo().getShowAddrProp(), showCall, true,nSid);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmShowInfo().getShowAddrProp(), showCall, false,nSid);
			}
		}
		
		//注册控制地址通知
		if (mInfo.getmControlAddr()!=null) {
			SKPlcNoticThread.getInstance().addNoticProp(
					mInfo.getmControlAddr(), control, true,nSid);
		}
		
		if (mInfo.isbScale()) {
			//源范围，X轴最大值
			if (mInfo.getmXTargetMaxAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmXTargetMaxAddr(),towardXMax, false,nSid);
			}
			
			//源范围，X轴最小值
			if (mInfo.getmXTargetMinAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmXTargetMinAddr(),towardXMin, false,nSid);
			}
			
			//源范围，Y轴最大值
			if (mInfo.getmYTargetMaxAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmYTargetMaxAddr(),towardYMax, false,nSid);
			}
			
			//源范围，Y轴最小值
			if (mInfo.getmYTargetMinAddr()!=null) {
				SKPlcNoticThread.getInstance().addNoticProp(
						mInfo.getmYTargetMinAddr(),towardYMin, false,nSid);
			}
		}
		
	}
	
	/**
	 * 显示控制
	 */
	@Override
	public boolean isShow() {
		itemIsShow();
		SKSceneManage.getInstance().onRefresh(item);
		return show;
	}
	
	/**
	 * 控件是否可以显现
	 */
	private void itemIsShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(mInfo.getmShowInfo());
		}
	}

	/**
	 * 获取数据库
	 */
	@Override
	public void getDataFromDatabase() {
		
	}

	/**
	 * 存储数据库
	 */
	@Override
	public void setDataToDatabase() {
		
	}

	/**
	 * 初始化控件
	 */
	@Override
	public void initGraphics() {
		
		itemIsShow();
		// 通知画面管理,调用控件的绘制方法
		SKSceneManage.getInstance().onRefresh(item);
	}

	/**
	 * 绘制控件
	 */
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (itemId==nItemId) {
			
			//Log.d("AKXYCurve", "show = "+show+", bShowData = "+nShowData);
			if (mInfo==null||!show) {
				return false;
			}
			
			//绘制背景
			drawBackground(canvas);
			
			if (nShowData!=0) {
				if (nShowData==2) {
					nShowData=0;
				}
				
				isDraw=true;
				//绘制实时数据
				if (mChannels!=null) {
					for (int i = 0; i < mChannels.size(); i++) {
						mChannels.get(i).draw(canvas, mInfo.getnTopLeftX()+mInfo.getnCurveX(),
								mInfo.getnTopLeftY()+mInfo.getnCurveY());
					}
				}
			}
			
		}
		return true;
	}

	/**
	 * 内存释放
	 */
	@Override
	public void realseMemeory() {
		
	}
	
	/**
	 * 绘制背景
	 * 包括刻度,网格,背景
	 */
	private void drawBackground(Canvas canvas){
		
		if (mBackBitmap==null||isResetBack) {
			isResetBack=false;
			mBackBitmap=Bitmap.createBitmap(mInfo.getnWidth(), mInfo.getnHeight(), Config.ARGB_8888);
			Canvas cc=new Canvas(mBackBitmap);
			cc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			//绘制刻度
			drawScale(cc);
			
			//绘制背景
			drawBackColor(cc);
			
			//绘制网格
			drawNet(cc);
			
		}
		//绘制背景图片
		canvas.drawBitmap(mBackBitmap, mInfo.getnTopLeftX(), mInfo.getnTopLeftY(), mPaint);
		
	}
	
	/**
	 * 绘制背景
	 */
	private void drawBackColor(Canvas canvas){
		
		Rect mRect=new Rect();
		int left=mInfo.getnCurveX();
		int top=mInfo.getnCurveY();
		int right=left+mInfo.getnCurveWidth();
		int bottom=top+mInfo.getnCurveHeight();
		
		mRect.set(left, top, right, bottom);
		RectItem item = new RectItem(mRect);
		//背景颜色
		item.setBackColor(mInfo.getnGraphColor());
		//边框颜色
		item.setLineColor(mInfo.getnBoradColor());
		item.draw(null, canvas);
		
	}
	
	/**
	 * 绘制网格
	 */
	private void drawNet(Canvas canvas){
		
		float left=0;
		float top=0;
		float nVLen=0;
		float nHLen=0;
		
		/**
		 * 显示网格
		 */
		if (mInfo.isbShowNet()) {
			
			/**
			 * 垂直线
			 * ——
			 * ——
			 * ——
			 */
			left=mInfo.getnCurveX();
			top=mInfo.getnCurveY();
			//垂直方向的间隔
			nVLen=((float)mInfo.getnCurveHeight())/mInfo.getnVMajorNum();
			
			if (mInfo.isbShowVMinorScale()) {
				//显示次刻度线
				float nMinorLen=nVLen/mInfo.getnVMinorNum();
				float nMinorTop=top;
				int size=mInfo.getnVMinorNum()*mInfo.getnVMajorNum();
				for (int j = 0; j < size; j++) {
					float[] pts=new float[4];
					pts[0]=(int)left;
					pts[1]=(int)nMinorTop;
					pts[2]=(int)left+mInfo.getnCurveWidth();
					pts[3]=(int)nMinorTop;
					nMinorTop+=nMinorLen;
					//绘制次刻度线
					if (j%mInfo.getnVMinorNum()!=0) {
						drawLine(canvas, pts, mInfo.getnVNetColor(), LINE_TYPE.DOT_LINE);
					}
				}
			}
			
			for (int i = 0; i < mInfo.getnVMajorNum(); i++) {
				//主刻度线
				float[] pts=new float[4];
				pts[0]=(int)left;
				pts[1]=(int)top;
				pts[2]=(int)left+mInfo.getnCurveWidth();
				pts[3]=(int)top;
				top+=nVLen;
				if (i>0) {
					//绘制主刻度线
					drawLine(canvas, pts, mInfo.getnVNetColor(), LINE_TYPE.SOLID_LINE);
				}
			}
			
			
			/**
			 * 水平线
			 * | | |
			 */
			//水平方向的间隔
			nHLen=((float)mInfo.getnCurveWidth())/mInfo.getnHMajorNum();
			left=mInfo.getnCurveX();
			top=mInfo.getnCurveY();
			
			if (mInfo.isbShowHMinorScale()) {
				//显示次刻度线
				float nMinorLen=nHLen/mInfo.getnHMinorNum();
				float nMinorLeft=left;
				int size=mInfo.getnHMinorNum()*mInfo.getnHMajorNum();
				for (int j = 0; j < size; j++) {
					float[] pts=new float[4];
					pts[0]=(int)nMinorLeft;
					pts[1]=(int)top;
					pts[2]=(int)nMinorLeft;
					pts[3]=(int)top+mInfo.getnCurveHeight();
					nMinorLeft+=nMinorLen;
					if (j%mInfo.getnHMinorNum()!=0) {
						//绘制次刻度线
						drawLine(canvas, pts, mInfo.getnHNetColor(), LINE_TYPE.DOT_LINE);
					}
				}
			}
			
			for (int i = 0; i < mInfo.getnHMajorNum(); i++) {
				//主刻度线
				float[] pts=new float[4];
				pts[0]=(int)left;
				pts[1]=(int)top;
				pts[2]=(int)left;
				pts[3]=(int)top+mInfo.getnCurveHeight();
				left+=nHLen;
				if (i>0) {
					//绘制主刻度线
					drawLine(canvas, pts, mInfo.getnHNetColor(), LINE_TYPE.SOLID_LINE);
				}
			}
		}
		
	}
	
	/**
	 * 绘制线条
	 */
	private void drawLine(Canvas canvas,float[] pts,int color,LINE_TYPE type){
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
		canvas.drawLines(pts, mLinePaint);
	}
	
	
	/**
	 * 绘制刻度
	 */
	private void drawScale(Canvas canvas){
		/**
		 * 垂直刻度
		 * 60.0——
		 * 30.0——
		 *  0.0——
		 */
		float nYLineLen = mInfo.getnCurveWidth() / 24;
		float nTop=mInfo.getnCurveY();
		Paint mFontPaint=new Paint();
		mFontPaint.setAntiAlias(true);
		mFontPaint.setStyle((Paint.Style.FILL_AND_STROKE));
		mFontPaint.setColor(mInfo.getnFontColor());
		mFontPaint.setTextSize(mInfo.getnFontSize());
		mFontPaint.setTextAlign(Align.RIGHT);
		//从右边开始画
		float tp=(float)(Math.abs(nVShowMax)>Math.abs(nVShowMin)?Math.abs(nVShowMax):Math.abs(nVShowMin));
		BigDecimal bd = new BigDecimal(tp).setScale(2, BigDecimal.ROUND_HALF_UP);
		float nfw=getFontWidth(bd+"", mFontPaint);
		float nPadding=(mInfo.getnCurveX()-nYLineLen-nfw)/2-2;
		if (nPadding<0) {
			nPadding=0;
		}
		float right=mInfo.getnCurveX()-nYLineLen-nPadding;
		if (mInfo.isbShowVScale()) {
			//显示垂直主刻度
			
			//先绘制文本
			nTop=nTop+getFontHeight(mFontPaint)/2-2;
			float nVLen=(float)mInfo.getnCurveHeight()/mInfo.getnVMajorNum();
			int size=mInfo.getnVMajorNum()+1;
			for (int i = 0; i < size; i++) {
				float temp=0;
				if (i==0) {
					temp=(float)nVShowMax;
				}else if (i==mInfo.getnVMajorNum()) {
					temp=(float)nVShowMin;
				}else{
					temp=(float)getRulerText(mInfo.getnVMajorNum()-i,nVShowMin,nVShowMax,size);
				}
				BigDecimal b = new BigDecimal(temp).setScale(2, BigDecimal.ROUND_HALF_UP);
				canvas.drawText(b+"", (int)right, (int)nTop, mFontPaint);
				nTop+=nVLen;
			}
			
			nTop=mInfo.getnCurveY();
			int length=mInfo.getnVMajorNum()+1;
			float left=mInfo.getnCurveX()-nYLineLen;
			for (int i = 0; i < length; i++) {
				//主刻度线
				float[] pts=new float[4];
				pts[0]=(int)left;
				pts[1]=(int)nTop;
				pts[2]=mInfo.getnCurveX();
				pts[3]=(int)nTop;
				nTop+=nVLen;
				//绘制主刻度线
				drawLine(canvas, pts, mInfo.getnScaleColor(), LINE_TYPE.SOLID_LINE);
			}
			
			float l=(mInfo.getnCurveX()-nYLineLen/2);
			length=mInfo.getnVMinorNum()*mInfo.getnVMajorNum();
			if (mInfo.isbShowVMinorScale()) {
				//显示次刻度线
				float nMinorLen=nVLen/mInfo.getnVMinorNum();
				float nMinorTop=mInfo.getnCurveY();
				for (int j = 0; j < length; j++) {
					float[] pt=new float[4];
					pt[0]=(int)l;
					pt[1]=(int)nMinorTop;
					pt[2]=mInfo.getnCurveX();
					pt[3]=(int)nMinorTop;
					nMinorTop+=nMinorLen;
					if (j%mInfo.getnVMinorNum()!=0){
						//绘制次刻度线
						drawLine(canvas, pt, mInfo.getnScaleColor(), LINE_TYPE.SOLID_LINE);
					}
				}
			}
			
		}
		
		
		/**
		 * 水平刻度
		 */
		float nXLineLen = mInfo.getnCurveHeight() / 15;
		float left=mInfo.getnCurveX();
		if(mInfo.isbShowHScale()){
			//先绘制文本
			float bLen=mInfo.getnHeight()-mInfo.getnCurveY()-mInfo.getnCurveHeight();
			nPadding=(bLen-nXLineLen)/2-2;
			if (nPadding<0) {
				nPadding=0;
			}
			float top=mInfo.getnCurveY()+mInfo.getnCurveHeight()+nPadding+nXLineLen+getFontHeight(mFontPaint)/2;
			float nHLen=(float)mInfo.getnCurveWidth()/mInfo.getnHMajorNum();
			int size= mInfo.getnHMajorNum()+1;
			float tt=(float)(Math.abs(nHShowMax)>Math.abs(nHShowMin)?Math.abs(nHShowMax):Math.abs(nHShowMin));
			left+=getFontWidth(tt+"", mFontPaint)/2;
			for (int i = 0; i <size; i++) {
				double temp=0;
				if (i==0) {
					temp=nHShowMin;
				}else if (i==mInfo.getnHMajorNum()) {
					temp=nHShowMax;
				}else{
					temp=getRulerText(i,nHShowMin,nHShowMax,size);
				}
				BigDecimal b = new BigDecimal(temp).setScale(2, BigDecimal.ROUND_HALF_UP);
				canvas.drawText(b+"", left, top, mFontPaint);
				left+=nHLen;
			}
			
			top=mInfo.getnCurveY()+mInfo.getnCurveHeight();
			left=mInfo.getnCurveX();
			if (mInfo.isbShowHMinorScale()) {
				//显示次刻度线
				float nMinorLen=nHLen/mInfo.getnHMinorNum();
				float nMinorLeft=left;
				size=mInfo.getnHMinorNum()*mInfo.getnHMajorNum();
				for (int j = 0; j < size; j++) {
					float[] pts=new float[4];
					pts[0]=(int)nMinorLeft;
					pts[1]=(int)top;
					pts[2]=(int)nMinorLeft;
					pts[3]=(int)(top+nXLineLen/2);
					nMinorLeft+=nMinorLen;
					if (j%mInfo.getnHMinorNum()!=0) {
						//绘制次刻度线
						drawLine(canvas, pts, mInfo.getnScaleColor(), LINE_TYPE.SOLID_LINE);
					}
				}
			}
			
			for (int i = 0; i < mInfo.getnHMajorNum()+1; i++) {
				//主刻度线
				float[] pts=new float[4];
				pts[0]=(int)left;
				pts[1]=(int)top;
				pts[2]=(int)left;
				pts[3]=(int)(top+nXLineLen);
				left+=nHLen;
				//绘制主刻度线
				drawLine(canvas, pts, mInfo.getnScaleColor(), LINE_TYPE.SOLID_LINE);
			}
			
		}
	}
	
	/**
	 * @param index -第几个刻度
	 * @param nMin -最小值
	 * @param nMan -最大值
	 * @param nMain -刻度数
	 */
	private double getRulerText(int index, double nMin, double nMax, int nMain) {
		double mValue = (nMax - nMin) / (nMain - 1);
		double dVal = 0;
		dVal = nMin + mValue * index;

		if (index == nMain - 1){
			dVal = nMax;
		}
			
		if (dVal == -0){
			dVal = 0;
		}

		return dVal;
	}

	
	/**
	 * 获取字体最宽的一行的宽度
	 * @param font-文本
	 * @param paint-已经设置大小的画笔
	 */
	private int getFontWidth(String font, Paint paint) {
		if (null != font) {
			return (int) paint.measureText(font);

		} else {
			return 0;
		}
	}
	
	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) (Math.ceil(fm.descent - fm.ascent));
	}

	
	/************** 注册地址通知 ***************/
	/**
	 * 显现地址改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}

	};
	
	
	/**
	 * 注册刷新控制地址
	 */
	SKPlcNoticThread.IPlcNoticCallBack control=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO 自动生成的方法存根
			if (nStatusValue!=null) {
				if (nStatusValue.get(0)==1) {
					nShowData=1;
					SKSceneManage.getInstance().onRefresh(item);
					if (mInfo.isbAutoReset()) {
						bRuning=false;
						writeBool(mInfo.getmControlAddr(), false);
					}
				}else {
					nShowData=0;
				}
			}
		}
	};
	
	/**
	 * X轴显示最大值
	 */
	SKPlcNoticThread.IPlcNoticCallBack showXMax=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0) {
					nHShowMax=change(nStatusValue);
					if (!mInfo.isbScale()) {
						//没有缩放
						nHTargetMax=nHShowMax;
					}
					update();
				}
			}
		}
	};
	
	/**
	 * X轴显示最小值
	 */
	SKPlcNoticThread.IPlcNoticCallBack showXMin=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0) {
					nHShowMin=change(nStatusValue);
					if (!mInfo.isbScale()) {
						//没有缩放
						nHTargetMin=nHShowMin;
					}
					update();
				}
			}
		}
	};
	
	/**
	 * Y轴显示最大值
	 */
	SKPlcNoticThread.IPlcNoticCallBack showYMax=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue!=null) {
				if(nStatusValue.size()>0){
					nVShowMax=change(nStatusValue);
					if (!mInfo.isbScale()) {
						//没有缩放
						nVTargetMax=nVShowMax;
					}
					update();
				}
			}
		}
	};
	
	/**
	 * Y轴显示最小值
	 */
	SKPlcNoticThread.IPlcNoticCallBack showYMin=new SKPlcNoticThread.IPlcNoticCallBack() {
		
		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0){
					nVShowMin=change(nStatusValue);
					if (!mInfo.isbScale()) {
						//没有缩放
						nVTargetMin=nVShowMin;
					}
					update();
				}
			}
		}
	};
	
	/**
	 * X轴源范围，最大值
	 */
	SKPlcNoticThread.IPlcNoticCallBack towardXMax=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0) {
					nHTargetMax=change(nStatusValue);
					//Log.d(TAG, "nHTargetMax = "+nHTargetMax);
					update();
				}
			}
		}
	};
	
	/**
	 * X轴源范围，最小值
	 */
	SKPlcNoticThread.IPlcNoticCallBack towardXMin=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0) {
					nHTargetMin=change(nStatusValue);
					//Log.d(TAG, "nHTargetMin = "+nHTargetMin);
					update();
				}
			}
		}
	};
	
	/**
	 * Y轴源范围，最大值
	 */
	SKPlcNoticThread.IPlcNoticCallBack towardYMax=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0) {
					nVTargetMax=change(nStatusValue);
					//Log.d(TAG, "nVTargetMax = "+nVTargetMax);
					update();
				}
			}
		}
	};
	
	/**
	 * Y轴源范围，最小值
	 */
	SKPlcNoticThread.IPlcNoticCallBack towardYMin=new SKPlcNoticThread.IPlcNoticCallBack(){

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			if (nStatusValue!=null) {
				if (nStatusValue.size()>0) {
					nVTargetMin=change(nStatusValue);
					//Log.d(TAG, "nVTargetMin = "+nVTargetMin);
					update();
				}
			}
		}
	};
	
	/**
	 * 通道数据发生变化
	 */
	IXYDataChange dataChange=new IXYDataChange() {
		
		@Override
		public void onChange() {
			// TODO 自动生成的方法存根
			if (show&&bRuning) {
				if (nShowData!=0) {
					SKSceneManage.getInstance().onRefresh(item);
				}
			}
		}
	};
	
	/**
	 * 更新通道的源范围
	 */
	private void update(){
		if(mChannels!=null){
			for (int i = 0; i < mChannels.size(); i++) {
				AKXYChannel channel=mChannels.get(i);
				channel.setnHMax(nHShowMax);
				channel.setnHMin(nHShowMin);
				channel.setnVMax(nVShowMax);
				channel.setnVMin(nVShowMin);
				
				channel.setnVTargetMax(nVTargetMax);
				channel.setnVTargetMin(nVTargetMin);
				channel.setnHTargetMax(nHTargetMax);
				channel.setnHTargetMin(nHTargetMin);
			}
			
			//范围发生改变，需要重新绘制背景
			isResetBack=true;
			
			if (nShowData==0) {
				if (isDraw) {
					//有绘制过数据，才需要重新，刷新界面
					//如果当前处于不绘制数据状态，由于范围变化，重新刷新一下界面
					nShowData=2;
				}
			}
			
			if (show) {
				//通知刷新
				SKSceneManage.getInstance().onRefresh(item);
			}
		}
	}
	
	/**
	 * XY曲线，通道数据发生变化
	 */
	public interface IXYDataChange{
		void onChange();
	}
	
	/**
	 * 写指定地址中的一个Bool数据
	 * */
	private static boolean writeBool(AddrProp addr, boolean data) {
		if (null == addr) {
			Log.e("ParamHelper", "writeBool: addr is null!");
			return false;
		}

		// 新建请求命令
		SEND_DATA_STRUCT QuestInfo = new SEND_DATA_STRUCT();
		QuestInfo.eDataType = DATA_TYPE.BIT_1; // 设置数据类型
		QuestInfo.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST; // 设置数据的排列模式
		QuestInfo.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_W;// 设置请求访问类型

		byte dataList[] = new byte[1];
		// 根据布尔值传入不同的参数值
		if (true == data) {
			dataList[0] = 1;
		} else {
			dataList[0] = 0;
		}
		return PlcRegCmnStcTools.setRegBytesData(addr, dataList, QuestInfo);
	}
	
	
	/**
	 * 监视地址值发生变化
	 */
	private Vector<Integer> mIDataList=null;
	private Vector<Short> mSDataList=null;
	private Vector<Long> mLDataList=null;
	private Vector<Float> mFDataList=null;
	private double change(Vector<Byte> nStatusValue){
		
		double v=0;
		if (mInfo.geteDataType()==null) {
			return v;
		}
		boolean result=false;
		
		switch (mInfo.geteDataType()) {
		case INT_16:
			//16位整数
			if (mSDataList==null) {
				mSDataList=new Vector<Short>();
			}else {
				mSDataList.clear();
			}
			result = PlcRegCmnStcTools.bytesToShorts(nStatusValue, mSDataList);
			if (result) {
				if (mSDataList.size()>0) {
					v=mSDataList.get(0);
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
				if (mIDataList.size()>0) {
					v=mIDataList.get(0);
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
				if (mIDataList.size()>0) {
					v=mIDataList.get(0);
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
				if (mLDataList.size()>0) {
					v=mLDataList.get(0);
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
				if (mIDataList.size()>0) {
					int nV=0;
					String s=DataTypeFormat.intToBcdStr((long) mIDataList.get(0), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nV=Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					v=nV;
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
				if (mLDataList.size()>0) {
					int nV=0;
					String s=DataTypeFormat.intToBcdStr((long) mLDataList.get(0), false);
					if (s!=null&&!s.equals("ERROR")) {
						try {
							nV=Integer.valueOf(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					v=nV;
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
				if (mFDataList.size()>0) {
					v=mFDataList.get(0);
				}
			}
			break;
		}
		
		return v;
	}

	
	/**
	 * 脚本对外接口
	 */
	@Override
	public IItem getIItem() {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public int getItemLeft(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnTopLeftX();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnTopLeftY();
		}
		return -1;
	}


	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnWidth();
		}
		return -1;
	}


	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return mInfo.getnHeight();
		}
		return -1;
	}


	@Override
	public short[] getItemForecolor(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public short[] getItemBackcolor(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return getColor(mInfo.getnGraphColor());
		}
		return null;
	}


	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			return getColor(mInfo.getnBoradColor());
		}
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
		
		if (mInfo != null) {
			if (x == mInfo.getnTopLeftX()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			mInfo.setnTopLeftX(x);
			int l=item.rect.left;
			item.rect.left=x;
			item.rect.right=x-l+item.rect.right;
			item.mMoveRect=new Rect();
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (mInfo != null) {
			if (y == mInfo.getnTopLeftY()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			mInfo.setnTopLeftY(y);
			int t = item.rect.top;
			item.rect.top = y;
			item.rect.bottom = y - t + item.rect.bottom;
			item.mMoveRect=new Rect();
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (mInfo != null) {
			if (w == mInfo.getnWidth()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			int temp=mInfo.getnWidth();
			mInfo.setnCurveWidth(mInfo.getnCurveWidth()+w-temp);
			mInfo.setnWidth(w);
			item.rect.right = w - item.rect.width() + item.rect.right;
			item.mMoveRect=new Rect();
			isResetBack=true;
			if(mChannels!=null){
				for (int i = 0; i < mChannels.size(); i++) {
					mChannels.get(i).setnWidth(mInfo.getnCurveWidth());
				}
			}
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (mInfo != null) {
			if (h == mInfo.getnHeight()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			int tmp=mInfo.getnHeight();
			mInfo.setnCurveHeight(mInfo.getnCurveHeight()+h-tmp);
			mInfo.setnHeight(h);
			item.rect.bottom = h - item.rect.height() + item.rect.bottom;
			item.mMoveRect=new Rect();
			isResetBack=true;
			if(mChannels!=null){
				for (int i = 0; i < mChannels.size(); i++) {
					mChannels.get(i).setnHeigth(mInfo.getnCurveHeight());
				}
			}
			SKSceneManage.getInstance().onRefresh(item);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			int color=Color.rgb(r, g, b);
			if (color==mInfo.getnGraphColor()) {
				return true;
			}
			mInfo.setnGraphColor(color);
			isResetBack=true;
			SKSceneManage.getInstance().onRefresh(item);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (mInfo!=null) {
			int color=Color.rgb(r, g, b);
			if (color==mInfo.getnBoradColor()) {
				return true;
			}
			mInfo.setnBoradColor(color);
			isResetBack=true;
			SKSceneManage.getInstance().onRefresh(item);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==show) {
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(item);
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
		// TODO Auto-generated method stub
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
