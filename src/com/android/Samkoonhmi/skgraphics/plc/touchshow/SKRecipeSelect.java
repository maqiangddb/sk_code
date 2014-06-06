//import SKGraphCmnTouch;
package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.RecipeSelectAdapter;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.graphicsdrawframe.FoldLineItem;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.macro.MacroManager;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.IItem;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.RecipeSelectInfo;
import com.android.Samkoonhmi.model.SKItems;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.plccommunicate.SKPlcNoticThread;
import com.android.Samkoonhmi.skenum.ADDRTYPE;
import com.android.Samkoonhmi.skenum.CSS_TYPE;
import com.android.Samkoonhmi.skenum.RECIPESELECT_TYPE;
import com.android.Samkoonhmi.skenum.TEXT_PIC_ALIGN;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.IRecipeCallBack;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.base.SKGraphCmnTouch;
import com.android.Samkoonhmi.skwindow.RecipectSearchWindow;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.ImageFileTool;
import com.android.Samkoonhmi.util.MSERV;
import com.android.Samkoonhmi.util.SKLanguage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * 配方选择器
 * 
 * @author 瞿丽平
 * 
 */
public class SKRecipeSelect extends SKGraphCmnTouch implements IItem {
	private RecipeSelectInfo info;
	private boolean flag = true;
	private Paint mPaint;
	private Rect rectBoder;// 矩形边框
	private Rect rectBox;// 显示下拉三角形的矩形
	private int rect2Width = 20;// 显示下拉三角形的矩形宽度
	private Vector<String> showFirstName = null;
	private Vector<Point> pointList;
	private int screenHeight = 0;// 屏幕的高度
	private PopupWindow mPopupWindow; // 显示的下拉窗口
	private LayoutInflater mInflater;
	private View cView; // 下拉窗口的布局
	private int boxHeight = 200;// 下拉触控的窗口的高度
	private List<RecipeOprop> dataList = null;// 显示数据集合
	private List<RecipeOprop> tempDataList = null;// 上拉下拉的显示临时集合
	private ListView listView;// 下拉列表
	private EditText searchText;// 搜索框
	private RecipeSelectAdapter adapter;
	public boolean popIsShow; // 窗口是否弹出
	private RectF rectBoderList;
	private float lineHeight = 0;
	private Rect myRect;
	private StaticTextModel text;
	private TextItem textItem;
	private RectItem rectItems;
	private FoldLineItem foldLineItem;
	private SKItems items;
	private int itemId;
	private int sceneId;
	private int selectIndex = -1;
	private boolean initFlag;
	private boolean isTouchFlag;
	private boolean isShowFlag;
	private boolean touchByUser;
	private boolean showByUser;
	private boolean showByAddr;
	private boolean touchByAddr;
	private SystemInfoBiz sysBiz;
	private boolean isOnClick;
	private myMainHandler hand = null;
	private Bitmap mLockBitmap;

