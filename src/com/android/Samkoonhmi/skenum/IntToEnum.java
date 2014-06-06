package com.android.Samkoonhmi.skenum;

import com.android.Samkoonhmi.skenum.BUTTON.BUTTON_TYPE;
import com.android.Samkoonhmi.util.COM_PORT_PARAM_PROP;

/**
 * int to enum
 * 
 * @author Administrator
 * @version v 1.0.0.1 创建时间 2012-6-11
 */
public class IntToEnum {

	
	/**
	 * 刻度条方向类
	 */
	public static CALIBRATION_DIRECTION getCalibrationDirection(int num){
		switch(num){
		case 1:
			return CALIBRATION_DIRECTION.DIRECTION_UP;//向上
		case 2:
			return CALIBRATION_DIRECTION.DIRECTION_DOWN;//向下
		case 3:
			return CALIBRATION_DIRECTION.DIRECTION_LEFT;//向左
		case 4:
			return CALIBRATION_DIRECTION.DIRECTION_RIGHT;//向右
		}
		return CALIBRATION_DIRECTION.DIRECTION_UP;
	}
	
	/**
	 * 流动块流动速度类
	 */
	public static SPEED getFlowSpeed(int num){
		switch(num){
		case 1:
			return SPEED.FIXEDFLOWSPEED;//固定速度
		case 2:
			return SPEED.TRENDFLOWSPEED;//动态速度
		case 3:
			return SPEED.LOW;//低速
		case 4:
			return SPEED.MIDDLE;//中速
		case 5:
			return SPEED.HIGH;//高速
		}
		return SPEED.FIXEDFLOWSPEED;
	}
	
	/**
	 * 键盘按钮操作属性类
	 */
	public static KEYBOARD_OPERATION getKeyOperation(int num){
		switch(num){
		case 1:
			return KEYBOARD_OPERATION.ENTER;//确定
		case 2:
			return KEYBOARD_OPERATION.DEL;//退后
		case 3:
			return KEYBOARD_OPERATION.CLR;//清除
		case 4:
			return KEYBOARD_OPERATION.ESC;//取消
		case 5:
			return KEYBOARD_OPERATION.TEXT;//文本
		case 6:
			return KEYBOARD_OPERATION.CAPS;//大写键
		}
		return KEYBOARD_OPERATION.TEXT;
	}
	/**
	 * 样式枚举类
	 */
	public static CSS_TYPE getCssType(int num) {
		switch (num) {
		case 1:
			return CSS_TYPE.CSS_TRANSPARENCE;
		case 2:
			return CSS_TYPE.CSS_SOLIDCOLOR;
		case 3:
			return CSS_TYPE.CSS_ORIENTATION;
		case 4:
			return CSS_TYPE.CSS_ORIENTATION_SYMMETRY;
		case 5:
			return CSS_TYPE.CSS_PORTRAIT;
		case 6:
			return CSS_TYPE.CSS_PORTRAIT_SYMMETRY;
		case 7:
			return CSS_TYPE.CSS_TIP_UP;
		case 8:
			return CSS_TYPE.CSS_TIP_UP_SYMMETRY;
		case 9:
			return CSS_TYPE.CSS_TIP_DOWN;
		case 10:
			return CSS_TYPE.CSS_TIP_DOWN_SYMMETRY;
		case 11:
			return CSS_TYPE.CSS_RIGHTCORNER_ERADIATE;
		case 12:
			return CSS_TYPE.CSS_LEFTCORNER_ERADIATE;
		case 13:
			return CSS_TYPE.CSS_CENTER_ERADIATE;
		
		}
		return CSS_TYPE.CSS_TRANSPARENCE;
	}

	/**
	 * BEELINE 1 直线 FOLDLINE 2 折线 FREELINE 3 自由直线 CURVEARCLINE 4 曲线圆弧
	 * 
	 * @param lineClass
	 * @return
	 */
	public static int convertLineClass(LINE_CLASS lineClass) {
		int i = 1;
		switch (lineClass) {
		case BEELINE:
			i = 1;
			break;
		case FOLDLINE:
			i = 2;
			break;
		case FREELINE:
			i = 3;
			break;
		case CURVEARCLINE:
			i = 4;
			break;
		default:
			i = 1;
			break;
		}
		return i;
	}

	/**
	 * 线类型转换
	 * 
	 * @param i
	 * @return
	 */
	public static LINE_TYPE getLineType(int i) {
		switch (i) {
		case 1:
			return LINE_TYPE.NO_PEN;
		case 2:
			return LINE_TYPE.SOLID_LINE;
		case 3:
			return LINE_TYPE.DASH_LINE;
		case 4:
			return LINE_TYPE.DOT_LINE;
		case 5:
			return LINE_TYPE.DASH_DOT_LINE;
		case 6:
			return LINE_TYPE.DASH_DOT_DOT_LINE;
		}
		return LINE_TYPE.SOLID_LINE;
	}

