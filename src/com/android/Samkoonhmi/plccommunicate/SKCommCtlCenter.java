package com.android.Samkoonhmi.plccommunicate;

import java.util.Vector;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.android.Samkoonhmi.model.PlcConnectionInfo;
import com.android.Samkoonhmi.model.SystemInfo;
import com.android.Samkoonhmi.skenum.PROTOCOL_TYPE;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcCmnDataCtlObj;
import com.android.Samkoonhmi.util.PlcCmnWriteCtlObj;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.TurnDataProp;

public class SKCommCtlCenter {

	/*通信和hand对象*/
	private HandlerThread mCmnCtlThread = null ;
	private CmnCtlHandler mCmnCtlHandler = null; 

	/*穿透控制参数*/
	private short nConnectPlcPort = 3;
	private short nConnectSlavePort = 4;
	private int nScreenNum = 3;
	
	/**
	 * 获得实例对象
	 */
	private static SKCommCtlCenter mCmnCtlManage = null;
	public static SKCommCtlCenter getInstance()
	{
		if(null == mCmnCtlManage)
		{
			mCmnCtlManage = new SKCommCtlCenter();
		}

		return mCmnCtlManage;
	}
	
	/**
	 * 获得通信线程的句柄
	 * @return
	 */
	public CmnCtlHandler getCmnCtlHandler()
	{
		if(null == mCmnCtlHandler)
		{
			/*创建和启动线程*/
			if(null == mCmnCtlHandler)
			{
				mCmnCtlThread = new HandlerThread("SKCommCtl");
				mCmnCtlThread.start();
			}
			
			/*创建通知句柄*/
			mCmnCtlHandler = new CmnCtlHandler(mCmnCtlThread.getLooper());
		}
		
		return mCmnCtlHandler;
	}
	
	/**
	 * 初始化从站通信
	 */
	public void initCmnSlave()
	{
		/*获取所有连接的大小*/
		if(null == SystemInfo.getPlcConnectionList()) return ;
		int nConnectSize = SystemInfo.getPlcConnectionList().size();
		PlcConnectionInfo mConnect = null;

		boolean bMasterPenetrate = false;
		for(int i = 0; i < nConnectSize; i++)
		{
			/*取得连接对象*/
			mConnect = SystemInfo.getPlcConnectionList().get(i);
			if(mConnect != null)
			{
				short eConnectType = mConnect.geteConnectPort();
				SKCommThread mThreadObj = SKCommThread.getComnThreadObj(eConnectType);
				if(null == mThreadObj) continue;

				boolean bUsePenetrate = mConnect.isbUseRelationPort();
				if(bUsePenetrate)
				{
					boolean bMaster = mConnect.isbMasterScreen();
					if(bMaster)
					{
						/*穿透主站*/
						mThreadObj.nScreenType = 1;
						bMasterPenetrate = true;
						if(mConnect.isbConnectScreenPort())
						{
							nConnectSlavePort = eConnectType;
							nScreenNum = mConnect.getnSlaveScreenNum();
						}
						else
						{
							nConnectPlcPort = eConnectType;
						}
					}
					else
					{
						/*穿透从站*/
						mThreadObj.nScreenType = 2;
					}
					
					/*启动线程*/
					mThreadObj.start(true);
				}
				else if(mConnect.getPlcAttributeList() != null)
				{
					/*如果有协议就启动线程*/
					PlcSampInfo mTmpPlcInfo = new PlcSampInfo();
					boolean bHaveMast = false;
					int nPlcSize = mConnect.getPlcAttributeList().size();
					for(int k = 0; k < nPlcSize; k++)
					{
						mTmpPlcInfo.eConnectType = eConnectType;
						mTmpPlcInfo.nProtocolIndex = mConnect.getPlcAttributeList().get(k).getnUserPlcId();
						mTmpPlcInfo.nSampRate = mConnect.getPlcAttributeList().get(k).getnMinCollectCycle();
						mTmpPlcInfo.sProtocolName = mConnect.getPlcAttributeList().get(k).getsPlcServiceType();

						PROTOCOL_TYPE eProType = ProtocolInterfaces.getProtocolInterface().getProtocolType(mTmpPlcInfo);
						if(eProType == PROTOCOL_TYPE.MASTER_MODEL)
						{
							bHaveMast = true;
							break;
						}
					}
					mThreadObj.start(bHaveMast);
				}
			}
		}// end for

		if(bMasterPenetrate)
		{
			getCmnCtlHandler().sendEmptyMessage(MODULE.PENETRATE_MAST_CTL_MSG);
		}
	}