	public SKRecipeSelect(int itemId, int sceneId, RecipeSelectInfo info) {
		isOnClick = false;
		popIsShow = false;
		this.itemId = itemId;
		this.sceneId = sceneId;
		isTouchFlag = true;
		isShowFlag = true;
		touchByUser = false;
		showByUser = false;
		showByAddr = false;
		touchByAddr = false;
		this.initFlag = true;
		mPaint = new Paint();
		rectBoderList = new RectF();
		items = new SKItems();
		sysBiz = new SystemInfoBiz();
		this.info = info;
		setState();
		
		// 触控权限地址
		if (null != info.getTouchInfo()) {
			if (info.getTouchInfo().getTouchAddrProp() != null) {
				touchByAddr = true;
			}
			if (info.getTouchInfo().isbTouchByUser()) {
				touchByUser = true;
			}
		}
		// 显现权限地址
		if (null != info.getShowInfo()) {
			if (info.getShowInfo().getShowAddrProp() != null) {
				showByAddr = true;
			}
			if (info.getShowInfo().isbShowByUser()) {
				showByUser = true;
			}
		}
				
		if (touchByAddr) {
			// Log.d("plc", "SKRecipeSelect 注册触控通知");
			ADDRTYPE addrType = info.getTouchInfo().geteCtlAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getTouchInfo().getTouchAddrProp(), touchCall,
						true, sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getTouchInfo().getTouchAddrProp(), touchCall,
						false,sceneId);
			}
		}
		
		if (showByAddr) {
			// Log.d("plc", "SKRecipeSelect 注册显现通知");''
			ADDRTYPE addrType = info.getShowInfo().geteAddrType();
			if (addrType == ADDRTYPE.BITADDR) {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, true,
						sceneId);
			} else {
				SKPlcNoticThread.getInstance().addNoticProp(
						info.getShowInfo().getShowAddrProp(), showCall, false,
						sceneId);
			}

		}

	}

	private void init() {
		if (null == info) {
			return;
		}
		// boxHeight = 200;
		if (SKSceneManage.getInstance().getCurrentInfo() != null) {
			screenHeight = SKSceneManage.getInstance().getCurrentInfo()
					.getnSceneHeight();
		}

		initFlag = true;

		// 注册地址接口
		registAddr();
		recipeSelectIsShow();
		recipeSelectIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
	}

	/**
	 * 初始化状态
	 */
	private void setState() {
		// 刷新的控件矩形
		myRect = new Rect(info.getnStartPosX(), info.getnStartPosY(),
				info.getnStartPosX() + info.getnWidth(), info.getnStartPosY()
						+ info.getnHeight());

		rect2Width = myRect.width() * 6 / 27;
		// 画带字矩形边框
		rectBoder = new Rect();
		rectBoder.left = info.getnStartPosX();
		rectBoder.right = info.getnStartPosX() + info.getnWidth() - rect2Width;
		rectBoder.top = info.getnStartPosY();
		rectBoder.bottom = info.getnStartPosY() + info.getnHeight();

		// 文本属性
		text = new StaticTextModel();
		text.setM_eTextAlign(TEXT_PIC_ALIGN.LEFT);
		text.setM_nFontColor(info.getnTextColor());
		text.setM_nFontSize(info.getnFontSize());
		text.setM_backColorPadding(info.getnBackColor());
		text.setStartX(info.getnStartPosX());
		text.setStartY(info.getnStartPosY());
		if (info.geteShowType() == RECIPESELECT_TYPE.COMBOX) {
			text.setStartX(info.getnStartPosX()+1);
			text.setStartY(info.getnStartPosY()+1);
			text.setRectHeight(rectBoder.height()-2);
			text.setRectWidth(rectBoder.width()-1);
		} else {
			text.setRectWidth(myRect.width()-1);
		} 
		if (info.geteShowType() == RECIPESELECT_TYPE.ARRAYLIST) {
			text.setLineColor(Color.BLACK);
			text.setLineWidth(1);
		} else {
			text.setLineColor(Color.rgb(183, 211, 252));
			text.setLineWidth(1);
		}
		text.setM_sFontFamly(info.getsFontFamily());
		text.setM_alphaPadding(info.getnTransparent());
		text.setBorderAlpha(255);

		// 文本
		textItem = new TextItem(text);
		textItem.initTextPaint();
		textItem.initRectBoderPaint();
		textItem.initRectPaint();

		// 显示下拉三角的矩形
		rectBox = new Rect();
		rectBox.left = rectBoder.right;
		rectBox.right = rectBoder.right + rect2Width;
		rectBox.top = rectBoder.top ;
		rectBox.bottom = rectBoder.bottom ;

		// 显示下拉三角的矩形对象
		rectItems = new RectItem(rectBox);
		rectItems.setLineColor(Color.rgb(183, 211, 252));
		rectItems.setStyle(CSS_TYPE.CSS_SOLIDCOLOR);
		rectItems.setBackColor(Color.rgb(183, 211, 252));
		rectItems.setLineWidth(1);
		rectItems.setAlpha(info.getnTransparent());

		// 下拉三角的点集合
		int leftPX = rectBox.left + rect2Width * 1 / 4;
		int leftPY = myRect.top + myRect.height() * 7 / 18;
		int rightPX = rectBox.left + rect2Width * 3 / 4;
		int rightPY = leftPY;
		int buttomX = rectBox.left + rect2Width / 2;
		int buttomY = rectBox.top + rectBox.height() * 11 / 18;
		pointList = new Vector<Point>();
		pointList.add(new Point(leftPX, leftPY));
		pointList.add(new Point(buttomX, buttomY));
		pointList.add(new Point(rightPX, rightPY));

		// 画下拉三角的折线对象
		foldLineItem = new FoldLineItem(pointList);
		foldLineItem.setLineColor(Color.rgb(77, 97, 133));
		foldLineItem.setLineWidth(2);

		items.itemId = this.itemId;
		items.nCollidindId = info.getnCollidindId();
		items.nZvalue = info.getnZvalue();
		items.sceneId = sceneId;
		items.rect = myRect;
		items.mGraphics = this;
	}

	private boolean reflesh = false;
	private IRecipeCallBack ICallBackDataList = new IRecipeCallBack() {

		@Override
		public void update() {
			// TODO Auto-generated method stub
			// 配方数据集合
			// if (null == dataList) {
			reflesh = false;
			dataList = new ArrayList<RecipeOprop>();
			RecipeOGprop oGprop = RecipeDataCentre.getInstance()
					.getOGRecipeData(info.getsShowRecipeId());
			if (null != oGprop) {
				dataList = oGprop.getmRecipePropList();
			}
			
			if (null != dataList) {
				//Log.d("RecipeSelect", "size="+dataList.size());
				if (dataList.size() > 0) {
					// 系统当前配方
					CurrentRecipe current = SystemInfo.getCurrentRecipe();
					RecipeOprop reci = null;
					for (int i = 0; i < dataList.size(); i++) {
						RecipeOprop reci2 = dataList.get(i);
						if (reci2.getnRecipeId() == current
								.getCurrentRecipeId()
								&& info.getsShowRecipeId() == current
										.getCurrentGroupRecipeId()) {
							reci = reci2;
							selectIndex = i;
							firstIndex = i;
							break;
						}
					}
					if (reci == null) {
						selectIndex = -1;
					}
					Vector<String> names = null;
					if (null != reci) {
						names = reci.getsRecipeName();
					}
					if (null != names) {
						if (!names.isEmpty()) {
							showFirstName = names;
						} else {
							showFirstName = null;
						}
					} else {
						showFirstName = null;
					}

				} else {
					showFirstName = null;
				}

			} else {
				showFirstName = null;
			}
			// 如果是列表形式，初始化显示的集合
			if (RECIPESELECT_TYPE.ARRAYLIST == info.geteShowType()) {
				setTempDataList(dataList);
			}
			reflesh = true;
			SKSceneManage.getInstance().onRefresh(items);
		}

		/* 修改当前配方 */
		/**
		 * 当前配方被修改了
		 * 
		 */
		@Override
		public void currRecipeUpdate() {
			// TODO Auto-generated method stub
//			showFirstName = null;
			if (null != dataList) {
				if (!dataList.isEmpty()) {
					for (int i = 0; i < dataList.size(); i++) {
						RecipeOprop reci = dataList.get(i);
						if (null != reci) {
							if (SystemInfo.getCurrentRecipe()
									.getCurrentRecipeId() == reci
									.getnRecipeId()
									&& info.getsShowRecipeId() == SystemInfo
											.getCurrentRecipe()
											.getCurrentGroupRecipeId()) {
								boolean b = false;
								// 判断一下该配方是否属于临时显示的集合中，如果属于则不去修改第一个显示的值，否则让当前配方显示在第一行
								if (tempDataList != null) {
									for (int k = 0; k < tempDataList.size(); k++) {
										RecipeOprop recitemp = tempDataList
												.get(k);
										if (reci.getnRecipeId() == recitemp
												.getnRecipeId()) {
											b = true;
											break;
										}
									}
								}
								if (!b) {
									firstIndex = i;
								}
								showFirstName = reci.getsRecipeName();
							}
						}

					}
				}
			}

			setTempDataList(dataList);
			SKSceneManage.getInstance().onRefresh(items);
		}
	};

	/**
	 * 初始化列表显示的集合
	 * 
	 * @param dataList
	 */
	private void setTempDataList(List<RecipeOprop> dataList) {
		if (null != dataList) {
			if (!dataList.isEmpty()) {
				tempDataList = new ArrayList<RecipeOprop>();
				int j = 0;
				if (info.getnCurrShowRow() > dataList.size() - firstIndex) {
					int k = dataList.size() - firstIndex;
					firstIndex = firstIndex - (info.getnCurrShowRow() - k);
					if (firstIndex < 0) {
						firstIndex = 0;
					}
				}
				for (int i = firstIndex; i < dataList.size(); i++) {
					if (j < info.getnCurrShowRow()) {
						tempDataList.add(dataList.get(i));
					}
					j++;

				}
			}
		}
	}

	public void addrNoticStatus(double nStatus) {
		// TODO put your implementation here.
	}

	private int downY = 0;
	private int upY = 0;
	private int firstIndex = 0;// 第一行数据的索引
	int touchX = 0;
	int touchY = 0;
	private int delayTime = 0; // 操作延长时间

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time = 0;
		// TODO Auto-generated method stub
		boolean touch = false;
		touchX = (int) event.getX();
		touchY = (int) event.getY();
		if (null == info) {
			return false;
		}

		if (touchX < info.getnStartPosX()
				|| touchX > info.getnStartPosX() + info.getnWidth()
				|| touchY > info.getnStartPosY() + info.getnHeight()
				|| touchY < info.getnStartPosY()) {
			if (popIsShow) {
				mPopupWindow.dismiss();
				popIsShow = false;
				return true;
			} else {
				return false;
			}

		} else {

			if (!isTouchFlag || !isShowFlag) {
				if (!isTouchFlag && info != null) {
					if (info.getTouchInfo() != null) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							if (info.getTouchInfo().isbTouchByUser()) {
								SKSceneManage.getInstance().turnToLoginPop();
							}
						}
					}
				}
				return false;
			}

			if (!reflesh) {
				touch = true;
				SKToast.makeText(SKSceneManage.getInstance().mContext,
						R.string.loaddata, Toast.LENGTH_LONG).show();
			} else {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					boolean doubleClick = onDoublicClick(touchX, touchY, event);
					if (!doubleClick) {
						downY = touchY;
						isOnClick = true;
						// 当配方选择器的类型是列表时 延长2s才去触发选择当前配方
						if (info.geteShowType() == RECIPESELECT_TYPE.ARRAYLIST) {
							if (null == info.getTouchInfo()) {
								delayTime = 2000;
							} else {
								if (info.getTouchInfo().isbTimeoutCancel() == true
										&& info.getTouchInfo().getnPressTime() > 0) {
									delayTime = 2000 + info.getTouchInfo()
											.getnPressTime() * 100;
								} else {
									delayTime = 2000;
								}
							}
						} else {
							// 下拉框
							if (null == info.getTouchInfo()) {
								delayTime = 0;
							} else {
								if (info.getTouchInfo().isbTimeoutCancel() == true
										&& info.getTouchInfo().getnPressTime() > 0) {
									delayTime = info.getTouchInfo()
											.getnPressTime() * 100 + 2000;
								} else {
									delayTime = 0 + 2000;
								}
							}
						}
						if (delayTime > 0) {
							if (null == hand) {
								hand = new myMainHandler(Looper.getMainLooper());
							}
							hand.sendEmptyMessageDelayed(TOUCHHANDLER,
									delayTime);
						} else {
							doTouch();
						}

					}

					touch = true;
				} else if (MotionEvent.ACTION_UP == event.getAction()
						|| MotionEvent.ACTION_CANCEL == event.getAction()) {
					isOnClick = false;
					upY = (int) event.getY();
					touch = true;

				} else if (MotionEvent.ACTION_MOVE == event.getAction()) {

					upY = (int) event.getY();
					int distanceY = upY - downY;

					int absDistance = Math.abs(distanceY);// 距离的绝对值
					int j = 0;
					if (null != dataList) {
						j = dataList.size() - info.getnCurrShowRow();
					}
					if (distanceY > 0 && absDistance >= lineHeight / 2) { // 说明是往下滑动,，并且滚动的距离大于等于行高，数据往上走
						isOnClick = false;
						if (firstIndex > 0) {
							firstIndex = firstIndex - 1;
						} else {
							firstIndex = 0;
						}
						downY = upY;
						setTempDataList(dataList);// 重新设置显示值

						SKSceneManage.getInstance().onRefresh(items);
					} else if (distanceY < 0 && absDistance >= lineHeight / 2) {// 说明是往上滑动，并且滚动的距离大于等于行高
						isOnClick = false;
						if (j > 0 && firstIndex < j) {
							firstIndex = firstIndex + 1;
							downY = upY;
							setTempDataList(dataList);// 重新设置显示值
							SKSceneManage.getInstance().onRefresh(items);
						}
					}

					touch = true;
				}
			}
		}

		return touch;
	}

	private static final int TOUCHHANDLER = 1;

	private class myMainHandler extends Handler {
		public myMainHandler(Looper loop) {
			super(loop);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == TOUCHHANDLER) {
				if (isOnClick) {
					doTouch();
				}
			}
		}
	}

	private int tempLine = 0;
	private long nClickTime = 0;
	private int nClickCount = 0;
	private RecipectSearchWindow searchWindow;

	private boolean onDoublicClick(int x, int y, MotionEvent event) {
		if (info.geteShowType() == RECIPESELECT_TYPE.COMBOX) {
			// 不处理下拉框样式的双击弹出搜索框
			return false;
		}
		boolean result = false;
		boolean valid = false;
		// 画行背景
		if ((System.currentTimeMillis() - nClickTime) < 600) {
			if ((System.currentTimeMillis() - nClickTime) > 30) {
				valid = true;
				result = false;
			}
		}

		nClickTime = System.currentTimeMillis();

		if (info.geteShowType() == RECIPESELECT_TYPE.ARRAYLIST) {
			// 获取点击的行数
			if (0 != lineHeight) {
				int i = (touchY - info.getnStartPosY()) / (int) lineHeight
						+ firstIndex;
				if (tempLine != i) {
					// 第一次点击这一行
					nClickCount = 1;
					tempLine = i;
				} else {
					nClickCount++;
					if (nClickCount >= 2) {
						if (valid) {
							searchWindow = new RecipectSearchWindow(dataList,
									info.getsShowRecipeId());
							searchWindow.initPopupWindow();
							searchWindow.showPopupWindow();
							nClickCount = 0;
							result = true;
						} else {
							// 点击了两次 但是时间不在双击的时间范围内
							nClickCount = 1;
						}
					}
				}
			}
		} else {
			nClickCount++;
			if (nClickCount >= 2) {
				if (valid) {
					searchWindow = new RecipectSearchWindow(dataList,
							info.getsShowRecipeId());
					searchWindow.initPopupWindow();
					searchWindow.showPopupWindow();
					nClickCount = 0;
					result = true;
				} else {
					// 点击了两次 但是时间不在双击的时间范围内
					nClickCount = 1;
				}
			}
		}

		return result;
	}

	private void doTouch() {
		// 如果是下拉框
		if (RECIPESELECT_TYPE.COMBOX == info.geteShowType()) {
			if (null == mPopupWindow) {
				initPopupWindow();
			}
			showPopupWindow();
		} else {
			// 如果是点击列表
			if (0 != lineHeight) {
				int i = (touchY - info.getnStartPosY()) / (int) lineHeight
						+ firstIndex;
				selectIndex = i;
				if (null != dataList && !dataList.isEmpty()) {
					if (i >= 0 && i < dataList.size()) {
						RecipeOprop reci = dataList.get(i);
						myUpdateCurrentRecipe(reci);// 改变当前系统配方
					}
				}
				SKSceneManage.getInstance().onRefresh(items);
				
				// 选择完，执行操作通知
				noticeAddr(info.getTouchInfo(), true);
				if (true == info.isbUseMacro()) {
					// 请求执行控件宏指令
					MacroManager.getInstance(null).Request(MSERV.CALLCM,
							(short) info.getnMacroId());
				}
				//发送广播
				notifiSelect();

			}
		}
	}

	/**
	 * 修改系统当前配方
	 * 
	 * @param reci
	 */
	private void myUpdateCurrentRecipe(RecipeOprop reci) {
		int sysRecipeId = SystemInfo.getCurrentRecipe().getCurrentRecipeId();// 系统当前配方id
		int sysRecipeGropId = SystemInfo.getCurrentRecipe()
				.getCurrentGroupRecipeId();// 系统当前配方组Id
		if (null != reci) {
			// 如果选择的配方组id或者配方Id不与系统当前的配方相同 则改变系统当前配方 防止点击重复的多次写入数据库
			if (sysRecipeId != reci.getnRecipeId()
					|| sysRecipeGropId != info.getsShowRecipeId()) {
				CurrentRecipe currentRecipe = new CurrentRecipe(
						reci.getnRecipeId(), info.getsShowRecipeId());
				// 改变当前配方
				// SystemInfo.setCurrentRecipe(currentRecipe);
				RecipeDataCentre.getInstance().setCurrRecipe(
						info.getsShowRecipeId(), reci.getnRecipeId());
				// 把当前配方写入数据库
				sysBiz.updateCurrentRecipe(currentRecipe);

			}
		}
	}

	/**
	 * 初始化窗口内容
	 */

	private void initPopupWindow() {
		if (null != dataList) {
			if (!dataList.isEmpty()) {
				if (info.getnCurrShowRow() < dataList.size()) {
					// length = info.getnCurrShowRow();
					boxHeight = info.getnCurrShowRow() * 40;
				} else {
					boxHeight = dataList.size() * 40;
				}
			} else {
				boxHeight = info.getnCurrShowRow() * 40;
			}

		} else {
			boxHeight = info.getnCurrShowRow() * 40;
		}
		if (boxHeight > 200) {
			boxHeight = 200;
		}
		SHOW_TYPE showType = SHOW_TYPE.DEFAULT;
		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			showType = SKSceneManage.getInstance().getCurrentInfo().geteType();
		}

		if (showType == SHOW_TYPE.FLOATING) {
			// 窗口
			int iDown = this.screenHeight - rectBoder.bottom;
			int iUp = this.screenHeight - rectBoder.height() - iDown;
			if (iDown > iUp) {
				if (boxHeight > iDown) {
					boxHeight = iDown;
				}
			} else {
				if (boxHeight > iUp) {
					boxHeight = iUp;
				}
			}
		} else {
			// 画面
			int iDown = this.screenHeight - rectBoder.bottom;
			int iUp = this.screenHeight - rectBoder.height() - iDown;
			if (iDown > iUp) {
				if (boxHeight > iDown) {
					boxHeight = iDown;
				}
			} else {
				if (boxHeight > iUp) {
					boxHeight = iUp;
				}
			}
		}
		if (boxHeight > 200) {
			boxHeight = 200;
		}
		mInflater = LayoutInflater.from(SKSceneManage.getInstance().mContext);
		// 获取窗口布局view
		// cView = mInflater.inflate(R.layout.recipeselectlist, null);
		cView = LayoutInflater.from(SKSceneManage.getInstance().mContext)
				.inflate(R.layout.recipeselectlist, null);
		// 设置窗口的颜色
		// cView.setBackgroundColor(Color.TRANSPARENT);
		// 获取显示在窗口的数据
		listView = (ListView) cView.findViewById(R.id.listViewrecipe);
		searchText = (EditText) cView.findViewById(R.id.listsearchtext);

		if (dataList != null && dataList.size() > 20) {
			searchText.setVisibility(View.VISIBLE);
		}
		listView.setFocusable(true);
		listView.setBackgroundColor(info.getnBackColor());
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// 设置搜索框的输入事件
		searchText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						String name = v.getText().toString();
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							updateList(name);
						}
						return false;
					}
				});

		// 设置listView 的每项点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;
				if (null != dataList) {
					if (!dataList.isEmpty()) {
						for (int j = 0; j < dataList.size(); j++) {
							if (j == position) {
								
								// 重新给选中的值赋给矩形显示框
								showFirstName = dataList.get(j)
										.getsRecipeName();
								RecipeOprop reci = dataList.get(j);
								myUpdateCurrentRecipe(reci);
								// 选择完，执行操作通知
								noticeAddr(info.getTouchInfo(), true);
								if (true == info.isbUseMacro()) {
									// 请求执行控件宏指令
									MacroManager.getInstance(null).Request(
											MSERV.CALLCM,
											(short) info.getnMacroId());
								}
								SKSceneManage.getInstance().onRefresh(items);
								
								//发送广播
								notifiSelect();
							}
						}
					}
					// 通知adapter里面改变勾选的值
					adapter.notifyDataSetChanged();
					if (popIsShow) {
						mPopupWindow.dismiss();
						popIsShow = false;
					}
				}
			}
		});
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;

			}
		});

		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, rectBoder.width()
				+ rectBox.width() - 1, boxHeight);
		// 做一个不在焦点外的处理事件监听
		mPopupWindow.getContentView().setOnTouchListener(
				new View.OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						SKSceneManage.getInstance().time = 0;
						mPopupWindow.setFocusable(false);
						mPopupWindow.dismiss();
						popIsShow = false;
						return true;
					}
				});

	}

	/**
	 * 显示下拉窗口的位置
	 */
	private void showPopupWindow() {
		// 窗口显示时要加上的窗口标题栏的高度
		popIsShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
		// mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		// 设置listview里面的值
		adapter = new RecipeSelectAdapter(SKSceneManage.getInstance().mContext,
				dataList, info);
		listView.setAdapter(adapter);
		int index = getCurrentReciIndex(dataList);
		listView.setSelection(index);
		if (null != searchText) {
			searchText.setText("");
			updateList("");
		}
		adapter.notifyDataSetChanged();
		int i = this.screenHeight - rectBoder.bottom;
		SHOW_TYPE showType = SHOW_TYPE.DEFAULT;
		if (null != SKSceneManage.getInstance().getCurrentInfo()) {
			showType = SKSceneManage.getInstance().getCurrentInfo().geteType();
		}

		if (showType == SHOW_TYPE.FLOATING && SKSceneManage.getInstance().getCurrentInfo().isbShowTitle()) {
			// 窗口
			if (i >= boxHeight) {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top + info.getnHeight() + 30);
			} else {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top - boxHeight + 30);

			}
		} else {
			// 画面
			if (i >= boxHeight) {
				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top + info.getnHeight());
			} else {

				mPopupWindow.showAtLocation(SKSceneManage.getInstance()
						.getCurrentScene(), Gravity.NO_GRAVITY,
						myRect.left + 1, myRect.top - boxHeight);

			}
		}
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub\
		recipeSelectIsShow();
		SKSceneManage.getInstance().onRefresh(items);
		return isShowFlag;
	}

	private void recipeSelectIsShow() {
		if (showByAddr || showByUser) {
			isShowFlag = popedomIsShow(info.getShowInfo());
		}
	}

	@Override
	public boolean isTouch() {
		// TODO Auto-generated method stub
		recipeSelectIsTouch();
		SKSceneManage.getInstance().onRefresh(items);
		return isTouchFlag;

	}

	private void recipeSelectIsTouch() {
		if (touchByAddr || touchByUser) {
			isTouchFlag = popedomIsTouch(info.getTouchInfo());
		}
	}

	/**
	 * 多语言切换通知刷新
	 */
	SKLanguage.ICallback languageICallback = new SKLanguage.ICallback() {

		@Override
		public void onLanguageChange(int languageId) {
			// TODO Auto-generated method stub
			SKSceneManage.getInstance().onRefresh(items);

		}
	};

	/**
	 * 注册地址
	 */
	private void registAddr() {

		// 注册配方数据通知接口
		RecipeDataCentre.getInstance().msgRegisterUpdate(ICallBackDataList);
		// 注册多语言切换接口
		if (SystemInfo.getLanguageNumber()>1) {
			SKLanguage.getInstance().getBinder().onRegister(languageICallback);
		}
		
	}

	/**
	 * 触控地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack touchCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isTouchFlag = isTouch();
		}

	};
	/**
	 * 显现地址值改变通知
	 */
	SKPlcNoticThread.IPlcNoticCallBack showCall = new SKPlcNoticThread.IPlcNoticCallBack() {

		@Override
		public void addrValueNotic(Vector<Byte> nStatusValue) {
			// TODO Auto-generated method stub
			isShowFlag = isShow();

		}

	};

	@Override
	public boolean drawGraphics(Canvas canvas, int itemId) {

		// mCanvas =canvas;
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

	private void draw(Paint paint, Canvas canvas) {

		// 如果是下拉框样式
		if (RECIPESELECT_TYPE.COMBOX == info.geteShowType()) {
			drawCombox(paint, canvas);
		} else if (RECIPESELECT_TYPE.ARRAYLIST == info.geteShowType()) {
			// 如果是列表样式
			drawList(paint, canvas, selectIndex);
		} else {
			// 默认是列表样式
			drawList(paint, canvas, selectIndex);
		}
		// 不可触控加上锁图标
		if (!isTouchFlag && SystemInfo.isbLockIcon()) {
			if (mLockBitmap == null) {
				if (SKSceneManage.getInstance().mContext != null)
					mLockBitmap = ImageFileTool.getBitmap(R.drawable.lock,
							SKSceneManage.getInstance().mContext);
			}
			if (mLockBitmap != null) {
				canvas.drawBitmap(mLockBitmap, info.getnStartPosX(),
						info.getnStartPosY(), null);
			}
		}

	}

	/**
	 * 画下拉框
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void drawCombox(Paint paint, Canvas canvas) {
		// 画文本
		drawTextValue(paint, canvas);

		// 画带下拉三角形的矩形
		rectItems.draw(paint, canvas);
		// 画三角形
		foldLineItem.draw(paint, canvas);
	}

	/**
	 * 画文本
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void drawTextValue(Paint paint, Canvas canvas) {
		String showText = " ";
		if (null != showFirstName) {
			if(showFirstName.size()>SystemInfo.getCurrentLanguageId()){
				showText = showFirstName.get(SystemInfo.getCurrentLanguageId());
			}else{
				showText = showFirstName.get(0);
			}
		}
		text.setM_sTextStr(showText);
		textItem.draw(canvas);
	}

	/**
	 * 画列表
	 * 
	 * @param paint
	 * @param canvas
	 */
	private void drawList(Paint paint, Canvas canvas, int itemIndex) {
		// double d = ;
		lineHeight = (float) info.getnHeight() / info.getnCurrShowRow();
		// 画每一项的矩形边框
		rectBoderList.left = info.getnStartPosX();
		rectBoderList.right = info.getnStartPosX() + info.getnWidth();
		text.setRectHeight(lineHeight);
		text.setLineWidth(1);
		text.setLineColor(Color.BLACK);
		int backColor = info.getnBackColor();// 每一行的背景颜色
		int alpha = info.getnTransparent();// 每一行的透明度
		boolean boo = false;
		if (dataList == null || dataList.isEmpty() || tempDataList == null) {
			for (int i = 0; i < info.getnCurrShowRow(); i++) {
				rectBoderList.top = (info.getnStartPosY() + i * lineHeight);
				rectBoderList.bottom = (info.getnStartPosY() + lineHeight + i
						* lineHeight);
				text.setStartY((int) rectBoderList.top);
				text.setM_backColorPadding(info.getnBackColor());
				text.setM_sTextStr(" ");
				textItem.draw(canvas);
			}
		} else {
			if (info.getnCurrShowRow() < dataList.size()) {
				boo = true;
			}
			for (int i = 0; i < info.getnCurrShowRow(); i++) {
				rectBoderList.top = (int) (info.getnStartPosY() + i
						* lineHeight);
				rectBoderList.bottom = (int) (info.getnStartPosY() + lineHeight + i
						* lineHeight);
				text.setStartY((int) rectBoderList.top);
				if (boo) { // 显示的行数小于集合的大小，可以往上 下拖拉
					if (i < tempDataList.size()) {
						RecipeOprop reci = tempDataList.get(i);
						if (null != reci) {
							Vector<String> names = reci.getsRecipeName();
							if (null != names) {
								if (!names.isEmpty()) {
									if (SystemInfo.getCurrentLanguageId() < names
											.size())
										text.setM_sTextStr(names.get(SystemInfo
												.getCurrentLanguageId()));
								}
							}
							if (reci.getnRecipeId() == SystemInfo
									.getCurrentRecipe().getCurrentRecipeId()
									&& info.getsShowRecipeId() == SystemInfo
											.getCurrentRecipe()
											.getCurrentGroupRecipeId()) {
								backColor = Color.BLUE;
								alpha = 255;
							} else {
								backColor = info.getnBackColor();
								alpha = info.getnTransparent();
							}
						}
					}
				} else {
					if (i < dataList.size()) {
						RecipeOprop reci = dataList.get(i);
						if (null != reci) {
							Vector<String> names = reci.getsRecipeName();
							if (null != names) {
								if (!names.isEmpty()) {
									if (SystemInfo.getCurrentLanguageId() < names
											.size())
										text.setM_sTextStr(names.get(SystemInfo
												.getCurrentLanguageId()));
								}
							}
							if (reci.getnRecipeId() == SystemInfo
									.getCurrentRecipe().getCurrentRecipeId()
									&& info.getsShowRecipeId() == SystemInfo
											.getCurrentRecipe()
											.getCurrentGroupRecipeId()) {
								backColor = Color.BLUE;
								alpha = 255;
							} else {
								backColor = info.getnBackColor();
								alpha = info.getnTransparent();
							}
						}
					} else {
						backColor = info.getnBackColor();
						alpha = info.getnTransparent();
						text.setM_sTextStr(" ");
					}
				}
				text.setRectHeight(lineHeight);
				text.setRectWidth(info.getnWidth());
				text.setM_backColorPadding(backColor);
				text.setM_alphaPadding(alpha);
				text.setBorderAlpha(255);
				textItem.initRectPaint();
				textItem.draw(canvas);
			}
		}
	}

	@Override
	public void realseMemeory() {
		isOnClick = false;
		if (popIsShow && null != mPopupWindow) {
			mPopupWindow.dismiss();
			popIsShow = false;
		}

		RecipeDataCentre.getInstance().msgDestoryCallback(ICallBackDataList);

	}

	@Override
	public void getDataFromDatabase() {

	}

	@Override
	public void setDataToDatabase() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initGraphics() {
		// TODO Auto-generated method stub
		init();

	}

	private void updateList(String name) {
		// 在这里编写自己想要实现的功能
		List<RecipeOprop> temp = DBTool.getInstance().getmRecipeSelectBiz()
				.getSearchList2(info.getsShowRecipeId(), name);
		dataList.clear();
		if (temp != null) {
			for (int i = 0; i < temp.size(); i++) {
				dataList.add(temp.get(i));
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void notifiSelect(){
		if (SKRecipeShow.mObservers != null && SKRecipeShow.mObservers.size() > 0) {
			SelectObser observable = new SelectObser();
			for(int i = 0; i < SKRecipeShow.mObservers.size(); i++){
				observable.addObserver(SKRecipeShow.mObservers.get(i));
			}

			observable.notifyChanges();
		}
	}
	
	class SelectObser extends Observable{
		
		public void notifyChanges(){
			setChanged();
			notifyObservers(-1);
		}
	}

	/**
	 * 获取当前配方在集合中的索引
	 * 
	 * @param list
	 * @return
	 */
	private int getCurrentReciIndex(List<RecipeOprop> list) {
		int returnIndex = 0;
		int currentReciId = SystemInfo.getCurrentRecipe().getCurrentRecipeId();
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				RecipeOprop reci = list.get(i);
				int j = reci.getnRecipeId();
				if (currentReciId == j) {
					returnIndex = i;
					break;
				}
			}
		}
		return returnIndex;
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
		if (info!=null) {
			return info.getnStartPosX();
		}
		return -1;
	}


	@Override
	public int getItemTop(int id) {
		// TODO Auto-generated method stub
		if (info!=null) {
			return info.getnStartPosY();
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
			if (x == info.getnStartPosX()) {
				return true;
			}
			if (x < 0|| x > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneWidth()) {
				return false;
			}
			info.setnStartPosX(x);
			int l=items.rect.left;
			items.rect.left=x;
			items.rect.right=x-l+items.rect.right;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.left=info.getnStartPosX();
			rectBoder.right = info.getnStartPosX() + info.getnWidth() - rect2Width;
			
			//文本显示区域
			text.setStartX(info.getnStartPosX());
			
			//显示三角形区域
			rectBox.left = rectBoder.right;
			rectBox.right = rectBoder.right + rect2Width;
			
			//三角形
			for (int i = 0; i < pointList.size(); i++) {
				pointList.get(i).x=pointList.get(i).x+x-l;
			}
			
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
			if (y == info.getnStartPosY()) {
				return true;
			}
			if (y < 0|| y > SKSceneManage.getInstance().getSceneInfo()
							.getnSceneHeight()) {
				return false;
			}
			info.setnStartPosY(y);
			int t = items.rect.top;
			items.rect.top = y;
			items.rect.bottom = y - t + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.top = info.getnStartPosY();
			rectBoder.bottom = info.getnStartPosY() + info.getnHeight();
			
			//文本显示区域
			text.setStartY(info.getnStartPosY());
			
			//显示三角形区域
			rectBox.top = rectBoder.top ;
			rectBox.bottom = rectBoder.bottom ;
			
			//三角形
			for (int i = 0; i < pointList.size(); i++) {
				pointList.get(i).y=pointList.get(i).y+y-t;
			}
			
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
			
			myRect.right=w-myRect.width()+myRect.right;
			rect2Width = myRect.width() * 6 / 27;
			
			//外边框
			rectBoder.right = info.getnStartPosX() + info.getnWidth() - rect2Width;
			
			//文本显示区域
			text.setRectWidth(rectBoder.width()-1);
			
			//显示三角形区域
			rectBox.left = rectBoder.right;
			rectBox.right = rectBoder.right + rect2Width;
			
			// 下拉三角的点集合
			int leftPX = rectBox.left + rect2Width * 1 / 4;
			int leftPY = myRect.top + myRect.height() * 7 / 18;
			int rightPX = rectBox.left + rect2Width * 3 / 4;
			int rightPY = leftPY;
			int buttomX = rectBox.left + rect2Width / 2;
			int buttomY = rectBox.top + rectBox.height() * 11 / 18;
			pointList.clear();
			pointList.add(new Point(leftPX, leftPY));
			pointList.add(new Point(buttomX, buttomY));
			pointList.add(new Point(rightPX, rightPY));
			
			mPopupWindow=null;
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
			int temp=info.getnHeight();
			info.setnHeight((short)h);
			items.rect.bottom = h - items.rect.height() + items.rect.bottom;
			items.mMoveRect=new Rect();
			
			//外边框
			rectBoder.bottom = info.getnStartPosY() + info.getnHeight();
			
			//文本显示区域
			text.setRectHeight(rectBoder.height()-2);
			
			//显示三角形区域
			rectBox.bottom = rectBoder.bottom ;
			
			//三角形
			for (int i = 0; i < pointList.size(); i++) {
				pointList.get(i).y=pointList.get(i).y+(h-temp)/2;
			}
			
			mPopupWindow=null;
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
			textItem.resetColor(color, 2);
			listView.setBackgroundColor(info.getnBackColor());
			SKSceneManage.getInstance().onRefresh(items);
			return true;
		}
		return false;
	}


	@Override
	public boolean setItemLineColor(int id, short r, short g, short b) {
		// TODO Auto-generated method stub
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
	 * 获取RGB颜色
	 */
	private short[] getColor(int color) {
		short[] c = new short[3];
		c[0] = (short) ((color >> 16) & 0xFF); // RED
		c[1] = (short) ((color >> 8) & 0xFF);// GREEN
		c[2] = (short) (color & 0xFF);// BLUE
		return c;

	}
}