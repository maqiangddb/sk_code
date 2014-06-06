package com.android.Samkoonhmi.model.skglobalcmn;

import java.util.Vector;
import com.android.Samkoonhmi.model.RecipeOGprop;

/**
 * recipe data collect centre.
 * @author Latory
 * @version v 1.0.0.1
 */
public class RecipeDataProp {

	/**
	 * recipe Group number
	 */
	private short nRecipeGroupNum = 0;

	/**
	 * recipe Group list
	 */
	private Vector<RecipeOGprop> mRecipeGroupList = null;

	private RecipeDataProp() {
	}

	/**
	 * 获取配方属性的单例
	 */
	private static RecipeDataProp mRecipeInfo = null;
	public synchronized static RecipeDataProp getInstance() {
		if (null == mRecipeInfo) {
			mRecipeInfo = new RecipeDataProp();
		}
		return mRecipeInfo;
	}

	/**
	 * 获得所有的配方组
	 * @return
	 */
	public Vector<RecipeOGprop> getmRecipeGroupList() {
		if (null == mRecipeGroupList) {
			mRecipeGroupList = new Vector<RecipeOGprop>();
		}
		return mRecipeGroupList;
	}

	/**
	 * 设置所有的配方组
	 * @param mRecipeGroupList
	 */
	public void setmRecipeGroupList(Vector<RecipeOGprop> mRecipeGroupList) {
		this.mRecipeGroupList = mRecipeGroupList;
	}

}
