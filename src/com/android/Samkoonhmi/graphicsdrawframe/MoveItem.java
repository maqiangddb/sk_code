package com.android.Samkoonhmi.graphicsdrawframe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skenum.Direction.DIRECTION;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;

/**
 * 滑动块
 */
public class MoveItem {

	private static final String TAG="MoveItem";
	public  int mVelocity = 40;//点击移动的距离
	private Bitmap mMoveDBitmap = null;//滑动块-没按钮
	private Bitmap mMoveNBitmap = null;//滑动块-按压
	public int nLeftX;//左顶点X坐标
	public int nLeftY;//左顶点Y坐标
	public int nWidth;//宽度
	public int nHeight;//长度
	private Context mContext;
	public DIRECTION eDirection;//方向，水平or垂直
	public int nLen;//按钮的长宽
	private RectF mRect;//滑动块所在矩形
	private double nAllLen=0;//总共可以滑动的距离
	private double nMoveLen=0;//已经移动的距离
	private double nBarLen=0;//滑动块总的长度
	private Canvas mCanvas;
	public ScrollListener listener;//滚动or点击回调
	private double nMoveScale=1;
	//private Paint paint;
	private Bitmap mBitmap=null;
	private boolean clicks[];
	private boolean flag;
	public int nAllCount;//总共要显示的行数
	public int nRow;//显示的行数
	private Paint mBitmapPaint;//图片专用
	
	public MoveItem(Context context){
		this.mContext=context;
		flag=false;
		mBitmapPaint=new Paint();
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setDither(true);
	}
	
	/**
	 * 初始化
	 */
	public void init(){
		nMoveLen=0;
		mRect=new RectF();
		flag=true;
		double nScale=1;
		if (eDirection==DIRECTION.LEVEL) {
			//水平
			nScale=((double)(nWidth-2*nLen))/(nAllLen+nWidth-2*nLen);
			nBarLen=nWidth-2*nLen;
			mRect.left=nLen;
			mRect.top=0;
			mRect.right=(int)((nWidth-2*nLen)*nScale)+nLen;
			mRect.bottom=nLen;
			if (mRect.width()<nLen) {
				mRect.right=mRect.left+nLen;
			}
			if (nAllLen<=0) {
				nMoveScale=1;
			}else {
				nMoveScale=((double)nAllLen)/(nWidth-2*nLen-mRect.width());
			}
			
		}else if (eDirection==DIRECTION.VERTICAL) {
			//垂直g
			nScale=((double)(nHeight-2*nLen))/(nAllLen+nHeight-2*nLen);
			nBarLen=nHeight-2*nLen;
			mRect.left=0;
			mRect.top=nLen;
			mRect.right=nLen;
			mRect.bottom=(int)((nHeight-2*nLen)*nScale)+nLen;
			if (mRect.height()<nLen) {
				mRect.bottom=mRect.top+nLen;
			}
			if (nAllLen==0) {
				nMoveScale=1;
			}else {
				nMoveScale=((double)nAllLen)/(nHeight-2*nLen-mRect.height());
			}
		}
		
		clicks=new boolean[]{false,false,false};
		//paint=new Paint();
		if(nWidth<1){
			nWidth=1;
		}
		if(nHeight<1){
			nHeight=1;
		}
		mBitmap=Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		mCanvas=new Canvas(mBitmap);
		
		mMoveDBitmap=getBitmap(true);
		mMoveNBitmap=getBitmap(false);
	}
	
