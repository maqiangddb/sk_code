package com.android.Samkoonhmi.graphicsdrawframe;

import java.util.ArrayList;
import java.util.Vector;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.TextAlignUtil;

public class PageItem {

	// 省略号
	private static final String sOut = "..";
	// 省略号所占的长度
	private int nOutLen;
	//宽
	public int nWidth=400; 
	//高
	public int nHeight=300;
	//第几页
	public int nPageIndex=1;
	//页的左顶点X坐标
	public float nLeftX=0;
	//页的左顶点Y坐标
	public float nLeftY=0;
	//行数
	public int nRow=4;
	//显示的列数
	public int nShowColunm=5;
	//列数
	public int nColunm=5;
	//字大小
	public float nFontSize=10;
	//竖标题字体大小
	public float nVFontSize=10;
	//竖标题背景颜色
	public int nVBgColor=Color.WHITE;
	//竖标题字体颜色
	public int nVFontColor=Color.BLACK;
	//字体类型
	public Typeface typeface;
	//字体类型
	public Typeface vTypeface;
	//对齐方式
	public TEXT_PIC_ALIGN mAlign=TEXT_PIC_ALIGN.CENTER; 
	//颜色
	public int nFontColor=Color.BLACK;
	//背景颜色
	public int nPageBgColor=Color.WHITE;
	//线的颜色
	public int nLineColor=Color.BLACK;
	//透明度
	public int nAlpha;
	private Canvas mCanvas;
	public Bitmap mBitmap;
	private Paint mPaint;
	//每小格宽
	public float nCellWidth;
	//每小格高
	public float nCellHeight;
	private PointF mPoint;
	//每小格原始大小
	private RectF mRect;
	//显示区域
	private Rect mShowRect;
	private Paint mLinePaint;
	//所有行的信息
	private Vector<RowCell> mRowItems;
	//行所在区域
	private RectF mRowRect;
	//点击行效果图片
	private Bitmap mRowBitmap;
	//行的高
	public ArrayList<Double> mRowHeight;
	//行的宽
	public ArrayList<Double> mRowWidth;
	public int nStartNum;
	private int nRowIndex;//起始行id
	private boolean bUserCode;//
	private Paint mBitmapPaint;//图片专用
	private TextAlignUtil mTextAlignUtil;
	
	/**
	 * @param width-宽
	 * @param heigth-高
	 * @param row-行数
	 * @param scolunm-要显示的列数
	 * @param colunm-实际列数
	 * @param sRect-显示区域
	 */
	public PageItem(int width,int heigth,int row,int scolunm,int colunm,Rect sRect) {
		this.nWidth=width;
		this.nHeight=heigth;
		this.nRow=row;
		this.nColunm=colunm;
		this.nShowColunm=scolunm;
		mShowRect=sRect;
		mRowRect=new RectF();
		mRowBitmap=ImageFileTool.getBitmap(R.drawable.row_bg, SKSceneManage.getInstance().mContext);
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
		mTextAlignUtil=new TextAlignUtil();
	}
	
	/**
	 * 初始化数据
	 */
	public void initData(){
		
		bUserCode=false;
		drowRow=-1;
		nClickIndex=-1;
		nRowIndex=0;
		nCellWidth=nWidth/nShowColunm;
		nCellHeight=nHeight/nRow;
		
		if (nColunm>nShowColunm) {
			nShowColunm=nColunm;
		}
		if(nWidth<1){
			nWidth=1;
		}
		if(nHeight<1){
			nHeight=1;
		}
		mBitmap=Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		
		mCanvas=new Canvas(mBitmap);
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setStrokeWidth(0);
		
		if (mAlign == TEXT_PIC_ALIGN.LEFT) {
			mPaint.setTextAlign(Align.LEFT);// 文本居左
		} else if (mAlign == TEXT_PIC_ALIGN.RIGHT) {
			mPaint.setTextAlign(Align.RIGHT);// 文本居中
		} else {
			mPaint.setTextAlign(Align.CENTER);// 文本居右
		}
		
		mRowRect=new RectF();
		
		mPaint.setColor(nFontColor);
		mPaint.setTextSize(nFontSize);
		mPoint=new PointF();
		mRect=new RectF();
		//mRect.set(0, 0, nCellWidth, nCellHeight);
		nOutLen=getFontWidth(sOut, mPaint);
		
		mLinePaint=new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(nLineColor);
		mLinePaint.setStyle(Style.FILL);
		mLinePaint.setStrokeWidth(1);
	}
	
