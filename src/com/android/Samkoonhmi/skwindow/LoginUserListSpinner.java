package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import java.util.List;

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

public class LoginUserListSpinner{

	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private View view;
	public static  boolean LoginUserListSpinnerisShow = false;
	private ListView mListView;
	private Context mContext;
	private List<String> mUserNameList;
	private ArrayAdapter mAdapter;
	private LinearLayout layout;
	private ICallUserName userNameCall;
	private int myTextViewWidth = 300;
	
	public LoginUserListSpinner(Context context,List<String> group,int textViewWidth){
		this.myTextViewWidth = textViewWidth;
		inflater=LayoutInflater.from(context);
		this.mContext=context;
		mUserNameList=new ArrayList<String>();
		if (group!=null) {
			mUserNameList=group;
		}
	}
	
	public void initPopWindow(){
		view = inflater.inflate(R.layout.userlist, null);
		mListView=(ListView)view.findViewById(R.id.listViewUser);
		layout=(LinearLayout)view.findViewById(R.id.userlistLine);
		layout.setBackgroundResource(R.drawable.spinner_bg);
		
		mAdapter=new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, mUserNameList);
		
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (userNameCall!=null) {
					userNameCall.onResult(mUserNameList.get(position));
				}
				if (mPopupWindow!=null) {
					LoginUserListSpinnerisShow=false;
					mPopupWindow.dismiss();
				}
			}
		});
		mPopupWindow=new PopupWindow(view,myTextViewWidth+18, 140);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.getContentView().setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mPopupWindow!=null) {
					LoginUserListSpinnerisShow=false;
					mPopupWindow.dismiss();
				}
				return false;
			}
		});
	}
	
	public void showPopWindow(View view,int x,int y){
		if (!LoginUserListSpinnerisShow) {
			LoginUserListSpinnerisShow=true;
			mPopupWindow.setFocusable(true);
			mPopupWindow.setOutsideTouchable(true);
			//mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			mPopupWindow.update();
			mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER_HORIZONTAL, x, y);
		}
	}
	
	public interface ICallUserName{
		void onResult(String userName);
	}
	
	public void setiCallGroupId(ICallUserName UserName) {
		this.userNameCall = UserName;
	}
}
