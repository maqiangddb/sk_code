package com.android.Samkoonhmi.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.SKTrendsThread;
import com.android.Samkoonhmi.adapter.ViewAdapter;
import com.android.Samkoonhmi.model.SceneItemPosInfo;
import com.android.Samkoonhmi.model.skmenu.SKMenuPageInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKKeyPopupWindow;
import com.android.Samkoonhmi.skwindow.DragView;
import com.android.Samkoonhmi.skwindow.PageNumView;
import com.android.Samkoonhmi.skwindow.SKMenuManage;
import com.android.Samkoonhmi.skwindow.SKMenuPageItemView;
import com.android.Samkoonhmi.skwindow.SKMenuPageView;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.SKLanguage;

/**
 * 画面菜单activity
 */
public class SKSceneMenu extends Activity implements  OnTouchListener,OnLongClickListener{

	private ArrayList<View> mSceneItems;
	private ViewAdapter adapter;
	private Gallery gallery;
	private PageNumView mNumView;
    private DragView mDragView;
    private SKMenuPageInfo mDragMenuInfo;//进行拖动的menuInfo
    private Vibrator mVibrator;
    private Bitmap iconBitmap = null;
     
    private int nPageNum;       //页面的总数
	private int nPageIndex;     //当前页面
	private int nOrgPageIndex;  //拖动view的初始页面
	private boolean canDrag;    // 是否可以拖动  true=可以
	private boolean canClick = true;// 是否可以点击
	private int mScrollZone ;    //进行屏幕切换的边界
	private int m_iClickX;     //最新的点击屏幕X坐标
	private int m_iClickY;     //最新的点击屏幕Y坐标
	
	//private ArrayList<Integer> iconArray = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.sk_scene_menu);
		SKSceneManage.getInstance().setActivity(this);
		
		
		
		initData();
		loadData();
		//进入界面动画
		overridePendingTransition(R.anim.dialog_up_enter,
				R.anim.dialog_bottom_out);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SKSceneManage.getInstance().isStarting=false;
		//自定义键盘是否弹出
		SKKeyPopupWindow.keyFlagIsShow=true;
		
	}
	
	/*
	 * 初始化数据
	 */
	private void initData(){
		mDragMenuInfo = new SKMenuPageInfo();
		mSceneItems = new ArrayList<View>();
		gallery = (Gallery) findViewById(R.id.sk_menu_gallery);
		mNumView = (PageNumView) findViewById(R.id.page_num_view);
		nPageIndex=1;
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		iconBitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.a71);
		mScrollZone = SKMenuManage.getInstance().dip2px(this, 30); 
		
