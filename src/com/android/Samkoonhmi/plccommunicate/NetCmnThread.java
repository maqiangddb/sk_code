package com.android.Samkoonhmi.plccommunicate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.NetPramObj;
import com.android.Samkoonhmi.util.NetSlaveProp;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.TurnDataProp;

import android.util.Log;

public class NetCmnThread {
	
	private final String TAG = "NetCmnThread";
	private Vector<ServerThread > mServerThreadList = new Vector<ServerThread >();
	private Vector<ClientThread > mClientThreadList = new Vector<ClientThread >();
	
	private boolean bLocalHaveSlave = false;
	private short nLocalOpenPort = 1;

	/**
	 * 打开网络通信口
	 * @param sIpAddrStr：IP地址比如192.168.1.149
	 * @param nPort：端口号，0-65535
	 * @return
	 */
	public boolean openNet(boolean bHaveSlave, short nOpenPort, PlcConnectionInfo mConnectInfo)
	{
		/*保存转发的口*/
		bLocalHaveSlave = bHaveSlave;
		nLocalOpenPort = nOpenPort;
		
		/*创建监听线程*/
		if(bHaveSlave && mConnectInfo != null && mConnectInfo.getPlcAttributeList() != null)
		{
			PlcSampInfo mTmpPlcInfo = new PlcSampInfo();
			int nPlcListSize = mConnectInfo.getPlcAttributeList().size();
			for(int i = 0; i < nPlcListSize; i++)
			{
				mTmpPlcInfo.eConnectType = nOpenPort;
				mTmpPlcInfo.nProtocolIndex = mConnectInfo.getPlcAttributeList().get(i).getnUserPlcId();
				mTmpPlcInfo.nSampRate = mConnectInfo.getPlcAttributeList().get(i).getnMinCollectCycle();
				mTmpPlcInfo.sProtocolName = mConnectInfo.getPlcAttributeList().get(i).getsPlcServiceType();
				
				int nNetPort = mConnectInfo.getPlcAttributeList().get(i).getnNetPortNum();
				PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
				if(eProType == PROTOCOL_TYPE.SLAVE_MODEL ||bHaveSlave)/*转发数据也建立服务*/
				{
					boolean bContain = false;
					int nSize = mServerThreadList.size();
					for(int k = 0; k < nSize; k++)
					{
						if(mServerThreadList.get(k).getNetPort() == nNetPort)
						{
							bContain = true;
							break;
						}
					}
					if(!bContain)
					{
						ServerThread mServerThread = new ServerThread(nNetPort);
						mServerThreadList.add(mServerThread);
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 关闭网络通信
	 */
	public boolean closeNet()
	{
		/*关闭客服端*/
		int nSize = mClientThreadList.size();
		for(int i = 0; i < nSize; i++)
		{
			mClientThreadList.get(i).closeNet();
		}
		
		/*关闭服务器程序*/
		nSize = mServerThreadList.size();
		for(int i = 0; i < nSize; i++)
		{
			mServerThreadList.get(i).closeNet();
		}
		
		return true;
	}

	/**
	 * 发送数据
	 * @param sSendData
	 * @return
	 */
	public synchronized boolean sendData(byte[] sSendData, NetPramObj mNetPram)
	{
		if(null == mNetPram)
		{
			return false;
		}
		System.out.println(" senddata  ... mNetPram.bServer" + mNetPram.bServer + " size = "+  sSendData.length);
		boolean bSendSucces = false;
		
		/*如果是服务器程序*/
		if(mNetPram.bServer)
		{
			boolean bContain = false;
			int nSize = mServerThreadList.size();
			for(int i = 0; i < nSize; i++)
			{
				if(mServerThreadList.get(i).getNetPort() == mNetPram.nNetPort)
				{
					bContain = true;
					bSendSucces = mServerThreadList.get(i).sendData(sSendData, mNetPram);
					break;
				}
			}

			/*不存在，则新建一个*/
			if(!bContain)
			{
				ServerThread mServerThread = new ServerThread(mNetPram.nNetPort);
				bSendSucces = mServerThread.sendData(sSendData, mNetPram);
				mServerThreadList.add(mServerThread);
			}
		}
		else
		{
			/*客服端线程*/
			boolean bContain = false;
			int nSize = mClientThreadList.size();
			for(int i = 0; i < nSize; i++)
			{
				if(mClientThreadList.get(i).getNetPram().bServer == mNetPram.bServer &&
						mClientThreadList.get(i).getNetPram().nNetPort == mNetPram.nNetPort &&
								mClientThreadList.get(i).getNetPram().sIpAddress.equals(mNetPram.sIpAddress))
				{
					bContain = true;
					bSendSucces = mClientThreadList.get(i).sendData(sSendData);
					break;
				}
			}

			/*不存在，则新建一个*/
			if(!bContain)
			{
				ClientThread mReadThread = new ClientThread(mNetPram);
				bSendSucces = mReadThread.sendData(sSendData);
				mClientThreadList.add(mReadThread);
			}
		}
		
		return bSendSucces;
	}
	
	/**
	 * 接收数据
	 * @param nGetBuff
	 * @return
	 */
	public synchronized boolean getData(Vector<Byte > nGetBuff, NetPramObj mNetPram)
	{
		boolean bSendSucces = false;
		if(null == mNetPram)
		{
			return false;
		}
		
		/*如果是服务器的情况*/
		if(mNetPram.bServer)
		{
			boolean bContain = false;
			int nSize = mServerThreadList.size();
			for(int i = 0; i < nSize; i++)
			{
				if(mServerThreadList.get(i).getNetPort() == mNetPram.nNetPort)
				{
					bContain = true;
					bSendSucces = mServerThreadList.get(i).getData(nGetBuff, mNetPram);
					break;
				}
			}

			/*不存在，则新建一个*/
			if(!bContain)
			{
				ServerThread mServerThread = new ServerThread(mNetPram.nNetPort);
				bSendSucces = mServerThread.getData(nGetBuff, mNetPram);
				mServerThreadList.add(mServerThread);
			}
		}
		else
		{
			/*客服端线程*/
			boolean bContain = false;
			int nSize = mClientThreadList.size();
			for(int i = 0; i < nSize; i++)
			{
				if(mClientThreadList.get(i).getNetPram().bServer == mNetPram.bServer &&
						mClientThreadList.get(i).getNetPram().nNetPort == mNetPram.nNetPort &&
								mClientThreadList.get(i).getNetPram().sIpAddress.equals(mNetPram.sIpAddress))
				{
					bContain = true;
					bSendSucces = mClientThreadList.get(i).getData(nGetBuff);
					break;
				}
			}

			/*不存在，则新建一个*/
			if(!bContain)
			{
				ClientThread mReadThread = new ClientThread(mNetPram);
				bSendSucces = mReadThread.getData(nGetBuff);
				mClientThreadList.add(mReadThread);
			}
		}

		return bSendSucces;
	}
	
	/**
	 * 清除接收缓存
	 */
	public synchronized void clearRcvBuff(NetPramObj mNetPram)
	{
		/*自由口的清除*/
		if(null == mNetPram)
		{
			return ;
		}
		
		/*如果是服务器的情况*/
		if(mNetPram.bServer)
		{
			int nSize = mServerThreadList.size();
			for(int i = 0; i < nSize; i++)
			{
				if(mServerThreadList.get(i).getNetPort() == mNetPram.nNetPort)
				{
					mServerThreadList.get(i).clearRcvBuff(mNetPram);
					break;
				}
			}
		}
		else
		{
			/*客服端线程*/
			int nSize = mClientThreadList.size();
			for(int i = 0; i < nSize; i++)
			{
				if(mClientThreadList.get(i).getNetPram().bServer == mNetPram.bServer &&
						mClientThreadList.get(i).getNetPram().nNetPort == mNetPram.nNetPort &&
						mClientThreadList.get(i).getNetPram().sIpAddress.equals(mNetPram.sIpAddress))
				{
					mClientThreadList.get(i).clearRcvBuff();
					break;
				}
			}
		}
	}
	
	/**
	 * 网络通信接收数据的线程
	 * @author Latory
	 *
	 */
	private class ClientThread extends Thread {
		private boolean bNetOpened = false;
		private NetPramObj mNetPramObj = new NetPramObj();
		private Socket mSocketCmnObj = null;
		private DatagramSocket mDataGramSocketClientObj = null;
		
		/*接收缓存*/
		private Vector<Byte > nRcvBuffList = new Vector<Byte >();
		private byte[] nTmpReadBuff = new byte[256];
		
		/**
		 * 通过IP地址和端口号新建的客户端线程
		 * @param mNetPram
		 */
		public ClientThread(NetPramObj mNetPram)
		{
			if(null != mNetPram)
			{
				mNetPramObj = mNetPram;
			}
			bNetOpened = true;
			start();
		}
		
		/**
		 * 通过套接字新建的客户端线程
		 * @param mSocket
		 */
		public ClientThread(Socket mSocket)
		{
			if(mSocket != null)
			{
				mSocketCmnObj = mSocket;
				mNetPramObj.nNetPort = mSocketCmnObj.getLocalPort();
				mNetPramObj.bServer = true;
				mNetPramObj.bTcpNet = true;
				if(null != mSocketCmnObj.getInetAddress())
				{
					mNetPramObj.sIpAddress = mSocketCmnObj.getInetAddress().getHostAddress();
				}
				
				bNetOpened = true;
				start();
			}
		}
		
		/**
		 * 获得IP地址和端口号属性
		 * @return
		 */
		public NetPramObj getNetPram()
		{
			return mNetPramObj;
		}
		
		/**
		 * 关闭网络通信
		 */
		public boolean closeNet()
		{
			if(mNetPramObj.bServer) return true;
				
			bNetOpened = false;
			this.interrupt();
			try {
    			sleep(10);  
    		} catch (InterruptedException e) {
//    			e.printStackTrace();
    		}
			
			boolean bSuccess = false;
			if(mSocketCmnObj != null )
			{
				try {
					mSocketCmnObj.shutdownInput();
					mSocketCmnObj.shutdownOutput();
					mSocketCmnObj.close();
					bSuccess = true;
				} catch (IOException e) {
//					e.printStackTrace();
					bSuccess = false;
				}
				mSocketCmnObj = null;
			}
			
			return bSuccess;
		}
		
		/**
		 * 移除无效的客户端
		 * @return
		 */
		public void removeClient()
		{
			if(mSocketCmnObj != null )
			{
				try {
					mSocketCmnObj.shutdownInput();
					mSocketCmnObj.shutdownOutput();
					mSocketCmnObj.close();
				} catch (IOException e) {
		//			e.printStackTrace();
				}
				mSocketCmnObj = null;
			}
		}
		
		/**
		 * 开始接收数据
		 */
		public void startRcvThread()
		{
			bNetOpened = true;
		}
		
		/**
		 * 清除接收缓存
		 */
		public void clearRcvBuff()
		{
			/*判断接收容器是否存在*/
			if(null == nRcvBuffList)
			{
				Log.e(TAG, "nRcvBuffList new failed, so restart system");
				return ;
			}

			nRcvBuffList.clear();
		}
		
		/**
		 * 发送数据
		 * @param sSendData
		 * @return
		 * @throws SocketException 
		 */
		public  boolean sendData(byte[] sSendData)
		{
			if (mNetPramObj.bTcpNet) {
				if (null == mSocketCmnObj) {
					if (null == mNetPramObj)
						return false;
					try {
						// StrictMode.setThreadPolicy(new
						// StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
						mSocketCmnObj = new Socket(mNetPramObj.sIpAddress,
								mNetPramObj.nNetPort);

					} catch (UnknownHostException e) {
						// e.printStackTrace();
					} catch (IOException e) {
						// e.printStackTrace();
					}
				}

				if (null == mSocketCmnObj)
					return false;
				if (null == sSendData || sSendData.length <= 0)
					return false;
				startRcvThread();

				try {
					if (null != mSocketCmnObj.getOutputStream()) {
						mSocketCmnObj.getOutputStream().write(sSendData);
						return true;
					}
				} catch (IOException e) {
					closeNet();
					// e.printStackTrace();
				}
				return false;
			} else {
				try {
					{
						if (null == mDataGramSocketClientObj) {
							try {
								mDataGramSocketClientObj = new DatagramSocket();
							} catch (SocketException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						if (null == mDataGramSocketClientObj) {
							return false;
						}
						InetAddress addr = InetAddress
								.getByName(mNetPramObj.sIpAddress);
						DatagramPacket sendPacket = new DatagramPacket(
								sSendData, sSendData.length, addr,
								mNetPramObj.nNetPort);
						startRcvThread();
						try {
							mDataGramSocketClientObj.send(sendPacket);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						return true;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		}
		
		/**
		 * 接收数据
		 * @param nGetBuff
		 * @return
		 */
		public boolean getData(Vector<Byte > nGetBuff)
		{
			nGetBuff.clear();
			
			/*判断接收容器是否存在*/
			if(null == nRcvBuffList)
			{
				Log.e(TAG, "nRcvBuffList new failed, so restart system");
				return false;
			}
			
			int size = nRcvBuffList.size();
			if(size <= 0 || null == nGetBuff) return false;

			for(int i = 0; i < size; i++)
			{
				nGetBuff.add(nRcvBuffList.get(i));
			}

			return true;
		}
		
		/**
		 * 具体接收的数据
		 * @param buffer
		 * @param nSize
		 */
		private void onDataReceived(byte[] buffer, int nSize)
		{
			if(null == buffer ) return ;
			int nLen = buffer.length;
			if(nLen <= 0) return ;

			if(nSize > nLen)
			{
				nSize = nLen;
			}
			if(nSize <= 0) return ;
			
			/*如果大于2048个字节，则自动清除*/
			if(nRcvBuffList.size() > 65535)
			{
				nRcvBuffList.clear();
			}

			for(int i = 0; i < nSize; i++)
			{
				nRcvBuffList.add(buffer[i]);
			}
			
			/*从站处理*/
			if(mNetPramObj.bServer)
			{
				SKCommThread mThreadObj = SKCommThread.getComnThreadObj(nLocalOpenPort);
				if (null != mThreadObj) 
				{
					/*有数据，转发*/
					int nRcvListSize = nRcvBuffList.size();
					if(nRcvListSize > 0)
					{
						byte[] nSlaveList = new byte[nRcvListSize];
						for(int i = 0; i < nRcvBuffList.size(); i++)
						{
							nSlaveList[i] = nRcvBuffList.get(i);
						}
						NetSlaveProp mNetSlave = new NetSlaveProp();
						mNetSlave.mNetPram = mNetPramObj;
						mNetSlave.mRcvData = nSlaveList;
						mThreadObj.getCmnRefreashHandler().obtainMessage(MODULE.SYSTEM_NET_SLAVE, mNetSlave).sendToTarget();
					}
				}
			}
		}
		
		@Override
		public void run() {
			super.run();

			while(true)
			{
				while (bNetOpened)    // 通信口是否打开
				{
					if (mNetPramObj.bTcpNet) {
						if (mSocketCmnObj == null)
							continue;

						try {
							/* 一直等待读数据 */
							if (null != mSocketCmnObj.getInputStream()) {
								int nLen = nTmpReadBuff.length;
								for (int i = 0; i < nLen; i++) {
									nTmpReadBuff[i] = 0;
								}

								int size = mSocketCmnObj.getInputStream().read(
										nTmpReadBuff);
								if (size > 0) {
									onDataReceived(nTmpReadBuff, size);
								}
							}
						} catch (IOException e) {
							closeNet();
							// e.printStackTrace();
						}
					} else {
						if (null == mDataGramSocketClientObj) {
							try {
								mDataGramSocketClientObj = new DatagramSocket();
							} catch (SocketException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (null == mDataGramSocketClientObj) {
							continue;
						}
						DatagramPacket recvPacket = new DatagramPacket(
								nTmpReadBuff, nTmpReadBuff.length);
						try {
							mDataGramSocketClientObj.receive(recvPacket);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						int size = recvPacket.getLength();
						for (int i = 0; i < size; i++) {
							nTmpReadBuff[i] = recvPacket.getData()[i];
						}
						if (size > 0) {
							onDataReceived(nTmpReadBuff, size);
						}
					}
				}
				
				/*休眠500ms*/
				try {
	    			sleep(500);  
	    		} catch (InterruptedException e) {
//	    			e.printStackTrace();
	    		}
			}// end while(true);
		}// end run
	}

	
	/**
	 * 网络通信接收数据的线程
	 * @author Latory
	 *
	 */
	private class ServerThread extends Thread {
		private int nNetPort = 502;
		private boolean bNetOpened = false;
		private ServerSocket mServerSocket = null;
		private Vector<ClientThread > mClientList = new Vector<ClientThread >();
		
		/**
		 * 新建一个服务器线程需要提供监视的端口号
		 * @param nPort
		 */
		public ServerThread(int nPort)
		{
			nNetPort = nPort;
			bNetOpened = true;
			start();
//			if(mServerSocket == null)
//			{
//				try {
//					mServerSocket = new ServerSocket(nPort);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
		}
		
		/**
		 * 获得服务器的监视端口
		 * @return
		 */
		public int getNetPort()
		{
			return nNetPort;
		}
		
		/**
		 * 关闭网络通信
		 */
		public boolean closeNet()
		{
			int nSize = mClientList.size();
			for(int i = 0; i < nSize; i++)
			{
				mClientList.get(i).closeNet();
			}
			bNetOpened = false;
			
			if(mServerSocket != null)
			{
				try {
					mServerSocket.close();
				} catch (IOException e) {
//					e.printStackTrace();
				}
			}
			mServerSocket = null;
			
			return true;
		}
		
		/**
		 * 发送数据
		 * @param sSendData
		 * @return
		 */
		public boolean sendData(byte[] sSendData, NetPramObj mNetPram)
		{
			if(null == mNetPram) return false;
			
			boolean bSendSucces = false;
			int nSize = mClientList.size();
			for(int i = nSize -1; i >= 0; i--)
			{
				if(mClientList.get(i).getNetPram().bServer == mNetPram.bServer &&
						mClientList.get(i).getNetPram().nNetPort == mNetPram.nNetPort &&
						mClientList.get(i).getNetPram().sIpAddress.equals(mNetPram.sIpAddress))
				{
					bSendSucces = mClientList.get(i).sendData(sSendData);
					break;
				}
			}
			
			return bSendSucces;
		}
		
		/**
		 * 接收数据
		 * @param nGetBuff
		 * @return
		 */
		public boolean getData(Vector<Byte > nGetBuff, NetPramObj mNetPram)
		{
			boolean bSendSucces = false;
			int nSize = mClientList.size();
			for(int i = nSize -1; i >= 0; i--)
			{
				if(mClientList.get(i).getNetPram().bServer == mNetPram.bServer &&
						mClientList.get(i).getNetPram().nNetPort == mNetPram.nNetPort &&
								mClientList.get(i).getNetPram().sIpAddress.equals(mNetPram.sIpAddress))
				{
					bSendSucces = mClientList.get(i).getData(nGetBuff);
					break;
				}
			}

			return bSendSucces;
		}
		
		/**
		 * 清除接收缓存
		 */
		public void clearRcvBuff(NetPramObj mNetPram)
		{
			int nSize = mClientList.size();
			for(int i = nSize -1; i >= 0; i--)
			{
				if(mClientList.get(i).getNetPram().bServer == mNetPram.bServer &&
						mClientList.get(i).getNetPram().nNetPort == mNetPram.nNetPort &&
								mClientList.get(i).getNetPram().sIpAddress.equals(mNetPram.sIpAddress))
				{
					mClientList.get(i).clearRcvBuff();
					break;
				}
			}
		}
		
		@Override
		public void run() {
			super.run();

			while(true)
			{
				while (bNetOpened)    // 通信口是否打开
				{
					if(mServerSocket == null)
					{
						try {
							mServerSocket = new ServerSocket(nNetPort);
						} catch (IOException e) {
							String sShowInfo = "create server failed, port is：" + nNetPort;
							SKPlcNoticThread.getInstance().getMainUIHandler().obtainMessage(MODULE.NOTIC_SHOW_TOAST, sShowInfo).sendToTarget();
					//		e.printStackTrace();
						}
					}
					
					if(null == mServerSocket) continue;

					Socket mNewClient = null;
					try {
						mNewClient = mServerSocket.accept();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(null != mNewClient)
					{
						ClientThread mNewClientThread = new ClientThread(mNewClient);
						if(null != mNewClientThread)
						{
//							int nSize = mClientList.size();
//							for(int i = nSize -1; i >= 0; i--)
//							{
//								if(mClientList.get(i).getNetPram().bServer == mNewClientThread.getNetPram().bServer &&
//										mClientList.get(i).getNetPram().nNetPort == mNewClientThread.getNetPram().nNetPort &&
//										mClientList.get(i).getNetPram().sIpAddress.equals(mNewClientThread.getNetPram().sIpAddress))
//								{
//									mClientList.get(i).closeNet();
//									mClientList.get(i).removeClient();
//									mClientList.remove(i);
//								}
//							}
							mClientList.add(mNewClientThread);
						}
					}
				}
				
				/*休眠500ms*/
				try {
	    			sleep(500);  
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
			}// end while(true);
		}// end run
	}
}
