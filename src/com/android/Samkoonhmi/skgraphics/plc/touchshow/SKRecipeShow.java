package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.DragTable;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.RecipeShowInfo;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableLoadInfo;
import com.android.Samkoonhmi.model.skglobalcmn.RecipeDataProp;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.IRecipeCallBack;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.DeleteDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
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
public class SKRecipeShow extends SKGraphCmnTouch {

	private static final String TAG = "SKRecipeShow";
	// 表格内容
	private boolean flag;
	private int nSceneId;
	private int nItemId;
	private String sTaskName;
	private Context mContext;
	private RecipeShowInfo info;
	private SKItems items;
	private boolean show; // 是否可显现
	private boolean touch; // 是否可触控
	private boolean showByAddr; // 是否注册显现地址
	private boolean touchByAddr; // 是否注册触控地址
	private boolean showByUser; // 是否受用户权限控件
	private boolean touchByUser; // 是否受用户权限控件
	private DragTable dTable;
	private RecipeDataProp.recipeOGprop mRecipeData;
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

	public SKRecipeShow(Context context, int sId, int itemId,RecipeShowInfo info) {
		this.mContext = context;
		this.nSceneId = sId;
		this.nItemId = itemId;
		this.flag = true;
		sTaskName = "";
		this.info=info;
		mLoadInfo=new TableLoadInfo();
		
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
		this.show = true;
		this.touch = true;
		this.showByAddr = false;
		this.touchByAddr = false;
		this.showByUser = false;
		this.touchByUser = false;
		this.isLoading=false;
		this.bUntreated=false;
		
		
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
			dTable.setiPageTurning(iTurning);
			dTable.init(null);
			
			dTable.initData(mRowCells, mHList,0);
			dTable.drawTable();
		}
		
		flag=true;
		mLoadInfo.nLoadType=0;
		mLoadInfo.nLoadCount=info.getnRow();
		
		// 注册通知
		register();
		
		itemShow();
		itemTouch();
		
		SKSceneManage.getInstance().onRefresh(items);
		
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
		String des = mContext.getString(R.string.recipe_des);
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
			nDataRank++;
			mHList.add(ID);
		}
		
		// 显示配方描述
		if (info.isbShowDescrip()) {
			nAddColum++;
			nDataRank++;
			mHList.add(des);
		}
		
		nDataRank++;//配方名称

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
				
				if (nAllCount>(info.getnRow()-1)){
					bFore=false;
					bLast=true;
				}else{
					bFore=true;
					bLast=false;
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
		boolean convert=false;//显示数据是否需要转换
		Vector<String> mClounm=new Vector<String>();
		RowCell rowCell=new RowCell();
		rowCell.nRowIndex=top;
		rowCell.nClounmCount=nColum;
		rowCell.mClounm=mClounm;
		rowCell.nRowIndex=top;
		
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
		if (info.isbShowDescrip()) {
			//配方描述
			if (recip!=null) {
				if (SystemInfo.getCurrentLanguageId()<recip.getsRecipeDescri().size()) {
					sTitle=recip.getsRecipeDescri().get(SystemInfo.getCurrentLanguageId());
				}
			}
			rowCell.mClounm.add(sTitle);
			nColumIndex++;
		}
		
		int k=0;
		int temp=nColum;
		if (nDataRank>nColum) {
			temp=nDataRank;
		}
		
		for (int j = nColumIndex; j <temp; j++) {
			
			convert=false;
			if (eDataTypeList!=null) {
				int t=j-nColumIndex;
				if (eDataTypeList.size()>t) {
					DATA_TYPE type=eDataTypeList.get(t);
					if (type==DATA_TYPE.BIT_1||type==DATA_TYPE.INT_16||type==DATA_TYPE.INT_32
							||type==DATA_TYPE.POSITIVE_INT_16||type==DATA_TYPE.POSITIVE_INT_32) {
						convert=true;
					}
				}
			}
			
			if(items!=null){
				if (items.length>k) {
					if (convert) {
						if (items[k]!=null) {
							int index=items[k].indexOf(".");
							if (index>0) {
								rowCell.mClounm.add(items[k].substring(0,index));
							}else{
								rowCell.mClounm.add(items[k]);
							}
						}else {
							rowCell.mClounm.add("");
						}
					}else {
						if (!isNumeric(items[k])) {
							BigDecimal decimal=new BigDecimal(Double.valueOf(items[k]));
							rowCell.mClounm.add(decimal+"");
						}else {
							rowCell.mClounm.add(items[k]);
						}
					}
					k++;
				}else {
					rowCell.mClounm.add("");
					k++;
				}
			}else{
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
		SKPlcNoticThread.getInstance().destoryCallback(showCall);
		SKPlcNoticThread.getInstance().destoryCallback(touchCall);
		if(deleteDialog!=null){
			deleteDialog.destory();
		}
		if (GlobalPopWindow.popIsShow) {
			if(keyPop!=null){
				keyPop.closePop();
			}
		}
	}

	@Override
	public boolean isShow() {
		itemShow();
		if (flag) {
			SKSceneManage.getInstance().onRefresh(items);
		}
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
		if (!show||!touch) {
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
						true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmShowInfo().getShowAddrProp(), showCall,
						false);
			}

		}

		// 注册触控地址
		if (touchByAddr) {
			ADDRTYPE addrType = info.getmTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
						touchCall, true);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(info.getmTouchInfo().getTouchAddrProp(),
						touchCall, false);
			}
		}
		
		/**
		 * 注册语言通知
		 */
		SKLanguage.getInstance().getBinder().onRegister(lCallback);
		
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
		    		
		    		if (nAllCount>info.getnRow()) {
						mLoadInfo.nRowIndex=nAllCount-info.getnRow()+1;
					}else {
						mLoadInfo.nRowIndex=1;
					}
					mLoadInfo.nEndIndex=nAllCount;
		    	}
		    	
		    	ArrayList<String[]>  list=RecipeDataCentre.getInstance().getRecipeGroup(info.getnRecipeGroupId(),mLoadInfo.nRowIndex,mLoadInfo.nLoadCount);
		    	
		    	if (mLoadInfo.nLoadType==0) {
		    		initTbaleItem();
				}
		    	
		    	//Log.d(TAG, "load........");
				initData(list, mLoadInfo.nLoadType, mLoadInfo.nRowIndex, mLoadInfo.nEndIndex);
			}
		}

		@Override
		public void onUpdate(String msg, int taskId) {

		}
	};

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

}
