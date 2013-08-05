package com.android.Samkoonhmi.databaseinterface;


import android.database.Cursor;

public class DataBase {

	public boolean close(Cursor cursor){
		if (cursor!=null) {
			cursor.close();
			return true;
		}
		return false;
	}
}
