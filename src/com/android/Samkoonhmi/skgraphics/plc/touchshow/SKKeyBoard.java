//import SKGraphCmnTouch;
package com.android.Samkoonhmi.skgraphics.plc.touchshow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.Samkoonhmi.databaseinterface.KeyBroadBiz;
import com.android.Samkoonhmi.graphicsdrawframe.RectItem;
import com.android.Samkoonhmi.graphicsdrawframe.TextItem;
import com.android.Samkoonhmi.model.KeyBoardButtonInfo;
import com.android.Samkoonhmi.model.KeyBoardInfo;
import com.android.Samkoonhmi.model.StaticTextModel;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.KEY_STYLE;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.DataTypeFormat;
import com.android.Samkoonhmi.util.ImageFileTool;

public class SKKeyBoard extends View {

	public ArrayList<SKKeyButton> buttonList;// 键盘按钮实体类集合
	private KeyBoardInfo keyInfo;// 键盘实体类
	private StaticTextModel textMax;// 文本最大值
	private StaticTextModel textMin;// 文本最小值
	private StaticTextModel textInput;// 输出文本实体类
	private TextItem textItemMax;// 文本最值
	private TextItem textItemMin;
	private TextItem textItemInput;// 输出文本
	private Rect rectKey;// 矩形键盘
	private RectItem item;// 矩形基类
	private String inputStr = "0";// 输出文本
	private String passStr = "";
	private Paint mpaint;// 矩形画笔
	private Paint tpaint;// 文本画笔
	private Bitmap bitmaps;// 图片
	private Rect rects;// 背景图片矩形
	private Context mcontext;// 上下文
	private boolean decimals;// 标记小数点
	private boolean bTextColor;// 文本颜色检查
	private boolean minus;// 标记负号
	private IClickLister lister;
	private int toaskStartX;
	private int toaskStartY;
	private String min = "0";// 最小值
	private String max = "0";// 最大值
	private boolean checks;// 检查输入
	private boolean isPass;// 密码
	private String lastText;
	private int nSceneId;
	private int nInputTextSize;
	private DATA_TYPE dataType;
	private String escText = "";
	private SKToast skToast = null;
	private KEYBOARD_OPERATION keyOperation;
	private KeyBroadBiz keys;// 键盘数据库
	private boolean init;
	private boolean bFirstClick=true;//第一次点击，清除之前数据
	private List<KeyBoardButtonInfo> listButtonInfo;

	public SKKeyBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mcontext = context;
		skToast = new SKToast();
		if (buttonList == null) {
			buttonList = new ArrayList<SKKeyButton>();
		}
		init = false;

