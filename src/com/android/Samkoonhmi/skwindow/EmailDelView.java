package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.AlarmDateAdapter;
import com.android.Samkoonhmi.skwindow.EmailOperDialog.IClickListener;

public class EmailDelView {
	private LayoutInflater inflater;
	private View view;
	private IClickListener mIClickListener = null;
	private CheckBox checkBox = null;
	private ListView listView = null;
	private AlarmDateAdapter mAdapter = null;
	private ArrayList<String> mToList = new ArrayList<String>();

	
	public EmailDelView(Context context , IClickListener i){
		inflater = LayoutInflater.from(context);
		mIClickListener = i;
		mAdapter = new AlarmDateAdapter(context);
	}
	
	public View addView(Bundle bundle){
		view = inflater.inflate(R.layout.email_delto, null);
		checkBox = (CheckBox) view.findViewById(R.id.mailcbx_all);
		checkBox.setOnCheckedChangeListener(mCheckedChangeListener);
		view.findViewById(R.id.email_del_sure).setOnClickListener(mListener);
		view.findViewById(R.id.email_del_cancel).setOnClickListener(mListener);
		listView = (ListView) view.findViewById(R.id.email_del_list);
		updateView(bundle);
		return view;
	}
	
	public void updateView(Bundle bundle){
		
		String toList = bundle.getString(EmailOperDialog.SET_TO);
		if (!TextUtils.isEmpty(toList)) {
			listView.setVisibility(View.VISIBLE);
			String []array =toList.split(",");
			mToList.clear();
			for(int i = 0; i < array.length; i++){
				mToList.add(array[i]);
			}
			
			mAdapter.setEmailToList(mToList);
			listView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}
		else {
			listView.setVisibility(View.INVISIBLE);
			checkBox.setChecked(false);
		}
		
		
	}
	

	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId() == R.id.email_del_sure ) {
				if (mToList.size() > 0) {
					Bundle bundle = new Bundle();
					bundle.putString(EmailOperDialog.SET_TO, updateEmailTo());
					bundle.putBoolean(EmailOperDialog.FROM_DEL, true);
					mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, bundle);
				}
				else {
					mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, null);
				}
			}
			else if (v.getId() == R.id.email_del_cancel) {
				mIClickListener.onJumpTo(EmailOperDialog.DIALOG_MAINVIEW, null);
			}
			
		}
	};
	
	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			for(int i = 0 ; i < mToList.size(); i++){
				mAdapter.setCheckState(i, isChecked);
			}
			mAdapter.notifyDataSetChanged();
		}
	};
	
	private String updateEmailTo(){
		String updateString = "";
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.addAll(mToList);
		for(int i = 0; i < mToList.size(); i++){
			if (mAdapter.getCheckState(i)) {
				String text = mToList.get(i);
				tempList.remove(text);
			}
		}
		if (tempList.size() == 1) {
			updateString = tempList.get(0);
		}
		else {
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < tempList.size(); i++){
				if (i != tempList.size() -1) {
					buffer.append(tempList.get(i)).append(",");
				}
				else {
					buffer.append(tempList.get(i));
				}
				updateString = buffer.toString();
			}
		}
	
		return updateString;
		
	}
	
}
