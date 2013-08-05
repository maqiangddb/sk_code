package com.android.Samkoonhmi.model;

import java.util.Vector;


/**
 * 一个配方的属性
 * @author Administrator
 *
 */
public class RecipeOprop{
	/*配方ID*/
	private int nRecipeId;
	
	/*配方多语言集合，下标是语言ID号*/
	private Vector<String > sRecipeName = new Vector<String >();
	
	/*配方描述，下标是语言ID号*/
	private Vector<String > sRecipeDescri = new Vector<String >();
	
	/*配方值， 下标是地址号*/
//	private Vector<Double > nRecipeValueList = new Vector<Double >() ;
//	private String[] nRecipeValueList = null;
	
	public RecipeOprop()
	{
	}
	
	public RecipeOprop copyRecipe()
	{
		RecipeOprop mNewRecipe = new RecipeOprop();
		mNewRecipe.nRecipeId = this.nRecipeId;
		
		mNewRecipe.sRecipeName.clear();
		int nSize = this.getsRecipeName().size();
		for(int i = 0; i < nSize; i++)
		{
			mNewRecipe.sRecipeName.add(this.getsRecipeName().get(i));
		}
		
		mNewRecipe.sRecipeDescri.clear();
		nSize = this.getsRecipeDescri().size();
		for(int i = 0; i < nSize; i++)
		{
			mNewRecipe.sRecipeDescri.add(this.getsRecipeDescri().get(i));
		}
		
//		nSize = this.getnRecipeValueList().length;
//		mNewRecipe.nRecipeValueList = new String[nSize];
//		for(int i = 0; i < nSize; i++)
//		{
//			mNewRecipe.nRecipeValueList[i] = getnRecipeValueList()[i];
//		}
		
		return mNewRecipe;
	}
	
	public Vector<String > getsRecipeName() {
		return sRecipeName;
	}

	public void setsRecipeName(Vector<String > sRecipeName) {
		this.sRecipeName = sRecipeName;
	}

	public int getnRecipeId() {
		return nRecipeId;
	}

	public void setnRecipeId(int nRecipeId) {
		this.nRecipeId = nRecipeId;
	}

	public Vector<String > getsRecipeDescri() {
		return sRecipeDescri;
	}

	public void setsRecipeDescri(Vector<String > sRecipeDescri) {
		this.sRecipeDescri = sRecipeDescri;
	}

//	public String[] getnRecipeValueList() {
//		return nRecipeValueList;
//	}
//
//	public void setnRecipeValueList(String[] nRecipeValueList) {
//		this.nRecipeValueList = nRecipeValueList;
//	}
}
