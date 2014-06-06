package com.android.Samkoonhmi.databaseinterface;

import java.util.ArrayList;
import java.util.HashMap;
import android.database.Cursor;
import android.util.Log;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.SceneItemInfo;
import com.android.Samkoonhmi.model.SceneNumInfo;
import com.android.Samkoonhmi.model.skmenu.SceneMenuInfo;
import com.android.Samkoonhmi.skenum.BACKCSS;
import com.android.Samkoonhmi.skenum.IntToEnum;
import com.android.Samkoonhmi.skglobalcmn.SkGlobalData;
import com.android.Samkoonhmi.skwindow.SKSceneManage.SHOW_TYPE;
import com.android.Samkoonhmi.util.MACRO_TYPE;

/**
 * 画面数据读取
 * 
 * @author 刘伟江
 * @version v 1.0.0.1
 */
public class SceneBiz extends DataBase{

	SKDataBaseInterface db = null;
	
	public SceneBiz(){
		
	}

	/**
	 * 查询画面信息
	 * @param id-画面Id
	 */
	public ScenceInfo select(int id) {
		if (id<0) {
			return null;
		}
		ScenceInfo info=null;
		try {
			db=SkGlobalData.getProjectDatabase();
			if (db!=null) {
				String sql = "select * from scene where nSceneId=?";
				Cursor cursor = db.getDatabaseBySql(sql, new String[]{id+""});
				if (cursor!=null) {
					info = new ScenceInfo();
					while (cursor.moveToNext()) {
						//nSceneId
						info.setnSceneId(cursor.getInt(1));
						//sScreenName
						info.setsScreenName(cursor.getString(2));
						//bLogout
						boolean touch=cursor.getString(3).equals("true")?true:false;
						info.setbLogout(touch);
						//sNumber
						info.setnNum(cursor.getInt(4));
						//nSceneWidth
						info.setnSceneWidth(cursor.getInt(5));
						//nSceneHeight
						info.setnSceneHeight(cursor.getInt(6));
						//eBackType
						info.seteBackType(getBackcss(cursor.getInt(7)));
						//nBackColor
						info.setnBackColor(cursor.getInt(8));
						//nForeColor
						info.setnForeColor(cursor.getInt(9));
						//eDrawStyle
						info.seteDrawStyle(IntToEnum.getCssType(cursor.getInt(10)));
						//sPicturePath
						info.setsPicturePath(cursor.getString(11));
						info.seteType(SHOW_TYPE.DEFAULT);
						info.setbShowTitle(false);
						//bSlide
						boolean slide=cursor.getString(13).equals("true")?true:false;
						info.setbSlide(slide);
						if (slide) {
							/**
							 * leftId=-1,表示不能滑动，
							 *       =0, 表示下一个画面
							 *       >0, 表示特定画面
							 * rightId 同上
							 */
							//nTowardLeftId
							info.setnTowardLeftId(cursor.getInt(14)-1);
							//nTowardRIghtId
							info.setnTowardRIghtId(cursor.getInt(15)-1);
						}else {
							info.setnTowardLeftId(-1);
							info.setnTowardRIghtId(-1);
						}
						//nSlideStyle
						info.setnSlideStyle(cursor.getInt(16));
						info.setSceneMacroIDList(selectMacroIDListBySceneID(info.getnSceneId()));
						
						if (info.getnTowardLeftId()>0) {
							info.setnTowardLeftId(getSceneId(info.getnTowardLeftId()));
						}
						if (info.getnTowardRIghtId()>0) {
							info.setnTowardRIghtId(getSceneId(info.getnTowardRIghtId()));
						}
					}
				}
				
				close(cursor);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SceneBiz", "get scene info error!");
		}
		
		return info;
	}
	
	/**
	 * 获取所有画面的属性
	 */
	public ArrayList<ScenceInfo> getAllSceneInfo(){
		//long start=System.currentTimeMillis();
		ArrayList<ScenceInfo> list=new ArrayList<ScenceInfo>();
		try {
			db=SkGlobalData.getProjectDatabase();
			if (db!=null) {
				String sql = "select * from scene order by sNumber  asc ";
				Cursor cursor = db.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						ScenceInfo info = new ScenceInfo();
						//nSceneId
						info.setnSceneId(cursor.getInt(1));
						//sScreenName
						info.setsScreenName(cursor.getString(2));
						//bLogout
						boolean touch=cursor.getString(3).equals("true")?true:false;
						info.setbLogout(touch);
						//sNumber
						info.setnNum(cursor.getInt(4));
						//nSceneWidth
						info.setnSceneWidth(cursor.getInt(5));
						//nSceneHeight
						info.setnSceneHeight(cursor.getInt(6));
						//eBackType
						info.seteBackType(getBackcss(cursor.getInt(7)));
						//nBackColor
						info.setnBackColor(cursor.getInt(8));
						//nForeColor
						info.setnForeColor(cursor.getInt(9));
						//eDrawStyle
						info.seteDrawStyle(IntToEnum.getCssType(cursor.getInt(10)));
						//sPicturePath
						info.setsPicturePath(cursor.getString(11));
						info.seteType(SHOW_TYPE.DEFAULT);
						info.setbShowTitle(false);
						//bSlide
						boolean slide=cursor.getString(13).equals("true")?true:false;
						info.setbSlide(slide);
						if (slide) {
							/**
							 * leftId=-1,表示不能滑动，
							 *       =0, 表示下一个画面
							 *       >0, 表示特定画面
							 * rightId 同上
							 */
							//nTowardLeftId
							info.setnTowardLeftId(cursor.getInt(14)-1);
							//nTowardRIghtId
							info.setnTowardRIghtId(cursor.getInt(15)-1);
						}else {
							info.setnTowardLeftId(-1);
							info.setnTowardRIghtId(-1);
						}
						//nSlideStyle
						info.setnSlideStyle(cursor.getInt(16));
						info.setSceneMacroIDList(selectMacroIDListBySceneID(info.getnSceneId()));
						
						if (info.getnTowardLeftId()>0) {
							info.setnTowardLeftId(getSceneId(info.getnTowardLeftId()));
						}
						if (info.getnTowardRIghtId()>0) {
							info.setnTowardRIghtId(getSceneId(info.getnTowardRIghtId()));
						}
						list.add(info);
					}
				}
				close(cursor);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SceneBiz", "get scene info error!");
		}
		//Log.d("DataBase", " time : "+(System.currentTimeMillis()-start));
		return list;
	}
	
	
	/**
	 * 获取画面类型
	 */
	public HashMap<Integer, ArrayList<Integer>> getSceneType(){
		HashMap<Integer, ArrayList<Integer>> list=new HashMap<Integer, ArrayList<Integer>>();
		try {
			db=SkGlobalData.getProjectDatabase();
			if (db!=null) {
				Cursor cursor=null;
				String sql="select distinct(nItemTableType),nSceneId from sceneanditem order by nSceneId ";
				cursor=db.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					int sid=-1;
					while (cursor.moveToNext()) {
						//nSceneId
						int id=cursor.getInt(1);
						if (sid!=id) {
							sid=id;
							ArrayList<Integer> mTypeList=new ArrayList<Integer>();
						    //nItemTableType
							int type=cursor.getInt(0);
							mTypeList.add(type);
							list.put(sid, mTypeList);
						}else {
							ArrayList<Integer> mTypeList=list.get(sid);
							//nItemTableType
							int type=cursor.getInt(0);
							mTypeList.add(type);
						}
						
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get item list error!");
		}
		
		
		return list;
	}
	
	/**
	 * 获取所有画面
	 */
	public ArrayList<SceneMenuInfo> select(){
		ArrayList<SceneMenuInfo> list=new ArrayList<SceneMenuInfo>();
		try {
			db=SkGlobalData.getProjectDatabase();
			if (db!=null) {
				String sql = "select * from scene where bIsAddMenu='true'";
				Cursor cursor = db.getDatabaseBySql(sql,null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						SceneMenuInfo info=new SceneMenuInfo();
						//nSceneId
						info.setId(cursor.getInt(1));
						//sScreenName
						info.setName(cursor.getString(2));
						list.add(info);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get all secen info error!");
		}
		return list;
	}
	
	/**
	 * 获取菜单画面数量，如果为0 不可画面不可滑动到菜单
	 */
	public boolean getTouchMenu(){
		boolean result=false;
		db=SkGlobalData.getProjectDatabase();
		if (db!=null) {
			String sql = "select count(*) as num from scene where bIsAddMenu='true'";
			Cursor cursor = db.getDatabaseBySql(sql,null);
			int num=0;
			if (cursor!=null) {
				while (cursor.moveToNext()) {
					//num
					num=cursor.getInt(0);
				}
			}
			close(cursor);
			if (num>0) {
				return true;
			}
		}
		return result;
	}
	
	/**
	 * 获取初始化控件数量
	 */
	public int getInitSceneNum(int sid){
		int count=0;
		String sql="select count(*) as num from sceneAndItem where nSceneId="+sid;
		try {
			db=SkGlobalData.getProjectDatabase();
			if (db!=null) {
				Cursor cursor=db.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						String num=cursor.getString(0);
						if (num!=null) {
							count=Integer.parseInt(num);
						}
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get scene max num error!");
		}
		
		return count;
	}
	
	/**
	 * 返回画面总数
	 */
	public int getSceneMaxNum(){
		int count=0;
		String sql="select max(sNumber) as num from scene ";
		try {
			db=SkGlobalData.getProjectDatabase();
			if (db!=null) {
				Cursor cursor=db.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						count=cursor.getInt(0);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get scene max num error!");
		}
		
		return count;
	}
	
	/**
	 * 判断是否存在这个画面
	 * 
	 */
	public boolean hasScene(int sceneId){
		boolean has=false;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				String sql = "select count(*) as num from scene where nSceneId=?";
				Cursor cursor = db.getDatabaseBySql(sql, new String[]{sceneId+""});
				if (cursor!=null) {
					int num=0;
					while (cursor.moveToNext()) {
						num=cursor.getInt(0);
					}
					if (num>0) {
						has=true;
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "hasScene(int sceneId) error!");
		}
		
		return has;
	}
	
	/**
	 * 根据id 获取画面id
	 */
	public int getSceneId(int numId){
		int num=0;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				String sql = "select nSceneId  from scene where id=?";
				Cursor cursor = db.getDatabaseBySql(sql, new String[]{numId+""});
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						//nSceneId
						num=cursor.getInt(0);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get scene id error!");
		}
		
		return num;
	}
	
	/**
	 * 根据画面id 获取序号id
	 */
	public int getIdBySceneId(int numId){
		int num=0;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				String sql = "select id from scene where nSceneId=?";
				Cursor cursor = db.getDatabaseBySql(sql, new String[]{numId+""});
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						//id
						num=cursor.getInt(0);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get scene id error!");
		}
		
		return num;
	}
	
	
	/**
	 * 根据画面id，获取序号id
	 */
	private static HashMap<Integer, Integer> map=null;
	public int getWindowId(int id){
		if (map==null) {
			map=new HashMap<Integer, Integer>();
			
			try {
				db=SkGlobalData.getProjectDatabase();
				if(db!=null){
					String sql = "select id,nSceneId from windown ";
					Cursor cursor = db.getDatabaseBySql(sql, null);
					if (cursor!=null) {
						while (cursor.moveToNext()) {
							//id
							int num=cursor.getInt(0);
							//nSceneId
							int sid=cursor.getInt(1);
							map.put(sid, num);
						}
					}
					close(cursor);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("SceneBiz", "get window id error!");
			}
		}
		
		int nid=-1;
		if (map.containsKey(id)) {
			nid=map.get(id);
		}
		
		return nid;
	}
	
	
	/**
	 * 获取所有画面id
	 */
	public ArrayList<Integer> getAllSceneId(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		int num=0;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				String sql = "select nSceneId  from scene ";
				Cursor cursor = db.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						//nSceneId
						num=cursor.getInt(0);
						list.add(num);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "get All Scene Id error!");
		}
		return list;
	}

	/**
	 * 跳转到下一画面
	 */
	public int getNextSceneId(int cId){
		int num=0;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				String sql = "select  nSceneId from  scene where nSceneId>? limit 1";
				Cursor cursor = db.getDatabaseBySql(sql, new String[]{cId+""});
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						//nSceneId
						num=cursor.getInt(0);
					}
				}
				close(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "getNextSceneId(int cId) error");
		}
		
		return num;
	}
	
