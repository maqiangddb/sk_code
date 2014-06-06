package com.android.Samkoonhmi.skwindow;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.EmailOperDialog.IClickListener;
import com.android.Samkoonhmi.system.SystemVariable;

public class EmailAddView {
	private View view;
	private Context mContext;
	private IClickListener mIClickListener = null;
	
	private String  mEmailto = null;
	
	public EmailAddView(Context context, IClickListener i){
		mContext = context;
		mIClickListener = i;
		
	}
	
	public View addView(Bundle bundle){
		view  = LayoutInflater.from(mContext).inflate(R.layout.email_add, null);
		view.findViewById(R.id.setemail_sure).setOnClickListener(mListener);
		view.findViewById(R.id.setemail_cancel).setOnClickListener(mListener);
		
		updateView(bundle);
		return view;
	}
	
	public void updateView(Bundle bundle){
		mEmailto = bundle.getString(EmailOperDialog.SET_TO);
	}
	
	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.setemail_sure) {
				String to = ((EditText)(view.findViewById(R.id.email_add_email))).getText().toString();
				if (!SystemVariable.isEmailNO(to)) {
					SKToast.makeText(mContext.getString(R.string.email_illegal), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Bundle bundle = new Bundle();
				if (TextUtils.isEmpty(mEmailto)) {
					mEmailto = to;
				}
				else {
					mEmailto = to + "," + mEmailto;
				}
				bundle.putString(EmailOperDialog.SET_TO, mEmailto);
				
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, bundle);
			}
			else if(v.getId() == R.id.setemail_cancel){
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, null);
			}
			
		}
	};

}
