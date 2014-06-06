package com.android.Samkoonhmi.graphicsdrawframe;

import java.util.ArrayList;
import java.util.Vector;

import javax.mail.internet.NewsAddress;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.TableTextInfo;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;

/**
 * 表格
 */
public class DragTable {

	private static final String TAG = "DragTable";
	private static final int HANDLER_MOVE = 0;
	private static final int HANDLER_CLICK = 1;
	private static final int HANDLER_BAR=2;
	private static final int HANDLER_LONG_TIME=3;
	private static final int HANDLER_CLEAR_BG=4;
	private static final int HANDLER_UPDATE_CLICK=5;
	private static final int HANDLER_ROLL=6;//翻页滚动
	private static final int CLICK_LOOPER=7;
	private static final int LONG_PRESS=8;
	private Paint paint;
	private Rect mFrameRect;
	private DragTableInfo info;
	private Context mContext;
	// 水平方向总的长度
	private int nAllHLen;
	// 垂直方向总的长度
	private int nAllVlen;
	private SKItems items;
	private Point mLPPoint;//第一行,第一列坐标
	private Point mRBPoint;//右边底部坐标
	//private Rect mHRectBar; //水平滚动条捕捉事件区域
	//private Rect mVRectBar; //垂直滚动条捕捉事件区域
	// 水平滚动条
	private MoveItem sHItem;
	// 垂直滚动条
	private MoveItem sVItem;
	private static int nBarLen=24;//滑动块宽度
	private IAddListener iAddListener; //添加事件
	private IPageTurning iPageTurning; //翻页事件
	public boolean drawLoadItem;      //画上or下页的控件
	private PageItem mPageItem;
	//private ArrayList<VTitleItem> mVTitleItems;竖标题
	private HTitleItem mHTitleItem;
	private int nRank;//显示列数
	private int nDataRank;//实际数据列数
	private Vector<RowCell> mRowCells;
	private IClickListener iClickListener;
	//private Rect mHRect;
	private Bitmap mHBGBitmap;
	private boolean isShowBar;//是否显示滚动条
	private DisplayMetrics dis;
	private UIHandler mUIHandler;
	
	
	//syb add
	private int mTouchColum;
	
	/**
	 * @param row-行数
	 * @param rank-列数
	 * @param shade-true，有阴影
	 */
	public DragTable(DragTableInfo info, Context context,SKItems item,boolean shade) {
		this.info = info;
		this.mContext = context;
		this.items=item;
		if (info.getmAlign()==null) {
			info.setmAlign(TEXT_PIC_ALIGN.CENTER);
		}
		mUIHandler=new UIHandler(mContext.getMainLooper());
	}


	/**
	 * 初始化表格信息
	 * @param list-表格显示内容
	 */
	public void init(ArrayList<ArrayList<TableTextInfo>> list) {
		
		if (paint==null) {
			paint=new Paint();
		}
		
		//初始化每一小格
		initTableItem(list);

		if (mFrameRect==null) {
			mFrameRect = new Rect();
		}
		mFrameRect.set(info.getnLeftTopX(), 
				info.getnLeftTopY(), 
				info.getnLeftTopX()+info.getnWidth(),
				info.getnLeftTopY()+info.getnHeight());
		
	}

	/**
	 * 表格刷新
	 */
	public void drawTable(){
		isHMove=false;
		SKSceneManage.getInstance().onRefresh(items);
	}
	
	//处于画图中，滑动事件无效
	public void draw(Canvas canvas) {
		if (info==null||mPageItem==null) {
			Log.d(TAG, "DragTableInfo info=null");
			return;
		}
		drawTable(paint,canvas,isHMove);
		
		isShowBar=true;
		if (mPageItem.nWidth<72||mPageItem.nHeight<72) {
			isShowBar=false;
		}
		if (isShowBar) {
			drawScrollBar(paint,canvas);
		}
	}
	
	/**
	 * 画表格
	 */
	private void drawTable(Paint paint, Canvas canvas,boolean isHMove) {
		
		if (mFrameRect==null||mHTitleItem==null) {
			return;
		}
		//画横标题的背景
//		if (mHRect==null) {
//			mHRect=new Rect(mFrameRect.left, mFrameRect.top, mFrameRect.right,
//					mFrameRect.top+mHTitleItem.nHeight);
//		}
		
		if (mHBGBitmap==null) {
			mHBGBitmap=ImageFileTool.getBitmap(R.drawable.title_row_bg, 
					SKSceneManage.getInstance().mContext);
		}
		
		//画横标题
		drawHTitle(paint,canvas);
		
		//画文本
		drawText(paint,canvas,isHMove);
		
		//画外框
		paint.setStyle(Style.STROKE);
		paint.setColor(info.getnFrameColor());
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		canvas.drawRect(mFrameRect.left+1, mFrameRect.top+1, mFrameRect.right-1, mFrameRect.bottom-1, paint);
		//canvas.drawRect(mFrameRect, paint);
	}
	
