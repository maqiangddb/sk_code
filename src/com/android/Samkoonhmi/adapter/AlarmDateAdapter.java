package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.alarm.AlarmGroupInfo;

public class AlarmDateAdapter extends BaseAdapter{
	
	private ArrayList<AlarmGroupInfo> list = null;
	private LayoutInflater inflater;

	
	public AlarmDateAdapter(ArrayList<AlarmGroupInfo> infoList, Context cont)
	{
		list = infoList;
		inflater = LayoutInflater.from(cont);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
			
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.contentView.setText(list.get(position).getsName());
		holder.nGroupId = list.get(position).getnGroupId();
		return convertView;
	}
	
	public class ViewHolder
	{
		public CheckBox checkView;
		public TextView contentView;
		public int nGroupId;
	}


}
