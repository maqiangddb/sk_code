package com.android.Samkoonhmi.skgraphics.plc.show;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.util.Log;

import com.android.Samkoonhmi.graphicsdrawframe.ArcRulerItem;
import com.android.Samkoonhmi.graphicsdrawframe.ArcRulerItem.ArcType;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.RulerItem;
import com.android.Samkoonhmi.model.CommonGraphInfo;
import com.android.Samkoonhmi.model.GraphBaseInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.StatisticsGraphInfo;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.GLOBAL_POS_PROP;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.util.FillRender;
import com.android.Samkoonhmi.util.GRAPH_SHAPE_TYPE;
import com.android.Samkoonhmi.util.ImageFileTool;

/**
 * 图表-形状
 * 
 * @author 刘伟江
 * @version v1.0.0.1 创建时间 2012-4-23 最后修改时间 2012-5-3
 * 
 */
public class SKGraphShape {

	private static final String TAG = "SKScene";
	private GraphBaseInfo mGraphInfo;
	private Paint mPaint;
	private Bitmap mBitmap = null;
	private Canvas mCanvas;
	private boolean reset;
	private Rect pRect;
	private boolean alram;
	private Bitmap mBgBitmap;
	private Paint mBitmapPaint;//图片专用

	public SKGraphShape(GraphBaseInfo info) {
		mGraphInfo = info;
		mPaint = new Paint();
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
	}

	/**
	 * 初始化-图形
	 * 
	 * @param init-true,表示初始化,false 状态更新
	 * @param status,plc 实时状态值
	 * @param alram-true 报警
	 */
	public synchronized void drawShape(boolean init, float status, Canvas canvas,boolean alram) {
		this.alram=alram;
		if (mGraphInfo != null) {
			switch (mGraphInfo.geteGraphType()) {
			case COMMON: // 普通
				commonGraph((CommonGraphInfo) mGraphInfo, init, status,canvas);
				break;
			case STATISTICS: // 统计
				//statisticsGraph((StatisticsGraphInfo) mGraphInfo, init,(int)status);
				break;
			case METER: // 仪表
				meterGraph(mGraphInfo, init, status,canvas);
				break;
			}
		}
	}
	
	/**
	 * 图表类型-普通
	 * @param init,是否初始化,true-初始化,false-状态更新
	 * @param status,plc 实时状态值
	 */
	private void commonGraph(CommonGraphInfo info, boolean init, float status,Canvas canvas) {
		switch (info.geteShapeType()) {
		case PILLA: // 柱状图
			drawPillaShape(info, init, status,canvas);
			break;
		case CIRCLE: // 圆
			drawCircleShape(info, init, status,canvas);
			break;
		case SECTOR: // 半圆
			drawSectorShape(info, init, status,canvas);
			break;
		case GROOVE: // 槽
			drawGrooveShape(info, init, status,canvas);
			break;
		}
	}

	/**
	 * 图表类型-统计
	 * 
	 * @param init
	 *            ,是否初始化,true-初始化,false-状态更新
	 * @param status
	 *            ,plc 实时状态值
	 */
	private void statisticsGraph(StatisticsGraphInfo info, boolean init,
			int status) {
		switch (info.geteShapeType()) {
		case PILLA: // 柱状图
			drawPillaShape(info, init, status);
			break;
		case CIRCLE: // 圆
			drawCircleShape(info, init, status);
			break;
		}
	}

	/**
	 * 图表类型-仪表
	 * @param init,是否初始化,true-初始化,false-状态更新
	 * @param status,plc 实时状态值
	 */
	private void meterGraph(GraphBaseInfo info, boolean init, float status,Canvas canvas) {
		drawMeterShare(info, init, status,canvas);
	}

	/**
	 * 调整标尺的方向
	 */
	private void changeRulerDirection(GraphBaseInfo info,RulerItem item) {

		//刻度方向，相对柱状图向外的，是左，相对状图向内是右
		rDir = info.geteRulerDirection();
		switch (info.geteDirection()) {
		case TOWARD_LEFT:
			if (rDir==DIRECTION.TOWARD_LEFT) {
				rDir=DIRECTION.TOWARD_BOTTOM;
				nRulerDir=0;
			}else {
				rDir=DIRECTION.TOWARD_TOP;
				nRulerDir=0;
				rItem.setnIncrease(1);
			}
			break;
		case TOWARD_RIGHT:
			if (rDir==DIRECTION.TOWARD_LEFT) {
				rDir=DIRECTION.TOWARD_TOP;
				nRulerDir=0;
			}else {
				rDir=DIRECTION.TOWARD_BOTTOM;
				nRulerDir=0;
				rItem.setnIncrease(1);
			}
			break;
		case TOWARD_TOP:
			if (rDir==DIRECTION.TOWARD_LEFT) {
				nRulerDir=0;
			}else {
				nRulerDir=0;
				rDir=DIRECTION.TOWARD_RIGHT;
				rItem.setnIncrease(1);
			}
			break;
		case TOWARD_BOTTOM:
			if (rDir==DIRECTION.TOWARD_LEFT) {
				rDir=DIRECTION.TOWARD_RIGHT;
				nRulerDir=0;
			}else {
				rDir=DIRECTION.TOWARD_LEFT;
				rItem.setnIncrease(1);
				nRulerDir=0;
			}
			break;
		}
	}

	/**
	 * 柱状图
	 * 
	 * @param info--普通图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param status,plc 实时状态值
	 */
	private RectItem sItem;// 外边框矩形
	private RectItem dItem;// 动态显示
	private Rect mRect;
	private Rect dRect;
	private RulerItem rItem;
	private DIRECTION rDir;
	private boolean draw;
	private int nRulerDir;
	private int nPreState;
	private void drawPillaShape(CommonGraphInfo info, boolean init, float status,Canvas canvas) {

		// 动态数据显示位置，左上右下
		int mLeft = 0, mTop = 0, mRight = 0, mBottom = 0;

		if (init) {
			
			nPreState=0;
			// 背景
			resetPillShape(info);
			if (mRect==null) {
				mRect = new Rect();
			}
			if (sItem==null) {
				sItem = new RectItem(mRect);
			}
			if (dRect==null) {
				dRect = new Rect();
			}
			if (dItem==null) {
				dItem = new RectItem(dRect);
			}
			
			rItem = new RulerItem(null);

			mRect.set(info.getnShowLeftTopX(), info.getnShowLeftTopY(),
					info.getnShowLeftTopX() + info.getnShowWidth(),
					info.getnShowLeftTopY() + info.getnShowHigth());

			sItem.setLineWidth(0);
			
			// 动态区域背景
			sItem.setBackColor(info.getnBackColor());

			// 标尺
			if (info.isHasRuler()) {
				rItem.setLineColor(info.getnRulingColor());
				rItem.setnMainRuling(info.getnMainRuling());
				rItem.setnRuling(info.getnRuling());
				rItem.setWidth(info.getnRulerWidth());
				rItem.setHeight(info.getnRulerHigth());
				rItem.setLineWidth(nLinePadding);
				rItem.setbShowMinorRuling(info.isbShowRuling());
				rItem.setnLTX(info.getnRulerLeftTopX());
				rItem.setnLTY(info.getnRulerLeftTopY());
				rItem.setnItemX(info.getnLeftTopX());
				rItem.setnItemY(info.getnLeftTopY());
				rItem.setnItemRX(info.getnLeftTopX()+info.getnWidth());
				rItem.setnItemRY(info.getnLeftTopY()+info.getnHeigth());
				rItem.setnMin(info.getnShowMin());
				rItem.setnMax(info.getnShowMax());
				rItem.setnIncrease(0);
				changeRulerDirection(mGraphInfo,rItem);
				rItem.setnRulerDir(nRulerDir);
				rItem.seteRulerDir(rDir);
			}

			// 动态数据显示区域
			dItem.setBackColor(info.getnTextColor());
			dItem.setLineWidth(0);
			dItem.setForeColor(info.getnDesignColor());
			dItem.setStyle(IntToEnum.getCssType(info.getnDesign()));
		}

		// 背景
		if (info.isHasBg()) {
		    Bitmap mBgBitamp = ImageFileTool.getBitmap(info.getsPic());
			if (mBgBitamp != null) {
				if (pRect==null) {
					pRect=new Rect(info.getnLeftTopX(), info.getnLeftTopY(),
							info.getnLeftTopX()+info.getnWidth(), 
							info.getnLeftTopY()+info.getnHeigth());
				}
				canvas.drawBitmap(mBgBitamp, null, pRect, mBitmapPaint);
			}
		}
		
	    double temp=1;
		draw=true;
		// 实时动态数据-plc
		switch (info.geteDirection()) {
		case TOWARD_LEFT: // 向左
			temp =(double) (info.getnShowWidth())
					/ (info.getnShowMax() - info.getnShowMin());// 显示范围与宽度
			if (status==1&&temp<1) {
				status=1;
			}else{
				status=(int)(temp*status);
			}
			mRight = info.getnShowLeftTopX() + info.getnShowWidth();
			mLeft = (int)(mRight - status);
			mBottom = info.getnShowLeftTopY() + info.getnShowHigth();
			mTop = info.getnShowLeftTopY();
			if (mRight-mLeft<=0) {
				draw=false;
			}
			mRect.right=info.getnShowLeftTopX() + info.getnShowWidth()-(mRight-mLeft);
			break;
		case TOWARD_RIGHT: // 向右
			temp = (double) (info.getnShowWidth())
					/ (info.getnShowMax() - info.getnShowMin());// 显示范围与宽度
			if (status==1&&temp<1) {
				status=1;
			}else{
				status=(int)(temp*status);
			}
			mLeft = info.getnShowLeftTopX();
			mTop = info.getnShowLeftTopY();
			mRight = (int)(info.getnShowLeftTopX() + status);
			mBottom = info.getnShowLeftTopY() + info.getnShowHigth();
			if (mRight-mLeft<=0) {
				draw=false;
			}
			mRect.left=info.getnShowLeftTopX()+mRight-mLeft;
			break;
		case TOWARD_TOP: // 向上
			temp = (double) (info.getnShowHigth())
					/ (info.getnShowMax() - info.getnShowMin());// 显示范围与高度
			if (status==1&&temp<1) {
				status=1;
			}else{
				status=(int)(temp*status);
			}
			mLeft = info.getnShowLeftTopX();
			mRight = info.getnShowLeftTopX() + info.getnShowWidth();
			mBottom = info.getnShowLeftTopY() + info.getnShowHigth();
			mTop =(int)(mBottom - status);
			if (mBottom-mTop<=0) {
				draw=false;
			}
			mRect.bottom=info.getnShowLeftTopY() + info.getnShowHigth()-(mBottom-mTop);
			break;
		case TOWARD_BOTTOM: // 向下
			temp = (double) (info.getnShowHigth())
					/ (info.getnShowMax() - info.getnShowMin());// 显示范围与高度
			if (status==1&&temp<1) {
				status=1;
			}else {
				status=(int)(temp*status);
			}
			mLeft = info.getnShowLeftTopX();
			mRight = info.getnShowLeftTopX() + info.getnShowWidth();
			mTop = info.getnShowLeftTopY();
			mBottom = (int)(mTop + status);
			if (mBottom-mTop<=0) {
				draw=false;
			}
			mRect.top=info.getnShowLeftTopY() +mBottom-mTop;
			break;
		}
		
		// 动态显示区域背景
	    sItem.draw(mPaint, canvas);

		if (draw) {
			dRect.set(mLeft, mTop, mRight, mBottom);
			// 画实时数据
			if (alram) {
				if (nPreState==0) {
					nPreState=1;
					dItem.setBackColor(info.getnAlarmTextColor());
					dItem.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
				}
			}else {
				if (nPreState==1||IntToEnum.getCssType(info.getnDesign())!=CSS_TYPE.CSS_SOLIDCOLOR) {
					nPreState=0;
					dItem.setBackColor(info.getnTextColor());
					dItem.setStyle(IntToEnum.getCssType(info.getnDesign()));
				}
			}
			dItem.draw(mPaint, canvas);
		}
		
		// 标尺
		if (info.isHasRuler()&&info.isbShowRuleValue()) {
			rItem.draw(mPaint, canvas);
		}
	}

