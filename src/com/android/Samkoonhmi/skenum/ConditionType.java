package com.android.Samkoonhmi.skenum;

public class ConditionType {

	public enum CONDITION_TYPE{
		ALARM_ON,        //on报警
		ALARM_OFF,       //off报警
		ALARM_VALUE,     //固定值报警
		ALARM_RANGE,     //范围内报警
		ALARM_RANGE_OUT  //范围外报警
	}
	
	public static CONDITION_TYPE getConditionType(int index){
		switch (index) {
		case 1:
			return CONDITION_TYPE.ALARM_ON;
		case 2:
			return CONDITION_TYPE.ALARM_OFF;
		case 3:
			return CONDITION_TYPE.ALARM_VALUE;
		case 4:
			return CONDITION_TYPE.ALARM_RANGE;
		case 5:
			return CONDITION_TYPE.ALARM_RANGE_OUT;
		}
		return CONDITION_TYPE.ALARM_VALUE;
	}
}
