package com.android.Samkoonhmi.util;

import com.android.Samkoonhmi.skenum.BYTE_H_L_POS;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.READ_WRITE_COM_TYPE;

public class SEND_DATA_STRUCT {
	public int eReadWriteCtlType = READ_WRITE_COM_TYPE.OTHER_CONTROL_TYPE;
	public DATA_TYPE eDataType = DATA_TYPE.OTHER_DATA_TYPE;
	public BYTE_H_L_POS eByteHLPos = BYTE_H_L_POS.L_BYTE_FIRST;
}
