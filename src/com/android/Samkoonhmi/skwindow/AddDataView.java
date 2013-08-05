package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * 表格添加一行
 */
public class AddDataView extends LinearLayout{

	private int nWidth;
	private int nHeight;
	private int nItemCount;
	private Context mContext;
	private ArrayList<EditText> mList;
	private LinearLayout mBottomLayout;
	
	public AddDataView(Context context,int width,int height,int count) {
		super(context);
		this.nWidth=width;
		this.nHeight=height;
		this.nItemCount=count;
		this.mContext=context;
		mList=new ArrayList<EditText>();
		mBottomLayout=new LinearLayout(mContext);
		mBottomLayout.setLayoutParams(new LayoutParams(width, height));
		addItem();
	}
	
	private void addItem(){
		int width=nWidth/(nItemCount+1);
		for (int i = 0; i <= nItemCount; i++) {
			EditText item=new EditText(mContext);
			item.setLines(1);
			item.setLayoutParams(new LayoutParams(width, nHeight));
			item.setPadding(0, 0, 0, 0);
			mList.add(item);
			mBottomLayout.addView(item);
		}
		addView(mBottomLayout);
	}
	
	
	
	@Override
	public void setOrientation(int orientation) {
		super.setOrientation(LinearLayout.VERTICAL);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mBottomLayout!=null) {
			mBottomLayout.layout(0, 0, nWidth, nHeight);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(MeasureSpec.makeMeasureSpec(nWidth, MeasureSpec.AT_MOST),
				MeasureSpec.makeMeasureSpec(nHeight, MeasureSpec.AT_MOST));
	}

	public ArrayList<EditText> getmList() {
		return mList;
	}
}
