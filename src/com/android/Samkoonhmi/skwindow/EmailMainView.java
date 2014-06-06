package com.android.Samkoonhmi.skwindow;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.EmailOperDialog.IClickListener;
import com.android.Samkoonhmi.vnc.VNCUtil;

public class EmailMainView {
	private Context mContext;
	private View view;
	private IClickListener mIClickListener = null;
	
	
	public EmailMainView(Context context, IClickListener i){
		mIClickListener = i;
		mContext = context;
	}
	
	public View addView(Bundle bundle ){
		view  = LayoutInflater.from(mContext).inflate(R.layout.emailoper_dialog, null);
		view.findViewById(R.id.email_set).setOnClickListener(listener);
		view.findViewById(R.id.email_add).setOnClickListener(listener);
		view.findViewById(R.id.email_reduce).setOnClickListener(listener);
		view.findViewById(R.id.email_addfile).setOnClickListener(listener);
		view.findViewById(R.id.email_send).setOnClickListener(listener);
		view.findViewById(R.id.email_exit).setOnClickListener(listener);
		
		updateView(bundle);
		return view; 
	}
	
	public void updateView(Bundle bundle){
		String from = bundle.getString(EmailOperDialog.SET_FROM);
		((TextView)view.findViewById(R.id.email_from)).setText(from);
		
		String to = bundle.getString(EmailOperDialog.SET_TO);
		((TextView)view.findViewById(R.id.email_to)).setText(to);
		
	}
	
	/**
	 * 使界面点击失效果
	 */
	private void setViewUnClick(){
		view.findViewById(R.id.email_set).setEnabled(false);
		view.findViewById(R.id.email_add).setEnabled(false);
		view.findViewById(R.id.email_reduce).setEnabled(false);
		view.findViewById(R.id.email_addfile).setEnabled(false);
		view.findViewById(R.id.email_send).setEnabled(false);
		view.findViewById(R.id.email_exit).setEnabled(false);
		view.findViewById(R.id.email_from).setEnabled(false);
		view.findViewById(R.id.email_content).setEnabled(false);
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.email_set) {//设置
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_SETVIEW, null);
			}
			else if (v.getId() == R.id.email_add) {//添加联系人
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_ADDVIEW, null);
			}
			else if (v.getId() == R.id.email_reduce) {//删除联系人
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_DELVIEW, null);
			}
			else if (v.getId() == R.id.email_addfile) {//添加附件
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_FILEVIEW, null);
			}
			else if (v.getId() == R.id.email_exit) {
				mIClickListener.onExit();
			}
			else if (v.getId() == R.id.email_send) {//发送
				if (!VNCUtil.isNetworkAvailable(mContext)) {//没有网络
					SKToast.makeText(mContext.getString(R.string.email_no_net), Toast.LENGTH_SHORT).show();
					return ;
				}
				String from = ((TextView)view.findViewById(R.id.email_from)).getText().toString();
				String to  = ((TextView)view.findViewById(R.id.email_to)).getText().toString();
				String content = ((EditText)view.findViewById(R.id.email_content)).getText().toString();
				if (TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
					SKToast.makeText(mContext.getString(R.string.email_all_info), Toast.LENGTH_SHORT).show();
					return ;
				}
				//防止用户多次点击
				setViewUnClick();
				mIClickListener.sendEmail(content);
				
			}
		}
	};

}
