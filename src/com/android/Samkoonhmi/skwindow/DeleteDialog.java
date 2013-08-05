package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 删除提示框
 */
public class DeleteDialog {

	private Context mContext;
	private LayoutInflater inflater;
	private View view;
	private PopupWindow mPopupWindow;
	public boolean showFlag=false;
	private Button btnOk;
	private Button btnCancel;
	private TextView txtMsg;
	private IDeleteListener deleteListener;
	private int id;
	
	public DeleteDialog(Context context){
		this.mContext=context;
		inflater=LayoutInflater.from(mContext);
	}
	
	/**
	 * 初始化pop
	 */
	public void initPopWindow(){
		if (view==null) {
			showFlag=false;
			view=inflater.inflate(R.layout.recipe_delete_view,null);
			view.setOnClickListener(listener);
			btnOk=(Button)view.findViewById(R.id.delete_ok);
			btnOk.setOnClickListener(listener);
			
			btnCancel=(Button)view.findViewById(R.id.delete_cancel);
			btnCancel.setOnClickListener(listener);
			
			txtMsg=(TextView)view.findViewById(R.id.delete_hint);
			
			mPopupWindow=new PopupWindow(view,250, 120);
		}
	}
	
	/**
	 * 显示pop
	 */
	public void showPopWindow(int rid,int id){
		if(showFlag==true)
		{
			return ;
		}
		showFlag = true;
		if(null ==mPopupWindow)
		{
			initPopWindow();
		}
		this.id=id;
		txtMsg.setText(this.mContext.getResources().getString(rid));
		mPopupWindow.setFocusable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER,0,0);
	}
	
	/**
	 * 点击事件
	 */
	View.OnClickListener listener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			SKSceneManage.getInstance().time=0;
			
			if (v.equals(btnOk)) {
				if(mPopupWindow!=null){
					showFlag=false;
					mPopupWindow.dismiss();
					if(deleteListener!=null){
						deleteListener.onDelete(id);
					}
				}
			}else if (v.equals(btnCancel)) {
				if(mPopupWindow!=null){
					showFlag=false;
					mPopupWindow.dismiss();
				}
			}
		}
	};
	
	public void destory(){
		if(showFlag){
			if (mPopupWindow!=null) {
				mPopupWindow.dismiss();
				showFlag=false;
			}
		}
	}
	
	public interface IDeleteListener{
		void onDelete(int id);
	}
	
	public void setDeleteListener(IDeleteListener deleteListener) {
		this.deleteListener = deleteListener;
	}

}
