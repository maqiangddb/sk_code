package com.android.Samkoonhmi.databaseinterface;

/**
 * 数据查询工具类型
 * @author Administrator
 * 创建时间 2012-9-3
 */
public class DBTool {

	/**
	 * 单例
	 */
	private static DBTool sInstance;
	public static DBTool getInstance(){
		if (sInstance==null) {
			sInstance=new DBTool();
		}
		return sInstance;
	}
	
	private DBTool(){}
	
	//报警数据库查询
	private AlarmBiz mAlarmBiz;
	//开关数据库查询
	private ButtonBiz mButtonBiz;
	//图表数据库查询
	private GraphBiz mGraphBiz;
	//历史数据显示数据库查询
	private HistoryShowBiz mHistoryShowBiz;
	//消息显示器数据库查询
	private MessageDisplayBiz mMessageDisplayBiz;
	//配方数据库查询
	private RecipeDataBiz mRecipeDataBiz;
	//配方显示器数据库查询
	private RecipeShowBiz mRecipeShowBiz;
	//动画显示器
	private AnimationViewerBiz mAnimationViewerBiz;
	//刻度
	private CalibrationBiz mCalibrationBiz;
	//下拉框信息转换类
	private ComboxInfoBiz mComboxInfoBiz;
	//宏指令
	private CompMacroBiz mCompMacroBiz;
	//动态圆形数据库查询 
	private DynamicCircleBiz mDynamicCircleBiz;
	//动态矩形数据库查询 
	private DynamicRectBiz mDynamicRectBiz;
	//GIF显示器数据库查询
	private GifViewerBiz mGifViewerBiz;
	//全局宏指令数据库访问接口
	private GlobalMacroBiz mGlobalMacroBiz;
	//曲线数据读取
	private HistoryTrendsBiz mHistoryTrendsBiz;
	//图片显示器数据库查询
	private ImageViewerBiz mImageViewerBiz;
	//键盘
	private KeyBroadBiz mKeyBroadBiz;
	//线
	private LineInfoBiz mLineInfoBiz;
	//场景
	private SceneBiz mSceneBiz;
	//场景宏
	private SceneMacroBiz mSceneMacroBiz;
	//场景位置信息
	private ScenePosInfoBiz mScenePosInfoBiz;
	//多边型
	private ShapInfoBiz mShapInfoBiz;
	//进度条
	private SlideBiz mSlideBiz;
	//静态文本
	private StaticTextBiz mStaticTextBiz;
	//时间显示器
	private TimeShowBiz mTimeShowBiz;
	//数值显示器
	private NumberInputBiz mNumberInputBiz;
	//acill显示器
	private AcillInputBiz mAcillInputBiz;
	//配方选择器
	private RecipeSelectBiz mRecipeSelectBiz;
	//窗口查询类
	private WindowBiz mWindowBiz;
	//组合图形查询类
	private GroupShapeBiz groupShapeBiz;
	//留言板
	private MessageBoardBiz messageBoardBiz;
	//时间守则
	private TimeSettingBiz mTimeSettingBiz;
	//系统参数
	private SystemInfoBiz mSystemInfoBiz;
	//流动块
	private FlowBlockBiz mFlowBlockBiz;
	//表格
	private TableBiz mTableBiz;
	//下拉框
	private DragdownBoxBiz mDragdownBoxBiz;
	//XY曲线
	private XYCurveBiz mXYCurveBiz;
	//表达式
	private ExpressBiz mExpressBiz;

	private UserInfoBiz mUserInfoBiz;

	/**
	 * 报警
	 */
	public AlarmBiz getmAlarmBiz() {
		if (mAlarmBiz==null) {
			mAlarmBiz=new AlarmBiz();
		}
		return mAlarmBiz;
	}
	
	/**
	 * 开关
	 */
	public ButtonBiz getmButtonBiz() {
		if (mButtonBiz==null) {
			mButtonBiz=new ButtonBiz();
		}
		return mButtonBiz;
	}
	
	/**
	 * 图表
	 */
	public GraphBiz getmGraphBiz() {
		if (mGraphBiz==null) {
			mGraphBiz=new GraphBiz();
		}
		return mGraphBiz;
	}
	
	/**
	 * 历史数据显示器
	 */
	public HistoryShowBiz getmHistoryShowBiz() {
		if (mHistoryShowBiz==null) {
			mHistoryShowBiz=new HistoryShowBiz();
		}
		return mHistoryShowBiz;
	}
	
