package com.android.Samkoonhmi.skenum;

public enum PLC_RAM_TRANS_TYPE {
	READ_FROM_PLC,          // read data from plc
	WRITE_TO_PLC ,          // write data to plc
	READ_WRITE_PLC,         // read data from plc or write data to plc
	OTHER_TRANS_TYPE        // other transport type
}
