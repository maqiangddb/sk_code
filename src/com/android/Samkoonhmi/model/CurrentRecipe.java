package com.android.Samkoonhmi.model;

public class CurrentRecipe {
	private int currentRecipeId;// 当前配方号
	private int currentGroupRecipeId;// 当前配方组号
	
	public CurrentRecipe() {
		super();
	}
	public CurrentRecipe(int currentRecipeId, int currentGroupRecipeId) {
		super();
		this.currentRecipeId = currentRecipeId;
		this.currentGroupRecipeId = currentGroupRecipeId;
	}
	public int getCurrentRecipeId() {
		return currentRecipeId;
	}
	public void setCurrentRecipeId(int currentRecipeId) {
		this.currentRecipeId = currentRecipeId;
	}
	public int getCurrentGroupRecipeId() {
		return currentGroupRecipeId;
	}
	public void setCurrentGroupRecipeId(int currentGroupRecipeId) {
		this.currentGroupRecipeId = currentGroupRecipeId;
	}


}
