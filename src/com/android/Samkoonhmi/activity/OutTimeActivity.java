package com.android.Samkoonhmi.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.model.PassWordInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.skwindow.SKToast;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 超出时效界面
 * 
 * @author Administrator
 * 
 */
public class OutTimeActivity extends Activity implements OnClickListener {
	private Button validateButton;
	private EditText validatePass;
	private String validatePassValue = "";
	private TextView showTextView;
	private String confirmStr = ""; // 超时提示字符串
	private SystemInfoBiz sysBiz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.outtime);
		if (null != SystemInfo.getOnePassWord()) {
			validatePassValue = SystemInfo.getOnePassWord().getsPwdStr();
			confirmStr = SystemInfo.getOnePassWord().getsTimeOut();
		}

		// Log.d("pass", "activity要输入的密码：" + validatePassValue);
		showTextView = (TextView) findViewById(R.id.showText);
		showTextView.setText(confirmStr);
		validateButton = (Button) findViewById(R.id.validatePass);
		validatePass = (EditText) findViewById(R.id.validateText);
		validateButton.setOnClickListener(this);
		sysBiz = new SystemInfoBiz();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		if (v.getId() == R.id.validatePass) {
			invalidate();
		}

	}

	private void invalidate() {
		if (!validatePassValue.equals(validatePass.getText().toString().trim())) {
			SKToast.makeText(this, R.string.validatewrong, Toast.LENGTH_SHORT)
					.show();
		} else {

			if(LoginActivity.readPassCount<SystemInfo.getPassWord().size() ){
				// 将已用过的密码标记设为true
				SystemInfo.getPassWord().get(LoginActivity.readPassCount).setUser(true);

				boolean modifyBoo = sysBiz.updatePassUse(SystemInfo.getPassWord()
						.get(LoginActivity.readPassCount).getId());
			}
			
			// 重新设置使用天数
			if (SystemInfo.isbProtectType() == false) {
				SharedPreferences.Editor shareEditor = getSharedPreferences(
						"hmiprotct", 0).edit();
				SharedPreferences sharedPreferences = getSharedPreferences(
						"hmiprotct", 0);
				int passIndex = sharedPreferences.getInt("passIndex", 0);
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				String writeDate = format.format(date);
				shareEditor.putString("dateTime", writeDate);
				shareEditor.putInt("dateNumber", 0);
				shareEditor.putInt("passIndex", (passIndex + 1));
				shareEditor.commit();

				passIndex = sharedPreferences.getInt("passIndex", 0);
			}
			// 重新设置系统参数中的密码实体
			LoginActivity.readPassCount = LoginActivity.readPassCount + 1;
			if (LoginActivity.readPassCount + 1 > SystemInfo.getPassWord()
					.size()) {
				SystemInfo.setOnePassWord(null);
			} else {
				SystemInfo.setOnePassWord(SystemInfo.getPassWord().get(
						LoginActivity.readPassCount));
			}

			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}

	}

}
