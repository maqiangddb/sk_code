package com.android.Samkoonhmi.skwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.databaseinterface.UserInfoBiz;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.model.UserInfo;
import com.android.Samkoonhmi.util.ParameterSet;


/**
 * 修改当前用户密码
 * 和注销当前用户
 */
public class SKUserOperDialog {

	private View view;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private Context mContext;
	public boolean isShow;
	private Button mBtnOk;
	private Button mBtnCancel;
	private EditText mName;//用户名
	private EditText mOldPwd;//旧密码
	private EditText mNewPwd;//新密码
	private EditText mConfirmPwd;//确定密码
	private EditText mInfo;//描述
	private TextView mUserName;//注销-当前用户名
	private int nType;
	
	private IOperCall iOperCall;
	
	public SKUserOperDialog(Context context){
		mContext=context;
		inflater=LayoutInflater.from(context);
	}
	
	/**
	 * 初始化
	 * @param type-0 修改用户密码
	 * @param type-1 注销当前用户
	 */
	private boolean initPopWindow(int type,int width,int height){
		nType=type;
		isShow=false;
		if (type==0) {
			view = inflater.inflate(R.layout.edit_user_info, null);
			mName=(EditText)view.findViewById(R.id.edit_name);
			mOldPwd=(EditText)view.findViewById(R.id.edit_old_pwd);
			mNewPwd=(EditText)view.findViewById(R.id.edit_pwd);
			mConfirmPwd=(EditText)view.findViewById(R.id.edit_pwd_confirm);
			mInfo=(EditText)view.findViewById(R.id.edit_user_message);
			UserInfo info=SystemInfo.getGloableUser();
			if (info==null||info.getName()==null||info.getName().equals("")
					||info.getName().equals(" ")) {
				SKToast.makeText(mContext.getString(R.string.no_user_login), Toast.LENGTH_LONG).show();
				return false;
				
			}else {
				mName.setText(info.getName());
			}
			
			mBtnOk=(Button)view.findViewById(R.id.edit_ok);
			mBtnCancel=(Button)view.findViewById(R.id.edit_cancel);
			mBtnOk.setOnClickListener(listener);
			mBtnCancel.setOnClickListener(listener);
		}else if (type==1) {
			view = inflater.inflate(R.layout.logout_user, null);
			mUserName=(TextView)view.findViewById(R.id.txt_user_name);
			UserInfo info=SystemInfo.getGloableUser();
			if (info==null||info.getName()==null||info.getName().equals("")
					||info.getName().equals(" ")) {
				SKToast.makeText(mContext.getString(R.string.no_user_login), Toast.LENGTH_LONG).show();
				return false;
				
			}else {
				mUserName.setText(info.getName());
			}
			mBtnOk=(Button)view.findViewById(R.id.edit_ok);
			mBtnCancel=(Button)view.findViewById(R.id.edit_cancel);
			mBtnOk.setOnClickListener(listener);
			mBtnCancel.setOnClickListener(listener);
		}
		mPopupWindow=new PopupWindow(view,width, height);
		return true;
	}
	
	/**
	 * 显示
	 */
	public void showPopWindow(int type,int width,int height){
		if(isShow){
			return;
		}
		boolean result=true;
		if (mPopupWindow==null) {
			result=initPopWindow(type,width, height);
		}
		if (!result) {
			return;
		}
		
		isShow=true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER, 0, 0);
	}
	
	View.OnClickListener listener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SKSceneManage.getInstance().time=0;
			if(v.equals(mBtnOk)){
				//确定
				if (nType==0) {
					editUser();
				}else {
					//注销用户
					if (mPopupWindow!=null) {
						mPopupWindow.dismiss();
						isShow=false;
					}
					ParameterSet.getInstance().outTimeLogout();
					SKSceneManage.getInstance().updateState();
				}
			}else if (v.equals(mBtnCancel)) {
				//取消
				if (iOperCall!=null) {
					iOperCall.onCancel();
				}
				if (mPopupWindow!=null) {
					mPopupWindow.dismiss();
					isShow=false;
				}
			}
		}
	};
	
	/**
	 * 修改用户信息
	 */
	private void editUser(){
		UserInfoBiz biz=new UserInfoBiz();
		UserInfo info=SystemInfo.getGloableUser();
		if (info==null) {
			return;
		}
		String oldPwd=mOldPwd.getText().toString();
		if (!info.getPassword().equals(oldPwd)) {
			// 用户名已经存在
			Toast.makeText(mContext, R.string.hint_pwd_error, Toast.LENGTH_SHORT).show();
			return ;
		} 
		if (!mNewPwd.getText().toString().equals("")) {
			// 如果密码不为空
			if (mConfirmPwd.getText().toString().equals("")) {
				// 确定密码为空
				Toast.makeText(mContext, R.string.hint_pwds_entry,
						Toast.LENGTH_SHORT).show();
				return ;
			} else if (!mNewPwd.getText().toString().trim()
					.equals(mConfirmPwd.getText().toString().trim())) {
				// 密码不一致
				Toast.makeText(mContext, R.string.hint_pwd_repeat,
						Toast.LENGTH_SHORT).show();
				return ;
			}
		}
		
		UserInfo temp=new UserInfo();
		temp.setName(info.getName());
		temp.setPassword(mNewPwd.getText().toString()+"");
		temp.setDescript(mInfo.getText().toString()+"");
		biz.updateUser(temp);
		
		Toast.makeText(mContext, R.string.hint_update_succeed,
				Toast.LENGTH_SHORT).show();
		
		if (mPopupWindow!=null) {
			mPopupWindow.dismiss();
			isShow=false;
		}
	}
	
	
	/**
	 * 关闭对话框
	 */
	public void hidePopWindow(){
		if(mPopupWindow!=null){
			mPopupWindow.dismiss();
			isShow=false;
		}
	}
	
	/**
	 * 操作确定回调接口
	 */
	public interface IOperCall{
		//确定
		void onConfirm();
		//取消
		void onCancel();
	}
	
	public void setiOperCall(IOperCall iOperCall) {
		this.iOperCall = iOperCall;
	}

}