	/**
	 * STYLE_NONE 1 没有箭头 FILLED_TRIANGLE 2 实心三角形 FOLD_LINE_TRIANGLE 3 大于号箭头
	 * FILLED_SCISSORS 4 燕子尾巴形状 FILLED_RECT 5 菱形 FILLED_ELLIPSE 6 实心圆
	 * 
	 * @param i
	 * @return
	 */
	public static END_ARROW_TYPE convertEndArrowType(int i) {
		END_ARROW_TYPE end = null;
		switch (i) {
		case 1:
			end = END_ARROW_TYPE.STYLE_NONE;
			break;
		case 2:
			end = END_ARROW_TYPE.FILLED_TRIANGLE;
			break;
		case 3:
			end = END_ARROW_TYPE.FOLD_LINE_TRIANGLE;

			break;
		case 4:
			end = END_ARROW_TYPE.FILLED_SCISSORS;
			break;
		case 5:
			end = END_ARROW_TYPE.FILLED_RECT;
			break;
		case 6:
			end = END_ARROW_TYPE.FILLED_ELLIPSE;
			break;
		default:
			end = END_ARROW_TYPE.STYLE_NONE;
			break;
		}
		return end;
	}

	/**
	 * 语言的转换
	 * 
	 * @param i
	 * @return
	 */
	public static TEXT_LANGUAGE getText_Language(int i) {
		switch (i) {
		case 1:
			return TEXT_LANGUAGE.ENGLISH;
		case 2:
			return TEXT_LANGUAGE.CHINESE_SIMPLIFI;
		case 3:
			return TEXT_LANGUAGE.CHINESE_TRADITIONAL;
		}
		return TEXT_LANGUAGE.CHINESE_SIMPLIFI;

	}

	/**
	 * 地址类型枚举
	 * 
	 * @param i
	 * @return
	 */
	public static ADDRTYPE getAddrType(int i) {
		switch (i) {
		case 1:
			return ADDRTYPE.BITADDR;
		case 2:
			return ADDRTYPE.WORDADDR;
		}
		return ADDRTYPE.BITADDR;
	}

	/**
	 * 按钮类型转换
	 * 
	 * @param i
	 * @return
	 */
	public static BUTTON.BUTTON_TYPE getButtonType(int i) {

		switch (i) {
		case 1:
			return BUTTON_TYPE.BIT;
		case 2:
			return BUTTON_TYPE.WORD;
		case 3:
			return BUTTON_TYPE.SCENE;
		case 4:
			return BUTTON_TYPE.PECULIAR;
		case 5:
			return BUTTON_TYPE.BIT_LIGHT;
		}
		return BUTTON_TYPE.BIT;
	}

	/**
	 * 几何图形转换
	 * 
	 * @param shapClass
	 * @return
	 */
	public static int getShapTypeValue(SHAP_CLASS shapClass) {

		switch (shapClass) {
		case CIRCLE:
			return 1;
		case ELLIPSE:
			return 1;
		case RECT:
			return 3;
		case CIRCLE_RECT:
			return 4;
		case POLYGON:
			return 5;
		case SECTOR:
			return 6;
		}
		return 0;
	}

	/**
	 * 数据类型枚举转换
	 * 
	 * @param i
	 * @return
	 */
	public static DATA_TYPE getDataType(int i) {
		switch (i) {

		case 1:
			return DATA_TYPE.BIT_1;
		case 2:
			return DATA_TYPE.INT_16;
		case 3:
			return DATA_TYPE.INT_32;
		case 4:
			return DATA_TYPE.POSITIVE_INT_16;
		case 5:
			return DATA_TYPE.POSITIVE_INT_32;
		case 6:
			return DATA_TYPE.BCD_16;
		case 7:
			return DATA_TYPE.BCD_32;
		case 8:
			return DATA_TYPE.FLOAT_32;
		case 9:
			return DATA_TYPE.ASCII_STRING;
		case 10:
			return DATA_TYPE.HEX_16;
		case 11:
			return DATA_TYPE.HEX_32;
		case 12:
			return DATA_TYPE.OTC_16;
		case 13:
			return DATA_TYPE.OTC_32;
		case 15:
			return DATA_TYPE.UNICODE_STRING;
		}
		return DATA_TYPE.OTHER_DATA_TYPE;
	}

