package com.android.Samkoonhmi.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

/**
 * Tcp服务
 */
public class TcpServer {

	private static final String TAG="TcpServer";
	//线程池
	private ExecutorService mExecutorService;
	//监视端口
	private int nPort;
	//连接服务
	private ServerSocket mServer;
	//退出
	private boolean quit=false;
	
	public TcpServer(int port){
		this.nPort=port;
		//创建线程池，池中具有(cup个数*20)条线程
		mExecutorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 20);
	}
	
	/**
	 * 退出服务
	 */
	public void quit(){
		this.quit=true;
		try {
			if (mServer!=null) {
				mServer.close();
			}
			if (mExecutorService!=null) {
				mExecutorService.shutdown();
			}
			Log.d(TAG, "AK Tcp server stop...");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "AK, tcp server quit error!!!");
		}
	}

	
	/**
	 * 启动服务
	 */
	public void start(){
		try {
			mServer=new ServerSocket(nPort);
			while (!quit) {
				Socket socket=mServer.accept();
				Log.d(TAG, "AK Tcp server start,nPort:"+nPort);
				//支持都用户并发访问，采用线程池管理每一个用户的连接请求
				mExecutorService.execute(new SocketTask(socket));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "AK, tcp server start error!!!");
		}
	}
}