	/**
	 * 初始化表格每一小项
	 */
	private double nTitleHeight=0;
	private void initTableItem(ArrayList<ArrayList<TableTextInfo>> list){
		
		if (info.getmRowHeight()==null||info.getmRowHeight().size()==0) {
			nTitleHeight = info.getnHeight() / info.getnRow();
		}else {
			nTitleHeight=info.getmRowHeight().get(0);
		}
		
		//第一行,第一列坐标
		mLPPoint=new Point(info.getnLeftTopX(),info.getnLeftTopY()+(int)nTitleHeight);
		nRank=info.getnRank();
		nDataRank=info.getnDataRank();
		
		//右边底部坐标
		if (mRBPoint==null) {
			mRBPoint=new Point(info.getnWidth()+info.getnLeftTopX(),
					info.getnLeftTopY()+info.getnHeight());
		}
		
		/**
		 * 初始化标题
		 */
		initTitle();
		
		/**
		 * 初始化页面
		 */
		initPage();
		
		/**
		 * 初始化滚动条
		 */
		initBar();
	}
	
	/**
	 * 初始化标题
	 */
	private void initTitle(){
		Rect hRect=new Rect(info.getnLeftTopX(), info.getnLeftTopY(), 
				info.getnLeftTopX()+info.getnWidth(), 
				info.getnLeftTopY()+(int)nTitleHeight);
		float nSize=info.getnTitleFontSize();
		if (null == dis) {
			dis=new DisplayMetrics();
			dis.density = (float) 1.3125;
			dis.densityDpi = 210;
			dis.scaledDensity = (float) 1.3125;
			dis.xdpi = (float) 225.77777;
			dis.ydpi = (float) 225.77777;
		}
		nSize=TypedValue.applyDimension(2, nSize, dis);
		mHTitleItem=new HTitleItem(info.getnWidth(), (int)nTitleHeight, nRank,nDataRank, hRect);
		mHTitleItem.mRowWidth=info.getmRowWidth();
		mHTitleItem.mRowHeight=info.getmRowHeight();
		mHTitleItem.nFontColor=info.getnTitleFontColor();
		mHTitleItem.nPageBgColor=info.getnTitleBackcolor();
		mHTitleItem.nFontSize=nSize;
		mHTitleItem.nLineColor=info.getnLineColor();
		mHTitleItem.nLeftX=info.getnLeftTopX();
		mHTitleItem.nLeftY=info.getnLeftTopY();
		mHTitleItem.mAlign=info.getmAlign();
		mHTitleItem.typeface=info.getmHTypeFace();
		mHTitleItem.nAlpha=info.getnAlpha();
		mHTitleItem.mContext=mContext;
		mHTitleItem.initData();
		mHTitleItem.initPage();
		
	}
	
	/**
	 * 初始化
	 */
	private void initPage(){
		
		Rect sRect=new Rect(mLPPoint.x, mLPPoint.y, mRBPoint.x, mRBPoint.y);
		
		mPageItem=new PageItem(mRBPoint.x-mLPPoint.x, mRBPoint.y-mLPPoint.y,
				info.getnRow()-1, nRank,nDataRank,sRect);
		
		float nVSize=info.getnVTitleFontSize();
		float nSize=info.getnTextFontSize();
		if (null == dis) {
			dis=new DisplayMetrics();
			dis.density = (float) 1.3125;
			dis.densityDpi = 210;
			dis.scaledDensity = (float) 1.3125;
			dis.xdpi = (float) 225.77777;
			dis.ydpi = (float) 225.77777;
		}
		nVSize=TypedValue.applyDimension(2, nVSize, dis); 
		nSize=TypedValue.applyDimension(2, nSize, dis); 
		
		mPageItem.nPageBgColor=info.getnTableBackcolor();
		mPageItem.nPageIndex=1;
		mPageItem.nLeftX=mLPPoint.x;
		mPageItem.nLeftY=mLPPoint.y;
		mPageItem.nFontColor=info.getnTextFontColor();
		mPageItem.nFontSize=nSize;
		mPageItem.nLineColor=info.getnLineColor();
		mPageItem.mAlign=info.getmAlign();
		mPageItem.nVFontColor=info.getnVTitleFontColor();
		mPageItem.nVFontSize=nVSize;
		mPageItem.nVBgColor=info.getnVTitleBackcolor();
		mPageItem.vTypeface=info.getmVTypeFace();
		mPageItem.typeface=info.getmTypeFace();
		mPageItem.mRowWidth=info.getmRowWidth();
		mPageItem.nAlpha=info.getnAlpha();
		mPageItem.setmRowHeight(info.getmRowHeight());
		
		mPageItem.initData();
	
	}
	
