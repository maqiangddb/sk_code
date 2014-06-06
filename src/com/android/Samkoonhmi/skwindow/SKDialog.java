package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.WindowInfo;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * 窗口
 * 
 * @author 刘伟江
 * @version v 1.0.0.1
 */
public class SKDialog extends Dialog {

	private Window window = null;
	private boolean show;
	private LinearLayout layout;
	private Context mContext;
	public SKDialog(Context context) {
		super(context, R.style.custom_dialog_style);
		show=false;
		mContext=context;
	}

	public void onCreate(SKScene view, ScenceInfo info) {
		if (show) {
			return;
		}

		if (view != null) {
			boolean draw = false;
			if (info!=null) {
				if (info.isbShowTitle()) {
					view.setTitleName(info.getsTileName());
					draw = true;
				}
				if (info.isbShowShutBtn()) {
					view.setTitleButton(info.isbShowShutBtn());
					draw = true;
				}
			}
			
			if (draw) {
				view.drawTitle();
			}
			
			if (view.getParent()!=null) {
				ViewGroup group=(ViewGroup)view.getParent();
				if (group!=null) {
					group.removeAllViewsInLayout();
				}
			}
		}
		LayoutInflater inflate = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout=(LinearLayout)inflate.inflate(R.layout.dialog_layout,null);
		layout.removeAllViews();
		layout.addView(view);
		setContentView(layout);
	}

	public void showDialog(int x,int y,boolean middle){
		try {
			show=true;
			window=this.getWindow();
			//window.setWindowAnimations(R.style.PopupAnimation);
			if (!middle) {
				window.setGravity(Gravity.LEFT|Gravity.TOP);
				WindowManager.LayoutParams lp=window.getAttributes();
				lp.x=x;
				lp.y=y;
				window.setAttributes(lp);
			}
			show();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SKDialog", "SKDialog show error!!!");
		}
		
	}

	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();
		if (layout!=null) {
			layout.removeAllViews();
		}
		this.dismiss();
	}

	
}