	/**
	 * 柱状图--普通图表
	 * 
	 * @param type-背景类型
	 * @return 是否有标尺 ,true-有标尺 ,是否有内边框,true-有内边框 是否有背景图片,true-表示有
	 */
	private void resetPillShape(GraphBaseInfo info) {
		info.setHasBg(true);
		info.setHasRuler(true);
		info.setHasFrame(true);
		
		// 背景
		switch (info.getnShapeId()) {
		case GRAPH_SHAPE_TYPE.CP_001:
		case GRAPH_SHAPE_TYPE.CP_002:
			info.setHasBg(false);
			break;
		}
	}

	// 可带标尺的扇形
	private ArcRulerItem arcFItem;
	// 可带标尺的扇形所在的矩形
	private RectF arcFRectF;
	private int  offsetDegree; //偏移角度

	/**
	 * 圆形--普通图表
	 * @param info--普通图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param status--plc 数据
	 */
	private void drawCircleShape(CommonGraphInfo info, boolean init, float status,Canvas canvas) {
		// 初始化
		if (init ) {
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(info.getnWidth(), info.getnHeigth(), Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清空
			
			arcFRectF = new RectF();
			arcFRectF.set(
					info.getnShowLeftTopX() - info.getnLeftTopX(),
					info.getnShowLeftTopY() - info.getnLeftTopY(),
					info.getnShowLeftTopX() - info.getnLeftTopX()
							+ info.getnShowWidth(), info.getnShowLeftTopY()
							- info.getnLeftTopY() + info.getnShowHigth());
			dstRectF=new RectF(info.getnLeftTopX(), info.getnLeftTopY(),
					info.getnLeftTopX()+info.getnWidth(), 
					info.getnLeftTopY()+info.getnHeigth());
			
			arcFRectF.width();
			arcFRectF.height();
			dstRectF.width();
			dstRectF.height();
			
			offsetDegree = info.getStartAngle();
			startAngle = 270 + offsetDegree;
			nCenterX = info.getnShowLeftTopX() - info.getnLeftTopX()
					+ ((float)info.getnShowWidth()) / 2;
			nCenterY = info.getnShowLeftTopY() - info.getnLeftTopY()
					+ ((float)info.getnShowHigth()) / 2;
			
			arcFItem = new ArcRulerItem(arcFRectF);
			arcFItem.clockwise=true;
			arcFItem.seteArcType(ArcType.ROUND);
			arcFItem.setBackColor(info.getnBackColor());
			//arcFItem.setnAlphas((short)255);
			arcFItem.setnAlphas((short)info.getnAlpha());
			arcFItem.setbHole(info.isbHole());
			arcFItem.setnHoleR(info.getnRadius());
			arcFItem.setLineColor(info.getnFrameColor());
			arcFItem.setLineWidth(1);
			arcFItem.setStartAngle(startAngle);
			arcFItem.setSweepAngle(info.getSpanAngle());
			arcFItem.setnMainRulingLength(info.getnRulerHigth());
			arcFItem.setnMainRuling(info.getnMainRuling());
			arcFItem.setbRuler(true);
			arcFItem.setbShowMinorRuling(info.isbShowRuling());
			arcFItem.setnMinorRuling(info.getnRuling());
			arcFItem.setnCenterX(nCenterX);
			arcFItem.setnCenterY(nCenterY);
			arcFItem.setnR(getR(info.getnShowWidth(), info.getnPointerType()));
			arcFItem.setnRulerColor(info.getnRulingColor());
			arcFItem.setUseCenter(true);
			arcFItem.setForeColor(info.getnDesignColor());
			arcFItem.setStyle(IntToEnum.getCssType(info.getnDesign()));
 			arcFItem.setnMin(info.getnShowMin());
			arcFItem.setnMax(info.getnShowMax());
			arcFItem.setbShowRuleValue(info.isbShowRuleValue());
			arcFItem.setnValueWidth(getValueWidth(info));
			arcFItem.setnOffsetAngle(offsetDegree);
			arcFItem.setbShowFrame(info.getShowFrame());
			
			arcFItem.draw(mPaint, mCanvas);
		} 
		
		//清理， 局部重绘
		arcFItem.clear(mCanvas, mPaint);
		

		//更新图表统计区域
		int color;
		int degree;
		if (alram) {
			color=info.getnAlarmTextColor();
		}else {
			color=info.getnTextColor();
		}
		
		int temp = (int) (((float) info.getSpanAngle() / (info.getnShowMax() - info.getnShowMin())) * status);
		if(info.isbStart()){
			//绘制起始指针
			arcFItem.drawStartLine(mPaint,info.getnRulingColor(), mCanvas, 180 +offsetDegree,1);
			//绘制偏移指针
			degree = temp + 180 + offsetDegree; //顺时针
			arcFItem.drawStartLine(mPaint,color, mCanvas, degree,info.getnPointerType());
		}else {
			//填充
			arcFItem.setnDataColor(color);//刻度颜色
			arcFItem.drawData(mPaint, mCanvas, temp);
		}
		
		
		//画背景图片
		if (info.getsPic()!=null) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				canvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
			}
		}
		
