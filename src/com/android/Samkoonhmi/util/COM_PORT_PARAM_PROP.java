package com.android.Samkoonhmi.util;

public class COM_PORT_PARAM_PROP {
	
	/**
	 * 检验方式
	 * @author Latory
	 *
	 */
	public class PARITY_TYPE{
		public static final int  PAR_NONE  = 0;               //无校验
		public static final int  PAR_EVEN  = 1;               //偶校验
		public static final int  PAR_ODD   = 2;               //奇校验
		public static final int  PAR_MARK  = 3;               //标记校验 WINDOWS ONLY
		public static final int  PAR_SPACE = 4;               //空格校验
	}
	
	/**
	 * 波特率
	 * @author Latory
	 *
	 */
	public class BAUD_RATE{
		public static final int  BAUD1200 = 1200;                  //波特率为1200
		public static final int  BAUD2400 = 2400;                  //波特率为2400
		public static final int  BAUD4800 = 4800;                  //波特率为4800
		public static final int  BAUD9600 = 9600;                  //波特率为9600
		public static final int  BAUD19200 = 19200;                //波特率为19200
		public static final int  BAUD38400 = 38400;                //波特率为38400
		public static final int  BAUD57600 = 57600;                //波特率为57600
		public static final int  BAUD115200 = 115200;              //波特率为115200
	}
	
	/**
	 * 数据位
	 * @author Latory
	 *
	 */
	public class DATA_BIT{
		public static final int  DATA_5 = 5;          //5个数据位
		public static final int  DATA_6 = 6;          //6个数据位
		public static final int  DATA_7 = 7;          //7个数据位
		public static final int  DATA_8 = 8;          //8个数据位
	}
	
	/**
	 * 停止位
	 * @author Latory
	 *
	 */
	public class STOP_BIT{
		public static final int  STOP_1 = 1;          //一个停止位
		public static final int  STOP_2 = 2;          //两个停止位
	}
	
	/**
	 * 数据流控制方式
	 * @author Latory
	 *
	 */
	public class DATA_FLOW_CTL{
		public static final int  FLOW_OFF = 0;              //无数据流控制
		public static final int  FLOW_HARDWARE = 1;              //硬件数据流控制
		public static final int  FLOW_XONXOFF = 2;              //Xon/Xof 数据流控制
	}
	
	/**
	 * 串口编号
	 * @author latory
	 *
	 */
	public class SERIAL_PORT_NUM{
//		public static final int  COM_0 = 0;           //串口0
		public static final int  COM_1 = 0;           //串口1
		public static final int  COM_2 = 1;           //串口2
		public static final int  COM_3 = 2;           //串口3
		public static final int  COM_4 = 3;           //串口4
		public static final int  COM_5 = 4;           //串口5
		public static final int  COM_NO = -1;           //无串口
	}
	
	/**
	 * 打开串口的标志
	 *  O_RDONLY 以只读方式打开文件
     *	O_WRONLY 以只写方式打开文件
     *	O_RDWR 以读写方式打开文件
     *	O_APPEND 写入数据时添加到文件末尾
     *	O_CREATE 如果文件不存在则产生该文件，使用该标志需要设置访问权限位mode_t
     * 	O_EXCL 指定该标志，并且指定了O_CREATE标志，如果打开的文件存在则会产生一个错误
     * 	O_TRUNC 如果文件存在并且成功以写或者只写方式打开，则清除文件所有内容，使得文件长度变为0
     *	O_NOCTTY 如果打开的是一个终端设备，这个程序不会成为对应这个端口的控制终端，如果没有该标志，任何一个输入，例如键盘中止信号等，都将影响进程。
     * 	O_NONBLOCK 该标志与早期使用的O_NDELAY标志作用差不多。程序不关心DCD信号线的状态，如果指定该标志，进程将一直在休眠状态，直到DCD信号线为0。
     *	O_SYNC 对I/O进行写等待
     *	返回值:成功返回文件描述符，如果失败返回-1
	 * @author Latory
	 *
	 */
	public class DEV_OPEN_FLAGS{
		public static final int O_RDONLY = 0x00;
		public static final int O_WRONLY = 0x01;
		public static final int O_RDWR = 0x02;
		public static final int O_CREAT = 0x0100; /* not fcntl */
		public static final int O_EXCL = 0x0200; /* not fcntl */
		public static final int O_NOCTTY = 0x0400; /* not fcntl */
		public static final int O_TRUNC = 0x01000; /* not fcntl */
		public static final int O_APPEND = 0x02000;
		public static final int O_NONBLOCK = 0x04000;
		public static final int O_NDELAY = 0x04000;
		public static final int O_SYNC = 0x010000;
		public static final int O_FSYNC = 0x010000;
		public static final int O_ASYNC = 0x020000;
	}
	
	/*串口的编号*/
	public int nSerialPortNum = SERIAL_PORT_NUM.COM_NO;
	
	/*波特率*/
	public int nBaudRate = BAUD_RATE.BAUD9600;
	
	/*数据位*/
	public int nDataBits = DATA_BIT.DATA_8;
	
	/*检验方式*/
	public int nParityType = PARITY_TYPE.PAR_NONE;
	
	/*停止位*/
	public int nStopBit = STOP_BIT.STOP_1;
	
	/*数据流控制方式*/
	public int nFlowType = DATA_FLOW_CTL.FLOW_OFF;
}
