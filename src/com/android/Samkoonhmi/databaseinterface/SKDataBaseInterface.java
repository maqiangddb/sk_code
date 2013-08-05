package com.android.Samkoonhmi.databaseinterface;

import java.util.HashMap;
import java.util.Vector;

import com.android.Samkoonhmi.SKThread;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.util.AddrProp;
import com.android.Samkoonhmi.util.MODULE;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class SKDataBaseInterface{
	
	/**
	 * database object 
	 */
	public SQLiteDatabase m_databaseObj;
	
	/**
	 * 一个查询条件的枚举
	 * @author Latory
	 *
	 */
	public static enum CASE_COLUMN_TYPE{
		QUERY_BY_ID,                     //根据ID号来查询
		QUERY_BY_SCENE_ID,               //根据场景号来查询
		QUERY_BY_ITEM_ID,                //根据item号来查询
		QUERY_ALL,                       //查询所有
		QUERY_NULL                       //不查询
	}
	
	/**
	 * 构造函数
	 */
    public SKDataBaseInterface(){
        // TODO put your implementation here.	
    	m_databaseObj = null;
    }
    
    /**
     * 打开数据库
     * @param sDatabasePath
     * @return
     */
    public synchronized boolean openDatabase(String sDatabasePath)
    {
    	if(null == m_databaseObj)
    	{
    		try {
    			if(null == sDatabasePath)
    			{
    				sDatabasePath = "/data/data/com.android.Samkoonhmi/databases/sd.dat";
    			}
    			m_databaseObj = SQLiteDatabase.openDatabase(sDatabasePath, null, SQLiteDatabase.OPEN_READWRITE);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("open database", e.getMessage());
    			return false;
    		}
    	}
    	return true;
    }
    
    /**
     * 创建数据库
     * @param sDatabasePath
     * @param sExcelSql
     * @return
     */
    public synchronized boolean createDatabase(String sDatabasePath, String sExcelSql)
    {
    	if(null == sDatabasePath)
    	{
    		Log.e("createDatabase", "create database failed , database filepath is null ");
    		return false;
    	}
    	
    	if(null == sExcelSql )
    	{
    		Log.e("createDatabase", "create " + sDatabasePath + "database failed , sExcelSql is null");
    		return false;
    	}
    	
    	if(null == m_databaseObj)
    	{
    		try {
    			m_databaseObj = SQLiteDatabase.openDatabase(sDatabasePath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
    			m_databaseObj.execSQL(sExcelSql);
    			
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("open database", e.getMessage());
    			return false;
    		}
    	}
    	else
    	{
    		m_databaseObj.execSQL(sExcelSql);
    	}
		
    	return true;
    }
    
    /**
     * 数据库是否打开
     * @return
     */
    public synchronized boolean databaseIsOpen()
    {
    	if(null == m_databaseObj) return false;
    	
    	return m_databaseObj.isOpen();
    }
    
    /**
     * 返回数据库对象
     * @return
     */
    private synchronized SQLiteDatabase getDatabaseObj()
    {
    	return m_databaseObj;
    }
    
    /**
     * 关闭数据库
     */
    public synchronized void closeDatabase()
    {
    	if(m_databaseObj != null)
    	{
    		m_databaseObj.close();
    	}
    }
    
    /**
     * 开始事务操作
     */
    public synchronized void beginTransaction()
    {
    	if(m_databaseObj == null) return ;
    	
    	m_databaseObj.beginTransaction();
    }
    
    /**
     * 提交事务，进行保存
     */
    public synchronized void commitTransaction()
    {
    	if(m_databaseObj == null) return ;
    	m_databaseObj.setTransactionSuccessful();
    }
    
    /**
     * 结束事务，只要执行beginTransaction（），就一定要调用endTransaction()
     */
    public synchronized void endTransaction()
    {
    	if(m_databaseObj == null) return ;
    	m_databaseObj.endTransaction();
    }
    
    //
    public SQLiteStatement compileStatement(String sql){
    	if (m_databaseObj==null) {
			return null;
		}
    	return m_databaseObj.compileStatement(sql);
    }
    
	
    /**
     * 根据用户自定义查询一些列
     * @param sTableName：表的名字
     * @param sColums：列的集合
     * @param sWhereClause：查询条件
     * @return
     */
    public synchronized Cursor getDatabaseByUserDef(String sTableName, String[] sColums, String sWhereClause)
    {
        // TODO put your implementation here.	
    	Cursor mCursor = null;
    	if(null == m_databaseObj)
    	{
    		Log.e("getDatabaseByUserDef：", "数据库查询对象为空");
    		return mCursor;
    	}
    	
    	/*生成脚本执行语句*/
    	String sSqlStr = "select ";
    	int nColumLen = 0;
    	if(null != sColums)
    	{
    		nColumLen = sColums.length;
    	}
    	
    	/*列集合为空的时候，查询所有列*/
    	if(nColumLen <= 0)
    	{
    		sSqlStr += "* ";
    	}
    	else 
    	{
    		for(int i = 0; i < nColumLen -1; i++)
    		{
    			sSqlStr += sColums[i];
				sSqlStr += ", ";
    		}
    		sSqlStr += sColums[nColumLen -1];
    		sSqlStr += " ";
		}
    	
    	sSqlStr += "from " + sTableName;
    	
    	/*查询条件不为空的时候添加查询条件*/
    	if(null == sWhereClause || sWhereClause.isEmpty() == false)
    	{
    		sSqlStr += " where " + sWhereClause;
    	}
    	
    	/*执行查询语句*/
    	try {
    		mCursor = m_databaseObj.rawQuery(sSqlStr, null);
		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.d("getDatabaseByUserDef", e.getMessage());
		}
    	
    	return mCursor;
    }
    
    /**
     * 根据一些默认的条件查询，比如根据ID、场景ID、itemid、查询所有数据等。
     * @param sTableName：表的名字
     * @param sColums：列的集合
     * @param eCaseType：查询类型，比如根据ID、场景ID、itemid、查询所有数据等。
     * @param nCaseValue：查询的值
     * @return
     */
    public synchronized Cursor getDatabaseByType(String sTableName, String[] sColums, CASE_COLUMN_TYPE eCaseType, int nCaseValue)
    {
    	Cursor mCursor = null;
    	if(null == m_databaseObj)
    	{
    		Log.e("getDatabaseByType：", "database inquiry failed, m_databaseObj = null");
    		return mCursor;
    	}
    	
    	/*生成脚本执行语句*/
    	String sSqlStr = "select ";
    	int nColumLen = 0;
    	if(null != sColums)
    	{
    		nColumLen = sColums.length;
    	}
    	
    	/*列集合为空的时候，查询所有列*/
    	if(nColumLen <= 0)
    	{
    		sSqlStr += "* ";
    	}
    	else 
    	{
    		for(int i = 0; i < nColumLen -1; i++)
    		{
    			sSqlStr += sColums[i];
				sSqlStr += ", ";
    		}
    		sSqlStr += sColums[nColumLen -1];
    		sSqlStr += " ";
		}
    	
    	sSqlStr += "from " + sTableName;
    	
    	String selectionArgs[] = null; 
    	switch(eCaseType)
    	{
    	case QUERY_BY_ID:
    	{
    		sSqlStr += " where ";
    		sSqlStr += "id = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_BY_SCENE_ID:
    	{
    		sSqlStr += " where ";
    		sSqlStr += "nSceneId = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_BY_ITEM_ID:
    	{
    		sSqlStr += " where ";
    		sSqlStr += "nItemId = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_ALL:
    	{
    		break;
    	}
    	case QUERY_NULL:
    	default:
    	{
    		Log.e("Query failed", "Please select the correct query as：QUERY_BY_ID、QUERY_BY_SCENE_ID、QUERY_BY_ITEM_ID");
    		return mCursor;
    	}
    	}
    	
    	/*执行查询语句*/
    	try {
    		mCursor = m_databaseObj.rawQuery(sSqlStr, selectionArgs);
		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.e("getDatabaseByType", e.getMessage());
		}
    	selectionArgs = null;
    	return mCursor; 
    }
    
    /**
     * 完全根据SQL语句查询，要求使用这接口的对SQL语句比较了解
     * @param sSqlStr：SQL查询语句
     * @param slecttionArgs：一些占位符的集合，支持占位符查询
     * @return
     */
    public synchronized Cursor getDatabaseBySql(String sSqlStr, String[] slecttionArgs)
    {
    	Cursor mCursor = null;
    	if(m_databaseObj != null)
    	{
    		/*执行查询数据*/
    		try {
    			mCursor = m_databaseObj.rawQuery(sSqlStr, slecttionArgs);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("getDatabaseBySql", e.getMessage());
    		}
    	}
    	
    	return mCursor; 
    }
    
    /**
     * 通过android定义的接口查询
     * @param distinct：指定是否去除重复记录
     * @param table：要查询的表名
     * @param columns：要查询的列名
     * @param selection：查询条件子句，相当于select语句的where关键字后面的部分，在条件子句中允许使用占位符“？“
     * @param selectionArgs：用于为selection子句中占位符传入的参数值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会出异常。
     * @param groupBy：用于控制分组，相当于select语句group by关键字后面的部分
     * @param having：用于对分组进行过滤，相当于select语句having关键字后面的部分
     * @param orderBy：用于对基类进行排序，相当于select语句order by关键字后面的部分，如：personid desc，age asc。
     * @param limit：用于进行分页，相当于select语句limit关键字后面的部分。
     * @return
     */
    public synchronized Cursor getDatabaseByAndroid(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs,
    		String groupBy,String having, String orderBy, String limit)
    {
    	Cursor mCursor = null;

    	if(m_databaseObj != null)
    	{
    		/*执行查询数据*/
    		try {
    			mCursor = m_databaseObj.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("getDatabaseByAndroid", e.getMessage());
    		}
    	}
    	
    	return mCursor; 
    }
    
    /**
     * 输入地址id号直接返回地址结构体
     * @param nAddrId
     * @return
     */
    public synchronized AddrProp getAddrById(int nAddrId){
    	if (mAddrProp.containsKey(nAddrId)) {
			return mAddrProp.get(nAddrId);
		}else{
			return readAddr(nAddrId);
		}
    }
    
    /**
     * 输入地址id号直接返回地址结构体
     * @param nAddrId
     * @return
     */
    public synchronized AddrProp readAddr(int nAddrId)
    {
	//	Cursor addrProp = getDatabaseByType("addr", null, CASE_COLUMN_TYPE.QUERY_BY_ID, nAddrId);
		Cursor addrProp = getDatabaseBySql("select * from addr where nAddrId = " + nAddrId, null);
		if(addrProp != null)
		{
			AddrProp mTmpAddr = null;
			if (addrProp.moveToNext()) {
				mTmpAddr=new AddrProp();
				/*读取连接类型*/
				mTmpAddr.eConnectType = addrProp.getShort(addrProp.getColumnIndex("eConnectType"));
				
				/*读取PLC自定义号*/
				mTmpAddr.nUserPlcId = addrProp.getShort(addrProp.getColumnIndex("nUserPlcId"));
				
				/*读取协议名字*/
				mTmpAddr.sPlcProtocol = addrProp.getString(addrProp.getColumnIndex("sPlcProtocol"));
				
				/*读取PLC的站号*/
				mTmpAddr.nPlcStationIndex = addrProp.getInt(addrProp.getColumnIndex("nPlcStationIndex"));
				
				/*读取PLC的寄存器号*/
				mTmpAddr.nRegIndex = addrProp.getShort(addrProp.getColumnIndex("nPlcRegIndex")); 
				
				/*读取PLC的地址值*/
				mTmpAddr.nAddrValue = addrProp.getInt(addrProp.getColumnIndex("nPlcStartAddr")); 
				
				/*读取PLC的地址长度*/
				mTmpAddr.nAddrLen = addrProp.getInt(addrProp.getColumnIndex("nAddrLen")); 
				
				/*读取PLC的地址读写属性*/
				mTmpAddr.eAddrRWprop = addrProp.getShort(addrProp.getColumnIndex("eRwLevel")); 
			}
			addrProp.close();
			return mTmpAddr;
		}else{
			Log.e("getAddrById", "From a database query address ID:" + nAddrId + " failed");
			return null;
		}
    }
    
    /**
     * 把地址从数据库加载到内存中
     */
    private void readAddr(){
    	//long start=System.currentTimeMillis();
    	
    	SKDataBaseInterface db=SkGlobalData.getProjectDatabase();
    	if (db!=null) {
    		try {
        		Cursor addrProp = db.getDatabaseBySql("select * from addr ", null);
        		if(addrProp != null){
        			while (addrProp.moveToNext()) {
        				AddrProp mTmpAddr = new AddrProp();
            			
        				int id=addrProp.getInt(addrProp.getColumnIndex("nAddrId"));
        				
        				mTmpAddr.nAddrId=id;
        				
            			/*读取连接类型*/
            			mTmpAddr.eConnectType = addrProp.getShort(addrProp.getColumnIndex("eConnectType"));
            			
            			/*读取PLC自定义号*/
            			mTmpAddr.nUserPlcId = addrProp.getShort(addrProp.getColumnIndex("nUserPlcId"));
            			
            			/*读取协议名字*/
            			mTmpAddr.sPlcProtocol = addrProp.getString(addrProp.getColumnIndex("sPlcProtocol"));
            			
            			/*读取PLC的站号*/
            			mTmpAddr.nPlcStationIndex = addrProp.getInt(addrProp.getColumnIndex("nPlcStationIndex"));
            			
            			/*读取PLC的寄存器号*/
            			mTmpAddr.nRegIndex = addrProp.getShort(addrProp.getColumnIndex("nPlcRegIndex")); 
            			
            			/*读取PLC的地址值*/
            			mTmpAddr.nAddrValue = addrProp.getInt(addrProp.getColumnIndex("nPlcStartAddr")); 
            			
            			/*读取PLC的地址长度*/
            			mTmpAddr.nAddrLen = addrProp.getInt(addrProp.getColumnIndex("nAddrLen")); 
            			
            			/*读取PLC的地址读写属性*/
            			mTmpAddr.eAddrRWprop = addrProp.getShort(addrProp.getColumnIndex("eRwLevel")); 
            			mAddrProp.put(id, mTmpAddr);
    				}
        			addrProp.close();
        		}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("SKDataBaseInterface", "load addr error!");
			}
    		
		}
    	
    	//Log.d("SKScene", "addr time:"+(System.currentTimeMillis()-start));
    }
    
    /**
     * 加载地址到内存
     */
    private static HashMap<Integer, AddrProp> mAddrProp=new HashMap<Integer, AddrProp>();
    private int nTaskId=1;
    public void loadAddr(){
    	mAddrProp.clear();
    	SKThread.getInstance().getBinder().onTask(MODULE.CALLBACK, nTaskId, null, callback, 0);
    }
    
    
    /**
     * 后台线程回调
     */
    SKThread.ICallback callback=new SKThread.ICallback() {
		
		@Override
		public void onUpdate(Object msg, int taskId) {
			if (taskId==nTaskId) {
				readAddr();
			}
		}
		
		@Override
		public void onUpdate(int msg, int taskId) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onUpdate(String msg, int taskId) {
			// TODO Auto-generated method stub
			
		}
	};
    
    public synchronized Vector<AddrProp > getAddrListBySql(String sSqlStr, String[] slecttionArgs)
    {
    	Cursor dataProp = null;
    	Vector<AddrProp > mTmpAddrList = new Vector<AddrProp >();
    	dataProp = getDatabaseBySql(sSqlStr, slecttionArgs);
    	if(null != dataProp)
		{
			mTmpAddrList.clear();
			while(dataProp.moveToNext())
			{
				AddrProp mTmpAddr = new AddrProp();
				
				/*读取连接类型*/
				mTmpAddr.eConnectType = dataProp.getShort(dataProp.getColumnIndex("eConnectType"));
				
				/*读取PLC自定义号*/
				mTmpAddr.nUserPlcId = dataProp.getShort(dataProp.getColumnIndex("nUserPlcId"));
				
				/*读取协议名字*/
				mTmpAddr.sPlcProtocol = dataProp.getString(dataProp.getColumnIndex("sPlcProtocol"));
				
				/*读取PLC的站号*/
				mTmpAddr.nPlcStationIndex = dataProp.getInt(dataProp.getColumnIndex("nPlcStationIndex"));
				
				/*读取PLC的寄存器号*/
				mTmpAddr.nRegIndex = dataProp.getShort(dataProp.getColumnIndex("nPlcRegIndex")); 
				
				/*读取PLC的地址值*/
				mTmpAddr.nAddrValue = dataProp.getInt(dataProp.getColumnIndex("nPlcStartAddr")); 
				
				/*读取PLC的地址长度*/
				mTmpAddr.nAddrLen = dataProp.getInt(dataProp.getColumnIndex("nAddrLen")); 
				
				/*读取PLC的地址读写属性*/
				mTmpAddr.eAddrRWprop = dataProp.getShort(dataProp.getColumnIndex("eRwLevel")); 
				
				mTmpAddrList.add(mTmpAddr);
			}
			dataProp.close();
			return mTmpAddrList;
		}
    	
    	Log.e("getAddrListBySql", "From a database query address list failed, SQL string is:：" + sSqlStr);
    	return null;
    }
    
    /**
     * 插入数据
     * @param sTableName：表名
     * @param values：要插入的值的集合，比如要与要插入的列名对应。
     * @return
     */
    public synchronized long insertData(String sTableName, ContentValues values)
    {
    	long nInsertId = -1;
    	
    	if(m_databaseObj != null)
    	{
    		/*执行插入数据*/
    		try {
    			nInsertId = m_databaseObj.insert(sTableName, null, values);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("insertData", e.getMessage());
    		}
    	}
    	
    	return nInsertId;
    }
    
    /**
     * 插入数据
     * @param sTableName：表名
     * @param values：要插入的值的集合，比如要与要插入的列名对应。
     * @return
     */
    public synchronized void insertData(String sSqlStr)
    {
    	if(m_databaseObj != null)
    	{
    		/*执行插入数据*/
    		try {
    			m_databaseObj.execSQL(sSqlStr);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("insertData", e.getMessage());
    		}
    	}
    }
    
    
    /**
     * 更新数据，修改数据库中的数据
     * @param sTableName：更新的表名
     * @param values：想要更新的数据集合
     * @param sWhereClause：满足该whereClause子句的记录将被更新
     * @param sWhereArgs：用于为whereClause子句传入参数
     * @例如： update（"tableName", values, "_id>?, new Integer[]{20});
     * @return
     */
    public synchronized int updateByUserDef(String sTableName, ContentValues values, String sWhereClause, String[] sWhereArgs)
    {
    	int nRowNum = 0;
    	
    	if(m_databaseObj != null)
    	{
    		/*执行更新数据*/
    		try {
    			nRowNum = m_databaseObj.update(sTableName, values, sWhereClause, sWhereArgs);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("updateByUserDef", e.getMessage());
    		}
    	}
    	
    	return nRowNum;
    }
    
    /**
     * 根据一些默认的条件更新，比如根据ID、场景ID、itemid、查询所有数据等。
     * @param sTableName：表名
     * @param values：要更新的值的集合
     * @param eCaseType：要更新的类型，比如根据ID、场景ID、itemid、查询所有数据等
     * @param nCaseValue：要更新的条件
     * @return
     */
    public synchronized int updateByType(String sTableName, ContentValues values, CASE_COLUMN_TYPE eCaseType, int nCaseValue)
    {
    	int nRowNum = -1;
    	if(null == m_databaseObj) return nRowNum;

    	/*判断条件*/
    	String sWhereClause = "";
    	String[] selectionArgs = null; 
    	switch(eCaseType)
    	{
    	case QUERY_BY_ID:
    	{
    		sWhereClause = "id = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_BY_SCENE_ID:
    	{
    		sWhereClause = "nSceneId = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_BY_ITEM_ID:
    	{
    		sWhereClause = "nItemId = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_ALL:
    	{
    		break;
    	}
    	case QUERY_NULL:
    	default:
    	{
    		Log.e("update database failed", "Please select the correct conditions as：QUERY_BY_ID、QUERY_BY_SCENE_ID、QUERY_BY_ITEM_ID");
    		return nRowNum;
    	}
    	}
    	
    	/*执行更新数据*/
    	try {
    		nRowNum = m_databaseObj.update(sTableName, values, sWhereClause, selectionArgs);
		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.e("updateByType", e.getMessage());
		}

    	return nRowNum;
    }
	
    /**
     * 根据用户自定义条件删除
     * @param sTableName：表名
     * @param sWhereClause：满足该条件的子句记录将会被删除
     * @param sWhereArgs：用于为sWhereClause子句传入参数。
     * @return
     */
    public synchronized int deleteByUserDef(String sTableName, String sWhereClause, String[] sWhereArgs)
    {
    	int nDelCount = -1;
    	if(m_databaseObj != null)
    	{
    		/*执行删除数据*/
    		try {
    			nDelCount = m_databaseObj.delete(sTableName, sWhereClause, sWhereArgs);
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("deleteByUserDef", e.getMessage());
    		}
    	}
    	return nDelCount;
    }
	
    /**
     * 根据一些默认的条件删除，比如根据ID、场景ID、itemid、查询所有数据等。
     * @param sTableName：表名
     * @param eCaseType：要更新的类型，比如根据ID、场景ID、itemid、查询所有数据等
     * @param nCaseValue：要更新的条件
     * @return
     */
    public synchronized int deleteByType(String sTableName, CASE_COLUMN_TYPE eCaseType, int nCaseValue)
    {
    	int nDelCount = 0;
    	if(null == m_databaseObj) return nDelCount;
    	
    	/*判断条件*/
    	String sWhereClause = "";
    	String[] selectionArgs = null; 
    	switch(eCaseType)
    	{
    	case QUERY_BY_ID:
    	{
    		sWhereClause = "id = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_BY_SCENE_ID:
    	{
    		sWhereClause = "nSceneId = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_BY_ITEM_ID:
    	{
    		sWhereClause = "nItemId = ?";
    		selectionArgs = new String[]{Integer.toString(nCaseValue)};
    		break;
    	}
    	case QUERY_ALL:
    	{
    		break;
    	}
    	case QUERY_NULL:
    	default:
    	{
    		Log.e("delete database failed", "Please select the correct conditions as：QUERY_BY_ID、QUERY_BY_SCENE_ID、QUERY_BY_ITEM_ID");
    		return nDelCount;
    	}
    	}
    	
    	/*执行删除数据*/
    	try {
    		nDelCount = m_databaseObj.delete(sTableName, sWhereClause, selectionArgs);
		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.e("deleteByType", e.getMessage());
		}
    	
    	return nDelCount;
    }
    
    /**
     * 执行SQL语句
     * @param sSqlStr : 执行任何SQL语句
     * @return
     */
    public synchronized boolean execSql(String sSqlStr)
    {
    	boolean bSuccess = false;
    	if(m_databaseObj != null)
    	{
    		/*执行更新数据*/
    		try {
    			m_databaseObj.execSQL(sSqlStr);
    			bSuccess = true;
    		} catch (SQLiteException e) {
    			// TODO: handle exception
    			Log.e("updateByUserDef", e.getMessage());
    			bSuccess = false;
    		}
    	}
    	
    	return bSuccess;
    }
    
    /**
     * 测试用的接口函数，使用的时候也可以参照这测试代码来写。
     */
    public void dataInterfaceTest()
    {
    	boolean bSuccess = openDatabase("/data/data/com.android.Samkoonhmi/databases/android.db");
    	
    	if(false == bSuccess) return ;
    	
    	deleteByType("bitCtlScene", CASE_COLUMN_TYPE.QUERY_ALL, 3);
    	
    	ContentValues values = new ContentValues();
    	
    	/*插入第一个数据*/
    	values.put("bValidValue", true);
    	values.put("bAutoReset", true);
    	values.put("nSceneId", 4);
    	values.put("nBitCtlAddrId ", 50);
    	insertData("bitCtlScene", values);
    	
    	/*插入第二个数据*/
    	values.put("bValidValue", true);
    	values.put("bAutoReset", true);
    	values.put("nSceneId", 5);
    	values.put("nBitCtlAddrId ", 60);
    	insertData("bitCtlScene", values);
    	
    	/*插入第三个数据*/
    	values.put("bValidValue", false);
    	values.put("bAutoReset", true);
    	values.put("nSceneId", 6);
    	values.put("nBitCtlAddrId ", 70);
    	insertData("bitCtlScene", values);
    	
    	/*查询所有数据*/
    	Cursor result = getDatabaseByType("bitCtlScene", null, CASE_COLUMN_TYPE.QUERY_ALL, 0);
    	if(result != null)
    	{
    		System.out.println("插入后");
    		printCursor(result);
    		result.close();
    	}
    	
    	/*修改数据ByType*/
    	values.put("bValidValue", false);
    	values.put("bAutoReset", false);
    	values.put("nSceneId", 2);
    	values.put("nBitCtlAddrId ", 20);
    	updateByType("bitCtlScene", values, CASE_COLUMN_TYPE.QUERY_BY_ID, 1);
    	
    	/*查询所有数据ByType*/
    	result = getDatabaseByType("bitCtlScene", null, CASE_COLUMN_TYPE.QUERY_ALL, 0);
    	if(result != null)
    	{
    		System.out.println("修改后");
    		printCursor(result);
    		result.close();
    	}
    	
    	/*修改数据 ByUserDef*/
    	values.put("bValidValue", true);
    	values.put("bAutoReset", true);
    	values.put("nSceneId", 3);
    	values.put("nBitCtlAddrId ", 30);
    	updateByUserDef("bitCtlScene", values, "id > ?", new String[]{"2"});
    	
    	/*查询所有数据ByUserDef*/
    	result = getDatabaseByUserDef("bitCtlScene", null, "nSceneId = 3");
    	if(result != null)
    	{
    		System.out.println("修改后");
    		printCursor(result);
    		result.close();
    	}
    	
    	/*删除数据 ByType*/
    	deleteByType("bitCtlScene", CASE_COLUMN_TYPE.QUERY_BY_SCENE_ID, 3);
    	
    	/*查询所有数据ByType*/
    	result = getDatabaseByAndroid(true, "bitCtlScene", null,
    			null, null, null, null, null, null);
    	if(result != null)
    	{
    		System.out.println("删除后");
    		printCursor(result);
    		result.close();
    	}
    	
    	/*删除数据 ByUserDef*/
    	deleteByUserDef("bitCtlScene", null, null);
    	
    	/*查询所有数据BySql*/
    	result = getDatabaseBySql("select * from bitCtlScene", null);
    	if(result != null)
    	{
    		System.out.println("删除后");
    		printCursor(result);
    		result.close();
    	}
    }
    
    public void printCursor(Cursor cur)
    {
    	if(null == cur) return ;
    	
    	String str[] = cur.getColumnNames();
    	String sColumnName = "";
    	for(int i = 0; i < str.length; i++)
    	{
    		sColumnName += str[i];
    		sColumnName += ",";
    	}
    	System.out.println(sColumnName);
    	
    	while(cur.moveToNext())
		{
			int size = cur.getColumnCount();
			String oStr = " ";
			for(int i = 0; i < size; i++)
			{
				oStr += cur.getString(i);
				oStr += ",";
			}
			System.out.println(oStr);
		}
    }
}