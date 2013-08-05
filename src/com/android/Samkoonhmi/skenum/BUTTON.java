package com.android.Samkoonhmi.skenum;


public class BUTTON {

	/**
	 * 画面开关按钮功能枚举
	 */
	public enum BUTTON_TYPE {
		BIT,      //位开关
		WORD,     //字开关
		SCENE,    //画面开关
		PECULIAR, //特殊开关
		BIT_LIGHT ;//位指示灯
	}
	
	/**
	 * 位按钮开关功能枚举
	 */
	public enum BIT_OPER_TYPE {

		SET_BIT(1), //置位
		RESET(2),   //复位
		REPLACE(3), //交替
		JOG(4);    //点动
		
		int value;
		BIT_OPER_TYPE(int value){
			this.value=value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	/**
	 * 字开关功能枚举
	 */
	public enum WORD_OPER_TYPE {
		INPUT_VALUE(1),  //输入数值
		ADD(2),          //加
		MINUS(3),        //减
		ADD_LOOPER(4),   //位加
		MINUS_LOOPER(5); //位减
		
		int value;
		WORD_OPER_TYPE(int value){
			this.value=value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	/**
	 * 开关按钮 ,监视类型
	 */
	public enum WATCH_TYPE {
		NONE(1),     //不监视
		DOUBLE(2),   //双态
		POLY(3);     //多态
		
		int value;
		WATCH_TYPE(int value){
			this.value=value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	/**
	 * 特殊开关按钮,功能枚举
	 */
	public enum PECULIAR_TYPE {

		BACKLIGHT(1),       //背景灯开关
		RESET(2),           //系统重启
		SET_SCENE_TIME(3),  //设置屏保时间
		SET_SYSTEM_TIME(4), //设置系统时间
		CHANGE_USER(5),     //切换当前用户
		USER_MANAGE(6),     //用户管理
		TOUCH_SOUND(7),     //触摸声音开关
		COPYRIGHT(8),       //产品授权
		CHANGE_LANGUAGE(9), //语言切换
		SCREENSHOT(10),     //屏幕截图
		OPER_WINDOW(11),    //窗口操作
		SAMPLING(12),       //采样数据操作
		FORMULA(13),        //配方操作
		ALARM(14),          //报警操作
		EDIT_USER_PWD(15),  //修改用户密码
		LOGOUT_USER(16),    //注销用户  
		IP_SET(17),         //IP设置
		ZOOM_IN(18),      //放大
		REDUCE(19),       //缩小
		WIFI(20);
		
		int value;
		PECULIAR_TYPE(int value){
			this.value=value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	/**
	 * 界面开关枚举
	 */
	public enum OPER_SCENE {

		NEXT,        //跳转到下一个画面
		OPEN,        //打开指定画面
		OPEN_WINDOW, //打开窗口
		COSE_WINDOW, //关闭窗口
		BACK;        //返回之前画面
	}
	
	/**
	 * 特殊按钮，操作类型
	 */
	public enum PECULIAR_OPER{
		OPEN_WINDOW(1),          //打开窗口
		CLOSE_WINDOW(2),         //关闭窗口
		//CLEAR_SAMPLING(3),       //清除采样数据
		EDIT_FORMULA(3),         //编辑当前配方
		ADD_FORMULA(4),          //新建配方
		DELETE_FORMULA(5),       //删除当前配方
		DELETE_ALL_FORMULA(6),   //删除全组配方
		WRITE_FORMULA(7),        //当前配方写入PLC
		READ_FORMULA(8),         //从PLC读取当前配方
		EXPORT_FORMULA(9),      //配方组导出为文件
		INTO_FORMULA(10),        //文件导入为配方组
		SAVE_CURRENT_FORMULA(11),//保存当前配方
		SAVE_FORMULA(14),        //保存当前配方
		ALRAM_SWITCH(15),        //报警声音开关
		CONFIRM_ALRAM(16),       //确定报警
		CLEAR_ALRAM(17),         //清除报警
		CLEAR_HISTORY_ALRAM(18), //清除历史报警数据
		EXPORT_FILE(19);         //导出历史数据
		
		int value;
		PECULIAR_OPER(int value){
			this.value=value;
		}
		
		public int getValue(){
			return value;
		}
	}
	
	/**
	 * 按钮监视类型,转换
	 */
	public static WATCH_TYPE getWatchType(int i){
		switch (i) {
		case 1:
			return WATCH_TYPE.NONE;
		case 2:
			return WATCH_TYPE.DOUBLE;
		case 3:
			return WATCH_TYPE.POLY;
		}
		return WATCH_TYPE.NONE;
	}
	
	/**
	 * 位按钮功能,转换
	 */
	public static BIT_OPER_TYPE getBitOperType(int i){
		switch (i) {
		case 1:
			return BIT_OPER_TYPE.SET_BIT;
		case 2:
			return BIT_OPER_TYPE.RESET;
		case 3:
			return BIT_OPER_TYPE.REPLACE;
		case 4:
			return BIT_OPER_TYPE.JOG;
		}
		return BIT_OPER_TYPE.SET_BIT;
	}

	/**
	 * 字按钮功能,转换
	 */
	public static WORD_OPER_TYPE getWordOperType(int i){
		switch (i) {
		case 1:
			return WORD_OPER_TYPE.INPUT_VALUE;
		case 2:
			return WORD_OPER_TYPE.ADD;
		case 3:
			return WORD_OPER_TYPE.MINUS;
		case 4:
			return WORD_OPER_TYPE.ADD_LOOPER;
		case 5:
			return WORD_OPER_TYPE.MINUS_LOOPER;
		}
		return WORD_OPER_TYPE.INPUT_VALUE;
	}

	/**
	 * 画面按钮功能,转换
	 */
	public static OPER_SCENE getOPerScene(int i){
		switch (i) {
		case 1:
			return OPER_SCENE.NEXT;
		case 2:
			return OPER_SCENE.OPEN;
		case 3:
			return OPER_SCENE.OPEN_WINDOW;
		case 4:
			return OPER_SCENE.COSE_WINDOW;
		case 5:
			return OPER_SCENE.BACK;
		}
		return OPER_SCENE.NEXT;
	}

	/**
	 * 特殊按钮功能,转换
	 */
	public static PECULIAR_TYPE getPeculiarType(int i){
		switch (i) {
		case 1:
			return PECULIAR_TYPE.BACKLIGHT;
		case 2:
			return PECULIAR_TYPE.RESET;
		case 3:
			return PECULIAR_TYPE.SET_SCENE_TIME;
		case 4:
			return PECULIAR_TYPE.SET_SYSTEM_TIME;
		case 5:
			return PECULIAR_TYPE.CHANGE_USER;
		case 6:
			return PECULIAR_TYPE.USER_MANAGE;
		case 7:
			return PECULIAR_TYPE.TOUCH_SOUND;
		case 8:
			return PECULIAR_TYPE.COPYRIGHT;
		case 9:
			return PECULIAR_TYPE.CHANGE_LANGUAGE;
		case 10:
			return PECULIAR_TYPE.SCREENSHOT;
		case 11:
			return PECULIAR_TYPE.OPER_WINDOW;
		case 12:
			return PECULIAR_TYPE.SAMPLING;
		case 13:
			return PECULIAR_TYPE.FORMULA;
		case 14:
			return PECULIAR_TYPE.ALARM;
		case 15:
			return PECULIAR_TYPE.EDIT_USER_PWD;
		case 16:
			return PECULIAR_TYPE.LOGOUT_USER;
		case 17:
			return PECULIAR_TYPE.IP_SET;
		case 18:
			return PECULIAR_TYPE.ZOOM_IN;
		case 19:
			return PECULIAR_TYPE.REDUCE;
		case 20:
			return PECULIAR_TYPE.WIFI;
		}
		return PECULIAR_TYPE.BACKLIGHT;
	}

	/**
	 * 特殊按钮操作,转换
	 */
	public static PECULIAR_OPER getPeculiarOper(int i){
		switch (i) {
		case 1:
			return PECULIAR_OPER.OPEN_WINDOW;
		case 2:
			return PECULIAR_OPER.CLOSE_WINDOW;
//		case 3:
//			return PECULIAR_OPER.CLEAR_SAMPLING;
		case 3:
			return PECULIAR_OPER.EDIT_FORMULA;
		case 4:
			return PECULIAR_OPER.ADD_FORMULA;
		case 5:
			return PECULIAR_OPER.DELETE_FORMULA;
		case 6:
			return PECULIAR_OPER.DELETE_ALL_FORMULA;
		case 7:
			return PECULIAR_OPER.WRITE_FORMULA;
		case 8:
			return PECULIAR_OPER.READ_FORMULA;
		case 9:
			return PECULIAR_OPER.EXPORT_FORMULA;
		case 10:
			return PECULIAR_OPER.INTO_FORMULA;
		case 11:
			return PECULIAR_OPER.SAVE_CURRENT_FORMULA;
		case 14:
			return PECULIAR_OPER.SAVE_FORMULA;
		case 15:
			return PECULIAR_OPER.ALRAM_SWITCH;
		case 16:
			return PECULIAR_OPER.CONFIRM_ALRAM;
		case 17:
			return PECULIAR_OPER.CLEAR_ALRAM;
		case 18:
			return PECULIAR_OPER.CLEAR_HISTORY_ALRAM;
		case 19:
			return PECULIAR_OPER.EXPORT_FILE;
		}
		return PECULIAR_OPER.OPEN_WINDOW;
	}
}