//		iconArray.add(R.drawable.a71);
//		iconArray.add(R.drawable.a72);
//		iconArray.add(R.drawable.a73);
//		iconArray.add(R.drawable.a74);
//		iconArray.add(R.drawable.a75);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
			// 应用程序退出，不是画面切换的退出
			SKTimer.getInstance().destroy();
			SKThread.getInstance().destory();
			SKLanguage.getInstance().destory();
			SKTrendsThread.getInstance().destory();
			SKPlcNoticThread.getInstance().stop();
			SKSceneManage.getInstance().exitSceneMacros(SKSceneManage.getInstance().nSceneId);
			SKSceneManage.getInstance().destroy();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化页面
	 */
	private void loadData() {
		if (SKMenuManage.getInstance().mPageMap.size()==0) {
			SKMenuManage.getInstance().setData(null);
		}
		
		if (SKMenuManage.getInstance().mPageMap != null) {
			nPageNum = SKMenuManage.getInstance().mPageMap.size();
			for (int i = 1; i <= nPageNum; i++) {
				createPage(SKMenuManage.getInstance().mPageMap.get(i));
			}
		}
		loadView();
	}

	/**
	 * 创建页面
	 */
	private void createPage(ArrayList<SceneItemPosInfo> list) {
		ArrayList<SKMenuPageInfo> mItemList = new ArrayList<SKMenuPageInfo>();

		for (int i = 0; i < list.size(); i++) {
			SKMenuPageItemView view = new SKMenuPageItemView(this, list.get(i), iconBitmap);
			final SKMenuPageInfo info = new SKMenuPageInfo();
			info.nPageIndex = list.get(i).nPageId;
			info.view = view;
			info.nPagePos = list.get(i).nPagePos;
			info.view.setOnLongClickListener(this);
			mItemList.add(info);
		}

		SKMenuPageView view = new SKMenuPageView(this, mItemList,
				SKMenuManage.getInstance().nItemWidth,
				SKMenuManage.getInstance().nItemHeight);
		mSceneItems.add(view);
	}
	
	/*
	 * 重新初始化数据
	 */
	private void reLoad(){
		mSceneItems.clear();
		for (int i = 1; i <= nPageNum; i++) {
			createPage(SKMenuManage.getInstance().mPageMap.get(i));
		}
		adapter.notifyDataSetChanged(mSceneItems);
		
		//重新置空 拖动的view
		mDragMenuInfo.view = null;
	}
	
	private float nDownX = 0;
	private float nDownY = 0;
	/**
	 * 加载页面
	 */
	private void loadView() {
		adapter = new ViewAdapter(mSceneItems);
		gallery.setAdapter(adapter);
		gallery.setOnTouchListener(this);

		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (nPageNum > 0) {
					if (null != mNumView) {
						mNumView.setmCount(nPageNum);
						mNumView.setmIndex(position + 1);
						mNumView.invalidate();
						nPageIndex=position+1;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		
		//进入当前画面所在页面
		int nSceneId=SKSceneManage.getInstance().nSceneId;
		if (nSceneId>0) {
			if (mSceneItems!=null) {
				//所有页
				for (int i = 0; i < mSceneItems.size(); i++) {
					SKMenuPageView mView=(SKMenuPageView)mSceneItems.get(i);
					//每一页
					for (int j = 0; j < mView.list.size(); j++) {
						SKMenuPageInfo info=mView.list.get(j);
						//每一小项
						if (info.view.nSceneId==nSceneId) {
							nPageIndex=info.nPageIndex;
							gallery.setSelection(nPageIndex-1);
							i=mSceneItems.size();
							break;
						}
						
					}
				}
			}
		}
	}
	
	
	/**
	 * 截取点击事件，进行手动分配
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		m_iClickX =  (int) ev.getX();
		m_iClickY = (int) ev.getY();
		
		if(true == canDrag ){
			onDragTouch(null, ev);
			return true;
		}
		else{
			onTouch(null, ev);
			gallery.onTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	//
	private boolean overdelay = true; // 约定 为true 满足条件就进行换屏
	private int DELAY_TIME = 1000;   // 延迟时间
	private Handler mHandler = new Handler();
	private Runnable delayRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			overdelay = true;
		}
	};
	

	/**
	 * 拖动处理
	 */
	private boolean onDragTouch(View v, MotionEvent ev){
		
		final int action = ev.getAction();
	    final int screenX = clamp((int)ev.getX(), 0, SKSceneManage.nSceneWidth);
	        
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		break;
		case MotionEvent.ACTION_MOVE:
		{
            mDragView.move((int)ev.getX(), (int)ev.getY());
            //进行换屏
            if ( screenX < mScrollZone && overdelay) {
            	gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
            	overdelay = false;
            	mHandler.postDelayed(delayRun, DELAY_TIME);
            }
            else if ( screenX > SKSceneManage.nSceneWidth - mScrollZone && overdelay) {
                gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                overdelay = false;
                mHandler.postDelayed(delayRun, DELAY_TIME);
            } 
		}
		break;
		case MotionEvent.ACTION_UP:
		{
			endDrag();
			afterDrag(ev.getX(), ev.getY());
		}
		break;
       case MotionEvent.ACTION_CANCEL:
       {
    	   endDrag();
       }
       break;
		default:
			break;
		}
		
		return true;
		
	}

	private boolean isBack = false;
	private SKMenuPageItemView clickView = null;
	/**
	 * 点击情况
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			nDownX = event.getX();
			nDownY = event.getY();
			SKMenuPageInfo menuInfo = getMenuItemInfoByXY(nDownX, nDownY, nPageIndex);
			if (menuInfo != null && menuInfo.view.isInImageRect((int)nDownX, (int)nDownY)) {
				clickView = menuInfo.view;
				clickView.setflag(true);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(nDownX-event.getX())>(SKSceneManage.nSceneWidth/8)) {
				canClick=false;//如果移动，就不处理点击事件
				
				if (clickView != null) {
					clickView.setflag(false);
				}
			}else {
				canClick = true;
			}
			if (Math.abs(nDownY-event.getY())>Math.abs(nDownX-event.getX())) {
				if (nDownY-event.getY()>SKSceneManage.nSceneHeight/6) {
					//往上滑动进入原来界面
					if(!isBack){
						isBack=true;
						SKSceneOne.update=true;
						Intent intent = new Intent();
						intent.setClass(SKSceneMenu.this, SKSceneOne.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						SKSceneMenu.this.finish();
					}
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (Math.abs(nDownX-event.getX())>(SKSceneManage.nSceneWidth/8)) {
				canClick=false;//如果移动，就不处理点击事件
			}
			else{
				canClick = true;
			}
			
			if (clickView != null) {
				clickView.setflag(false);
				
				if (action == MotionEvent.ACTION_UP) 
				{
					performClick(clickView);
				}
			}
			endDrag();
			break;
		}
		return false;
	}
	
	private void performClick(SKMenuPageItemView view){
		
		if (canClick && view.isInImageRect(m_iClickX, m_iClickY)) 
		{
			view.setEnabled(false);
			SKSceneManage.getInstance().nSceneId = view.nSceneId;
			Intent intent = new Intent();
			SKSceneOne.update=true;
			intent.setClass(SKSceneMenu.this, SKSceneOne.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			SKSceneMenu.this.finish();
		}
	}
	
	/**
	 * 开始拖动
	 * @param v
	 */
	private void startDrag(View v){
		
		SKMenuPageItemView itemView = (SKMenuPageItemView) v;
		itemView.setflag(true);
		
		Bitmap bitmap = getViewBitmap(v);
		if (bitmap == null) {
			return;
		}
        itemView.setVisibility(View.GONE);
        mDragMenuInfo.nPageIndex = itemView.getPageIndex();
        mDragMenuInfo.nPagePos = itemView.getPagePos();
        mDragMenuInfo.view = itemView;
        nOrgPageIndex = itemView.getPageIndex();
        canDrag  = true;
        
		//进行设置震动
        if (mVibrator != null) {
			mVibrator.vibrate(100);
		}
		
		//关闭键盘
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        
       mDragView = new DragView(getApplicationContext(), bitmap, 
                0, 0, bitmap.getWidth(), bitmap.getHeight());
        mDragView.show(v.getWindowToken(), v.getLeft() - SKMenuManage.getInstance().nItemWidth /2
        		, v.getTop() - SKMenuManage.getInstance().nItemHeight / 2);
        
        if(bitmap != null){
        	bitmap.recycle();
        	bitmap = null;
        }

	}
	/**
	 * 结束拖动
	 */
	private void endDrag(){
		if (canDrag) 
		{
			canDrag = false;
			if (mDragView != null) {
				mDragView.remove();
				mDragView = null;
			}
			if (mDragMenuInfo != null) {
				mDragMenuInfo.view.setVisibility(View.VISIBLE);
				mDragMenuInfo.view.setflag(false);
			}
		}
	}
	
	/**
	 * 拖动后 相关处理
	 */
	private void afterDrag( float x, float y){
		
		final SKMenuPageInfo info = getMenuItemInfoByXY(x,y, nPageIndex);//松手位置的item
		final SKMenuPageView view = (SKMenuPageView) mSceneItems.get(nPageIndex - 1 );//当前的页面
		final int pagePos  = getMenuItemPosByXY(x, y);//松手时候的页面位置，取值范围0~11

		//两个item进行交换的情况
		if (info != null)
		{
			final int nOldpagePos = mDragMenuInfo.nPagePos;//5
			canClick = false;
			info.view.bringToFront();
			//动画相关
			TranslateAnimation animation = null;
			int duration  = 0;
			int fromX = info.view.getLeft();;
			int fromY = info.view.getTop();;
			int toX = mDragMenuInfo.view.getLeft();
			int toY = mDragMenuInfo.view.getTop();
			if (nPageIndex - nOrgPageIndex > 0) {
					toX = - SKMenuManage.getInstance().nItemWidth;
			}
			else if (nPageIndex - nOrgPageIndex < 0) {
					toX= SKSceneManage.nSceneWidth;
			}
			
			animation = new TranslateAnimation(0, toX - fromX, 0, toY - fromY);
			duration = (int) Math.sqrt((fromX - toX)* (fromX - toX) + (fromY - toY)*(fromY - toY))/3 ;
			if (duration < 200) {
				duration = 200;
			}
			animation.setDuration(duration);
			
			if (nPageIndex == mDragMenuInfo.nPageIndex) {
				//同一个页面
				view.setPageInfo(mDragMenuInfo, info.nPagePos, nPageIndex);
				animation.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}
					public void onAnimationRepeat(Animation animation) {
					}
					public void onAnimationEnd(Animation animation) {
						view.setPageInfo(info, nOldpagePos, nPageIndex);
						reLoad();
						canClick = true;
					}
				});
				
				info.view.setAnimation(animation);
				animation.startNow();
				
				mDragMenuInfo.nPagePos =  pagePos;					
				SKMenuManage.getInstance().updateMapItem(mDragMenuInfo, mDragMenuInfo.nPageIndex, false);
				SKMenuManage.getInstance().updateMapItem(info, info.nPageIndex, false);
				
			}else {
				//不同页面
				final SKMenuPageView  orgView = (SKMenuPageView) mSceneItems.get(mDragMenuInfo.nPageIndex - 1);
				
				animation.setAnimationListener(new AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}
					public void onAnimationRepeat(Animation animation) {
					}
					public void onAnimationEnd(Animation animation) {
						orgView.addPageInfoItem(info, nOldpagePos, nOrgPageIndex);
						
						mDragMenuInfo.nPagePos = pagePos;
						mDragMenuInfo.nPageIndex = nPageIndex;
						SKMenuManage.getInstance().updateMapItem(mDragMenuInfo, nOrgPageIndex, true);
						SKMenuManage.getInstance().updateMapItem(info, nPageIndex, true);
						
						reLoad();
						canClick = true;
					}
				});
				info.view.setAnimation(animation);
				animation.startNow();
				
				//移除
				orgView.removePageInfoItem(mDragMenuInfo);
				view.removePageInfoItem(info);
				
				 //添加
				view.addPageInfoItem(mDragMenuInfo, pagePos, nPageIndex);	
			}
		}
		else //拖动到空白地方的情况                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
		{
			if (nPageIndex == mDragMenuInfo.nPageIndex) {
				//同一个页面
				view.setPageInfo(mDragMenuInfo, pagePos, nPageIndex);
				mDragMenuInfo.nPagePos = pagePos;
				SKMenuManage.getInstance().updateMapItem(mDragMenuInfo, mDragMenuInfo.nPageIndex,  false);
				
			}else {
				//不同页面
				SKMenuPageView  orgView = (SKMenuPageView) mSceneItems.get(mDragMenuInfo.nPageIndex - 1);
				orgView.removePageInfoItem(mDragMenuInfo);
				view.addPageInfoItem(mDragMenuInfo, pagePos, nPageIndex);
				
				mDragMenuInfo.nPagePos = pagePos;
				mDragMenuInfo.nPageIndex = nPageIndex;
				SKMenuManage.getInstance().updateMapItem(mDragMenuInfo,nOrgPageIndex, true);
			}
			reLoad();
		}
		
		
	}
	
	
	 private int clamp(int val, int min, int max) {
	        if (val < min) {
	            return min;
	        } else if (val >= max) {
	            return max - 1;
	        } else {
	            return val;
	        }
	    }
	 
	 /**
	  * 获取拖动图片
	  */
	 private Bitmap getViewBitmap(View v) {
	        v.clearFocus();
	        v.setPressed(false);

	        boolean willNotCache = v.willNotCacheDrawing();
	        v.setWillNotCacheDrawing(false);

	        int color = v.getDrawingCacheBackgroundColor();
	        v.setDrawingCacheBackgroundColor(0);

	        if (color != 0) {
	            v.destroyDrawingCache();
	        }
	        v.buildDrawingCache();
	        Bitmap cacheBitmap = v.getDrawingCache();
	        if (cacheBitmap == null) {
	            return null;
	        }

	        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

	        v.destroyDrawingCache();
	        v.setWillNotCacheDrawing(willNotCache);
	        v.setDrawingCacheBackgroundColor(color);

	        return bitmap;
	    }
	    
		/**
		 * 长按事件启动 拖动
		 * @param v
		 * @return
		 */
	@Override
	public boolean onLongClick(View v) {

		//start==true 是有拖动的情况
		if (v instanceof SKMenuPageItemView && canClick) {
			
			SKMenuPageItemView view = (SKMenuPageItemView) v;
			if (view.isInImageRect(m_iClickX, m_iClickY)) {
				startDrag(v);
			}	
		}
		return false;
	}
	
	/**
	 * 获取点击位置的SKMenuPageInfo
	 */
	private SKMenuPageInfo getMenuItemInfoByXY(float x, float y, int pageIndex){
		
		if (pageIndex <0 || pageIndex > mSceneItems.size()) {
			return null;
		}
		 
		SKMenuPageView view = (SKMenuPageView) mSceneItems.get(pageIndex - 1 );
		if(view != null){
			
			ArrayList<SKMenuPageInfo> infoList = view.getPageInfo();
			
			//Log.d("test", "....."+infoList.size());
			for(SKMenuPageInfo info: infoList){
				
				//Log.d("test", "info page:"+info.nPageIndex+".pos:"+info.nPagePos);
				if (x >= info.view.getLeft() && x < info.view.getRight()  
						&& y >= info.view.getTop() && y < info.view.getBottom() ) {
					if (mDragMenuInfo.view == null || mDragMenuInfo.view != null  && info.view.nSceneId != mDragMenuInfo.view.nSceneId) {
						return info;
					}
					
				
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 获取当前的点击位置
	 */
	private int getMenuItemPosByXY(float x, float y){
		int itemWidth = SKMenuManage.getInstance().nItemWidth;
		int itemHigh = SKMenuManage.getInstance().nItemHeight;
		
		for(int i =0; i < 12; i++ ){
			int line = i / 4;
			int row  = i % 4;
			
			if ( x >= row * itemWidth && x < (row + 1) * itemWidth
					&& y >= line * itemHigh && y < (line + 1) * itemHigh) {
				return i;
			}
		}
		
		return 0;
	}
	
	
}
