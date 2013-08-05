package com.android.Samkoonhmi.util;

public class DataTypeFormat {
	/**
	 * 写入的时候将BCDString转换成int 存入
	 * 
	 * @param sInputStr
	 * @param len
	 * @return
	 */
	public static long bcdStrToInt(String sInputStr, int len) {
		int nStrLen = sInputStr.length();
		long nBcdValue = 0;
		if (0 == nStrLen)
			return 0;

		int nStartIndex = nStrLen - len / 4;
		if (nStartIndex < 0) {
			nStartIndex = 0;
		}
		sInputStr = sInputStr.substring(nStartIndex, nStrLen);
       try{
    	    nBcdValue = Long.valueOf(sInputStr, 16);
       }catch(Exception e)
       {
    	   
       }
		return nBcdValue;
	}

	/**
	 * 将地址中的值用BCD码显示
	 * 
	 * @param nInputValue
	 * @param bSuccess
	 * @return
	 */
	public static String intToBcdStr(long nInputValue, boolean bSuccess) {
		String sOutStr = "ERROR";
		double nBcdValue = 0;

		try {
			int nHight24Bit = (int) nInputValue / 16;
			int nLow4Bit = (int) nInputValue % 16;
			if (nLow4Bit > 9) {
				bSuccess = false;
				return sOutStr;
			}
			for (int i = 0; i < 8; i++) {
				nBcdValue = (nHight24Bit >> i * 4) % 16;
				if (nBcdValue >= 10) {
					bSuccess = false;
					if ("".equals(sOutStr)) {
						sOutStr = "ERROR";
					}
					return sOutStr;
				}
			}

			sOutStr = Long.toString(nInputValue, 16);

			bSuccess = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sOutStr = "ERROR";
		}
		if ("".equals(sOutStr)) {
			sOutStr = "ERROR";
		}
		return sOutStr;
	}

	/**
	 * 16进制转int
	 * 
	 * @param sInputStr
	 * @param len
	 *            位长
	 * @return
	 */
	public static long hexStrToInt(String sInputStr, int len) {
		if (len != 16 && len != 32)
			return 0;

		int nStrLen = sInputStr.length();
		if (0 == nStrLen)
			return 0;

		int nStartIndex = nStrLen - len / 4;
		if (nStartIndex < 0) {
			nStartIndex = 0;
		}
		sInputStr = sInputStr.substring(nStartIndex, nStrLen);

		long nHexValue = Long.valueOf(sInputStr, 16);

		double nMax = 0;
		if (len == 16) {
			nMax = Math.pow(2, 16) - 1;
			if (nHexValue > nMax) {
				nHexValue = (long) nMax;
			}
		} else if (len == 32) {
			nMax = Math.pow(2, 32) - 1;
			if (nHexValue > nMax) {
				nHexValue = (long) nMax;
			}
		}

		return nHexValue;
	}

	/**
	 * int 转换成16进制String
	 * 
	 * @param nInputValue
	 * @return
	 */
	public static String intToHexStr(long nInputValue) {
		String sOutStr = "";
		double nMax = Math.pow(2, 32) - 1;
		if (nInputValue > nMax) {
			return String.valueOf(nMax);
		}

		sOutStr = Long.toString(nInputValue, 16);

		return sOutStr;
	}

	/**
	 * 八进制转int
	 * 
	 * @param sInputStr
	 * @param len
	 *            位长
	 * @return
	 */
	public static long octStrToInt(String sInputStr, int len) {
		int nTmpLen = 0;
		if (len == 16) {
			nTmpLen = 6;
		} else if (len == 32) {
			nTmpLen = 11;
		} else { 
			return 0;
		}

		int nStrLen = sInputStr.length();
		if (0 == nStrLen)
			return 0;

		int nStartIndex = nStrLen - nTmpLen;
		if (nStartIndex < 0) {
			nStartIndex = 0;
		}
		long nOctValue = 0;
		sInputStr = sInputStr.substring(nStartIndex, nStrLen);
		try {
			nOctValue = Long.valueOf(sInputStr, 8);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		double nMax = 0;
		if (len == 16) {
			nMax = Math.pow(2, 16) - 1;
			if (nOctValue > nMax) {
				nOctValue = (long) nMax;
			}
		} else if (len == 32) {
			nMax = Math.pow(2, 32) - 1;
			if (nOctValue > nMax) {
				nOctValue = (long) nMax;
			}
		}

		return nOctValue;
	}

	/**
	 * int 转八进制String
	 * 
	 * @param nInputValue
	 * @return
	 */
	public static String intToOctStr(long nInputValue) {
		String sOutStr = "";

		double nMax = Math.pow(2, 32) - 1;
		if (nInputValue > nMax) {
			return String.valueOf(nMax);
		}

		sOutStr = Long.toString(nInputValue, 8);

		return sOutStr;
	}

}