	/**
	 * 初始化滚动条
	 */
	private void initBar(){
		
		info.setnHBarLen(mRBPoint.x-mLPPoint.x-3*nBarLen);
		info.setnVBarLen(mRBPoint.y-mLPPoint.y-3*nBarLen);
		
		/**
		 * 水平滚动条捕捉事件的区域
		 */
		//mHRectBar=new Rect(mLPPoint.x, mRBPoint.y-2*nBarLen, mRBPoint.x-nBarLen, mRBPoint.y);
		
		/**
		 * 垂直滚动条捕捉事件的区域
		 */
		//mVRectBar=new Rect(mRBPoint.x-2*nBarLen,mLPPoint.y,mRBPoint.x,mRBPoint.y-nBarLen);
		
		sHItem=new MoveItem(mContext);
		sHItem.listener=listener;
		
		sHItem.nWidth=mRBPoint.x-mLPPoint.x-nBarLen+1;
		sHItem.nHeight=nBarLen;
		sHItem.nLen=nBarLen;
		sHItem.nLeftX=mLPPoint.x+1;
		sHItem.nLeftY=mRBPoint.y-nBarLen;
		sHItem.setnAllLen(0);
		sHItem.eDirection=DIRECTION.LEVEL;
		sHItem.mVelocity=(int)(mPageItem.nCellWidth);
		sHItem.init();
		
		sVItem=new MoveItem(mContext);
		sVItem.listener=listener;
		sVItem.nWidth=nBarLen;
		sVItem.nHeight=mRBPoint.y-mLPPoint.y-nBarLen+1;
		sVItem.nLen=nBarLen;
		sVItem.nLeftX=mRBPoint.x-nBarLen;
		sVItem.nLeftY=mLPPoint.y+1;
		sVItem.setnAllLen(0);
		sVItem.nRow=info.getnRow()-1;
		sVItem.eDirection=DIRECTION.VERTICAL;
		sVItem.mVelocity=(int)(mPageItem.nCellHeight);
		sVItem.init();
		
		nAllHLen = 0;
		nAllVlen = 0;
	}
	
	/**
	 * 更新表格信息
	 * @param list-行的信息
	 * @param row-第几行
	 */
	public void updateData(ArrayList<String> list,int row){
		
	}
	
	
	/**
	 * 画中间文本表格
	 */
	private void drawText(Paint paint,Canvas canvas,boolean isHMove){
		mPageItem.draw(canvas);
	}
	
	
	/**
	 * 画横标题
	 */
	private void drawHTitle(Paint paint,Canvas canvas){
		mHTitleItem.draw(canvas);
	}
		
	
	/**
	 * 画滚动条
	 */
	private void drawScrollBar(Paint paint,Canvas canvas){
		
		if (nAllHLen>0) {
			sHItem.draw(canvas);
		}
		
		if (nAllVlen>0) {
			sVItem.draw(canvas);
		}
		
		if (nAllHLen>0||nAllVlen>0) {
			isShowBar=true;
			paint.setStyle(Style.FILL);
			paint.setColor(Color.rgb(245, 245, 245));
			canvas.drawRect(mFrameRect.right-nBarLen, mFrameRect.bottom-nBarLen,
					mFrameRect.right, mFrameRect.bottom, paint);
		}else {
			isShowBar=false;
		}
	}
	
	/**
	 * 点击滚动时，颜色变换
	 */
	public void scrollBarClick(int index,boolean isHClick){
		
	}
	
	
	/**
	 * 拖动事件
	 */
	private float downX = 0;
	private float downY = 0;
	private boolean isHMove = true;// 是否水平移动,true-表示水平移动
	private boolean isMove = false;// 是否移动
	private int nClickCount=0;
	private int nRowIndex=0;
	private long nClickTime=0;
	private boolean isDrag;
	private boolean isClickDown;
	private float nDifferX;
	private float nDifferY;
	private boolean bLongEvent;
	//syb add to get the column that touch on
	public int getTouchColum(float X){
		try{
			double x_click = X-info.getnLeftTopX();//距表格最左边位置
			//表格拖动了多少？
			//x_left += sHItem.getnMoveLen();
			
			double x_start=0;
			int Max = info.getmRowWidth().size();
			x_start=-sHItem.getnMoveLen();
			for(int i=0;i<info.getnDataRank();i++){
			
				double len=0;
				if(i<Max){
					len = info.getmRowWidth().get(i);
				}else{
					len = mPageItem.nCellWidth;
				}
				
				if(x_start<=x_click&&x_click<(x_start+len)){
					return i;
				}
		
				x_start +=len;
				
			}
			//表格列宽多少？
			//return ((int)x_left/((int)info.getnWidth()/info.getnRank()));
			return -1;
		}catch(Exception e){
			return -1;
		}
	}
	
	/**
	 * 针对 没有水平滚动条的
	 * 获取指定表头单元格的Rect 
	 * @param index  指定单元格， 从0开始
	 * @return
	 */
	
	public Rect getHTitleRect(int index){
		if (index < 0  ||  index >= info.getnDataRank()) {
			return null;
		}
		
		double x_start=0;
		Rect rect = new Rect();
		x_start = -sHItem.getnMoveLen();
		for( int i = 0; i < index ; i++){
			x_start += info.getmRowWidth().get(i);
		}
		rect.left = (int) x_start;
		rect.right = (int)(x_start + info.getmRowWidth().get(index));
		rect.top =  mHTitleItem.getmShowRect().top;
		rect.bottom = mHTitleItem.getmShowRect().bottom;
		
		return rect;
	}
	
