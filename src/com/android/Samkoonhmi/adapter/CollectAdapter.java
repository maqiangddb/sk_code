package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.sk_historytrends.CollectItem;

public class CollectAdapter extends ArrayAdapter<CollectItem> {
	private static List<CollectItem> data = null;
	private LayoutInflater inflater;

	public CollectAdapter(Context context, List<CollectItem> list) {
		super(context, R.layout.collect_item, list);
		data = list;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return data.size();
	}

	public ArrayList<CollectItem> getList() {
		return (ArrayList<CollectItem>) data;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		HolerView holerView = null;

		if (null == convertView) {
			holerView = new HolerView();
			// 获取listViewItem的布局
			convertView = inflater.inflate(R.layout.collect_item, null);
			holerView.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.item_id);
			holerView.sName = (TextView) convertView
					.findViewById(R.id.item_content);
			holerView.mCheckBox.setOnClickListener(mListener);
			convertView.setTag(holerView);
		} else {
			holerView = (HolerView) convertView.getTag();
		}
		if (null != data) {
			holerView.mCheckBox.setChecked(data.get(position).isCheck);
			holerView.mCheckBox.setTag(position);
			holerView.sName.setText(data.get(position).sGName);
		}
		
		return convertView;
	}
	
	private android.view.View.OnClickListener mListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int tag = (Integer) v.getTag();
			data.get(tag).isCheck= !data.get(tag).isCheck;
		}
	};

	public class HolerView {
		public CheckBox mCheckBox;
		public TextView sName;
	}

}