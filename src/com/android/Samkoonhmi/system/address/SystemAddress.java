package com.android.Samkoonhmi.system.address;

import com.android.Samkoonhmi.databaseinterface.SystemInfoBiz;
import com.android.Samkoonhmi.util.AddrProp;

public class SystemAddress {
	public static SystemAddress systemAddress;
	private static SystemInfoBiz sysBiz;
	public static SystemAddress getInstance()
	{
		if(null == systemAddress)
		{
			systemAddress = new SystemAddress();
		}
		if(null == sysBiz)
		{
			sysBiz = new SystemInfoBiz();
		}
		return systemAddress;
	}
	private AddrProp enableBeep;//是否触摸声音 r/w
	private AddrProp isAlarmBeep;//是否报警声音 r/w
	private AddrProp isAlarm;//有实时报警正在发生 r
	private AddrProp sys_CurScene;//当前画面编号r
	private AddrProp sys_CurSec;//当前秒 r/w
	private AddrProp sys_CurMin;//当前分  r/w
	private AddrProp sys_CurHour;//当前时  r/w
	private AddrProp sys_CurDate;//当前日 r/w
	private AddrProp sys_CurMon;//当前月  r/w
	private AddrProp sys_CurYear;//当前年  r/w
	private AddrProp sys_CurWeek;//当前星期  r
	private AddrProp sys_RunHour;//系统运行时间时  r
	private AddrProp sys_RunMin;//系统运行时间分 r
	private AddrProp sys_RunSec;//系统运行时间秒r
	private AddrProp sys_LanIndex;//系统语言选择 r/w
	private AddrProp sys_Beep;//蜂鸣器开关r/w
	private AddrProp sys_backLight;//背光灯亮度 r/w
	private AddrProp tri_ClrAlarm;//历史报警记录清除r/w
	private AddrProp tri_ClrSamp;//历史数据记录清除 r/w
	private AddrProp tri_ClrBoard;//留言板信息清除 r/w
	private AddrProp isUdisk;//U盘连接状态 r
	private AddrProp isSDCard;//SD卡连接状态 r
	private AddrProp tri_MdPara;//连接设置参数确认修改 r/w
	private AddrProp isComErr;//通讯异常窗口是否弹出r/w
	private AddrProp cOM1_Name;//com1连接名称 r
	private AddrProp cOM1_Num;//COM1 本机编号 r
	private AddrProp cOM1_Buad;//COM1 波特率 r/w
	private AddrProp cOM1_Len;//COM1 数据长度 r/w
	private AddrProp cOM1_Chk;//COM1 校验位r/w
	private AddrProp cOM1_Stop;//COM1 停止位r/w
	private AddrProp cOM1_Status;//COM1 通信状态r
	private AddrProp cOM2_Name;//COM2连接名称r
	private AddrProp cOM2_Num;//COM2 本机编号r
	private AddrProp cOM2_Buad;//COM2 波特率r/w
	private AddrProp cOM2_Len;//COM2 数据长度r/w
	private AddrProp cOM2_Chk;//COM2 校验位r/w
	private AddrProp cOM2_Stop;//COM2 停止位r/w
	private AddrProp cOM2_Status;//COM2 通信状态r
	private AddrProp eth_Name;//以太网连接名称R
	private AddrProp eth_Num;//以太网 本机编号r
	private AddrProp eth_Status;//以太网 通信状态r
	private AddrProp sys_ScrSav;//画面待机r/w
	private AddrProp sys_SavBri;//待机亮度r/w
	private AddrProp sys_SaveTime;//待机保护时间r/w
	private AddrProp is_LogOut;//待机是否注销r/w
	private AddrProp sys_InitNum;//初始画面号r/w
	private AddrProp sys_CurRcpGrp;//当前配方组r/w
	private AddrProp sys_CurRcp;//当前配方号r/w
	private AddrProp sys_RunDay;//系统运行天数r
	private AddrProp tri_SetTime;//触发从地址值设置时间
	private AddrProp Sys_SetSec;//修改当前秒数
	private AddrProp Sys_CurrentUser;//当前登录用户
	private AddrProp Tri_LogOut;//触发注销用户
	private AddrProp Tri_SMS;//发送短信
	private AddrProp IsSMSsend;//短信发送状态
	private AddrProp WIFI_Ip;//wifi Ip
	private AddrProp WIFI_Status;//wifi 状态
	private AddrProp WIFI_Signal;//wifi 信号强度
	private AddrProp TG_Ip;//3gIP
	private AddrProp TG_Status;//3g 状态
	private AddrProp TG_Signal;//3g信号强度
	private AddrProp TG_Num;//3g号码
	private AddrProp SP_Type;//运营商
	private AddrProp Trans_Prog;//传输状态
	private AddrProp SMS_Str;//短信内容
	private AddrProp mAlarmConfirm;
	
	
	
/*****************************************系统时间*******************************************************************/
	/**
	 * 系统运行时间秒  r
	 * @return
	 */
	public AddrProp sys_RunSecAddr()
	{
		if(null == sys_RunSec)
		{
			sys_RunSec = new AddrProp();
			sys_RunSec.eConnectType =1; /*读取连接类型*/
			sys_RunSec.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_RunSec.sPlcProtocol ="local";  /*读取协议名字*/
			sys_RunSec.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_RunSec.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_RunSec.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_RunSec.nAddrValue =60013;/*读取PLC的地址值*/
			sys_RunSec.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_RunSec;
	}
	/**
	 * 系统运行时间分  r
	 * @return
	 */
	public AddrProp sys_RunMinAddr()
	{
		if(null == sys_RunMin)
		{
			sys_RunMin = new AddrProp();
			sys_RunMin.eConnectType =1; /*读取连接类型*/
			sys_RunMin.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_RunMin.sPlcProtocol ="local";  /*读取协议名字*/
			sys_RunMin.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_RunMin.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_RunMin.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_RunMin.nAddrValue =60012;/*读取PLC的地址值*/
			sys_RunMin.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_RunMin;
	}
	
	/**
	 * 系统运行时间时  r
	 * @return
	 */
	public AddrProp sys_RunHourAddr()
	{
		if(null == sys_RunHour)
		{
			sys_RunHour = new AddrProp();
			sys_RunHour.eConnectType =1; /*读取连接类型*/
			sys_RunHour.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_RunHour.sPlcProtocol ="local";  /*读取协议名字*/
			sys_RunHour.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_RunHour.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_RunHour.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_RunHour.nAddrValue =60011;/*读取PLC的地址值*/
			sys_RunHour.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_RunHour;
	}
	/**
	 * 系统运行时间天  r
	 * @return
	 */
	public AddrProp sys_RunDayAddr()
	{
		if(null == sys_RunDay)
		{
			sys_RunDay = new AddrProp();
			sys_RunDay.eConnectType =1; /*读取连接类型*/
			sys_RunDay.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_RunDay.sPlcProtocol ="local";  /*读取协议名字*/
			sys_RunDay.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_RunDay.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_RunDay.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_RunDay.nAddrValue =60010;/*读取PLC的地址值*/
			sys_RunDay.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_RunDay;
	}
	
	/**
	 * 修改系统秒数
	 * @return
	 */
	public AddrProp Sys_SetSecAddr(){
		if(null == Sys_SetSec)
		{
			Sys_SetSec = new AddrProp();
			Sys_SetSec.eConnectType =1; /*读取连接类型*/
			Sys_SetSec.nUserPlcId =0;  /*读取PLC自定义号*/
			Sys_SetSec.sPlcProtocol ="local";  /*读取协议名字*/
			Sys_SetSec.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			Sys_SetSec.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			Sys_SetSec.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			Sys_SetSec.nAddrValue =60009;/*读取PLC的地址值*/
			Sys_SetSec.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return Sys_SetSec;
	}
	/**
	 * 当前星期 r
	 * @return
	 */
	public AddrProp sys_CurWeekAddr()
	{
		if(null == sys_CurWeek)
		{
			sys_CurWeek = new AddrProp();
			sys_CurWeek.eConnectType =1; /*读取连接类型*/
			sys_CurWeek.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurWeek.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurWeek.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurWeek.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurWeek.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurWeek.nAddrValue =60007;/*读取PLC的地址值*/
			sys_CurWeek.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurWeek;
	}
	/**
	 * 当前秒 r/w
	 * @return
	 */
	public AddrProp sys_CurSecAddr()
	{
		if(null == sys_CurSec)
		{
			sys_CurSec = new AddrProp();
			sys_CurSec.eConnectType =1; /*读取连接类型*/
			sys_CurSec.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurSec.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurSec.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurSec.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurSec.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurSec.nAddrValue =60006;/*读取PLC的地址值*/
			sys_CurSec.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurSec;
	}
	/**
	 * 当前分 r/w
	 * @return
	 */
	public AddrProp sys_CurMinAddr()
	{
		if(null == sys_CurMin)
		{
			sys_CurMin = new AddrProp();
			sys_CurMin.eConnectType =1; /*读取连接类型*/
			sys_CurMin.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurMin.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurMin.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurMin.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurMin.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurMin.nAddrValue =60005;/*读取PLC的地址值*/
			sys_CurMin.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurMin;
	}
	/**
	 * 当前时 r/w
	 * @return
	 */
	public AddrProp sys_CurHourAddr()
	{
		if(null == sys_CurHour)
		{
			sys_CurHour = new AddrProp();
			sys_CurHour.eConnectType =1; /*读取连接类型*/
			sys_CurHour.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurHour.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurHour.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurHour.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurHour.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurHour.nAddrValue =60004;/*读取PLC的地址值*/
			sys_CurHour.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurHour;
	}
	/**
	 * 当前日 r/w
	 * @return
	 */
	public AddrProp sys_CurDateAddr()
	{
		if(null == sys_CurDate)
		{
			sys_CurDate = new AddrProp();
			sys_CurDate.eConnectType =1; /*读取连接类型*/
			sys_CurDate.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurDate.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurDate.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurDate.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurDate.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurDate.nAddrValue =60003;/*读取PLC的地址值*/
			sys_CurDate.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurDate;
	}
	/**
	 * 当前月   r/w
	 * @return
	 */
	public AddrProp sys_CurMonAddr()
	{
		if(null == sys_CurMon)
		{
			sys_CurMon = new AddrProp();
			sys_CurMon.eConnectType =1; /*读取连接类型*/
			sys_CurMon.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurMon.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurMon.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurMon.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurMon.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurMon.nAddrValue =60002;/*读取PLC的地址值*/
			sys_CurMon.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurMon;
	}
	/**
	 * 当前年 r/w
	 * @return
	 */
	public AddrProp sys_CurYearAddr()
	{
		if(null == sys_CurYear)
		{
			sys_CurYear = new AddrProp();
			sys_CurYear.eConnectType =1; /*读取连接类型*/
			sys_CurYear.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurYear.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurYear.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurYear.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurYear.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurYear.nAddrValue =60001;/*读取PLC的地址值*/
			sys_CurYear.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurYear;
	}
	
/********************************************连接设置寄存器**************************************************************************/
	/**
	 *com1连接名称 r
	 * @return
	 */
	public AddrProp cOM1_NameAddr()
	{
		if(null == cOM1_Name)
		{
			cOM1_Name = new AddrProp();
			cOM1_Name.eConnectType =1; /*读取连接类型*/
			cOM1_Name.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Name.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Name.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Name.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Name.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Name.nAddrValue =60033;/*读取PLC的地址值*/
			cOM1_Name.nAddrLen =20;    /*读取PLC的地址长度*/
		}
		return cOM1_Name;
	}
	/**
	 *COM1 本机编号 r
	 * @return
	 */
	public AddrProp cOM1_NumAddr()
	{
		if(null == cOM1_Num)
		{
			cOM1_Num = new AddrProp();
			cOM1_Num.eConnectType =1; /*读取连接类型*/
			cOM1_Num.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Num.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Num.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Num.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Num.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Num.nAddrValue =60053;/*读取PLC的地址值*/
			cOM1_Num.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM1_Num;
	}
	/**
	 *COM1 波特率 r/w
	 * @return
	 */
	public AddrProp cOM1_BuadAddr()
	{
		if(null == cOM1_Buad)
		{
			cOM1_Buad = new AddrProp();
			cOM1_Buad.eConnectType =1; /*读取连接类型*/
			cOM1_Buad.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Buad.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Buad.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Buad.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Buad.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Buad.nAddrValue =60054;/*读取PLC的地址值*/
			cOM1_Buad.nAddrLen =2;    /*读取PLC的地址长度*/
		}
		return cOM1_Buad;
	}
	/**
	 *COM1 数据长度 r/w
	 * @return
	 */
	public AddrProp cOM1_LenAddr()
	{
		if(null == cOM1_Len)
		{
			cOM1_Len = new AddrProp();
			cOM1_Len.eConnectType =1; /*读取连接类型*/
			cOM1_Len.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Len.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Len.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Len.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Len.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Len.nAddrValue =60056;/*读取PLC的地址值*/
			cOM1_Len.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM1_Len;
	}
	/**
	 *COM1 校验位r/w
	 * @return
	 */
	public AddrProp cOM1_ChkAddr()
	{
		if(null == cOM1_Chk)
		{
			cOM1_Chk = new AddrProp();
			cOM1_Chk.eConnectType =1; /*读取连接类型*/
			cOM1_Chk.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Chk.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Chk.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Chk.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Chk.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Chk.nAddrValue =60057;/*读取PLC的地址值*/
			cOM1_Chk.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM1_Chk;
	}
	/**
	 *COM1 停止位r/w
	 * @return
	 */
	public AddrProp cOM1_StopAddr()
	{
		if(null == cOM1_Stop)
		{
			cOM1_Stop = new AddrProp();
			cOM1_Stop.eConnectType =1; /*读取连接类型*/
			cOM1_Stop.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Stop.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Stop.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Stop.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Stop.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Stop.nAddrValue =60058;/*读取PLC的地址值*/
			cOM1_Stop.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM1_Stop;
	}
	/**
	 *COM1 通信状态r
	 * @return
	 */
	public AddrProp cOM1_StatusAddr()
	{
		if(null == cOM1_Status)
		{
			cOM1_Status = new AddrProp();
			cOM1_Status.eConnectType =1; /*读取连接类型*/
			cOM1_Status.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM1_Status.sPlcProtocol ="local";  /*读取协议名字*/
			cOM1_Status.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM1_Status.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM1_Status.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM1_Status.nAddrValue =60059;/*读取PLC的地址值*/
			cOM1_Status.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM1_Status;
	}
	
	
	/**
	 *com2连接名称 r
	 * @return
	 */
	public AddrProp cOM2_NameAddr()
	{
		if(null == cOM2_Name)
		{
			cOM2_Name = new AddrProp();
			cOM2_Name.eConnectType =1; /*读取连接类型*/
			cOM2_Name.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Name.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Name.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Name.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Name.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM2_Name.nAddrValue =60079;/*读取PLC的地址值*/
			cOM2_Name.nAddrLen =20;    /*读取PLC的地址长度*/
		}
		return cOM2_Name;
	}
	/**
	 *COM2 本机编号 r
	 * @return
	 */
	public AddrProp cOM2_NumAddr()
	{
		if(null == cOM2_Num)
		{
			cOM2_Num = new AddrProp();
			cOM2_Num.eConnectType =1; /*读取连接类型*/
			cOM2_Num.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Num.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Num.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Num.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Num.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM2_Num.nAddrValue =60099;/*读取PLC的地址值*/
			cOM2_Num.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM2_Num;
	}
	/**
	 *COM2 波特率 r/w
	 * @return
	 */
	public AddrProp cOM2_BuadAddr()
	{
		if(null == cOM2_Buad)
		{
			cOM2_Buad = new AddrProp();
			cOM2_Buad.eConnectType =1; /*读取连接类型*/
			cOM2_Buad.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Buad.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Buad.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Buad.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Buad.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/ 
			cOM2_Buad.nAddrValue =60100;/*读取PLC的地址值*/
			cOM2_Buad.nAddrLen =2;    /*读取PLC的地址长度*/
		}
		return cOM2_Buad;
	}
	/**
	 *COM2 数据长度 r/w
	 * @return
	 */
	public AddrProp cOM2_LenAddr()
	{
		if(null == cOM2_Len)
		{
			cOM2_Len = new AddrProp();
			cOM2_Len.eConnectType =1; /*读取连接类型*/
			cOM2_Len.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Len.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Len.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Len.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Len.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM2_Len.nAddrValue =60102;/*读取PLC的地址值*/
			cOM2_Len.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM2_Len;
	}
	/**
	 *COM2 校验位r/w
	 * @return
	 */
	public AddrProp cOM2_ChkAddr()
	{
		if(null == cOM2_Chk)
		{
			cOM2_Chk = new AddrProp();
			cOM2_Chk.eConnectType =1; /*读取连接类型*/
			cOM2_Chk.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Chk.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Chk.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Chk.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Chk.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM2_Chk.nAddrValue =60103;/*读取PLC的地址值*/
			cOM2_Chk.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM2_Chk;
	}
	/**
	 *COM2 停止位r/w
	 * @return
	 */
	public AddrProp cOM2_StopAddr()
	{
		if(null == cOM2_Stop)
		{
			cOM2_Stop = new AddrProp();
			cOM2_Stop.eConnectType =1; /*读取连接类型*/
			cOM2_Stop.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Stop.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Stop.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Stop.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Stop.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM2_Stop.nAddrValue =60104;/*读取PLC的地址值*/
			cOM2_Stop.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM2_Stop;
	}
	/**
	 *COM2 通信状态r
	 * @return
	 */
	public AddrProp cOM2_StatusAddr()
	{
		if(null == cOM2_Status)
		{
			cOM2_Status = new AddrProp();
			cOM2_Status.eConnectType =1; /*读取连接类型*/
			cOM2_Status.nUserPlcId =0;  /*读取PLC自定义号*/
			cOM2_Status.sPlcProtocol ="local";  /*读取协议名字*/
			cOM2_Status.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			cOM2_Status.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			cOM2_Status.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			cOM2_Status.nAddrValue =60105;/*读取PLC的地址值*/
			cOM2_Status.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return cOM2_Status;
	}
	/**
	 *以太网连接名称R
	 * @return
	 */
	public AddrProp eth_NameAddr()
	{
		if(null == eth_Name)
		{
			eth_Name = new AddrProp();
			eth_Name.eConnectType =1; /*读取连接类型*/
			eth_Name.nUserPlcId =0;  /*读取PLC自定义号*/
			eth_Name.sPlcProtocol ="local";  /*读取协议名字*/
			eth_Name.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			eth_Name.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			eth_Name.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			eth_Name.nAddrValue =60125;/*读取PLC的地址值*/
			eth_Name.nAddrLen =20;    /*读取PLC的地址长度*/
		}
		return eth_Name;
	}
	/**
	 *以太网 本机编号r
	 * @return
	 */
	public AddrProp eth_NumAddr()
	{
		if(null == eth_Num)
		{
			eth_Num = new AddrProp();
			eth_Num.eConnectType =1; /*读取连接类型*/
			eth_Num.nUserPlcId =0;  /*读取PLC自定义号*/
			eth_Num.sPlcProtocol ="local";  /*读取协议名字*/
			eth_Num.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			eth_Num.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			eth_Num.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			eth_Num.nAddrValue =60145;/*读取PLC的地址值*/
			eth_Num.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return eth_Num;
	}
	/**
	 *以太网 通信状态r
	 * @return
	 */
	public AddrProp eth_StatusAddr()
	{
		if(null == eth_Status)
		{
			eth_Status = new AddrProp();
			eth_Status.eConnectType =1; /*读取连接类型*/
			eth_Status.nUserPlcId =0;  /*读取PLC自定义号*/
			eth_Status.sPlcProtocol ="local";  /*读取协议名字*/
			eth_Status.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			eth_Status.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			eth_Status.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			eth_Status.nAddrValue =60146;/*读取PLC的地址值*/
			eth_Status.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return eth_Status;
	}
	
/*********************************************杂项***********************************************************************/	
	/**
	 * 当前画面编号r
	 * @return
	 */
	public AddrProp sys_CurSceneAddr()
	{
		if(null == sys_CurScene)
		{
			sys_CurScene = new AddrProp();
			sys_CurScene.eConnectType =1; /*读取连接类型*/
			sys_CurScene.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurScene.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurScene.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurScene.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurScene.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurScene.nAddrValue =60166;/*读取PLC的地址值*/
			sys_CurScene.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurScene;
	}
	
	/**
	 * 系统语言选择 r/w
	 * @return
	 */
	public AddrProp sys_LanIndexAddr()
	{
		if(null == sys_LanIndex)
		{
			sys_LanIndex = new AddrProp();
			sys_LanIndex.eConnectType =1; /*读取连接类型*/
			sys_LanIndex.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_LanIndex.sPlcProtocol ="local";  /*读取协议名字*/
			sys_LanIndex.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_LanIndex.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_LanIndex.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_LanIndex.nAddrValue =60167;/*读取PLC的地址值*/
			sys_LanIndex.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_LanIndex;
	}
	/**
	 * 背光灯亮度 r/w
	 * @return
	 */
	public AddrProp sys_backLightAddr()
	{
		if(null == sys_backLight)
		{
			sys_backLight = new AddrProp();
			sys_backLight.eConnectType =1; /*读取连接类型*/
			sys_backLight.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_backLight.sPlcProtocol ="local";  /*读取协议名字*/
			sys_backLight.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_backLight.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_backLight.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_backLight.nAddrValue =60168;/*读取PLC的地址值*/
			sys_backLight.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_backLight;
	}
	/**
	 *画面待机r/w
	 * @return
	 */
	public AddrProp sys_ScrSavAddr()
	{
		if(null == sys_ScrSav)
		{
			sys_ScrSav = new AddrProp();
			sys_ScrSav.eConnectType =1; /*读取连接类型*/
			sys_ScrSav.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_ScrSav.sPlcProtocol ="local";  /*读取协议名字*/
			sys_ScrSav.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_ScrSav.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_ScrSav.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_ScrSav.nAddrValue =60169;/*读取PLC的地址值*/
			sys_ScrSav.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_ScrSav;
	}
	
	/**
	 *待机保护时间r/w
	 * @return
	 */
	public AddrProp sys_SaveTimeAddr()
	{
		if(null == sys_SaveTime)
		{
			sys_SaveTime = new AddrProp();
			sys_SaveTime.eConnectType =1; /*读取连接类型*/
			sys_SaveTime.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_SaveTime.sPlcProtocol ="local";  /*读取协议名字*/
			sys_SaveTime.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_SaveTime.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_SaveTime.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_SaveTime.nAddrValue =60170;/*读取PLC的地址值*/
			sys_SaveTime.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_SaveTime;
	}
	
	/**
	 *初始画面号r/w
	 * @return
	 */
	public AddrProp sys_InitNumAddr()
	{
		if(null == sys_InitNum)
		{
			sys_InitNum = new AddrProp();
			sys_InitNum.eConnectType =1; /*读取连接类型*/
			sys_InitNum.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_InitNum.sPlcProtocol ="local";  /*读取协议名字*/
			sys_InitNum.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_InitNum.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_InitNum.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_InitNum.nAddrValue =60171;/*读取PLC的地址值*/
			sys_InitNum.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_InitNum;
	}
	/**
	 *当前配方组r/w
	 * @return
	 */
	public AddrProp sys_CurRcpGrpAddr()
	{
		if(null == sys_CurRcpGrp)
		{
			sys_CurRcpGrp = new AddrProp();
			sys_CurRcpGrp.eConnectType =1; /*读取连接类型*/
			sys_CurRcpGrp.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurRcpGrp.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurRcpGrp.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurRcpGrp.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurRcpGrp.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurRcpGrp.nAddrValue =60172;/*读取PLC的地址值*/
			sys_CurRcpGrp.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurRcpGrp;
	}
	/**
	 *当前配方号r/w
	 * @return
	 */
	public AddrProp sys_CurRcpAddr()
	{
		if(null == sys_CurRcp)
		{
			sys_CurRcp = new AddrProp();
			sys_CurRcp.eConnectType =1; /*读取连接类型*/
			sys_CurRcp.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_CurRcp.sPlcProtocol ="local";  /*读取协议名字*/
			sys_CurRcp.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_CurRcp.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_CurRcp.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_CurRcp.nAddrValue =60173;/*读取PLC的地址值*/
			sys_CurRcp.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_CurRcp;
	}
	
	/**
	 * 当前用户名地址
	 * @return
	 */
	public AddrProp Sys_CurrentUserAddr (){
		if(null == Sys_CurrentUser)
		{
			Sys_CurrentUser = new AddrProp();
			Sys_CurrentUser.eConnectType =1; /*读取连接类型*/
			Sys_CurrentUser.nUserPlcId =0;  /*读取PLC自定义号*/
			Sys_CurrentUser.sPlcProtocol ="local";  /*读取协议名字*/
			Sys_CurrentUser.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			Sys_CurrentUser.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			Sys_CurrentUser.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			Sys_CurrentUser.nAddrValue =60174;/*读取PLC的地址值*/
			Sys_CurrentUser.nAddrLen =20;    /*读取PLC的地址长度*/
		}
		return Sys_CurrentUser;
	}
	
/************************************位地址寄存器***************************************************************************/
	/**
	 * 触发注销用户
	 * @return
	 */
	public AddrProp Tri_LogOutAddr(){
		if(null == Tri_LogOut)
		{
			Tri_LogOut = new AddrProp();
			Tri_LogOut.eConnectType =1; /*读取连接类型*/
			Tri_LogOut.nUserPlcId =0;  /*读取PLC自定义号*/
			Tri_LogOut.sPlcProtocol ="local";  /*读取协议名字*/
			Tri_LogOut.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			Tri_LogOut.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			Tri_LogOut.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			Tri_LogOut.nAddrValue =60014;/*读取PLC的地址值*/
			Tri_LogOut.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return Tri_LogOut;
	}
	/**
	 *触发设置时间
	 * @return
	 */
	public AddrProp tri_SetTimeAddr()
	{
		if(null == tri_SetTime)
		{
			tri_SetTime = new AddrProp();
			tri_SetTime.eConnectType =1; /*读取连接类型*/
			tri_SetTime.nUserPlcId =0;  /*读取PLC自定义号*/
			tri_SetTime.sPlcProtocol ="local";  /*读取协议名字*/
			tri_SetTime.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			tri_SetTime.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			tri_SetTime.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			tri_SetTime.nAddrValue =60013;/*读取PLC的地址值*/
			tri_SetTime.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return tri_SetTime; 
	}
	
	/**
	 *待机是否注销r/w
	 * @return
	 */
	public AddrProp is_LogOutAddr()
	{
		if(null == is_LogOut)
		{
			is_LogOut = new AddrProp();
			is_LogOut.eConnectType =1; /*读取连接类型*/
			is_LogOut.nUserPlcId =0;  /*读取PLC自定义号*/
			is_LogOut.sPlcProtocol ="local";  /*读取协议名字*/
			is_LogOut.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			is_LogOut.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			is_LogOut.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			is_LogOut.nAddrValue =60012;/*读取PLC的地址值*/
			is_LogOut.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return is_LogOut;
	}
	
	/**
	 *待机亮度r/w
	 * @return
	 */
	public AddrProp sys_SavBriAddr()
	{
		if(null == sys_SavBri)
		{
			sys_SavBri = new AddrProp();
			sys_SavBri.eConnectType =1; /*读取连接类型*/
			sys_SavBri.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_SavBri.sPlcProtocol ="local";  /*读取协议名字*/
			sys_SavBri.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_SavBri.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_SavBri.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_SavBri.nAddrValue =60011;/*读取PLC的地址值*/
			sys_SavBri.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_SavBri;
	}
	
	/**
	 *通讯异常窗口是否弹出r/w
	 * @return
	 */
	public AddrProp isComErrAddr()
	{
		if(null == isComErr)
		{
			isComErr = new AddrProp();
			isComErr.eConnectType =1; /*读取连接类型*/
			isComErr.nUserPlcId =0;  /*读取PLC自定义号*/
			isComErr.sPlcProtocol ="local";  /*读取协议名字*/
			isComErr.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			isComErr.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			isComErr.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			isComErr.nAddrValue =60010;/*读取PLC的地址值*/
			isComErr.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return isComErr;
	}
	
	/**
	 *连接设置参数确认修改 r/w
	 * @return
	 */
	public AddrProp tri_MdParaAddr()
	{
		if(null == tri_MdPara)
		{
			tri_MdPara = new AddrProp();
			tri_MdPara.eConnectType =1; /*读取连接类型*/
			tri_MdPara.nUserPlcId =0;  /*读取PLC自定义号*/
			tri_MdPara.sPlcProtocol ="local";  /*读取协议名字*/
			tri_MdPara.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			tri_MdPara.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			tri_MdPara.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			tri_MdPara.nAddrValue =60009;/*读取PLC的地址值*/
			tri_MdPara.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return tri_MdPara;
	}
	
	/**
	 * SD卡连接状态 r
	 * @return
	 */
	public AddrProp isSDCardAddr()
	{
		if(null == isSDCard)
		{
			isSDCard = new AddrProp();
			isSDCard.eConnectType =1; /*读取连接类型*/
			isSDCard.nUserPlcId =0;  /*读取PLC自定义号*/
			isSDCard.sPlcProtocol ="local";  /*读取协议名字*/
			isSDCard.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			isSDCard.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			isSDCard.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			isSDCard.nAddrValue =60008;/*读取PLC的地址值*/
			isSDCard.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return isSDCard;
	}

	
	/**
	 * U盘连接状态 r
	 * @return
	 */
	public AddrProp isUdiskAddr()
	{
		if(null == isUdisk)
		{
			isUdisk = new AddrProp();
			isUdisk.eConnectType =1; /*读取连接类型*/
			isUdisk.nUserPlcId =0;  /*读取PLC自定义号*/
			isUdisk.sPlcProtocol ="local";  /*读取协议名字*/
			isUdisk.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			isUdisk.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			isUdisk.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			isUdisk.nAddrValue =60007;/*读取PLC的地址值*/
			isUdisk.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return isUdisk;
	}

	/**
	 * 留言板信息清除 r/w
	 * @return
	 */
	public AddrProp tri_ClrBoardAddr()
	{
		if(null == tri_ClrBoard)
		{
			tri_ClrBoard = new AddrProp();
			tri_ClrBoard.eConnectType =1; /*读取连接类型*/
			tri_ClrBoard.nUserPlcId =0;  /*读取PLC自定义号*/
			tri_ClrBoard.sPlcProtocol ="local";  /*读取协议名字*/
			tri_ClrBoard.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			tri_ClrBoard.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			tri_ClrBoard.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			tri_ClrBoard.nAddrValue =60006;/*读取PLC的地址值*/
			tri_ClrBoard.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return tri_ClrBoard;
	}
	/**
	 * 历史数据记录清除 r/w
	 * @return
	 */
	public AddrProp tri_ClrSampAddr()
	{
		if(null == tri_ClrSamp)
		{
			tri_ClrSamp = new AddrProp();
			tri_ClrSamp.eConnectType =1; /*读取连接类型*/
			tri_ClrSamp.nUserPlcId =0;  /*读取PLC自定义号*/
			tri_ClrSamp.sPlcProtocol ="local";  /*读取协议名字*/
			tri_ClrSamp.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			tri_ClrSamp.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			tri_ClrSamp.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			tri_ClrSamp.nAddrValue =60005;/*读取PLC的地址值*/
			tri_ClrSamp.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return tri_ClrSamp;
	}
	/**
	 * 历史报警记录清除r/w
	 * @return
	 */
	public AddrProp tri_ClrAlarmAddr()
	{
		if(null == tri_ClrAlarm)
		{
			tri_ClrAlarm = new AddrProp();
			tri_ClrAlarm.eConnectType =1; /*读取连接类型*/
			tri_ClrAlarm.nUserPlcId =0;  /*读取PLC自定义号*/
			tri_ClrAlarm.sPlcProtocol ="local";  /*读取协议名字*/
			tri_ClrAlarm.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			tri_ClrAlarm.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			tri_ClrAlarm.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			tri_ClrAlarm.nAddrValue =60004;/*读取PLC的地址值*/
			tri_ClrAlarm.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return tri_ClrAlarm;
	}
	/**
	 * 蜂鸣器开关r/w
	 * @return
	 */
	public AddrProp sys_BeepAddr()
	{
		if(null == sys_Beep)
		{
			sys_Beep = new AddrProp();
			sys_Beep.eConnectType =1; /*读取连接类型*/
			sys_Beep.nUserPlcId =0;  /*读取PLC自定义号*/
			sys_Beep.sPlcProtocol ="local";  /*读取协议名字*/
			sys_Beep.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			sys_Beep.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			sys_Beep.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			sys_Beep.nAddrValue =60003;/*读取PLC的地址值*/
			sys_Beep.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return sys_Beep;
	}
	/**
	 * 有实时报警正在发生 r
	 * @return
	 */
	public AddrProp isAlarmAddr()
	{
		if(null == isAlarm)
		{
			isAlarm = new AddrProp();
			isAlarm.eConnectType =1; /*读取连接类型*/
			isAlarm.nUserPlcId =0;  /*读取PLC自定义号*/
			isAlarm.sPlcProtocol ="local";  /*读取协议名字*/
			isAlarm.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			isAlarm.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			isAlarm.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			isAlarm.nAddrValue =60002;/*读取PLC的地址值*/
			isAlarm.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return isAlarm;
	}
	
	/**
	 * 是否报警声音 r/w
	 * @return
	 */
	public AddrProp isAlarmBeepAddr()
	{
		if(null == isAlarmBeep)
		{
			isAlarmBeep = new AddrProp();
			isAlarmBeep.eConnectType =1; /*读取连接类型*/
			isAlarmBeep.nUserPlcId =0;  /*读取PLC自定义号*/
			isAlarmBeep.sPlcProtocol ="local";  /*读取协议名字*/
			isAlarmBeep.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			isAlarmBeep.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			isAlarmBeep.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			isAlarmBeep.nAddrValue =60001;/*读取PLC的地址值*/
			isAlarmBeep.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return isAlarmBeep;
	}
	
	/**
	 * 是否触摸声音
	 * @return
	 */
	public AddrProp enableBeepAddr()
	{
		if(null == enableBeep)
		{
			enableBeep = new AddrProp();
			enableBeep.eConnectType =1; /*读取连接类型*/
			enableBeep.nUserPlcId =0;  /*读取PLC自定义号*/
			enableBeep.sPlcProtocol ="local";  /*读取协议名字*/
			enableBeep.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			enableBeep.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			enableBeep.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			enableBeep.nAddrValue =60000;/*读取PLC的地址值*/
			enableBeep.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return enableBeep;
	}
	
	public AddrProp mAlarmComfirm()
	{
		if(null == mAlarmConfirm)
		{
			mAlarmConfirm = new AddrProp();
			mAlarmConfirm.eConnectType =1; /*读取连接类型*/
			mAlarmConfirm.nUserPlcId =0;  /*读取PLC自定义号*/
			mAlarmConfirm.sPlcProtocol ="local";  /*读取协议名字*/
			mAlarmConfirm.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			mAlarmConfirm.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			mAlarmConfirm.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			mAlarmConfirm.nAddrValue =60017;/*读取PLC的地址值*/
			mAlarmConfirm.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return mAlarmConfirm;
	}
	
	
	//////////////////////////////
	/**
	 * 发送短信
	 * @return
	 */
	public AddrProp TriSms()
	{
		if(null == Tri_SMS)
		{
			Tri_SMS = new AddrProp();
			Tri_SMS.eConnectType =1; /*读取连接类型*/
			Tri_SMS.nUserPlcId =0;  /*读取PLC自定义号*/
			Tri_SMS.sPlcProtocol ="local";  /*读取协议名字*/
			Tri_SMS.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			Tri_SMS.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			Tri_SMS.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			Tri_SMS.nAddrValue =60015;/*读取PLC的地址值*/
			Tri_SMS.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return Tri_SMS;
	}
	
	/**
	 * 短信发送状态
	 * @return
	 */
	public AddrProp isSmsSend()
	{
		if(null == IsSMSsend)
		{
			IsSMSsend = new AddrProp();
			IsSMSsend.eConnectType =1; /*读取连接类型*/
			IsSMSsend.nUserPlcId =0;  /*读取PLC自定义号*/
			IsSMSsend.sPlcProtocol ="local";  /*读取协议名字*/
			IsSMSsend.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			IsSMSsend.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			IsSMSsend.nRegIndex =0;  /*读取PLC的寄存器号 位地址0   字地址1*/
			IsSMSsend.nAddrValue =60016;/*读取PLC的地址值*/
			IsSMSsend.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return IsSMSsend;
	}
	
	/**
	 * 短信内容
	 * @return
	 */
	public AddrProp smsMsg()
	{
		if(null == SMS_Str)
		{
			SMS_Str = new AddrProp();
			SMS_Str.eConnectType =1; /*读取连接类型*/
			SMS_Str.nUserPlcId =0;  /*读取PLC自定义号*/
			SMS_Str.sPlcProtocol ="local";  /*读取协议名字*/
			SMS_Str.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			SMS_Str.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			SMS_Str.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			SMS_Str.nAddrValue =60230;/*读取PLC的地址值*/
			SMS_Str.nAddrLen =100;    /*读取PLC的地址长度*/
		}
		return SMS_Str;
	}
	
	/**
	 * wifi ip
	 * @return
	 */
	public AddrProp wifiIp()
	{
		if(null == WIFI_Ip)
		{
			WIFI_Ip = new AddrProp();
			WIFI_Ip.eConnectType =1; /*读取连接类型*/
			WIFI_Ip.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Ip.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Ip.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Ip.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Ip.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Ip.nAddrValue =60200;/*读取PLC的地址值*/
			WIFI_Ip.nAddrLen =4;    /*读取PLC的地址长度*/
		}
		return WIFI_Ip;
	}
	
	/**
	 * wifi ip
	 * @return
	 */
	private AddrProp WIFI_Ip1;
	public AddrProp wifiIp1()
	{
		if(null == WIFI_Ip1)
		{
			WIFI_Ip1 = new AddrProp();
			WIFI_Ip1.eConnectType =1; /*读取连接类型*/
			WIFI_Ip1.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Ip1.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Ip1.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Ip1.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Ip1.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Ip1.nAddrValue =60200;/*读取PLC的地址值*/
			WIFI_Ip1.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return WIFI_Ip1;
	}
	
	
	/**
	 * wifi ip
	 * @return
	 */
	private AddrProp WIFI_Ip2;
	public AddrProp wifiIp2()
	{
		if(null == WIFI_Ip2)
		{
			WIFI_Ip2 = new AddrProp();
			WIFI_Ip2.eConnectType =1; /*读取连接类型*/
			WIFI_Ip2.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Ip2.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Ip2.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Ip2.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Ip2.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Ip2.nAddrValue =60201;/*读取PLC的地址值*/
			WIFI_Ip2.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return WIFI_Ip2;
	}
	
	/**
	 * wifi ip
	 * @return
	 */
	private AddrProp WIFI_Ip3;
	public AddrProp wifiIp3()
	{
		if(null == WIFI_Ip3)
		{
			WIFI_Ip3 = new AddrProp();
			WIFI_Ip3.eConnectType =1; /*读取连接类型*/
			WIFI_Ip3.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Ip3.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Ip3.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Ip3.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Ip3.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Ip3.nAddrValue =60202;/*读取PLC的地址值*/
			WIFI_Ip3.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return WIFI_Ip3;
	}
	
	/**
	 * wifi ip
	 * @return
	 */
	private AddrProp WIFI_Ip4;
	public AddrProp wifiIp4()
	{
		if(null == WIFI_Ip4)
		{
			WIFI_Ip4 = new AddrProp();
			WIFI_Ip4.eConnectType =1; /*读取连接类型*/
			WIFI_Ip4.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Ip4.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Ip4.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Ip4.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Ip4.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Ip4.nAddrValue =60203;/*读取PLC的地址值*/
			WIFI_Ip4.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return WIFI_Ip4;
	}
	
	/**
	 * wifi 状态
	 * @return
	 */
	public AddrProp wifiStatus()
	{
		if(null == WIFI_Status)
		{
			WIFI_Status = new AddrProp();
			WIFI_Status.eConnectType =1; /*读取连接类型*/
			WIFI_Status.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Status.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Status.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Status.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Status.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Status.nAddrValue =60204;/*读取PLC的地址值*/
			WIFI_Status.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return WIFI_Status;
	}
	
	/**
	 * wifi 信号强度
	 * @return
	 */
	public AddrProp wifiSignal()
	{
		if(null == WIFI_Signal)
		{
			WIFI_Signal = new AddrProp();
			WIFI_Signal.eConnectType =1; /*读取连接类型*/
			WIFI_Signal.nUserPlcId =0;  /*读取PLC自定义号*/
			WIFI_Signal.sPlcProtocol ="local";  /*读取协议名字*/
			WIFI_Signal.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			WIFI_Signal.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			WIFI_Signal.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			WIFI_Signal.nAddrValue =60205;/*读取PLC的地址值*/
			WIFI_Signal.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return WIFI_Signal;
	}
	
	/**
	 * 3g 信号IP
	 * @return
	 */
	public AddrProp TgIp()
	{
		if(null == TG_Ip)
		{
			TG_Ip = new AddrProp();
			TG_Ip.eConnectType =1; /*读取连接类型*/
			TG_Ip.nUserPlcId =0;  /*读取PLC自定义号*/
			TG_Ip.sPlcProtocol ="local";  /*读取协议名字*/
			TG_Ip.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			TG_Ip.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			TG_Ip.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			TG_Ip.nAddrValue =60206;/*读取PLC的地址值*/
			TG_Ip.nAddrLen =4;    /*读取PLC的地址长度*/
		}
		return TG_Ip;
	}
	
	/**
	 * 3g 状态
	 * @return
	 */
	public AddrProp TgStatus()
	{
		if(null == TG_Status)
		{
			TG_Status = new AddrProp();
			TG_Status.eConnectType =1; /*读取连接类型*/
			TG_Status.nUserPlcId =0;  /*读取PLC自定义号*/
			TG_Status.sPlcProtocol ="local";  /*读取协议名字*/
			TG_Status.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			TG_Status.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			TG_Status.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			TG_Status.nAddrValue =60210;/*读取PLC的地址值*/
			TG_Status.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return TG_Status;
	}
	
	/**
	 * 3g 信号强度
	 * @return
	 */
	public AddrProp TgSignal()
	{
		if(null == TG_Signal)
		{
			TG_Signal = new AddrProp();
			TG_Signal.eConnectType =1; /*读取连接类型*/
			TG_Signal.nUserPlcId =0;  /*读取PLC自定义号*/
			TG_Signal.sPlcProtocol ="local";  /*读取协议名字*/
			TG_Signal.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			TG_Signal.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			TG_Signal.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			TG_Signal.nAddrValue =60211;/*读取PLC的地址值*/
			TG_Signal.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return TG_Signal;
	}
	
	/**
	 * 3g号码
	 * @return
	 */
	public AddrProp TGNum()
	{
		if(null == TG_Num)
		{
			TG_Num = new AddrProp();
			TG_Num.eConnectType =1; /*读取连接类型*/
			TG_Num.nUserPlcId =0;  /*读取PLC自定义号*/
			TG_Num.sPlcProtocol ="local";  /*读取协议名字*/
			TG_Num.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			TG_Num.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			TG_Num.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			TG_Num.nAddrValue =60214;/*读取PLC的地址值*/
			TG_Num.nAddrLen =11;    /*读取PLC的地址长度*/
		}
		return TG_Num;
	}
	
	/**
	 * 运营商
	 * @return
	 */
	public AddrProp SpType()
	{
		if(null == SP_Type)
		{
			SP_Type = new AddrProp();
			SP_Type.eConnectType =1; /*读取连接类型*/
			SP_Type.nUserPlcId =0;  /*读取PLC自定义号*/
			SP_Type.sPlcProtocol ="local";  /*读取协议名字*/
			SP_Type.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			SP_Type.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			SP_Type.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			SP_Type.nAddrValue =60212;/*读取PLC的地址值*/
			SP_Type.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return SP_Type;
	}
	
	/**
	 * 传输状态
	 * @return
	 */
	public AddrProp TransProg()
	{
		if(null == Trans_Prog)
		{
			Trans_Prog = new AddrProp();
			Trans_Prog.eConnectType =1; /*读取连接类型*/
			Trans_Prog.nUserPlcId =0;  /*读取PLC自定义号*/
			Trans_Prog.sPlcProtocol ="local";  /*读取协议名字*/
			Trans_Prog.eAddrRWprop =12;/*读取PLC的地址读写等级*/
			Trans_Prog.nPlcStationIndex =sysBiz.getSceenNum();  /*读取PLC的站号*/  //根据屏号 取com1的屏号
			
			Trans_Prog.nRegIndex =1;  /*读取PLC的寄存器号 位地址0   字地址1*/
			Trans_Prog.nAddrValue =60213;/*读取PLC的地址值*/
			Trans_Prog.nAddrLen =1;    /*读取PLC的地址长度*/
		}
		return Trans_Prog;
	}
	
	
}
