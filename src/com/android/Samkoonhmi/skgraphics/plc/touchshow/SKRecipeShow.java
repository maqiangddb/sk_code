package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Pattern;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.SKTimer;
import com.android.Samkoonhmi.can.can;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.DragTable;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.RecipeShowInfo;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableLoadInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.WINDOW_TYPE;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.EditRecipeInfo;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.IRecipeCallBack;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.DeleteDialog;
import com.android.Samkoonhmi.skwindow.SKMenuManage;
import com.android.Samkoonhmi.skwindow.SKRecipeDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.ContextUtl;
import com.android.Samkoonhmi.util.GlobalPopWindow;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TextAlignUtil;


/**
 * 配方显示器
 * 
 * @author 刘伟江 创建时间 2012-7-28
 */
public class SKRecipeShow extends SKGraphCmnTouch implements IItem{
 
	private static final String TAG = "SKRecipeShow";
	// 表格内容
	private boolean flag;
	private int nSceneId;
	private int nItemId;
	private String sTaskName;
	private final Context mContext;
	private RecipeShowInfo info;
	private SKItems items;
	private boolean show; // 是否可显现
	private boolean touch; // 是否可触控
	private boolean showByAddr; // 是否注册显现地址
	private boolean touchByAddr; // 是否注册触控地址
	private boolean showByUser; // 是否受用户权限控件
	private boolean touchByUser; // 是否受用户权限控件
	private DragTable dTable;
	private RecipeOGprop mRecipeData;
	private DeleteDialog deleteDialog;
	private GlobalPopWindow keyPop; 
	private DragTableInfo dInfo;
	private ArrayList<String> mHList;
	private Vector<RowCell> mRowCells;
	private int nDataRank;//实际的数据列
	private int nColum;//界面可以显示的数据列
	private int nTop;//显示起始行
	private int nAllCount;//总共的数据量
	private boolean isLoading;//正在加载中
	private boolean bUntreated;//有未处理加载
	private TableLoadInfo mLoadInfo;//加载数据信息包
	public static ArrayList<Observer> mObservers = new ArrayList<Observer>();
	private Observer updateObserver;
	private SKKeyPopupWindow popKey = null;
	private GlobalPopWindow systemKey = null;
	private Point mClickPoint ;
	private boolean isFirstInit = true;
	private ArrayList<String[]> mRecipeGroup = null; //配方组 元素信息

	public SKRecipeShow(Context context, int sId, int itemId,RecipeShowInfo info) {
		this.mContext = context;
		this.nSceneId = sId;
		this.nItemId = itemId;
		this.flag = true;
		sTaskName = "";
		this.info=info;
		mLoadInfo=new TableLoadInfo();
		mClickPoint = new Point();
		
		if (info!=null) {
			Rect rect = new Rect(info.getnStartPosX(),
					info.getnStartPosY(), info.getnStartPosX()
							+ info.getnWidth(), info.getnStartPosY()
							+ info.getnHeight());
			
			items = new SKItems();
			items.itemId = nItemId;
			items.sceneId = nSceneId;
			items.nZvalue = info.getnZvalue();
			items.nCollidindId = info.getnCollidindId();
			items.rect = rect;
			items.mGraphics=this;
			
			if (dInfo==null) {
				
				nColum = info.getnColum()+1; // 要显示的列数
				nDataRank=DBTool.getInstance().getmRecipeDataBiz().getRecipeElementCount(info.getnRecipeGroupId())+1;
				if (info.isbShowRecipeID()) {
					nDataRank++;
				}
				
				dInfo = new DragTableInfo();
				dInfo.setnAlpha(info.getnAlpha());
				dInfo.setnLineColor(info.getnLineColor());
				dInfo.setnFrameColor(info.getnLineColor());
				dInfo.setnTableBackcolor(info.getnTextBackColor());
				dInfo.setnTitleFontColor(info.getnHTitleTextColor());
				dInfo.setnTitleFontSize((short)info.getnHTitleFontSize());
				dInfo.setnTitleBackcolor(info.getnHTitleBackColor());
				dInfo.setnVTitleFontColor(info.getnVTitleTextColor());
				dInfo.setnVTitleFontSize((short)info.getnVTitleFontSize());
				dInfo.setnVTitleBackcolor(info.getnVTitleBackColor());
				dInfo.setnTextFontColor(info.getnTextColor());
				dInfo.setnTextFontSize((short)info.getnTextFontSize());
				dInfo.setmAlign(info.geteType());
				dInfo.setmTypeFace(Typeface.DEFAULT);
				dInfo.setmHTypeFace(TextAlignUtil.getTypeFace(info.getsHTitleFont()));
				dInfo.setmVTypeFace(TextAlignUtil.getTypeFace(info.getsVTitleFont()));
				dInfo.setnRow((short) (info.getnRow()+1));
				dInfo.setnRank((short) nColum);
				dInfo.setnDataRank((short)nDataRank);
				dInfo.setmRowWidth(info.getmRowWidht());
				dInfo.setmRowHeight(info.getmRowHeight());
				dInfo.setnWidth(info.getnWidth());
				dInfo.setnHeight(info.getnHeight());
				dInfo.setnLeftTopX(info.getnStartPosX());
				dInfo.setnLeftTopY(info.getnStartPosY());
				dTable = new DragTable(dInfo, mContext, items,true);
				dTable.setiClickListener(iListener);
				dTable.setiPageTurning(iTurning);
				dTable.init(null);
				
				dTable.initData(mRowCells, mHList,0);
				dTable.drawTable();
			}
			
			this.show = true;
			this.touch = true;
			this.showByAddr = false;
			this.touchByAddr = false;
			this.showByUser = false;
			this.touchByUser = false;
			mRecipeGroup = RecipeDataCentre.getInstance().getRecipeGroup(info.getnRecipeGroupId());
			
			// 显现权限
			if (info.getmShowInfo() != null) {
				if (info.getmShowInfo().getShowAddrProp()!=null) {
					// 受地址控制
					showByAddr = true;
				}
				if (info.getmShowInfo().isbShowByUser()) {
					// 受用户权限控制
					showByUser = true;
				}
			}

			// 触控权限
			if (info.getmTouchInfo() != null) {
				if (info.getmTouchInfo().getTouchAddrProp()!=null) {
					// 受地址控制
					touchByAddr = true;
				}

				if (info.getmTouchInfo().isbTouchByUser()) {
					// 受用户权限控制
					touchByUser = true;
				}
			}

			// 注册显现地址
			if (showByAddr) {
				ADDRTYPE addrType = info.getmShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(), showCall,
							true,nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(), showCall,
							false,nSceneId);
				}

			}

