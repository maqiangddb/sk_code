package com.android.Samkoonhmi.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.activity.LoginActivity;
import com.android.Samkoonhmi.databaseinterface.LockInfoBiz;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.WINDOW_TYPE;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKHistoryTrends.TYPE;
import com.android.Samkoonhmi.skwindow.LoginUserListSpinner;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog.IOperCall;
import com.android.Samkoonhmi.skwindow.SKSwitchOperDialog.IOperCall.CALLTYPE;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.system.SystemControl;
import com.android.Samkoonhmi.system.SystemVariable;
import com.android.Samkoonhmi.system.address.SystemAddress;

public class GlobalPopWindow {
	private View showView;// 显示在哪个View
	private LayoutInflater mInflater;
	private View cView;// 显示的View
	private int windowColor = Color.BLACK;// 窗口的颜色
	private PopupWindow mPopupWindow; // 显示的窗口
	public static boolean popIsShow; // 窗口是否弹出
	private WINDOW_TYPE mWindowType;// 窗口类型
	private int mStartX;
	private int mStartY;
	private int mWidth;
	private int mHeight;
	private String validatePassValue = "";
	private String comfirmStr = "";
	private String screnceIdPath;// 屏保界面Id号图片路径
	private ICallBack callback;
	private final static int LOGIN_WAIT = 0;
	private final static int LOGIN_VALIDATE = 1;
	private final static int LOGIN_SUCCESS = 2;
	private Activity myActivity;
	private String userNameValue;
	private String passwordValue;
	private UserInfo user;
	private boolean isLogin = true;
	private String inputMax;
	private String inputMin;
	private static int NUMBER = 1;
	private static int ASCIITEXT = 2;
	private static int PASSWORD = 3;
	private static int NUMBERPASS = 4; // 数字显示密码形式
	private LoginUserListSpinner mSpinner;
	private IOperCall iOperCall;// 登录，操作通知
	private IOperCall.CALLTYPE eType;

	public void setiOperCall(IOperCall iOperCall,IOperCall.CALLTYPE type) {
		this.iOperCall = iOperCall;
		this.eType=type;
	}

	public String getInputMax() {
		return inputMax;
	}

	public void setInputMax(String inputMax) {
		this.inputMax = inputMax;
	}

	public String getInputMin() {
		return inputMin;
	}

	public void setInputMin(String inputMin) {
		this.inputMin = inputMin;
	}

	/**
	 * 对话框
	 */
	public static AlertDialog dlg = null;

