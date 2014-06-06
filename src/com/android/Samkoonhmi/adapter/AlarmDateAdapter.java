package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;
import com.android.Samkoonhmi.model.skglobalcmn.HistoryDataCollect;

public class AlarmDateAdapter extends BaseAdapter{
	
	private ArrayList<AlarmGroupInfo> alarmlist = null;
	private Vector<RecipeOGprop> recipelist = null;
	private ArrayList<String> mEmailToList = null;
	private Vector<HistoryDataCollect> mHistoryList = null; 
	private LayoutInflater inflater;
	private ArrayList<Boolean> mCheckList = new ArrayList<Boolean>();//使用list防止 刷新混乱
	
	private int CURRENT_LIST = 1;;
	private final int ALARM_LIST = 1;
	private final int RECIPE_LIST = 2;
	private final int EMAIL_DEL =3;
	private final int HIS_LIST = 4;
	

	public AlarmDateAdapter(Context cont)
	{
		inflater = LayoutInflater.from(cont);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0 ;
		if (CURRENT_LIST == ALARM_LIST) {
			size = alarmlist.size();
		}
		else if (CURRENT_LIST == RECIPE_LIST) {
			size = recipelist.size();
		}
		else if (CURRENT_LIST == EMAIL_DEL) {
			size = mEmailToList.size();
		}
		else if (CURRENT_LIST == HIS_LIST) {
			size = mHistoryList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (CURRENT_LIST == ALARM_LIST) {
			return alarmlist.get(position);
		}
		else if (CURRENT_LIST == RECIPE_LIST) {
			return recipelist.get(position);
		}
		else if (CURRENT_LIST == EMAIL_DEL) {
			return mEmailToList.get(position);
		}
		else if (CURRENT_LIST == HIS_LIST) {
			return mHistoryList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView =  inflater.inflate(R.layout.edit_alarm_item, null);
			holder.checkView = (CheckBox) convertView.findViewById(R.id.alarm_checkbox);
			holder.contentView =  (TextView) convertView.findViewById(R.id.item_message);
			holder.checkView.setOnClickListener(mListener);
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (CURRENT_LIST == ALARM_LIST) {
			holder.contentView.setText(alarmlist.get(position).getsName());
			holder.nGroupId = alarmlist.get(position).getnGroupId();
		}
		else if (CURRENT_LIST == RECIPE_LIST) {
			holder.contentView.setText(recipelist.get(position).getsRecipeGName());
			holder.nGroupId = recipelist.get(position).getnGRecipeID();
		}
		else if(CURRENT_LIST == EMAIL_DEL){
			holder.contentView.setText(mEmailToList.get(position));
			holder.nGroupId = position;
		}
		else if (CURRENT_LIST == HIS_LIST) {
			holder.contentView.setText(mHistoryList.get(position).getsName());
			holder.nGroupId = mHistoryList.get(position).getnGroupId();
		}
		
		holder.checkView.setChecked(mCheckList.get(position));
		holder.checkView.setTag(position);
		
		return convertView;
	}
	
	private OnClickListener mListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int tag = (Integer) v.getTag();
			mCheckList.set(tag, !mCheckList.get(tag));
			
		}
	};
	
	public class ViewHolder
	{
		public CheckBox checkView;
		public TextView contentView;
		public int nGroupId;
	}
	
	public void setAlarmList(ArrayList<AlarmGroupInfo>list){
		mCheckList.clear();
		for(int i = 0; i < list.size(); i++){
			mCheckList.add(false);
		}
		alarmlist = list;
		CURRENT_LIST = ALARM_LIST;
	}
	
	public void setRecipeList(Vector<RecipeOGprop> list){
		mCheckList.clear();
		for(int i = 0; i < list.size(); i++){
			mCheckList.add(false);
		}
		recipelist = list;
		CURRENT_LIST = RECIPE_LIST;
	}
	
	public void setEmailToList(ArrayList<String> list){
		mCheckList.clear();
		for(int i = 0; i < list.size(); i++){
			mCheckList.add(false);
		}
		mEmailToList = list;
		CURRENT_LIST = EMAIL_DEL;
	}
	
	public void setHistoryList(Vector<HistoryDataCollect>list){
		mCheckList.clear();
		for(int i = 0; i < list.size(); i++){
			mCheckList.add(false);
		}
		mHistoryList = list;
		CURRENT_LIST =  HIS_LIST;
	}
	
	public void setCheckState(int index, boolean state){
		mCheckList.set(index, state);
	}
	
	public boolean getCheckState(int index){
		return mCheckList.get(index);
	}
	
	/**
	 * 返回选中配方组
	 * @return
	 */
	public ArrayList<Integer> getSelectRecipeList(){
		ArrayList<Integer> selectList = new ArrayList<Integer>();
		for(int i = 0; i < mCheckList.size(); i++){
			if (mCheckList.get(i)) {
				selectList.add(recipelist.get(i).getnGRecipeID());
			}
			
		}
		return selectList;
	}
	
	
	/**
	 * 返回选中的历史数组
	 * @return
	 */
	public ArrayList<Integer> getHistoryList(){
		ArrayList<Integer> selectList = new ArrayList<Integer>();
		for(int i = 0; i < mCheckList.size(); i++){
			if (mCheckList.get(i)) {
				Integer value = (int)mHistoryList.get(i).getnGroupId();
				selectList.add(value);
			}
			
		}
		return selectList;
	}
	
	
	public ArrayList<Integer> getAlarmList(){
		ArrayList<Integer> selectList = new ArrayList<Integer>();
		for(int i = 0; i < mCheckList.size(); i++){
			if (mCheckList.get(i)) {
				Integer value = (int)alarmlist.get(i).getnGroupId();
				selectList.add(value);
			}
			
		}
		return selectList;
	}


}
