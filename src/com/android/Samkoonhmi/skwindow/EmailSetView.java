package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.EmailOperDialog.IClickListener;
import com.android.Samkoonhmi.system.SystemVariable;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class EmailSetView {
	
	private View view;
	private Context mContext;
	private IClickListener mIClickListener = null;
	
	

	public EmailSetView(Context context , IClickListener i){
		mContext = context;
		mIClickListener = i;
		
	}
	
	public View addView(Bundle bundle){
		view = LayoutInflater.from(mContext).inflate(R.layout.email_set, null);
		view.findViewById(R.id.setemail_sure).setOnClickListener(mListener);
		view.findViewById(R.id.setemail_cancel).setOnClickListener(mListener);
		
		
		updateView(bundle);
		
		return view;
	}
	
	// 内部自己调用
	private void updateView(Bundle bundle){
		String from = bundle.getString(EmailOperDialog.SET_FROM);
		if (!TextUtils.isEmpty(from)) {
			((EditText)view.findViewById(R.id.set_emailFrom)).setText(from);
		}
		
		String password = bundle.getString(EmailOperDialog.SET_PASSWORD);
		if (!TextUtils.isEmpty(password)) {
			((EditText)view.findViewById(R.id.set_password)).setText(password);
		}
		
		String server = bundle.getString(EmailOperDialog.SET_SERVER);
		if (!TextUtils.isEmpty(server)) {
			((EditText)view.findViewById(R.id.set_server)).setText(server);
		}
	}
	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.setemail_sure) {
				
				String emailAddress = ((EditText)view.findViewById(R.id.set_emailFrom)).getText().toString();
				String emailPassword = ((EditText)view.findViewById(R.id.set_password)).getText().toString();
				String emailServer = ((EditText)view.findViewById(R.id.set_server)).getText().toString();
				if (!SystemVariable.isEmailNO(emailAddress)) {
					SKToast.makeText(mContext.getString(R.string.email_illegal), Toast.LENGTH_SHORT).show();
					return;
				}
				else if (TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(emailPassword) || TextUtils.isEmpty(emailServer)) {
					SKToast.makeText(mContext.getString(R.string.email_all_info), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Bundle bundle = new Bundle();
				bundle.putString(EmailOperDialog.SET_FROM, emailAddress);
				bundle.putString(EmailOperDialog.SET_PASSWORD, emailPassword);
				bundle.putString(EmailOperDialog.SET_SERVER, emailServer);
			
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, bundle);
			}
			else if (v.getId() == R.id.setemail_cancel) {
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, null);
			}
			
		}
	};
	

}