	public ICallBack getCallback() {
		return callback;
	}

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}

	public interface ICallBack {

		void onStart(); // 是否需要启动HmiAcitivity里面的定时器

		void onShow();// 判断窗口是否显示了，在HmiAcitivity 里面 如果窗口已经显示了 ，定时器注销

		void inputFinish(String result);
	};

	/**
	 * 登录窗口构造函数
	 * 
	 * @param view
	 * @param windowType
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 */
	public GlobalPopWindow(View view, WINDOW_TYPE windowType, int startX,
			int startY, int width, int height, Activity activity) {
		this.showView = view;
		this.mWindowType = windowType;
		this.mStartX = startX;
		this.mStartY = startY;
		this.mWidth = width;
		this.mHeight = height;
		this.myActivity = activity;

	}

	/**
	 * 触摸超时的构造函数
	 * 
	 * @param view
	 * @param windowType
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 * @param screenId
	 */
	public GlobalPopWindow(View view, WINDOW_TYPE windowType, int startX,
			int startY, int width, int height, String screnceIdPath, String nul) {
		this.showView = view;
		this.mWindowType = windowType;
		this.mStartX = startX;
		this.mStartY = startY;
		this.mWidth = width;
		this.mHeight = height;
		this.screnceIdPath = screnceIdPath;
	}

	/**
	 * 超出使用时效的构造函数
	 * 
	 * @param view
	 * @param windowType
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 * @param validate
	 */
	public static boolean outTimeWindow = false;

	public GlobalPopWindow(View view, WINDOW_TYPE windowType, int startX,
			int startY, int width, int height, String validate,
			String comfirmStr, String nul) {
		this.showView = view;
		this.mWindowType = windowType;
		this.mStartX = startX;
		this.mStartY = startY;
		this.mWidth = width;
		this.mHeight = height;
		validatePassValue = validate;
		this.comfirmStr = comfirmStr;

	}

	/**
	 * 键盘窗口
	 * 
	 * @param view
	 * @param windowType
	 * @param inputType
	 * @param max
	 * @param min
	 *            inputType 键盘弹出类型 1 数字 2 字母 3 密码
	 */
	private int inputType = 0;
	private DATA_TYPE myDataType = DATA_TYPE.OTHER_DATA_TYPE;

	public GlobalPopWindow(View view, WINDOW_TYPE windowType, int inputType,
			DATA_TYPE dataType) {
		this.showView = view;
		this.mWindowType = windowType;
		this.mStartX = 0;
		this.mStartY = SKSceneManage.nSceneHeight / 2;
		this.mWidth = SKSceneManage.nSceneWidth;
		this.mHeight = SKSceneManage.nSceneHeight / 2;
		// this.inputMax = max;
		// this.inputMin = min;
		this.inputType = inputType;
		this.myDataType = dataType;
	}

	/**
	 * 初始化窗口内容
	 */
	public void initPopupWindow() {
		if (null != showView) {
			mInflater = LayoutInflater.from(showView.getContext());
			switch (mWindowType) {
			case LOGIN:// 登录窗口
				loginPop();
				break;
			case SCREENSAVER:// 屏保窗口
				screencePop();
				break;
			case OUTTIME:// 超出时效窗口
				outTimePop();
				break;
			case LOCK:// 锁屏
				lockPop();
				break;
			case KEYBOARD: // 键盘窗口
				openKeyBoard();
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 登录窗口
	 */
	// 用户名适配器
	private ArrayAdapter<String> userNameAdapter;
	private EditText userView = null;

	private void loginPop() {
		isLogin = true;
		cView = mInflater.inflate(R.layout.windowlogin, null);
		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, mWidth, mHeight);
		userView = (EditText) cView.findViewById(R.id.spinner_userList);
		final EditText passWord = (EditText) cView.findViewById(R.id.passWord);
		passWord.setFocusable(true);
		Button loginButton = (Button) cView.findViewById(R.id.login);
		Button cancelButton = (Button) cView.findViewById(R.id.cancel);
		cancelButton.setVisibility(View.VISIBLE);
		String defaultUserName = "";
		if (null != SystemInfo.getDefaultUser()) {
			defaultUserName = SystemInfo.getDefaultUser().getName();
		}
		if (SystemInfo.getUserNameList() != null
				&& SystemInfo.getUserNameList().size() > 0) {
			defaultUserName = SystemInfo.getUserNameList().get(0);
		}
		userNameValue = defaultUserName;
		userView.setText(defaultUserName);
		// passWord.setText(defaultUserPass);
		userView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;
				int textViewWidth = userView.getWidth();
				if (mSpinner == null) {
					mSpinner = new LoginUserListSpinner(SKSceneManage
							.getInstance().mContext, SystemInfo
							.getUserNameList(), textViewWidth);
					mSpinner.initPopWindow();
				}
				mSpinner.showPopWindow(userView, 0, 32);
				mSpinner.setiCallGroupId(userCall);
			}
		});

		// 登录按钮事件
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time = 0;
				// Log.d("user", "isLogin:" + isLogin);
				if (isLogin) {
					isLogin = false;
					// TODO Auto-generated method stub
					// userNameValue = userName.getText().toString().trim();
					passwordValue = passWord.getText().toString().trim();
					boolean bool = invalidateValue(userNameValue, passwordValue);
					if (bool) {
						myHandler.sendEmptyMessage(LOGIN_WAIT);

					}
				} else {
					SKToast.makeText(myActivity, R.string.islogin,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time = 0;
				// TODO Auto-generated method stub
				if (v.getId() == R.id.cancel) {
					if (dlg != null) {
						dlg.dismiss();
					}

					mPopupWindow.dismiss();
					popIsShow = false;
					callback.onStart();
					if (iOperCall!=null) {
						if (eType==null||eType==CALLTYPE.OPER) {
							iOperCall.onCancel();
						}else {
							iOperCall.onStartMacro(MotionEvent.ACTION_DOWN);
						}
					}
				}
			}
		});

	}

	private LoginUserListSpinner.ICallUserName userCall = new LoginUserListSpinner.ICallUserName() {

		@Override
		public void onResult(String userName) {
			// TODO Auto-generated method stub
			if (null != userView)
				userView.setText(userName);
			userNameValue = userName;

		}

	};

	/**
	 * 屏保窗口
	 */
	private void screencePop() {
		cView = mInflater.inflate(R.layout.scrence, null);
		// 设置窗口的颜色
		// cView.setBackgroundColor(Color.YELLOW);
		ImageView image = (ImageView) cView.findViewById(R.id.outImage);
		Bitmap bm = ImageFileTool.getBitmap(SystemInfo.getsScreenIndex());
		if (null != bm) {
			image.setImageBitmap(bm);
		}
		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, mWidth, mHeight);
		// 做一个不在焦点外的处理事件监听
		mPopupWindow.getContentView().setOnTouchListener(
				new View.OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						mPopupWindow.setFocusable(false);
						mPopupWindow.dismiss();
						popIsShow = false;
						callback.onStart();
						//Log.d("PopWindow", "show..........");
						SystemVariable.getInstance().writeBitAddr(0, SystemAddress.getInstance().SceneSaver());
						return true;
					}
				});

	}

	/**
	 * 超出时效窗口
	 */
	private void outTimePop() {
		// Log.d("pass", "要填写的密码 窗口：：" + validatePassValue);
		cView = mInflater.inflate(R.layout.outtime, null);
		// 设置窗口的颜色
		cView.setBackgroundColor(windowColor);
		TextView showText = (TextView) cView.findViewById(R.id.showText);// 显示出来的字
		showText.setText(comfirmStr);
		final EditText validatePass = (EditText) cView
				.findViewById(R.id.validateText);
		Button button = (Button) cView.findViewById(R.id.validatePass);
		// button 按键处理事件
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time = 0;
				if (!validatePassValue.equals(validatePass.getText().toString()
						.trim())) {
					SKToast.makeText(showView.getContext(),
							R.string.validatewrong, Toast.LENGTH_SHORT).show();
				} else {
					// 关闭窗口，继续使用
					// 重新设置使用天数
					if (LoginActivity.readPassCount < SystemInfo.getPassWord()
							.size()) {
						// 将已用过的密码标记设为true
						SystemInfo.getPassWord()
								.get(LoginActivity.readPassCount).setUser(true);
						SystemInfoBiz sysBiz = new SystemInfoBiz();
						boolean modifyBoo = sysBiz.updatePassUse(SystemInfo
								.getPassWord().get(LoginActivity.readPassCount)
								.getId());
					}
					if (SystemInfo.isbProtectType() == false) {
						SharedPreferences.Editor shareEditor = showView
								.getContext()
								.getSharedPreferences("hmiprotct", 0).edit();
						SharedPreferences sharedPreferences = showView
								.getContext().getSharedPreferences("hmiprotct",
										0);
						int passIndex = sharedPreferences
								.getInt("passIndex", 0);
						Date date = new Date();
						SimpleDateFormat format = new SimpleDateFormat(
								"yyyy/MM/dd");
						String writeDate = format.format(date);
						shareEditor.putString("dateTime", writeDate);
						shareEditor.putInt("dateNumber", 0);
						shareEditor.putInt("passIndex", (passIndex + 1));
						shareEditor.commit();
						passIndex = sharedPreferences.getInt("passIndex", 0);
					}
					// 重新设置系统参数中的密码实体
					LoginActivity.readPassCount = LoginActivity.readPassCount + 1;
					if (LoginActivity.readPassCount + 1 > SystemInfo
							.getPassWord().size()) {
						SystemInfo.setOnePassWord(null);
					} else {
						SystemInfo.setOnePassWord(SystemInfo.getPassWord().get(
								LoginActivity.readPassCount));
					}
					SKSceneManage.getInstance().setbHmiLock(false);
					mPopupWindow.dismiss();
					popIsShow = false;
					outTimeWindow = false;// 时效窗口已经关闭
					callback.onStart();
				}

			}
		});

		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, mWidth, mHeight);
	}

	public void lockPop() {

		// Log.d("pass", "要填写的密码 窗口：：" + validatePassValue);
		cView = mInflater.inflate(R.layout.outtime, null);
		// 设置窗口的颜色
		cView.setBackgroundColor(windowColor);
		TextView showText = (TextView) cView.findViewById(R.id.showText);// 显示出来的字
		showText.setText(comfirmStr);
		final EditText validatePass = (EditText) cView
				.findViewById(R.id.validateText);
		Button button = (Button) cView.findViewById(R.id.validatePass);
		// button 按键处理事件
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SKSceneManage.getInstance().time = 0;
				if (!validatePassValue.equals(validatePass.getText().toString()
						.trim())) {
					SKToast.makeText(showView.getContext(),
							R.string.validatewrong, Toast.LENGTH_SHORT).show();
				} else {

					// 设置解锁状态
					LockInfoBiz.getInstance().SetbIsLock(false);

					// 重新设置系统参数中的密码实体
					// 这里
					if (LoginActivity.readPassCount + 1 > SystemInfo
							.getPassWord().size()) {
						SystemInfo.setOnePassWord(null);
					} else {
						SystemInfo.setOnePassWord(SystemInfo.getPassWord().get(
								LoginActivity.readPassCount));
					}
					mPopupWindow.dismiss();
					popIsShow = false;
				}

			}
		});

		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, mWidth, mHeight);
	}

	/**
	 * 打开键盘窗口
	 */
	static String inputText = "";

	public void openKeyBoard() {
		final InputMethodManager imm = (InputMethodManager) SKSceneManage
				.getInstance()
				.getActivity()
				.getSystemService(
						SKSceneManage.getInstance().getActivity().INPUT_METHOD_SERVICE);
		SKSceneManage
				.getInstance()
				.getActivity()
				.getWindow()
				.setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 显示软键盘
		// TODO Auto-generated method stub
		cView = mInflater.inflate(R.layout.popkeyboard, null);
		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, mWidth, mHeight);
		final EditText editText = (EditText) cView.findViewById(R.id.edit);
		// editText.setText(getTextValue());
		editText.setWidth(0);
		if (inputType == NUMBER) {
			if (myDataType == DATA_TYPE.HEX_16
					|| myDataType == DATA_TYPE.HEX_32) {
				editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			} else {
				editText.setInputType((EditorInfo.TYPE_CLASS_PHONE));
			}

		} else if (inputType == ASCIITEXT) {
			editText.setInputType(InputType.TYPE_CLASS_TEXT);
		} else if (inputType == PASSWORD) {
			editText.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else if (inputType == NUMBERPASS) {
			if (myDataType == DATA_TYPE.HEX_16
					|| myDataType == DATA_TYPE.HEX_32) {
				editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			} else {
				editText.setInputType(EditorInfo.TYPE_CLASS_PHONE
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}

		editText.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == 66 && event.getAction() == KeyEvent.ACTION_DOWN) {

					inputText = editText.getText().toString().trim();
					// 检出输入是否合法
					inputIsError(editText, callback);
					mPopupWindow.dismiss();
					popIsShow = false;
					callback.onStart();

				} else if (keyCode == KeyEvent.KEYCODE_BACK) {
					mPopupWindow.dismiss();
					popIsShow = false;
					callback.onStart();
				}
				return false;

			}
		});

	}

	/**
	 * 检查输入是否合法
	 * 
	 * @param editText
	 * @param callback
	 */
	private void inputIsError(EditText editText, ICallBack callback) {
		boolean isNumber = isNumeric(inputText);
		// 是数值输入器
		if (inputType == NUMBER) {
			// 输入表示数字
			if (isNumber) {
				double d = 0;
				String showMin = String.valueOf(getInputMin());
				String showMax = String.valueOf(getInputMax());
				try {
					if (myDataType == DATA_TYPE.HEX_16) {
						d = Integer.valueOf(inputText, 16);
						showMin = Integer.toHexString(Integer
								.valueOf(getInputMin()));
						showMax = Integer.toHexString(Integer
								.valueOf(getInputMax()));
					} else if (myDataType == DATA_TYPE.HEX_32) {

						d = Long.valueOf(inputText, 16);
						showMax = Long.toHexString(Long.valueOf(getInputMax()));// 将最大最小值转成16进制格式进行提示
						showMin = Long.toHexString(Long.valueOf(getInputMin()));
					} else {
						d = Double.parseDouble(inputText);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				if (d > Double.valueOf(getInputMax())
						|| d < Double.valueOf(getInputMin())) {
					SKToast.makeText(showView.getContext(),
							"请输入  " + showMin + " ~ " + showMax + " 之间的数值",
							Toast.LENGTH_SHORT).show();

					editText.setText("");

				} else {
					callback.inputFinish(inputText);
				}
			} else { // 输入非数字，判断是否是16进制
				if (myDataType == DATA_TYPE.HEX_16) {
					String hex16StringMax = Integer.toHexString(Integer
							.valueOf(getInputMax()));// 将最大最小值转成16进制格式进行提示
					String hex16StringMin = Integer.toHexString(Integer
							.valueOf(getInputMin()));
					try {
						int inputTextHex = Integer.valueOf(inputText, 16); // 将输入的字符转成10进制数进行大小比较
						if (inputTextHex > Double.valueOf(getInputMax())
								|| inputTextHex < Double.valueOf(getInputMin())) {
							SKToast.makeText(
									showView.getContext(),
									"请输入  " + hex16StringMin + " ~ "
											+ hex16StringMax + " 之间的数值",
									Toast.LENGTH_SHORT).show();
							editText.setText("");
						} else {
							callback.inputFinish(inputText);
						}
					} catch (Exception e) {
						// TODO: handle exception

						SKToast.makeText(
								showView.getContext(),
								"请输入  " + hex16StringMin + " ~ "
										+ hex16StringMax + " 之间的数值",
								Toast.LENGTH_SHORT).show();
						editText.setText("");
						// e.printStackTrace();
					}
				} else if (myDataType == DATA_TYPE.HEX_32) {
					String hex32StringMax = Long.toHexString(Long
							.valueOf(getInputMax()));// 将最大最小值转成16进制格式进行提示
					String hex32StringMin = Long.toHexString(Long
							.valueOf(getInputMin()));
					try {
						long inputTextHex = Long.valueOf(inputText, 16);// 将输入的字符转成10进制数进行大小比较
						if (inputTextHex > Double.valueOf(getInputMax())
								|| inputTextHex < Double.valueOf(getInputMin())) {

							SKToast.makeText(
									showView.getContext(),
									"请输入  " + hex32StringMin + " ~ "
											+ hex32StringMax + " 之间的数值",
									Toast.LENGTH_SHORT).show();
							editText.setText("");
						} else {
							callback.inputFinish(inputText);
						}
					} catch (Exception e) {
						SKToast.makeText(
								showView.getContext(),
								"请输入  " + hex32StringMin + " ~ "
										+ hex32StringMax + " 之间的数值",
								Toast.LENGTH_SHORT).show();
						editText.setText("");

						// e.printStackTrace();
					}
				} else { // 如果是数值输入器 ，输入的非数字，又不是16进制 ，则输入非法
					SKToast.makeText(
							showView.getContext(),
							"请输入  " + getInputMin() + " ~ " + getInputMax()
									+ " 之间的数值", Toast.LENGTH_SHORT).show();
					editText.setText("");
				}
			}
		} else { // 非数值输入器
			if (!inputText.equals("")) {
				int inputTextLength = inputText.length();
				if (inputTextLength > Double.valueOf(getInputMax())) {
					SKToast.makeText(
							showView.getContext(),
							"输入的字符数不能超过" + Integer.valueOf(getInputMax()) + "个",
							Toast.LENGTH_SHORT).show();
				} else {
					callback.inputFinish(inputText);
				}
			} else {
				callback.inputFinish(inputText);
			}

		}

	}

	/**
	 * 判断是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern
				.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$|^-\\d+\\.\\d+$");
		if (null == str || "".equals(str)) {
			return false;
		}

		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 显示下拉窗口的位置
	 */
	public void showPopupWindow() {
		// Log.d("SKScene", "showPopupWindow" + ",showView:" + showView);
		try {
			
			popIsShow = true;
			if (callback != null) {
				callback.onShow();
			}
			if (null == mPopupWindow) {
				initPopupWindow();
			}
			if (null != mPopupWindow) {
				if (WINDOW_TYPE.SCREENSAVER == mWindowType) {
					mPopupWindow.setFocusable(false);
				} else {
					mPopupWindow.setFocusable(true);
				}
				mPopupWindow.update();
				// Log.d("SKScene", "showView::" + showView);

				mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
				if (null != showView && null != mPopupWindow && SKProgress.onResume) {
					mPopupWindow.showAtLocation(showView, Gravity.CENTER, 0, 0);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			popIsShow = false;
		}

	}

	public void closePop() {
		try {
			if (popIsShow && null != mPopupWindow) {
				mPopupWindow.dismiss();
				popIsShow = false;
				if (callback != null) {
					callback.onStart();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("GlobalPopWindow", "ak closePop error !");
		}
		
	}

	/**
	 * 关闭窗口
	 */
	public void closePopWindow() {
		try {
			if (popIsShow && null != mPopupWindow) {
				mPopupWindow.dismiss();
				popIsShow = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("GlobalPopWindow", "ak closePopWindow error!");
		}
	}

	/**
	 * 验证用户名或密码是否为空
	 * 
	 * @param userNameValue
	 * @param passwordValue
	 * @return
	 */
	private boolean invalidateValue(String userNameValue, String passwordValue) {
		if ("".equals(userNameValue) || null == userNameValue) {
			SKToast.makeText(showView.getContext(), R.string.userisnull,
					Toast.LENGTH_LONG).show();
			isLogin = true;
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 处理登录的消息
	 */
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN_WAIT:
				showDialog(true, "正在验证，请稍后！");
				myHandler.sendEmptyMessageAtTime(LOGIN_VALIDATE, 10);
				break;
			case LOGIN_VALIDATE:
				validateFromBase(myActivity);
				break;
			case LOGIN_SUCCESS:
				if (dlg != null) {
					dlg.dismiss();
				}

				mPopupWindow.dismiss();
				popIsShow = false;
				// 切换用户，更新界面显示和触控
				SKSceneManage.getInstance().updateState();
				callback.onStart();
				if (iOperCall != null) {
					if (eType==null||eType==CALLTYPE.OPER) {
						iOperCall.onConfirm(MotionEvent.ACTION_DOWN);
					}else {
						iOperCall.onStartMacro(MotionEvent.ACTION_DOWN);
					}
					
				}
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 验证登录数据
	 * 
	 * @param activity
	 * @param userNameValue
	 * @param passwordValue
	 * @param user
	 */
	public void validateFromBase(Context context) {
		boolean userIsNull = ParameterSet.getInstance().getUserFromBase(
				userNameValue, passwordValue);
		if (userIsNull) {
			myHandler.sendEmptyMessage(LOGIN_SUCCESS);
		} else {

			SKToast.makeText(context, R.string.namepasswrong,Toast.LENGTH_SHORT).show();
			if (null != dlg) {
				dlg.dismiss();
			}
			isLogin = true;
		}
	}

	/**
	 * 显示等待对话框
	 * 
	 * @param isShow
	 * @param message
	 *            提示信息
	 */
	public void showDialog(boolean isShow, String message) {

		try {
			if (isShow) {
				dlg = new AlertDialog.Builder(SKSceneManage.getInstance()
						.getActivity()).create();
				dlg.show();
				LinearLayout layout = (LinearLayout) myActivity.getLayoutInflater()
						.inflate(R.layout.hold_alert, null);
				if (null != message) {
					TextView textView = (TextView) layout
							.findViewById(R.id.hold_title);
					textView.setText(message);
				}
				dlg.getWindow().setContentView(layout);
			} else {
				if (dlg != null) {
					dlg.dismiss();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("GlobalPopWindow", "ak showDialog error! ");
		}
	}

}
