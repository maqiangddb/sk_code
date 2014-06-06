package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.EidtUserView.CHANG_TYPE;
import com.android.Samkoonhmi.skwindow.EidtUserView.IClickListener;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ViewFlipper;

public class SKEditUserDialog implements IClickListener {
	private PopupWindow window;
	private LayoutInflater inflater;
	private View view;
	private ViewFlipper flipper;
	private EidtUserView eView;
	private AddUserView aView;
	private int nWidth;
	private int nHeigth;
	public boolean show;
	private Context mcontext;
	
	public SKEditUserDialog(Context context,Activity activity){
		mcontext = context;
		getViewMeasure();
		eView=new EidtUserView(activity, this);
		aView=new AddUserView(activity, this);
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.edit_user_viewgroup, null);
		flipper = (ViewFlipper) view.findViewById(R.id.user_view_flipper);
		flipper.addView(eView.addView(R.layout.eidt_user_view,nWidth,nHeigth));
		flipper.addView(aView.addView(R.layout.add_user_view,nWidth,nHeigth));
		window = new PopupWindow(view,nWidth,nHeigth);
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
	
	public boolean isShow(){
		return show;
	}
	
	public void show(){
		if(window==null){
			return;
		}
		View parent = SKSceneManage.getInstance().getCurrentScene();
		if(parent == null){
			return;
		}
		window.setFocusable(true);
		window.update();
		window.showAtLocation(parent, Gravity.CENTER, 0, 0);
		show=true;
	}

	@Override
	public void onExit() {
		show=false;
		if(window==null){
			return;
		}
		window.dismiss();
	}

	@Override
	public void onPre(CHANG_TYPE type, boolean result) {
		// 切换到编辑界面
		if (flipper != null) {
			flipper.setInAnimation(mcontext, R.anim.dialog_left_enter);
			flipper.setOutAnimation(mcontext, R.anim.dialog_left_out);
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
	public void onNext(CHANG_TYPE type, boolean result) {
		// 切换到添加or更新用户界面
		if (flipper != null) {
			flipper.setInAnimation(mcontext, R.anim.dialog_right_enter);
			flipper.setOutAnimation(mcontext, R.anim.dialog_right_out);
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
