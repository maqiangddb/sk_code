package com.android.Samkoonhmi.activity;

import android.view.View;
import android.view.inputmethod.BaseInputConnection;

public class MyBaseInputConnection extends BaseInputConnection {

	private String text="";
	public MyBaseInputConnection(View targetView, boolean fullEditor) {
		super(targetView, fullEditor);
		// TODO Auto-generated constructor stub
		
	}
	@Override
	public boolean commitText(CharSequence text, int newCursorPosition) {
		// TODO Auto-generated method stub
		text=text.toString();
		return true;
	}
	


}
