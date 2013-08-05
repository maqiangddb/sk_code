package com.android.Samkoonhmi.plccommunicate;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Vector;

import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;
import com.android.Samkoonhmi.skwindow.SKProgress;
import com.android.Samkoonhmi.skwindow.SKToast;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.MODULE;
import com.android.Samkoonhmi.util.PlcSampInfo;
import com.android.Samkoonhmi.util.SEND_DATA_STRUCT;

/**
 * 通信中 地址通知的线程，需要调用获得接口函数getInstance()，通过接口addNoticProp添加地址，
 * 然后调用start()接口启动线程，只要你用的地方注册了回调接口IPlcNoticCallBack中的方法，
 * 你就可以得到通知，得到你想要的地址值。
 * @author Latory
 *
 */
public class SKPlcNoticThread
{
	/*刷新线程*/
	private HandlerThread mNoticThread = null ;
	private MainUIHandler mMainHandler = null; 
	private CmnHandler mNoticHandler = null; 
	
	/*地址与通知的值的和通知属性集合的一个映射*/
	private Vector<AddrNoticProp > addrMapIndexList = new Vector<AddrNoticProp >();

	/*零时发送结构体*/
	private SEND_DATA_STRUCT mSendData = new SEND_DATA_STRUCT();
	private PlcSampInfo mTmpPlcInfo = new PlcSampInfo();
	private Vector<Byte > nTmpByteList = new Vector<Byte >();
	
	/*控制线程启动和停止的变量*/
	private boolean m_bThreadLoop = false;
	
	private SKPlcNoticThread()
	{
	}
	/**
	 * 通知线程的单实例
	 */
	private static SKPlcNoticThread m_mNoticThredObj = null;
	public synchronized static SKPlcNoticThread getInstance(){
		if (null == m_mNoticThredObj) {
			m_mNoticThredObj = new SKPlcNoticThread();
		}
		return m_mNoticThredObj;
	}
	
	/**
	 * 获得通知线程的句柄
	 * @return
	 */
	public CmnHandler getCmnNoticHandler()
	{
		if(null == mNoticHandler)
		{
			/*创建和启动线程*/
			if(null == mNoticThread)
			{
				mNoticThread = new HandlerThread("addrNotic");
				mNoticThread.start();
			}
			
			/*创建通知句柄*/
			mNoticHandler = new CmnHandler(mNoticThread.getLooper());
		}
		
		return mNoticHandler;
	}
	
	/**
	 * 获得主线程的句柄
	 * @return
	 */
	public MainUIHandler getMainUIHandler()
	{
		if(null == mMainHandler)
		{
			/*创建通知句柄*/
			mMainHandler = new MainUIHandler(Looper.getMainLooper());
		}
		
		return mMainHandler;
	}
	
	/**
	 * 地址通知结构体
	 * @author Latory
	 *
	 */
	private class AddrNoticProp{
		Vector<IPlcNoticCallBack> nNoticPropList = new Vector<IPlcNoticCallBack>();     //通知属性list
		Vector<Byte > nByteList = new Vector<Byte >();   //当前地址的值
		boolean bBitAddr = false;
		AddrProp mAddrInfo = null;
	}
	
	/**
	 * 添加地址 注册接口时 发送消息结构体
	 * @author Latory
	 *
	 */
	private class AddAddrProp{
		AddrProp mAddrInfo = null;
		IPlcNoticCallBack mCallBack = null;
		boolean bBitAddr;
	}
	
	/**
	 * 增加通知地址属性函数接口
	 * @param addr
	 * @param noticProp
	 */
	public void addNoticProp(AddrProp addr, IPlcNoticCallBack mCallBack, boolean bBitAddr)
	{
		if(null == addr)
		{
			Log.e("addNoticProp", "address object is null, Register call back failed");
			return ;
		}
		
		/*通知*/
		AddAddrProp mCallBackInfo = new AddAddrProp();
		mCallBackInfo.mAddrInfo = addr;
		mCallBackInfo.mCallBack = mCallBack;
		mCallBackInfo.bBitAddr = bBitAddr;
		
		getCmnNoticHandler().obtainMessage(MODULE.ADD_CALL_BACK, mCallBackInfo).sendToTarget();
	}
	