			// 注册触控地址
			if (touchByAddr) {
				ADDRTYPE addrType = info.getmTouchInfo().geteCtlAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
							touchCall, true,nSceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
							touchCall, false,nSceneId);
				}
			}
		}
		
	}

	@Override
	public void getDataFromDatabase() {
		
	}

	@Override
	public void setDataToDatabase() {

	}

	@Override
	public void initGraphics() {
		init();
	}

	/**
	 * 初始化表格每一格的宽高
	 */
	private void init() {
		if (info == null) {
			
			return;
		}
		
		this.isLoading=false;
		this.bUntreated=false;
		isFirstInit = true;
		
		flag=true;
		mLoadInfo.nLoadType=0;
		mLoadInfo.nLoadCount=info.getnRow();
		
		// 注册通知
		register();
		
		itemShow();
		itemTouch();
		
		//添加监听
		updateObserver = new UpdateObserver();
		mObservers.add(updateObserver);
		
		SKSceneManage.getInstance().onRefresh(items);
		
	}
	
	// 点击事件
	public SKRecipeDialog sDialog;
	DragTable.IClickListener iListener = new DragTable.IClickListener() {
		
		@Override
		public void onLongClick(int index, int gid, int aid, int type) {
			// TODO Auto-generated method stub
			SKSceneManage.getInstance().time=0;
		}
		
		@Override
		public void onDoubleClick(int index, int gid, int aid, int type) {
			// TODO Auto-generated method stub
			if (!touch||info==null) {
				//不能点击
				return;
			}
			
			SKSceneManage.getInstance().time=0;
			
			if (dTable.getClickRowIndex()< 0 || index < 0 || (index + nCurTop) >= mRecipeLists.size()) {// 点击无效范围，返回
				return;
			}
			if (info.isbShowRecipeID() && dTable.getClickRowIndex() == 1) {//点击ID 那么不能够进行编辑, 返回
				return ;
			}
			mClickPoint.set(dTable.getClickRowIndex(), index + nCurTop);
			
			if (useSmallKey()) {//使用小键盘
				if (null == popKey) {
					popKey = new SKKeyPopupWindow(ContextUtl.getInstance(),true, mRecipeData.getnKeyId(), mRecipeData.geteDataTypeList().get(0));
					popKey.setCallback(call);
				}
				
				if(dTable.getClickRowIndex() == 0){// 点中配方名称
					popKey.setKeyType(false);
					popKey.setInputType(false);
				}
				else {
					popKey.setKeyType(true);
					popKey.setInputType(true);
				}
				
				String[] mm=getMaxAndMin( mRecipeData.geteDataTypeList().get(0));
				Point keyPoint = getKeyboardPos(dTable.getContentRect(dTable.getClickPoint().x, dTable.getClickPoint().y, bLast), popKey.getKeyboardWidth(), popKey.getKeyboardHeigh());
				if (SKKeyPopupWindow.keyFlagIsShow) {
					popKey.setShowMax(mm[1]);
					popKey.setShowMin(mm[0]);
					popKey.setnStartX(keyPoint.x);
					popKey.setnStartY(keyPoint.y);
					popKey.initPopUpWindow();
					popKey.showPopUpWindow();
				}
			}else {//使用系统键盘
				openKey();
			}
			
		}
		
		@Override
		public void onClick(int index, int gid, int aid, int type) {
			// TODO Auto-generated method stub
			SKSceneManage.getInstance().time=0;
		}
	};
	
	private boolean useSmallKey(){
		if (mRecipeData.getnKeyId() > -1) {
			return SKKeyPopupWindow.existKeyBroad(mRecipeData.getnKeyId());
		}
		
		return false;
	}
	
	/**
	 * 获取键盘的左上角坐标
	 * @param clickPoint 点击位置
	 * @param keyW 键盘宽度
	 * @param keyH 键盘高度
	 * @return
	 */
	/**
	private Point getKeyboardPos(Point clickPoint, int keyW, int keyH){
		int space = SKMenuManage.dip2px(ContextUtl.getInstance(), 30);
		int tempX;
		int tempY;
		int xPos = (SKSceneManage.nSceneWidth - keyW) / 2;
		int yPos = (SKSceneManage.nSceneHeight - keyH) / 2;
		Point point = new Point(xPos, yPos);
		if (keyH + clickPoint.y + space <= SKSceneManage.nSceneHeight) {//下方
			tempY = clickPoint.y + space;
			if ( clickPoint.x - keyW/2 < 0) {
				tempX = 0;
			}
			else if (clickPoint.x + keyW/2 > SKSceneManage.nSceneWidth ) {
				tempX = SKSceneManage.nSceneWidth - keyW;
			}
			else {
				tempX = clickPoint.x - keyW /2;
			}
			
			point.set(tempX, tempY);
		}
		else if (clickPoint.y - keyH - space >= 0) { //上方
			tempY = clickPoint.y - keyH -space;
			if ( clickPoint.x - keyW/2 < 0) {
				tempX = 0;
			}
			else if (clickPoint.x + keyW/2 > SKSceneManage.nSceneWidth ) {
				tempX = SKSceneManage.nSceneWidth - keyW;
			}
			else {
				tempX = clickPoint.x - keyW /2;
			}
			
			point.set(tempX, tempY);
		}
		else if (clickPoint.x- keyW - space >= 0) {// 左边
			tempX = clickPoint.x - keyW - space;
			if (clickPoint.y - keyH/2 < 0 ) {
				tempY = 0;
			}
			else if (clickPoint.y + keyH/2 > SKSceneManage.nSceneHeight) {
				tempY = SKSceneManage.nSceneHeight - keyH;
			}
			else {
				tempY  = clickPoint.y - keyH/2;
			}
			
			point.set(tempX, tempY);
		}
		else if (clickPoint.x + keyW + space <= SKSceneManage.nSceneHeight) {// 右边
			tempX = clickPoint.x + space; 
			if (clickPoint.y - keyH/2 < 0 ) {
				tempY = 0;
			}
			else if (clickPoint.y + keyH/2 > SKSceneManage.nSceneHeight) {
				tempY = SKSceneManage.nSceneHeight - keyH;
			}
			else {
				tempY  = clickPoint.y - keyH/2;
			}
			
			point.set(tempX, tempY);
		}
		
		return point;
	}
	**/
	
	/**
	 * 通过点击单元格  获取 小键盘的位置
	 * @param item 被点击的单元格
	 * @param keyW 小键盘的宽度
	 * @param keyH 小键盘的高度
	 * @return
	 */
	public Point getKeyboardPos(Rect item, int keyW, int keyH){
		int space = SKMenuManage.dip2px(ContextUtl.getInstance(), 3);
		Point nPoint = new Point();
		nPoint.x = (SKSceneManage.nSceneWidth - keyW) / 2;
		nPoint.y = (SKSceneManage.nSceneHeight - keyH) / 2;
		
		if (item != null) {
			int startX; //
			int startY;
			if (item.bottom  + keyH + space  <= SKSceneManage.nSceneHeight) {//显示在下方
				startY = item.bottom + space;
				startX = item.centerX() - keyW / 2; //X中心对齐
				if (startX < 0 ) {
					startX = 0;
				}
				else if (startX + keyW > SKSceneManage.nSceneWidth) {
					startX = SKSceneManage.nSceneWidth - keyW;
				}
				
				nPoint.set(startX, startY);
			}
			else if (item.top - keyH - space >= 0) { //显示在上方
				startY = item.top - keyH - space;
				startX = item.centerX() - keyW / 2; // X中心对齐
				if (startX < 0 ) {
					startX = 0;
				}
				else if (startX + keyW > SKSceneManage.nSceneWidth) {
					startX = SKSceneManage.nSceneWidth - keyW;
				}
				
				nPoint.set(startX, startY);
			}
			else if (item.left - keyW - space >= 0) { //显示在左边
				startX = item.left - keyW - space;
				startY = item.centerY() - keyH / 2;
				if (startY < 0 ) {
					startY = 0 ;
				}
				else if (startY + keyH > SKSceneManage.nSceneHeight) {
					startY = SKSceneManage.nSceneHeight - keyH;
				}
				
				nPoint.set(startX, startY);
			}
			else if (item.right + keyW + space <= SKSceneManage.nSceneWidth) {// 右边
				startX = item.right + space;
				startY = item.centerY() - keyH / 2; 
				if (startY < 0) {
					startY = 0;
				}
				else if (startY + keyH > SKSceneManage.nSceneHeight) {
					startY = SKSceneManage.nSceneHeight - keyH;
				}
				
				nPoint.set(startX, startY);
			}
		}
		
		
		return nPoint;
	}
	

	/**
	 * 初始化配方的显示器
	 */
	private Vector<RecipeOprop> mRecipeLists = null;// 配方组
	private Vector<Vector<String>> mTitleNameList = null; // 配方元素
	private Vector<DATA_TYPE > eDataTypeList;//配方元素类型
	/**
	 * @param list-配方元素列表
	 * @param top其始行序号，从0开始
	 */
	private void initTbaleItem() {
		
		int nTemp;
		
		if (mHList==null) {
			mHList=new ArrayList<String>();
		}else {
			mHList.clear();
		}
		
		if (mRowCells==null) {
			mRowCells=new Vector<RowCell>();
		}
		
		if (mRecipeData!=null) {
			mRecipeLists = mRecipeData.getmRecipePropList();
			mTitleNameList = mRecipeData.getsElemNameList();
			eDataTypeList=mRecipeData.geteDataTypeList();
		}else {
			if (mRecipeLists!=null) {
				mRecipeLists.clear();
			}
			if (mTitleNameList!=null) {
				mTitleNameList.clear();
			}
			if (eDataTypeList!=null) {
				eDataTypeList.clear();
			}
		}

		//配方id
		String ID = mContext.getString(R.string.recipe_id);
		//配方描述
	//	String des = mContext.getString(R.string.recipe_des);
		int nAddColum=0;
		
		/**
		 * 初始化行列
		 */
		// 第一行
		String name="配方名称";
		if (info.getmRecipeNames()!=null) {
			if (info.getmRecipeNames().size()>SystemInfo.getCurrentLanguageId()) {
				name=info.getmRecipeNames().get(SystemInfo.getCurrentLanguageId());
			}
		}
		mHList.add(name);
		
		
		//配方Id
		if (info.isbShowRecipeID()) {
			nAddColum++;
			//nDataRank++;
			mHList.add(ID);
		}
		
//		// 显示配方描述
//		if (info.isbShowDescrip()) {
//			nAddColum++;
//			nDataRank++;
//			mHList.add(des);
//		}
		
	//	nDataRank++;//配方名称

		if (nDataRank>nColum) {
			nTemp=nDataRank;
		}else{
			nTemp=nColum;
		}
		
		/**
		 * 第一行，元素列
		 */
		int n=0;
		for (int i = nAddColum; i < nTemp; i++) {
			nAddColum++;
			if (mTitleNameList==null||mTitleNameList.size()==0) {
				mHList.add("");
				n++;
			}else {
				if (n<mTitleNameList.size()) {
					if (mTitleNameList.get(n).size()>SystemInfo.getCurrentLanguageId()) {
						mHList.add(mTitleNameList.get(n).get(SystemInfo.getCurrentLanguageId())+"");
					}else {
						mHList.add("");
					}
					n++;
				}else {
					mHList.add("");
					n++;
				}
			}
		}
	}
	
	private int nCurTop = 0;
	private boolean bCurrent;
	private void initData(ArrayList<String[]> elems,int type,int top,int end){
		bCurrent=false;
		CurrentRecipe cinfo=SystemInfo.getCurrentRecipe();
		if (cinfo!=null) {
			if (info!=null) {
				if (cinfo.getCurrentGroupRecipeId()==info.getnRecipeGroupId()) {
					bCurrent=true;
				}
			}
		}
		
		int index=top-1;
		if (index<0) {
			index=0;
		}
		nCurTop = index;
		
		Vector<RecipeOprop> list=new Vector<RecipeOprop>();
		if (mRecipeLists.size()>index) {
			for (int i = index; i < info.getnRow()+index; i++) {
				if (mRecipeLists.size()>i) {
					list.add(mRecipeLists.get(i));
				}
			}
		}
		mRowCells.clear();
		if (list.size()>0) {
			//行
			int id=top;
			if(id<1){
				id=1;
			}
			for (int i = 0; i < list.size(); i++) {
				RecipeOprop recip=list.get(i);
				String[] items=null;
				if (recip!=null) {
					if (bCurrent&&recip.getnRecipeId()==cinfo.getCurrentRecipeId()) {
						items=RecipeDataCentre.getInstance().getRecipeData(cinfo.getCurrentGroupRecipeId(),
								cinfo.getCurrentRecipeId(), false);
					}else {
						if (elems!=null) {
							if (elems.size()>i) {
								items=elems.get(i);
							}
						}
					}
				}
				addRow(recip,id++,cinfo,items,mRowCells);
			}
		}		

		if(dTable!=null){
			dTable.updateView(1);
			dTable.updateRowIndex(0);
			dTable.initData(mRowCells, mHList, 0);

			if (type==0) {
				
				if (nAllCount <= info.getnRow()){//最顶部
					bFore=true;
					bLast=false;
				}else if (top + info.getnRow() > nAllCount) {//最底部
					bFore=false;
					bLast=true;
				}
				else {
					dTable.gotoRow(top);
					bLast = false;
				}
				dTable.updateDataNum(nAllCount);
			}else {
				
				if (bUntreated) {
					bUntreated = false;
					mLoadInfo.nLoadType=type;
					if (this.nTop>=nAllCount) {
						mLoadInfo.nRowIndex=nAllCount-info.getnRow()+1;
						mLoadInfo.nEndIndex=nAllCount;
					}else {
						mLoadInfo.nRowIndex=this.nTop;
						mLoadInfo.nEndIndex=this.nTop+info.getnRow();
					}
					mLoadInfo.nLoadCount=info.getnRow();
					
					if (sTaskName.equals("")) {
						sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
					}
					SKThread.getInstance().getBinder().onTask(MODULE.RECIPER_COLLECT_NOTIC, TASK.RECIPE_READ, sTaskName);
				}
				
			}
		}
		
		if (bLast&&dTable.isShowBar()) {
			//处于底部，并且滑动块显示
			if (mRowCells.size()>=info.getnRow()) {
				dTable.updateView(2);
			}
			dTable.moveToBottom();
		}
		
		isLoading = false;
        SKSceneManage.getInstance().onRefresh(items);
	}
	

	private void addRow(RecipeOprop recip,int top,CurrentRecipe cinfo,String[] items,Vector<RowCell> rowCells){
	//	boolean convert=false;//显示数据是否需要转换
		Vector<String> mClounm=new Vector<String>();
		RowCell rowCell=new RowCell();
		rowCell.nRowIndex=top;
		rowCell.nClounmCount=nColum;
		rowCell.mClounm=mClounm;
		
		int nColumIndex=0;
		
		String sTitle="";
		//第一列
		if (recip!=null) {
			//配方名称
			if (SystemInfo.getCurrentLanguageId()<recip.getsRecipeName().size()) {
				sTitle=recip.getsRecipeName().get(SystemInfo.getCurrentLanguageId());
			}
			rowCell.mClounm.add(sTitle);
			nColumIndex++;
		}
		if (info.isbShowRecipeID()) {
			//配方id
			if (recip!=null) {
				rowCell.mClounm.add(recip.getnRecipeId()+"");
			}
			nColumIndex++;
		}

		
		int k=0;
		int temp=nColum;
		if (nDataRank>nColum) {
			temp=nDataRank;
		}
		
		for (int j = nColumIndex; j <temp; j++) {
			
//			convert=false;
//			if (eDataTypeList!=null) {
//				int t=j-nColumIndex;
//				if (eDataTypeList.size()>t) {
//					DATA_TYPE type=eDataTypeList.get(t);
//					if (type==DATA_TYPE.BIT_1||type==DATA_TYPE.INT_16||type==DATA_TYPE.INT_32
//							||type==DATA_TYPE.POSITIVE_INT_16||type==DATA_TYPE.POSITIVE_INT_32) {
//						convert=true;
//					}
//				}
//			}

			if (items!=null && items.length>k) {
//				if (convert) {
//					int index=items[k].indexOf(".");
//					if (index>0) {
//						rowCell.mClounm.add(items[k].substring(0,index));
//					}else{
//						rowCell.mClounm.add(items[k]);
//					}
//				}else {
//					if (!isNumeric(items[k])) {
//						BigDecimal decimal=new BigDecimal(Double.valueOf(items[k]));
//						rowCell.mClounm.add(decimal+"");
//					}else {
//						rowCell.mClounm.add(items[k]);
//					}
//				}
				rowCell.mClounm.add(items[k]);
				k++;
			}else {
				rowCell.mClounm.add("");
				k++;
			}
		
			
		}
		rowCells.add(rowCell);
	}
	
	/**
	 * 刷新 绘制
	 */
	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		if (show) {
			if (itemId == nItemId && (info != null)) {
				if (dTable!=null) {
					dTable.draw(canvas);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 退出 回收
	 */
	@Override
	public void realseMemeory() {
		this.flag = false;
		RecipeDataCentre.getInstance().msgDestoryCallback(ICallBackDataList);
		sTaskName="";
		SKThread.getInstance().getBinder().onDestroy(tCallback, sTaskName);
		
		if(deleteDialog!=null){
			deleteDialog.destory();
		}
		if (GlobalPopWindow.popIsShow) {
			if(keyPop!=null){
				keyPop.closePop();
			}
		}
		
		sDialog = null;
		
		if (updateObserver != null) {
			mObservers.remove(updateObserver);
			updateObserver = null;
		}
	}

	@Override
	public boolean isShow() {
		itemShow();
		SKSceneManage.getInstance().onRefresh(items);
		return show;
	}

	/**
	 * 显现
	 */
	private void itemShow() {
		if (showByAddr || showByUser) {
			show = popedomIsShow(info.getmShowInfo());
		}
	}

	/**
	 * 触控
	 */
	private void itemTouch() {
		if (touchByAddr || touchByUser) {
			touch = popedomIsTouch(info.getmTouchInfo());
		}
	}

	@Override
	public boolean isTouch() {
		itemTouch();
		return touch;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		if (!show) {
			return false;
		}
		if (dTable != null) {
			return dTable.onTouchEvent(event);
		}
		return false;
	}

	/**
	 * 注册通知
	 */
	private void register() {
		
		/**
		 * 注册语言通知
		 */
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(lCallback);
		}
		
		
		/**
		 * 注册配方通知
		 */
		RecipeDataCentre.getInstance().msgRegisterUpdate(ICallBackDataList);
	}

	/**
	 * 后台线程回调
	 */
	SKThread.ICallback tCallback = new SKThread.ICallback() {

		@Override
		public void onUpdate(Object msg, int taskId) {

		}

		@Override
		public void onUpdate(int msg, int taskId) {
		    if(TASK.RECIPE_READ==taskId){
		    	if (mRecipeData == null) {
					mRecipeData = RecipeDataCentre.getInstance().getOGRecipeData(
							info.getnRecipeGroupId());
				}
		    	if (mRecipeData==null) {
		    		isLoading=false;
					return;
				}

		    	
	    		if(mLoadInfo.nLoadType==0){
		    		
		    		nAllCount=RecipeDataCentre.getInstance().getRecipeDataCount(info.getnRecipeGroupId());
		    		mRecipeGroup.clear();
		    		mRecipeGroup = RecipeDataCentre.getInstance().getRecipeGroup(info.getnRecipeGroupId());
		    		if (isFirstInit) {
						isFirstInit = false;
						
						if (nAllCount>info.getnRow()) {
							mLoadInfo.nRowIndex=nAllCount-info.getnRow()+1;
						}else {
							mLoadInfo.nRowIndex=1;
						}
						mLoadInfo.nEndIndex=nAllCount;
					}else{
		    			//
		    			mLoadInfo.nRowIndex = nCurTop + 1;
		    			mLoadInfo.nEndIndex = mLoadInfo.nRowIndex + info.getnRow();
		    			if (nAllCount <= info.getnRow()) {//配方数目 小于
							mLoadInfo.nRowIndex = 1;
							mLoadInfo.nEndIndex = nAllCount;
							
						}else if (mLoadInfo.nEndIndex > nAllCount) {//配方只有部分显示的情况
							mLoadInfo.nRowIndex = nAllCount - info.getnRow() + 1;
							mLoadInfo.nEndIndex = nAllCount;
						}
					}
		    		
		    		
		    	}
	    		ArrayList<String[]>  list = getShowRecipes(mLoadInfo.nRowIndex -1, mLoadInfo.nLoadCount);
		    	
		    	if (mLoadInfo.nLoadType==0) {
		    		initTbaleItem();
				}
		    	
				initData(list, mLoadInfo.nLoadType, mLoadInfo.nRowIndex, mLoadInfo.nEndIndex);
			
		    	
		    	
			}
		}

		@Override
		public void onUpdate(String msg, int taskId) {

		}
	};
	
	/**
	 * 
	 * @param startIndex--开始位置
	 * @param count  --  配方数目
	 * @return 指定返回的配方数据
	 */
	private ArrayList<String[]> getShowRecipes(int startIndex, int count){
		ArrayList<String[]> list = new ArrayList<String[]>();
		startIndex = Math.max(0, startIndex);
		int endIndex = startIndex + count;
		endIndex = Math.min(endIndex, mRecipeGroup.size());
		
		for(int i = startIndex; i < endIndex; i++){
			list.add(mRecipeGroup.get(i));
		}	
		return list;
	}
	
	/**
	 * 更新缓存中的配方数据
	 * @param index
	 * @param items
	 */
	private void updateRecipeList(int index , String[]items){
		if (index < mRecipeGroup.size() && index > -1) {
			mRecipeGroup.set(index, items);
		}
	}

	/**
	 * 语言切换
	 */
	SKLanguage.ICallback lCallback=new SKLanguage.ICallback() {
		
		@Override
		public void onLanguageChange(int languageId) {
			mLoadInfo.nLoadType=0;
			mLoadInfo.nLoadCount=info.getnRow();
			
			if (sTaskName.equals("")) {
				sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.RECIPER_COLLECT_NOTIC, TASK.RECIPE_READ, sTaskName);
		}
	};
	
	/**
	 * 触摸地址回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isTouch();
		}
	};

	/**
	 * 显隐地址回调
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			isShow();
		}
	};

	
	/**
	 * 配方更新
	 */
	private IRecipeCallBack ICallBackDataList = new IRecipeCallBack() {

		@Override
		public void update() {
			if (info==null||!flag) {
				return;
			}
			if (isLoading) {
				return;
			}
			isLoading=true;
			mLoadInfo.nLoadType=0;
			mLoadInfo.nLoadCount=info.getnRow();
			
			if (sTaskName.equals("")) {
				sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
			}
			SKThread.getInstance().getBinder().onTask(MODULE.RECIPER_COLLECT_NOTIC, TASK.RECIPE_READ, sTaskName);
		}

		@Override
		public void currRecipeUpdate() {
			
		}
	};

	
	/**
	 * 删除
	 */
	DeleteDialog.IDeleteListener deleteListener=new DeleteDialog.IDeleteListener() {
		
		@Override
		public void onDelete(int id) {
			
		}
	};
	
	/**
	 * 翻页
	 */
	DragTable.IPageTurning iTurning=new DragTable.IPageTurning() {
		
		@Override
		public void onUpdate(int len) {

		}
		
		@Override
		public void onPre(int page) {

		}
		
		@Override
		public void onNext(int page) {

		}

		@Override
		public void onLoad(int top, int type,int site) {
			dataCenter(top,type,site);
		}
	};
	
	/**
	 * 拖动处理中心
	 */
	private boolean bLast=false;
	private boolean bFore=false;
	private void dataCenter(int top,int type,int site){
		//Log.d(TAG, "top:"+top+",type:"+type+",site:"+site+",isLoading:"+isLoading);
		
		if (isLoading) {
			bUntreated=true;
			this.nTop=top;
			return;
		}
		
		if (type==1||type==3) {
			if (bFore) {
				//Log.d(TAG, "已经处于最前面...");
				return;
			}else {
				if (site==0) {
					//处于最顶部
					bFore=true;
					bLast=false;
				}else{
					if (site==1) {
						//处于中间
						bLast=false;
					}
					bFore=false;
				}
			}
		}
		
		if (type==2||type==4) {
			if (bLast) {
				//Log.d(TAG, "已经处于最后面...");
				return;
			}else {
				if(site==2){
					//处于底部
					bLast=true;
					bFore=false;
				}else {
					if (site==1) {
						//处于中间
						bFore=false;
					}
					bLast=false;
				}
			}
		}
		
		isLoading=true;
		if (bLast) {
			if (nAllCount>info.getnRow()) {
				top=nAllCount-info.getnRow()+1;
			}
		}
		
		//Log.d(TAG, ">>>>>>>type:"+type+",top:"+top);
		mLoadInfo.nLoadType=type;
		mLoadInfo.nRowIndex=top;
		mLoadInfo.nEndIndex=top+info.getnRow();
		mLoadInfo.nLoadCount=info.getnRow();
		
		if (sTaskName.equals("")) {
			sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
		}
		SKThread.getInstance().getBinder().onTask(MODULE.RECIPER_COLLECT_NOTIC, TASK.RECIPE_READ, sTaskName);
	}
	
	/**
	 * 根据 recipeID 获取 recipe 在队里中的位置 （位置从1开始）
	 * @param recipeid
	 * @return
	 */
	private int getRecipePosById(int recipeid){
		int num = 1;
		for(int i =0 ; i < mRecipeLists.size(); i++){
			RecipeOprop reGprop = mRecipeLists.get(i);
			if (recipeid != reGprop.getnRecipeId()) {
				num ++;
			}
			else {
				break;
			}
		}
		return  num;
	}
	
	//监听当前选中的配方，然后进行跳转
	class UpdateObserver implements Observer{

		@Override
		public void update(Observable observable, final Object data) {
			// TODO Auto-generated method stub
			if (SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId() == info.getnRecipeGroupId()) {
				
				Runnable updateRunnable = new Runnable() {
					public void run() {
						int cur = getRecipePosById(SystemInfo.getCurrentRecipe().getCurrentRecipeId());
						int goId = (Integer) data;
						if (goId > -1) {//编辑配方 goID > -1
							//cur = goId + 1;
							cur = getRecipePosById(goId);
						}
						
						if (info.getnRow()  == 1) {
							dataCenter(cur,1,1);
						}
						else {
							
							if(isLoading){
								return;
							}
							isLoading = true;
						
							bLast=false;
							bFore=false;
							if ( cur == mRecipeLists.size() && mRecipeLists.size() > 1) {//如果是最后一个配方 且配方的数目不为0
								bLast = true;
							}
							mLoadInfo.nLoadType = 1;
							dTable.gotoRow(cur);
							mLoadInfo.nRowIndex= Math.max(0, cur -1);
							mLoadInfo.nEndIndex= mLoadInfo.nRowIndex + info.getnRow();
							mLoadInfo.nLoadCount=info.getnRow();
						
							if (sTaskName.equals("")) {
								sTaskName=SKThread.getInstance().getBinder().onRegister(tCallback);
							}
							SKThread.getInstance().getBinder().onTask(MODULE.RECIPER_COLLECT_NOTIC, TASK.RECIPE_READ, sTaskName);
						}
						
					}
				};
				
				//进行延迟，防止刷新冲突
				mHandler.postDelayed(updateRunnable, 800);
		}
		}
		
	}
	
	private Handler mHandler=new Handler();
	
	/**
	 * 是否是数字
	 */
	public boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$"); 
	    return pattern.matcher(str).matches();    
	} 
	
	class DataInfo{
		public int id;         //id-配方表里面的列id
		public String name;    //列名称
		public boolean isMust; //是否必须填写
		public String tag;     //字段标示
	}
	
	
	SKKeyPopupWindow.ICallback call=new SKKeyPopupWindow.ICallback(){

		@Override
		public void onResult(String result, KEYBOARD_OPERATION type) {
			if ( result==null) {
				return;
			}
			if (type == KEYBOARD_OPERATION.ENTER) {
//				if (mList.size()>nListIndex) {
					//mList.get(nListIndex).setText(result);
//					nListIndex=-1;
//				}
				updateRecipeData(result);
			}
		}
		
	};
	
	private String[] getMaxAndMin(DATA_TYPE dataType){
		String[] mm=new String[]{0+"",0+""};
		switch (dataType) {
		case INT_16:
			mm[0]="-32768";
			mm[1]="32767";
			break;
		case POSITIVE_INT_16:
			mm[0]="0";
			mm[1]="65535";
			break;
		case INT_32:
			mm[0]="-2147483648";
			mm[1]="2147483647";
			break;
		case POSITIVE_INT_32:
			mm[0]="0";
			mm[1]="4294967295";
			break;
		case FLOAT_32:
			mm[0]="-2147483648";
			mm[1]="2147483647";
			break;
		case BCD_16:
			mm[0]="0";
			mm[1]="9999";
			break;
		case BCD_32:
			mm[0]="0";
			mm[1]="99999999";
			break;
		case BIT_1:
			mm[0]="0";
			mm[1]="1";
			break;
		}
		return mm;
	}
	
	/**
	 * 根据用户的输入更新配方数据  
	 * @param newDate
	 */
	/**
	 * 1、刷新优化
	 * 2、更新界面
	 * 3、检查输入是否合法
	 * @param newData
	 */
	private void updateRecipeData(String newData){
		//如果更新字符串为空 那么就返回
		if (TextUtils.isEmpty(newData) || TextUtils.isEmpty(newData.trim())) {
			return;
		}
		String updateData = newData.trim();
		
		//获取配方
		if (mRecipeLists.size() > mClickPoint.y) {//
			RecipeOprop recipe = mRecipeLists.get(mClickPoint.y);
			
			if (mClickPoint.x == 0) {//修改   配方名称
				Vector<String> nameList = recipe.getsRecipeName();
				if (nameList.size() > SystemInfo.getCurrentLanguageId()) {
					nameList.set(SystemInfo.getCurrentLanguageId(), updateData);
					String[] items = RecipeDataCentre.getInstance().getRecipeData(info.getnRecipeGroupId(), recipe.getnRecipeId(), false);
					saveRecipe(recipe, items);
				}
			}
			else {
				if (info.isbShowRecipeID() && mClickPoint.x == 1) {//修改 配方ID
					if (!isLegalID(updateData, recipe)) {
						return ;
					}
					
					String[] items = RecipeDataCentre.getInstance().getRecipeData(info.getnRecipeGroupId(), recipe.getnRecipeId(), false);
					recipe.setnRecipeId(Integer.valueOf(updateData));
					saveRecipe(recipe, items);
				}
				else//修改配方元素
				{
					int recipeIndex = mClickPoint.x -1 ; //- 配方名称
					if (info.isbShowRecipeID()) {
						recipeIndex -= 1; //- ID;
					}
					
					if (!isLegalElement(updateData, recipeIndex)) {//不合法输入
						SKToast.makeText(mContext.getString(R.string.input_errors), Toast.LENGTH_SHORT).show();
						return;
					}
					
//					CurrentRecipe cInfo=new CurrentRecipe();
//					cInfo.setCurrentGroupRecipeId(info.getnRecipeGroupId());
//					cInfo.setCurrentRecipeId(recipe.getnRecipeId());
					
					updateData = RecipeDataCentre.subZeroAndDot(updateData);
					String[] items = RecipeDataCentre.getInstance().getRecipeData(info.getnRecipeGroupId(), recipe.getnRecipeId(), false);
					items[recipeIndex] = updateData; 
					updateRecipeList(mClickPoint.y, items);  //更新缓存数据
					saveRecipe(recipe, items);               //更新数据库数据
//					EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
//					eInfo.mRecipeData=recipe;
//					eInfo.mRecipeInfo=cInfo;
//					eInfo.sValueList=items;
//					RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
				}
			}
		}
		
		// 操作通知
		if (info!=null) {
			if (info.getmTouchInfo()!=null) {
				noticeAddr(info.getmTouchInfo(), true);
			}
		}
	}
	
	
	/**
	 * ---保存配方数据
	 * @param recipe -保存的配方
	 * @param items  -保存的元素
	 */
	private void saveRecipe(RecipeOprop recipe , String[]items){
		CurrentRecipe cInfo=new CurrentRecipe();
		cInfo.setCurrentGroupRecipeId(info.getnRecipeGroupId());
		cInfo.setCurrentRecipeId(recipe.getnRecipeId());
		
		EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
		eInfo.mRecipeData=recipe;
		eInfo.mRecipeInfo=cInfo;
		eInfo.sValueList=items;
		RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
	}
	
	
	
	
	/**
	 * 检测 id是否有效
	 * @param id - 用户输入的ID
	 * @param recipe - 当前修改的配方
	 * @return 有效合法的id，返回true，否则返回false
	 */
	private boolean isLegalID(String id , RecipeOprop recipe){
		
		if (id.equals(String.valueOf(recipe.getnRecipeId()))) {//没有修改 - 返回false
			return false;
		}

		if (!isNumeric(id)) { //不是数字   - 返回false
			SKToast.makeText(ContextUtl.getInstance(),mContext.getString(R.string.recipe_prompt)+", ID "
					+mContext.getString(R.string.recipe_prompt_num), Toast.LENGTH_SHORT).show();
			return false;
		}
		else
		{
			int temp=Integer.valueOf(id);
			boolean bid=DBTool.getInstance().getmRecipeDataBiz().existRecipeID(info.getnRecipeGroupId()+"", id+"");
			if (bid) {// 数字已存在 --返回false
				SKToast.makeText(mContext,"ID:"+id+","+mContext.getString(R.string.recipe_exists), Toast.LENGTH_SHORT).show();
				return false;
			}
			else
			{
				if (temp<0||temp>32767){//数字超出范围 --返回false
					SKToast.makeText(mContext,"ID:"+id+","+mContext.getString(R.string.out_of_range), Toast.LENGTH_SHORT).show();
					return false;
				}
				return true;
			}
		
		}
	}
	
	/**
	 * 检测value值是否有效
	 * @param value --修改值
	 * @param index --位置
	 * @return
	 */
	private boolean isLegalElement(String value, int index){
		
		if (!isNumeric(value)) {// 不是数字 --返回
			return false;
		}else{
			double temp = Double.valueOf(value);;
			DATA_TYPE type=eDataTypeList.get(index);
			switch (type) {
				case INT_16:
					if (!isInt(value+"",mContext.getString(R.string.integer))) {
						return false;
					}
					if (temp<-32768||temp>32767) {
						SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case POSITIVE_INT_16:
					if (!isPosInt(value+"",mContext.getString(R.string.positive_integer))) {
						return false;
					}
					if (temp<0||temp>65535) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case INT_32:
					if (!isInt(value+"",mContext.getString(R.string.integer))) {
						return false;
					}
					if (temp<-2147483648||temp>2147483647) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case POSITIVE_INT_32:
					if (!isPosInt(value+"",mContext.getString(R.string.positive_integer))) {
						return false;
					}
					if (temp<0||temp>4294967295L) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case FLOAT_32:
					if (temp<-2147483648||temp>2147483647) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case BCD_16:
					if (!isInt(value+"","BCD")) {
						return false;
					}
					if (temp<0||temp>9999) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case BCD_32:
					if (!isInt(value+"","BCD")) {
						return false;
					}
					if (temp<0||temp>99999999) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				case BIT_1:
					if (!isInt(value+"",mContext.getString(R.string.bit))) {
						return false;
					}
					if (temp<0||temp>1) {
						SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
						return false;
					}
					break;
				default:
					return false;
			
			}
		}
		return  true ;
	}
	
	
	/**
	 * 是否是正整数
	 */
	public boolean isPosInt(String str,String msg){ 
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet=false;
		Pattern pattern = Pattern.compile("[0-9]*");
		resulet=pattern.matcher(str).matches();
		if (!resulet) {
			SKToast.makeText(mContext,  mContext.getString(R.string.enter)
					+msg
					+mContext.getString(R.string.type), Toast.LENGTH_SHORT).show();
		}
		return resulet;
	}
	
	
	/**
	 * 是否是整数
	 */
	public boolean isInt(String str,String msg){ 
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet=false;
		Pattern pattern = Pattern.compile("[-_0-9]*");
		resulet=pattern.matcher(str).matches();
		if (!resulet) {
			SKToast.makeText(mContext, mContext.getString(R.string.enter)
					+msg
					+mContext.getString(R.string.type), Toast.LENGTH_SHORT).show();
		}
		return resulet;
	}
	
	
	/**
	 * 系统键盘
	 */
	private void openKey() {
		if (systemKey == null) {
			systemKey = new GlobalPopWindow(SKSceneManage.getInstance().getCurrentScene(), WINDOW_TYPE.KEYBOARD, 2,
					DATA_TYPE.ASCII_STRING);// 1 代表数值
		}

		systemKey.setCallback(keyback);
		systemKey.initPopupWindow();
		if (null != systemKey) {
			String[] mm=getMaxAndMin( mRecipeData.geteDataTypeList().get(0));
			systemKey.setInputMax(mm[1]);
			systemKey.setInputMin(mm[0]);
			// 允许输入 并且勾选了输入提示 则更换背景
			drawBack = true;
			SKSceneManage.getInstance().onRefresh(items);
			
			systemKey.showPopupWindow();
		}
	
	}

	/**
	 * 系统键盘回调函数
	 */
	GlobalPopWindow.ICallBack keyback = new GlobalPopWindow.ICallBack() {

		@Override
		public void inputFinish(String result) {
			// TODO Auto-generated method stub

			updateRecipeData(result);
		}

		// 当关闭popWindow 时 是否 启动定时器
		@Override
		public void onStart() {
			// 关闭键盘 把背景去掉
			if (drawBack) {
				drawBack = false;
				SKSceneManage.getInstance().onRefresh(items);
			}
			SKSceneManage.getInstance().timeOut();
		}

		// 窗口显示的时候，判断定时器是否打开 如果打开则销毁
		@Override
		public void onShow() {
			// TODO Auto-generated method stub
			if (SKTimer.getInstance().getBinder()
					.isRegister(SKSceneManage.getInstance().sCallback)) {
				SKTimer.getInstance().getBinder()
						.onDestroy(SKSceneManage.getInstance().sCallback);
			}
		}
	};
	// 点击输入画输入框提示框
	private boolean drawBack = false;

	
	/**
	 * 控件对外接口
	 */
	
	public IItem getIItem(){
		return this;
	}
	
	@Override
	public int getItemLeft(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return info.getnStartPosX();
		}
		return -1;
	}

	@Override
	public int getItemTop(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return info.getnStartPosY();
		}
		return -1;
	}

	@Override
	public int getItemWidth(int id) {
		// TODO 自动生成的方法存根
		if(info!=null){
			return info.getnWidth();
		}
		return -1;
	}

	@Override
	public int getItemHeight(int id) {
		// TODO 自动生成的方法存根
		if(info!=null){
			return info.getnHeight();
		}
		return -1;
	}

	@Override
	public short[] getItemForecolor(int id) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public short[] getItemBackcolor(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return getColor(info.getnTextBackColor());
		}
		return null;
	}

	@Override
	public short[] getItemLineColor(int id) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			return getColor(info.getnLineColor());
		}
		return null;
	}

	@Override
	public boolean getItemVisible(int id) {
		// TODO 自动生成的方法存根
		return show;
	}

	@Override
	public boolean getItemTouchable(int id) {
		// TODO 自动生成的方法存根
		return touch;
	}

	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if (x<0||x>SKSceneManage.getInstance().getCurrentScene().nSceneWidth) {
				return false;
			}
			if (x==info.getnStartPosX()) {
				return true;
			}
			info.setnStartPosX((short)x);
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			dTable.resetLeftTopX(x);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean setItemTop(int id, int y) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if (y<0||y>SKSceneManage.getInstance().getCurrentScene().nSceneHeight) {
				return false;
			}
			if (y==info.getnStartPosY()) {
				return true;
			}
			info.setnStartPosY((short)y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			dTable.resetLeftTopY(y);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if(w<0||w>SKSceneManage.getInstance().getCurrentScene().nSceneWidth){
				return false;
			}
			if(w==info.getnWidth()){
				return true;
			}
			info.setnWidth((short)w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect=new Rect();
			dTable.resetWidth(w);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			if (h<0||h>SKSceneManage.getInstance().getCurrentScene().nSceneHeight) {
				return false;
			}
			if(h==info.getnHeight()){
				return true;
			}
			info.setnHeight((short)h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect=new Rect();
			dTable.resetHeigth(h);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemForecolor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemBackcolor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			int color=Color.rgb(r, g, b);
			if (info.getnTextBackColor()==color) {
				info.setnTextBackColor(color);
			}
			dTable.resetBackcolor(color);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO 自动生成的方法存根
		if (info!=null) {
			int color=Color.rgb(r, g, b);
			if (info.getnLineColor()==color) {
				info.setnLineColor(color);
			}
			dTable.resetLinecolor(color);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO 自动生成的方法存根
		if (v==show) {
			return true;
		}
		show=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO 自动生成的方法存根
		if (v==touch) {
			return true;
		}
		touch=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemPageUp(int id) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			dTable.turnPage(0);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemPageDown(int id) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			dTable.turnPage(1);
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemFlick(int id, boolean v, int time) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemHroll(int id, int w) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			int type=0;
			if (w<0) {
				type=1;
			}
			dTable.moveRank(type,Math.abs(w));
			return true;
		}
		return false;
	}

	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO 自动生成的方法存根
		if (dTable!=null) {
			int type=0;
			if(h<0){
				type=1;
			}
			dTable.moveRow(type, Math.abs(h));
			return true;
		}
		return false;
	}

	@Override
	public boolean setGifRun(int id, boolean v) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemText(int id, int lid, String text) {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	public boolean setItemAlpha(int id, int alpha) {
		// TODO 自动生成的方法存根
		if (info==null||alpha<0||alpha>255) {
			return false;
		}
		if (info.getnAlpha()==alpha) {
			return true;
		}
		info.setnAlpha((short)alpha);
		dTable.resetAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}

	@Override
	public boolean setItemStyle(int id, int style) {
		// TODO 自动生成的方法存根
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