	/**
	 * 
	 * @param X---当前点击的X坐标
	 * @param Y---当前点击的Y坐标
	 * @param bottom---当前是否在bottom的位置
	 * @return
	 */
	public Rect getContentRect(int X , int Y, boolean bBottom){
		Rect rect = new Rect(0, 0 ,0 ,0);
		
		// 计算Rect的left, right // 
		double x_click = X - info.getnLeftTopX();//距表格最左边位置
		double x_start=0;
		int Max = info.getmRowWidth().size();
		x_start=-sHItem.getnMoveLen();
		for(int i = 0;i < info.getnDataRank();i++){
			double len=0;
			if( i < Max){
				len = info.getmRowWidth().get(i);
			}else{
				len = mPageItem.nCellWidth;
			}
			
			if(x_start <= x_click && x_click < ( x_start + len )){
				rect.left = (int)(x_start + info.getnLeftTopX());
				rect.right = (int)(x_start + len + info.getnLeftTopX());
				break;
			}
			x_start += len;
		}
		
		//计算 Rect的top、bottom
		double y_start = mHTitleItem.getmShowRect().bottom ;  
		if (bBottom) {
			y_start -= 24;
		}
		for(int i = 0; i < info.getnRow(); i++){
			double heigh = info.getmRowHeight().get(i);
			if (y_start < Y  &&  Y < y_start + heigh ) {
				rect.top = (int) y_start;
				rect.bottom = (int) (y_start +heigh);
				break;
			}
			y_start += heigh;
		}

		// 返回rect
		if((rect.left == 0 && rect.right == 0) || (rect.top == 0 && rect.bottom == 0)){ //获取rect 失败
			return null;
		}
		else {
			return rect;
		}
		
	}
	
	private Point downPoint = new Point();
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		
		float X = event.getX();
		float Y = event.getY();
		
		if(mUIHandler==null){
			return false;
		}
		
		if (X < info.getnLeftTopX() || Y < info.getnLeftTopY()
				|| X > (info.getnLeftTopX() + info.getnWidth())
				|| Y > info.getnLeftTopY() + info.getnHeight()) {
			if (isClickDown) {
				mUIHandler.removeMessages(LONG_PRESS);
				mUIHandler.removeMessages(HANDLER_CLICK);
			}
			isClickDown=false;
			return false;
		}
		
		//触摸标题栏响应
		if(mHTitleItem.getmShowRect()!=null){
			if(mHTitleItem.getmShowRect().contains((int)X,(int) Y)){
				if(mHTitleItem.onTouchEvent(event)){
					mUIHandler.removeMessages(LONG_PRESS);
					mUIHandler.removeMessages(HANDLER_CLICK);
					return false;
				}
			}
		}
		/**
		 * 事件分类：根据点击位置事件
		 * 1-水平方向  左边按钮,滑动块,右边按钮
		 * 2-垂直方向  顶部按钮,滑动块,底部按钮
		 *         把宽分成10份，左边6份滑动，右边4份拖动
		 * isPaging=true 启动翻页事件
		 * isPading=false 启动滑动事件
		 */
		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			//点击事件-按下
			isMove=false;
			isDrag=false;
			bLongEvent=true;
			isSingleClick = false;
			//获得触摸列 
			mTouchColum = getTouchColum(X);
			downPoint.set((int)event.getX(), (int)event.getY());
			