		canvas.drawBitmap(mBitmap, info.getnLeftTopX(), info.getnLeftTopY(),
				mBitmapPaint);
	}
	
	private int getR( int showWidth, short type){
		int r = 0;
		if (type == 2) { // 如果是粗直线
			r = showWidth / 2  - (10 * showWidth /60);
		}
		else {
			r = showWidth / 2  - (5 * showWidth /60);
		}
		
		return r;
	}
	
	private int getValueWidth(CommonGraphInfo info){
		float spaceLen =0;
		float marginTop = info.getnShowLeftTopY() - info.getnLeftTopY();
		float marginLeft = info.getnShowLeftTopX() - info.getnLeftTopX();
		float marginBttm = info.getnHeigth() - info.getnShowHigth() - marginTop;
		float marginRgt = info.getnWidth() - info.getnShowWidth() - marginLeft;
		
		int centerPos = (info.getStartAngle() + info.getSpanAngle()/2 + 45) %360 /90;
		switch (centerPos) {
		case 0:
			spaceLen = marginTop;
			break;
		case 1:
			spaceLen = marginRgt;
			break;
		case 2:
			spaceLen = marginBttm;
			break;
		case 3:
			spaceLen = marginLeft;
		default:
			break;
		}

		return (int)(spaceLen - info.getnRulerHigth());
	}
	

	/**
	 * 半圆形--普通图表
	 * @param info--普通图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param status--plc 数据
	 */
	private RectF bgRectF;
	private void drawSectorShape(CommonGraphInfo info, boolean init, float status,Canvas canvas) {
		
		startAngle = 180;
		sweepAngle = 180;
		nCenterX = info.getnShowLeftTopX() - info.getnLeftTopX()
				+ info.getnShowWidth() / 2;
		nCenterY = info.getnShowLeftTopY() - info.getnLeftTopY()
				+ info.getnShowHigth() / 2;

		if (mBitmap == null) {
			mBitmap = Bitmap.createBitmap(info.getnWidth(), info.getnHeigth(),
					Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		}
		
		// 初始化
		if (init) {
			
			//清空
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			nPointerDegrees = 135;
			nDegrees = 180;
			arcFRectF = new RectF();
			mRectF=new RectF(info.getnLeftTopX(), info.getnLeftTopY(),
					info.getnLeftTopX()+info.getnWidth(),
					info.getnLeftTopY()+info.getnHeigth());

			arcFRectF.set(
					info.getnShowLeftTopX() - info.getnLeftTopX(),
					info.getnShowLeftTopY() - info.getnLeftTopY(),
					info.getnShowLeftTopX() - info.getnLeftTopX()
							+ info.getnShowWidth(), info.getnShowLeftTopY()
							- info.getnLeftTopY() + info.getnShowHigth());
			
			dstRectF=new RectF(info.getnLeftTopX(), info.getnLeftTopY(),
					info.getnLeftTopX()+info.getnWidth(), 
					info.getnLeftTopY()+info.getnHeigth()/2+2*info.getnRulerHigth());

			bgRectF=new RectF(info.getnLeftTopX(), info.getnLeftTopY(),
					info.getnLeftTopX()+info.getnWidth(), 
					info.getnLeftTopY()+info.getnHeigth()/2
					+info.getnShowLeftTopY()-info.getnLeftTopY()-info.getnRulerHigth());
			
			arcFItem = new ArcRulerItem(arcFRectF);
			arcFItem.seteArcType(ArcType.HALF);
			arcFItem.seteArcOrientation(GLOBAL_POS_PROP.MID_TOP);
			arcFItem.setBackColor(info.getnBackColor());
			arcFItem.setnAlphas((short)255);
			arcFItem.setbHole(info.isbHole());
			arcFItem.setnHoleR(info.getnRadius());
			arcFItem.setLineColor(info.getnFrameColor());
			arcFItem.setLineWidth(1);
			arcFItem.setStartAngle(startAngle);
			arcFItem.setSweepAngle(sweepAngle);
			arcFItem.setnMainRulingLength(info.getnRulerHigth());
			arcFItem.setnMainRuling(info.getnMainRuling());
			arcFItem.setnMinorRuling(info.getnRuling());
			arcFItem.setbShowMinorRuling(info.isbShowRuling());
			arcFItem.setbRuler(true);
			arcFItem.setUseCenter(false);
			arcFItem.setnCenterX(nCenterX);
			arcFItem.setnCenterY(nCenterY);
			arcFItem.setnDataColor(info.getnTextColor());
			arcFItem.setForeColor(info.getnDesignColor());
			arcFItem.setStyle(IntToEnum.getCssType(info.getnDesign()));
			arcFItem.setnR(info.getnShowHigth() / 2 - arcFItem.getLineWidth());

			arcFItem.setStyle(IntToEnum.getCssType(info.getnDesign()));
			arcFItem.setnMin(info.getnShowMin());
			arcFItem.setnMax(info.getnShowMax());
			arcFItem.setbShowRuleValue(info.isbShowRuleValue());
			arcFItem.clockwise=true;
			arcFItem.setnValueWidth(info.getnShowLeftTopY()-info.getnLeftTopY()-info.getnRulerHigth());
			arcFItem.setnRulerColor(info.getnRulingColor());
			arcFItem.draw(mPaint, mCanvas);
		}

		if (info.getsPic()!=null) {
			if (!info.getsPic().equals("")) {
				if (mBgBitmap==null) {
					mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
				}
				if (mBgBitmap!=null) {
					canvas.drawBitmap(mBgBitmap, null, bgRectF, mBitmapPaint);
				}
			}
		}
		
		arcFItem.clear(mCanvas, mPaint);

		int color;
		if (alram) {
			color=info.getnAlarmTextColor();
		}else{
			color=info.getnTextColor();
		}
		
		// 画数据
		int temp = (int) (((float) nDegrees / (info.getnShowMax() - info
				.getnShowMin())) * status);
		arcFItem.setnDataColor(color);
		arcFItem.drawData(mPaint, mCanvas, temp);
		
		canvas.drawBitmap(mBitmap, info.getnLeftTopX(), info.getnLeftTopY(),
				mBitmapPaint);
	}

	/**
	 * 槽形--普通图表
	 * @param info--普通图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param status--plc 数据
	 */
	private void drawGrooveShape(CommonGraphInfo info, boolean init, float status,Canvas canvas) {
		if (info.getnShapeId() != 0) {
			
			if (info.getnShapeId()>54) {
				//由于没有背景和刻度，上位没有传显示位置和显示大小
				info.setnShowLeftTopX(info.getnLeftTopX());
				info.setnShowLeftTopY(info.getnLeftTopY());
				info.setnShowWidth(info.getnWidth());
				info.setnShowHigth(info.getnHeigth());
			}
			
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(info.getnShowWidth(),
						info.getnShowHigth(), Config.ARGB_8888);
				mCanvas = new Canvas(mBitmap);
			}

			if (init) {
				mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			}

			drawGroove(info, mCanvas,canvas, status, init);
			
			//边框
			if (mFrameBitmap!=null) {
				canvas.drawBitmap(mFrameBitmap, info.getnShowLeftTopX(),
						info.getnShowLeftTopY(), mBitmapPaint);
			}
			
			canvas.drawBitmap(mBitmap, info.getnShowLeftTopX(),
					info.getnShowLeftTopY(), mBitmapPaint);
		}
	}

	/**
	 * 柱状图--统计图表
	 * @param info--统计图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param param--plc 数据
	 */
	private void drawPillaShape(StatisticsGraphInfo info, boolean init,
			int param) {
	}

	/**
	 * 圆形--统计图表
	 * @param info--统计图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param param--plc 数据
	 */
	private void drawCircleShape(StatisticsGraphInfo info, boolean init,
			int param) {
		
		Bitmap mBitmap = null;
		Canvas canvas = null;
		int startAngle = 0, sweepAngle = 0;
		int nCenterX = 0, nCenterY = 0;
		int nRulerLenX = 0;// 标尺X方向所占长度
		int nRulerLenY = 0;// 标尺Y方向所占长度
		int nHeigth = 0; // 标尺和扇形的高度
		int nWidth = 0; // 标尺和扇形的宽度
		RectF mRectF = new RectF();
		startAngle = 0;
		sweepAngle = 360;
		nCenterY = nCenterX = info.getnShowWidth() / 2 + info.getnRulerHigth();
		ArcRulerItem mItem = new ArcRulerItem(mRectF);
		mItem.seteArcType(ArcType.ROUND);
		nRulerLenY = nRulerLenX = 2 * info.getnRulerHigth();
		nWidth = info.getnRulerHigth() - 1 + info.getnShowWidth();
		nHeigth = info.getnRulerHigth() - 1 + info.getnShowHigth();

		mRectF.set(1 + info.getnRulerHigth(), 1 + info.getnRulerHigth(),
				nWidth, nHeigth);

		mBitmap = Bitmap.createBitmap(info.getnShowWidth() + nRulerLenX,
				info.getnShowHigth() + nRulerLenY, Config.ARGB_8888);
		canvas = new Canvas(mBitmap);

		mItem.setLineColor(Color.WHITE);
		mItem.setLineWidth(1);
		mItem.setStartAngle(startAngle);
		mItem.setSweepAngle(sweepAngle);
		mItem.setnMainRulingLength(info.getnRulerHigth());
		mItem.setnMainRuling(info.getnMainRuling());
		mItem.setbRuler(true);
		mItem.setnMinorRuling(info.getnRuling());
		mItem.setnCenterX(nCenterX);
		mItem.setnCenterY(nCenterY);
		mItem.draw(mPaint, canvas);
		mCanvas.drawBitmap(mBitmap,
				info.getnShowLeftTopX() - info.getnRulerHigth(),
				info.getnShowLeftTopY() - info.getnRulerHigth(), mBitmapPaint);
	}

	/**
	 * 仪表--仪表图表
	 * @param info--普通图表对象
	 * @param init--是否是初始化,true-初始化
	 * @param param--plc 数据
	 */
	private int startAngle = 0, sweepAngle = 0;
	private float nCenterX = 0, nCenterY = 0;
	private ArcRulerItem mItem;
	private int nPointerDegrees;
	private int nDegrees;// 可转动角度
	private Xfermode xMode=new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private void drawMeterShare(GraphBaseInfo info, boolean init, float param,Canvas canvas) {
		
		nWidth = info.getnWidth();
		nHeight = info.getnHeigth();
		nCenterX = info.getnShowLeftTopX() - info.getnLeftTopX()
				+ ((float)info.getnShowWidth()) / 2;
		nCenterY = info.getnShowLeftTopY() - info.getnLeftTopY()
				+ ((float)info.getnShowHigth()) / 2;
		
		Log.d(TAG, "nWidth ="+nWidth+",nHeight ="+nHeight+",nCenterX ="+nCenterX+",nCenterY ="+nCenterY);
		if (mBitmap == null||init) {
			mBitmap = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
			mCanvas = new Canvas(mBitmap);
		}

		if (init) {
		
			//清空
			mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			
			if (mRectF == null) {
				mRectF = new RectF();
			}
			mRectF.set(
					info.getnShowLeftTopX() - info.getnLeftTopX(),
					info.getnShowLeftTopY() - info.getnLeftTopY(),
					info.getnShowLeftTopX() - info.getnLeftTopX()
							+ info.getnShowWidth(), info.getnShowLeftTopY()
							- info.getnLeftTopY() + info.getnShowHigth());
			mItem = new ArcRulerItem(mRectF);

			switch (info.getnShapeId()) {
			case 1:
			case 2:
			case 3:
			case 4:
				// 圆
				nDegrees = 360;
				drawCircle(info, mItem);
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				// 四分之三
				nDegrees = 270;
				drawThreeQuarterCircle(info, mItem);
				break;
			case 9:
			case 12:
			case 13:
			case 14:
			case 19:
			case 23:
			case 25:
			case 26:
				// 四分之一
				nDegrees = 90;
				drawQuarterCircle(info, mItem);
				break;
			case 18:
			case 21:
			case 29:
			case 30:
				// 半圆
				nDegrees = 180;
				drawHalfCircle(info, mItem);
				break;
			default:
				nDegrees = 360;
				drawCircle(info, mItem);
				return;
			}

			mItem.setnAlphas(info.getnAlpha());
			mItem.setLineColor(info.getnRulingColor());
			mItem.setBackColor(info.getnBackColor());
			mItem.setnRulerColor(info.getnRulingColor());
			mItem.setLineWidth(1);
			mItem.setStartAngle(startAngle);
			mItem.setSweepAngle(sweepAngle);
			mItem.setnMainRulingLength(info.getnRulerHigth());
			mItem.setnMainRuling(info.getnMainRuling());
			mItem.setbRuler(true);
			mItem.setbShowMinorRuling(info.isbShowRuling());
			mItem.setnMinorRuling(info.getnRuling());
			mItem.setnCenterX(nCenterX);
			mItem.setnCenterY(nCenterY);
			mItem.setnR(info.getnShowWidth() / 2 - mItem.getLineWidth());
			mItem.setnR(getR(info.getnShowWidth(), info.getnPointerType()));
			mItem.setnMin(info.getnShowMin());
			mItem.setnMax(info.getnShowMax());
			mItem.setbShowRuleValue(info.isbShowRuleValue());
			mItem.setbShowFrame(info.getShowFrame());
			mItem.draw(mPaint, mCanvas);
		}

		int color;
		if (alram) {
			color=info.getnAlarmTextColor();
		}else {
			color=info.getnTextColor();
		}
		
		if (init) {
			mItem.drawPointer(mPaint,color, mCanvas, nPointerDegrees-45,info.getnPointerType());
		} 
		
		int temp = (int) (((float) nDegrees / (info.getnShowMax() - info
				.getnShowMin())) * param);
		if (info.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
			// 向左
			mItem.drawPointer(mPaint,color, mCanvas, nPointerDegrees - temp-45,info.getnPointerType());
		} else if (info.geteDirection() == DIRECTION.TO_RIGHT_ROTATE) {
			// 向右
			mItem.drawPointer(mPaint,color, mCanvas, nPointerDegrees + temp-45,info.getnPointerType());
		}
		
		//满足条件就擦去背景
//		Paint paint = new Paint();
//		paint.setXfermode(xMode);
//		paint.setColor(Color.TRANSPARENT);
//		paint.setAntiAlias(true);
//		mCanvas.drawArc(mItem.getBgRectF(), 0, 360, true, paint);

		canvas.drawBitmap(mBitmap, info.getnLeftTopX(), info.getnLeftTopY(),
				mBitmapPaint);
	}

	/**
	 * 仪表-圆形 nPointerDegrees-启动位置在xy坐标轴中为 -45
	 */
	private void drawCircle(GraphBaseInfo info, ArcRulerItem mItem) {

		// 圆
		startAngle = 0;
		sweepAngle = 360;// 顺时针转动的角度-起始点水平右边为0
		mItem.seteArcType(ArcType.ROUND);
		mItem.setnValueWidth(info.getnShowLeftTopY()-info.getnLeftTopY()-info.getnRulerHigth());
		if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
			// 向左转动
			mItem.clockwise=false;
		} else {
			// 向右转动
			mItem.clockwise=true;
		}
		switch (info.getnShapeId()) {
		case 1:
			nPointerDegrees = 225;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_TOP);
			break;
		case 2:
			nPointerDegrees = 45;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_BELOW);
			break;
		case 3:
			nPointerDegrees = 135;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.LEFT_POS);
			break;
		case 4:
			mItem.seteArcOrientation(GLOBAL_POS_PROP.RIGHT_POS);
			nPointerDegrees = 315;
			break;
		}
	}

	/**
	 * 仪表-四分之三圆 nPointerDegrees-启动位置在xy坐标轴中为 -45
	 */
	private void drawThreeQuarterCircle(GraphBaseInfo info, ArcRulerItem mItem) {

		mItem.seteArcType(ArcType.THREE);
		mItem.setUseCenter(true);
		mItem.setnValueWidth(info.getnShowLeftTopY()-info.getnLeftTopY()-info.getnRulerHigth());
		sweepAngle = 270;// 顺时针转动的角度-起始点水平右边为0
		switch (info.getnShapeId()) {
		case 5: // 四分之三-开口向下
			startAngle = 135;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_BELOW);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 360;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 90;
				mItem.clockwise=true;
			}
			break;
		case 6: // 四分之三-开口向上
			startAngle = 315;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_TOP);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 180;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 270;
				mItem.clockwise=true;
			}
			break;
		case 7: // 四分之三-开口向左
			startAngle = 225;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.LEFT_POS);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 90;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 180;
				mItem.clockwise=true;
			}
			break;
		case 8: // 四分之三-开口向右
			startAngle = 45;
			mItem.seteArcOrientation(GLOBAL_POS_PROP.RIGHT_POS);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 270;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 0;
				mItem.clockwise=true;
			}
			break;
		}
	}

	/**
	 * 仪表-半圆 nPointerDegrees-启动位置在xy坐标轴中为 -45
	 */
	private void drawHalfCircle(GraphBaseInfo info, ArcRulerItem mItem) {

		mItem.seteArcType(ArcType.HALF);
		sweepAngle = 180;// 顺时针转过的角度-起始点水平右边为0

		switch (info.getnShapeId()) {
		case 18: // 半圆-上
			startAngle = 180;
			mItem.setnValueWidth(info.getnHeigth()-info.getnShowHigth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_TOP);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 315;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 135;
				mItem.clockwise=true;
			}
			break;
		case 21: // 半圆-下
			startAngle = 0;
			mItem.setnValueWidth(info.getnHeigth()-info.getnShowHigth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_BELOW);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 135;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 315;
				mItem.clockwise=true;
			}
			break;
		case 29: // 半圆-左
			startAngle = 90;
			mItem.setnValueWidth(info.getnWidth()-info.getnShowWidth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.LEFT_POS);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 225;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 45;
				mItem.clockwise=true;
			}
			break;
		case 30: // 半圆-右
			startAngle = 270;
			mItem.setnValueWidth(info.getnWidth()-info.getnShowWidth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.RIGHT_POS);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 45;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 225;
				mItem.clockwise=true;
			}
			break;
		}

	}

	/**
	 * 仪表-四分之一圆 nPointerDegrees-启动位置在xy坐标轴中为 -45
	 */
	private void drawQuarterCircle(GraphBaseInfo info, ArcRulerItem mItem) {

		mItem.seteArcType(ArcType.QUARTER);
		mItem.setUseCenter(true);
		sweepAngle = 90;// 顺时针转过的角度-起始点水平右边为0

		switch (info.getnShapeId()) {
		case 9: // 四分之一-左上
			startAngle = 180;
			mItem.setnValueWidth(info.getnShowLeftTopY()-info.getnLeftTopY()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.LEFT_TOP);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 225;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 135;
				mItem.clockwise=true;
			}
			break;
		case 12: // 四分之一-右上
			startAngle = 270;
			mItem.setnValueWidth(info.getnShowLeftTopY()-info.getnLeftTopY()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.RIGHT_TOP);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 315;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 225;
				mItem.clockwise=true;
			}
			break;
		case 13: // 四分之一-左下
			startAngle = 90;
			mItem.setnValueWidth(info.getnShowLeftTopX()-info.getnLeftTopX()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.LEFT_BELOW);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 135;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 45;
				mItem.clockwise=true;
			}
			break;
		case 14: // 四分之一-右下
			startAngle = 0;
			mItem.setnValueWidth(info.getnWidth()-info.getnShowWidth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.RIGHT_BELOW);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 45;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 315;
				mItem.clockwise=true;
			}
			break;
		case 19: // 四分之一-上
			startAngle = 225;
			mItem.setnValueWidth(info.getnHeigth()-info.getnShowHigth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_TOP);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 270;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 180;
				mItem.clockwise=true;
			}
			break;
		case 23: // 四分之一-下
			startAngle = 45;
			mItem.setnValueWidth(info.getnHeigth()-info.getnShowHigth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.MID_BELOW);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 90;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 0;
				mItem.clockwise=true;
			}
			break;
		case 25: // 四分之一-左
			startAngle = 135;
			mItem.setnValueWidth(info.getnWidth()-info.getnShowWidth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.LEFT_POS);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 180;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 90;
				mItem.clockwise=true;
			}
			break;
		case 26: // 四分之一-右
			startAngle = 315;
			mItem.setnValueWidth(info.getnWidth()-info.getnShowWidth()-info.getnRulerHigth());
			mItem.seteArcOrientation(GLOBAL_POS_PROP.RIGHT_POS);
			if (mGraphInfo.geteDirection() == DIRECTION.TO_LEFT_ROTATE) {
				// 向左转动
				nPointerDegrees = 0;
				mItem.clockwise=false;
			} else {
				// 向右转动
				nPointerDegrees = 270;
				mItem.clockwise=true;
			}
			break;
		}

	}

	/**
	 * 槽状图
	 * @param info--普通图表对象
	 * @param bCanvas--带Bitmap
	 * @param sCanvas--画面
	 * @param shapeId
	 *            1-水瓶形,2-梯六边形,3-梯五边形 4-三角形,5-正六边形,6-正五边形 7-椭圆 ,8-圆梯形 ,9-梯形
	 *            10-圆底烧瓶,11-正四边形
	 * @param status--plc实时数据
	 * @param init--初始化=true
	 */
	private Path mPath = null;
	private RectF mRectF = null;
	private static final float mPScale = (float) 0.05;
	private static final float mScale = (float) 0.90;
	private float mHPadding = 0;// 水平
	private float mVPadding = 0;// 垂直
	private float bottomHeigth = 0;
	private float midHeigth = 0;
	private float topHeigth = 0;
	private float topWidth = 0;
	private float leftPadding = 0;
	private float inTopWidth = 0;
	private float inBottomHeight = 0;
	private float inTopHeight = 0;
	private float inMidHeight = 0;
	private boolean upturn;// //默认所有画都是以向上为基本图形，其他方向以这个来旋转
	private RectF dstRectF;
	private FillRender fillRender;
	private Shader myShader;
	private boolean isShader;
	private Bitmap mFrameBitmap;
	private Canvas mFrameCanvas;
	private void drawGroove(CommonGraphInfo info, Canvas bCanvas,Canvas sCanvas, float status,
			boolean init) {

		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(info.getnFrameColor());
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setXfermode(null);

		if (IntToEnum.getCssType(info.getnDesign())!=CSS_TYPE.CSS_SOLIDCOLOR
				&&(IntToEnum.getCssType(info.getnDesign())!=CSS_TYPE.CSS_TRANSPARENCE)) {
			if (fillRender==null) {
				fillRender=new FillRender();
			}
			if (myShader==null) {
				myShader=fillRender.setRectCss(IntToEnum.getCssType(info.getnDesign()), 
						info.getnShowLeftTopX(),info.getnShowLeftTopY(),
						info.getnShowLeftTopX()+info.getnShowWidth(),
						info.getnShowLeftTopY()+info.getnShowHigth(), 
						info.getnDesignColor(), info.getnTextColor());
			}
			isShader=true;
		}else {
			isShader=false;
		}
		
		if (reset) {
			// 判断是否有背景，是否有刻度，具体对应哪个类型
			grooveInfo(info);
			dstRectF=new RectF(info.getnLeftTopX(), info.getnLeftTopY(), 
					info.getnLeftTopX()+info.getnWidth(),
					info.getnLeftTopY()+info.getnHeigth());
		}

		DIRECTION eDir = info.geteDirection();
		int shapeId = info.getnShapeId();
		switch (info.getnShapeId()) {
		case 1: // 水瓶
			grooveOne(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 2: // 梯六边形
			grooveTwo(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 3: // 梯五边形
			grooveThree(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 4: // 三角形
			grooveFour(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 5: // 正八边形
			grooveFive(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 6: // 正六边形
			grooveSix(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 7: // 椭圆
			grooveSeven(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 8: // 圆梯形
			grooveEight(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 9: // 梯形
			grooveNine(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 10: // 圆底烧瓶
			grooveTen(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		case 11: // 正四边形
			grooveEleven(info, bCanvas,sCanvas, shapeId, eDir, status, init);
			break;
		}
	}

	/**
	 * 水瓶
	 */
	short temp;
	private int nWidth = 0, nHeight = 0;
	private int nRWidth = 0, nRHeight = 0;
	private int nRLx, nRLy;// 刻度的坐标
	private DIRECTION eRulerDir;// 刻度向内or向外
	private DIRECTION eDecorientation;// 刻度整体方向
	private static int nLinePadding = 1;
	private float nSlope = 1;
	private void grooveOne(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);

		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());;
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		if (init) {
			topWidth = nWidth * 5 / 13; // 顶部宽度
			leftPadding = (nWidth - topWidth) / 2; // 距离起始点的距离
			bottomHeigth = nHeight * 11 / 21; // 底部的高度
			midHeigth = nHeight * 6 / 21; // 中间的高度
			topHeigth = nHeight * 4 / 21; // 顶部的高度

			mHPadding = nWidth * mPScale; // 水平方向内外框的距离
			mVPadding = nHeight * mPScale; // 垂直方向内外框的距离
			nSlope = leftPadding / midHeigth;

			// 内框
			inTopWidth = topWidth - 2 * mHPadding;
			inBottomHeight = bottomHeigth - mVPadding;
			inTopHeight = topHeigth - mVPadding;

			if (mPath == null) {
				mPath = new Path();
			}
		}

		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		// 显示范围
		if (status>0&&status*temp<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}

		
		if (!upturn) {
			// 跟默认方向相反
			if (status>0) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				mPath.reset();
				mPath.moveTo(leftPadding + mHPadding, mVPadding);
				mPath.lineTo(leftPadding + mHPadding + inTopWidth, mVPadding);
				mPath.lineTo(leftPadding + mHPadding + inTopWidth, inTopHeight
						+ mVPadding);
				mPath.lineTo(nWidth - mHPadding, inTopHeight + midHeigth
						+ mVPadding);
				mPath.lineTo(nWidth - mHPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, inTopHeight + midHeigth + mVPadding);
				mPath.lineTo(leftPadding + mHPadding, inTopHeight + mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);

				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + mVPadding, nWidth, nHeight, mPaint);
			}
		} else {
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			if (isShader) {
				if (alram) {
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(info.getnAlarmTextColor());
				}else {
					mPaint.setShader(myShader);
					mPaint.setStyle(Style.FILL_AND_STROKE);
				}
			}else {
				mPaint.setStyle(Style.FILL);
				if (alram) {
					mPaint.setColor(info.getnAlarmTextColor());
				}else {
					mPaint.setColor(info.getnTextColor());
				}
			}
			
			if (status>0) {
				if (status <= inBottomHeight) {
					mPath.reset();
					mPath.moveTo(mHPadding, nHeight - status - mVPadding);
					mPath.lineTo(nWidth - mHPadding, nHeight - status - mVPadding);
					mPath.lineTo(nWidth - mHPadding, nHeight - mVPadding);
					mPath.lineTo(mHPadding, nHeight - mVPadding);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				} else if (status < inBottomHeight + midHeigth) {
					float mLeft = (status - inBottomHeight) * nSlope;
					mPath.reset();
					mPath.moveTo(mHPadding + mLeft, nHeight - status - mVPadding);
					mPath.lineTo(nWidth - mLeft - mHPadding, nHeight - status
							- mVPadding);
					mPath.lineTo(nWidth - mHPadding, inTopHeight + midHeigth
							+ mVPadding);
					mPath.lineTo(nWidth - mHPadding, nHeight - mVPadding);
					mPath.lineTo(mHPadding, nHeight - mVPadding);
					mPath.lineTo(mHPadding, inTopHeight + midHeigth + mVPadding);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				} else{
					if (status>(nHeight - 2 * mVPadding)) {
						status=(int)(nHeight - 2 * mVPadding);
					}
					mPath.reset();
					mPath.moveTo(leftPadding + mHPadding, nHeight - status
							- mVPadding);
					mPath.lineTo(leftPadding + mHPadding + inTopWidth, nHeight
							- status - mVPadding);
					mPath.lineTo(leftPadding + mHPadding + inTopWidth, inTopHeight
							+ mVPadding);
					mPath.lineTo(nWidth - mHPadding, inTopHeight + midHeigth
							+ mVPadding);
					mPath.lineTo(nWidth - mHPadding, nHeight - mVPadding);
					mPath.lineTo(mHPadding, nHeight - mVPadding);
					mPath.lineTo(mHPadding, inTopHeight + midHeigth + mVPadding);
					mPath.lineTo(leftPadding + mHPadding, inTopHeight + mVPadding);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				}
			}
		}

		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			mPaint.reset();
			mPaint.setXfermode(null);
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(info.getnFrameColor());

			// 外框
			mPath.reset();
			mPath.moveTo(leftPadding + nLinePadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth - nLinePadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth - nLinePadding, topHeigth);
			mPath.lineTo(nWidth - nLinePadding, topHeigth + midHeigth);
			mPath.lineTo(nWidth - nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, topHeigth + midHeigth);
			mPath.lineTo(leftPadding + nLinePadding, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			// 内框
			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding + mHPadding, mVPadding);
			mPath.lineTo(leftPadding + mHPadding + inTopWidth, mVPadding);
			mPath.lineTo(leftPadding + mHPadding + inTopWidth, inTopHeight
					+ mVPadding);
			mPath.lineTo(nWidth - mHPadding, inTopHeight + midHeigth + mVPadding);
			mPath.lineTo(nWidth - mHPadding, nHeight - mVPadding);
			mPath.lineTo(mHPadding, nHeight - mVPadding);
			mPath.lineTo(mHPadding, inTopHeight + midHeigth + mVPadding);
			mPath.lineTo(leftPadding + mHPadding, inTopHeight + mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
		}
		

		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 梯六边型
	 */
	private void grooveTwo(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);

		//背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//
			}
		}
		
		if (init) {
			// 外框
			topWidth = nWidth * 6 / 16;
			leftPadding = nWidth * 5 / 16;
			topHeigth = nHeight / 3;
			bottomHeigth = nHeight * 2 / 3;
			nSlope = leftPadding / topHeigth;

			// 内框
			mHPadding = nWidth * mPScale;
			mVPadding = nHeight * mPScale;
			inTopHeight = topHeigth - mVPadding;
			inBottomHeight = bottomHeigth - mVPadding;
			if (mPath == null) {
				mPath = new Path();
			}
		}

		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		double temp=(nHeight - 2 * mVPadding)/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&status*temp<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}

		if (!upturn) {
			// 与默认方向相反，默认方向是向上。
			if (status>0) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				mPath.reset();
				mPath.moveTo(leftPadding , mVPadding);
				mPath.lineTo(leftPadding + topWidth, mVPadding);
				mPath.lineTo(nWidth - mHPadding, mVPadding
						+ inTopHeight);
				mPath.lineTo(nWidth - mHPadding , nHeight - mVPadding);
				mPath.lineTo(mHPadding , nHeight - mVPadding);
				mPath.lineTo(mHPadding, inTopHeight + mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);

				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + mVPadding, nWidth, nHeight, mPaint);
			}
			
		} else {
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			if (isShader) {
				if (alram) {
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(info.getnAlarmTextColor());
				}else {
					mPaint.setShader(myShader);
					mPaint.setStyle(Style.FILL_AND_STROKE);
				}
			}else {
				mPaint.setStyle(Style.FILL);
				if (alram) {
					mPaint.setColor(info.getnAlarmTextColor());
				}else {
					mPaint.setColor(info.getnTextColor());
				}
			}
			if (status>0) {
				if (status <= inBottomHeight) {
					mPath.reset();
					mPath.moveTo(mHPadding , nHeight - status
							- mVPadding);
					mPath.lineTo(nWidth - mHPadding, nHeight
							- status - mVPadding);
					mPath.lineTo(nWidth - mHPadding, nHeight
							- mVPadding );
					mPath.lineTo(mHPadding, nHeight - mVPadding);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				} else{
					if (status>(nHeight - 2 * mVPadding)) {
						status=(int)(nHeight - 2 * mVPadding);
					}
					float mLeft = (status - inBottomHeight) * nSlope;
					mPath.reset();
					mPath.moveTo(mLeft + mHPadding, nHeight
							- mVPadding - status );
					mPath.lineTo(nWidth - mLeft - mHPadding , nHeight
							- mVPadding - status );
					mPath.lineTo(nWidth - mHPadding, mVPadding
							+ inTopHeight);
					mPath.lineTo(nWidth - mHPadding, nHeight
							- mVPadding );
					mPath.lineTo(mHPadding, nHeight - mVPadding);
					mPath.lineTo(mHPadding, inTopHeight
							+ mVPadding);
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				}
			}
			
		}
		
		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(info.getnFrameColor());

			// 外框
			mPath.reset();
			mPath.moveTo(leftPadding + nLinePadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth + nLinePadding, nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, topHeigth);
			mPath.lineTo(nWidth - nLinePadding, nHeight);
			mPath.lineTo(nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			// 内框
			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding, mVPadding);
			mPath.lineTo(leftPadding + topWidth, mVPadding);
			mPath.lineTo(nWidth - mHPadding, mVPadding + inTopHeight);
			mPath.lineTo(nWidth - mHPadding, nHeight - mVPadding);
			mPath.lineTo(mHPadding, nHeight - mVPadding);
			mPath.lineTo(mHPadding, inTopHeight + mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
		}

		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 梯五边形
	 */
	private void grooveThree(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);

		if (init) {
			// 外框
			mPaint.setColor(Color.WHITE);
			topHeigth = nHeight * 2 / 5;
			leftPadding = nWidth / 2;
			nSlope = leftPadding / topHeigth;

			// 内框
			inTopHeight = mScale * nHeight * 2 / 5;
			inBottomHeight = mScale * nHeight * 3 / 5;
			mHPadding = nWidth * mPScale;
			mVPadding = nHeight * mPScale;

			if (mPath == null) {
				mPath = new Path();
			}
		}

		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		//背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&status*temp<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}

		if (!upturn) {
			// 与默认方向相反，默认方向是向上
			if (status>0) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				
				mPath.reset();
				mPath.moveTo(leftPadding, mVPadding );
				mPath.lineTo(nWidth - mHPadding, inTopHeight
						+ mVPadding);
				mPath.lineTo(nWidth - mHPadding, nHeight
						- mVPadding );
				mPath.lineTo(mHPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, inTopHeight + mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);

				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + mVPadding, nWidth, nHeight, mPaint);
			}
		} else {
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			if (isShader) {
				if (alram) {
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(info.getnAlarmTextColor());
				}else {
					mPaint.setShader(myShader);
					mPaint.setStyle(Style.FILL_AND_STROKE);
				}
			}else {
				mPaint.setStyle(Style.FILL);
				if (alram) {
					mPaint.setColor(info.getnAlarmTextColor());
				}else {
					mPaint.setColor(info.getnTextColor());
				}
			}
			
			if (status>0) {
				if (status <= inBottomHeight) {
					mPath.reset();
					mPath.moveTo(mHPadding, nHeight - status
							- mVPadding );
					mPath.lineTo(nWidth - mHPadding, nHeight
							- status - mVPadding );
					mPath.lineTo(nWidth - mHPadding, nHeight
							- mVPadding );
					mPath.lineTo(mHPadding , nHeight - mVPadding);
					mPath.close();
					canvas.drawPath(mPath, mPaint);

				} else{
					if (status >(nHeight - 2 * mVPadding)) {
						status=(int)(nHeight-2*mVPadding);
					}
					float mLeft = (status - inBottomHeight) * nSlope;
					mPath.reset();
					mPath.moveTo(mLeft + mHPadding , nHeight
							- mVPadding - status);
					mPath.lineTo(nWidth - mLeft - mHPadding,
							nHeight - mVPadding - status );
					mPath.lineTo(nWidth - mHPadding , mVPadding
							+ inTopHeight );
					mPath.lineTo(nWidth - mHPadding, nHeight
							- mVPadding );
					mPath.lineTo(mHPadding, nHeight - mVPadding
							);
					mPath.lineTo(mHPadding, inTopHeight
							+ mVPadding );
					mPath.close();
					canvas.drawPath(mPath, mPaint);
				}
			}
			
		}

		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(info.getnFrameColor());

			// 外框
			mPath.reset();
			mPath.moveTo(leftPadding, nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, topHeigth);
			mPath.lineTo(nWidth - nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			// 内框
			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding, mVPadding);
			mPath.lineTo(nWidth - mHPadding - nLinePadding, inTopHeight + mVPadding);
			mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight - mVPadding
					- nLinePadding);
			mPath.lineTo(mHPadding + nLinePadding, nHeight - mVPadding
					- nLinePadding);
			mPath.lineTo(mHPadding + nLinePadding, inTopHeight + mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
		}
		

		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 三角形
	 */
	private float inPadding=0;
	private float inBPadding=0;
	private void grooveFour(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);

		if (init) {
			// 外框
			mPaint.setColor(Color.WHITE);
			leftPadding = nWidth / 2;
			topHeigth = nHeight;
			nSlope = leftPadding / topHeigth;

			// 内框
			mHPadding = nWidth * mPScale;
			mVPadding = nHeight * mPScale;
			inTopHeight = nHeight-5*mVPadding/2;

			inPadding = (3*mVPadding/2)* nSlope;
			inBPadding=mVPadding*nSlope;
			
			if(mPath==null){
				mPath = new Path();
			}
		} 
		
		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		//背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		double temp=inTopHeight/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&status*temp<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}
		
		if (!upturn) {
			if (status>0) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				mPath.reset();
				mPath.moveTo(leftPadding, 3*mVPadding/2);
				mPath.lineTo(nWidth - inPadding-inBPadding, nHeight - mVPadding);
				mPath.lineTo(inPadding+inBPadding, nHeight - mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
				
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + 3*mVPadding/2, nWidth, nHeight, mPaint);
			}
		}else {
			if (status>0) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setXfermode(null);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				float mLeft = status * nSlope;
				mPath.reset();
				mPath.moveTo(mLeft +inPadding+inBPadding, nHeight- mVPadding - status);
				mPath.lineTo(nWidth -mLeft- inPadding-inBPadding,nHeight- mVPadding - status);
				mPath.lineTo(nWidth - inPadding-inBPadding, nHeight- mVPadding);
				mPath.lineTo(inPadding+inBPadding, nHeight- mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);

			}
		}
		
		if(mFrameBitmap==null){
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);

			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(info.getnFrameColor());
			
			//画外框
			mPath.reset();
			mPath.moveTo(leftPadding, nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, nHeight - nLinePadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			//画内框
			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding, 3*mVPadding/2);
			mPath.lineTo(nWidth - inBPadding-inPadding, nHeight - mVPadding);
			mPath.lineTo(inBPadding+inPadding, nHeight - mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

		}
		
		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 正八边形
	 */
	private void grooveFive(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);
		
		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}

		if (init) {
			// 外框
			leftPadding = nWidth * 5 / 16;
			topWidth = nWidth * 6 / 16;
			topHeigth = nHeight * 5 / 16;
			midHeigth = nHeight * 6 / 16;
			bottomHeigth = nHeight * 5 / 16;
			nSlope = leftPadding / topHeigth;

			//内框
			mHPadding = nWidth * mPScale;
			mVPadding = nHeight * mPScale;
			topHeigth = topHeigth - mVPadding;
			bottomHeigth = bottomHeigth - mVPadding;
			
			mPath = new Path();
		} 

		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		mPaint.reset();
		mPaint.setAntiAlias(true);
		if (isShader) {
			if (alram) {
				mPaint.setStyle(Style.FILL);
				mPaint.setColor(info.getnAlarmTextColor());
			}else {
				mPaint.setShader(myShader);
				mPaint.setStyle(Style.FILL_AND_STROKE);
			}
		}else {
			mPaint.setStyle(Style.FILL);
			if (alram) {
				mPaint.setColor(info.getnAlarmTextColor());
			}else {
				mPaint.setColor(info.getnTextColor());
			}
		}

		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		
		if (status>0&&temp*status<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}
		
		if (status>0) {
			if (status <= bottomHeigth) {
				float mLeft = status * nSlope;
				mPath.reset();
				mPath.moveTo(leftPadding - mLeft, nHeight
						- mVPadding - status);
				mPath.lineTo(nWidth - leftPadding + mLeft,
						nHeight - mVPadding - status);
				mPath.lineTo(leftPadding + topWidth, nHeight- mVPadding );
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);

			} else if (status <= midHeigth + bottomHeigth) {
				mPath.reset();
				mPath.moveTo(mHPadding , nHeight - mVPadding- status);
				mPath.lineTo(nWidth - mHPadding , nHeight- mVPadding - status);
				mPath.lineTo(nWidth - mHPadding, topHeigth
						+ midHeigth + mVPadding);
				mPath.lineTo(nWidth - leftPadding, nHeight- mVPadding);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, topHeigth + midHeigth+ mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);

			} else{
				if(status>( nHeight - 2 * mVPadding)){
					status= (int)(nHeight - 2 * mVPadding);
				}
				float mLeft = (status - midHeigth - bottomHeigth) * nSlope;
				mPath.reset();
				mPath.moveTo(mLeft + mHPadding, nHeight - status
						- mVPadding);
				mPath.lineTo(nWidth - mLeft - mHPadding, nHeight
						- status - mVPadding);
				mPath.lineTo(nWidth - mHPadding, topHeigth
						+ mVPadding);
				mPath.lineTo(nWidth - mHPadding, topHeigth
						+ midHeigth + mVPadding);
				mPath.lineTo(leftPadding + topWidth, nHeight- mVPadding);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, topHeigth + midHeigth+ mVPadding);
				mPath.lineTo(mHPadding, topHeigth + mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			}
		}

		if (mFrameBitmap == null) {
			mFrameBitmap = Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas = new Canvas(mFrameBitmap);

			resetCanvas(info, mFrameCanvas);

			mPaint.reset();
			mPaint.setStyle(Style.FILL);
			mPaint.setAntiAlias(true);
			mPaint.setColor(info.getnFrameColor());

			leftPadding = nWidth * 5 / 16;
			topWidth = nWidth * 6 / 16;
			topHeigth = nHeight * 5 / 16;
			midHeigth = nHeight * 6 / 16;
			bottomHeigth = nHeight * 5 / 16;
			nSlope = leftPadding / topHeigth;
			
			mPath.reset();
			mPath.moveTo(leftPadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth, nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, topHeigth);
			mPath.lineTo(nWidth - nLinePadding, topHeigth + midHeigth);
			mPath.lineTo(leftPadding + topWidth, nHeight - nLinePadding);
			mPath.lineTo(leftPadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, topHeigth + midHeigth);
			mPath.lineTo(nLinePadding, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			// 内框
			mHPadding = nWidth * mPScale;
			mVPadding = nHeight * mPScale;
			topHeigth = topHeigth - mVPadding;
			bottomHeigth = bottomHeigth - mVPadding;

			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding, mVPadding);
			mPath.lineTo(leftPadding + topWidth, mVPadding);
			mPath.lineTo(nWidth - mHPadding, topHeigth + mVPadding);
			mPath.lineTo(nWidth - mHPadding, topHeigth + midHeigth + mVPadding);
			mPath.lineTo(leftPadding + topWidth, nHeight - mVPadding);
			mPath.lineTo(leftPadding, nHeight - mVPadding);
			mPath.lineTo(mHPadding, topHeigth + midHeigth + mVPadding);
			mPath.lineTo(mHPadding, topHeigth + mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
		}

		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 正六边形
	 */
	private void grooveSix(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();
		// 旋转画图
		resetCanvas(info, canvas);
		
		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		if (init) {
			// 画外框
			leftPadding = nWidth / 2;
			topHeigth = nHeight * 3 / 11;
			midHeigth = nHeight * 5 / 11;
			bottomHeigth = nHeight * 3 / 11;
			nSlope = leftPadding / topHeigth;
			 //nBSlope=leftPadding/bottomHeigth;

			// 画内框
			mHPadding = nWidth * mPScale; // 水平方向内外框的距离
			mVPadding = nHeight * mPScale; // 垂直方向内外框的距离
			topHeigth = topHeigth * mScale;
			midHeigth = midHeigth * mScale;
			bottomHeigth = bottomHeigth * mScale;
			
			if (mPath == null) {
				mPath = new Path();
			}
			
		} 
		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		mPaint.reset();
		mPaint.setAntiAlias(true);
		if (isShader) {
			if (alram) {
				mPaint.setStyle(Style.FILL);
				mPaint.setColor(info.getnAlarmTextColor());
			} else {
				mPaint.setShader(myShader);
				mPaint.setStyle(Style.FILL_AND_STROKE);
			}
		} else {
			mPaint.setStyle(Style.FILL);
			if (alram) {
				mPaint.setColor(info.getnAlarmTextColor());
			} else {
				mPaint.setColor(info.getnTextColor());
			}
		}

		double temp = (nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		if (status > 0 && temp * status < 1) {
			status = 1;
		} else {
			status = (int) (status * temp);
		}

		if (status > 0) {
			if (status <= bottomHeigth) {
				float mLeft = status * nSlope;
				mPath.reset();
				mPath.moveTo(leftPadding - mLeft, nHeight - status - mVPadding);
				mPath.lineTo(leftPadding + mLeft, nHeight - status - mVPadding);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			} else if (status < midHeigth + bottomHeigth) {
				mPath.reset();
				mPath.moveTo(mHPadding, nHeight - status - mVPadding);
				mPath.lineTo(nWidth - mHPadding, nHeight - status - mVPadding);
				mPath.lineTo(nWidth - mHPadding, topHeigth + midHeigth
						+ mVPadding);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, topHeigth + midHeigth + mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			} else {

				if (status > (nHeight - 2 * mVPadding)) {
					status = (int) (nHeight - 2 * mVPadding);
				}

				float mLeft = (status - midHeigth - bottomHeigth) * nSlope;
				mPath.reset();
				mPath.moveTo(mLeft + mHPadding, nHeight - status - mVPadding);
				mPath.lineTo(nWidth - mLeft - mHPadding, nHeight - status
						- mVPadding);
				mPath.lineTo(nWidth - mHPadding, topHeigth + mVPadding);
				mPath.lineTo(nWidth - mHPadding, topHeigth + midHeigth
						+ mVPadding);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, topHeigth + midHeigth + mVPadding);
				mPath.lineTo(mHPadding, topHeigth + mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			}
		}
		
		if(mFrameBitmap==null){
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			leftPadding = nWidth / 2;
			topHeigth = nHeight * 3 / 11;
			midHeigth = nHeight * 5 / 11;
			bottomHeigth = nHeight * 3 / 11;
			nSlope = leftPadding / topHeigth;
			
			mPaint.setStyle(Style.FILL);
			mPaint.setAntiAlias(true);
			mPaint.setColor(info.getnFrameColor());
			mPath.reset();
			mPath.moveTo(leftPadding, nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, topHeigth);
			mPath.lineTo(nWidth - nLinePadding, topHeigth + midHeigth);
			mPath.lineTo(leftPadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, topHeigth + midHeigth);
			mPath.lineTo(nLinePadding, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			// 画内框
			mHPadding = nWidth * mPScale; // 水平方向内外框的距离
			mVPadding = nHeight * mPScale; // 垂直方向内外框的距离
			topHeigth = topHeigth * mScale;
			midHeigth = midHeigth * mScale;
			bottomHeigth = bottomHeigth * mScale;

			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding, mVPadding);
			mPath.lineTo(nWidth - mHPadding, topHeigth + mVPadding);
			mPath.lineTo(nWidth - mHPadding, topHeigth + midHeigth + mVPadding);
			mPath.lineTo(leftPadding, nHeight - mVPadding);
			mPath.lineTo(mHPadding, topHeigth + midHeigth + mVPadding);
			mPath.lineTo(mHPadding, topHeigth + mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
		}
		
		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}

	}

	/**
	 * 椭圆
	 */
	private RectF inRectF = null;
	private void grooveSeven(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 方向旋转
		resetCanvas(info, canvas);

		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		if (info.isHasRuler()) {
			nRLx = info.getnRulerLeftTopX();
			nRLy = info.getnRulerLeftTopY();
			nRWidth = info.getnRulerWidth();
			nRHeight = info.getnRulerHigth();
		}

		if (init) {
			
			mPaint.setColor(info.getnFrameColor());
			mPaint.setStyle(Style.FILL);
			
			if (mRectF == null) {
				mRectF = new RectF();
			}
			if (inRectF == null) {
				inRectF = new RectF();
			}

			if (mFrameBitmap==null) {
				mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
						info.getnShowHigth(), Config.ARGB_8888);
				mFrameCanvas=new Canvas(mFrameBitmap);
				
				resetCanvas(info, mFrameCanvas);
				
				mPaint.setColor(info.getnFrameColor());
				mPaint.setAntiAlias(true);
				mPaint.setStyle(Style.FILL);
				
				// 画外框
				mRectF.set(nLinePadding, nLinePadding, nWidth - nLinePadding,
						nHeight - nLinePadding);
				mFrameCanvas.drawOval(mRectF, mPaint);

				// 画内框
				mPaint.setColor(info.getnBackColor());
				mHPadding = nWidth * mPScale; // 水平方向内外框的距离
				mVPadding = nHeight * mPScale; // 垂直方向内外框的距离
				inRectF.set(mRectF.left + mHPadding, mRectF.top + mVPadding,
						mRectF.right - mHPadding, mRectF.bottom - mVPadding);
				mFrameCanvas.drawOval(inRectF, mPaint);
			}

		} 
		
		// 清除
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		// 画数据
		if (status > 0) {
			mPaint.reset();
			mPaint.setXfermode(null);
			mPaint.setAntiAlias(true);
			if (isShader) {
				if (alram) {
					mPaint.setStyle(Style.FILL);
					mPaint.setColor(info.getnAlarmTextColor());
				} else {
					mPaint.setShader(myShader);
					mPaint.setStyle(Style.FILL_AND_STROKE);
				}
			} else {
				mPaint.setStyle(Style.FILL);
				if (alram) {
					mPaint.setColor(info.getnAlarmTextColor());
				} else {
					mPaint.setColor(info.getnTextColor());
				}
			}
			canvas.drawOval(inRectF, mPaint);

			double temp = (nHeight - 2 * mVPadding)
					/ (info.getnShowMax() - info.getnShowMin());
			if (status > 0 && temp * status < 1) {
				status = 1;
			} else {
				status = (int) (temp * status);
			}

			mPaint.setColor(Color.TRANSPARENT);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			canvas.drawRect(mHPadding, mVPadding, nWidth - mHPadding, nHeight
					- status - mVPadding, mPaint);
		}

		// 画标尺
		if(info.isHasRuler()){
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 圆梯形
	 */
	private RectF dRectF = null;
	private void grooveEight(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);

		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		if (init) {
			topHeigth = nHeight / 2;
			leftPadding = nWidth / 2;
			mHPadding = nWidth * (float) 0.084; // 水平方向内外框的距离
			mVPadding = nHeight * (float) 0.084; // 垂直方向内外框的距离
			inTopHeight = topHeigth - mVPadding;

			if (mPath == null) {
				mPath = new Path();
			}
			if (mRectF == null) {
				mRectF = new RectF();
			}
			if (inRectF == null) {
				inRectF = new RectF();
			}
			if (dRectF == null) {
				dRectF = new RectF();
			}

			//外框
			mRectF.set(nLinePadding, nLinePadding, nWidth - nLinePadding,
					nHeight - nLinePadding);

			//内框
			inRectF.set(mRectF.left + mHPadding, mRectF.top + mVPadding,
					mRectF.right - mHPadding, mRectF.bottom - mHPadding);

			//显示区域
			dRectF.set(inRectF.left, inRectF.top,
					inRectF.right, inRectF.bottom);
		} 
		
		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&temp*status<1) {
			status=1;
		}else {
			status=(int)(temp*status);
		}
		
		if(status>0){
			if (!upturn) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				
				canvas.drawArc(inRectF, 180, 180, true, mPaint);
				mPath.reset();
				mPath.moveTo(mHPadding + nLinePadding, nHeight - inTopHeight
						- mVPadding-nLinePadding);
				mPath.lineTo(mHPadding + nLinePadding, nHeight - mVPadding);
				mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight - mVPadding);
				mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight
						- inTopHeight - mVPadding-nLinePadding);
				canvas.drawPath(mPath, mPaint);
				
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + mVPadding, nWidth, nHeight, mPaint);
			}else {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setXfermode(null);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				
				if (status>0&&status <= (nHeight - topHeigth - mVPadding)) {
					mPaint.setStyle(Style.FILL);
					mPath.reset();
					mPath.moveTo(mHPadding + nLinePadding, nHeight - status-mVPadding);
					mPath.lineTo(mHPadding + nLinePadding, nHeight - mVPadding);
					mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight - mVPadding);
					mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight - status-mVPadding);
					canvas.drawPath(mPath, mPaint);
				} else{
					if (status>(nHeight - 2 * mVPadding)) {
						status=(int)(nHeight - 2 * mVPadding);
					}
					// 画下面矩形
					mPaint.setStyle(Style.FILL);
					mPath.reset();
					mPath.moveTo(mHPadding + nLinePadding, nHeight - inTopHeight
							- mVPadding-nLinePadding);
					mPath.lineTo(mHPadding + nLinePadding, nHeight - mVPadding);
					mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight - mVPadding);
					mPath.lineTo(nWidth - mHPadding - nLinePadding, 
							nHeight- inTopHeight - mVPadding-nLinePadding);
					canvas.drawPath(mPath, mPaint);

					// 画上面椭圆
					canvas.drawArc(dRectF, 180, 180, true, mPaint);
					// 清除多余数据
					mPaint.setColor(Color.TRANSPARENT);
					mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
					canvas.drawRect(nLinePadding, mVPadding, nWidth - nLinePadding,
							nHeight - status - mVPadding, mPaint);
					mPaint.setXfermode(null);
				}
			}
		}
		
		
		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			// 画外框
			// 画上面的椭圆
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			mPaint.setColor(info.getnFrameColor());
			mPaint.setStyle(Style.FILL);
			mFrameCanvas.drawArc(mRectF, 180, 180, false, mPaint);

			// 画下面的矩形
			mPath.reset();
			mPath.moveTo(nLinePadding, nHeight - topHeigth - nLinePadding);
			mPath.lineTo(nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, nHeight - topHeigth
					- nLinePadding);
			mFrameCanvas.drawPath(mPath, mPaint);

			// 画内框
			mPaint.setColor(info.getnBackColor());
			mFrameCanvas.drawArc(inRectF, 180, 180, false, mPaint);
			mPath.reset();
			mPath.moveTo(mHPadding + nLinePadding, nHeight - inTopHeight
					- mVPadding-nLinePadding);
			mPath.lineTo(mHPadding + nLinePadding, nHeight - mVPadding);
			mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight - mVPadding);
			mPath.lineTo(nWidth - mHPadding - nLinePadding, nHeight
					- inTopHeight - mVPadding-nLinePadding);
			mFrameCanvas.drawPath(mPath, mPaint);
		}

		// 画标尺
		if(info.isHasRuler()){
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 梯形
	 */
	private void grooveNine(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);

		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		// 画标尺
		if(info.isHasRuler()){
			drawRuler(info,sCanvas);
		}

		if (init) {
			//外框
			leftPadding = nWidth * 9 / 28;
			topWidth = nWidth * 10 / 28;
			nSlope = leftPadding / nHeight;
			
			// 画内框
			mVPadding = nHeight * mPScale; // 垂直方向内外框的距离
			mHPadding = mVPadding*nSlope;  // 水平方向内外框的距离
			inTopWidth = topWidth-2*mHPadding;
			

			if (mPath == null) {
				mPath = new Path();
			}
		} 
		
		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&temp*status<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}
		
		if (status>0) {
			if (!upturn) {
				//与默认方向相反，默认方向向上
				mPaint.reset();
				mPaint.setAntiAlias(true);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				
				mPath.reset();
				mPath.moveTo(leftPadding + mHPadding, mVPadding);
				mPath.lineTo(leftPadding + mHPadding + inTopWidth, mVPadding);
				mPath.lineTo(nWidth - 3*mHPadding, nHeight - mVPadding);
				mPath.lineTo(3*mHPadding, nHeight - mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
				
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + mVPadding, nWidth, nHeight, mPaint);
				
			}else {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setXfermode(null);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				
				if (status>(nHeight - 2 * mVPadding)) {
					status=(int)(nHeight - 2 * mVPadding);
				}
				float mLeft = status * nSlope;
				mPath.reset();
				mPath.moveTo(3*mHPadding + mLeft, nHeight-status-mVPadding);
				mPath.lineTo(nWidth-3*mHPadding - mLeft, nHeight-status-mVPadding);
				mPath.lineTo(nWidth - 3*mHPadding, nHeight - mVPadding);
				mPath.lineTo(3*mHPadding, nHeight - mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			}
		}
		
		
		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setXfermode(null);
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(info.getnFrameColor());
			
			// 画外框
			mPath.reset();
			mPath.moveTo(leftPadding + nLinePadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth - nLinePadding, nLinePadding);
			mPath.lineTo(nWidth - nLinePadding, nHeight - nLinePadding);
			mPath.lineTo(nLinePadding, nHeight - nLinePadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			//画内框
			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding + mHPadding, mVPadding);
			mPath.lineTo(leftPadding + mHPadding + inTopWidth, mVPadding);
			mPath.lineTo(nWidth - 3*mHPadding, nHeight - mVPadding);
			mPath.lineTo(3*mHPadding, nHeight - mVPadding);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
			
		}
		
	}

	/**
	 * 圆底烧瓶
	 */
	private void grooveTen(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();
		
		// 旋转画图
		resetCanvas(info, canvas);
		
		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		if (init) {
			leftPadding = nWidth * 3 / 10;
			topWidth = nWidth * 4 / 10;
			topHeigth = nHeight * 2 / 5;
			bottomHeigth = nHeight * 3 / 5;

			mHPadding = nWidth * mPScale; // 水平方向内外框的距离
			mVPadding = nHeight * mPScale; // 垂直方向内外框的距离

			inTopWidth = topWidth - 2 * mHPadding;
			inBottomHeight = bottomHeigth - 2 * mVPadding;

			if (mPath == null) {
				mPath = new Path();
			}
			if (mRectF == null) {
				mRectF = new RectF();
			}
			if (inRectF == null) {
				inRectF = new RectF();
			}
			mRectF.set(nLinePadding, topHeigth - bottomHeigth / 32
					- nLinePadding, nWidth - nLinePadding, nHeight
					- nLinePadding);

			inRectF.set(mRectF.left + mHPadding, mRectF.top + mVPadding,
					mRectF.right - mHPadding, mRectF.bottom - mVPadding);

		} 

		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&temp*status<1) {
			status=1;
		}else {
			status=(int)(status*temp);
		}
		
		if (status>0) {
			if (upturn) {
				if (status <= nHeight-topHeigth-2*mVPadding) {
					mPaint.reset();
					mPaint.setAntiAlias(true);
					mPaint.setXfermode(null);
					if (isShader) {
						if (alram) {
							mPaint.setStyle(Style.FILL);
							mPaint.setColor(info.getnAlarmTextColor());
						}else {
							mPaint.setShader(myShader);
							mPaint.setStyle(Style.FILL_AND_STROKE);
						}
					}else {
						mPaint.setStyle(Style.FILL);
						if (alram) {
							mPaint.setColor(info.getnAlarmTextColor());
						}else {
							mPaint.setColor(info.getnTextColor());
						}
					}
					canvas.drawOval(inRectF, mPaint);
					
					mPaint.reset();
					mPaint.setAntiAlias(true);
					mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
					mPaint.setStyle(Style.FILL);
				
					canvas.drawRect(nLinePadding, topHeigth + mVPadding-5*nLinePadding, nWidth
							- nLinePadding, nHeight - status -mVPadding,
							mPaint);
					mPaint.setXfermode(null);
				} else{
					if (status>(nHeight-2*mVPadding)) {
						status=(int)(nHeight-2*mVPadding-nLinePadding);
					}
					mPaint.reset();
					mPaint.setAntiAlias(true);
					mPaint.setXfermode(null);
					if (isShader) {
						if (alram) {
							mPaint.setStyle(Style.FILL);
							mPaint.setColor(info.getnAlarmTextColor());
						}else {
							mPaint.setShader(myShader);
							mPaint.setStyle(Style.FILL_AND_STROKE);
						}
					}else {
						mPaint.setStyle(Style.FILL);
						if (alram) {
							mPaint.setColor(info.getnAlarmTextColor());
						}else {
							mPaint.setColor(info.getnTextColor());
						}
					}
					canvas.drawOval(inRectF, mPaint);
					mPath.reset();
					mPath.moveTo(leftPadding + mHPadding, topHeigth + mVPadding);
					mPath.lineTo(leftPadding + mHPadding, nHeight - status
							- mVPadding);
					mPath.lineTo(leftPadding + topWidth - mHPadding, nHeight
							- status - mVPadding);
					mPath.lineTo(leftPadding + topWidth - mHPadding, topHeigth
							+ mVPadding);
					canvas.drawPath(mPath, mPaint);
				}
				
			}else {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setXfermode(null);
				if (isShader) {
					if (alram) {
						mPaint.setStyle(Style.FILL);
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setShader(myShader);
						mPaint.setStyle(Style.FILL_AND_STROKE);
					}
				}else {
					mPaint.setStyle(Style.FILL);
					if (alram) {
						mPaint.setColor(info.getnAlarmTextColor());
					}else {
						mPaint.setColor(info.getnTextColor());
					}
				}
				mPath.reset();
				mPath.moveTo(leftPadding + mHPadding, topHeigth + mVPadding);
				mPath.lineTo(leftPadding + mHPadding, mVPadding);
				mPath.lineTo(leftPadding + topWidth - mHPadding, mVPadding);
				mPath.lineTo(leftPadding + topWidth - mHPadding, topHeigth + mVPadding);
				canvas.drawPath(mPath, mPaint);
				canvas.drawOval(inRectF, mPaint);
				
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setColor(Color.TRANSPARENT);
				mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawRect(0, status + mVPadding, nWidth, nHeight, mPaint);
			}
		}
		
		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			// 画外框
			mPaint.reset();
			mPaint.setXfermode(null);
			mPaint.setAntiAlias(true);
			mPaint.setColor(info.getnFrameColor());
			mPaint.setStyle(Style.FILL);
			
			mPath.reset();
			mPath.moveTo(leftPadding + nLinePadding, topHeigth);
			mPath.lineTo(leftPadding + nLinePadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth - nLinePadding, nLinePadding);
			mPath.lineTo(leftPadding + topWidth - nLinePadding, topHeigth);
			mFrameCanvas.drawPath(mPath, mPaint);

			mFrameCanvas.drawOval(mRectF, mPaint);

			// 画内框
			mPaint.setXfermode(null);
			mPaint.setColor(info.getnBackColor());
			mPaint.setStyle(Style.FILL);
			mPath.reset();
			mPath.moveTo(leftPadding + mHPadding, topHeigth + mVPadding);
			mPath.lineTo(leftPadding + mHPadding, mVPadding);
			mPath.lineTo(leftPadding + topWidth - mHPadding, mVPadding);
			mPath.lineTo(leftPadding + topWidth - mHPadding, topHeigth + mVPadding);
			mFrameCanvas.drawPath(mPath, mPaint);

			mPaint.setStyle(Style.FILL);
			mFrameCanvas.drawOval(inRectF, mPaint);
			
		}
		
		// 画标尺
		if(info.isHasRuler()){
			drawRuler(info,sCanvas);
		}
	}

	/**
	 * 正四边形
	 */
	private void grooveEleven(CommonGraphInfo info, Canvas canvas,Canvas sCanvas, int shapeId,
			DIRECTION eDir, float status, boolean init) {

		eDecorientation = info.geteDirection();
		eRulerDir = info.geteRulerDirection();

		// 旋转画图
		resetCanvas(info, canvas);
		
		//画背景
		if (info.isHasBg()) {
			if (mBgBitmap==null) {
				mBgBitmap=ImageFileTool.getBitmap(info.getsPic());
			}
			if (mBgBitmap!=null) {
				sCanvas.drawBitmap(mBgBitmap, null, dstRectF, mBitmapPaint);
				//this
			}
		}
		
		if (init) {
			leftPadding = nWidth / 2;
			topHeigth = nHeight / 2;
			mHPadding = nWidth * mPScale; // 水平方向内外框的距离
			mVPadding = nHeight * mPScale; // 垂直方向内外框的距离
			nSlope = leftPadding / (topHeigth + 1);

			if (mPath == null) {
				mPath = new Path();
			}

		}
		// 清除数据
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setXfermode(null);
		if (isShader) {
			if (alram) {
				mPaint.setStyle(Style.FILL);
				mPaint.setColor(info.getnAlarmTextColor());
			}else {
				mPaint.setShader(myShader);
				mPaint.setStyle(Style.FILL_AND_STROKE);
			}
		}else {
			mPaint.setStyle(Style.FILL);
			if (alram) {
				mPaint.setColor(info.getnAlarmTextColor());
			}else {
				mPaint.setColor(info.getnTextColor());
			}
		}

		double temp=(nHeight - 2 * mVPadding)
				/ (info.getnShowMax() - info.getnShowMin());
		if (status>0&&temp*status<1) {
			status=1;
		}else {
			status=(int)(temp*status);
		}
		
		if (status>0) {
			if (status < nHeight - topHeigth - mVPadding) {
				float mLeft = status * nSlope;
				mPath.reset();
				mPath.moveTo(leftPadding - mLeft, nHeight - status - mVPadding);
				mPath.lineTo(nWidth - leftPadding + mLeft, nHeight - status
						- mVPadding);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			} else {
				if (status>(nHeight - 2 * mVPadding)) {
					status=(int)(nHeight - 2 * mVPadding);
				}
				float mLeft = (status - nHeight / 2 + mVPadding) * nSlope;
				mPath.reset();
				mPath.moveTo(mLeft + mHPadding, nHeight - status - mVPadding);
				mPath.lineTo(nWidth - mLeft - mHPadding, nHeight - status
						- mVPadding);
				mPath.lineTo(nWidth - mHPadding, topHeigth);
				mPath.lineTo(leftPadding, nHeight - mVPadding);
				mPath.lineTo(mHPadding, topHeigth);
				mPath.close();
				canvas.drawPath(mPath, mPaint);
			}
		}
		
		
		if (mFrameBitmap==null) {
			mFrameBitmap= Bitmap.createBitmap(info.getnShowWidth(),
					info.getnShowHigth(), Config.ARGB_8888);
			mFrameCanvas=new Canvas(mFrameBitmap);
			
			resetCanvas(info, mFrameCanvas);
			
			// 画外框
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setColor(info.getnFrameColor());
			mPaint.setStyle(Style.FILL);
			mPath.reset();
			mPath.moveTo(leftPadding, 0);
			mPath.lineTo(nWidth, topHeigth);
			mPath.lineTo(leftPadding, nHeight);
			mPath.lineTo(0, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);

			// 画内框
			mPaint.setColor(info.getnBackColor());
			mPath.reset();
			mPath.moveTo(leftPadding, mVPadding - nLinePadding);
			mPath.lineTo(nWidth - mHPadding + nLinePadding, topHeigth);
			mPath.lineTo(leftPadding, nHeight - mVPadding + nLinePadding);
			mPath.lineTo(mHPadding - nLinePadding, topHeigth);
			mPath.close();
			mFrameCanvas.drawPath(mPath, mPaint);
			
			
		}
		
		// 画标尺
		if (info.isHasRuler()) {
			drawRuler(info,sCanvas);
		}

	}

	/**
	 * 旋转，重新调整
	 */
	private void resetCanvas(CommonGraphInfo info, Canvas canvas) {
		if (reset) {
			if (info.isHasRuler()) {
				nRLx = info.getnRulerLeftTopX();
				nRLy = info.getnRulerLeftTopY();
				nRWidth = info.getnRulerWidth();
				nRHeight = info.getnRulerHigth();
			}

			switch (info.geteDirection()) {
			case TOWARD_TOP:
				// 默认方向
				nWidth = info.getnShowWidth();
				nHeight = info.getnShowHigth();
				break;
			case TOWARD_BOTTOM:
				canvas.rotate(180, info.getnShowWidth() / 2,(info.getnShowHigth()+1) / 2);
				nWidth = info.getnShowWidth();
				nHeight = info.getnShowHigth();
				break;
			case TOWARD_LEFT:
				canvas.rotate(-90, info.getnShowHigth() / 2,(info.getnShowHigth()+1) / 2);
				nWidth = info.getnShowHigth();
				nHeight = info.getnShowWidth();
				break;
			case TOWARD_RIGHT:
				canvas.rotate(90, info.getnShowWidth() / 2,(info.getnShowWidth()+1)/ 2);
				nWidth = info.getnShowHigth();
				nHeight = info.getnShowWidth();
				break;
			}
		}
	}

	/**
	 * 画标尺
	 */
	private void drawRuler(CommonGraphInfo info,Canvas canvas) {
		if (info.isHasRuler()&&info.isbShowRuleValue()) {
			// 刻度
			if (reset) {
				rItem = new RulerItem(null);
				rItem.setLineColor(info.getnRulingColor());
				rItem.setnMainRuling(info.getnMainRuling());
				rItem.setnRuling(info.getnRuling());
				rItem.setWidth(nRWidth);
				rItem.setHeight(nRHeight);
				rItem.setLineWidth(1);
				rItem.setbShowMinorRuling(info.isbShowRuling());
				rItem.setnLTX(nRLx);
				rItem.setnLTY(nRLy);
				rItem.setnItemX(info.getnLeftTopX());
				rItem.setnItemY(info.getnLeftTopY());
				rItem.setnItemRX(info.getnLeftTopX()+info.getnWidth());
				rItem.setnItemRY(info.getnLeftTopY()+info.getnHeigth());
				rItem.setnMin(info.getnShowMin());
				rItem.setnMax(info.getnShowMax());
				rItem.setnIncrease(1);
				rItem.seteRulerDir(eRulerDir);
			}
			rItem.setLineColor(info.getnRulingColor());
			rItem.draw(mPaint, canvas);
		}
	}

	/**
	 * 判断是否有刻度，是否有标尺，具体对应哪个类型
	 */
	private void grooveInfo(CommonGraphInfo info) {
		if (info.getnShapeId() < 21) {
			grooveInfoOne(info);
		} else if (info.getnShapeId() < 41) {
			grooveInfoTwo(info);
		} else if (info.getnShapeId() < 55) {
			grooveInfoThree(info);
		}else if(info.getnShapeId()<75){
			grooveInfoFour(info);
		}
	}

	/**
	 * 小于21
	 */
	private void grooveInfoOne(CommonGraphInfo info) {

		// 有标尺
		info.setHasRuler(true);
		info.setHasBg(false);
		resetDieTwo(info);
		switch (info.getnShapeId()) {
		case 1:
			upturn = false;
			resetDirOne(info);
			break;
		case 2:
			upturn = false;
			resetDirOne(info);
			break;
		case 3:
			upturn = false;
			resetDirOne(info);
			break;
		case 4:
			upturn=false;
			resetDirOne(info);
			break;
		case 5:
			//resetDieTwo(info);
			break;
		case 6:
			//resetDieTwo(info);
			break;
		case 7:
			// resetDirOne(info);
			//resetDieTwo(info);
			break;
		case 8:
			upturn=true;
			//resetDieTwo(info);
			// resetDirOne(info);
			break;
		case 9:
			upturn=true;
			//resetDieTwo(info);
			break;
		case 10:
			upturn=true;
			//resetDieTwo(info);
			// resetDirOne(info);
			break;
		case 11:
			upturn = true;
			info.setnShapeId(1);
			//resetDieTwo(info);
			break;
		case 12:
			upturn = true;
			info.setnShapeId(2);
			//resetDieTwo(info);
			break;
		case 13:
			upturn = true;
			info.setnShapeId(3);
			//resetDieTwo(info);
			break;
		case 14:
			info.setnShapeId(7);
			//resetDieTwo(info);
			break;
		case 15:
			upturn=true;
			info.setnShapeId(4);
			//resetDieTwo(info);
			break;
		case 16:
			upturn=false;
			info.setnShapeId(8);
			resetDirOne(info);
			break;
		case 17:
			upturn=false;
			info.setnShapeId(9);
			resetDirOne(info);
			break;
		case 18:
			upturn=false;
			info.setnShapeId(10);
			resetDirOne(info);
			break;
		case 19:
			info.setnShapeId(11);
			//resetDieTwo(info);
			break;
		case 20:
			info.setnShapeId(7);
			//resetDieTwo(info);
			break;
		}
	}

	/**
	 * 小于41
	 */
	private void grooveInfoTwo(CommonGraphInfo info) {
		// 有标尺
		info.setHasRuler(true);
		info.setHasBg(true);
		resetDieTwo(info);
		switch (info.getnShapeId()) {
		case 21:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(1);
			break;
		case 22:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(2);
			break;
		case 23:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(3);
			break;
		case 24:
			upturn=false;
			resetDirOne(info);
			info.setnShapeId(4);
			break;
		case 25:
			// resetDirOne(info);
			//resetDieTwo(info);
			info.setnShapeId(5);
			break;
		case 26:
			// resetDirOne(info);
			//resetDieTwo(info);
			info.setnShapeId(6);
			break;
		case 27:
			// resetDirOne(info);
			//resetDieTwo(info);
			info.setnShapeId(7);
			break;
		case 28:
			upturn=true;
			//resetDieTwo(info);
			// resetDirOne(info);
			info.setnShapeId(8);
			break;
		case 29:
			upturn=true;
			//resetDieTwo(info);
			info.setnShapeId(9);
			break;
		case 30:
			upturn=true;
			//resetDieTwo(info);
			info.setnShapeId(10);
			// resetDirOne(info);
			break;
		case 31:
			upturn = true;
			info.setnShapeId(1);
			//resetDieTwo(info);
			break;
		case 32:
			upturn = true;
			info.setnShapeId(2);
			//resetDieTwo(info);
			break;
		case 33:
			upturn = true;
			info.setnShapeId(11);
			//resetDieTwo(info);
			break;
		case 34:
			info.setnShapeId(3);
			//resetDieTwo(info);
			break;
		case 35:
			upturn=true;
			info.setnShapeId(4);
			//resetDieTwo(info);
			break;
		case 36:
			upturn=false;
			info.setnShapeId(8);
			resetDirOne(info);
			break;
		case 37:
			upturn=false;
			info.setnShapeId(9);
			resetDirOne(info);
			break;
		case 38:
			upturn=false;
			info.setnShapeId(10);
			resetDirOne(info);
			break;
		case 39:
			info.setnShapeId(7);
			//resetDieTwo(info);
			break;
		case 40:
			info.setnShapeId(7);
			//resetDieTwo(info);
			break;
		}
	}

	/**
	 * 小于55
	 */
	private void grooveInfoThree(CommonGraphInfo info) {
		info.setHasRuler(false);
		info.setHasBg(true);
		switch (info.getnShapeId()) {
		case 41:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(1);
			break;
		case 42:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(2);
			break;
		case 43:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(3);
			break;
		case 44:
			upturn=false;
			resetDirOne(info);
			info.setnShapeId(4);
			break;
		case 45:
			// resetDirOne(info);
			info.setnShapeId(5);
			break;
		case 46:
			// resetDirOne(info);
			info.setnShapeId(6);
			break;
		case 47:
			// resetDirOne(info);
			info.setnShapeId(7);
			break;
		case 48:
			upturn=true;
			//resetDieTwo(info);
			// resetDirOne(info);
			info.setnShapeId(8);
			break;
		case 49:
			upturn=true;
			//resetDieTwo(info);
			info.setnShapeId(9);
			break;
		case 50:
			upturn=true;
			//resetDieTwo(info);
			info.setnShapeId(10);
			// resetDirOne(info);
			break;
		case 51:
			upturn = true;
			info.setnShapeId(1);
			//resetDieTwo(info);
			break;
		case 52:
			upturn = true;
			info.setnShapeId(2);
			//resetDieTwo(info);
			break;
		case 53:
			upturn = true;
			info.setnShapeId(11);
			//resetDieTwo(info);
			break;
		case 54:
			info.setnShapeId(3);
			//resetDieTwo(info);
			break;
		}

	}

	/**
	 * 小于75
	 */
	private void grooveInfoFour(CommonGraphInfo info){
		info.setHasRuler(false);
		info.setHasBg(true);
		switch (info.getnShapeId()) {
		case 55:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(1);
			break;
		case 56:
			upturn = false;
			resetDirOne(info);
			info.setnShapeId(2);
			break;
		case 57:
			//upturn = false;
			resetDirOne(info);
			info.setnShapeId(3);
			break;
		case 58:
			upturn=false;
			resetDirOne(info);
			info.setnShapeId(4);
			break;
		case 59:
			// resetDirOne(info);
			info.setnShapeId(5);
			break;
		case 60:
			// resetDirOne(info);
			info.setnShapeId(6);
			break;
		case 61:
			// resetDirOne(info);
			info.setnShapeId(7);
			break;
		case 62:
			upturn=true;
			//resetDieTwo(info);
			// resetDirOne(info);
			info.setnShapeId(8);
			break;
		case 63:
			upturn=true;
			//resetDieTwo(info);
			info.setnShapeId(9);
			break;
		case 64:
			upturn=true;
			//resetDieTwo(info);
			info.setnShapeId(10);
			// resetDirOne(info);
			break;
		case 65:
			upturn = true;
			info.setnShapeId(1);
			//resetDieTwo(info);
			break;
		case 66:
			upturn = true;
			info.setnShapeId(2);
			//resetDieTwo(info);
			break;
		case 67:
			//upturn = true;
			info.setnShapeId(3);
			//resetDieTwo(info);
			break;
		case 68:
			info.setnShapeId(8);
			//resetDieTwo(info);
			break;
		case 69:
			upturn=true;
			info.setnShapeId(4);
			//resetDieTwo(info);
			break;
		case 70:
			upturn=false;
			info.setnShapeId(8);
			resetDirOne(info);
			break;
		case 71:
			upturn=false;
			info.setnShapeId(9);
			resetDirOne(info);
			break;
		case 72:
			upturn=false;
			info.setnShapeId(10);
			resetDirOne(info);
			break;
		case 73:
			info.setnShapeId(11);
			//resetDieTwo(info);
			break;
		case 74:
			info.setnShapeId(7);
			//resetDieTwo(info);
			break;
		}
	}
	
	/**
	 * 转变方向
	 */
	private void resetDirOne(CommonGraphInfo info) {
		switch (info.geteDirection()) {
		case TOWARD_LEFT:
			//info.seteRulerDirection(DIRECTION.TOWARD_TOP);
			info.seteDirection(DIRECTION.TOWARD_RIGHT);
			break;
		case TOWARD_RIGHT:
			info.seteDirection(DIRECTION.TOWARD_LEFT);
			//info.seteRulerDirection(DIRECTION.TOWARD_BOTTOM);
			break;
		case TOWARD_TOP:
			info.seteDirection(DIRECTION.TOWARD_BOTTOM);
			break;
		case TOWARD_BOTTOM:
			info.seteDirection(DIRECTION.TOWARD_TOP);
			break;
		}
	}

	/**
	 * 转变方向
	 */
	private void resetDieTwo(CommonGraphInfo info) {
		switch (info.geteDirection()) {
		case TOWARD_LEFT:
			info.seteRulerDirection(DIRECTION.TOWARD_TOP);
			//info.seteDirection(DIRECTION.TOWARD_RIGHT);
			break;
		case TOWARD_RIGHT:
			//info.seteDirection(DIRECTION.TOWARD_LEFT);
			info.seteRulerDirection(DIRECTION.TOWARD_BOTTOM);
			break;
		case TOWARD_TOP:
			info.seteRulerDirection(DIRECTION.TOWARD_RIGHT);
			break;
		case TOWARD_BOTTOM:
			info.seteRulerDirection(DIRECTION.TOWARD_LEFT);
			break;
		}
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}
}
