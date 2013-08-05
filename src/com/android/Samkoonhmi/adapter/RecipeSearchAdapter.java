package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.RecipectItemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;

public class RecipeSearchAdapter extends ArrayAdapter<RecipectItemInfo> {
	private static List<RecipectItemInfo> data = null;
	private LayoutInflater inflater;
	//private RecipectItemInfo info;

	public RecipeSearchAdapter(Context context, List<RecipectItemInfo> list,
			RecipectItemInfo info) {
		super(context, R.layout.comboxlistitem, list);
		data = list;
		inflater = LayoutInflater.from(context);
	//	this.info = info;
	}

	public int getCount() {
		return data.size();
	}

	public ArrayList<RecipectItemInfo> getList() {
		return (ArrayList<RecipectItemInfo>) data;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		HolerView holerView = null;

		if (null == convertView) {
			holerView = new HolerView();
			// 获取listViewItem的布局
			convertView = LayoutInflater.from(
					SKSceneManage.getInstance().mContext).inflate(
					R.layout.comboxlistitem, null);
			holerView.functionName = (TextView) convertView
					.findViewById(R.id.functionName);
			holerView.functionName.setTextColor(Color.BLACK);
			holerView.radioButton = (ImageView) convertView
					.findViewById(R.id.radio);
			convertView.setTag(holerView);
		} else {
			holerView = (HolerView) convertView.getTag();
		}
		if (null != data && !data.isEmpty()) {
			// 根据选中的值确定图片
			if (data.get(position).isChecked() == true) {
				holerView.radioButton
						.setBackgroundResource(R.drawable.btn_check_on);
			} else {
				holerView.radioButton
						.setBackgroundResource(R.drawable.btn_check_off);
			}
			RecipectItemInfo itemInfo = data.get(position);
			if (null != itemInfo) {
				
					holerView.functionName.setText(itemInfo.getsRecipeName());
				}

		}
		return convertView;
	}


	public class HolerView {
		TextView functionName;
		ImageView radioButton;
	}

}