			isClickDown=onClickDown(X,Y,event);
			mUIHandler.removeMessages(LONG_PRESS);
			mUIHandler.sendEmptyMessageDelayed(LONG_PRESS, 1000);
			mUIHandler.removeMessages(HANDLER_CLICK);
			mUIHandler.sendEmptyMessageDelayed(HANDLER_CLICK, 400);
		}
		
		if (event.getAction()==MotionEvent.ACTION_CANCEL
				||event.getAction()==MotionEvent.ACTION_UP) {
			isClickDown=false;
			downX=X;
			downY=Y;
			
			//求出在X方向、Y方向移动距离的最大值
			int distance = (int)Math.max(Math.abs(downX- downPoint.x), Math.abs(downY - downPoint.y));
			if (isSingleClick && event.getAction() == MotionEvent.ACTION_UP  && distance  < 30 && isDrag) {
				if (iClickListener != null) {
					RowCell item = mPageItem.getRowInfo();
					if (item != null) {
						iClickListener.onClick(item.nClickIndex, item.gid, item.aid, 0);
					}
				}
			}
			mUIHandler.removeMessages(LONG_PRESS);
			mUIHandler.removeMessages(HANDLER_CLICK);
		}
		
		if (isDrag) {
			
			//先分水平拖动or垂直拖动
			nDifferX=Math.abs(X-downX);
			nDifferY=Math.abs(Y-downY);
			if (nDifferY>nDifferX) {
				//垂直移动
				if (X<info.getnLeftTopX()+(info.getnWidth()-24)*0.7) {
					//翻页
					if (nDifferY<info.getnHeight()*0.33) {
						if (nDifferY>5) {
							nClickCount=0;
						}
						return true;
					}
					mUIHandler.removeMessages(LONG_PRESS);
					mUIHandler.removeMessages(HANDLER_CLICK);
					if (Y-downY>0) {
						turnPage(0);
					}else {
						turnPage(1);
					}
				}else {
					//拖动
					if (nDifferY<(((float)sVItem.nHeight)/sVItem.nRow)) {
						//拖动的距离太小了
						if (nDifferY>5) {
							nClickCount=0;
						}
						return true;
					}
					if (Y-downY>0) {
						dragPage(0, -nDifferY,true);
					}else {
						dragPage(1, nDifferY,true);
					}
				}
			}else {
				if (nDifferX<4) {
					return true;
				}
				//水平移动
				if (X-downX>0) {
					dragPage(0, -nDifferX,false);
				}else {
					dragPage(1, nDifferX,false);
				}
			}
			bLongEvent=false;
		//	nClickCount=0;
			downX=X;
			downY=Y;
			
		}else {
			if (sHItem==null||sVItem==null||isClickDown||!isShowBar) {
				return true;
			}
			//nClickCount=0;
			bLongEvent=false;
			sHItem.onTouchEvent(event);
			sVItem.onTouchEvent(event);
			
		}
		
		return true;
	}
	
	/**
	 * 按下事件
	 */
	
	private boolean isHClick;
	private Point   mClickPoint = new Point();
	private boolean onClickDown(float X,float Y,MotionEvent event){
	
		if(mPageItem==null){
			return false;
		}
		boolean result=false;
		
		boolean valid = false;
		//画行背景
		if ((System.currentTimeMillis()-nClickTime)<600) {
			if ((System.currentTimeMillis()-nClickTime)>150) {
				valid=true;
			}
		}
		
		nClickTime=System.currentTimeMillis();
		
		if(Math.abs(X-downX)> 15||Math.abs(Y-downY)>15){
			valid = false;
		}
		
		downX=X;
		downY=Y;
		mClickPoint.set((int)downX, (int)downY);
		if(mPageItem.clickRow(X,Y,isShowBar)){
			//双击某一行
			if (iClickListener!=null) {
				if (nRowIndex!=mPageItem.drowRow) {
					nRowIndex=mPageItem.drowRow;
					nClickCount=1;
				}else {
					nClickCount++;
					if (nClickCount>=2) {
						if (valid) {
							RowCell item =mPageItem.getRowInfo();
							if (item==null) {
								iClickListener.onDoubleClick(-1, -1, -1, -1);
							}else {
								iClickListener.onDoubleClick(item.nClickIndex, item.gid, item.aid, 0);
							}
							nClickCount=0;
						}else {
							nClickCount=1;
						}
					}
				}
			}
			SKSceneManage.getInstance().onRefresh(items);
		}
		
		if (X<(info.getnLeftTopX()+info.getnWidth()-24)) {
			//如果按下的位置,处于手指可以拖动区域。
			if (Y<info.getnLeftTopY()+info.getnHeight()-30) {
				isDrag=true;
			}
		}
		
		//先分水平和垂直方向
		if (isShowBar) {
			if (X<info.getnLeftTopX()+info.getnWidth()-24) {
				//水平
				if (X<info.getnLeftTopX()+24||X>info.getnLeftTopX()+info.getnWidth()-48) {
					sHItem.clicXY(X, Y);
					result=true;
					isHClick=true;
					mUIHandler.sendEmptyMessageDelayed(CLICK_LOOPER, 250);
				}
			}else {
				//Log.d("SKScene", "Y:"+Y+",top:"+(info.getnLeftTopY()+mHTitleItem.nHeight+24)+",bottom:"+(info.getnLeftTopY()+info.getnHeight()-48));
				if (Y<info.getnLeftTopY()+mHTitleItem.nHeight+24||Y>info.getnLeftTopY()+info.getnHeight()-48) {
					sVItem.clicXY(X,Y);
					isHClick=false;
					result=true;
					mUIHandler.sendEmptyMessageDelayed(CLICK_LOOPER, 250);
				}
			}
		}
		return result;
	}
	
	public Point getClickPoint(){
		return mClickPoint;
	}
	
	
	private long nStart=0;
	/**
	 * 翻页
	 * @param type=0,前一页，type=1,下一页
	 */
	public void turnPage(int type){
		
		if (System.currentTimeMillis()-nStart<500) {
			return;
		}
		nClickCount=0;
		nStart=System.currentTimeMillis();
		
		if (nAllCount>sVItem.nRow) {
			if (type==0) {
				//Log.d(TAG, "前一页.....");
				sVItem.turnPage(-mPageItem.nHeight, true);
			}else if(type==1) {
				//Log.d(TAG, "下一页.....");
				sVItem.turnPage(mPageItem.nHeight, true);
			}
		}
	}
	
	/**
	 * 移动一列
	 * @param type-0 向左，type-1 向右
	 */
	public void moveRank(int type,int len){
		dragPage(type, len, false);
	}
	
	/**
	 * 移动一行
	 * @param type-0 向上，type-1 向下
	 */
	public void moveRow(int type,int len){
		dragPage(type, len, true);
	}
	
	/**
	 * 拖动显示
	 * @param type=0,向前显示，type=1,向后显示
	 * @param len-移动的距离
	 * @param isV-true 垂直移动,false 水平移动
	 */
	private void dragPage(int type,float len,boolean isV){
		if (isV) {
			if (nAllCount>sVItem.nRow) {
				if (type==0) {
					//Log.d(TAG, "向前显示.....");
					sVItem.turnPage(len, true);
				}else if(type==1) {
					//Log.d(TAG, "向后显示.....");
					sVItem.turnPage(len, true);
				}
			}
		}else {
			if (type==0) {
				//Log.d(TAG, "向前显示.....");
				sHItem.turnPage(len, false);
			}else if(type==1) {
				//Log.d(TAG, "向后显示.....");
				sHItem.turnPage(len, false);
			}
		}
		
	}
	
	/**
	 * 跳转到特定行
	 */
	public void gotoRow(int top){
		if (sVItem!=null) {
			sVItem.gotoRow(top);
		}
	}
	
	/**
	 * 加载下一页信息
	 * @param list-控件信息集合
	 * @param type-翻页类型，0-表示上一页，1-表示下一页 
	 */
	public void loadItem(ArrayList<ArrayList<TableTextInfo>> list,int type){
 
	}
	
	/**
	 * 改变每小格的坐标
	 * @param isHMove-true,水平
	 * @param len,移动的距离
	 */
	private void updateCellXY(boolean isHMove, double len) {
		if (isHMove) {
			mPageItem.nLeftX+=len;
			//更新标题
			mHTitleItem.nLeftX+=len;
			
		} else {
			mPageItem.update(-sVItem.getnMoveLen());
		}
		
	}
	

