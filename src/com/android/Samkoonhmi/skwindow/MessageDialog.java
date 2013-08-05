package com.android.Samkoonhmi.skwindow;

import java.util.List;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.databaseinterface.DBTool;
import com.android.Samkoonhmi.databaseinterface.MessageBoardBiz;
import com.android.Samkoonhmi.model.MessageDetailInfo;
import com.android.Samkoonhmi.model.RowCell;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * 留言板dialog
 * 
 * @author 瞿丽平
 * 
 */
public class MessageDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Window window = null;
	// private Activity activity;
	private int nWidth;
	private int nHeigth;
	public boolean show;
	private RadioButton addButton;
	private RadioButton editButton;
	private RadioButton deleteButton;
	private RadioButton seeButton;
	private Button okButton;
	private Button cancelButton;
	private DateTimeSetting timeSetting;
	private DeleteDialog deleteDialog;
	// private MessageDetailInfo detailInfo;
	private int messageBoardId;
	private RowCell rowCell;
	private LinearLayout layout;
	private int messageCount;// 留言记录

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public RowCell getRowCell() {
		return rowCell;
	}

	public void setRowCell(RowCell rowCell) {
		this.rowCell = rowCell;
	}

	public int getMessageBoardId() {
		return messageBoardId;
	}

	public void setMessageBoardId(int messageBoardId) {
		this.messageBoardId = messageBoardId;
	}

	// public MessageDetailInfo getDetailInfo() {
	// return detailInfo;
	// }
	//
	// public void setDetailInfo(MessageDetailInfo detailInfo) {
	// this.detailInfo = detailInfo;
	// }

	public interface IMessageRefresh {

		void onRefresh(MessageDetailInfo detailInfo, int i);
	};

	public IMessageRefresh ImessageRefresh;

	public void setImessageRefresh(IMessageRefresh imessageRefresh) {
		ImessageRefresh = imessageRefresh;
	}

	public MessageDialog(Context context) {
		// super(activity, R.style.custom_dialog_style);
		// TODO Auto-generated constructor stub
		super(context, R.style.custom_dialog_style);
		show = false;
		nWidth = 800;
		nHeigth = 480;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.messageboard);
		getViewMeasure();
		layout = (LinearLayout) findViewById(R.id.messageview);
		LayoutParams params = new LayoutParams(nWidth, nHeigth);
		layout.setLayoutParams(params);
		addButton = (RadioButton) findViewById(R.id.add);
		editButton = (RadioButton) findViewById(R.id.edit);
		deleteButton = (RadioButton) findViewById(R.id.delete);
		seeButton = (RadioButton) findViewById(R.id.see);
		okButton = (Button) findViewById(R.id.message_ok);
		cancelButton = (Button) findViewById(R.id.message_cancel);
		okButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		// 如果没有选中对象，则让编辑，删除，查看单选按钮置灰
		if (null == getRowCell()) {
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			seeButton.setEnabled(false);
		}

	}

	private void getViewMeasure() {
		int width = SKSceneManage.nSceneWidth;
		int height = SKSceneManage.nSceneHeight;

		if (width < 600) {
			nWidth = 250;
		} else if (width * 3 / 4 >= 600) {
			nWidth = 250;
		} else {
			nWidth = 250;
		}

		if (height < 360) {
			nHeigth = 250;
		} else if (height * 3 / 4 >= 360) {
			nHeigth = 250;
		} else {
			nHeigth = 250;
		}
	}

	public void showDialog() {
		if (show) {
			return;
		}
		show = true;
		window = getWindow();
		window.setWindowAnimations(R.style.PopupAnimation);
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setAttributes(lp);
		show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		show = false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time=0;
		if (v == cancelButton) {
			dismiss();
		} else if (v == okButton) {

			if (addButton.isChecked()) {
				if (getMessageCount() > 99) {
					SKToast.makeText(SKSceneManage.getInstance().mContext,
							R.string.message_more, Toast.LENGTH_SHORT).show();
					return;
				} else {
					dismiss();
					if (timeSetting == null || !timeSetting.showFlag) {
						Activity activity = SKSceneManage.getInstance()
								.getActivity();
						if (activity == null) {
							return;
						}
						timeSetting = new DateTimeSetting(activity);
						timeSetting.setMessageBoardId(getMessageBoardId());
						timeSetting.onCreate(DateTimeSetting.TYPE.ADD_MESSAGE,
								nWidth, nHeigth);
						timeSetting.showDialog(
								DateTimeSetting.TYPE.ADD_MESSAGE, 0, 0, nWidth,
								nHeigth);
						timeSetting.setiMessageAddCallBack(imessageAddCallBack);
					}
				}
			} else if (seeButton.isChecked()) {
				if (getRowCell() == null) {
					SKToast.makeText(SKSceneManage.getInstance().mContext,R.string.nullsee, Toast.LENGTH_SHORT).show();
					return;
				}
				dismiss();
				if (timeSetting == null || !timeSetting.showFlag) {
					Activity activity = SKSceneManage.getInstance()
							.getActivity();
					if (activity == null) {
						return;
					}
					timeSetting = new DateTimeSetting(activity);
					MessageDetailInfo info = new MessageDetailInfo();
					info.setnId(getRowCell().aid);
					info.setnItemId(getMessageBoardId());
					// 取最后一列 为信息值
					info.setsMessage(getRowCell().mClounm
							.get(getRowCell().nClounmCount - 1));
					timeSetting.setMessageDetailInfo(info);
					timeSetting.onCreate(DateTimeSetting.TYPE.SEE_MESSAGE,
							nWidth, nHeigth);
					timeSetting.showDialog(DateTimeSetting.TYPE.SEE_MESSAGE, 0,
							0, nWidth, nHeigth);
				}
			} else if (editButton.isChecked()) {
				if (getRowCell() == null) {
					SKToast.makeText(SKSceneManage.getInstance().mContext,R.string.selectnotnull, Toast.LENGTH_SHORT).show();
					return;
				}
				dismiss();
				if (timeSetting == null || !timeSetting.showFlag) {
					Activity activity = SKSceneManage.getInstance()
							.getActivity();
					if (activity == null) {
						return;
					}
					timeSetting = new DateTimeSetting(activity);
					MessageDetailInfo info = new MessageDetailInfo();
					info.setnId(getRowCell().aid);
					info.setnItemId(getMessageBoardId());
					// 取最后一列 为信息值
					info.setsMessage(getRowCell().mClounm
							.get(getRowCell().nClounmCount - 1));
					timeSetting.setMessageDetailInfo(info);
					timeSetting.onCreate(DateTimeSetting.TYPE.EDIT_MESSAGE,
							nWidth, nHeigth);
					timeSetting.showDialog(DateTimeSetting.TYPE.EDIT_MESSAGE,
							0, 0, nWidth, nHeigth);

					timeSetting.setiMessageEditCallBack(imessageEditCallBack);
				}
			} else if (deleteButton.isChecked()) {
				if (getRowCell() == null) {
					SKToast.makeText(SKSceneManage.getInstance().mContext,R.string.selectnotnull, Toast.LENGTH_SHORT).show();
					return;
				}
				dismiss();
				if (null == deleteDialog || !deleteDialog.showFlag) {
					deleteDialog = new DeleteDialog(SKSceneManage.getInstance()
							.getActivity());
					deleteDialog
							.showPopWindow(R.string.suredelete, getRowCell().aid);
					deleteDialog.setDeleteListener(deleteListener);
				}
			}

		}

	}

	/**
	 * 删除成功通知界面刷新
	 */
	DeleteDialog.IDeleteListener deleteListener = new DeleteDialog.IDeleteListener() {

		@Override
		public void onDelete(int id) {
			boolean deleteBoolean = DBTool.getInstance().getMessageBoard()
					.deleteMessage(id);
			if (deleteBoolean) {
				SKToast.makeText(SKSceneManage.getInstance().mContext,
						R.string.delete_message_success, Toast.LENGTH_SHORT)
						.show();
				ImessageRefresh.onRefresh(null, 3);

			} else {
				SKToast.makeText(SKSceneManage.getInstance().mContext,
						R.string.delete_message_faild, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 添加成功，通知刷新界面
	 */
	DateTimeSetting.IMessageAddCallBack imessageAddCallBack = new DateTimeSetting.IMessageAddCallBack() {

		@Override
		public void addNotice(MessageDetailInfo info, int i) {
			// TODO Auto-generated method stub
			ImessageRefresh.onRefresh(info, i);
		}
	};
	/**
	 * 修改成功，通知刷新界面
	 */
	DateTimeSetting.IMessageEditCallBack imessageEditCallBack = new DateTimeSetting.IMessageEditCallBack() {

		@Override
		public void eidtNotice(MessageDetailInfo info, int i) {
			// TODO Auto-generated method stub
			ImessageRefresh.onRefresh(info, i);

		}
	};
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.dispatchKeyEvent(event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		SKSceneManage.getInstance().time = 0;
		return super.onKeyUp(keyCode, event);
	}
}
