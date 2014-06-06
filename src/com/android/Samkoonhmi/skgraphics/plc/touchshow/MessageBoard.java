package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.graphicsdrawframe.DragTable;
import com.android.Samkoonhmi.model.DragTableInfo;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.MessageBoardInfo;
import com.android.Samkoonhmi.model.MessageDetailInfo;
import com.android.Samkoonhmi.model.RowCell;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.TableLoadInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.MessageDialog;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.util.DateStringUtil;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.SKLanguage;
import com.android.Samkoonhmi.util.TASK;
import com.android.Samkoonhmi.util.TextAlignUtil;

/**
 * 留言板
 * 
 * @author 瞿丽平
 * 
 */
public class MessageBoard extends SKGraphCmnTouch implements IItem {
	private static final String TAG = "MessageBoard";
	private Paint mPaint;
	private MessageBoardInfo info;
	private Rect mRect;
	private SKItems items;
	private int itemId;
	private int sceneId;
	private String sTaskName;
	private boolean initFlag;
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean showByUser;
	private boolean touchByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private ArrayList<String> mHList;
	private Vector<RowCell> mRowCells;
	private Context myContext;
	// 正在加载中
	private boolean isLoading;
	// 总共的条数
	private int nAllCount;
	// 起始行
	private int nTop;
	// 表格
	private DragTable dTable;
	// 表格内容
	private int mRankCount;
	// 有未加载的任务
	private boolean bUntreated;
	private TableLoadInfo mLoadInfo;// 加载数据信息包
	private Bitmap mLockBitmap;