//	/**
//	 * 更新滑动块的位置
//	 */
//	private void updateBarXY(float len){
//		if (sVItem!=null) {
//			sVItem.updateXY(-len);
//		}
//	}
	
	public void updateMoveBarXY(int row,int type){
		
	}
	
	/**
	 * 滑动块移动到底部
	 */
	public void moveToBottom(){
		//Log.d(TAG, "moveToBottom.....");
		//bUpdate=false;
		if (sVItem!=null) {
			sVItem.moveToBottom();
		}
	}
	
	/**
	 * 滑动块移动到顶部
	 */
	public void moveToTop(){
		//bUpdate=false;
		//Log.d(TAG, "moveToTop.....");
		if (sVItem!=null) {
			sVItem.moveToTop();
		}
	}
	
	/**
	 * 更新起始行序号
	 */
	public void updateRowIndex(int index){
		if(mPageItem!=null){
			if (index<0) {
				index=0;
			}
			mPageItem.setnRowIndex(index);
		}
	}
	
	/**
	 * 初始化数据
	 */
	public void initData(Vector<RowCell> list,ArrayList<String> row,int startNum){
		if (mPageItem==null||mHTitleItem==null||sVItem==null||sHItem==null) {
			return;
		}
		mRowCells=list;
		mHTitleItem.updateData(row);
		mPageItem.setmRowItems(mRowCells);
		mPageItem.nStartNum=startNum;
	}
	
	/**
	 * 更新数据总量
	 * @param count-总数的数据
	 */
	private int nAllCount=0;
	public void updateDataNum(int count){
		if (mPageItem==null||sVItem==null||sHItem==null) {
			return;
		}
		
		nAllCount=count;
		if (sVItem!=null) {
			sVItem.nAllCount=nAllCount;
		}
		updatePageNum(count);
	}
	
	/**
	 * 更新标题
	 */
	public void updateTitle(ArrayList<String> row){
		mHTitleItem.updateData(row);
	}
	
	/**
	 * 重新设置标题画笔
	 */
	public void resetTitlePaint(){
		mHTitleItem.nFontSize=info.getnTitleFontSize();
		mHTitleItem.typeface=info.getmHTypeFace();
		mHTitleItem.nPageBgColor=info.getnTitleBackcolor();
		mHTitleItem.resetPaint();
	}
	
	/**
	 * 更新页面数据
	 */
	public void updateData(Vector<RowCell> list){
		if (mPageItem==null||list==null) {
			return;
		}
		mRowCells=list;
		mPageItem.setmRowItems(mRowCells);
	}
	
	/**
	 * 更新页数
	 * 有水平滚动条必须有垂直滚动条
	 * 有垂直滚动条必须有水平滚动条
	 */
	private void updatePageNum(int count){
		
		boolean reset=false;
		nAllVlen = nBarLen;
		nAllHLen = nBarLen;
		
		int page=count/mPageItem.nRow;
		int size=count%mPageItem.nRow;
		if (size>0) {
			info.setnPageCount(page+1);
		}else {
			info.setnPageCount(page);
		}
		
		if (page>0) {
			nAllVlen+=(page-1)*mPageItem.getHeight();
			for (int i = 0; i < size; i++) {
				if (info.getmRowHeight().size()>i) {
					nAllVlen+=info.getmRowHeight().get(i);
				}
			}
			sVItem.setnAllLen(nAllVlen);
			reset=true;
		}
		
		if (nDataRank>nRank) {
			if (info.getnDiffer()>0) {
				nAllHLen+=nBarLen+info.getnDiffer();
			}else {
				if (info.getnAllColumWidth()>info.getnWidth()||info.getnAllColumWidth()==0) {
					//getnAllColumWidth 主要是用于历史数据显示器，由于历史数据列的特殊性，其他getnAllColumWidth都等于=0
					nAllHLen+=nBarLen+(nDataRank-nRank)*mPageItem.nCellWidth;
				}else {
					nAllHLen+=nBarLen;
				}
			}
			sHItem.setnAllLen(nAllHLen);
			if (!reset) {
				sVItem.setnAllLen(nBarLen);
			}
			reset=true;
		}else {
			if (reset) {
				sHItem.setnAllLen(nAllHLen);
			}
		}
		
		if (reset) {
			sVItem.resetBarLen();
			sHItem.resetBarLen();
		}else {
			nAllVlen = 0;
			nAllHLen = 0;
		}
		
		info.setnPageCount(page);
	}
	
	/**
	 * 更新页面信息
	 */
	public void updatePageList(ArrayList<ArrayList<String>> datas){
		
	}
	
	/**
	 * 滚动条,滚动时，改变表格显示位置
	 */
	//private boolean bUpdate;
	MoveItem.ScrollListener listener=new MoveItem.ScrollListener() {
		
		@Override
		public void onMove(boolean isHMove, double len) {
			updateCellXY(isHMove,len);
			SKSceneManage.getInstance().onRefresh(items);
		}

		@Override
		public void onUpdateView(int type) {
			if (mPageItem!=null) {
				mPageItem.updateView(type);
			}
		}

		@Override
		public void onUpdate(int type, int top, int site) {
			SKSceneManage.getInstance().onRefresh(items);
			//Log.d(TAG, "bUpdate..."+bUpdate);
			if (iPageTurning!=null) {
				iPageTurning.onLoad(top, type,site);
			}
//			if (bUpdate) {
//				
//			}else {
//				bUpdate=true;
//			}
		}

	};
	
	/**
	 * @param type-1正常显示，2往上移动一段距离（水平滚动条的高度）
	 */
	public void updateView(int type){
		if (mPageItem!=null) {
			mPageItem.updateView(type);
		}
	}
	
	/**
	 * 隐藏滚动条
	 */
	public void hideBar(){
		SKSceneManage.getInstance().onRefresh(items);
	}
	
	/**
	 * 返回可以设置的总数
	 */
	public int getShowCount(){
		if (sVItem!=null) {
			return sVItem.getShowCount();
		}
		return 0;
	}
	
	/**
	 * 数据销毁
	 */
	public void destory(){
		if(mRowCells!=null){
			mRowCells.clear();
		}
		if (mHTitleItem!=null) {
			mHTitleItem.destory();
		}
	}
	
	/**
	 * 翻页接口
	 */
	public interface IPageTurning{
		//前一页
		void onPre(int page);
		//下一页
		void onNext(int page);
		//更新控件位置
		void onUpdate(int len);
		/**
		 * 数据加载
		 * @param top-从第几行开始获取
		 * @param type 滑动类型
	     *        type-1点击顶部按钮
	     *        type-2点击底部按钮
	     *        type-3向上滑动
	     *        type-4向下滑动
	     * @param site-所处位置
	     *        site-0 顶部
	     *        site-1 中间
	     *        site-2 底部
		 */
		void onLoad(int top,int type,int site);
	}
	
	/**
	 * 点击事件
	 */
	public interface IClickListener{
		
		/**
		 * 长按
		 * @param index-数组下标
		 * @param gid-组id
		 * @param aid-组里面子项id
		 * @param type-相应类型 type=0,确定报警;
		 */
		void onLongClick(int index,int gid,int aid,int type);
		
		/**
		 * 双击
		 * @param index-数组下标
		 * @param gid-组id
		 * @param aid-组里面子项id
		 * @param type-相应类型 type=0,确定报警;
		 */
		void onDoubleClick(int index,int gid,int aid,int type);
		
		/**
		 * 点击
		 * @param index-数组下标
		 * @param gid-组id
		 * @param aid-组里面子项id
		 * @param type-相应类型 type=0,确定报警;
		 */
		void onClick(int index,int gid,int aid,int type);
		
	}
	
	//添加数据接口
	public interface IAddListener{
		//添加事件
		void add();
		//修改事件
		void update(TableTextInfo info);
		//删除事件
		void delete(int row,String text);
	}
	
	//设置添加回调接口
	public void setiAddListener(IAddListener iAddListener) {
		this.iAddListener = iAddListener;
	}
	
	//设置翻页事件
	public void setiPageTurning(IPageTurning iPageTurning) {
		this.iPageTurning = iPageTurning;
	}
	
	public void setiClickListener(IClickListener iClickListener) {
		this.iClickListener = iClickListener;
	}
	
	public boolean isShowBar() {
		if (mPageItem==null) {
			return false;
		}
		if (mPageItem.nWidth<72||mPageItem.nHeight<72) {
			isShowBar=false;
		}else {
			if (nAllHLen>0||nAllVlen>0) {
				isShowBar=true;
			}else {
				isShowBar=false;
			}
		}
		return isShowBar;
	}

	private boolean isSingleClick = false;
	public class UIHandler extends Handler{
		
		public UIHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case CLICK_LOOPER:
				if (isClickDown) {
					boolean result;
					if (isHClick) {
						result=sHItem.clicXY(downX, downY);
					}else{
						result=sVItem.clicXY(downX, downY);
					}
					if (result) {
						mUIHandler.sendEmptyMessageDelayed(CLICK_LOOPER, 250);
					}
				}
				break;
			case LONG_PRESS:
				if (bLongEvent) {
					//Log.d(TAG, "long click....");
					if (iClickListener!=null) {
						iClickListener.onLongClick(mPageItem.drowRow, mTouchColum, 0, 0);
					}
					isSingleClick = false;
					nClickCount = 0; 
				}
				break;
			case HANDLER_CLICK:
			{
				isSingleClick = true;
				nClickCount = 0;
			}
			break;
			}
		}
		
	}
	
	/**
	 * 可拖动线的坐标
	 */
	class MoveLine {
		int lineX; //左顶点X坐标
		int lineY; //右顶点Y坐标
		int len;   //可拖动区域
	}
	
	/**
	 * 设置标题背景颜色，需要刷新才能有效
	 * @param nTitleFontColor 标题背景颜色
	 */
	public void setnInfogetnTitleBackcolor(int nTitleFontColor){
		this.info.setnTitleBackcolor(nTitleFontColor);
	}
	
	public void setEnterKeyShow(boolean isShow){
		mHTitleItem.setEnterKeyShow(isShow);
	}
	
	public HTitleItem getHTitleItem(){
		return mHTitleItem;
	}
	
	/**
	 * 设置背景
	 * @param color-颜色
	 */
	public void resetBackcolor(int color){
		this.info.setnTableBackcolor(color);
		mPageItem.nPageBgColor=color;
	}
	
	/**
	 * 设置边框颜色
	 * @param color-颜色
	 */
	public void resetLinecolor(int color){
		this.info.setnFrameColor(color);
	}
	
	/**
	 * 设置控件透明度
	 * @param alpha-透明度
	 */
	public void resetAlpha(int alpha){
		this.info.setnAlpha(alpha);
		mPageItem.nAlpha=alpha;
	}
	
	/**
	 * 设置控件，左边X坐标
	 */
	public void resetLeftTopX(int x){
		this.info.setnLeftTopX((short)x);
		
		//标题
		int tmp=mHTitleItem.getmShowRect().left;
		mHTitleItem.getmShowRect().left=x;
		mHTitleItem.getmShowRect().right=mHTitleItem.getmShowRect().right+x-tmp;
		mHTitleItem.nLeftX=info.getnLeftTopX();
		
		
		//表格内容
		mPageItem.getShowRect().left=x;
		mPageItem.getShowRect().right=mPageItem.getShowRect().right+x-tmp;
		mPageItem.nLeftX=x;
		
		//外边框
		mFrameRect.left=x;
		mFrameRect.right=mFrameRect.right+x-tmp;
		
		//滑动块
		mLPPoint.x=x;
		mRBPoint.x=x+info.getnWidth();
		sHItem.nLeftX=mLPPoint.x+1;
		sVItem.nLeftX=mRBPoint.x-nBarLen;
	}
	
	/**
	 * 设置控件，左边Y坐标
	 */
	public void resetLeftTopY(int y){
		this.info.setnLeftTopY((short)y);
		
		//标题
		int tmp=mHTitleItem.getmShowRect().top;
		mHTitleItem.getmShowRect().top=y;
		mHTitleItem.getmShowRect().bottom=mHTitleItem.getmShowRect().bottom+y-tmp;
		mHTitleItem.nLeftY=info.getnLeftTopY();
		
		//表格内容
		mLPPoint.y=mLPPoint.y+y-tmp;
		mPageItem.getShowRect().top=mLPPoint.y;
		mPageItem.getShowRect().bottom=mPageItem.getShowRect().bottom+y-tmp;
		mPageItem.nLeftY=mLPPoint.y;;
		
		//外边框
		mFrameRect.top=y;
		mFrameRect.bottom=mFrameRect.bottom+y-tmp;
		
		//滑动块
		mLPPoint.y=(int)(info.getnLeftTopY()+nTitleHeight);
		mRBPoint.y=info.getnLeftTopY()+info.getnHeight();
		sHItem.nLeftY=mRBPoint.y-nBarLen;
		sVItem.nLeftY=mLPPoint.y+1;

	}
	
	/**
	 * 设置控件长度
	 * @param w-宽度
	 */
	public void resetWidth(int w){
		this.info.setnWidth((short)w);
		
		//标题
		mHTitleItem.resetWidth(w);
		
		//表格内容
		mPageItem.resetWidth(w);
		
		//外边框
		int tmp=mFrameRect.width();
		mFrameRect.right=mFrameRect.right+w-tmp;
		
		//滑动块
		mRBPoint.x=info.getnLeftTopX()+info.getnWidth();
		sHItem.nWidth=mRBPoint.x-mLPPoint.x-nBarLen+1;
		sVItem.nWidth=nBarLen;
		sVItem.nLeftX=mRBPoint.x-nBarLen;
		
		sHItem.init();
		sVItem.init();
	}
	
	/**
	 * 设置控件高度
	 * @param h-高度
	 */
	public void resetHeigth(int h){
		this.info.setnHeight((short)h);
		
		//外边框
		double len=(h-mFrameRect.height())/info.getnRow();
		
		mFrameRect.bottom=mFrameRect.bottom+(int)(info.getnRow()*len);
		
		//标题
		mHTitleItem.resetHeight(len);
		
		nTitleHeight=nTitleHeight+len;
		
		//表格内容
		mPageItem.resetHeight(len);
		
		
		mLPPoint.y=(int)(mLPPoint.y+len);
		mPageItem.nLeftY=mLPPoint.y;
		
		//滑动块
		mRBPoint.y=info.getnLeftTopY()+info.getnHeight();
		sHItem.nHeight=nBarLen;
		sVItem.nHeight=mRBPoint.y-mLPPoint.y-nBarLen+1;
		sHItem.nLeftY=mRBPoint.y-nBarLen;
		sVItem.nLeftY=mLPPoint.y+1;
		sHItem.init();
		sVItem.init();
	}
	
	/**
	 * -返回点击列
	 * @return
	 */
	public int getClickRowIndex(){
		return mTouchColum;
	}
}