	/**
	 * 主线程截获消息，出现UI刷新
	 * @author Latory
	 *
	 */
	public class CmnCtlHandler extends Handler
	{
		public CmnCtlHandler(Looper looper)
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (msg.what)
			{
			case MODULE.PENETRATE_MAST_CTL_MSG:         //穿透控制中心
			{
				/*如果线程在跑 则继续发送系统从站读刷新*/
				getCmnCtlHandler().removeMessages(MODULE.PENETRATE_MAST_CTL_MSG);
				penetrateCenter();
				getCmnCtlHandler().sendEmptyMessage(MODULE.PENETRATE_MAST_CTL_MSG);
				break;
			}
			case MODULE.SYSTEM_MAST_WRITE:         //实时通信 处理系统主站写数据
			{
				SKCommThread mThreadObj = SKCommThread.getComnThreadObj(nConnectPlcPort);
				if(null == mThreadObj) break ;

				mThreadObj.systemWriteData();
				break;
			}
			case MODULE.USER_READ:                //实时通信 处理写数据
			{
				if(msg.obj != null)
				{
					SKCommThread mThreadObj = SKCommThread.getComnThreadObj(nConnectPlcPort);
					if(null == mThreadObj) break ;

					mThreadObj.readPlcDataByMsg((PlcCmnDataCtlObj)msg.obj);
				}
				break;
			}
			case MODULE.USER_WRITE:               //实时通信 处理写数据
			{
				if(msg.obj != null)
				{
					SKCommThread mThreadObj = SKCommThread.getComnThreadObj(nConnectPlcPort);
					if(null == mThreadObj) break ;

					mThreadObj.writePlcDataByMsg((PlcCmnWriteCtlObj)msg.obj);
				}
				break;
			}
			case MODULE.TURN_MSG_DATA:
			{
				if(msg.obj != null)
				{
					SKCommThread mThreadObj = SKCommThread.getComnThreadObj(nConnectPlcPort);
					if(null == mThreadObj) break ;

					mThreadObj.dealTurnData((TurnDataProp)msg.obj);
				}
				break;
			}
			default:
			{
				break;
			}
			}//end switch
		}
	}

	/**
	 * 穿透协调处理中心
	 */
	public void penetrateCenter()
	{
		SKCommThread mPlcThreadObj = SKCommThread.getComnThreadObj(nConnectPlcPort);
		if(null == mPlcThreadObj) return ;

		/*完成本机上的读*/
		mPlcThreadObj.systemReadData();
		
		SKCommThread mSlaveThreadObj = SKCommThread.getComnThreadObj(nConnectSlavePort);
		if(null == mSlaveThreadObj) return ;
		
		/*开始远程从机上的读写*/
		byte[] nSendData = new byte[4];
		nSendData[0] = 5;
		nSendData[1] = 1;
		nSendData[3] = 10;
		int nTimeout2s = 2000;
		int nTimeOut1s = 1000;
		int nTimeOut5 = 500;
		int nInterval = 10;
		
		PlcConnectionInfo mConnect = null;
		if(nConnectSlavePort == 8)
		{
			if(null == SystemInfo.getPlcConnectionList()) return;
			
			int nConnectSize = SystemInfo.getPlcConnectionList().size();
			for(int j = 0; j < nConnectSize; j++)
			{
				if(SystemInfo.getPlcConnectionList().get(j).geteConnectPort() == nConnectSlavePort)
				{
					mConnect = SystemInfo.getPlcConnectionList().get(j);
					break;
				}
			}
			if(mConnect == null)
			{
				return ;
			}
		}
		
		for(byte i = 1; i <= nScreenNum; i++)
		{
			nSendData[2] = i;
			PlcSampInfo mPlcInfo = new PlcSampInfo();
			mPlcInfo.eConnectType = nConnectSlavePort;
			if(nConnectSlavePort == 8)
			{
				if(mConnect.getPlcAttributeList() != null)
				{
					int nPlcListSize = mConnect.getPlcAttributeList().size();
					if(i < nPlcListSize + 1)
					{
						mPlcInfo.nProtocolIndex = mConnect.getPlcAttributeList().get(i - 1).getnUserPlcId();
					}
//					for(int j = 0; j < nPlcListSize; j++)
//					{
//						mPlcInfo.nProtocolIndex = mConnect.getPlcAttributeList().get(j).getnUserPlcId();
//					}
				}
			}
			boolean bSuccess = CmnPortManage.getInstance().sendData(mPlcInfo, nSendData);
			if(!bSuccess) continue;
			
			int nReadTimes = 0;
			boolean bRcvData = false;
			Vector<Byte > nTmpRecList = new Vector<Byte >();
			while(nReadTimes * nInterval < nTimeout2s)
			{
				try {
    				Thread.sleep(nInterval);  //一次休眠5毫秒
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
				
				nReadTimes++;
				if(nReadTimes *nInterval > nTimeOut5 && bRcvData == false)
				{
					break;
				}
				
				/*获得从站发送来的数据*/
				int nRcvLen = 0;
				mPlcInfo.eConnectType = nConnectSlavePort;
				bRcvData = CmnPortManage.getInstance().getData(mPlcInfo, nTmpRecList);
				if(bRcvData)
				{
					/*清除缓存*/
		        	CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
		        	
					/*如果长度不够  则重新分配内存*/
					nRcvLen = nTmpRecList.size();
					
					/*碰到结束符则退出*/
					if(nRcvLen == 4 &&
							nTmpRecList.get(0) == 5 &&
							nTmpRecList.get(1) == 2 &&
							nTmpRecList.get(2) == i &&
							nTmpRecList.get(3) == 10)
					{
						break;
					}
					
					/*赋值给数组*/
					byte[] nTmpSendBuff = new byte[nRcvLen];
					for(int nRcv = 0; nRcv < nRcvLen; nRcv++)
					{
						nTmpSendBuff[nRcv] = nTmpRecList.get(nRcv);
					}
					
					/*发送给PLC口*/
					mPlcInfo.eConnectType = nConnectPlcPort;
					CmnPortManage.getInstance().sendData(mPlcInfo, nTmpSendBuff);
					
					int nTmpTimes = 0;
					while(nTmpTimes*10 < nTimeOut1s)
					{
						try {
		    				Thread.sleep(10);  //一次休眠5毫秒
		    			} catch (InterruptedException e) {
		    				e.printStackTrace();
		    			}
						nTmpTimes++;
						
						/*等待从站口的数据*/
						mPlcInfo.eConnectType = nConnectSlavePort;
						bRcvData = CmnPortManage.getInstance().getData(mPlcInfo, nTmpRecList);
						if(bRcvData)
						{
							break;
						}
						else
						{
							/*从站口没有数据来，则接收到主站口数据，立即发送到从站口去*/
							mPlcInfo.eConnectType = nConnectPlcPort;
							bRcvData = CmnPortManage.getInstance().getData(mPlcInfo, nTmpRecList);
							if(bRcvData)
							{
								/*清除缓存*/
					        	CmnPortManage.getInstance().clearRcvBuff(mPlcInfo);
					        	
								/*如果长度不够  则重新分配内存*/
								nRcvLen = nTmpRecList.size();
								byte[] nTmpCheckBuff = new byte[nRcvLen];
								
								/*赋值给数组*/
								for(int nRcv = 0; nRcv < nRcvLen; nRcv++)
								{
									nTmpCheckBuff[nRcv] = nTmpRecList.get(nRcv);
								}
								
								mPlcInfo.eConnectType = nConnectSlavePort;
								CmnPortManage.getInstance().sendData(mPlcInfo, nTmpCheckBuff);
							}
							
							bRcvData = false;
						}
					}// end while
				} // end if(bRcvData)
			} // end while
		} // end for
	}// end function
}
