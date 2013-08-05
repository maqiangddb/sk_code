package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * 设置表格起始行序号
 */
public class SKTableRowNum {

	private View view;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	private Context mContext;
	public boolean isShow;
	private Button mBtnOk;
	private Button mBtnCancel;
	private ImageView mBtnPre;
	private ImageView mBtnNext;
	private EditText mTextNum;
	private IOperCall iOperCall;
	private int nMax;
	private int nCurrenValue;
	
	public SKTableRowNum(Context context){
		this.mContext=context;
		inflater=LayoutInflater.from(context);
	}
	
	/**
	 * 初始化
	 */
	private void initPopWindow(int width,int height){
		view = inflater.inflate(R.layout.set_row_num, null);
		
		mBtnPre=(ImageView)view.findViewById(R.id.btn_pre);
		mBtnNext=(ImageView)view.findViewById(R.id.btn_next);
		mBtnOk=(Button)view.findViewById(R.id.btn_ok);
		mBtnCancel=(Button)view.findViewById(R.id.btn_cancel);
		mTextNum=(EditText)view.findViewById(R.id.edt_row_num);
		
		mBtnPre.setOnClickListener(listener);
		mBtnNext.setOnClickListener(listener);
		mBtnOk.setOnClickListener(listener);
		mBtnCancel.setOnClickListener(listener);
		
		mPopupWindow=new PopupWindow(view,width, height);
	}
	
	
	/**
	 * 显示界面
	 */
	public void showPopWindow(int top,int count,int width,int height){
		if(isShow){
			return;
		}
		
		if (mPopupWindow==null) {
			initPopWindow(width, height);
		}
		
		this.nMax=count;
		nCurrenValue=top;
		isShow=true;
		mTextNum.setText(top+"");
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance().getCurrentScene(), Gravity.CENTER, 0, 0);
	}
	
	/**
	 * 点击事件处理
	 */
	View.OnClickListener listener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v.equals(mBtnOk)) {
				//确定
				confim(mTextNum.getText().toString());
			}else if (v.equals(mBtnCancel)) {
				//取消
				cancel();
			}else if (v.equals(mBtnPre)) {
				//减一
				minus();
			}else if (v.equals(mBtnNext)) {
				//加一
				add();
			}
		}
	};
	
	/**
	 * 加一
	 */
	private void add(){
		if (nCurrenValue>=nMax) {
			SKToast.makeText(mContext.getString(R.string.maximum), Toast.LENGTH_SHORT).show();
			return;
		}
		nCurrenValue++;
		mTextNum.setText(nCurrenValue+"");
		mPopupWindow.update();
	}
	
	/**
	 * 减去
	 */
	private void minus(){
		if (nCurrenValue<=1) {
			SKToast.makeText(mContext.getString(R.string.minimum), Toast.LENGTH_SHORT).show();
			return;
		}
		nCurrenValue--;
		mTextNum.setText(nCurrenValue+"");
		mPopupWindow.update();
	}
	
	/**
	 * 确定
	 */
	private void confim(String value){
		if (value==null||value.equals("")) {
			SKToast.makeText(mContext.getString(R.string.input_errors), Toast.LENGTH_SHORT).show();
			return;
		}
		int num=1;
		try {
			num=Integer.valueOf(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (num<1||num>nMax) {
			SKToast.makeText(mContext.getString(R.string.out_of_range)+"\n"
		                    +mContext.getString(R.string.minimum)+":0,"
		                    +mContext.getString(R.string.maximum)+":"
		                    +nMax , Toast.LENGTH_SHORT).show();
			return;
		}
		
		nCurrenValue=num;
		mPopupWindow.dismiss();
		if (iOperCall!=null) {
			iOperCall.onConfirm(nCurrenValue);
		}
		isShow=false;
	}
	
	/**
	 * 取消
	 */
	private void cancel(){
		isShow=false;
		mPopupWindow.dismiss();
	}
	
	/**
	 * 操作确定回调接口
	 */
	public interface IOperCall{
		/**
		 * @param top-起始行序号
		 */
		void onConfirm(int top);
		//取消
		void onCancel();
	}
	
	public void setiOperCall(IOperCall iOperCall) {
		this.iOperCall = iOperCall;
	}
}
