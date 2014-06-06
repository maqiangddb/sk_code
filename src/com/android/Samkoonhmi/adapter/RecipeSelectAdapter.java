package com.android.Samkoonhmi.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.RecipeSelectInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skwindow.SKSceneManage;
import com.android.Samkoonhmi.util.TextAlignUtil;

public class RecipeSelectAdapter extends ArrayAdapter<RecipeOprop> {
	private static List<RecipeOprop> data = null;
	private LayoutInflater inflater;
	private RecipeSelectInfo info;

	public RecipeSelectAdapter(Context context, List<RecipeOprop> list,
			RecipeSelectInfo info) {
		super(context, R.layout.comboxlistitem, list);
		data = list;
		inflater = LayoutInflater.from(context);
		this.info = info;
	}

	public int getCount() {
		return data.size();
	}

	public ArrayList<RecipeOprop> getList() {
		return (ArrayList<RecipeOprop>) data;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		HolerView holerView = null;

		if (null == convertView) {
			holerView = new HolerView();
			// 获取listViewItem的布局
			// convertView = inflater.inflate(R.layout.recipeitem, null);
			convertView = LayoutInflater.from(
					SKSceneManage.getInstance().mContext).inflate(
					R.layout.recipeitem, null);
			holerView.recipeName = (TextView) convertView
					.findViewById(R.id.recipeName);
			convertView.setTag(holerView);
		} else {
			holerView = (HolerView) convertView.getTag();
		}
		if (null != data) {
			if (!data.isEmpty()) {
				holerView.recipeName.setTextColor(info.getnTextColor());
				holerView.recipeName.setTextSize(info.getnFontSize());
				RecipeOprop reci = data.get(position);
				if (null != reci) {
					Vector<String> names = reci.getsRecipeName();
					if (null != names) {
						if (!names.isEmpty()) {
							if (SystemInfo.getCurrentLanguageId() < names
									.size()) {
								holerView.recipeName
										.setText(names.get(SystemInfo
												.getCurrentLanguageId()));
							}else{
								holerView.recipeName
								.setText(names.get(0));
							}
						}
					}
				}
//				Typeface typeFace = Typeface.create(info.getsFontFamily(),
//						Typeface.NORMAL);
				Typeface typeFace = TextAlignUtil.getTypeFace(info.getsFontFamily());
				holerView.recipeName.setTypeface(typeFace);
			}
		}
		return convertView;
	}

	public class HolerView {
		TextView recipeName;
	}

}