	/**
	 * 多边形端点连接类型
	 * 
	 * @param i
	 * @return
	 */
	public static END_POINT_TYPE getEndPointType(int i) {

		switch (i) {
		case 1:
			return END_POINT_TYPE.FLAT_CAP;
		case 2:
			return END_POINT_TYPE.SQUARE_CAP;
		case 3:
			return END_POINT_TYPE.ROUND_CAP;

		}
		return END_POINT_TYPE.FLAT_CAP;
	}

	/**
	 * 配方选择器显示样式
	 * 
	 * @param i
	 * @return
	 */
	public static RECIPESELECT_TYPE getRecipeSelectType(int i) {
		switch (i) {
		case 1:
			return RECIPESELECT_TYPE.ARRAYLIST;
		case 2:
			return RECIPESELECT_TYPE.COMBOX;
		}
		return RECIPESELECT_TYPE.ARRAYLIST;
	}

	/**
	 * 文本对齐方式枚举
	 * 
	 * @param i
	 * @return
	 */
	public static TEXT_PIC_ALIGN getTextPicAlign(int i) {
		switch (i) {
		case 1:
			return TEXT_PIC_ALIGN.LEFT;
		case 2:
			return TEXT_PIC_ALIGN.CENTER;
		case 3:
			return TEXT_PIC_ALIGN.RIGHT;
		}
		return TEXT_PIC_ALIGN.CENTER;
	}

	/**
	 * 显示类型 常量还是地址
	 * 
	 * @param i
	 * @return
	 */
	public static SHOWAREA getShowArea(int i) {
		switch (i) {
		case 1:
			return SHOWAREA.CONSTANT;
		case 2:
			return SHOWAREA.ADDRESS;
		}
		return SHOWAREA.CONSTANT;
	}

	/**
	 * 获取输入类型
	 * 
	 * @param i
	 * @return
	 */
	public static INPUT_TYPE getInputType(int i) {
		switch (i) {
		case 1:
			return INPUT_TYPE.TOUCH;
		case 2:
			return INPUT_TYPE.BIT;
		}
		return INPUT_TYPE.TOUCH;
	}

	/**
	 * 日期格式枚举
	 * 
	 * @param i
	 * @return
	 */
	public static DATE_FORMAT getDateType(int i) {
		switch (i) {
		case 1:
			return DATE_FORMAT.YYYYMMDD_SLASH;
		case 2:
			return DATE_FORMAT.YYYYMMDD_POINT;
		case 3:
			return DATE_FORMAT.YYYYMMDD_ACROSS;
		case 4:
			return DATE_FORMAT.MMDDYYYY_SLASH;
		case 5:
			return DATE_FORMAT.MMDDYYYY_POINT;
		case 6:
			return DATE_FORMAT.MMDDYYYY_ACROSS;
		case 7:
			return DATE_FORMAT.DDMMYYYY_SLASH;
		case 8:
			return DATE_FORMAT.DDMMYYYY_POINT;
		case 9:
			return DATE_FORMAT.DDMMYYYY_ACROSS;
		}
		return DATE_FORMAT.YYYYMMDD_SLASH;
	}

	/**
	 * 时间类型的转换
	 * 
	 * @param i
	 * @return
	 */
	public static TIME_FORMAT getTimeType(int i) {

		switch (i) {
		case 1:
			return TIME_FORMAT.HHMM_COLON;
		case 2:
			return TIME_FORMAT.HHMMSS_COLON;
		case 3:
			return TIME_FORMAT.HHMM_ACROSS;
		case 4:
			return TIME_FORMAT.HHMMSS_ACROSS;
		}
		return TIME_FORMAT.HHMM_COLON;
	}

	/**
	 * 星期类型枚举的转换
	 * 
	 * @param i
	 * @return
	 */
	public static WEEK_FORMAT getWeekType(int i) {
		switch (i) {
		case 1:
			return WEEK_FORMAT.CHINAFORMAT;
		case 2:
			return WEEK_FORMAT.ENGLISHFORMAT;
		}
		return WEEK_FORMAT.CHINAFORMAT;
	}

	/**
	 * 闪烁类型
	 */
	public static FLICK_TYPE getFlickType(int i) {
		switch (i) {
		case 1:
			return FLICK_TYPE.NO_FLICK;
		case 2:
			return FLICK_TYPE.FLICK_TEXT;
		case 3:
			return FLICK_TYPE.FLICK_STATUS;
		case 4:
			return FLICK_TYPE.FLICK_SHOW;
		}
		return FLICK_TYPE.NO_FLICK;
	}