	/**
	 * 查询所有窗口的id
	 */
	public ArrayList<Integer> getWindowId(){
		ArrayList<Integer> list=null;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				//窗口
				String s = "select nSceneId from  windown";
				Cursor c = db.getDatabaseBySql(s, null);
				if (c!=null) {
					int num=0;
					list=new ArrayList<Integer>();
					while (c.moveToNext()) {
						//nSceneId
						num=c.getInt(0);
						list.add(num);
					}
					
				}
				close(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "SceneBiz get scene and window id error!");
		}
		
		return list;
	}
	
	/**
	 * 获取所有画面id
	 */
	public ArrayList<SceneNumInfo> getSceneNum(){
		ArrayList<SceneNumInfo> list=new ArrayList<SceneNumInfo>();
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				//画面
				String sql = "select id,nSceneId,sNumber,sScreenName from scene ";
				Cursor cursor = db.getDatabaseBySql(sql, null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						SceneNumInfo info=new SceneNumInfo();
						//id
						info.setId(cursor.getInt(0));
						//nSceneId
						info.setSid(cursor.getInt(1));
						//sNumber
						info.setNum(cursor.getInt(2));
						//sScreenName
						info.setName(cursor.getString(3));
						info.seteType(SHOW_TYPE.DEFAULT);
						list.add(info);
					}
				}
				close(cursor);
				
				//窗口
				String s = "select id,nSceneId,sNumber,sScreenName from  windown";
				Cursor c = db.getDatabaseBySql(s, null);
				if (c!=null) {
					while (c.moveToNext()) {
						SceneNumInfo info=new SceneNumInfo();
						//id
						info.setId(c.getInt(0));
						//nSceneId
						info.setSid(c.getInt(1));
						//sNumber
						info.setNum(c.getInt(2));
						//sScreenName
						info.setName(c.getString(3));
						info.seteType(SHOW_TYPE.FLOATING);
						list.add(info);
					}
					
				}
				close(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "ak get scene num error!");
		}
		
		return list;
	}
	
	
	/**
	 * 获取sid 类型，场景or窗口
	 */
	public int[] getSceneType(int sid){
		int[] type=new int[]{0,0};
		boolean select=true;
		try {
			db=SkGlobalData.getProjectDatabase();
			if(db!=null){
				//画面
				String s = "select nSceneId from  scene where nSceneId=?";
				Cursor c = db.getDatabaseBySql(s, new String[]{sid+""});
				if (c!=null) {
					int num=0;
					while (c.moveToNext()) {
						//nSceneId
						num=c.getInt(0);
						if (num>0) {
							type[0]=1;
							select=false;
						}
					}
					
				}
				close(c);
				
				if (select) {
					//窗口
					String sql = "select nSceneId from  windown where nSceneId=?";
					Cursor cursor = db.getDatabaseBySql(sql, new String[]{sid+""});
					if (cursor!=null) {
						int num=0;
						while (cursor.moveToNext()) {
							//nSceneId
							num=cursor.getInt(0);
							if (num>0) {
								type[1]=1;
							}
						}
						
					}
					close(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("SceneBiz", "SceneBiz get scene and window id error!");
		}
		return type;
	}
	
	/**
	 * 获取场景里面的所有控件类型
	 * @param id-场景id
	 */
	public String getItemType(int id){
		String result="";
		return result;
	}
	
	/**
	 * 画面背景
	 */
	public BACKCSS getBackcss(int num){
		if (num==1) {
			return BACKCSS.BACK_CSS;
		}else if(num==2){
			return BACKCSS.BACK_IMG;
		}
		return BACKCSS.BACK_CSS;
	}
	
	/**
	 * 获得指定场景的场景宏指令ID
	 * */
	String mMacroQueryStr = new String("select * from macro where SceneID=? and (MacroType=? or MacroType=?)"); //查询语句
	public ArrayList<Short> selectMacroIDListBySceneID(int sceneid){
		
		db = SkGlobalData.getProjectDatabase();
		if (null == db) {// 获得数据库失败
			Log.e("SceneBiz", "selectMacroIDListBySceneID: Get database failed!");
			return null;
		}
		
		Cursor tmpCursor = null;
	
		tmpCursor = db.getDatabaseBySql(mMacroQueryStr,
				new String[] {Integer.toString(sceneid),Integer.toString(MACRO_TYPE.SLOOP),Integer.toString(MACRO_TYPE.SCTRLOOP)});
		if (null == tmpCursor) {// 获取游标失败
			Log.e("SceneBiz", "selectMacroIDListBySceneID: Get cursor failed!");
			return null;
		}
		
		ArrayList<Short> idlist = new ArrayList<Short>();
		while (tmpCursor.moveToNext()) {
			//MacroID
			idlist.add(tmpCursor.getShort(1));
		}
		close(tmpCursor);
		return idlist;
	}

	public int getItemId(int sid,int type){
		int id=-1;
		db=SkGlobalData.getProjectDatabase();
		if (db==null) {
			return id;
		}
		String sql="select nItemId from  sceneAndItem  where nSceneId=? and nItemTableType=?";
		Cursor cursor=db.getDatabaseBySql(sql, new String[]{sid+"",type+""});
		if (cursor!=null) {
			if (cursor.moveToNext()) {
				//nItemId
				id=cursor.getInt(0);
			}
			cursor.close();
		}
		return id;
	}
}
