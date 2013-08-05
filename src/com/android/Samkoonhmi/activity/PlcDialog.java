package com.android.Samkoonhmi.activity;

import com.android.Samkoonhmi.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class PlcDialog extends Activity {

	private Button mBtnOk;
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//设置无标题  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //设置全屏  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
		setContentView(R.layout.plc_dialog);
		
		String msg="";
		Intent intent=getIntent();
		if (intent!=null) {
			msg=intent.getStringExtra("msg");
		}
		
		Log.d("SKScene", "MSG:"+msg);
		TextView mText = (TextView)findViewById(R.id.txt_msg);
		mText.setText(msg);
		
		mBtnOk=(Button)findViewById(R.id.btn_ok);
		mBtnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exit();
			}
		});
		
	}
	
	private void exit(){
		this.finish();
	}
	
}
