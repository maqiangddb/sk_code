package com.android.Samkoonhmi.model;

import java.util.Vector;
import com.android.Samkoonhmi.skenum.DATA_TYPE;
import com.android.Samkoonhmi.skenum.STORAGE_MEDIA;
import com.android.Samkoonhmi.util.AddrProp;

/**
 * 一组配方的属性
 * @author Latory
 */
public class RecipeOGprop {

	public RecipeOGprop() {
	}

	/* 配方组ID */
	private int nGRecipeID;

	/* 配方组名 */
	private String sRecipeGName = "";

	/* 配方组描述 */
	private String sRecipeGDescri = "";

	/* 配方数量 */
	private int nRecipeNum;

	/* 配方长度，指地址长度 */
	private int nRecipeLen;

	/* 地址是否连续 */
	private boolean bContinue = true;

	/* 保存路径 */
	private STORAGE_MEDIA eSaveMedia = STORAGE_MEDIA.OTHER_STORAGE_MEDIA;

	/* 是否需要传输完成通知 */
	private boolean bCompleteNotic;

	/* 是否需要控制地址 */
	private boolean bNeedCtlAddr;

	/* 配方控制地址 */
	private AddrProp mCtlAddr = new AddrProp();

	/* 写完通知地址 */
	private AddrProp mComNoticAddr = new AddrProp();

	/*
	 * 配方元素名称列表，sElemNameList.get(0)是元素1的所有语言集合，sElemNameList.get(0).get(0)
	 * 是元素1的第一种语言的名称
	 */
	private Vector<Vector<String>> sElemNameList = new Vector<Vector<String>>();

	/* 地址的集合 */
	private Vector<AddrProp> nValueAddrList = new Vector<AddrProp>();

	/* 配方数据类型 */
	private Vector<DATA_TYPE> eDataTypeList = new Vector<DATA_TYPE>();

	/* 配方数据集合 */
	private Vector<RecipeOprop> mRecipePropList = new Vector<RecipeOprop>();

	/* 自定义键盘id */
	private int nKeyId;

	/* 自定义键盘X坐标 */
	private int nBoardX;

	/* 自定义键盘Y坐标 */
	private int nBoardY;
	
	/* 拷贝配方的名称*/
	private String mCopyRecipeName;
	
	/* 被拷贝的配方Id*/
	private int nCopyFrom ;
	/* 拷贝到的配方ID*/
	private int nCopyTo;

	public RecipeOGprop copyGroupRecipe() {
		RecipeOGprop mNewGroup = new RecipeOGprop();

		mNewGroup.setnGRecipeID(this.getnGRecipeID());
		mNewGroup.setsRecipeGName(this.getsRecipeGName());
		mNewGroup.setsRecipeGDescri(this.getsRecipeGDescri());
		mNewGroup.setnRecipeNum(this.getnRecipeNum());
		mNewGroup.setnRecipeLen(this.getnRecipeLen());
		mNewGroup.seteSaveMedia(this.geteSaveMedia());
		mNewGroup.setbCompleteNotic(this.isbCompleteNotic());
		mNewGroup.setbNeedCtlAddr(this.isbNeedCtlAddr());

		mNewGroup.mCtlAddr = this.mCtlAddr;
		mNewGroup.mComNoticAddr = this.mComNoticAddr;
		mNewGroup.setnKeyId(this.getnKeyId());
		mNewGroup.setnBoardX(this.getnBoardX());
		mNewGroup.setnBoardY(this.getnBoardY());

		int nEsize = this.getsElemNameList().size();
		for (int i = 0; i < nEsize; i++) {
			Vector<String> sTmpList = new Vector<String>();
			int nLSize = this.getsElemNameList().get(i).size();
			for (int k = 0; k < nLSize; k++) {
				sTmpList.add(this.getsElemNameList().get(i).get(k));
			}

			mNewGroup.getsElemNameList().add(sTmpList);
		}
		mNewGroup.nValueAddrList = this.nValueAddrList;
		mNewGroup.eDataTypeList = this.eDataTypeList;

		mNewGroup.mRecipePropList.clear();
		int nSize = this.getmRecipePropList().size();
		for (int i = 0; i < nSize; i++) {
			mNewGroup.mRecipePropList.add(this.getmRecipePropList().get(i)
					.copyRecipe());
		}

		return mNewGroup;
	}

