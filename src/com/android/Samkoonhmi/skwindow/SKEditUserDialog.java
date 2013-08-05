package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.EidtUserView.CHANG_TYPE;
import com.android.Samkoonhmi.skwindow.EidtUserView.IClickListener;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ViewFlipper;

/**
 * 添加用户对话框
 * 
 * @author 刘伟江 创建时间 2012-7-14
 */
public class SKEditUserDialog extends Dialog implements IClickListener {

	private Window window = null;
	private ViewFlipper flipper;
	private Activity activity;
	private EidtUserView eView;
	private AddUserView aView;
	private int nWidth;
	private int nHeigth;
	public boolean show;

	public SKEditUserDialog(Activity activity) {
		super(activity);
		this.activity = activity;
		show=false;
		nWidth=800;
		nHeigth=480;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_user_viewgroup);
		eView=new EidtUserView(activity, this);
		aView=new AddUserView(activity, this);
		
		getViewMeasure();
		flipper = (ViewFlipper) findViewById(R.id.user_view_flipper);
		flipper.addView(eView.addView(R.layout.eidt_user_view,nWidth,nHeigth));
		flipper.addView(aView.addView(R.layout.add_user_view,nWidth,nHeigth));
	}

	private void getViewMeasure(){
		int width=SKSceneManage.nSceneWidth;
		int height=SKSceneManage.nSceneHeight;
		
		if (width<600) {
			nWidth=width;
		}else if (width*3/4>=600) {
			nWidth=600;
		}else {
			nWidth=width*3/4;
		}
		
		if (height<360) {
			nHeigth=height;
		}else if (height*3/4>=360) {
			nHeigth=360;
		}else {
			nHeigth=height*3/4;
		}
	}
	
	public void showDialog() {
		if (show) {
			return;
		}
		show=true;
		window = getWindow();
		window.setWindowAnimations(R.style.PopupAnimation);
		WindowManager.LayoutParams lp = window.getAttributes();
		//lp.width = 600;
		//lp.height = 300;
		window.setAttributes(lp);
		show();
	}
	
	/**
	 * 加载当前用户权限
	 * 当dialog 不是创建时调用
	 */
	public void loadData(){
		if (aView!=null) {
			aView.updateData();
		}
		if (eView!=null) {
			eView.updateData();
		}
	}

	@Override
	public void onExit() {
		dismiss();
		show=false;
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		show=false;
	}

	@Override
	public void onPre(CHANG_TYPE type,boolean result) {
		// 切换到编辑界面
		if (flipper != null) {
			flipper.setInAnimation(activity, R.anim.dialog_left_enter);
			flipper.setOutAnimation(activity, R.anim.dialog_left_out);
			flipper.showPrevious();
			//从编辑界面进入添加or更新界面
			if (type==CHANG_TYPE.EDIT_TO_ADD||type==CHANG_TYPE.EDIT_TO_UPDATE) {
				aView.setClick();
				aView.setType(type,eView.getnCurrentId());
			}
			
			//从添加or更新界面进入编辑界面
			if (type==CHANG_TYPE.ADD_TO_EDIT||type==CHANG_TYPE.UPDATE_TO_EDIT) {
				eView.setClick();
				eView.update(result,false);
			}
		}
	}

	@Override
	public void onNext(CHANG_TYPE type,boolean result) {
		// 切换到添加or更新用户界面
		if (flipper != null) {
			flipper.setInAnimation(activity, R.anim.dialog_right_enter);
			flipper.setOutAnimation(activity, R.anim.dialog_right_out);
			flipper.showPrevious();
			//从编辑界面进入添加or更新界面
			if (type==CHANG_TYPE.EDIT_TO_ADD||type==CHANG_TYPE.EDIT_TO_UPDATE) {
				aView.setClick();
				aView.setType(type,eView.getnCurrentId());
			}
			
			//从添加or更新界面进入编辑界面
            if (type==CHANG_TYPE.ADD_TO_EDIT||type==CHANG_TYPE.UPDATE_TO_EDIT) {
            	eView.setClick();
            	eView.update(result,false);
			}
		}
	}

}
