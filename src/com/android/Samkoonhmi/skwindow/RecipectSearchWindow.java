package com.android.Samkoonhmi.skwindow;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.adapter.RecipeSearchAdapter;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.RecipectItemInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.util.ParameterSet;

public class RecipectSearchWindow implements OnClickListener {
	private LayoutInflater mInflater;
	private View cView;
	private ListView listView;
	private LinearLayout setLine;
	private Button setButton;
	private PopupWindow mPopupWindow;
	private List<RecipectItemInfo> data;
	private RecipeSearchAdapter adapter;
	private int nLeftPX;
	private int nLeftPY;
	private int nWidth;
	private int nHeight;
	private RecipectItemInfo info;
	private boolean popIsShow;
	private EditText searchName;
	private int nGid;
	private int index;

	// private List<recipeOprop> tempList;

	public RecipectSearchWindow(List<RecipeOprop> dataList,int Gid) {
        setnGid(Gid);
		if (null != dataList) {
			// tempList = new ArrayList<recipeOprop>();
			data = new ArrayList<RecipectItemInfo>();
			for (int i = 0; i < dataList.size(); i++) {
				RecipectItemInfo info = new RecipectItemInfo();
				info.setChecked(false);
				RecipeOprop recip = dataList.get(i);
				info.setnGroupId(getnGid());
				info.setnRecipeId(recip.getnRecipeId());
				info.setsRecipeName(recip.getsRecipeName().get(
						SystemInfo.getCurrentLanguageId()));
				data.add(info);
				// tempList.add(recip);
			}
		}
		index = -1;
	}

	public void initPopupWindow() {
		// 获取窗口的位置
		getPosition();
		mInflater = LayoutInflater.from(SKSceneManage.getInstance()
				.getCurrentScene().getContext());
		// 获取窗口布局view
		cView = mInflater.inflate(R.layout.search, null);
		searchName = (EditText) cView.findViewById(R.id.searchtext);
		searchName
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						String name = v.getText().toString();
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							// 在这里编写自己想要实现的功能
							List<RecipectItemInfo> temp = DBTool.getInstance()
									.getmRecipeSelectBiz()
									.getSearchList(getnGid(), name);
							data.clear();
							if (temp != null) {
								for (int i = 0; i < temp.size(); i++) {
									data.add(temp.get(i));
								}
							}
							adapter.notifyDataSetChanged();
						}

						return false;
					}
				});

		// 获取显示在窗口的数据

		listView = (ListView) cView.findViewById(R.id.searchrecilist);
		listView.setFocusable(true);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setLine = (LinearLayout) cView.findViewById(R.id.setLine);
		if (null == data || data.isEmpty()) {
			setLine.setVisibility(View.GONE);
		}
		// 设置listView 的每项点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;
				if (null != data && !data.isEmpty()) {
					for (int i = 0; i < data.size(); i++) {
						if (i == position) {
							index = i;
							RecipectItemInfo itemInfo = data.get(i);
							if (null != itemInfo) {
								itemInfo.setChecked(true);

							} else {
								data.get(i).setChecked(false);
							}
						} else {
							data.get(i).setChecked(false);
						}
					}// 通知adapter里面改变勾选的值
					adapter.notifyDataSetChanged();
				}
			}
		});
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				SKSceneManage.getInstance().time = 0;

			}
		});

		// 获取确定和取消按钮，并加入点击事件
		setButton = (Button) cView.findViewById(R.id.setcurrentreci);

		setButton.setOnClickListener(this);

		// 设置窗口的大小
		mPopupWindow = new PopupWindow(cView, nWidth, nHeight);
		// 做一个不在焦点外的处理事件监听
		mPopupWindow.getContentView().setOnTouchListener(
				new View.OnTouchListener() {

					public boolean onTouch(View v, MotionEvent event) {
						SKSceneManage.getInstance().time = 0;
						mPopupWindow.setFocusable(false);
						mPopupWindow.dismiss();
						popIsShow = false;
						return true;
					}
				});
	}

	/**
	 * 显示下拉窗口的位置
	 */
	public void showPopupWindow() {
		if (!SKSceneManage.getInstance().isbWindowFocus()) {
			//窗口未获取焦点
			Log.e("AKPopupWindow", "no window forcus ...");
			return ;
		}
		// 窗口显示时要加上的窗口标题栏的高度
		popIsShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.update();
		// 填充listView
		adapter = new RecipeSearchAdapter(SKSceneManage.getInstance()
				.getCurrentScene().getContext(), data, info);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance()
				.getCurrentScene(), Gravity.NO_GRAVITY, nLeftPX, nLeftPY);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == setButton) {
			// 设置当前配方
			if (index == -1) {
				Toast.makeText(SKSceneManage.getInstance().mContext, R.string.pleaseselect,
						Toast.LENGTH_SHORT).show();

				return;
			} else {
				int recipeId = data.get(index).getnRecipeId();
				ParameterSet.getInstance().myUpdateCurrentRecipe(recipeId,
						getnGid());
				if (popIsShow) {
					SKSceneManage.getInstance().time = 0;
					mPopupWindow.setFocusable(false);
					mPopupWindow.dismiss();
					popIsShow = false;
				}
			}
		}

	}

	public int getnGid() {
		return nGid;
	}

	public void setnGid(int nGid) {
		this.nGid = nGid;
	}

	public List<RecipectItemInfo> getData() {
		return data;
	}

	public void setData(List<RecipectItemInfo> data) {
		this.data = data;
	}

	private void getPosition() {
		nLeftPX = 0;
		nLeftPY = 0;
		int width = SKSceneManage.nSceneWidth, height = SKSceneManage.nSceneHeight;
		nWidth = 800;
		nHeight = 480;
		if (width < 600) {
			nWidth = 250;
		} else if (width * 3 / 4 >= 600) {
			nWidth = 250;
		} else {
			nWidth = 250;
		}
		if (height == 240) {
			nHeight = 220;
		} else if (height < 360) {
			nHeight = 250;
		} else if (height * 3 / 4 >= 360) {
			nHeight = 250;
		} else {
			nHeight = 250;
		}
		nLeftPX = width / 2 - nWidth / 2;
		nLeftPY = height / 2 - nHeight / 2;
	}

}
