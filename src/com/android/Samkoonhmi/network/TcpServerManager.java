package com.android.Samkoonhmi.network;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * TCP server
 * 管理类
 */
public class TcpServerManager {

	private static final String TAG="TcpServerManager";
	//是否启动监视服务
	private boolean bStartServer;
	//端口号
	private static int port=5566;
	//服务是否运行着
	private boolean bRuning=false;
	//服务
	private TcpServer server;
	
	// 单例
	private static TcpServerManager sInstance = null;
	public synchronized static TcpServerManager getInstance() {
		if (sInstance == null) {
			sInstance = new TcpServerManager();
		}
		return sInstance;
	}
	
	public TcpServerManager(){
		
	}
	
	private int getInfo(Context context){
		int nPort=5566;
		SharedPreferences share = context.getSharedPreferences("information", 0);
		bStartServer = share.getBoolean("net_server", false);
		String temp = share.getString("net_port", "5566");
		try {
			nPort = Integer.valueOf(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nPort;
	}
	
	/**
	 * 启动服务
	 */
	public void onStart(Context context){
		boolean start=false;
		int temp=getInfo(context);
		
		//Log.d(TAG, "bRuning:"+bRuning+",bStartServer:"+bStartServer+",port:"+port+",temp:"+temp);
		if (bRuning) {
			if (!bStartServer||temp!=port) {
				if (server!=null) {
					//退出
					bRuning=false;
					server.quit();
				}
			}
			if (bStartServer&&(temp!=port)) {
				start=true;
			}
			
		}else {
			if (bStartServer) {
				start=true;
			}
		}
		
		port=temp;
		
		if (start) {
			bRuning=true;
			new Thread(){

				@Override
				public void run() {
					super.run();
					Log.d(TAG, "start port:"+port);
					server=new TcpServer(port);
					server.start();
				}
				
			}.start();
		}
		
	}
	
	/**
	 * 停止服务
	 */
	public void onStop(){
		if (server!=null) {
			bRuning=false;
			server.quit();
		}
	}
}
