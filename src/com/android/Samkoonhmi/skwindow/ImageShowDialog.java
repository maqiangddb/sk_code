package com.android.Samkoonhmi.skwindow;

import com.android.Samkoonhmi.R;
import com.android.Samkoonhmi.SKScene;
import com.android.Samkoonhmi.model.ScenceInfo;
import com.android.Samkoonhmi.model.WindowInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

/**
 * 图片显示器，放大缩小
 */
public class ImageShowDialog extends Dialog {

	public boolean isShow;
	private Button mBtnCancel;
	private ImageView mImageView;
	private Window window = null;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	private float oldDist;
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private PointF start = new PointF();
	private PointF mid = new PointF();

	public ImageShowDialog(Context context) {
		super(context, R.style.custom_dialog_style);
		isShow = false;
	}

	public void onCreate() {
		isShow=true;
		setContentView(R.layout.image_show);
		mBtnCancel = (Button) findViewById(R.id.btn_close);
		mBtnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isShow = false;
				dismiss();
			}
		});

		mImageView = (ImageView) findViewById(R.id.show_image);
		mImageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ImageView view = (ImageView) v;
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - start.x,
								event.getY() - start.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}

				view.setImageMatrix(matrix);
				return true;
			}

			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});
	}

	public void showDialog(Bitmap bitmap) {
		isShow = true;
		if (mImageView != null) {
			mImageView.setImageBitmap(bitmap);
		}
		window = this.getWindow();
		window.setGravity(Gravity.CENTER);
		window.setWindowAnimations(R.style.PopupAnimation);
		WindowManager.LayoutParams lp = window.getAttributes();
		ScenceInfo info = SKSceneManage.getInstance().getSceneInfo();
		if (info != null) {
			lp.width = info.getnSceneWidth();
			lp.height = info.getnSceneHeight();
			int x=(lp.width-bitmap.getWidth())/2;
			int y=(lp.height-bitmap.getHeight())/2;
			if (x<0) {
				x=0;
			}
			if (y<0) {
				y=0;
			}
			matrix.setTranslate(x, y);
			mImageView.setImageMatrix(matrix);
		}
		window.setAttributes(lp);
		show();
	}

}
