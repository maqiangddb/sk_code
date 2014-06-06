package com.android.Samkoonhmi.skwindow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.network.PhoneManager;
import com.android.Samkoonhmi.network.TcpServerManager;
import com.android.Samkoonhmi.util.ContextUtl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

public class IPSet {

	private View view;
	private PopupWindow mPopupWindow;
	private LayoutInflater inflater;
	public boolean isShow;
	private Button mBtnOk;
	private Button mBtnCancel;
	private CheckBox mCheckBox;
	private EditText mPort;
	private Context mContext;
	private String port="";
	private boolean bStartServer=false;
	private EditText mEdtIp;//ip
	private EditText mEdtMask;//子网掩码
	private EditText mEdtGate;//网关
	private EditText mEdtDns;//dns
	private IntentFilter filter;
	private RadioButton mDhcpRadio;//动态Ip
	private RadioButton mManualRadio;//静态Ip
	private IPSetType mType;
	private String sIp="";
	private String sMask="";
	private String sGate="";
	private String sDns="";
	
	public IPSet(Context context) {
		mContext=context;
		inflater = LayoutInflater.from(context);
		readInfo();
	}

	/**
	 * 初始化
	 */
	private void initPopWindow(IPSetType type) {
		isShow = false;
		
		if (type==IPSetType.SERVER) {
			view = inflater.inflate(R.layout.ipset, null);
			mBtnOk = (Button) view.findViewById(R.id.btn_ok);
			mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
			mBtnOk.setOnClickListener(listener);
			mBtnCancel.setOnClickListener(listener);
				
			mPort=(EditText)view.findViewById(R.id.port);
			mCheckBox=(CheckBox)view.findViewById(R.id.cxb_server);
			mCheckBox.setChecked(bStartServer);
			mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					bStartServer=isChecked;
				}
			});
			
			mPopupWindow = new PopupWindow(view, 300, 160);
		}else if (type==IPSetType.ETHERNET) {
			view = inflater.inflate(R.layout.ethernet_ip, null);
			mBtnOk = (Button) view.findViewById(R.id.okButton);
			mBtnOk.setOnClickListener(listener);
			mBtnCancel=(Button)view.findViewById(R.id.btnCancel);
			mBtnCancel.setOnClickListener(listener);
			mEdtIp=(EditText)view.findViewById(R.id.ipaddress1);
			mEdtMask=(EditText)view.findViewById(R.id.subnet1);
			mEdtGate=(EditText)view.findViewById(R.id.gate1);
			mEdtDns=(EditText)view.findViewById(R.id.dns_edit);
			mDhcpRadio=(RadioButton)view.findViewById(R.id.dhcp_radio);
			mManualRadio=(RadioButton)view.findViewById(R.id.manual_radio);
			
			mDhcpRadio.setOnClickListener(new RadioButton.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mEdtIp.setEnabled(false);
					mEdtMask.setEnabled(false);
					mEdtGate.setEnabled(false);
					mEdtDns.setEnabled(false);
				}
			});
			
			mManualRadio.setOnClickListener(new RadioButton.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mEdtIp.setEnabled(true);
					mEdtMask.setEnabled(true);
					mEdtGate.setEnabled(true);
					mEdtDns.setEnabled(true);
					
					mEdtIp.setText(sIp);
					mEdtMask.setText(sMask);
					mEdtGate.setText(sGate);
					mEdtDns.setText(sDns);
				}
			});
			
			mPopupWindow = new PopupWindow(view, 320, 240);
			
			filter=new IntentFilter();
			filter.addAction("com.samkoon.etnernet.config");
			mContext.registerReceiver(receiver, filter);
			
			Intent intent=new Intent();
			intent.setAction("com.samkoon.ethernet.read");
			mContext.sendBroadcast(intent);
			
			
			
		}
		
	}

	/**
	 * 显示
	 */
	public void showPopWindow(IPSetType type) {
		if (!SKSceneManage.getInstance().isbWindowFocus()) {
			//窗口未获取焦点
			Log.e("AKPopupWindow", "no window forcus ...");
			return ;
		}
		if (isShow) {
			return;
		}
		
		mType=type;
		
		if (mPopupWindow == null) {
			initPopWindow(type);
		}

		if (mType==IPSetType.SERVER) {
			mPort.setText(port);
		}
		
		isShow = true;
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
		mPopupWindow.update();
		mPopupWindow.showAtLocation(SKSceneManage.getInstance()
				.getCurrentScene(), Gravity.CENTER, 0, 0);
	}
	
	
	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			SKSceneManage.getInstance().time = 0;
			if (mType==IPSetType.SERVER) {
				server(v);
			}else if (mType==IPSetType.ETHERNET) {
				ethernet(v);
			}
			
		}
	};
	
	private void ethernet(View v){
		
		if (v.equals(mBtnOk)) {
			//确定
			if (mPopupWindow != null) {
				
				Intent intent=new Intent();
				intent.setAction("com.samkoon.ethernet.setting");
				if (mDhcpRadio.isChecked()) {
					intent.putExtra("eth_type", "DHCP");
				}else {
					boolean ip=validateIp();
					if (!ip) {
						return;
					}
					boolean route=validateGate();
					if (!route) {
						return;
					}
					boolean mask=validateNet();
					if (!mask) {
						return;
					}
					boolean dns=validateDns();
					if (!dns) {
						return;
					}
					
					if (ip&&route&&mask&&dns) {
						intent.putExtra("eth_type", "STATIC");
						Bundle bundle=new Bundle();
						bundle.putString("eth_info_ip", mEdtIp.getText().toString().trim());
						bundle.putString("eth_info_gate", mEdtGate.getText().toString().trim());
						bundle.putString("eth_info_mask", mEdtMask.getText().toString().trim());
						bundle.putString("eth_info_dns", mEdtDns.getText().toString().trim());
						intent.putExtra("eth_info", bundle);
					}else {
						return;
					}
					
				}
				mContext.sendBroadcast(intent);
				mPopupWindow.dismiss();
				PhoneManager.getInstance().readAllIp();
				isShow = false;
				mContext.unregisterReceiver(receiver);
			}
		}else if (v.equals(mBtnCancel)) {
			//关闭
			try {
				if (mPopupWindow != null) {
					mPopupWindow.dismiss();
					isShow = false;
					mContext.unregisterReceiver(receiver);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void server(View v){
		if (v.equals(mBtnOk)) {
			// 确定 
			port=mPort.getText().toString();
			if (port==null||port.equals("")) {
				SKToast.makeText(mContext, ContextUtl.getInstance().getString(R.string.history_port_error), Toast.LENGTH_SHORT).show();
				return;
			}
			if (!isPosInt(port, ContextUtl.getInstance().getString(R.string.history_port_error))) {
				return;
			}
			
			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
				isShow = false;
			}

			saveInfo();
			
			if (bStartServer) {
				TcpServerManager.getInstance().onStart(mContext);
			}else {
				TcpServerManager.getInstance().onStop();
			}
			
			
		} else if (v.equals(mBtnCancel)) {
			// 取消

			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
				isShow = false;
			}
		}
	}
	
	/**
	 * 读取保存信息
	 */
	private void readInfo(){
		SharedPreferences share = mContext.getSharedPreferences("information", 0);
		bStartServer=share.getBoolean("net_server", false);
		port=share.getString("net_port", "5566");
		
	}
	
	/**
	 * 保存信息
	 */
	private void saveInfo(){
		SharedPreferences.Editor mEditor = mContext.getSharedPreferences(
				"information", 0).edit();
		mEditor.putBoolean("net_server", bStartServer);
		mEditor.putString("net_port", port);
		
		mEditor.commit();
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
	 * 以太网ip获取
	 */
	private BroadcastReceiver receiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle config=intent.getBundleExtra("eth_config");
			if (config!=null) {
				config.getBoolean("eth_enabled");
				String ip=config.getString("eth_ip");
				String mask=config.getString("eth_mask");
				String gate=config.getString("eth_gate");
				String dns=config.getString("eth_dns");
				String mode=config.getString("eth_mode");
				
				sIp=ip;
				sMask=mask;
				sDns=dns;
				sGate=gate;
				
				//Log.d("IPset", "mode="+mode);
				if (mode!=null&&mode.equals("manual")) {
					mEdtIp.setEnabled(true);
					mEdtMask.setEnabled(true);
					mEdtGate.setEnabled(true);
					mEdtDns.setEnabled(true);
					
					mEdtIp.setText(ip);
					mEdtMask.setText(mask);
					mEdtGate.setText(gate);
					mEdtDns.setText(dns);
					mDhcpRadio.setChecked(false);
					mManualRadio.setChecked(true);
					
				}else {
					mEdtIp.setEnabled(false);
					mEdtMask.setEnabled(false);
					mEdtGate.setEnabled(false);
					mEdtDns.setEnabled(false);
					mDhcpRadio.setChecked(true);
					mManualRadio.setChecked(false);
				}
				
			}
		}
		
	};
	
	/**
	 * 验证输入的正确性
	 */
	private boolean validateIp() {
		boolean returnB = false;
		String ip1 = mEdtIp.getText().toString().trim();
		boolean ip1b = validateNull(ip1);
		// ip段都不为空
		if (ip1b) {
			boolean ipcheck = validateIp(ip1);
			if (ipcheck) {
				returnB = true;
			} else {
				Toast.makeText(mContext, R.string.iperror, Toast.LENGTH_SHORT).show();
				returnB = false;
			}
		} else {
			Toast.makeText(mContext, R.string.ipnull, Toast.LENGTH_SHORT).show();
			returnB = false;
		}
		return returnB;

	}

	/**
	 * 验证掩码
	 * @return
	 */
	private boolean validateNet() {
		boolean returnB = false;
		String net1 = mEdtMask.getText().toString().trim();
		boolean net1b = validateNull(net1);
		// ip段都不为空
		if (net1b) {
			boolean ipcheck = validateIp(net1);
			if (ipcheck) {
				returnB = true;
			} else {
				Toast.makeText(mContext, R.string.neterror, Toast.LENGTH_SHORT)
						.show();
				returnB = false;
			}
		} else {
			Toast.makeText(mContext, R.string.netnull, Toast.LENGTH_SHORT).show();
			returnB = false;

		}
		return returnB;
	}

	/**
	 * 验证网关
	 * @return
	 */
	private boolean validateGate() {
		boolean returnB = false;
		String gate1 = mEdtGate.getText().toString().trim();
		boolean gate1b = validateNull(gate1);
		// ip段都不为空
		if (gate1b) {
			boolean ipcheck = validateIp(gate1);
			if (ipcheck) {
				returnB = true;
			} else {
				Toast.makeText(mContext, R.string.gateerror, Toast.LENGTH_SHORT)
						.show();
				returnB = false;
			}
		} else {
			Toast.makeText(mContext, R.string.gatenull, Toast.LENGTH_SHORT).show();
			returnB = false;

		}
		return returnB;
	}

	/**
	 * 验证网关
	 * @return
	 */
	private boolean validateDns() {
		boolean returnB = false;
		String dns = mEdtDns.getText().toString().trim();
		if (dns==null||dns.equals("")) {
			return true;
		}
		// ip段都不为空
		boolean ipcheck = validateIp(dns);
		if (ipcheck) {
			returnB = true;
		} else {
			Toast.makeText(mContext, R.string.gateerror, Toast.LENGTH_SHORT)
					.show();
			returnB = false;
		}
		return returnB;
	}

	/**
	 * 验证是否为空
	 * 
	 * @param text
	 * @return
	 */
	private boolean validateNull(String text) {
		if (text == null || "".equals(text)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 验证ip的合法性
	 * 
	 * @param ip
	 * @return
	 */
	private boolean validateIp(String ip) {
		Pattern pattern = Pattern
				.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");

		Matcher matcher = pattern.matcher(ip); // 以验证127.400.600.2为例

		return matcher.matches();
	}

	//IP设置类型
	public enum IPSetType{
		SERVER,
		ETHERNET;
	}
}