	/**
	 * 消息显示器
	 */
	public MessageDisplayBiz getmMessageDisplayBiz() {
		if (mMessageDisplayBiz==null) {
			mMessageDisplayBiz=new MessageDisplayBiz();
		}
		return mMessageDisplayBiz;
	}
	
	/**
	 * 配方数据
	 */
	public RecipeDataBiz getmRecipeDataBiz() {
		if (mRecipeDataBiz==null) {
			mRecipeDataBiz=new RecipeDataBiz();
		}
		return mRecipeDataBiz;
	}
	
	
	/**
	 * 配方显示器
	 */
	public RecipeShowBiz getmRecipeShowBiz() {
		if (mRecipeShowBiz==null) {
			mRecipeShowBiz=new RecipeShowBiz();
		}
		return mRecipeShowBiz;
	}

	/**
	 * 动画显示器
	 */
	public AnimationViewerBiz getmAnimationViewerBiz() {
		if (mAnimationViewerBiz==null) {
			mAnimationViewerBiz=new AnimationViewerBiz();
		}
		return mAnimationViewerBiz;
	}

	/**
	 * 刻度
	 */
	public CalibrationBiz getmCalibrationBiz() {
		if (mCalibrationBiz==null) {
			mCalibrationBiz=new CalibrationBiz();
		}
		return mCalibrationBiz;
	}

	/**
	 * 下拉框
	 */
	public ComboxInfoBiz getmComboxInfoBiz() {
		if (mComboxInfoBiz==null) {
			mComboxInfoBiz=new ComboxInfoBiz();
		}
		return mComboxInfoBiz;
	}

	/**
	 * 控件宏指令
	 */
	public CompMacroBiz getmCompMacroBiz() {
		if (mCompMacroBiz==null) {
			mCompMacroBiz=new CompMacroBiz();
		}
		return mCompMacroBiz;
	}

	/**
	 * 动态圆
	 */
	public DynamicCircleBiz getmDynamicCircleBiz() {
		if (mDynamicCircleBiz==null) {
			mDynamicCircleBiz=new DynamicCircleBiz();
		}
		return mDynamicCircleBiz;
	}

	/**
	 * 动态矩形
	 */
	public DynamicRectBiz getmDynamicRectBiz() {
		if (mDynamicRectBiz==null) {
			mDynamicRectBiz=new DynamicRectBiz();
		}
		return mDynamicRectBiz;
	}

	/**
	 * GIF显示器
	 */
	public GifViewerBiz getmGifViewerBiz() {
		if (mGifViewerBiz==null) {
			mGifViewerBiz=new GifViewerBiz();
		}
		return mGifViewerBiz;
	}

	/**
	 * 全局宏
	 */
	public GlobalMacroBiz getmGlobalMacroBiz() {
		if (mGlobalMacroBiz==null) {
			mGlobalMacroBiz=new GlobalMacroBiz();
		}
		return mGlobalMacroBiz;
	}

	/**
	 * 曲线
	 */
	public HistoryTrendsBiz getmHistoryTrendsBiz() {
		if(mHistoryTrendsBiz==null){
			mHistoryTrendsBiz=new HistoryTrendsBiz();
		}
		return mHistoryTrendsBiz;
	}

	/**
	 * 图片显示器
	 */
	public ImageViewerBiz getmImageViewerBiz() {
		if (mImageViewerBiz==null) {
			mImageViewerBiz=new ImageViewerBiz();
		}
		return mImageViewerBiz;
	}

	/**
	 * 键盘
	 */
	public KeyBroadBiz getmKeyBroadBiz() {
		if (mKeyBroadBiz==null) {
			mKeyBroadBiz=new KeyBroadBiz();
		}
		return mKeyBroadBiz;
	}

	/**
	 * 线
	 */
	public LineInfoBiz getmLineInfoBiz() {
		if (mLineInfoBiz==null) {
			mLineInfoBiz=new LineInfoBiz();
		}
		return mLineInfoBiz;
	}

	/**
	 * 场景
	 */
	public SceneBiz getmSceneBiz() {
		if (mSceneBiz==null) {
			mSceneBiz=new SceneBiz();
		}
		return mSceneBiz;
	}

	/**
	 * 场景宏
	 */
	public SceneMacroBiz getmSceneMacroBiz() {
		if(mSceneMacroBiz==null){
			mSceneMacroBiz=new SceneMacroBiz();
		}
		return mSceneMacroBiz;
	}
	/**
	 * 场景位置信息
	 */
	public ScenePosInfoBiz getScenePosInfoBiz(){
		if (mScenePosInfoBiz == null) {
			mScenePosInfoBiz = new ScenePosInfoBiz();
		}
		return mScenePosInfoBiz;
	}