		mpaint = new Paint();
		tpaint = new Paint();
		textMax = new StaticTextModel();
		textMin = new StaticTextModel();
		textInput = new StaticTextModel();
		rectKey = new Rect();

	}

	/**
	 * 初始化
	 */
	public void init() {
		getDataFromDatabase();
		// keys=new KeyBroadBiz();
		nInputTextSize = keyInfo.getnTextFontSize();
		// 初始化
		decimals = true;// 小数点
		minus = true;// 负号
		bTextColor = true;
		if (escText.equals("")) {
			escText = "0";
		}
		init = true;
		initText();
	}

	/**
	 * 初始化键盘框架
	 */
	public void initKey() {
		// 设置矩形键盘坐标
		rectKey.left = 0;
		rectKey.top = 0;
		rectKey.right = rectKey.left + keyInfo.getNkeyWidth();
		rectKey.bottom = rectKey.top + keyInfo.getNkeyHeight();
	}

	/**
	 * 系统方法
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (init) {
			draws(canvas, mpaint);
		}
	}

	/**
	 * 画所有
	 */
	public void draws(Canvas canvas, Paint paint) {
		drawKey(canvas, paint);// 画键盘
		drawKeyOne(canvas);// 画键盘第一行
		drawKeyTwo(canvas);// 画键盘第二行
		// 循环键盘按钮
		for (int i = 0; i < buttonList.size(); i++) {
			buttonList.get(i).drawButton(canvas);
		}
	}

	/**
	 * 画键盘
	 */
	public void drawKey(Canvas canvas, Paint paint) {
		initKey();
		item = new RectItem(rectKey);
		// 选择颜色还是图片代码
		if (keyInfo.geteBackType() == KEY_STYLE.PICTURE_STYLE) {
			if (keyInfo.getsPicturePath() != null
					&& !"".equals(keyInfo.getsPicturePath())) {// 加图片
				bitmaps = ImageFileTool.getBitmap(keyInfo.getsPicturePath());
				if (rects == null) {
					rects = new Rect();
					rects.set(keyInfo.getNkeyStartX(), keyInfo.getNkeyStartY(),
							keyInfo.getNkeyStartX() + keyInfo.getNkeyWidth(),
							keyInfo.getNkeyStartY() + keyInfo.getNkeyHeight());
				}
				if (bitmaps != null) {
					canvas.drawBitmap(bitmaps, null, rects, null);// 画图片
				}
			}
		} else {
			keyColor();// 键盘颜色
			item.setLineColor(Color.BLACK);// 边框色
			item.draw(paint, canvas);
		}
	}

	/**
	 * 键盘背景颜色
	 */
	public void keyColor() {
		item.setBackColor(keyInfo.getNkeyBackColor());// 背景色
		item.setForeColor(keyInfo.getNkeyForeColor());// 前景色
		item.setStyle(keyInfo.getEkeyStyle());// 样式
	}

	/**
	 * 画键盘第一层
	 */
	private BigDecimal b;

	public void drawKeyOne(Canvas canvas) {
		// 文本最小值

		String minText = "";
		if (dataType == DATA_TYPE.HEX_16) {
			minText = Integer.toHexString(Integer.valueOf(getMin()));
		} else if (dataType == DATA_TYPE.HEX_32) {
			minText = Long.toHexString(Long.valueOf(getMin()));
		} else {
			minText = getMin();
		}
		textMin.setM_sTextStr("Min:" + minText);

		if (keyInfo.getnMinWidth() != 0 && keyInfo.getnMinHeight() != 0) {
			textItemMin.draw(canvas);
		}

		// 文本最大值

		String maxText = "";
		if (dataType == DATA_TYPE.HEX_16) {
			maxText = Integer.toHexString(Integer.valueOf(getMax()));
		} else if (dataType == DATA_TYPE.HEX_32) {
			maxText = Long.toHexString(Long.valueOf(getMax()));
		} else {
			maxText = getMax();
		}
		textMax.setM_sTextStr("Max:" + maxText);

		if (keyInfo.getnMaxWidth() != 0 && keyInfo.getnMaxHeight() != 0) {
			textItemMax.draw(canvas);
		}
	}

	private void initText() {
		// 输入最大值
		textMax.setM_alphaPadding(keyInfo.getnMaxAlpha());
		textMax.setBorderAlpha(keyInfo.getnMaxAlpha());
		textMax.setM_backColorPadding(keyInfo.getnMaxBackColor());
		textMax.setM_eTextAlign(keyInfo.getnMaxAlign());
		textMax.setM_foreColorPadding(keyInfo.getnMaxForeColor());
		textMax.setM_nFontColor(keyInfo.getnMaxFontColor());
		textMax.setM_nFontSize(keyInfo.getnMaxFontSize());
		textMax.setM_sFontFamly(keyInfo.getnMaxFont());
		textMax.setM_stylePadding(keyInfo.getnMaxStyle());
		textMax.setM_textPro(keyInfo.getnMaxFontPro());
		textMax.setRectHeight(keyInfo.getnMaxHeight());
		textMax.setRectWidth(keyInfo.getnMaxWidth());
		textMax.setStartX(keyInfo.getnMaxStartX());
		textMax.setStartY(keyInfo.getnMaxStartY());
		if (textItemMax == null) {
			textItemMax = new TextItem(textMax);
			// 设置背景边框画笔属性
			textItemMax.initRectBoderPaint();
			// 设置背景画笔属性
			textItemMax.initRectPaint();
			// 设置文本画笔属性
			textItemMax.initTextPaint();
		}
		// 最小值
		textMin.setM_alphaPadding(keyInfo.getnMinAlpha());
		textMin.setBorderAlpha(keyInfo.getnMinAlpha());
		textMin.setM_backColorPadding(keyInfo.getnMinBackColor());
		textMin.setM_eTextAlign(keyInfo.getnMinAlign());
		textMin.setM_foreColorPadding(keyInfo.getnMinForeColor());
		textMin.setM_nFontColor(keyInfo.getnMinFontColor());
		textMin.setM_nFontSize(keyInfo.getnMinFontSize());
		textMin.setM_sFontFamly(keyInfo.getnMinFont());
		textMin.setM_stylePadding(keyInfo.getnMinStyle());
		textMin.setM_textPro(keyInfo.getnMinFontPro());
		textMin.setRectHeight(keyInfo.getnMinHeight());
		textMin.setRectWidth(keyInfo.getnMinWidth());
		textMin.setStartX(keyInfo.getnMinStartX());
		textMin.setStartY(keyInfo.getnMinStartY());
		if (textItemMin == null) {
			textItemMin = new TextItem(textMin);
			// 设置背景边框画笔属性
			textItemMin.initRectBoderPaint();
			// 设置背景画笔属性
			textItemMin.initRectPaint();
			// 设置文本画笔属性
			textItemMin.initTextPaint();
		}

		// 输入文本框
		textInput.setM_alphaPadding(keyInfo.getnTextAlpha());
		textInput.setBorderAlpha(keyInfo.getnTextAlpha());
		textInput.setM_backColorPadding(keyInfo.getnTextBackColor());// 文本框背景颜色
		textInput.setM_foreColorPadding(keyInfo.getnTextForeColor());
		textInput.setM_stylePadding(keyInfo.getnTextStyle());
		textInput.setM_eTextAlign(keyInfo.getnTextAlign());
		textInput.setM_nFontColor(keyInfo.getnTextFontColor());
		textInput.setM_nFontSize(nInputTextSize);
		textInput.setM_nFontSpace(textInput.getM_nFontSpace());
		textInput.setM_sFontFamly(keyInfo.getnTextFont());
		textInput.setM_textPro(keyInfo.getnTextFontPro());
		textInput.setRectHeight(keyInfo.getnTextHeight());
		textInput.setRectWidth(keyInfo.getnTextWidth());
		textInput.setStartX(keyInfo.getnTextStartX());
		textInput.setStartY(keyInfo.getnTextStartY());
		if (textItemInput == null) {
			textItemInput = new TextItem(textInput);
			// 设置背景边框画笔属性
			textItemInput.initRectBoderPaint();
			// 设置背景画笔属性
			textItemInput.initRectPaint();
		}
	}

	/**
	 * 画键盘第二层
	 */
	public void drawKeyTwo(Canvas canvas) {

		if (isPass) {
			for (int i = 0; i < inputStr.length(); i++) {
				passStr += "*";
			}
			textInput.setM_sTextStr(passStr);
			passStr = "";
		} else {
			textInput.setM_sTextStr(inputStr+"");
		}

		textInput.setM_nFontSize(nInputTextSize);
		// 设置文本画笔属性
		textItemInput.initTextPaint();
		if (keyInfo.getnTextWidth() != 0 && keyInfo.getnTextHeight() != 0) {
			textItemInput.draw(canvas);
		}
	}

	/**
	 * 超出范围显示的颜色
	 */
	public void errorRed() {
		textInput.setM_backColorPadding(Color.RED);
		textInput.setM_foreColorPadding(Color.RED);
		// 设置背景画笔属性
		textItemInput.initRectPaint();
	}

	/**
	 * 还原输出文本颜色
	 */
	public void rigthWhite() {
		textInput.setM_backColorPadding(keyInfo.getnTextBackColor());
		textInput.setM_foreColorPadding(keyInfo.getnTextForeColor());
		textInput.setM_stylePadding(keyInfo.getnTextStyle());
		// 设置背景画笔属性
		textItemInput.initRectPaint();
	}

	/**
	 * 点击按钮
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		SKSceneManage.getInstance().time=0;
		// 如果键盘实体类为空，则返回false
		if (keyInfo == null) {
			return false;
		}
		SKSceneManage.getInstance().time = 0;
		if (buttonList != null) {
			for (int i = 0; i < buttonList.size(); i++) {
				buttonList.get(i).onTouchEvent(event);// 调用键盘按钮触碰事件
			}

		}
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN
				|| action == MotionEvent.ACTION_MOVE) {

			if (event.getX() > keyInfo.getnTextStartX()
					&& event.getX() < keyInfo.getnTextStartX()
							+ keyInfo.getnTextWidth()
					&& event.getY() > keyInfo.getnTextStartY()
					&& event.getY() < keyInfo.getnTextStartY()
							+ keyInfo.getnTextHeight()) {
				nInputTextSize = keyInfo.getnTextFontSize() * 2;
				rigthWhite();// 变白色
			}
		}
		if (action == MotionEvent.ACTION_UP) {
			nInputTextSize = keyInfo.getnTextFontSize();
		}
		return true;
	}

	SKKeyBoard.ICallback callback = new SKKeyBoard.ICallback() {
		
		@Override
		public void onResult(String inputText, KEYBOARD_OPERATION keyOperation,
				boolean down) {
			keyOperations(inputText, keyOperation,down);// 调用键盘按钮操作方法
		}
	};

	// 接口
	interface ICallback {
		
		/**
		 * @param inputText-输入字符
		 * @param keyOperation-按钮类型
		 * @param down-ture 按下
		 * 两个参数都为null时，表示刷新界面
		 */
		void onResult(String inputText, KEYBOARD_OPERATION keyOperation,boolean down);
	}

	interface IClickLister {
		void close();

		void closeEsc();
	}

	/**
	 * 键盘按钮操作
	 * 
	 * @param inputText
	 * @param keyOperation
	 */
	public void keyOperations(String inputText, KEYBOARD_OPERATION keyOperation,boolean down) {
		
		StringBuffer sb = new StringBuffer();// 创建StringBuffer
		if (keyOperation == KEYBOARD_OPERATION.ESC) {// 取消按钮
			rigthWhite();// 变白色
			decimals = true;
			minus = true;// 标记负号
			// 隐藏popUpWindow
			if (lister != null) {
				lister.closeEsc();
			}
			sb.append(escText);
			inputStr = sb.toString();
		} else if (keyOperation == KEYBOARD_OPERATION.DEL) {// 单个删除
			rigthWhite();// 变白色
			int textLength = inputStr.length();// 总长度
			if (textLength != 0 && !inputStr.equals("0")) {// 如果长度不为0
				sb.append(inputStr);// 叠加字符串
				sb.delete(textLength - 1, textLength);// 单个删除操作
				inputStr = sb.toString();
				if(inputStr.equals(""))
				{
					inputStr ="0";
				}
			} else {
				decimals = true;
				minus = true;// 标记负号
				inputStr = "0";
			}
		} else if (keyOperation == KEYBOARD_OPERATION.CLR) {// 清空字符按钮
			inputStr = "0";
			rigthWhite();// 变白色
			decimals = true;
			minus = true;// 标记负号
		} else if (keyOperation == KEYBOARD_OPERATION.ENTER) {// 确定按钮
			// checks判断当前键盘是否为数值型键盘，checks为true是数值型键盘
			if (checks) {
				// 确定字符
				if (inputStr.equals("-")) {
					inputStr = "0";
				}

				boolean boo = isNumeric(inputStr);// 正则表达式
				if (boo == false) {// 如果输入框中含有英文字符
					decimals = true;
					minus = true;// 标记负号
					if (dataType == DATA_TYPE.HEX_16)// 16位16进制
					{
						if (inputStr==null||inputStr.equals("")) {
							inputStr="0";
						}
						int inputTextHex = Integer.valueOf(inputStr, 16); // 将输入的字符转成10进制数进行大小比较
						if (inputTextHex > Double.valueOf(getMax())
								|| inputTextHex < Double.valueOf(getMin())) {
							errorRed();// 文本框变红色
						} else {
							sb.append(inputStr);
							inputStr = sb.toString();
							if (lister != null) {
								lister.close();
							}
							escText = inputStr;
						}

					} else if (dataType == DATA_TYPE.HEX_32)// 32位16进制
					{
						if (inputStr==null||inputStr.equals("")) {
							inputStr="0";
						}
						long inputTextHex = Long.valueOf(inputStr, 16);// 将输入的字符转成10进制数进行大小比较
						if (inputTextHex > Double.valueOf(getMax())
								|| inputTextHex < Double.valueOf(getMin())) {
							errorRed();// 文本框变红色
						} else {
							sb.append(inputStr);
							inputStr = sb.toString();
							if (lister != null) {
								lister.close();
							}
							escText = inputStr;
						}
					} else {
						errorRed();// 文本框变红色
					}

				} else {
					if (inputStr==null||inputStr.equals("")) {
						inputStr="0";
					}
					double numText = 0L;
					
                    if(dataType == DATA_TYPE.HEX_16){
                    	numText =Integer.valueOf(inputStr, 16);
                    }else if(dataType == DATA_TYPE.HEX_32)
                    {
                    	numText= Long.valueOf(inputStr, 16);
                    }else{
                    	numText = Double.parseDouble(inputStr);
                    }
					if (numText > Double.valueOf(getMax())
							|| numText < Double.valueOf(getMin())) {
						errorRed();// 文本框变红色
					} else {
						decimals = true;
						minus = true;// 标记负号
						// 隐藏popUpWindow
						if (lister != null) {
							lister.close();
						}
						escText = inputStr;
					}
				}
			} else {// ASCII键盘
				decimals = true;// 小数点
				minus = true;// 标记负号
				// 判断输出字符长度是否大于显示位数
				if (inputStr.length() > Double.valueOf(getMax())) {
					errorRed();
				} else {
					sb.append(inputStr);
					inputStr = sb.toString();
					if (lister != null) {
						lister.close();
					}
					// inputStr = "0";
					escText = inputStr;
				}
			}
		} else if (keyOperation == KEYBOARD_OPERATION.TEXT) {// 字符按钮
			if(down){
				if (inputText.equals(".")) {// 小数点按钮
					if (decimals) {
						if (inputStr.equals("")) {
							inputStr = "0.";
						} else if (inputStr.indexOf("-") == 0
								&& inputStr.length() == 1) {
							inputStr = "-0.";
						} else {
							sb.append(inputStr);
							sb.append(inputText);
							inputStr = sb.toString();
						}
						decimals = false;
					}
					rigthWhite();// 变白色
				} else if (inputText.equals("-")) {// 负号按钮
					if (minus) {// 标记负号
						if (inputStr.equals("0")) {
							inputStr = "";
						}
						sb.append(inputText);
						sb.append(inputStr);
						inputStr = sb.toString();
						minus = false;// 标记负号
					}
					rigthWhite();// 变白色
				} else {
					// 叠加字符
					if (inputStr.equals("0")||inputStr==null||bFirstClick) {
						bFirstClick=false;
						inputStr = "";
					}
					sb.append(inputStr+"");
					sb.append(inputText);
					inputStr = sb.toString();
					rigthWhite();// 变白色
				}
			}
		}
		
		invalidate();
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern
				.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$|^-\\d+\\.\\d+$");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public void getDataFromDatabase() {
		// 键盘实体类
		if (keyInfo == null) {
			keyInfo = new KeyBoardInfo();

			// 数据库
			if (keys == null) {
				keys = new KeyBroadBiz();
			}
			keyInfo = keys.selectKeyBorad(getnSceneId());

			if (listButtonInfo == null) {
				listButtonInfo = keys.getKeyButton(getnSceneId());
				for (int i = 0; i < listButtonInfo.size(); i++) {
					SKKeyButton keyButtons = new SKKeyButton(
							listButtonInfo.get(i), mcontext);
					keyButtons.setToast(skToast);
					keyButtons.setCallback(callback);
					keyButtons.setToastX(getToaskStartX());
					keyButtons.setToastY(getToaskStartY());
					buttonList.add(keyButtons);
				}
			}
		}

	}

	public IClickLister getLister() {
		return lister;
	}

	public void setLister(IClickLister lister) {
		this.lister = lister;
	}

	public KeyBoardInfo getKeyInfo() {
		return keyInfo;
	}

	public void setKeyInfo(KeyBoardInfo keyInfo) {
		this.keyInfo = keyInfo;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getMax() {
		return max;
	}

	public void setMax(String max) {
		this.max = max;
	}

	public boolean isChecks() {
		return checks;
	}

	public void setChecks(boolean checks) {
		this.checks = checks;
	}

	public int getnSceneId() {
		return nSceneId;
	}

	public void setnSceneId(int nSceneId) {
		this.nSceneId = nSceneId;
	}

	public DATA_TYPE getDataType() {
		return dataType;
	}

	public void setDataType(DATA_TYPE dataType) {
		this.dataType = dataType;
	}

	public String getLastText() {
		return lastText;
	}

	public void setLastText(String lastText) {
		this.lastText = lastText;
	}

	public String getInputStr() {
		return inputStr;
	}

	public void setInputStr(String inputStr) {
		this.inputStr = inputStr;
	}

	public int getToaskStartX() {
		return toaskStartX;
	}

	public void setToaskStartX(int toaskStartX) {
		this.toaskStartX = toaskStartX;
	}

	public int getToaskStartY() {
		return toaskStartY;
	}

	public void setToaskStartY(int toaskStartY) {
		this.toaskStartY = toaskStartY;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
	
	public String getSInputStr(){
		return inputStr;
	}
	
	public void setbFirstClick(boolean bFirstClick) {
		this.bFirstClick = bFirstClick;
	}

}