package com.android.Samkoonhmi.util;

import java.util.Vector;

public class PublicMathTools {
	
	/**
	 * 求最大公约数
	 * @param nValueList
	 * @return
	 */
	public static int getMaxDivisor(Vector<Integer > nValueList)
	{
		int nMaxDivisor = 1;
		if(null == nValueList || nValueList.isEmpty()) return nMaxDivisor;

		int nSize = nValueList.size();

		/*先求最小数*/
		int nMinValue = nValueList.get(0);
		for(int i = 1; i < nSize; i++)
		{
			if(nMinValue > nValueList.get(i))
			{
				nMinValue = nValueList.get(i);
			}
		}

		/*求最大公约数*/
		boolean bGetSuccess = true;
		while(nMinValue > 0)
		{
			bGetSuccess = true;
			for(int i = 0; i < nSize; i++)
			{
				if(nValueList.get(i)%nMinValue != 0)
				{
					bGetSuccess = false;
					break;
				}
			}

			/*成功则退出while循环*/
			if(bGetSuccess)
			{
				nMaxDivisor = nMinValue;
				break;
			}
			else
			{
				nMinValue--;
			}
		}

		return nMaxDivisor;
	}

	/**
	 * 求最小公倍数
	 * @param nValueList
	 * @return
	 */
	public static int getMinMultiple(Vector<Integer > nValueList)
	{
		/*初始化最小公倍数*/
		int nMinMultiple = 1;

		if(null == nValueList || nValueList.isEmpty()) return nMinMultiple;

		int nSize = nValueList.size();

		/*先求最大数*/
		int nMaxValue = nValueList.get(0);
		for(int i = 1; i < nSize; i++)
		{
			if(nMaxValue < nValueList.get(i))
			{
				nMaxValue = nValueList.get(i);
			}
		}

		/*求最大公约数乘以所有数除以最大公约数*/
		int nMaxDivisor = getMaxDivisor(nValueList);
		if(nMaxDivisor < 1)
		{
			nMaxDivisor = 1;
		}
		int nMaxMultiple = nMaxDivisor;
		int nTmpValue = 1;
		for(int i = 0; i < nSize; i++)
		{
			nTmpValue = nValueList.get(i)/nMaxDivisor;
			if(nTmpValue > 0)
			{
				nMaxMultiple *= nTmpValue;
			}
		}

		/*求最小公倍数*/
		for(nTmpValue = nMaxValue; nTmpValue <= nMaxMultiple; nTmpValue++)
		{
			boolean bGetSuccess = true;
			for(int i = 0; i < nSize; i++)
			{
				int nValue = nValueList.get(i);
				if(nValue != 0)
				{
					if(nTmpValue % nValue != 0)
					{
						bGetSuccess = false;
						break;
					}
				}
			}

			/*成功则退出while循环*/
			if(bGetSuccess)
			{
				nMinMultiple = nTmpValue;
				break;
			}
		}

		return nMinMultiple;
	}
}
