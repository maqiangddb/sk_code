package com.android.Samkoonhmi.skwindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.model.CurrentRecipe;
import com.android.Samkoonhmi.model.RecipeOGprop;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;

public class RecipeCopyDialog extends Dialog{
	private Spinner fromSpinner = null;
	private Spinner toSpinner = null;
	
	private ArrayList<String> fromList = new ArrayList<String>();
	private ArrayList<String> toList = new ArrayList<String>();
	private ArrayAdapter<String> fromAdapter = null;
	private ArrayAdapter<String> toAdapter = null;
	private int mSelectIndex = - 1;

	public RecipeCopyDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recipe_copy);
		
		initData();
		initView();
	}
	
	private void initView(){
		RadioGroup selectGroup = (RadioGroup)findViewById(R.id.recipe_copy_seletc);
		selectGroup.setOnCheckedChangeListener(mChangeListener);
		
		findViewById(R.id.recipe_copy_confirm).setOnClickListener(mListener);
		findViewById(R.id.recipde_copy_cancel).setOnClickListener(mListener);
		
		fromSpinner = (Spinner) findViewById(R.id.recipe_copy_from);
		fromSpinner.setAdapter(fromAdapter);
		toSpinner = (Spinner) findViewById(R.id.recipe_copy_to);
		toSpinner.setAdapter(toAdapter);
	};
	
	private void initData(){
		toList.addAll(getMemoryRecipeList());
		fromAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, fromList);
		toAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, toList);
		fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	
	private OnCheckedChangeListener mChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == R.id.recipe_copy_from_U) {
				mSelectIndex = 0;
				fromAdapter.clear();
				File[] recipeFiles = getFile(STORAGE_MEDIA.U_DISH);
				
				fromAdapter.addAll(getFileNameList(recipeFiles));
				fromAdapter.notifyDataSetChanged();
			}
			else if (checkedId == R.id.recipe_copy_from_sd) {
				mSelectIndex = 1;
				fromAdapter.clear();
				File[] recipeFiles = getFile(STORAGE_MEDIA.SD_DISH);
				
				fromAdapter.addAll(getFileNameList(recipeFiles));
				fromAdapter.notifyDataSetChanged();
			}
			else {
				mSelectIndex = 2;
				fromAdapter.clear();
				File[] recipeFiles = getFile(STORAGE_MEDIA.INSIDE_DISH);
				
				fromAdapter.addAll(getFileNameList(recipeFiles));
				fromAdapter.notifyDataSetChanged();
			}
		}
	};
	
	private View.OnClickListener mListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.recipe_copy_confirm) {
				cancel();
				int toGroupId = getGroupIdByPos(toSpinner.getSelectedItemPosition());
				String fromName = getFromFileNameByPos(fromSpinner.getSelectedItemPosition());
				importFile(toGroupId, fromName);
			}
			else if (v.getId() == R.id.recipde_copy_cancel) {
				cancel();
			}
		}
	};
	
	/**
	 * 获取配方组的名称
	 * @return
	 */
	private ArrayList<String> getMemoryRecipeList(){
		Vector<RecipeOGprop> recipelist = RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
		ArrayList<String> list = new ArrayList<String>();
		
		if (recipelist != null && recipelist.size() > 0) {
			for(RecipeOGprop bean : recipelist){
				list.add(bean.getsRecipeGName());
			}
		}
		
		return list;
	}
	
	/**
	 *  配方组Id
	 * @param pos Spinner选中的位置
	 * @return 配方组ID
	 */
	private int getGroupIdByPos( int pos){
		Vector<RecipeOGprop> recipelist = RecipeDataCentre.getInstance().getRecipeDataProp().getmRecipeGroupList();
		if (recipelist != null && pos > -1 &&  pos < recipelist.size()) {
			RecipeOGprop bean = recipelist.get(pos);
			if (bean != null) {
				return bean.getnGRecipeID();
			}
			
		}
		return -1;
	}
	
	
	/**
	 * 
	 * @param path -文件夹路径
	 * @return  文件夹
	 */
	private File createDir(String path){
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File filepath = new File(path);
		if (filepath != null && !filepath.exists()) {
			filepath.mkdirs();
		}
		
		return filepath;
		
	}
	
	
	/**
	 * 
	 * @param filetype
	 * @return -获取指定目录下面的.csv文件
	 */
	private File[]  getFile(STORAGE_MEDIA filetype){
		String path = "/mnt/sdcard/";
		switch (filetype) {
		case INSIDE_DISH:
			path = "/data/data/com.android.Samkoonhmi/formula/recipe/";
			break;
		case SD_DISH:
			path = "/mnt/sdcard/";
			break;
		case U_DISH:
			path = "/mnt/usb2/";
			break;
		}
		
		File filepath = createDir(path);
		if (!filepath.exists()) {
			SKToast.makeText(getContext().getString(R.string.recipe_copy_no_path), Toast.LENGTH_SHORT).show();
			return null;
		}
		
		
		File[] files = filepath.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				if (filename.endsWith(".csv")) {
					return true;
				}
				return false;
			}
		});
		
		return files;
	}
	
	
	private ArrayList<String> getFileNameList(File[] files){
		ArrayList<String> nameList = new ArrayList<String>();
		if (files == null || files.length == 0) {
			SKToast.makeText(getContext().getString(R.string.recipe_copy_no_recipe), Toast.LENGTH_SHORT).show();
		}
		
		if (files != null) {
			for(int i = 0; i < files.length; i++){
				nameList.add(files[i].getName());
			}
		}
		
		return nameList;
	}
	
	/**
	 * 
	 * @param pos --fromSpinner 选择的位置
	 * @return 文件名称
	 */
	private String getFromFileNameByPos(int pos){
		if (pos < 0) {
			return null;
		}
		File[] files = null;
		if (mSelectIndex == 0) {
			files = getFile(STORAGE_MEDIA.U_DISH);
		}
		else if (mSelectIndex == 1) {
			files = getFile(STORAGE_MEDIA.SD_DISH);
		}
		else if (mSelectIndex == 2) {
			files = getFile(STORAGE_MEDIA.INSIDE_DISH);
		}
		
		if (files == null || files.length == 0) {
			return null;
		}
		
		return files[pos].getName();
	}

	
	
	/**
	 *  进行导入配方操作
	 */
	private boolean importFile(int togroupId, String fromName){
		if (togroupId < 0 || mSelectIndex < 0 || TextUtils.isEmpty(fromName)) {
			SKToast.makeText(getContext().getString(R.string.recipe_copy_input_error), Toast.LENGTH_SHORT).show();
			return false;
		}

		RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(togroupId);
		oGprop.setmCopyRecipeName(fromName.trim());
		if (mSelectIndex == 0) {
			oGprop.seteSaveMedia(STORAGE_MEDIA.U_DISH);
		}
		else if (mSelectIndex == 1) {
			oGprop.seteSaveMedia(STORAGE_MEDIA.SD_DISH);
		}
		else {
			oGprop.seteSaveMedia(STORAGE_MEDIA.INSIDE_DISH);
		}
		
		CurrentRecipe cinfo=new CurrentRecipe();
		cinfo.setCurrentGroupRecipeId(togroupId);
		cinfo.setCurrentRecipeId(-1);
		RecipeDataCentre.getInstance().msgReadRecipeFromFile(cinfo);

		return true;
	}
	
}