	/**
	 * 排列状态枚举
	 */
	public static ARRAY_ORDER getArrayOrder(int i) {
		switch (i) {
		case 1:
			return ARRAY_ORDER.LEFT_TO_RIGHT;
		case 2:
			return ARRAY_ORDER.UP_TO_DOWN;
		case 3:
			return ARRAY_ORDER.RIGHT_TO_LEFT;
		case 4:
			return ARRAY_ORDER.DOWN_TO_UP;
		case 5:
			return ARRAY_ORDER.CLOCK_WISE;
		case 6:
			return ARRAY_ORDER.COUNTER_CLOCK_WISE;
		case 7:
			return ARRAY_ORDER.NOT_MOVE;
		case 8:
			return ARRAY_ORDER.OTHER_MOVE_DERECT;
		}
		return ARRAY_ORDER.LEFT_TO_RIGHT;
	}

	public static PLC_RAM_TRANS_TYPE getRecipeTransType(short nDbValue) {
		PLC_RAM_TRANS_TYPE result = PLC_RAM_TRANS_TYPE.OTHER_TRANS_TYPE;
		switch (nDbValue) {
		case 1: {
			result = PLC_RAM_TRANS_TYPE.READ_FROM_PLC;
			break;
		}
		case 2: {
			result = PLC_RAM_TRANS_TYPE.WRITE_TO_PLC;
			break;
		}
		case 3: {
			result = PLC_RAM_TRANS_TYPE.READ_WRITE_PLC;
			break;
		}
		case 4:
		default: {
			result = PLC_RAM_TRANS_TYPE.OTHER_TRANS_TYPE;
			break;
		}
		}

		return result;
	}
 /**
  * 控制点和普通点的转换
  * @param i
  * @return
  */
	public static POINT_TYPE getPointType(int i) {
		switch (i) {
		case 1:
			return POINT_TYPE.GENERALPOINT;
		case 2:
			return POINT_TYPE.CONTROLPOINT;

		}
		return POINT_TYPE.GENERALPOINT;
	}
	/**
	 * 表格单元格类型转换
	 * @param i
	 * @return
	 */
	public static TABLEITEM_TYPE getItemType(int i)
	{
		switch (i) {
		
		case 1:
			return TABLEITEM_TYPE.row;
		case 2:
			return TABLEITEM_TYPE.col;
		}
		return TABLEITEM_TYPE.row;
	}
	/**
	 * 键盘样式属性选择
	 * @param i
	 * @return
	 */
	public static KEY_STYLE getKeyType(int i)
	{
		switch (i) {
		case 0:
			return KEY_STYLE.COLOR_STYLE;
		case 1:
			return KEY_STYLE.PICTURE_STYLE;
		}
		return KEY_STYLE.PICTURE_STYLE;
	}
	/**
	 * 屏保亮度枚举
	 * @param i
	 * @return
	 */
	public static LIGHTENESS getlighteness(int i)
	{
		switch (i) {
		case 1:
			
			return LIGHTENESS.no_light;
		case 2:
			return LIGHTENESS.small_light;
		case 3:
			return LIGHTENESS.middle_light;

		}
		return LIGHTENESS.small_light;
		
	}
	public static int  getBaudrate(int i)
	{
		switch (i) {
		case 1:
			
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD1200;
		case 2:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD2400;
		case 3:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD4800;
		case 4:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD9600;
		case 5:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD19200;
		case 6:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD38400;
		case 7:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD57600;
		case 8:
			return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD115200;
			
		}
		return COM_PORT_PARAM_PROP.BAUD_RATE.BAUD9600;
	}
	/**
	 * 获取表达式的符号枚举
	 * @param i
	 * @return
	 */
	public static EXPRESS_SIGN getExpressSign(int i){
		switch (i) {
		case 1:
			return EXPRESS_SIGN.ADD;
		case 2:
			return EXPRESS_SIGN.REDUCE;
		case 3:
			return EXPRESS_SIGN.MULTIPLY;
		case 4:
			return EXPRESS_SIGN.DIVIDE;
		case 5:
			return EXPRESS_SIGN.XOR;
		case 6:
			return EXPRESS_SIGN.MOD;
		case 7:
			return EXPRESS_SIGN.AND;
		case 8:
			return EXPRESS_SIGN.OR;
		case 9:
			return EXPRESS_SIGN.LEFT;
		case 10:
			return EXPRESS_SIGN.RIGHT;
		default:
			return EXPRESS_SIGN.NONE;
			
		}
	}
	/**
	 * 获取运算数据的类型
	 * @param i
	 * @return
	 */
	public static EXPRESS_NUM_TYPE getExpType(int i){
		switch (i) {
		case 1:
			return EXPRESS_NUM_TYPE.ADDRESS;
		case 2:
			return EXPRESS_NUM_TYPE.CONSTANT;
		default:
			return EXPRESS_NUM_TYPE.CONSTANT;
		}
	}

}
 