	/**
	 * 画图
	 */
	public void draw(Canvas canvas){
		
		if (!flag) {
			init();
			flag=true;
		}
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		
		if (eDirection==DIRECTION.LEVEL) {
			mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_h_bg, mContext),
					null, new Rect(0, 0, nWidth, nHeight), mBitmapPaint);
		}else {
			mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_v_bg, mContext),
					null, new Rect(0, 0, nWidth, nHeight), mBitmapPaint);
		}
		
		//左边or顶部
		drawButton(0);
		
		//滑动
		if (eDirection==DIRECTION.LEVEL) {
			if (nWidth>(2*20+30)) {
				drawButton(1);
			}
		}else {
			if (nHeight>(2*20+30)) {
				drawButton(1);
			}
		}
		
		
		//右边or底部
		drawButton(2);
		canvas.drawBitmap(mBitmap, nLeftX, nLeftY, mBitmapPaint);
		
	}
	
	/**
	 * 拖拉事件
	 */
	private float downX,downY;
	boolean reuslt=false;
	private boolean isClickDown;
	private int index=0;
	private long nTime=0;
	private boolean isMove;
	private boolean isClickMove;//点击移动
	public boolean onTouchEvent(MotionEvent event){
		SKSceneManage.getInstance().time=0;
		float x=event.getX();
		float y=event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMove=false;
			isClickMove=false;
			if (eDirection==DIRECTION.LEVEL) {
				if (x>(nLeftX+mRect.left)&&x<(nLeftX+mRect.right)) {
					isMove=true;
				}else {
                    if (x>(nLeftX+nLen)&&x<(nLeftX+nWidth-nLen)) {
                    	isClickMove=true;
					}
				}
			}else if (eDirection==DIRECTION.VERTICAL) {
				if (y>(nLeftY+mRect.top)&&y<(nLeftY+mRect.bottom)) {
					isMove=true;
				}else {
					if (y>(nLeftY+nLen)&&y<(nLeftY+nHeight-nLen)) {
						isClickMove=true;
					}
				}
			}
			if (isClickMove) {
				if (eDirection==DIRECTION.LEVEL) {
					if (x>mRect.right+nLeftX) {
						downX=mRect.right+nLeftX;
					}else {
						downX=mRect.left+nLeftX;
					}
					
				}else {
					if (y>(mRect.bottom+nLeftY)) {
						downY=mRect.bottom+nLeftY;
					}else {
						downY=mRect.top+nLeftY;
					}
				}
			}else {
				downX=x;
				downY=y;
			}
			nTime=0;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isMove) {
				if (System.currentTimeMillis()-nTime>=100) {
					nTime=System.currentTimeMillis();
					moveXY(x, y);
				}
			}else {
				if (isClickMove) {
					moveXY(x, y);
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (isClickDown) {
				switch (index) {
				case 0:
					clicks[0]=false;
					break;
				case 1:
					clicks[1]=false;
					break;
				case 2:
					clicks[2]=false;
					break;
				}
			}
			reuslt=true;
			break;
		}
		return reuslt;
	} 
	
	/**
	 * 滑动
	 */
	private void moveXY(float x,float y){
		
		if (mRect==null) {
			return;
		}
		int temp=0;
		if (eDirection==DIRECTION.LEVEL) {
			if (Math.abs(x-downX)<2) {
				return;
			}
			//水平
			if (x-downX>0) {
				//从左往右
				if (nMoveLen>=nAllLen) {
					downX=x;
					return;
				}
				index=1;
				isClickDown=true;
				temp=(int)((x-downX)*nMoveScale);
				if (nMoveLen+temp>=nAllLen) {
					if (listener!=null) {
						listener.onMove(true,nMoveLen-nAllLen);
					}
					nMoveLen=nAllLen;
					updateBar(nWidth-nLen-mRect.right, true);
				}else {
					nMoveLen+=temp;
					if (listener!=null) {
						listener.onMove(true,-temp);
					}
					updateBar((float)(temp/nMoveScale), true);
				}
				downX=x;
			}else{
				//从右往左
				if (nMoveLen<=0) {
					downX=x;
					return;
				}
				index=1;
				isClickDown=true;
				temp=(int)((downX-x)*nMoveScale);
				if (nMoveLen-temp<=0) {
					if (listener!=null) {
						listener.onMove(true, nMoveLen);
					}
					nMoveLen=0;
					updateBar(nLen-mRect.left, true);
				}else{
					nMoveLen-=temp;
					if (listener!=null) {
						listener.onMove(true, temp);
					}
					updateBar(-(float)(temp/nMoveScale), true);
				}
				downX=x;
			}
		}else if (eDirection==DIRECTION.VERTICAL) {
			if (Math.abs(y-downY)<2) {
				return;
			}
			//垂直
			if (y-downY>0) {
				//从上往下
				if (nMoveLen>=nAllLen) {
					downY=y;
					updateTop(4,2);
					return;
				}
				index=1;
				isClickDown=true;
				temp=(int)((y-downY)*nMoveScale);
				
				//Log.d(TAG, ">>>>>>>>temp:"+temp);
				
				if (nMoveLen+temp>=nAllLen) {
					nMoveLen=nAllLen;
					updateBar(nHeight-nLen-mRect.bottom, true);
					
				}else {
					nMoveLen+=temp;
					updateBar((float)(temp/nMoveScale), true);
				}
				updateTop(4,2);
				downY=y;
			}else {
				//从下往上
				if (nMoveLen<=0) {
					downY=y;
					updateTop(3,1);
					return;
				}
				index=1;
				isClickDown=true;
				temp=(int)((downY-y)*nMoveScale);
				//Log.d(TAG, ">>>>>>>>temp:"+temp);
				
				if (nMoveLen-temp<=0) {
					nMoveLen=0;
					updateBar(nLen-mRect.top, true);
				}else {
					nMoveLen-=temp;
					updateBar((float)(-temp/nMoveScale), true);
				}
				updateTop(3,1);
				downY=y;
			}
		}
	}
	
	/**
	 * 跳转到指定行
	 */
	public void gotoRow(int top){
		double nTop=(nAllCount-nRow+1)*Math.abs(nMoveLen)/nAllLen;
		double len=top*nAllLen/(nAllCount-nRow+1);
		if (nTop==top) {
			return;
		}
		//Log.d(TAG, "top:"+top+",nTop:"+nTop+",len:"+len);
		if (top>nTop) {
			turnPage((float)(len-nMoveLen), true);
		}else {
			turnPage(-(float)(nMoveLen-len), true);
		}
	}
	
	/**
	 * 滑动翻页
	 * @param len-移动的距离
	 * @param isV=true 垂直
	 */
	public void turnPage(float len,boolean isV){
		if (isV) {
			//垂直
			//Log.d(TAG, ">>>>>>>>len:"+len);
			if (len>0) {
				//从上往下
				if (nMoveLen>=nAllLen) {
					updateTop(4,2);
					return;
				}
				index=1;
				isClickDown=true;
				if (nMoveLen+len>=nAllLen) {
					nMoveLen=nAllLen;
					updateBar(nHeight-nLen-mRect.bottom, true);
					
				}else {
					nMoveLen+=len;
					updateBar((float)(len/nMoveScale), true);
				}
				updateTop(4,2);
			}else {
				//从下往上
				if (nMoveLen<=0) {
					updateTop(3,1);
					return;
				}
				index=1;
				isClickDown=true;
				len=-len;
				if (nMoveLen-len<=0) {
					nMoveLen=0;
					updateBar(nLen-mRect.top, true);
				}else {
					nMoveLen-=len;
					updateBar((float)(-len/nMoveScale), true);
				}
				updateTop(3,1);
			}
		}else{
			//水平
			if (len>0) {
				//从左往右
				index=1;
				isClickDown=true;
				if (nMoveLen+len>=nAllLen) {
					if (listener!=null) {
						listener.onMove(true,nMoveLen-nAllLen);
					}
					nMoveLen=nAllLen;
					updateBar(nWidth-nLen-mRect.right, true);
				}else {
					nMoveLen+=len;
					if (listener!=null) {
						listener.onMove(true,-len);
					}
					updateBar((float)(len/nMoveScale), true);
				}
			}else{
				//从右往左
				len=-len;
				index=1;
				isClickDown=true;
				if (nMoveLen-len<=0) {
					if (listener!=null) {
						listener.onMove(true, nMoveLen);
					}
					nMoveLen=0;
					updateBar(nLen-mRect.left, true);
				}else{
					nMoveLen-=len;
					if (listener!=null) {
						listener.onMove(true, len);
					}
					updateBar(-(float)(len/nMoveScale), true);
				}
			}
		}
	}
	
	/**
	 * @param update-1 往上移动，2往下移动
	 * @param type 滑动类型
	 *        type-1点击顶部按钮
	 *        type-2点击底部按钮
	 *        type-3向上滑动
	 *        type-4向下滑动
	 */
	private void updateTop(int type,int update){
		
		double top=(nAllCount-nRow+1)*Math.abs(nMoveLen)/nAllLen;
		int site=0;
		if (listener!=null) {
			if(update==2) {
				if (nMoveLen>=nAllLen) {
					listener.onUpdateView(update);
				}
			}else {
				listener.onUpdateView(update);
			}
			if(top<=0){
				site=0;//顶部
			}else {
				if (nMoveLen>=nAllLen) {
					site=2;//底部
				}else {
					site=1;//中间
				}
			}
			listener.onUpdate(type, (int)top,site);
		}
	}

	
	/**
	 * 改变滑动块位置
	 */
	public void updateBar(float len,boolean down){
		if (mRect==null) {
			return;
		}
		
		if(eDirection==DIRECTION.LEVEL){
			//水平
			mRect.left=mRect.left+len;
			mRect.right=mRect.right+len;
			
		}else if (eDirection==DIRECTION.VERTICAL) {
			//垂直
			mRect.top=mRect.top+len;
			mRect.bottom=mRect.bottom+len;
			if (mRect.top<len) {
				mRect.top=len;
				mRect.bottom=mRect.top+len;
			}
			
			if (mRect.top>nHeight-nLen) {
				mRect.top=nHeight-nLen;
				mRect.bottom=mRect.top+nLen;
			}
		}
		//Log.d("SKScene", "bar nMoveLen:"+nMoveLen+",len:"+len*nMoveScale);
		
		clicks[1]=down;
		
	}
	