	/**
	 * 注销回调通知接口
	 * @param mCallBack
	 */
	public synchronized void destoryCallback(IPlcNoticCallBack mCallBack)
	{
		if(mCallBack == null ) return ;
		
		//Log.d("SKScene", "mCallBack:"+mCallBack);
		getCmnNoticHandler().obtainMessage(MODULE.DESTORY_CALLBACK, mCallBack).sendToTarget();
	}
	
	/**
	 * 线程启动
	 */
	public void start()
	{
		/*创建通知线程*/
		if(null == mNoticThread)
		{
			mNoticThread = new HandlerThread("addrNotic");
			mNoticThread.start();
		}

		/*创建通知刷新UI句柄*/
		if(null == mMainHandler)
		{
			mMainHandler = new MainUIHandler(Looper.getMainLooper());
		}
		
		/*创建线程刷新句柄*/
		if(null == mNoticHandler)
		{
			mNoticHandler = new CmnHandler(mNoticThread.getLooper());
		}
		
		/*刷新线程的开关*/
		m_bThreadLoop = true;
	}
	
	/**
	 * 线程不进行消息循环
	 */
	public void stop()
	{
		if (mNoticHandler==null) return;
		
		m_bThreadLoop = false; 
		mNoticHandler.removeMessages(MODULE.ADD_CALL_BACK);
		mNoticHandler.removeMessages(MODULE.PLC_NOTIC_VALUE_CHANGE);
		mNoticHandler.removeMessages(MODULE.LOCAL_ADDR_REFREASH);
		
		/*清除地址*/
//		mNoticHandler.sendEmptyMessage(MODULE.NOTIC_CLEAR_CALLBACK);
	}
	
