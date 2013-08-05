package com.android.Samkoonhmi.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 画面适配器
 */
public class ViewAdapter extends BaseAdapter{

	private List<View> views;

	public ViewAdapter(List<View> list) {
		views = list;
	}

	public int getCount() {
		return views.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (views.size()>position) {
			return views.get(position);
		}
		return null;
	}
	

	public void notifyDataSetChanged(List<View> list) {
		// TODO Auto-generated method stub
	
		views = list;
		
		super.notifyDataSetChanged();
	}
}
