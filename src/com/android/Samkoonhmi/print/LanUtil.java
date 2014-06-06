package com.android.Samkoonhmi.print;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Vector;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * AK以太网打印
 */
public class LanUtil {

	private static final String TAG="LanUtil";
	//默认IP
	private String sIp="192.168.1.100";
	//默认端口
	private int nPort=9100;
	//单列
	private static LanUtil sInstance = null;
	public static LanUtil getInstance() {
		if (sInstance == null) {
			sInstance = new LanUtil();
		}
		return sInstance;
	}
	
	/**
	 * 打印文字
	 */
	private Vector<String> mList=null;
	public synchronized void printText(Vector<String> list){
		mList=list;
		
		new Thread(){

			@Override
			public void run() {
				super.run();
				try {
					
					Socket mSocket=new java.net.Socket(); 
					mSocket.connect(new InetSocketAddress(sIp , nPort),1000); // 创建一个 socket 
					
					// 创建输入输出数据流
					PrintWriter mWriter = new PrintWriter(new OutputStreamWriter(mSocket.getOutputStream(), "GBK"));
					mWriter.write(new char[]{0x1C,0x26});
					for (int i = 0; i < mList.size(); i++) {
						String tmp=mList.get(i);
						if (tmp!=null) {
							mWriter.println(tmp);
						}
					}

					
					//Log.d(TAG, "ak print .... ");
					mWriter.flush();
					mWriter.close();
					mSocket.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}.start();
		
	}
	
	
	/**
	 * 打印图片
	 */
	private Bitmap mBitmap=null;
	public void printBitmap(Bitmap bitmap){
		
		mBitmap=bitmap;
		new Thread(){

			@Override
			public void run() {
				super.run();
				try {
					
					Socket mSocket=new java.net.Socket(); 
					mSocket.connect(new InetSocketAddress(sIp , nPort),1000); // 创建一个 socket 
					PrintWriter mWriter = new PrintWriter(mSocket.getOutputStream());// 创建输入输出数据流
					
					//初始化
					mWriter.write(0x1B);
					mWriter.write(0x40);
					
//				    for (int i = 0; i <30; i++) {
//				    	mWriter.println(" ");
//					}
//					
//					char[] pp=new char[]{0x1B,0x2A,0x0,0x4,0x0};
//					char[] data=new char[]{0x1,0x1,0x1,0x2,0x2,0x2,0x3,0x3,0x3,0x4,0x4,0x4};
//					
//					mWriter.write(pp);
//					mWriter.write(data);
//					
//					mWriter.write(0x2A);
					
					mWriter.flush();
					mWriter.close();
					mSocket.close();
					
					Log.d(TAG, "ak print bitmap .... ");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}.start();
		
	}
	
	/**
	 * 获取打印IP
	 */
	public String getsIp() {
		return sIp;
	}

	/**
	 * 设置打印IP
	 */
	public void setsIp(String sIp) {
		this.sIp = sIp;
	}

	/**
	 * 获取打印端口
	 */
	public int getnPort() {
		return nPort;
	}

	/**
	 * 设置打印端口
	 */
	public void setnPort(int nPort) {
		this.nPort = nPort;
	}
}