	/**
	 * 地址通知线程截获消息，循环刷新
	 * @author Latory
	 *
	 */
	public class CmnHandler extends Handler
	{
		public CmnHandler(Looper looper)
		{
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case MODULE.LOCAL_ADDR_REFREASH:  //线程刷新
			{
				if(!m_bThreadLoop) break;
				
				/*如果有消息存在，则清除其他刷新消息，都是一样的结果*/
				if(null != mNoticHandler)
				{
					mNoticHandler.removeMessages(MODULE.LOCAL_ADDR_REFREASH);
				}
				refreashLocalSlave();
				break;
			}
			case MODULE.ADD_CALL_BACK:
			{
//				if(!m_bThreadLoop) break;
				msgAddNoticProp((AddAddrProp)msg.obj);
				break;
			}
			case MODULE.DESTORY_CALLBACK:
			{
				msgDestoryCallback((IPlcNoticCallBack)msg.obj);
				break;
			}
			case MODULE.PLC_NOTIC_VALUE_CHANGE:
			{
				if(!m_bThreadLoop) break;
//				plcThreadNotic((PlcNoticValue)msg.obj);
				break;
			}
			case MODULE.NOTIC_CLEAR_CALLBACK:
			{
				clearAllNotic();
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
	 * 主线程截获消息，出现UI刷新
	 * @author Latory
	 *
	 */
	public class MainUIHandler extends Handler
	{
		public MainUIHandler(Looper looper)
		{
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			switch (msg.what)
			{
			case MODULE.NOTIC_UI_REFREASH:         //PLC通知
			{
				if(!m_bThreadLoop) break;
	//			dealPlcNotic((AddrNoticProp)msg.obj);
				break;
			}
			case MODULE.NOTIC_SHOW_TOAST:         //线程通知显示提示框
			{
				SKToast.showText((String)msg.obj, Toast.LENGTH_LONG);
				break;
			}
			case MODULE.NOTIC_SHOW_PRESS:         //线程通知显示进度条
			{
				SKProgress.show((String)msg.obj);
				break;
			}
			case MODULE.NOTIC_HIDE_PRESS:         //线程通知隐藏进度条
			{
				SKProgress.hide();
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
	 * 清除所有通知
	 */
	private void clearAllNotic()
	{
		if(null != addrMapIndexList)
		{
			addrMapIndexList.clear();
		}
	}
	
	/**
	 * 串口读写线程通知刷新
	 * @param mNoticInfo
	 */
//	private void plcThreadNotic(PlcNoticValue mNoticInfo)
//	{
//		if(null == addrMapIndexList || addrMapIndexList.isEmpty()) return ;
//		if(null == mNoticInfo.nValueList) return ;
//		
//		/*查找是否有地址需要通知*/
//		int nAddrSize = addrMapIndexList.size();
//		for(int i = 0; i < nAddrSize; i++)
//		{
//			AddrNoticProp mAddrProp = addrMapIndexList.get(i);
//			
//			/*PLC号相同*/
//			if(mAddrProp.mAddrInfo.nUserPlcId == mNoticInfo.mAddrProp.nUserPlcId &&
//							mAddrProp.mAddrInfo.nPlcStationIndex == mNoticInfo.mAddrProp.nPlcStationIndex &&
//								mAddrProp.mAddrInfo.nRegIndex == mNoticInfo.mAddrProp.nRegIndex)
//			{
//				int nOldStart = 0;
//	//			int nOldEnd = 0;
//				int nNewStart = 0;
//				int nNewEnd = 0;
//				
//				int nByteSpan = 1;
//				if(mAddrProp.bBitAddr)
//				{
//					nByteSpan = 1;
//				}
//				else
//				{
//					nByteSpan = 2;
//				}
//				
//				/*计算注册地址的起始值*/
//				if(mAddrProp.mAddrInfo.nAddrValue < mNoticInfo.mAddrProp.nAddrValue)
//				{
//					nOldStart = (mNoticInfo.mAddrProp.nAddrValue - mNoticInfo.mAddrProp.nAddrValue) * nByteSpan;
//					nNewStart = 0;
//				}
//				else
//				{
//					nNewStart = (mAddrProp.mAddrInfo.nAddrValue - mNoticInfo.mAddrProp.nAddrValue) * nByteSpan;
//					nOldStart = 0;
//				}
//				
//				/*计算注册地址的终止值*/
//				if(mAddrProp.mAddrInfo.nAddrValue + mAddrProp.mAddrInfo.nAddrLen <= mNoticInfo.mAddrProp.nAddrValue + mNoticInfo.mAddrProp.nAddrLen)
//				{
//	//				nOldEnd = mAddrProp.mAddrInfo.nAddrLen * nByteSpan -1;
//					nNewEnd = (mAddrProp.mAddrInfo.nAddrValue + mAddrProp.mAddrInfo.nAddrLen - mNoticInfo.mAddrProp.nAddrValue) * nByteSpan -1;
//				}
//				else
//				{
//					nNewEnd = mNoticInfo.mAddrProp.nAddrLen * nByteSpan -1;
//	//				nOldEnd = (mNoticInfo.mAddrProp.nPlcStartAddr + mNoticInfo.mAddrProp.nAddrLen - mAddrProp.mAddrInfo.nPlcStartAddr) * nByteSpan -1;
//				}
//				
//				/*接收到的数据处理*/
//				byte[] list = mNoticInfo.nValueList;
//				int nListLen = list.length;
//				
//				int nLen = nNewEnd - nNewStart + 1;
//				if(nNewEnd >= nListLen)
//				{
//					nLen = nListLen - nNewStart;
//				}
//				
//				/*判断数据是否相同，不相同则通知*/
//				boolean bNotic = false;
//				for(int k = 0; k < nLen; k++)
//				{
//					/*数据不够则填充0*/
//					while(nOldStart + k >= mAddrProp.nByteList.size())
//					{
//						mAddrProp.nByteList.add((byte)0);
//						bNotic = true;
//					}
//					
//					/*比较是否相同*/
//					byte tmp = mAddrProp.nByteList.get(nOldStart + k);
//					if(tmp != list[nNewStart + k])
//					{
//						mAddrProp.nByteList.set(nOldStart + k, list[nNewStart + k]);
//						bNotic = true;
//					}
//				}
//				
//				/*需要通知刷新*/
//				if(bNotic)
//				{
//					/*通知回调*/
//					dealPlcNotic(mAddrProp);
//				}
//			}
//		}
//	}
	
	/**
	 * 增加通知地址属性函数接口
	 * @param addr
	 * @param noticProp
	 */
	private void msgAddNoticProp(AddAddrProp addrInfo)
	{
		if(null == addrInfo.mAddrInfo)
		{
			Log.e("msgAddNoticProp", "address object is null, Register call back failed");
			return ;
		}
		
		boolean bContain = false;
		int nIndex = 0;
		
		/*添加地址*/
		if(null == addrMapIndexList)
		{
			addrMapIndexList = new Vector<AddrNoticProp >();
		}
		
		/*取得地址的集合*/
		int nAddrSize = addrMapIndexList.size();
		for(int i = 0; i < nAddrSize; i++)
		{
			AddrProp mTmpAddr = addrMapIndexList.get(i).mAddrInfo;

			if(mTmpAddr.eConnectType == addrInfo.mAddrInfo.eConnectType &&
					mTmpAddr.nAddrLen == addrInfo.mAddrInfo.nAddrLen &&
					mTmpAddr.nUserPlcId == addrInfo.mAddrInfo.nUserPlcId &&
					mTmpAddr.nRegIndex == addrInfo.mAddrInfo.nRegIndex &&
					mTmpAddr.nAddrValue == addrInfo.mAddrInfo.nAddrValue && 
					mTmpAddr.nPlcStationIndex == addrInfo.mAddrInfo.nPlcStationIndex &&
//					mTmpAddr.eAddrRWprop == addrInfo.mAddrInfo.eAddrRWprop &&
					mTmpAddr.sPlcProtocol.equals(addrInfo.mAddrInfo.sPlcProtocol))
			{
				nIndex = i;
				bContain = true;
				break;
			}
		}

		/*是否已经存在这个地址*/
		if(bContain)
		{
			if(!addrMapIndexList.get(nIndex).nNoticPropList.contains(addrInfo.mCallBack))
			{
				addrMapIndexList.get(nIndex).nNoticPropList.add(addrInfo.mCallBack);
				addrMapIndexList.get(nIndex).bBitAddr = addrInfo.bBitAddr;

				/*注册时更新数据*/
				regCallback(addrInfo);
			}
		}
		else
		{
			AddrNoticProp tmpANP = new AddrNoticProp();
			tmpANP.nNoticPropList.clear();
			tmpANP.nNoticPropList.add(addrInfo.mCallBack);
			tmpANP.bBitAddr = addrInfo.bBitAddr;
			tmpANP.mAddrInfo = addrInfo.mAddrInfo;
			addrMapIndexList.add(tmpANP);

			/*注册时更新数据*/
			regCallback(addrInfo);
		}
	}
	
	/**
	 * 实际执行删除回调
	 * @param mCallback
	 */
	private void msgDestoryCallback(IPlcNoticCallBack mCallback)
	{
		if(null == addrMapIndexList) return ;
		
		/*从所有通知属性中查找*/
		int nMapIndex = -1;
		int nIndex = -1;
		int nNoticsize = addrMapIndexList.size();
		for(int i = 0; i < nNoticsize; i++)
		{
			AddrNoticProp mNTprop = addrMapIndexList.get(i);
			if(null == mNTprop) continue;
			
			boolean bSearch = false;
			int nCallbSize = mNTprop.nNoticPropList.size();
			for(int k = 0; k < nCallbSize; k++)
			{
				if(mNTprop.nNoticPropList.get(k).equals(mCallback))
				{
					nMapIndex = i;
					nIndex = k;
					bSearch = true;
					break;
				}
			}
			
			/*如果已经找到 退出循环*/
			if(bSearch)
			{
				break;
			}
		}
		
		/*执行删除操作*/
		if(nMapIndex < 0 || nIndex < 0)
		{
			return ;
		}
		
		AddrNoticProp mNoticprop = addrMapIndexList.get(nMapIndex);
		if(null == mNoticprop) return ;
		
		/*如果所有的回调都不存在，则删除通知对象*/
		mNoticprop.nNoticPropList.remove(nIndex);
		if(mNoticprop.nNoticPropList.isEmpty())
		{
			addrMapIndexList.remove(nMapIndex);
		}
	}

	/**
	 * 回调接口，回调到控件更新，每个有是否显示，是否触控，或者状态变化的控件都要实现这接口。
	 * 取得nstatusValue值后 一定要根据数据类型调用PlcRegCmnStcTools.intToUInt(nStatusValue);等
	 * 转换成你需要的数据类型。
	 * @author Latory
	 * 
	 */
	public interface IPlcNoticCallBack{
		
		/*返回的值都存在int型的内存中使用方法参照以上说明*/
		public void addrValueNotic(Vector<Byte > nStatusValue);
	}
	
	/**
	 * 处理回调，更新主线程UI
	 * @param mCallBackProp
	 */
	private void dealPlcNotic(AddrNoticProp mCallBackProp)
	{
		if(null == mCallBackProp) return ;
		if(mCallBackProp.nNoticPropList.isEmpty()) return ;
		
		/*回调属性，包括回调对象信息和回调通知的值*/
		int nIndexSize = mCallBackProp.nNoticPropList.size();
		
		for(int i = 0; i < nIndexSize; i++)
		{
			IPlcNoticCallBack mCallback = mCallBackProp.nNoticPropList.get(i);

			/*根据回调的类型回调*/
			if(mCallback != null)
			{
				mCallback.addrValueNotic(mCallBackProp.nByteList);
			}
		}
	}
	
	/**
	 * 注册时更新回调
	 * @param mCallBackProp
	 */
	private void regCallback(AddAddrProp mCallBackProp)
	{
		if(null == mCallBackProp || mCallBackProp.mCallBack == null || mCallBackProp.mAddrInfo == null) return ;
		
		AddrProp mAddrProp =  mCallBackProp.mAddrInfo;
		
		/*读写属性*/
		mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
		mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_ONCE_R;
		if(mCallBackProp.bBitAddr)
		{
			mSendData.eDataType = DATA_TYPE.BIT_1;
		}
		else
		{
			mSendData.eDataType = DATA_TYPE.INT_16;
		}
		
		Vector<Byte > nNoticList = new Vector<Byte >();
		PlcRegCmnStcTools.getRegBytesData(mAddrProp, nNoticList, mSendData);

		mCallBackProp.mCallBack.addrValueNotic(nNoticList);
	}
	
	/**
	 * 刷新内部地址和slave地址
	 */
	private void refreashLocalSlave()
	{
		/* init send data struct */
		int nAddrSize = addrMapIndexList.size();
		for(int i = 0; i < nAddrSize; i++)
		{
			AddrNoticProp mNotic = addrMapIndexList.get(i);
			if(null != mNotic)
			{
				AddrProp mAddr = mNotic.mAddrInfo;
				if(mAddr != null)
				{
//					SKCommThread.getComnThreadObj(mAddr.eConnectType).getPlcSampInfo(mAddr, mTmpPlcInfo);
//					ProtocolInterfaces mProtocolObj = ProtocolInterfaces.getProtocolInterface();
//					if(null != mProtocolObj)
//					{
//						PROTOCOL_TYPE eFunType = mProtocolObj.getProtocolType(mTmpPlcInfo);
//						if(eFunType == PROTOCOL_TYPE.MASTER_MODEL)
//						{
//							continue;
//						}
//					}

					/*从地址主动取值*/
					if(mNotic.bBitAddr)
					{
						mSendData.eDataType = DATA_TYPE.BIT_1;
					}
					else
					{
						mSendData.eDataType = DATA_TYPE.INT_16;
					}
					mSendData.eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
					mSendData.eReadWriteCtlType = READ_WRITE_COM_TYPE.GLOBAL_LOOP_R;
					
					nTmpByteList.clear();
					PlcRegCmnStcTools.getRegBytesData(mAddr, nTmpByteList, mSendData);
					if(nTmpByteList.isEmpty())
					{
						continue;
					}
					
					/*判断数值是否相等，相等返回*/
					int nSize = nTmpByteList.size();
					if(nSize == mNotic.nByteList.size())
					{
						boolean bEquals = true;
						for(int k = 0; k < nSize; k++)
						{
							if(!mNotic.nByteList.get(k).equals(nTmpByteList.get(k)))
							{
								bEquals = false;
								break;
							}
						}
						if(bEquals)
						{
							continue;
						}
					}

					mNotic.nByteList.clear();
					for(int k = 0; k < nSize; k++)
					{
						mNotic.nByteList.add(nTmpByteList.get(k));
					}
					
					/*通知回调*/
					dealPlcNotic(mNotic);
				}// end if(mAddr != null)
			} // end if(null != mNotic)
		}// end for
	}
}