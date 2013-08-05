package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;

import com.android.Samkoonhmi.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class SKRecipeSpinner{

	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private View view;
	private boolean isShow;
	private ListView mListView;
	private Context mContext;
	private ArrayList<String> mGroupName;
	private ArrayAdapter mAdapter;
	private LinearLayout layout;
	private ICallGroupId iCallGroupId;
	
	public SKRecipeSpinner(Context context,ArrayList<String> group){
		inflater=LayoutInflater.from(context);
		this.mContext=context;
		mGroupName=new ArrayList<String>();
		if (group!=null) {
			mGroupName=group;
		}
	}
	
	public void initPopWindow(){
		view = inflater.inflate(R.layout.recipeselectlist, null);
		mListView=(ListView)view.findViewById(R.id.listViewrecipe);
		layout=(LinearLayout)view.findViewById(R.id.listLinereci);
		layout.setBackgroundResource(R.drawable.spinner_bg);
		
		mAdapter=new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, mGroupName);
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (iCallGroupId!=null) {
					iCallGroupId.onResult(position,mGroupName.get(position));
				}
				if (mPopupWindow!=null) {
					isShow=false;
					mPopupWindow.dismiss();
				}
			}
		});
		mPopupWindow=new PopupWindow(view,214, 140);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.getContentView().setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mPopupWindow!=null) {
					isShow=false;
					mPopupWindow.dismiss();
				}
				return false;
			}
		});
	}
	
	public void showPopWindow(View view,int x,int y){
		if (!isShow) {
			isShow=true;
			mPopupWindow.setFocusable(true);
			mPopupWindow.setOutsideTouchable(true);
			//mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			mPopupWindow.update();
			mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER_HORIZONTAL, x, y);
		}
	}
	
	public interface ICallGroupId{
		void onResult(int gId,String name);
	}
	
	public void setiCallGroupId(ICallGroupId iCallGroupId) {
		this.iCallGroupId = iCallGroupId;
	}
}
