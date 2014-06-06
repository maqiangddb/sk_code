package com.android.Samkoonhmi.skwindow;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.KEYBOARD_OPERATION;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.EditRecipeInfo;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKButtonFunction;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKKeyPopupWindow;
import com.android.Samkoonhmi.skgraphics.plc.touchshow.SKRecipeShow;
import com.android.Samkoonhmi.system.StorageStateManager;
import com.android.Samkoonhmi.util.AlarmGroup;
import com.android.Samkoonhmi.util.MODULE;

public class SKRecipeDialog {

	private static final int EIDE_VIEW=1;
	private static final int SAVE_EIDE_DATA=2;
	private static final int SAVE_HINT=3;
	private static final int INSERT_VIEW=4;
	private View view;
	private Context mContext;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private int type;
	private Button btnOk;
	private Button btnCancel;
	private LinearLayout addLayout;
	private LinearLayout fileLayout;
	private LinearLayout topLayout;
	private SKRecipeSpinner mView;
	public boolean isShow;
	private ArrayList<EditText> mList;
	private LinearLayout mBottomLayout;
	private LinearLayout mTopLayout;
	private LinearLayout mLoadLayout;
	private boolean showData;
	private int nWidth;
	private int nHeight;
	private LinearLayout selectLayout;
	private TextView txtName;
	private TextView btnFrame;
	private ArrayList<String> gName;
	private SKRecipeSpinner mSpinner;
	private TextView mTextGroup;
	private RadioButton btnU;//U盘
	private RadioButton btnSD;//sd卡
	private SKKeyPopupWindow popKey = null;
	private UIHandler mHandler;
	
	/**
	 * type 1=新加，
	 *      2=修改，
	 *      3=删除组，
	 *      4=配方组导出文件，
	 *      5=文件导入到配方组
	 *      6=历史报警数据导出
	 *      7=导出全部配方
	 */
	public SKRecipeDialog(Context context,int type){
		this.mContext=context;
		this.type=type;
		this.isShow=false;
		inflater=LayoutInflater.from(context);
		mList=new ArrayList<EditText>();
		mHandler=new UIHandler(Looper.getMainLooper());
	}
	
	/**
	 * 初始化
	 */
	public boolean initPopWindow(){
		boolean reslut=false;
		if (type==1) {
			//新加
			nWidth=3*SKSceneManage.nSceneWidth/4;
			nHeight=220;
		}else if(type==2) {
			//修改
			nWidth=3*SKSceneManage.nSceneWidth/4;
			nHeight=180;
		}else if(type==3){
			//删除配方组
			nWidth=350;
			nHeight=160;
		}else if (type==4) {
			//配方组导出文件
			nWidth=350;
			nHeight=180;
		}else if (type==5) {
			//文件导入到配方组
			nWidth=350;
			nHeight=180;
		}else if (type==6) {
			//历史报警数据导出
			nWidth=350;
			nHeight=130;
		}else if (type==7) {
			nWidth=350;
			nHeight=140;
		}
		view = inflater.inflate(R.layout.recipe_edit_view, null);
		
		topLayout=(LinearLayout)view.findViewById(R.id.edit_layout_top);
		
		addLayout=(LinearLayout)view.findViewById(R.id.recipe_layout);
		addLayout.setOrientation(LinearLayout.VERTICAL);
		
		fileLayout=(LinearLayout)view.findViewById(R.id.layout_file);
		fileLayout.setVisibility(View.GONE);
		btnSD=(RadioButton)view.findViewById(R.id.btn_sd_ka);
		btnU=(RadioButton)view.findViewById(R.id.btn_u_pan);
		
		txtName=(TextView)view.findViewById(R.id.txt_name);
		selectLayout=(LinearLayout)view.findViewById(R.id.select_layout);
		
		mTextGroup=(TextView)view.findViewById(R.id.txt_recipe_group);
		mTextGroup.setVisibility(View.GONE);
		
		btnOk=(Button)view.findViewById(R.id.btn_ok);
		btnCancel=(Button)view.findViewById(R.id.btn_cancel);
		
		btnOk.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);
		