	/**
	 * 多边型
	 */
	public ShapInfoBiz getmShapInfoBiz() {
		if (mShapInfoBiz==null) {
			mShapInfoBiz=new ShapInfoBiz();
		}
		return mShapInfoBiz;
	}

	/**
	 * 进度条
	 */
	public SlideBiz getmSlideBiz() {
		if (mSlideBiz==null) {
			mSlideBiz=new SlideBiz();
		}
		return mSlideBiz;
	}

	/**
	 * 静态文本
	 */
	public StaticTextBiz getmStaticTextBiz() {
		if(mStaticTextBiz==null){
			mStaticTextBiz=new StaticTextBiz();
		}
		return mStaticTextBiz;
	}

	/**
	 * 时间显示器
	 */
	public TimeShowBiz getmTimeShowBiz() {
		if (mTimeShowBiz==null) {
			mTimeShowBiz=new TimeShowBiz();
		}
		return mTimeShowBiz;
	}
	
	/**
	 * 数值显示器
	 */
	public NumberInputBiz getmNumberInputBiz() {
		if(mNumberInputBiz==null){
			mNumberInputBiz=new NumberInputBiz();
		}
		return mNumberInputBiz;
	}

	/**
	 * acill显示器
	 */
	public AcillInputBiz getmAcillInputBiz() {
		if (mAcillInputBiz==null) {
			mAcillInputBiz=new AcillInputBiz();
		}
		return mAcillInputBiz;
	}

	/**
	 * 配方选择器
	 */
	public RecipeSelectBiz getmRecipeSelectBiz() {
		if (mRecipeSelectBiz==null) {
			mRecipeSelectBiz=new RecipeSelectBiz();
		}
		return mRecipeSelectBiz;
	}
	
	/**
	 * 窗口查询类
	 */
	public WindowBiz getmWindowBiz() {
		if(mWindowBiz==null){
			mWindowBiz=new WindowBiz();
		}
		return mWindowBiz;
	}
	/**
	 * 组合图形查询类
	 * @return
	 */
	public GroupShapeBiz getGroupShape()
	{
		if(null == groupShapeBiz)
		{
			groupShapeBiz = new GroupShapeBiz();
		}
		return groupShapeBiz;
	}
	/**
	 * 留言板查询类
	 * @return
	 */
	public MessageBoardBiz getMessageBoard(){
		if(null == messageBoardBiz)
		{
			messageBoardBiz = new MessageBoardBiz();
		}
		return messageBoardBiz;
	}
	
	/**
	 * 时间同步
	 */
	public TimeSettingBiz getmTimeSettingBiz() {
		if (null==mTimeSettingBiz) {
			mTimeSettingBiz=new TimeSettingBiz();
		}
		return mTimeSettingBiz;
	}
	
	/**
	 * 系统参数
	 */
	public SystemInfoBiz getmSystemInfoBiz() {
		if (null==mSystemInfoBiz) {
			mSystemInfoBiz=new SystemInfoBiz();
		}
		return mSystemInfoBiz;
	}
	
	/**
	 * 表格
	 */
	public TableBiz getmTableBiz() {
		if (null==mTableBiz) {
			mTableBiz=new TableBiz();
		}
		return mTableBiz;
	}

	/**
	 * 流动块
	 */
	public FlowBlockBiz getmFlowBlockBiz() {
		if (null==mFlowBlockBiz) {
			mFlowBlockBiz=new FlowBlockBiz();
		}
		return mFlowBlockBiz;
	}
	
	/**
	 * 下拉框
	 */
	public DragdownBoxBiz getmDragdownBoxBiz(){
		if(null==mDragdownBoxBiz){
			mDragdownBoxBiz=new DragdownBoxBiz();
		}
		return mDragdownBoxBiz;
	}

	/**
	 * 表达式
	 * @return
	 */
	public ExpressBiz getmExpressBiz(){
		if(null == mExpressBiz){
			mExpressBiz = new ExpressBiz();
		}
		return mExpressBiz;
	}
	/**
	 * 用户管理
	 */
	public UserInfoBiz getmUserInfoBiz(){
		if(null==mUserInfoBiz){
			mUserInfoBiz=new UserInfoBiz();
		}
		return mUserInfoBiz;
	}
	
	/**
	 * XY曲线
	 */
	public XYCurveBiz getmXYCurveBiz() {
		if (null==mXYCurveBiz) {
			mXYCurveBiz=new XYCurveBiz();
		}
		return mXYCurveBiz;
	}
}