//	/**
//	 * 翻页更新滚动条位置
//	 */
//	public void updateXY(double len){
//		
//		if (nMoveLen+len>=nAllLen) {
//			nMoveLen=nAllLen;
//			updateBar(nHeight-nLen-mRect.bottom, false);
//			listener.onUpdateView(2);
//		}else {
//			nMoveLen+=len;
//			updateBar((float)(len/nMoveScale), false);
//			if (nMoveLen==nAllLen) {
//				listener.onUpdateView(2);
//			}else {
//				listener.onUpdateView(1);
//			}
//		}
//
//	}
	
	/**
	 * 把滑动块移动到最下面
	 */
	public void moveToBottom(){
		double result=0;
		result=(nHeight-nLen-mRect.bottom)*nMoveScale;
		if (nMoveLen+result>=nAllLen) {
			nMoveLen=nAllLen;
			if (listener!=null) {
				listener.onUpdateView(2);
			}
			updateBar(nHeight-nLen-mRect.bottom, true);
		}else {
			nMoveLen+=result;
			updateBar((float)(result/nMoveScale), true);
		}
	}
	
	/**
	 * 把滑动块移动到最上面
	 */
	public void moveToTop(){
		nMoveLen=0;
		updateBar(nLen-mRect.top,false);
	}
	
	
	/**
	 * 点击
	 */
	public boolean clicXY(float x,float y){
		boolean result=false;
		if (mRect==null) {
			return result;
		}
		if (eDirection==DIRECTION.LEVEL) {
			/**
			 * 水平
			 */
			if (y<nLeftY+nHeight-30) {
				return result;
			}
			
			if (x<nLeftX+nLen) {
				//左边
				if (nMoveLen<=0) {
					return result;
				}
				
				result=true;
				index=0;
				isClickDown=true;
				if (nMoveLen-mVelocity<=0) {
					if (listener!=null) {
						listener.onMove(true, nMoveLen);
					}
					nMoveLen=0;
					updateBar(nLen-mRect.left,false);
				}else {
					nMoveLen-=mVelocity;
					if (listener!=null) {
						listener.onMove(true, mVelocity);
					}
					updateBar(-(float)(mVelocity/nMoveScale),false);
				}
				clicks[0]=true;
			}
			if(x>nLeftX+nWidth-nLen){
				//右边
				if (nMoveLen>=nAllLen) {
					return result;
				}
				result=true;
				index=2;
				isClickDown=true;
				if (nMoveLen+mVelocity>=nAllLen) {
					if (listener!=null) {
						listener.onMove(true, nMoveLen-nAllLen);
					}
					nMoveLen=nAllLen;
					updateBar(nWidth-nLen-mRect.right, false);
				}else {
					nMoveLen+=mVelocity;
					if (listener!=null) {
						listener.onMove(true, -mVelocity);
					}
					updateBar((float)(mVelocity/nMoveScale), false);
				}
				clicks[2]=true;
				
			}
		}else if(eDirection==DIRECTION.VERTICAL) {
			/**
			 * 垂直
			 */
//			if (nAllCount<nRow) {
//				return true;
//			}
			if(x<nLeftX+nWidth-30)
				return result;
			if (y<nLeftY+nLen) {
				//顶部
				if (nMoveLen<=0) {
					updateTop(1,1);
					return result;
				}
				result=true;
				index=0;
				isClickDown=true;
				if (nMoveLen-mVelocity<=0) {
					updateBar(nLen-mRect.top,false);
					nMoveLen=0;
				}else {
					updateBar(-(float)(mVelocity/nMoveScale),false);
					nMoveLen-=mVelocity;
				}
				updateTop(1,1);
				clicks[0]=true;
			}
			if (y>nLeftY+nHeight-nLen) {
				//底部
				if (nMoveLen>=nAllLen) {
					updateTop(2,2);
					return result;
				}
				result=true;
				index=2;
				isClickDown=true;
				if (nMoveLen+mVelocity>=nAllLen) {
					nMoveLen=nAllLen;
					updateBar(nHeight-nLen-mRect.bottom, false);
				}else {
					nMoveLen+=mVelocity;
					updateBar((float)(mVelocity/nMoveScale),false);
				}
				updateTop(2,2);
				clicks[2]=true;
			}
		}
		return result;
	}
	
	/**
	 * 画按钮和滑动块
	 * @param type 0=左边，1=中间，2=右边
	 */
	public void drawButton(int type){
		switch (type) {
		case 0://左边or顶部
			if (clicks[0]) {
				if (eDirection==DIRECTION.LEVEL) {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_l_btn_n, mContext), 
							0, 0, mBitmapPaint);
				}else {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_t_btn_n, mContext), 
							0, 0, mBitmapPaint);
				}
				clicks[0]=false;
			}else{
				if (eDirection==DIRECTION.LEVEL) {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_l_btn_d, mContext), 
							0, 0, mBitmapPaint);
				}else {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_t_btn_d, mContext), 
							0, 0, mBitmapPaint);
				}
			}
			break;
		case 1://中间滑动块
			if (clicks[1]) {
				mCanvas.drawBitmap(mMoveNBitmap, mRect.left, mRect.top, mBitmapPaint);
				clicks[1]=false;
			}else{
				mCanvas.drawBitmap(mMoveDBitmap, mRect.left, mRect.top, mBitmapPaint);
			}
			break;
		case 2://右边or底部
			if (clicks[2]) {
				if (eDirection==DIRECTION.LEVEL) {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_r_btn_n, mContext), 
							nWidth-nLen, 0, mBitmapPaint);
				}else {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_b_btn_n, mContext), 
							0, nHeight-nLen, mBitmapPaint);
				}
				clicks[2]=false;
			}else{
				if (eDirection==DIRECTION.LEVEL) {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_r_btn_d, mContext), 
							nWidth-nLen, 0, mBitmapPaint);
				}else {
					mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_b_btn_d, mContext), 
							0, nHeight-nLen, mBitmapPaint);
				}
			}
			break;
		}
	}
	
	/**
	 * 重新设置滚动条的长度
	 */
	public void resetBarLen(int len){
		double nScale=1;
		if (eDirection==DIRECTION.LEVEL) {
			nWidth=len;
			nScale=((double)(nWidth-2*nLen))/(nAllLen+nWidth-2*nLen);
			float width=mRect.width();
			mRect.left=(int)(nMoveLen*nScale)+nLen;
			mRect.right=(int)(width*nScale)+mRect.left;
		}else if (eDirection==DIRECTION.VERTICAL) {
			nHeight=len;
			nScale=((double)(nHeight-2*nLen))/nAllLen;
			float height=mRect.height();
			mRect.top=(int)(nMoveLen*nScale)+nLen;
			mRect.bottom=(int)(height*nScale)+mRect.top;
			
			if (mRect.top<len) {
				mRect.top=len;
				mRect.bottom=mRect.top+len;
			}
			
			if (mRect.top>nHeight-nLen) {
				mRect.top=nHeight-nLen;
				mRect.bottom=mRect.top+nLen;
			}
		}
		if(nWidth<1){
			nWidth=1;
		}
		if(nHeight<1){
			nHeight=1;
		}
		mBitmap=Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		mCanvas=new Canvas(mBitmap);
		
		mMoveDBitmap=getBitmap(true);
		mMoveNBitmap=getBitmap(false);
	}
	
	/**
	 * 重新设置可以拖动区域的长度
	 */
	public void resetBarLen(){
		double nScale=1;
		if (eDirection==DIRECTION.LEVEL) {
			//水平
			nScale=((double)(nWidth-2*nLen))/(nAllLen+nWidth-2*nLen);
			mRect.left=nLen;
			mRect.top=0;
			mRect.right=(int)((nWidth-2*nLen)*nScale)+nLen;
			mRect.bottom=nLen;
			if (mRect.width()<nLen) {
				mRect.right=mRect.left+nLen;
			}
			
			nMoveScale=((float)nAllLen)/(nWidth-2*nLen-mRect.width());
			
			
			if (nMoveLen>0) {
				mRect.left+=nMoveLen/nMoveScale;
				mRect.right+=nMoveLen/nMoveScale;
			}
			
		}else if (eDirection==DIRECTION.VERTICAL) {
			//垂直
			nScale=((float)(nHeight-2*nLen))/(nAllLen+nHeight-2*nLen);
			mRect.left=0;
			mRect.top=nLen;
			mRect.right=nLen;
			mRect.bottom=(int)((nHeight-2*nLen)*nScale)+nLen;
			if (mRect.height()<nLen) {
				mRect.bottom=mRect.top+nLen;
			}
			nMoveScale=((float)nAllLen)/(nHeight-2*nLen-mRect.height());
			
			if (nMoveLen>0) {
				mRect.top+=nMoveLen/nMoveScale;
				mRect.bottom+=nMoveLen/nMoveScale;
			}
		}
		
		mMoveDBitmap=getBitmap(true);
		mMoveNBitmap=getBitmap(false);
	}
	
	/**
	 * 获取滚动块的图片
	 */
	private Bitmap getBitmap(boolean defualt) {
		Bitmap bitmap1 = null;
		
		int nWidth=24;
		int nHeiht=24;
		
		if (eDirection==DIRECTION.VERTICAL) {
			if (mRect.height()==0) {
				nHeiht=24;
			}else {
				nHeiht=(int)mRect.height();
			}
		}else {
			if (mRect.width()==0) {
				nWidth=24;
			}else {
				nWidth=(int)mRect.width();
			}
		}
		if(nWidth<1){
			nWidth=1;
		}
		if(nHeiht<1){
			nHeiht=1;
		}
		bitmap1=Bitmap.createBitmap(nWidth,nHeiht, Config.ARGB_8888);
		Canvas mCanvas=new Canvas(bitmap1);
		
		Rect rect=new Rect(0,0,(int)mRect.width(),(int)mRect.height());
		if (eDirection==DIRECTION.LEVEL) {
			if (defualt) {
				mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_h_bar_d, mContext), 
						null, rect, mBitmapPaint);
			}else {
				mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_h_bar_n, mContext), 
						null, rect, mBitmapPaint);
			}
		}else {
			if (defualt) {
				mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_v_bar_d, mContext), 
						null, rect, mBitmapPaint);
			}else {
				mCanvas.drawBitmap(ImageFileTool.getBitmap(R.drawable.scroll_v_bar_n, mContext), 
						null, rect, mBitmapPaint);
			}
		}
		
		boolean draw=true;
		Bitmap bitmap=null;
		if (eDirection==DIRECTION.LEVEL) {
			//水平
			if (mRect.width()>=30) {
				bitmap=ImageFileTool.getBitmap(R.drawable.h_move, mContext);
			}else {
				draw=false;
			}
		}else if (eDirection==DIRECTION.VERTICAL) {
			//垂直
			if (mRect.height()>=30) {
				bitmap=ImageFileTool.getBitmap(R.drawable.v_move, mContext);
			}else {
				draw=false;
			}
		}
		if (draw) {
			int left=(int)(mRect.width()-bitmap.getWidth())/2;
			int top=(int)(mRect.height()-bitmap.getHeight())/2;
			
			mCanvas.drawBitmap(bitmap, left, top, mBitmapPaint);
		}
		
		return bitmap1;
	}

	/**
	 * 滚动回调接口
	 */
	interface ScrollListener{
		/**
		 * @param dir-移动的方向
		 * @param len-移动的距离
		 */
		void onMove(boolean isHMove,double len);
		
		/**
		 * @param top=开始id
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
		void onUpdate(int type,int top,int site);
		
		/**
		 * 
		 */
		void onUpdateView(int type);
	}
	
	/**
	 * 返回可以设置的总数
	 */
	public int getShowCount(){
		double count=0;
		count=(nAllCount-nRow+1)*Math.abs(nAllLen)/nAllLen;
		return (int)count;
	}
	
	public void setnAllLen(double nAllLen) {
		if (nMoveLen>nAllLen) {
			nMoveLen=nAllLen;
		}
		this.nAllLen = nAllLen;
	}

	public void setnMoveLen(double nMoveLen) {
		this.nMoveLen = nMoveLen;
	}
	
	public double getnAllLen() {
		return nAllLen;
	}

	public double getnMoveLen() {
		return nMoveLen;
	}

}