	public int getnGRecipeID() {
		return nGRecipeID;
	}

	public void setnGRecipeID(int nGRecipeID) {
		this.nGRecipeID = nGRecipeID;
	}

	public String getsRecipeGName() {
		return sRecipeGName;
	}

	public void setsRecipeGName(String sRecipeGName) {
		this.sRecipeGName = sRecipeGName;
	}

	public String getsRecipeGDescri() {
		return sRecipeGDescri;
	}

	public void setsRecipeGDescri(String sRecipeGDescri) {
		this.sRecipeGDescri = sRecipeGDescri;
	}

	public int getnRecipeNum() {
		return nRecipeNum;
	}

	public void setnRecipeNum(int nRecipeNum) {
		this.nRecipeNum = nRecipeNum;
	}

	public int getnRecipeLen() {
		return nRecipeLen;
	}

	public void setnRecipeLen(int nRecipeLen) {
		this.nRecipeLen = nRecipeLen;
	}

	public boolean isbContinue() {
		return bContinue;
	}

	public void setbContinue(boolean bContinue) {
		this.bContinue = bContinue;
	}

	public STORAGE_MEDIA geteSaveMedia() {
		return eSaveMedia;
	}

	public void seteSaveMedia(STORAGE_MEDIA eSaveMedia) {
		this.eSaveMedia = eSaveMedia;
	}

	public boolean isbCompleteNotic() {
		return bCompleteNotic;
	}

	public void setbCompleteNotic(boolean bCompleteNotic) {
		this.bCompleteNotic = bCompleteNotic;
	}

	public boolean isbNeedCtlAddr() {
		return bNeedCtlAddr;
	}

	public void setbNeedCtlAddr(boolean bNeedCtlAddr) {
		this.bNeedCtlAddr = bNeedCtlAddr;
	}

	public AddrProp getmCtlAddr() {
		return mCtlAddr;
	}

	public void setmCtlAddr(AddrProp mCtlAddr) {
		this.mCtlAddr = mCtlAddr;
	}

	public AddrProp getmComNoticAddr() {
		return mComNoticAddr;
	}

	public void setmComNoticAddr(AddrProp mComNoticAddr) {
		this.mComNoticAddr = mComNoticAddr;
	}

	public Vector<Vector<String>> getsElemNameList() {
		return sElemNameList;
	}

	public void setsElemNameList(Vector<Vector<String>> sElemNameList) {
		this.sElemNameList = sElemNameList;
	}

	public Vector<AddrProp> getnValueAddrList() {
		return nValueAddrList;
	}

	public void setnValueAddrList(Vector<AddrProp> nValueAddrList) {
		this.nValueAddrList = nValueAddrList;
	}

	public Vector<RecipeOprop> getmRecipePropList() {
		return mRecipePropList;
	}

	public void setmRecipePropList(Vector<RecipeOprop> mRecipePropList) {
		this.mRecipePropList = mRecipePropList;
	}

	public Vector<DATA_TYPE> geteDataTypeList() {
		return eDataTypeList;
	}

	public void seteDataTypeList(Vector<DATA_TYPE> eDataTypeList) {
		this.eDataTypeList = eDataTypeList;
	}

	public int getnKeyId() {
		return nKeyId;
	}

	public void setnKeyId(int nKeyId) {
		this.nKeyId = nKeyId;
	}

	public int getnBoardX() {
		return nBoardX;
	}

	public void setnBoardX(int nBoardX) {
		this.nBoardX = nBoardX;
	}

	public int getnBoardY() {
		return nBoardY;
	}

	public void setnBoardY(int nBoardY) {
		this.nBoardY = nBoardY;
	}
	
	public void setmCopyRecipeName(String copyName){
		this.mCopyRecipeName = copyName;
	}
	
	public String getmCopyRecipeName(){
		return mCopyRecipeName;
	}
	
	public void setnCopyFrom(int fromId){
		nCopyFrom = fromId;
	}
	
	public int getnCopyFrom(){
		return nCopyFrom;
	}
	
	public void setnCopyTo(int toId){
		nCopyTo = toId;
	}
	
	public int getnCopyTo(){
		return nCopyTo;
	}
	
}
