package com.android.Samkoonhmi.skwindow;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

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
import com.android.Samkoonhmi.model.RecipeOprop;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre;
import com.android.Samkoonhmi.skglobalcmn.RecipeDataCentre.EditRecipeInfo;

public class SingleRecipeCopyDialog extends Dialog{
	private Spinner fromSpinner = null;
	private Spinner toSpinner = null;
	
	private ArrayList<String> fromList = new ArrayList<String>();
	private ArrayList<String> toList = new ArrayList<String>();
	private ArrayAdapter<String> fromAdapter = null;
	private ArrayAdapter<String> toAdapter = null;
	RecipeOGprop mRecipeData = null;
	private int mSelectIndex = - 1;
	

	public SingleRecipeCopyDialog(Context context) {
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
		
	  mRecipeData = RecipeDataCentre.getInstance().getOGRecipeData(SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId());
	  if(mRecipeData == null ){
		  return ;
	  }
	  
	  if(getRecipeNameList().size() == 0){
		  return ;
	  }
		
	  	fromList.addAll(getRecipeNameList());
		toList.addAll(getRecipeNameList());
		fromAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, fromList);
		toAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, toList);
		fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}
	
	
	private boolean bExistPath = false ;
	
	
	private OnCheckedChangeListener mChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == R.id.recipe_copy_from_U) {
				
				if(mRecipeData != null){
					bExistPath = existPath(STORAGE_MEDIA.U_DISH);
					mSelectIndex = 0;
				}
				
			}
			else if (checkedId == R.id.recipe_copy_from_sd) {
				if(mRecipeData != null){
					mSelectIndex = 1;	
					bExistPath = existPath(STORAGE_MEDIA.SD_DISH);
				}
				
			}
			else {
				if(mRecipeData != null){
					mSelectIndex = 2;
					bExistPath = true;
				}
				
			}
		}
	};
	
	private View.OnClickListener mListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.recipe_copy_confirm) {
				cancel();
				int toRecipeId = toSpinner.getSelectedItemPosition();
				int fromRecipeId = fromSpinner.getSelectedItemPosition();
				copyRecipe(fromRecipeId, toRecipeId);
			}
			else if (v.getId() == R.id.recipde_copy_cancel) {
				cancel();
			}
		}
	};
	
	/**
	 *  进行配方拷贝
	 * @param fromId -源配方ID
	 * @param toId   -目的配方ID
	 */
	private void copyRecipe(int fromId, int toId){
		if(!bExistPath  || (fromId == toId && mSelectIndex == 2 )){
			return ;
		}
		
		if(fromId < 0 || toId < 0){
			return;
		}
		
		String[] items = null;
		if(mSelectIndex == 2){ // 在屏中拷贝
			items = getRecipeData(fromId);
		}
		else{ //从U盘或者SD卡中进行拷贝
			items = getOutRecipeData(fromId);
		}
		
		saveRecipe(toId, items);
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
	
	private boolean isFile(String path){
		if(TextUtils.isEmpty(path)){
			return false;
		}
		
		try {
			File file = new File(path);
			if(!file.exists() ){
				return file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false ;
		}
		return true;
	}
	
	private String mCopyPath ;
	private boolean existPath(STORAGE_MEDIA filetype){
		
		String  path = null;
		if(filetype == STORAGE_MEDIA.SD_DISH ){
			path = "/mnt/sdcard/";
		}
		else if(filetype == STORAGE_MEDIA.U_DISH){
			path = "/mnt/usb2/";
		}
		
		File filepath = createDir(path);
		if (filepath == null || !filepath.exists()) {
			SKToast.makeText(getContext().getString(R.string.recipe_copy_no_path), Toast.LENGTH_SHORT).show();
			return false ;
		}
		
		String file = path + mRecipeData.getsRecipeGName() + ".csv";
		mCopyPath = file;
		if(!isFile(file)){
			SKToast.makeText(getContext().getString(R.string.recipe_copy_no_recipe), Toast.LENGTH_SHORT).show();
			return false ;
		}
		
		return true;
	}
	

	
	private ArrayList<String> getRecipeNameList(){
		
		Vector<RecipeOprop>  recipeList = mRecipeData.getmRecipePropList();
		ArrayList<String> nameList = new ArrayList<String>();
		if(recipeList != null && recipeList.size() > 0){
			for(int i = 0; i < recipeList.size(); i++){
				nameList.add(recipeList.get(i).getsRecipeName().get(SystemInfo.getCurrentLanguageId()));
			}
		}
		
		return nameList;
	}
	
	/**
	 * 
	 * @param recipeId
	 * @param items
	 */
	private void saveRecipe(int recipeId, String[] items){
		if(items == null){
			return ;
		}
		
		CurrentRecipe cRecipe=RecipeDataCentre.getInstance().getCurrRecipe();
		cRecipe.setCurrentRecipeId(recipeId);
		RecipeOGprop oGprop=RecipeDataCentre.getInstance().getOGRecipeData(cRecipe.getCurrentGroupRecipeId());
		if (oGprop==null) {
			return;
		}
		
		RecipeOprop data=null;
		Vector<RecipeOprop>  mRecipeLists=oGprop.getmRecipePropList();
		if (mRecipeLists!=null) {
			for (int i = 0; i < mRecipeLists.size(); i++) {
				if (mRecipeLists.get(i).getnRecipeId()== recipeId) {
					data=mRecipeLists.get(i);
					break;
				}
			}
		}
		if (data==null) {
			return;
		}
		
		//保存
        EditRecipeInfo eInfo=RecipeDataCentre.getInstance().new EditRecipeInfo();
        eInfo.mRecipeData=data;
		eInfo.mRecipeInfo=cRecipe;
		eInfo.sValueList= items;
		RecipeDataCentre.getInstance().msgEditRecipeSave(eInfo);
		
	}
	
	/**
	 *  从屏中 获取拷贝的内容
	 * @param recipeId
	 * @return
	 */
	private String[] getRecipeData(int recipeId){
		String[] items = RecipeDataCentre.getInstance().getRecipeData(SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId(), recipeId, false);
		
		return items;
	}
	
	/**
	 * 从U盘 或者SD卡中 获取拷贝的内容
	 * @param recipeId
	 * @return
	 */
	private String[] getOutRecipeData(int recipeId){
		if(mSelectIndex == -1){
			return null;
		}
		STORAGE_MEDIA type = STORAGE_MEDIA.U_DISH;
		if(mSelectIndex == 1){
			type = STORAGE_MEDIA.SD_DISH;
		}
	
		if(!existPath(type)){
			return null;
		}
		
		try {
			
			DataInputStream fileReaderHand = new DataInputStream(new FileInputStream(mCopyPath));
			BufferedReader readBuffer = new BufferedReader(new InputStreamReader(fileReaderHand, "GBK"));
			String sTmpStr;

			String[] sHeadBuf = null;
			String[] sBodyBuf = null;
			ArrayList<String> elementBuf = new ArrayList<String>();
			// head
			sTmpStr = readBuffer.readLine();
			sHeadBuf = sTmpStr.split(",");

			// 读取body
			final Vector<String[]> sBodyValues = new Vector<String[]>();
			sBodyValues.clear();
			while ((sTmpStr = readBuffer.readLine()) != null) {
				sBodyBuf = sTmpStr.split(",");
				if (sBodyBuf.length != sHeadBuf.length) {
					readBuffer.close();
					return null;
				}
				// element name
				elementBuf.add(sBodyBuf[0]);
				// body
				String[] beanBuf = new String[sBodyBuf.length - 1];
				for (int i = 1; i < sBodyBuf.length; i++) {
					beanBuf[i - 1] = sBodyBuf[i];
				}
				sBodyValues.add(beanBuf);
			}


			 Vector<String[]> sGroupValues = new Vector<String[]>();
			sGroupValues.clear();
			for (int i = 0; i < sHeadBuf.length - 1; i++) {

				String[] sValueList = new String[sBodyValues.size()];
				for (int j = 0; j < sBodyValues.size(); j++) {
					String[] temp = sBodyValues.elementAt(j);
					sValueList[j] = temp[i].trim();
				}

				// 进行检查数据读取的数据是否合法
				if (!isValidData(SystemInfo.getCurrentRecipe().getCurrentGroupRecipeId(), sValueList)) { // 如果输入的数据不合法， 那么就退出
					readBuffer.close();
					return null;
				}

				sGroupValues.add(sValueList);
			}

			
			readBuffer.close();
			String[] elemet = sGroupValues.get(recipeId);
			sGroupValues = null; 
			return elemet;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	
		
	}
	
	
	/**
	 * 检查从文件中读取的数据是否是合法的数据 不合法 return false
	 * 
	 * @return
	 */
	private boolean isValidData(int groupId, String[] valueList) {
		RecipeOGprop recipeData = RecipeDataCentre.getInstance()
				.getOGRecipeData(groupId);
		Vector<DATA_TYPE> eDataTypeList;// 配方元素类型
		if (recipeData == null || recipeData.geteDataTypeList() == null) {
			return false;
		}

		eDataTypeList = recipeData.geteDataTypeList();
		if (eDataTypeList.size() != valueList.length) {
			SKToast.makeText(getContext().getString(R.string.recipe_len_error), Toast.LENGTH_SHORT).show();
			return false;
		}

		boolean ret = true;
		for (int i = 0; i < valueList.length; i++) {
			valueList[i] = valueList[i].trim();

			if (!valueList[i].equals("")) {
				double temp = Double.valueOf(valueList[i]);
				String value = valueList[i];
				DATA_TYPE type = eDataTypeList.get(i);

				switch (type) {
				case INT_16:
					if (!isInt(value) || temp < -32768 || temp > 32767) {
						ret = false;
					}
					break;
				case POSITIVE_INT_16:
					if (!isPosInt(value) || temp < 0 || temp > 65535) {
						ret = false;
					}
					break;
				case INT_32:
					if (!isInt(value) || temp < -2147483648
							|| temp > 2147483647) {
						ret = false;
					}
					break;
				case POSITIVE_INT_32:
					if (!isPosInt(value) || temp < 0 || temp > 4294967295L) {
						ret = false;
					}
					break;
				case FLOAT_32:
					if (temp < -2147483648 || temp > 2147483647) {
						ret = false;
					}
					break;
				case BCD_16:
					if (!isInt(value) || temp < 0 || temp > 9999) {
						ret = false;
					}
					break;
				case BCD_32:
					if (!isInt(value) || temp < 0 || temp > 99999999) {
						ret = false;
					}
					break;
				case BIT_1:
					if (!isInt(value) || temp < 0 || temp > 1) {
						ret = false;
					}
					break;
					
				}
				
				if (!ret) {
					SKToast.makeText(getContext().getString(R.string.recipe_import_error), Toast.LENGTH_SHORT).show();
					return false ;
				}

			} else {
				valueList[i] = 0 + "";
			}
		}
		return true;
	}

	/**
	 * 是否是正整数
	 */
	public boolean isPosInt(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet = false;
		Pattern pattern = Pattern.compile("[0-9]*");
		resulet = pattern.matcher(str).matches();
		return resulet;
	}

	/**
	 * 是否是整数
	 */
	public boolean isInt(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		boolean resulet = false;
		Pattern pattern = Pattern.compile("[-_0-9]*");
		resulet = pattern.matcher(str).matches();
		return resulet;
	}

}
