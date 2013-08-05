package com.android.Samkoonhmi.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import android.util.Log;

/**
 * 用户请求socket
 */
public class SocketTask implements Runnable{

	private static final String TAG="SocketTask";
	//连接socket
	private Socket mClient=null;
	
	public SocketTask(Socket socket){
		mClient=socket;
	}
	
	@Override
	public void run() {
		try {
			if (mClient!=null) {
				DataInputStream dis = new DataInputStream(new BufferedInputStream(mClient.getInputStream()));
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(mClient.getOutputStream()));
				
				byte []buf=new byte[2];
				dis.read(buf);
				
				if (buf[0]==NetParam.TEST) {
					//连接测试
					if (out!=null) {
						buf[0]=0x0;
						buf[1]=0x1;
						out.write(buf);
						out.flush();
					}
					
					dis.read(buf);
					Log.d(TAG, "read :"+buf[0]+","+buf[1]);
					if (buf[0]==NetParam.UPLOAD) {
						//上传数据
						switch (buf[1]) {
						case NetParam.UP_COLLENT:
							//上载历史数据
							CollentFileServer cServer=new CollentFileServer(mClient,dis,out);
							cServer.startUpLoadFile();
							break;
						default:
							break;
						}
					}else if (buf[0]==NetParam.DOWN) {
						//下载数据
					}
				}else {
					
					Log.d(TAG, "read :"+buf[0]+","+buf[1]);
					if (buf[0]==NetParam.UPLOAD) {
						//上传数据
						switch (buf[1]) {
						case NetParam.UP_COLLENT:
							//上载历史数据
							CollentFileServer cServer=new CollentFileServer(mClient,dis,out);
							cServer.startUpLoadFile();
							break;
						default:
							break;
						}
					}else if (buf[0]==NetParam.DOWN) {
						//下载数据
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "SocketTask error!");
		}
	}

}