	public MessageBoard(Context context, int itemId, int sceneId,
			MessageBoardInfo info) {
		this.myContext = context;
		initFlag = true;
		isTouchFlag = true;
		isShowFlag = true;
		showByUser = false;
		touchByUser = false;
		showByAddr = false;
		touchByAddr = false;
		this.sceneId = sceneId;
		this.itemId = itemId;
		this.sTaskName = "";
		mPaint = new Paint();
		items = new SKItems();
		mLoadInfo = new TableLoadInfo();
		this.info = info;
		
		if (info!=null) {
			
			mRect = new Rect();
			mRect.left = info.getnStartX();
			mRect.right = info.getnStartX() + info.getnWidth();
			mRect.top = info.getnStartY();
			mRect.bottom = info.getnStartY() + info.getnHeight();

			items.itemId = this.itemId;
			items.nCollidindId = info.getnCollidindId();
			items.nZvalue = info.getnZvalue();
			items.sceneId = sceneId;
			items.rect = mRect;
			items.mGraphics=this;
			
			mHList = new ArrayList<String>();
			mRowCells = new Vector<RowCell>();
			
			if (null != info.getTouchInfo()) {
				if (-1 != info.getTouchInfo().getnAddrId()
						&& info.getTouchInfo().isbTouchByAddr()) {
					touchByAddr = true;
				}
				if (info.getTouchInfo().isbTouchByUser()) {
					touchByUser = true;
				}
			}
			if (null != info.getShowInfo()) {
				if (-1 != info.getShowInfo().getnAddrId()
						&& info.getShowInfo().isbShowByAddr()) {
					showByAddr = true;
				}
				if (info.getShowInfo().isbShowByUser()) {
					showByUser = true;
				}
			}
			
			// 注册触控地址值
			if (touchByAddr && null != info.getTouchInfo().getTouchAddrProp()) {
				ADDRTYPE addrType = info.getTouchInfo().geteCtlAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance()
							.addNoticProp(info.getTouchInfo().getTouchAddrProp(),
									touchCall, true,sceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getTouchInfo().getTouchAddrProp(), touchCall,
							false,sceneId);
				}
			}
			
			// 注册显现地址值
			if (showByAddr && null != info.getShowInfo().getShowAddrProp()) {
				ADDRTYPE addrType = info.getShowInfo().geteAddrType();
				if (addrType == ADDRTYPE.BITADDR) {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getShowInfo().getShowAddrProp(), showCall, true,sceneId);
				} else {
					SKPlcNoticThread.getInstance().addNoticProp(
							info.getShowInfo().getShowAddrProp(), showCall, false,sceneId);
				}

			}

		}
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		if (null == info) {
			return;
		}
		initFlag = true;
		
		// 初始化表头
		initTableItem();

		// 初始化表格样式
		if (null == dTable) {
			DragTableInfo dInfo = new DragTableInfo();
			dInfo.setnAlpha(info.getnAlpha());
			dInfo.setnFrameColor(info.getnLineColor()); // 设置表格边框颜色
			dInfo.setnLineColor(info.getnInnerLineColor());

			dInfo.setnTitleFontColor(info.getnTFontColor());// 设置表头字体颜色
			dInfo.setnTitleFontSize((short) info.getnTFontSize());// 设置表头字体大小
			dInfo.setnTitleBackcolor(info.getnTBackColor());// 设置表头背景颜色
			Typeface titleTypeFace = TextAlignUtil.getTypeFace(info
					.getsTFontType());
			dInfo.setmHTypeFace(titleTypeFace);// 设置表头字体类型

			dInfo.setnTextFontColor(info.getnFontColor());// 设置正文字体颜色
			dInfo.setnTextFontSize((short) info.getnFontSize());// 设置正文字体大小
			Typeface contentTypeFace = TextAlignUtil.getTypeFace(info
					.getsFontType());
			dInfo.setmTypeFace(contentTypeFace);// 设置正文字体类型
			dInfo.setnTableBackcolor(info.getnBackColor());// 设置正文背景色

			dInfo.setnVTitleBackcolor(info.getnBackColor());
			dInfo.setnVTitleFontColor(info.getnFontColor());
			dInfo.setnVTitleFontSize((short) info.getnFontSize());

			dInfo.setnRow((short) (info.getnRowCount() + 1));
			dInfo.setnRank((short) mRankCount);
			dInfo.setnWidth((short) info.getnWidth());
			dInfo.setnHeight((short) info.getnHeight());
			dInfo.setmRowHeight(info.getmRowHeight());
			dInfo.setmRowWidth(info.getmRowWidht());
			dInfo.setnLeftTopX((short) info.getnStartX());
			dInfo.setnLeftTopY((short) info.getnStartY());
			dInfo.setnPageIndex(1);
			dInfo.setmAlign(TEXT_PIC_ALIGN.CENTER);
			dTable = new DragTable(dInfo, myContext, items, true);
			dTable.setiClickListener(iListener);
			dTable.setiPageTurning(iTurning);
			dTable.init(null);
			dTable.initData(mRowCells, mHList, 0);
		}

		// 注册清除留言信息的接口
		SystemVariable.getInstance().setClearCall(clearCall);

		// 注册地址
		registAddr();
		messageBoardIsShow();
		messageBoardIsTouch();
		SKSceneManage.getInstance().onRefresh(items);

		mLoadInfo.nLoadType = 0;
		mLoadInfo.nLoadCount = info.getnRowCount();

		if (sTaskName.equals("")) {
			sTaskName = SKThread.getInstance().getBinder()
					.onRegister(tCallback);
		}
		// 发送读取控件数据命令
		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.NUMBER_INPUT_SHOW, TASK.READMESSAGEBOARD,
						sTaskName);
	}

	/**
	 * 表格处理点击事件
	 */
	private MessageDialog dialog;
	private int mIndex = -1;
	DragTable.IClickListener iListener = new DragTable.IClickListener() {

		@Override
		public void onLongClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time = 0;

		}

		@Override
		public void onDoubleClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time = 0;
			mIndex = index;
			if (dialog == null || !dialog.show) {
				dialog = new MessageDialog(SKSceneManage.getInstance()
						.getActivity());
				if (index >= 0) {
					RowCell rowCell = mRowCells.elementAt(index);
					dialog.setRowCell(rowCell);
				}
				dialog.setMessageCount(nAllCount);
				dialog.setMessageBoardId(info.getnItemId());
				dialog.showDialog();
				dialog.setImessageRefresh(onMessageRefresh);
				dialog.setCanceledOnTouchOutside(false);
			}

		}

		@Override
		public void onClick(int index, int gid, int aid, int type) {
			SKSceneManage.getInstance().time = 0;

		}

	};

	/**
	 * 翻页
	 */
	DragTable.IPageTurning iTurning = new DragTable.IPageTurning() {

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
		public void onLoad(int top, int type, int site) {
			dataCenter(top, type, site);
		}
	};

	/**
	 * 拖动处理中心
	 */
	private boolean bLast = false;
	private boolean bFore = false;

	private void dataCenter(int top, int type, int site) {

		// Log.d(TAG,
		// "......top:"+top+",type:"+type+",site:"+site+",isLoading:"+isLoading);
		if (isLoading) {
			bUntreated = true;
			this.nTop = top;
			return;
		}

		if (type == 1 || type == 3) {
			if (bFore) {
				// Log.d(TAG, "已经处于最前面...");
				return;
			} else {
				if (site == 0) {
					// 处于最顶部
					bFore = true;
					bLast = false;
				} else {
					if (site == 1) {
						// 处于中间
						bLast = false;
					}
					bFore = false;
				}
			}
		}

		if (type == 2 || type == 4) {
			if (bLast) {
				// Log.d(TAG, "已经处于最后面...");
				return;
			} else {
				if (site == 2) {
					// 处于底部
					bLast = true;
					bFore = false;
				} else {
					if (site == 1) {
						// 处于中间
						bFore = false;
					}
					bLast = false;
				}
			}
		}

		isLoading = true;
		if (bLast) {
			if (nAllCount > info.getnRowCount()) {
				top = nAllCount - info.getnRowCount() + 1;
			}
		}

		// Log.d(TAG, "...........type:"+type+",top:"+top);
		mLoadInfo.nLoadType = type;
		mLoadInfo.nRowIndex = top;
		mLoadInfo.nEndIndex = top + info.getnRowCount();
		mLoadInfo.nLoadCount = info.getnRowCount();

		if (sTaskName.equals("")) {
			sTaskName = SKThread.getInstance().getBinder()
					.onRegister(tCallback);
		}

		SKThread.getInstance()
				.getBinder()
				.onTask(MODULE.NUMBER_INPUT_SHOW, TASK.READMESSAGEBOARD,
						sTaskName);
	}

	MessageDialog.IMessageRefresh onMessageRefresh = new MessageDialog.IMessageRefresh() {

		@Override
		public void onRefresh(MessageDetailInfo detailInfo, int i) {
			// TODO Auto-generated method stub
			if (i == 1) {
				// 新增成功
				nAllCount++;
				nTop = 1;
				mLoadInfo.nLoadType = 0;

			} else if (i == 2) {
				// 修改
				nTop = 1;
				mLoadInfo.nLoadType = 0;
			} else if (i == 3) {
				// 删除成功
				nAllCount--;
				if (dTable != null) {
					dTable.updateDataNum(nAllCount);
					if (nAllCount <= 0) {
						mRowCells.clear();
						dTable.updateRowIndex(0);
						dTable.updateData(mRowCells);
						dTable.updateView(1);
						dTable.moveToTop();
						SKSceneManage.getInstance().onRefresh(items);
						return;
					}
				}
				mLoadInfo.nLoadType = 1;
			}

			isLoading = true;
			mLoadInfo.nRowIndex = nTop;
			mLoadInfo.nLoadCount = info.getnRowCount();

			SKThread.getInstance()
					.getBinder()
					.onTask(MODULE.NUMBER_INPUT_SHOW, TASK.READMESSAGEBOARD,
							sTaskName);

		}

	};

	/**
	 * 初始化表头
	 */
	private void initTableItem() {

		mHList.clear();

		mRankCount = 0;
		if (info.isbShowId()) {
			mRankCount++;
			mHList.add(info.getsNumberNameList().get(
					SystemInfo.getCurrentLanguageId()));
		}
		if (info.isbShowTime()) {
			mRankCount++;
			mHList.add(info.getsTimeNameList().get(
					SystemInfo.getCurrentLanguageId()));
		}

		if (info.isbShowDate()) {
			mRankCount++;
			mHList.add(info.getsDateNameList().get(
					SystemInfo.getCurrentLanguageId()));
		}

		mRankCount++;
		mHList.add(info.getsMessageInfoList().get(
				SystemInfo.getCurrentLanguageId()));

	}

	/**
	 * 初始化表格数据
	 * 
	 * @param list
	 *            -留言信息列表
	 * @param type
	 *            -加载类型
	 * @param top
	 *            -起始行，从0开始
	 * @param end
	 *            -
	 */
	Vector<RowCell> tempRows = new Vector<RowCell>();

	private void initData(List<MessageDetailInfo> list, int type, int top,
			int end) {
		// 数据查询
		mRowCells.clear();
		if (list != null && list.size() > 0) {
			int id = top;
			if (id < 1) {
				id = 1;
			}
			for (int i = 0; i < list.size(); i++) {
				MessageDetailInfo detMessage = list.get(i);
				addRow(detMessage, id++, mRowCells);
			}
		}

		if (dTable != null) {

			dTable.updateView(1);
			dTable.updateRowIndex(0);
			dTable.updateData(mRowCells);

			if (type == 0) {
				bFore = true;
				bLast = false;
				dTable.updateDataNum(nAllCount);
				dTable.moveToTop();
			} else {

				if (bLast && dTable.isShowBar()) {
					// 处于底部，并且滑动块显示
					if (mRowCells.size() >= info.getnRowCount()) {
						dTable.updateView(2);
					}
					dTable.moveToBottom();
				}

				if (bUntreated) {
					bUntreated = false;
					mLoadInfo.nLoadType = type;
					if (this.nTop >= nAllCount) {
						mLoadInfo.nRowIndex = nAllCount - info.getnRowCount()
								+ 1;
						mLoadInfo.nEndIndex = nAllCount;
					} else {
						mLoadInfo.nRowIndex = this.nTop;
						mLoadInfo.nEndIndex = this.nTop + info.getnRowCount();
					}
					mLoadInfo.nLoadCount = info.getnRowCount();
					if (sTaskName.equals("")) {
						sTaskName = SKThread.getInstance().getBinder()
								.onRegister(tCallback);
					}
					SKThread.getInstance()
							.getBinder()
							.onTask(MODULE.NUMBER_INPUT_SHOW,
									TASK.READMESSAGEBOARD, sTaskName);
				}

			}

			isLoading = false;
			SKSceneManage.getInstance().onRefresh(items);
		}

	}

	private void addRow(MessageDetailInfo detMessage, int top,
			Vector<RowCell> rowCells) {

		Vector<String> mClounms = new Vector<String>();
		RowCell rowCell = new RowCell();
		rowCell.nRowIndex = top;
		rowCell.aid = detMessage.getnId();
		rowCell.nClounmCount = mRankCount;
		rowCell.mClounm = mClounms;
		if (info.isbShowId()) {
			mClounms.add(top + "");
		}
		if (info.isbShowTime()) {
			String time = DateStringUtil.converTime(info.getnTimeType(),
					new Date(detMessage.getnTime()));
			mClounms.add(time);
		}
		if (info.isbShowDate()) {
			String date = DateStringUtil.convertDate(info.getnDateType(),
					new Date(detMessage.getnTime()));
			mClounms.add(date);
		}
		mClounms.add(detMessage.getsMessage());
		rowCells.add(rowCell);

	}

	public void addrNoticStatus(double nStatus) {

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time = 0;
		if (isTouchFlag && isShowFlag) {

			if (dTable != null) {
				return dTable.onTouchEvent(event);
			}
		}
		return false;
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub
		messageBoardIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;

	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		messageBoardIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
		return isTouchFlag;
	}

	private void messageBoardIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(info.getTouchInfo());
		}
	}

	private void messageBoardIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	@Override
	public void initGraphics() {

		init();
	}

	/**
	 * 注册地址值
	 */
	private void registAddr() {
		
		// 注册多语言切换接口
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(languageICallback);
		}
		
	}

	/**
	 * 多语言切换通知刷新
	 */
	SKLanguage.ICallback languageICallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			// TODO Auto-generated method stub
			initTableItem();
			if (dTable != null) {
				dTable.updateTitle(mHList);
				SKSceneManage.getInstance().onRefresh(items);
			}

		}
	};
	/**
	 * 触控地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isTouch();
		}

	};
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShow();
		}

	};

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {
		// TODO Auto-generated method stub
		if (null == info) {
			return false;
		}
		if (this.itemId == itemId) {
			if (isShowFlag) {
				draw(mPaint, canvas);
			}
			initFlag = true;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 具体的画法
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void draw(Paint paint, Canvas canvas) {
		// 画表格
		dTable.draw(canvas);
		// 不可触控加上锁图标
		if (!isTouchFlag && SystemInfo.isbLockIcon()) {

			if (mLockBitmap == null) {
				if (SKSceneManage.getInstance().mContext != null) {
					mLockBitmap = ImageFileTool.getBitmap(R.drawable.lock,
							SKSceneManage.getInstance().mContext);
				}

			}
			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getnStartX(),
						info.getnStartY(), null);
			}
		}

	}

	@Override
	public void realseMemeory() {
		if (SystemInfo.getLanguageNumber()>1) {
			SKThread.getInstance().getBinder().onDestroy(tCallback, sTaskName);
		}
		sTaskName = "";
	}

	/**
	 * 后台线程
	 */
	SKThread.ICallback tCallback = new SKThread.ICallback() {

		@Override
		public void onUpdate(Object msg, int taskId) {

		}

		@Override
		public void onUpdate(int msg, int taskId) {
			if (taskId == TASK.READMESSAGEBOARD) {

				// Log.d(TAG, "load......");
				if (nAllCount == 0 || mLoadInfo.nLoadType == 0) {
					nAllCount = DBTool.getInstance().getMessageBoard()
							.getDataCount(items.itemId);
				}

				if (mLoadInfo.nLoadType == 0) {
					mLoadInfo.nRowIndex = 1;
					mLoadInfo.nEndIndex = info.getnRowCount();
				}

				List<MessageDetailInfo> list = DBTool
						.getInstance()
						.getMessageBoard()
						.getMessageList(info.getnItemId(), mLoadInfo.nRowIndex,
								mLoadInfo.nLoadCount);
				initData(list, mLoadInfo.nLoadType, mLoadInfo.nRowIndex,
						mLoadInfo.nEndIndex);
			}
		}

		@Override
		public void onUpdate(String msg, int taskId) {

		}
	};

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {

	}

	// 清空留言消息
	SystemVariable.ICallBack clearCall = new SystemVariable.ICallBack() {

		@Override
		public void clearMessage(boolean reuslt) {
			// TODO Auto-generated method stub
			if (reuslt) {
				mRowCells.clear();
				nAllCount = 0;
				if (null != dTable) {
					dTable.updateDataNum(nAllCount);
					dTable.moveToTop();
					dTable.updateData(mRowCells);
					SKSceneManage.getInstance().onRefresh(items);
				}
				SKToast.makeText(myContext, R.string.clr_boardsuccess,
						Toast.LENGTH_SHORT).show();
			} else {
				SKToast.makeText(myContext, R.string.clr_boardfail,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

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
		if (info!=null) {
			return info.getnStartX();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnStartY();
		}
		return -1;
	}


	@Override
	public int getItemWidth(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnWidth();
		}
		return -1;
	}


	@Override
	public int getItemHeight(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnHeight();
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
		//nCurrentState;
		if (info!=null) {
			return getColor(info.getnBackColor());
		}
		return null;
	}
	

	@Override
	public short[] getItemLineColor(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return getColor(info.getnLineColor());
		}
		return null;
	}


	@Override
	public boolean getItemVisible(int id) {
		// TODO Auto-generated method stub
		return isShowFlag;
	}


	@Override
	public boolean getItemTouchable(int id) {
		// TODO Auto-generated method stub
		return isTouchFlag;
	}


	@Override
	public boolean setItemLeft(int id, int x) {
		// TODO Auto-generated method stub
		
		if (info != null) {
			if (x == info.getnStartX()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnStartX(x);
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			dTable.resetLeftTopX(x);
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemTop(int id, int y) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (y == info.getnStartY()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnStartY(y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			dTable.resetLeftTopY(y);
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemWidth(int id, int w) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (w == info.getnWidth()) {
				return true;
			}
			if (w < 0|| w > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnWidth((short)w);
			items.rect.right = w - items.rect.width() + items.rect.right;
			items.mMoveRect=new Rect();
			dTable.resetWidth(w);
			SKSceneManage.getInstance().onRefresh(items);
		} else {
			return false;
		}
		return true;
	}


	@Override
	public boolean setItemHeight(int id, int h) {
		// TODO Auto-generated method stub
		if (info != null) {
			if (h == info.getnHeight()) {
				return true;
			}
			if (h < 0|| h > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnHeight((short)h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect=new Rect();
			dTable.resetHeigth(h);
			SKSceneManage.getInstance().onRefresh(items);
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
		if (info!=null) {
			int color=Color.rgb(r, g, b);
			if (color==info.getnBackColor()) {
				return true;
			}
			info.setnBackColor(color);
			dTable.resetBackcolor(color);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
		if (info!=null) {
			int color=Color.rgb(r, g, b);
			if (color==info.getnLineColor()) {
				return true;
			}
			info.setnLineColor(color);
			dTable.resetLinecolor(color);
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemVisible(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==isShowFlag) {
			return true;
		}
		isShowFlag=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}


	@Override
	public boolean setItemTouchable(int id, boolean v) {
		// TODO Auto-generated method stub
		if (v==isTouchFlag) {
			return true;
		}
		isTouchFlag=v;
		SKSceneManage.getInstance().onRefresh(items);
		return true;
	}


	@Override
	public boolean setItemPageUp(int id) {
		// TODO Auto-generated method stub
		if (dTable!=null) {
			dTable.turnPage(0);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemPageDown(int id) {
		// TODO Auto-generated method stub
		if (dTable!=null) {
			dTable.turnPage(1);
			return true;
		}
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
		if (dTable!=null) {
			int type=0;
			if (w<0) {
				type=1;
			}
			dTable.moveRank(type,w);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemVroll(int id, int h) {
		// TODO Auto-generated method stub
		if (dTable!=null) {
			int type=0;
			if(h<0){
				type=1;
			}
			dTable.moveRow(type, h);
			return true;
		}
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
		if (info==null||alpha<0||alpha>255) {
			return false;
		}
		if (info.getnAlpha()==alpha) {
			return true;
		}
		info.setnAlpha(alpha);
		dTable.resetAlpha(alpha);
		SKSceneManage.getInstance().onRefresh(items);
		return true;
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
