package com.android.Samkoonhmi.adapter;


import java.util.ArrayList;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.skwindow.DateTimeSetting.Item;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DateSettingAdapter extends ArrayAdapter<Item>{

	private ArrayList<Item> items;
	private LayoutInflater inflater;
	
	public DateSettingAdapter(Context context,ArrayList<Item> item) {
		super(context, R.layout.date_setting_item, item);
		items=item;
		inflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HodlerView holerView=null;
		if (convertView==null) {
			holerView=new HodlerView();
			convertView=inflater.inflate(R.layout.date_setting_item, null);
			holerView.title=(TextView)convertView.findViewById(R.id.item_title);
			holerView.content=(TextView)convertView.findViewById(R.id.item_content);
			convertView.setTag(holerView);
		}else {
			holerView=(HodlerView)convertView.getTag();
		}
		
		holerView.title.setText(items.get(position).sTitle);
		holerView.content.setText(items.get(position).sContent);
		
		return convertView;
	}
	
	public class HodlerView{
		TextView title;
		TextView content;
	}
}