	/**
	 * 画文本
	 */
	private double nTempWidht;
	private double nTempHeight;
	private void drawText(Canvas canvas){
		
		//画背景颜色
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		//画竖标题背景
		int nLeftPadding=0;
		if (nVBgColor!=nPageBgColor) {
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(nVBgColor);
			mPaint.setAlpha(nAlpha);
			if (nLeftX+nCellWidth>mShowRect.left) {
				if(mRowWidth==null||mRowWidth.size()==0){
					nTempWidht=nCellWidth;
				}else {
					nTempWidht=mRowWidth.get(0);
				}
				nLeftPadding=(int)(nLeftX+nTempWidht-mShowRect.left-1);
				Rect rect=new Rect(0, 0,nLeftPadding, nHeight);
				canvas.drawRect(rect,mPaint);
			}
		}
		
		mPaint.setColor(nPageBgColor);
		mPaint.setAlpha(nAlpha);
		Log.d("PageItem", "nPageBgColor ="+nPageBgColor+",nAlpha="+nAlpha);
		if (nAlpha>0) {
			canvas.drawRect(nLeftPadding, 0, nWidth, nHeight, mPaint);
		}
		
		
		mPaint.setAlpha(255);
		mPaint.setColor(nFontColor);
		//画文本
		int row=nRow;
		if (mRowItems!=null) {
			if (mRowItems.size()>nRow) {
				row+=2;
			}
		}
		
		double startY=nY;
		
		//Log.d("SKScene", "startY:"+startY+",nPage:"+nPage+",len"+nPage*nHeight+",nMoveLen:"+nMoveLen+",nStartRow:"+nStartRow);
		
		try {
			
			if (mRowItems==null) {
				//没有数据也要画表格
				// 画竖线
				int left=0,right=0;
				boolean drawLine;
				for (int i = 0; i < nShowColunm; i++) {
					drawLine=true;
					nTempWidht=nCellWidth;
					if (mRowWidth!=null) {
						if (mRowWidth.size()>i) {
							nTempWidht=mRowWidth.get(i);
						}
					}
					
					if (i==0) {
						right=(int)(nLeftX+nTempWidht);
						drawLine=false;
					}else {
						right+=nTempWidht;
					}
					left=(int)(right-nTempWidht);
					
					if (right<mShowRect.left) {
						drawLine=false;
					}
					
					if (left>mShowRect.right) {
						break;
					}
					if (drawLine) {
						canvas.drawLine(left-mShowRect.left, 0, left-mShowRect.left, nHeight, mLinePaint);
					}
				}
				//画横线
				for (int i = 0; i < row; i++) {
					nTempHeight=nCellHeight;
					if (mRowHeight!=null||mRowHeight.size()<i) {
						if (mRowHeight.size()>i) {
							nTempHeight=mRowHeight.get(i);
						}
					}
					canvas.drawLine(0, (int)(startY+nTempHeight), nWidth, (int)(startY+nTempHeight), mLinePaint);
					startY+=nTempHeight;
				}
				return;
			}
			for (int i = 0; i < row; i++) {
				Vector<String> clounm=null;
				RowCell item=null;
				if (mRowItems!=null) {
					int size=mRowItems.size();
					if (size>i+nRowIndex) {
						item=mRowItems.get(i+nRowIndex);
						clounm=item.mClounm;
					}
				
				}
				
				nTempHeight=nCellHeight;
				if (mRowHeight!=null||mRowHeight.size()<i) {
					if (mRowHeight.size()>i) {
						nTempHeight=mRowHeight.get(i);
					}
				}
				
				//行背景
				if (item!=null) {
					if (item.isSetRowColor) {
						mPaint.setColor(item.nRowColor);
						canvas.drawRect(0, (int)startY, nWidth, (int)(startY+nTempHeight), mPaint);
					}
				}
				
				//点击行效果
				boolean draw=false;
				if (bUserCode) {
					if (nClickIndex==i+nStartNum) {
						draw=true;
					}
				}else {
					if (mRowItems.size()>i) {
						if(nClickIndex==mRowItems.get(i).nRowIndex){
							draw=true;
						}
					}
				}
				if (draw) {
					mRowRect.left=0;
					mRowRect.right=nWidth;
					mRowRect.top=(float)startY;
					mRowRect.bottom=(float)(startY+nTempHeight);
					if(mRowBitmap!=null){
						canvas.drawBitmap(mRowBitmap, null, mRowRect, mBitmapPaint);
					}
				}
				
				//Log.d("SKScene", "--startY:"+startY);
				if (i==row-1) {
					drawRowText(canvas,clounm,(int)startY,false,i);
				}else {
					drawRowText(canvas,clounm,(int)startY,true,i);
				}
				
				startY+=nTempHeight;
			}
			//Log.d("SKScene", "----------------");
			
			// 画竖线
			int left=0,right=0;
			boolean drawLine;
			for (int i = 0; i < nShowColunm; i++) {
				drawLine=true;
				nTempWidht=nCellWidth;
				if (mRowWidth!=null) {
					if (mRowWidth.size()>i) {
						nTempWidht=mRowWidth.get(i);
					}
				}
				
				if (i==0) {
					right=(int)(nLeftX+nTempWidht);
					drawLine=false;
				}else {
					right+=nTempWidht;
				}
				left=(int)(right-nTempWidht);
				
				if (right<mShowRect.left) {
					drawLine=false;
				}
				
				if (left>mShowRect.right) {
					break;
				}
				if (drawLine) {
					canvas.drawLine(left-mShowRect.left, 0, left-mShowRect.left, nHeight, mLinePaint);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("PageItem", "PageItem drawtext error!");
		}
		
	}
	
	
	/**
	 * @param type=1 获取起始行位置
	 *        type=2更新滚动距离
	 */
	private double nY;
	public void update(double len){
		
	} 
	
	/**
	 * @param type-1 正常显示 ，2往上移动一段距离（水平滚动条的高度）
	 */
	public void updateView(int type){
		if (type==1) {
			nY=0;
		}else if(type==2){
			nY=-24;
		}
	}
	
	/**
	 * 根据行数获取距离
	 */
	public double getLenByRow(int row){
		double result=0;
		int page=(int)((double)row/nRow);
		int size=0;
		
		result=page*nHeight;
		if (row%nRow>0&&page>0) {
			size=row-page*nRow;
			if (mRowHeight!=null) {
				for (int i = 0; i < size; i++) {
					result+=mRowHeight.get(i);
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * 点击行
	 * @param x-点击x坐标
	 * @param y-点击y坐标
	 * @param show-是否显示滚动条
	 */
	public int drowRow;
	private int nClickIndex=-1;
	public boolean clickRow(float x,float y,boolean show){
		if (mRowItems==null) {
			return false;
		}
		boolean result=false;
		if (show) {
			if (y>=mShowRect.top&&y<(mShowRect.top+mShowRect.height()-30)&&x<(mShowRect.left+mShowRect.width()-60)) {
				result=true;
			}
		}else {
			if (y>=nLeftY&&y<nLeftY+nHeight) {
				result=true;
			}
		}
		if (result) {
			
			if (mRowHeight!=null) {
				int height=0;
				drowRow=0;
				for (int i = 0; i < mRowHeight.size(); i++) {
					height+=mRowHeight.get(i);
					if (height>=(Math.abs(y)-nLeftY-nY)) {
						drowRow+=i;
						if (bUserCode) {
							nClickIndex=drowRow+nStartNum;
						}else {
							if (mRowItems.size()>drowRow) {
								nClickIndex=mRowItems.get(drowRow).nRowIndex;
							}
							
						}
						break;
					}
				}
			}
			
		}
		return result;
	}
	
	/**
	 * 获取点击行下标
	 */
	public RowCell getRowInfo(){
		RowCell item=null;
		if (drowRow==-1) {
			return item;
		}
		if(mRowItems!=null){
			if (mRowItems.size()>drowRow) {
				mRowItems.get(drowRow).nClickIndex=drowRow;
			    item= mRowItems.get(drowRow);
			}
		}
		return item;
	}
	
	/**
	 * 画一行数据
	 * @param clounm-列
	 * @param index-第几行
	 */
	private void drawRowText(Canvas canvas,Vector<String> clounm,int startY,boolean drawLine,int row){
		boolean drawColum;
		int left=0;
		int right=0;
		
		//Log.d("SKScene", "------startY:"+startY);
		
		for (int i = 0; i < nShowColunm; i++) {
			String text="";
			drawColum=true;
			
			if (clounm!=null) {
				if(clounm.size()>i){
					text=clounm.get(i);
				}
			}
			
			nTempWidht=nCellWidth;
			if (mRowWidth!=null) {
				if (mRowWidth.size()>i) {
					nTempWidht=mRowWidth.get(i);
				}
			}
			
			if (i==0) {
				right=(int)(nLeftX+nTempWidht);
			}else {
				right+=nTempWidht;
			}
			left=(int)(right-nTempWidht);
			
			mRect.set(0, 0, (int)nTempWidht, (int)nTempHeight);
			
			if (right<mShowRect.left) {
				drawColum=false;
			}
			
			if (left>mShowRect.right) {
				break;
			}
			
			if (drawColum) {
				//第一列
				if (i==0) {
					if (vTypeface!=null) {
						mPaint.setTypeface(vTypeface);
					}
					mPaint.setColor(nVFontColor);
					mPaint.setTextSize(nVFontSize);
				}else {
					if (typeface!=null) {
						mPaint.setTypeface(typeface);
					}
					mPaint.setColor(nFontColor);
					mPaint.setTextSize(nFontSize);
				}
				
				if (i==0) {
					//第一列
					if (text.equals("CODE")) {
						bUserCode=true;
						if (nStartNum==0) {
							text=1+row+nStartNum+"";
						}else {
							text=row+nStartNum+"";
						}
					}
				}
				
				if (!TextUtils.isEmpty(text)) {
					int fontWidth = getFontWidth(text, mPaint);
					int fontHeight = getFontHeight(mPaint);
					mTextAlignUtil.getAlign(mPoint,mAlign, mRect.height(),mRect.width(), fontWidth,fontHeight);
					String temp=text;
					if (fontWidth>nTempWidht) {
						temp=getText(text, (int)(nTempWidht-nOutLen), text.length(), mPaint);
					}
					canvas.drawText(temp,mPoint.x+left-mShowRect.left, mPoint.y+startY, mPaint);
				}
			}
		}
		
		if(drawLine){
			canvas.drawLine(0, (int)(startY+nTempHeight), nWidth, (int)(startY+nTempHeight), mLinePaint);
		}
	}
	
	/**
	 * 获取高度
	 */
	public int getHeight(){
		return nHeight;
	}
	
	/**
	 * 获取行高
	 */
	public float getRowHeiht(){
		return nCellHeight;
	}
	
	/**
	 * 画每一小格文本和线
	 */
	public void initPage(){

	}
	
	public void draw(Canvas canvas){
		/**
		 * 画文本
		 */
		drawText(mCanvas);
		
		if (mBitmap!=null) {
			canvas.drawBitmap(mBitmap, mShowRect.left, mShowRect.top, mBitmapPaint);
		}

	}
	
	/**
	 * 更新数据
	 */
	public void updateData(int page,ArrayList<ArrayList<String>> data){

	}
	
	/**
	 * 拖动事件
	 */
	public boolean onTouchEvent(MotionEvent event){
		SKSceneManage.getInstance().time=0;
		boolean result=false;
		return result;
	}
	
	/**
	 * 获取字体所占宽度
	 * @param font-文本
	 * @param paint-已经设置大小的画笔
	 */
	private int getFontWidth(String font, Paint paint) {
		if (TextUtils.isEmpty(font)) {
			return 0;
		}
		return (int) paint.measureText(font);
	}
	
	/**
	 * 获取字体所占的高度
	 */
	private int getFontHeight(Paint paint){
		FontMetrics fm=paint.getFontMetrics();
		return (int)Math.ceil(fm.descent-fm.top)+2;
	}
	
	/**
	 * 字符串截取
	 * @param text-原始文本
	 * @param width-显示区域的宽度
	 * @param len-文本的长度
	 * @param paint-已经设置好大小的画笔
	 */
	private String getText(String text, int width, int len, Paint paint) {
		String mText = "";
		int mLen = 0;
		if (!text.equals("")) {
			for (int i = 0; i < len; i++) {
				mLen += getFontWidth(text.charAt(i) + "", paint);
				if (mLen >= width) {
					return mText + sOut;
				}
				mText += text.charAt(i);
			}
		}
		return mText;
	}

	public void setmRowItems(Vector<RowCell> mRowItems) {
		this.mRowItems = mRowItems;
	}
	
	public void setmRowHeight(ArrayList<Double> rowHeight) {
		mRowHeight=new ArrayList<Double>();
		if (rowHeight!=null) {
			for (int i = 1; i < rowHeight.size(); i++) {
				mRowHeight.add(rowHeight.get(i));
			}
		}
	}
	
	public void setnRowIndex(int nRowIndex) {
		this.nRowIndex = nRowIndex;
	}

	/**
	 * 获取显示区域
	 */
	public Rect getShowRect(){
		return mShowRect;
	}
	
	/**
	 * 重新设置宽度
	 * @param w-宽度
	 */
	public void resetWidth(int w){
		
		int tmp=mShowRect.width();
		mShowRect.right=mShowRect.right+w-tmp;
		nWidth=w;
		
//		double len=(w-tmp)/nShowColunm;
//		for (int i = 0; i < mRowWidth.size(); i++) {
//			double ww=mRowWidth.get(i);
//			mRowWidth.set(i, len+ww);
//		}
		initData();
	}
	
	/**
	 * 重新设置高度
	 * @param h-高度
	 */
	public void resetHeight(double len){
		//标题
		mShowRect.top=mShowRect.top+(int)len;
		mShowRect.bottom=mShowRect.bottom+(int)(len*(nRow+1));
		nHeight=mShowRect.height();
		
		for (int i = 0; i < mRowHeight.size(); i++) {
			double ww=mRowHeight.get(i);
			mRowHeight.set(i, len+ww);
		}
		
		initData();
	}
}