		if (type==1) {
			gName=new ArrayList<String>();
			btnFrame=(TextView)view.findViewById(R.id.spinner_recipe);
			btnFrame.setOnClickListener(listener);
			
			btnOk.setEnabled(false);
			selectLayout.setVisibility(View.VISIBLE);
			addLayout.setVisibility(View.GONE);
			txtName.setText(mContext.getString(R.string.add_recipe));
		}else if(type==2){
			selectLayout.setVisibility(View.GONE);
			addLayout.setVisibility(View.VISIBLE);
			txtName.setText(mContext.getString(R.string.modify_recipes));
			reslut=editView(addLayout);
		}else if (type==3) {
			txtName.setText(mContext.getString(R.string.recipe_group));
			selectLayout.setVisibility(View.VISIBLE);
			addLayout.setVisibility(View.GONE);
			mTextGroup.setVisibility(View.VISIBLE);
			mTextGroup.setText("");
			gName=new ArrayList<String>();
			btnFrame=(TextView)view.findViewById(R.id.spinner_recipe);
			btnFrame.setOnClickListener(listener);
		}else if (type==4) {
			txtName.setText(mContext.getString(R.string.recipe_group));
			selectLayout.setVisibility(View.VISIBLE);
			addLayout.setVisibility(View.GONE);
			mTextGroup.setVisibility(View.GONE);
			gName=new ArrayList<String>();
			btnFrame=(TextView)view.findViewById(R.id.spinner_recipe);
			btnFrame.setOnClickListener(listener);
			fileLayout.setVisibility(View.VISIBLE);
			
			// 给btnFrame 赋初值
			nGroupId = 0;
			Vector<RecipeOGprop> list=RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
			if (list != null && list.size() > 0) {
				btnFrame.setText(list.get(0).getsRecipeGName());
			}
			
		}else if (type==5) {
			txtName.setText(mContext.getString(R.string.recipe_group));
			selectLayout.setVisibility(View.VISIBLE);
			addLayout.setVisibility(View.GONE);
			mTextGroup.setVisibility(View.GONE);
			gName=new ArrayList<String>();
			fileLayout.setVisibility(View.VISIBLE);
			btnFrame=(TextView)view.findViewById(R.id.spinner_recipe);
			btnFrame.setOnClickListener(listener);
			
			// 给btnFrame 赋初值
			nGroupId = 0;
			Vector<RecipeOGprop> list=RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
			if (list != null && list.size() > 0) {
				btnFrame.setText(list.get(0).getsRecipeGName());
			}
			
		}else if (type==6) {
			txtName.setText(mContext.getString(R.string.storage_path));
			selectLayout.setVisibility(View.GONE);
			addLayout.setVisibility(View.GONE);
			mTextGroup.setVisibility(View.GONE);
			gName=new ArrayList<String>();
			btnFrame=(TextView)view.findViewById(R.id.spinner_recipe);
			btnFrame.setOnClickListener(listener);
			fileLayout.setVisibility(View.VISIBLE);
		}else if (type==7) {
			topLayout.setVisibility(View.GONE);
			fileLayout.setVisibility(View.VISIBLE);
		}
		mPopupWindow=new PopupWindow(view,nWidth, nHeight);
		return reslut;
	}
	
	
	
	/**
	 * 添加view
	 */
	private void addViews(){
		
		mTitleNameList=mRecipeData.getsElemNameList();
		eDataTypeList=mRecipeData.geteDataTypeList();
		len=mRecipeData.getnRecipeLen()+1;
		mList.clear();
		
		String data;
		for (int i = 0; i < len; i++) {
			data="";
			if(i==0){
				//配方名称
				data=mContext.getString(R.string.recipe_name);
			}
//			else if (i==1) {
//				//配方id
//				data=mContext.getString(R.string.recipe_id);
//			}else if (i==2) {
//				//配方描述
//				data=mContext.getString(R.string.recipe_des);
//			}
			else {
				if (mTitleNameList!=null) {
					if (mTitleNameList.size()>i-1) {
						if (mTitleNameList.get(i-1).size()>SystemInfo.getCurrentLanguageId()) {
							data=mTitleNameList.get(i-1).get(SystemInfo.getCurrentLanguageId());
						}
					}
				}
			}
			//标题
			TextView item=new TextView(mContext);
			item.setText(data);
			item.setTextColor(Color.BLACK);
			item.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
			item.setLayoutParams(new LayoutParams(100, 40));
			mTopLayout.addView(item);
			
			
			
			//元素数据
			EditText editText=new EditText(mContext);
			editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			
			if (mRecipeData.getnKeyId()!=-1) {
				boolean reulst=SKKeyPopupWindow.existKeyBroad(mRecipeData.getnKeyId());
				if (!reulst) {
					//如果自定义键盘不存在，则调用系统键盘
					mRecipeData.setnKeyId(-1);
				}
			}
			if (mRecipeData.getnKeyId()!=-1) {
				//使用自定义键盘
				editText.setInputType(InputType.TYPE_NULL);
				editText.setOnClickListener(listener);
				editText.setTag(i);
				editText.setFocusableInTouchMode(false);
			}else {
				//使用系统键盘
				if (i!=0&&i!=2) {
					editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				}
			}
			//加上提示
			editText.setHint(data);
			//editText.setLines(1);
			editText.setLayoutParams(new LayoutParams(100, 60));
//			if (i==1) {
//				int id=DBTool.getInstance().getmRecipeDataBiz().getRecipeId(nGroupId);
//				editText.setText(id+"");
//			}
			mList.add(editText);
			mBottomLayout.addView(editText);
		}
		mHandler.sendEmptyMessage(INSERT_VIEW);
	}

	/**
	 * 编辑
	 */
	private boolean convert=false;//显示数据是否需要转换
	private int len;
	private RecipeOGprop mRecipeData;
	private Vector<RecipeOprop> mRecipeLists = null;// 配方元素据
	private Vector<Vector<String>> mTitleNameList = null; // 配方名称
	private Vector<DATA_TYPE > eDataTypeList;//配方元素类型
	private CurrentRecipe info;
	private RecipeOprop recipe;
	private boolean editView(LinearLayout layout){
		boolean reslut=false;
		showData=true;
		addLayout.removeAllViews();
		
		info=SystemInfo.getCurrentRecipe();
		if (info==null) {
			return reslut;
		}
		
		mRecipeData = RecipeDataCentre.getInstance().getOGRecipeData(
				info.getCurrentGroupRecipeId());
		if (mRecipeData==null) {
			return reslut;
		}
		
		btnOk.setEnabled(false);
		
		mBottomLayout=new LinearLayout(mContext);
		mBottomLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		mTopLayout=new LinearLayout(mContext);
		mTopLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		
		mLoadLayout=new LinearLayout(mContext);
		mLoadLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		//len=mRecipeData.getnRecipeLen()+3;
		len=mRecipeData.getnRecipeLen()+1;
		
		mTitleNameList=mRecipeData.getsElemNameList();
		eDataTypeList=mRecipeData.geteDataTypeList();
		
		mRecipeLists=mRecipeData.getmRecipePropList();
		if (mRecipeLists!=null) {
			for (int i = 0; i < mRecipeLists.size(); i++) {
				if (mRecipeLists.get(i).getnRecipeId()==info.getCurrentRecipeId()) {
					recipe=mRecipeLists.get(i);
					break;
				}
			}
		}
		
		if(recipe==null){
			return reslut;
		}
		
		TextView item=new TextView(mContext);
		item.setTextSize(18);
		item.setText(mContext.getString(R.string.load_data));
		item.setTextColor(Color.BLACK);
		item.setGravity(Gravity.CENTER);
		item.setLayoutParams(new LinearLayout.LayoutParams(nWidth, 100));
		mLoadLayout.addView(item);
		addLayout.addView(mLoadLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		mLoadLayout.layout(0, 50, nWidth, 100);
		
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, nTaskId, null, callback, 0);
		
		reslut=true;
		return reslut;
	}
	

	
	private int nTaskId=1;
	SKThread.ICallback callback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			if (taskId==nTaskId) {
				if (showData) {
					if (type==1) {
						addViews();
					}else if (type==2) {
						updateEditView();
					}
				}
			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			// TODO Auto-generated method stub
			
		}
	};
	
	/**
	 * 更新编辑界面
	 */
	private void updateEditView(){
		String[] elems=RecipeDataCentre.getInstance().getRecipeData(info.getCurrentGroupRecipeId(), recipe.getnRecipeId(), false);
		
		for (int i = 0; i < len; i++) {
			//配方标题
			String sTitleName="";
			String data="";
			if (i==0) {
				//配方名称
				sTitleName=mContext.getString(R.string.recipe_name);
				if (recipe.getsRecipeName()!=null) {
					if (recipe.getsRecipeName().size()>SystemInfo.getCurrentLanguageId()) {
						data=recipe.getsRecipeName().get(SystemInfo.getCurrentLanguageId());
					}
				}
			}
//			else if (i==1) {
//				//配方id
//				sTitleName=mContext.getString(R.string.recipe_id);
//				if (recipe!=null) {
//					data=recipe.getnRecipeId()+"";
//				}
//			}else if (i==2) {
//				//配方描述
//				sTitleName=mContext.getString(R.string.recipe_des);
//				if (recipe.getsRecipeDescri()!=null) {
//					if (recipe.getsRecipeDescri().size()>SystemInfo.getCurrentLanguageId()) {
//						data=recipe.getsRecipeDescri().get(SystemInfo.getCurrentLanguageId());
//					}
//				}
//			}
			else{
				//配方元素名称.
				if (mTitleNameList!=null) {
					if (mTitleNameList.size()>i-1) {
						if (mTitleNameList.get(i-1).size()>SystemInfo.getCurrentLanguageId()) {
							sTitleName=mTitleNameList.get(i-1).get(SystemInfo.getCurrentLanguageId());
						}
					}
				}
				
				if (elems!=null) {
					if (elems.length>i-1) {
						data=elems[i-1];
						convert=false;
						if (eDataTypeList.size()>i-1) {
							DATA_TYPE type=eDataTypeList.get(i-1);
							if (type==DATA_TYPE.BIT_1||type==DATA_TYPE.INT_16||type==DATA_TYPE.INT_32
									||type==DATA_TYPE.POSITIVE_INT_16||type==DATA_TYPE.POSITIVE_INT_32) {
								convert=true;
							}
						}
						
						if (data==null||data.equals("")||data.equals(" ")) {
							data="0";
						}else {
							if (convert) {
								BigDecimal decimal=new BigDecimal(Double.valueOf(data).longValue());
								data=decimal+"";
							}else {
								if (!isNumeric(data)) {
									BigDecimal decimal=new BigDecimal(Double.valueOf(data));
									data=decimal+"";
								}
								
							}
						}
					}
				}
			}
			
			TextView item=new TextView(mContext);
			item.setText(sTitleName);
			item.setTextColor(Color.BLACK);
			item.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
			item.setLayoutParams(new LinearLayout.LayoutParams(100, 40));
			mTopLayout.addView(item);
			
			
			//配方元素
			EditText txtEditText=new EditText(mContext);
			txtEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			txtEditText.setLines(1);
			txtEditText.setText(data);
	
			txtEditText.setLayoutParams(new LinearLayout.LayoutParams(100, 60));
			txtEditText.setGravity(Gravity.CENTER);

			
			if (mRecipeData.getnKeyId()!=-1) {
				boolean reulst=SKKeyPopupWindow.existKeyBroad(mRecipeData.getnKeyId());
				if (!reulst) {
					//如果自定义键盘不存在，则调用系统键盘
					mRecipeData.setnKeyId(-1);
				}
			}
			
			if (mRecipeData.getnKeyId()!=-1) {
				//使用自定义键盘
				txtEditText.setInputType(InputType.TYPE_NULL);
				txtEditText.setOnClickListener(listener);
				txtEditText.setTag(i);
				txtEditText.setFocusableInTouchMode(false);
			}else {
				//使用系统键盘
				if (i!=0&&i!=2) {
					txtEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				}
			}
			mList.add(txtEditText);
			mBottomLayout.addView(txtEditText);
			mBottomLayout.setGravity(Gravity.CENTER);
				
		}
		
		mHandler.sendEmptyMessage(EIDE_VIEW);
	}
	
	private Toast skToast;
	public class UIHandler extends Handler{

		public UIHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==EIDE_VIEW) {
				if (showData) {
					mLoadLayout.setVisibility(View.GONE);
					addLayout.removeView(mLoadLayout);
					
					addLayout.addView(mTopLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
				    mTopLayout.layout(0, 40, nWidth, 100);
					
					addLayout.addView(mBottomLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
					mBottomLayout.layout(0, 100, nWidth, 180);
					
					//mPopupWindow.update();
					isShow=false;
					
					btnOk.setEnabled(true);
					showPopWindow();
				}
			}else if (msg.what==SAVE_EIDE_DATA) {
				boolean reslut=update();
				if (skToast!=null) {
					skToast.cancel();
				}
				if (reslut) {
					if (mPopupWindow != null) {
						isShow = false;
						mPopupWindow.dismiss();
					}
				}else {
					btnOk.setEnabled(true);
					btnCancel.setEnabled(true);
				}
			}else if (msg.what==SAVE_HINT) {
				skToast=SKToast.makeText(mContext.getString(R.string.save_data), Toast.LENGTH_SHORT);
				if (skToast!=null) {
					skToast.show();
				}
			}else if(msg.what==INSERT_VIEW){
			
				mLoadLayout.setVisibility(View.GONE);
				addLayout.removeView(mLoadLayout);
				
				addLayout.addView(mTopLayout, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				mTopLayout.layout(0, 40, nWidth, 100);
				
				addLayout.addView(mBottomLayout, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				mBottomLayout.layout(0, 100, nWidth, 180);
				
				//mPopupWindow.update();
				isShow=false;
				
				btnOk.setEnabled(true);
				btnFrame.setEnabled(true);
				showPopWindow();
				loading=false;
			}
		}
		
	}
	
	/**
	 * 显示
	 */
	public synchronized void showPopWindow(){
		if (!SKSceneManage.getInstance().isbWindowFocus()) {
			//窗口未获取焦点
			Log.e("AKPopupWindow", "no window forcus ...");
			return ;
		}
		if (!isShow) {
			if (SKSceneManage.getInstance().getCurrentScene()==null) {
				Log.d("SKScene", "current scene null....");
				return;
			}
			isShow=true;
			mPopupWindow.setFocusable(true);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			mPopupWindow.update();
			mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER, 0, 0);
		}
	}
	
	/**
	 * 释放内存
	 */
	private void clear(){
		if (mTopLayout!=null) {
			mTopLayout.removeAllViews();
		}
		if (mBottomLayout!=null) {
			mBottomLayout.removeAllViews();
		}
		if (addLayout!=null) {
			addLayout.removeAllViews();
		}
		if (selectLayout!=null) {
			selectLayout.removeAllViews();
		}
		if (mLoadLayout!=null) {
			mLoadLayout.removeAllViews();
		}
		if (fileLayout!=null) {
			fileLayout.removeAllViews();
		}
		loading=false;
	}
	

	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			SKSceneManage.getInstance().time=0;
			if (v.equals(btnOk)) {
				// 确定
				if (type==1&&nGroupId==-1) {
					return;
				}
				boolean b=save();
				if (b) {
					clear();
					if (mPopupWindow != null) {
						isShow = false;
						mPopupWindow.dismiss();
					}
				}
			} else if (v.equals(btnCancel)) {
				// 取消
				clear();
				showData=false;
				if (mPopupWindow != null) {
					isShow = false;
					mPopupWindow.dismiss();
				}
			}else if (v.equals(btnFrame)) {
				ArrayList<String> group=new ArrayList<String>();
				Vector<RecipeOGprop> list=RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
				if (list!=null) {
					for (int i = 0; i < list.size(); i++) {
						group.add(list.get(i).getsRecipeGName());
					}
					if (mSpinner==null) {
						mSpinner=new SKRecipeSpinner(mContext,group);
						mSpinner.initPopWindow();
						mSpinner.setiCallGroupId(iCallGroupId);
					}
					
					if (type==1) {
						if (SKSceneManage.nSceneWidth==800) {
							mSpinner.showPopWindow(btnFrame,-btnFrame.getLeft(),32);
						}else {
							mSpinner.showPopWindow(btnFrame,22,32);
						}
					}else if (type==3) {
						mSpinner.showPopWindow(btnFrame,26,62);
					}else if (type==4) {
						mSpinner.showPopWindow(btnFrame,26,62);
					}else if (type==5) {
						mSpinner.showPopWindow(btnFrame,26,62);
					}
				}
			}else {
				Object tag=v.getTag();
				if (tag==null) {
					return;
				}
				String index=tag.toString();
				if (index==null||index.equals("")) {
					return;
				}
				int id=Integer.valueOf(index);
				nListIndex=id;
				if (null == popKey) {
					popKey = new SKKeyPopupWindow(SKSceneManage.getInstance().mContext,
							true, mRecipeData.getnKeyId(), mRecipeData.geteDataTypeList().get(0));
					popKey.setCallback(call);
				}
				String[] mm=getMaxAndMin( mRecipeData.geteDataTypeList().get(0));
				if (SKKeyPopupWindow.keyFlagIsShow) {
					popKey.setLastText(((EditText)v).getText().toString());
					if(nListIndex ==0){//修改配方名称
						popKey.setKeyType(false);
						popKey.setInputType(false);
					}
					else {//修改配方元素
						popKey.setKeyType(true);
						popKey.setInputType(true);
					}
					popKey.setShowMax(mm[1]);
					popKey.setShowMin(mm[0]);
					popKey.setnStartX(mRecipeData.getnBoardX());
					popKey.setnStartY(mRecipeData.getnBoardY());
					popKey.initPopUpWindow();
					popKey.showPopUpWindow();
				}
			}
		}
	};
	
	private int nListIndex=-1;
	SKKeyPopupWindow.ICallback call=new SKKeyPopupWindow.ICallback(){

		@Override
		public void onResult(String result, KEYBOARD_OPERATION type) {
			if (nListIndex==-1||mList==null||result==null) {
				return;
			}
			if (type == KEYBOARD_OPERATION.ENTER) {
				if (mList.size()>nListIndex) {
					mList.get(nListIndex).setText(result);
					nListIndex=-1;
				}
			}
		}
		
	};
	
	private String[] getMaxAndMin(DATA_TYPE dataType){
		String[] mm=new String[]{0+"",0+""};
		switch (dataType) {
		case INT_16:
			mm[0]="-32768";
			mm[1]="32767";
			break;
		case POSITIVE_INT_16:
			mm[0]="0";
			mm[1]="65535";
			break;
		case INT_32:
			mm[0]="-2147483648";
			mm[1]="2147483647";
			break;
		case POSITIVE_INT_32:
			mm[0]="0";
			mm[1]="4294967295";
			break;
		case FLOAT_32:
			mm[0]="-2147483648";
			mm[1]="2147483647";
			break;
		case BCD_16:
			mm[0]="0";
			mm[1]="9999";
			break;
		case BCD_32:
			mm[0]="0";
			mm[1]="99999999";
			break;
		case BIT_1:
			mm[0]="0";
			mm[1]="1";
			break;
		}
		return mm;
	}
	
	private int nGroupId=-1;
	private boolean loading=false;//正在加载数据
	SKRecipeSpinner.ICallGroupId iCallGroupId=new SKRecipeSpinner.ICallGroupId() {
		
		@Override
		public void onResult(int gId,String name) {
			btnFrame.setText(name);
			if (type==1) {
				if (loading) {
					return;
				}
				loading=true;
				if (gId!=nGroupId) {
					addData(gId);
				}
			}else if(type==3){
				nGroupId=gId;
			}else if (type==4) {
				nGroupId=gId;
			}else if (type==5) {
				nGroupId=gId;
			}
		}
	};
	
	/**
	 * 删除配方组
	 */
	private boolean deleteGroup(int gid){
		if (gid>-1) {
			RecipeDataCentre.getInstance().msgDeleteRecipeGroup(gid);
			Toast.makeText( SKRecipeDialog.this.mContext,"当前配方組已成功删除", Toast.LENGTH_SHORT).show();
		}else {
			return false;
		}
		return true;
	}
	
	/**
	 * 文件导入到配方组
	 */
	private boolean readFile(int gid){
		if (gid>-1) {
			RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(gid);
			
			if (btnU.isChecked()) {
				if (oGprop!=null) {
					oGprop.seteSaveMedia(STORAGE_MEDIA.U_DISH);
				}
			}else {
				if (oGprop!=null) {
					oGprop.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
				}
			}
			CurrentRecipe cinfo=new CurrentRecipe();
			cinfo.setCurrentGroupRecipeId(gid);
			cinfo.setCurrentRecipeId(-1);
			RecipeDataCentre.getInstance().msgReadRecipeFromFile(cinfo);
		}else {
			return false;
		}
		return true;
	}
	
	/**
	 * 配方组导出到文件
	 */
	private boolean writeFile(int gid){
		if (gid>-1) {
			RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(gid);
			
			if (btnU.isChecked()) {
				if (oGprop!=null) {
					oGprop.seteSaveMedia(STORAGE_MEDIA.U_DISH);
				}
			}else {
				if (oGprop!=null) {
					oGprop.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
				}
			}
			CurrentRecipe cinfo=new CurrentRecipe();
			cinfo.setCurrentGroupRecipeId(gid);
			cinfo.setCurrentRecipeId(-1);
			RecipeDataCentre.getInstance().msgWriteRecipeToFile(cinfo);
		}else {
			return false;
		}
		return true;
	}
	
	/**
	 * 添加配方，加载标题数据
	 */
	private void addData(int gId){
		mRecipeData = RecipeDataCentre.getInstance().getOGRecipeData(
				gId);
		if (mRecipeData==null) {
			loading=false;
			return;
		}
		
		nGroupId=gId;
		//清空数据
		if (mTopLayout!=null) {
			mTopLayout.removeAllViews();
			addLayout.removeView(mTopLayout);
		}
		if (mBottomLayout!=null) {
			mBottomLayout.removeAllViews();
			addLayout.removeView(mBottomLayout);
		}
		if (mLoadLayout!=null) {
			mLoadLayout.removeAllViews();
			addLayout.removeView(mLoadLayout);
		}
		
		showData=true;
		addLayout.setVisibility(View.VISIBLE);
		selectLayout.setVisibility(View.VISIBLE);
		btnFrame.setEnabled(false);
		btnOk.setEnabled(false);
		
		if (mLoadLayout==null) {
			mLoadLayout=new LinearLayout(mContext);
			mLoadLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
		
		if (mBottomLayout==null) {
			mBottomLayout=new LinearLayout(mContext);
			mBottomLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		if (mTopLayout==null) {
			mTopLayout=new LinearLayout(mContext);
			mTopLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
		
		TextView item=new TextView(mContext);
		item.setTextSize(18);
		item.setText(mContext.getString(R.string.load_data));
		item.setTextColor(Color.BLACK);
		item.setGravity(Gravity.CENTER);
		item.setLayoutParams(new LinearLayout.LayoutParams(nWidth, 100));
		mLoadLayout.addView(item);
		mLoadLayout.setVisibility(View.VISIBLE);
		addLayout.addView(mLoadLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		mLoadLayout.layout(0, 50, nWidth, 100);
		
		mPopupWindow.update();
		isShow=false;
		showPopWindow();
		
		SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, nTaskId, null, callback, 0);
		
	}
	
	/**
	 * 保存数据
	 */
	private boolean save(){
		boolean result=false;
		if (type==1) {
			result=insert();
			if (result) {
				SKToast.makeText(mContext, R.string.hint_result_succeed, Toast.LENGTH_SHORT).show();
			}
		}else if(type==2) {
			result=false;
			btnOk.setEnabled(false);
			btnCancel.setEnabled(false);
			if (len>100) {
				//配方长度大于100，才需要提示等待
				mHandler.sendEmptyMessage(SAVE_HINT);
				mHandler.sendEmptyMessageDelayed(SAVE_EIDE_DATA, 100);
			}else {
				mHandler.sendEmptyMessage(SAVE_EIDE_DATA);
			}
		}else if (type==3) {
			result=deleteGroup(nGroupId);
			if (!result) {
				SKToast.makeText(mContext, R.string.delete_failed, Toast.LENGTH_SHORT).show();
			}
		}else if(type==4) {
			result=writeFile(nGroupId);
			if (!result) {
				SKToast.makeText(mContext, R.string.export_failed, Toast.LENGTH_SHORT).show();
			}
		}else if (type==5) {
			result=readFile(nGroupId);
			if (!result) {
				SKToast.makeText(mContext, R.string.import_failed, Toast.LENGTH_SHORT).show();
			}
		}else if (type==6) {
			//历史报警数据导出
			result=exportAlarmFile();
		}else if (type==7) {
			String path="";
			if (btnU.isChecked()) {
				//U盘
				path="/mnt/usb2/";
				if(!StorageStateManager.getInstance().isUSBMounted()){
					SKToast.makeText(mContext.getString(R.string.u_pand), Toast.LENGTH_SHORT).show();
					return false;
				}
			}else {
				//sd卡
				path = "/mnt/sdcard/";
				if (!StorageStateManager.getInstance().isSDMounted()) {
					SKToast.makeText(mContext.getString(R.string.sd_card), Toast.LENGTH_SHORT).show();
					return false;
				}
			}
			RecipeDataCentre.getInstance().msgWriteAllRecipeSToFiles(path);
			result=true;
		}
		return result;
	}
	
	/**
	 * 更新
	 */
	private boolean update(){
		
		if (recipe==null) {
			return false;
		}
		String valses="";
		for (int i = 0; i < mList.size(); i++) {
			if(i==0){
				//配方名称
				String name=mList.get(i).getText().toString().trim();
				if (!name.equals(recipe.getsRecipeName().get(SystemInfo.getCurrentLanguageId()))) {
					boolean bname=DBTool.getInstance().getmRecipeDataBiz().existRecipeName(name, info.getCurrentGroupRecipeId());
					if(bname){
						SKToast.makeText(mContext,mContext.getString(R.string.recipe_name)+":"
								+name+","+mContext.getString(R.string.recipe_exists), Toast.LENGTH_SHORT).show();
						return false;
					}else {
						recipe.getsRecipeName().set(SystemInfo.getCurrentLanguageId(), name);
					}
				}
			}
//			else if (i==1) {
//				//配方id
//				String id=mList.get(i).getText().toString().trim();
//				//pid=recipe.getnRecipeId();
//				if (!id.equals(recipe.getnRecipeId())) {
//					if (!isNumeric(id)) {
//						SKToast.makeText(mContext,
//								mContext.getString(R.string.recipe_prompt)+", ID "
//								+mContext.getString(R.string.recipe_prompt_num), Toast.LENGTH_SHORT).show();
//						return false;
//					}else {
//						int temp=Integer.valueOf(id);
//						if (temp!=recipe.getnRecipeId()) {
//							boolean bid=DBTool.getInstance().getmRecipeDataBiz().existRecipeID(info.getCurrentGroupRecipeId()+"", id+"");
//							if (bid) {
//								SKToast.makeText(mContext,"ID:"+id+","
//							+mContext.getString(R.string.recipe_exists), Toast.LENGTH_SHORT).show();
//								return false;
//							}else{
//								if (temp<0||temp>32767){
//									SKToast.makeText(mContext,"ID:"+id+","
//											+mContext.getString(R.string.out_of_range), Toast.LENGTH_SHORT).show();
//									return false;
//								}
//								recipe.setnRecipeId(Integer.valueOf(id));
//							}
//						}
//					}
//				}
//				
//			}else if (i==2) {
//				//配方描述
//				String des=mList.get(i).getText().toString().trim()+" ";
//				if (recipe.getsRecipeDescri().size()==0) {
//					Vector<String> descri=new Vector<String>();
//					for (int j = 0; j < SystemInfo.getLanguageNumber(); j++) {
//						descri.add("");
//					}
//					recipe.setsRecipeDescri(descri);
//				}
//				recipe.getsRecipeDescri().set(SystemInfo.getCurrentLanguageId(), des);
//			}
			else {
				//配方元素
				String value=mList.get(i).getText().toString().trim();
				if (!value.equals("")) {
					if (!isNumeric(value)) {
						SKToast.makeText(mContext,mContext.getString(R.string.recipe_prompt)+(i+1)
								+mContext.getString(R.string.recipe_rank)
								+mContext.getString(R.string.recipe_prompt_num), Toast.LENGTH_SHORT).show();
						return false;
					}else{
						double temp;
						if (value==null||value.equals("")) {
							value="0";
							temp=0;
						}else {
							temp=Double.valueOf(value);
						}
						int k=i-1;
						if (eDataTypeList!=null) {
							if (eDataTypeList.size()>k) {
								DATA_TYPE type=eDataTypeList.get(k);
								switch (type) {
								case INT_16:
									if (!isInt(value+"",mContext.getString(R.string.integer))) {
										return false;
									}
									if (temp<-32768||temp>32767) {
										SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case POSITIVE_INT_16:
									if (!isPosInt(value+"",mContext.getString(R.string.positive_integer))) {
										return false;
									}
									if (temp<0||temp>65535) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case INT_32:
									if (!isInt(value+"",mContext.getString(R.string.integer))) {
										return false;
									}
									if (temp<-2147483648||temp>2147483647) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case POSITIVE_INT_32:
									if (!isPosInt(value+"",mContext.getString(R.string.positive_integer))) {
										return false;
									}
									if (temp<0||temp>4294967295L) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case FLOAT_32:
									if (temp<-2147483648||temp>2147483647) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case BCD_16:
									if (!isInt(value+"","BCD")) {
										return false;
									}
									if (temp<0||temp>9999) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case BCD_32:
									if (!isInt(value+"","BCD")) {
										return false;
									}
									if (temp<0||temp>99999999) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								case BIT_1:
									if (!isInt(value+"",mContext.getString(R.string.bit))) {
										return false;
									}
									if (temp<0||temp>1) {
										SKToast.makeText(mContext,  mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
										return false;
									}
									break;
								}
							}
						}
					}
				}else{
					value="0";
				}
				if(i<mList.size()-1){
					valses+=value+",";
				}else {
					valses+=value+"";
				}
			}
			
		}
		
		EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
		
		String elemsValue[]=null;
		if (valses!=null) {
			elemsValue=valses.split(",");
		}
		
		CurrentRecipe cInfo=new CurrentRecipe();
		cInfo.setCurrentGroupRecipeId(info.getCurrentGroupRecipeId());
		cInfo.setCurrentRecipeId(recipe.getnRecipeId());
		
		//RecipeDataCentre.getInstance().setCurrRecipe(info.getCurrentGroupRecipeId(), recipe.getnRecipeId());
		eInfo.mRecipeData=recipe;
		eInfo.mRecipeInfo=cInfo;
		eInfo.sValueList=elemsValue;
		
		
		//RecipeDataCentre.getInstance().setRecipeData(info.getCurrentGroupRecipeId(), recipe.getnRecipeId(), elemsValue);
		RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
		
		//编辑完成之后 进行广播
		notifiSelect(recipe.getnRecipeId());
		return true;
	}
	
	private void notifiSelect(int editId){
		if (SKRecipeShow.mObservers != null && SKRecipeShow.mObservers.size() > 0) {
			SelectObser observable = new SelectObser();
			for(int i = 0; i < SKRecipeShow.mObservers.size(); i++){
				observable.addObserver(SKRecipeShow.mObservers.get(i));
			}
			observable.mEditId = editId;
			observable.notifyChanges();
		}
	}
	
	class SelectObser extends Observable{
		
		public int mEditId = 0;
		public void notifyChanges(){
			setChanged();
			notifyObservers(mEditId);
		}
	}
	
	/**
	 * 插入
	 */
	private boolean insert(){
		
		//创建一个配方
		RecipeOprop recipe=new RecipeOprop();
		
		int count=SystemInfo.getLanguageNumber();
		String values="";
		
		int size=mList.size();
		
		for (int i = 0; i < size; i++) {
			if(i==0){
				//配方名称
				String name=mList.get(i).getText().toString().trim();
				if (name==null||name.equals("")) {
					SKToast.makeText(mContext,mContext.getString(R.string.recipe_name)+","
							+mContext.getString(R.string.recipe_add_null), Toast.LENGTH_SHORT).show();
					return false;
				}else {
					boolean bname=DBTool.getInstance().getmRecipeDataBiz().existRecipeName(name, nGroupId);
					if(bname){
						SKToast.makeText(mContext,mContext.getString(R.string.recipe_name)+":"
								+name+","+mContext.getString(R.string.recipe_exists), Toast.LENGTH_SHORT).show();
						return false;
					}else {
						Vector<String> names=new Vector<String>();
						for (int j = 0; j < count; j++) {
							names.add(name);
						}
						recipe.setsRecipeName(names);
					}
				}
				
			}
//			else if (i==1) {
//				//配方id
//				String id=mList.get(i).getText().toString().trim();
//				if (id==null||id.equals("")) {
//					SKToast.makeText(mContext,"ID,"
//							+mContext.getString(R.string.recipe_add_null), Toast.LENGTH_SHORT).show();
//					return false;
//				}else{
//					if (!id.equals(recipe.getnRecipeId())) {
//						if (!isNumeric(id)) {
//							SKToast.makeText(mContext,
//									mContext.getString(R.string.recipe_prompt)+", ID "
//									+mContext.getString(R.string.recipe_prompt_num), Toast.LENGTH_SHORT).show();
//							return false;
//						}else {
//							boolean bid=DBTool.getInstance().getmRecipeDataBiz().existRecipeID(nGroupId+"",id);
//							if (bid) {
//								SKToast.makeText(mContext,"ID:"+id+","
//							+mContext.getString(R.string.recipe_exists), Toast.LENGTH_SHORT).show();
//								return false;
//							}else{
//								recipe.setnRecipeId(Integer.valueOf(id));
//							}
//						}
//					}
//				}
//				
//			}else if (i==2) {
//				//配方描述
//				String des=mList.get(i).getText().toString().trim();
//				Vector<String> desList=new Vector<String>();
//				for (int j = 0; j < count; j++) {
//					desList.add(des);
//				}
//				recipe.setsRecipeDescri(desList);
//			}
			else {
				//配方元素
				
				String value=mList.get(i).getText().toString().trim();
				if (!value.equals("")) {
					if (!isNumeric(value)) {
						SKToast.makeText(mContext,mContext.getString(R.string.recipe_prompt)+(i+1)
								+mContext.getString(R.string.recipe_rank)
								+mContext.getString(R.string.recipe_prompt_num), Toast.LENGTH_SHORT).show();
						return false;
					}
				}else {
					value="0";
				}
				
				double temp;
				if (value.equals("")) {
					value="0";
					temp=0;
				}else {
					temp=Double.valueOf(value);
				}
				
				int k=i-1;
				if (eDataTypeList!=null) {
					if (eDataTypeList.size()>k) {
						DATA_TYPE type=eDataTypeList.get(k);
						switch (type) {
						case INT_16:
							if (!isInt(value+"",mContext.getString(R.string.integer))) {
								return false;
							}
							if (temp<-32768||temp>32767) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case POSITIVE_INT_16:
							if (!isPosInt(value+"",mContext.getString(R.string.positive_integer))) {
								return false;
							}
							if (temp<0||temp>65535) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case INT_32:
							if (!isInt(value+"",mContext.getString(R.string.integer))) {
								return false;
							}
							if (temp<-2147483648||temp>2147483647) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case POSITIVE_INT_32:
							if (!isPosInt(value+"",mContext.getString(R.string.positive_integer))) {
								return false;
							}
							if (temp<0||temp>4294967295L) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case FLOAT_32:
							if (temp<-2147483648||temp>2147483647) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case BCD_16:
							if (!isInt(value+"","BCD")) {
								return false;
							}
							if (temp<0||temp>9999) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case BCD_32:
							if (!isInt(value+"","BCD")) {
								return false;
							}
							if (temp<0||temp>99999999) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						case BIT_1:
							if (!isInt(value+"",mContext.getString(R.string.bit))) {
								return false;
							}
							if (temp<0||temp>1) {
								SKToast.makeText(mContext, mContext.getString(R.string.out_of_range)+":"+temp, Toast.LENGTH_SHORT).show();
								return false;
							}
							break;
						}
					}
				}
				if (i<size-1) {
					values+=value+",";
				}else {
					values+=value+"";
				}
			}
			
		}
		
		//设置配方ID
		int id=DBTool.getInstance().getmRecipeDataBiz().getRecipeId(nGroupId);
		recipe.setnRecipeId(id);
		//设置配方描述
		Vector<String> desList=new Vector<String>();
		desList.add("");
		recipe.setsRecipeDescri(desList );
		

		EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
		
		CurrentRecipe info=new CurrentRecipe();
		info.setCurrentGroupRecipeId(nGroupId);
		info.setCurrentRecipeId(-1);
		
		String elemsValue[]=null;
		if (values!=null) {
			elemsValue=values.split(",");
		}
		eInfo.mRecipeData=recipe;
		eInfo.mRecipeInfo=info;
		eInfo.sValueList=elemsValue;
		RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
		return true;
		
	}
	
	/**
	 * 是否是数字,带小数
	 */
	public boolean isNumeric(String str){ 
		Pattern pattern = Pattern
				.compile("^\\d+$|^\\d+\\.\\d+$|-\\d+$|^-\\d+\\.\\d+$");
		if (null == str || "".equals(str)) {
			return false;
		}

		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;   
	} 
	
	/**
	 * 是否是正整数
	 */
	public boolean isPosInt(String str,String msg){ 
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet=false;
		Pattern pattern = Pattern.compile("[0-9]*");
		resulet=pattern.matcher(str).matches();
		if (!resulet) {
			SKToast.makeText(mContext,  mContext.getString(R.string.enter)
					+msg
					+mContext.getString(R.string.type), Toast.LENGTH_SHORT).show();
		}
		return resulet;
	}
	
	/**
	 * 是否是整数
	 */
	public boolean isInt(String str,String msg){ 
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet=false;
		Pattern pattern = Pattern.compile("[-_0-9]*");
		resulet=pattern.matcher(str).matches();
		if (!resulet) {
			SKToast.makeText(mContext, mContext.getString(R.string.enter)
					+msg
					+mContext.getString(R.string.type), Toast.LENGTH_SHORT).show();
		}
		return resulet;
	}

	/**
	 * 报警历史数据导出
	 */
	private boolean exportAlarmFile(){
	
		boolean result=false;
		String path="";
		String sMsg="";
	
		if (btnU.isChecked()) {
			//U盘
			path="/mnt/usb2/";
			sMsg=mContext.getString(R.string.u_error);
			if(!StorageStateManager.getInstance().isUSBMounted()){
				SKToast.makeText(mContext.getString(R.string.u_pand), Toast.LENGTH_SHORT).show();
				return false;
			}
		}else {
			//sd卡
			path = "/mnt/sdcard/";
			sMsg=mContext.getString(R.string.sd_error);
			if (!StorageStateManager.getInstance().isSDMounted()) {
				SKToast.makeText(mContext.getString(R.string.sd_card), Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		File pFile=new File(path);
		if (!pFile.exists()) {
			SKToast.makeText(sMsg, Toast.LENGTH_SHORT).show();
			return result;
		}
		
		if (path.equals("")) {
			SKToast.makeText(mContext.getString(R.string.path_error), Toast.LENGTH_SHORT).show();
			return result;
		}
//		
		result=true;
		AlarmGroup.getInstance().exportFile(path);
		return result;
	} 
}
