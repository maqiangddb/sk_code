package com.android.Samkoonhmi.skwindow;

import java.util.regex.Pattern;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.network.TcpServerManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class IPSet {

	private View view;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	public boolean isShow;
	private Button mBtnOk;
	private Button mBtnCancel;
	private CheckBox mCheckBox;
	private EditText mPort;
	private Context mContext;
	private String port="";
	private boolean bStartServer=false;
	

	public IPSet(Context context) {
		mContext=context;
		inflater = LayoutInflater.from(context);
		readInfo();
	}

	/**
	 * 初始化
	 */
	private void initPopWindow() {
		isShow = false;
		view = inflater.inflate(R.layout.ipset, null);
		mBtnOk = (Button) view.findViewById(R.id.btn_ok);
		mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
		mBtnOk.setOnClickListener(listener);
		mBtnCancel.setOnClickListener(listener);
			
		mPort=(EditText)view.findViewById(R.id.port);
		mCheckBox=(CheckBox)view.findViewById(R.id.cxb_server);
		mCheckBox.setChecked(bStartServer);
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				bStartServer=isChecked;
			}
		});
		
		mPopupWindow = new PopupWindow(view, 300, 160);
	}

	/**
	 * 显示
	 */
	public void showPopWindow() {
		if (isShow) {
			return;
		}
		if (mPopupWindow == null) {
			initPopWindow();
		}

		mPort.setText(port);
		
		isShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance()
				.getCurrentScene(), Gravity.CENTER, 0, 0);
	}
	
	
	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			SKSceneManage.getInstance().time = 0;
			if (v.equals(mBtnOk)) {
				// 确定
				
				port=mPort.getText().toString();
				if (port==null||port.equals("")) {
					SKToast.makeText(mContext, "端口填写错误", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!isPosInt(port, "端口填写错误")) {
					return;
				}
				
				if (mPopupWindow != null) {
					mPopupWindow.dismiss();
					isShow = false;
				}

				saveInfo();
				
				if (bStartServer) {
					TcpServerManager.getInstance().onStart(mContext);
				}else {
					TcpServerManager.getInstance().onStop();
				}
				
				
			} else if (v.equals(mBtnCancel)) {
				// 取消

				if (mPopupWindow != null) {
					mPopupWindow.dismiss();
					isShow = false;
				}
			}
		}
	};
	
	private void readInfo(){
		SharedPreferences share = mContext.getSharedPreferences("information", 0);
		bStartServer=share.getBoolean("net_server", false);
		port=share.getString("net_port", "5566");
		
	}
	
	private void saveInfo(){
		SharedPreferences.Editor mEditor = mContext.getSharedPreferences(
				"information", 0).edit();
		mEditor.putBoolean("net_server", bStartServer);
		mEditor.putString("net_port", port);
		
		mEditor.commit();
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

}
