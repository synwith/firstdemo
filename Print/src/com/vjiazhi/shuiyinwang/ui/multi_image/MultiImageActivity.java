package com.vjiazhi.shuiyinwang.ui.multi_image;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.ListImageDirPopupWindow.OnImageDirSelected;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyAdapter.AdapterItemListener;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.GetImageFolderSingleInstance;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.ImageFloder;
import com.vjiazhi.shuiyinwang.utils.L;

import com.moutian.yinta.R;


public class MultiImageActivity extends Activity implements OnImageDirSelected,
		AdapterItemListener,
		MultiImagePreviewActivity.MyAdapterHasChangedInterface {
	private List<File> mImgs = new ArrayList<File>();
	private GridView mGirdView;
	private MyAdapter mAdapter;
	private ArrayList<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	private RelativeLayout mBottomLy;
	private TextView btnChooseDir;
	private RelativeLayout all_pics_layout;
	private Button btnReviewImages;
	private Button btnComfirm, mBackButton;
	private static final int RESULT_CAMERA_IMAGE = 101;
	private String m_strCameraImgPathName;
	private int mScreenHeight;
	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		}
	};

	private void data3View() {
		mImgs.addAll(GetImageFolderSingleInstance.Instance(
				MultiImageActivity.this).getImgListFile());
		File file1 = new File("capture");
		mImgs.add(0, file1);
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item);
		mAdapter.setAdapterItemListener(this);
		mGirdView.setAdapter(mAdapter);
	}

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {

		// 把”所有图片”添加进去
		ImageFloder imageFloder = null;
		imageFloder = new ImageFloder();
		imageFloder.setName("所有图片");
		imageFloder.setCount(mImgs.size());
		if (mImgs.size() > 0)
			imageFloder.setFirstImagePath(mImgs.get(0).getAbsolutePath());

		mImageFloders.add(0, imageFloder);

		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multi_img_main);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		MultiImagePreviewActivity.setAdpaterInterface(this);

		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getBundleExtra("IMG");
			if (bundle != null) {
				MyAdapter.mSelectedImage = bundle.getStringArrayList("IMGLIST");
			}
		}

		initView();
		getImages();
		initEvent();

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onResume(this);
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		mImageFloders.addAll(GetImageFolderSingleInstance.Instance(
				MultiImageActivity.this).getImageFolderArrayList());
		data3View();
		initListDirPopupWindw();
		// mHandler.sendEmptyMessage(0);
	}

	public List<File> getArrayListFromDir(String dir) {
		File mImgDir = new File(dir);
		return Arrays.asList(mImgDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		btnComfirm = (Button) findViewById(R.id.picture_sure);
		mBackButton = (Button) findViewById(R.id.back_multimg);
		mBackButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnComfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ImgMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("AllSelected",
						MyAdapter.mSelectedImage);
				intent.putExtra("All", bundle);
				startActivity(intent);
			}
		});

		mGirdView = (GridView) findViewById(R.id.id_gridView);
		all_pics_layout = (RelativeLayout) findViewById(R.id.all_pics_layout);
		btnChooseDir = (TextView) findViewById(R.id.btn_choose_dir);
		btnReviewImages = (Button) findViewById(R.id.btn_review_images);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

		btnReviewImages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						MultiImagePreviewActivity.class);
				startActivity(intent);
			}
		});
		if (MyAdapter.mSelectedImage.size() > 0) {
			btnComfirm.setText("完成(" + MyAdapter.mSelectedImage.size() + ")");
			btnReviewImages.setEnabled(true);
		}

	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		all_pics_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mListImageDirPopupWindow != null) {
					mListImageDirPopupWindow
							.setAnimationStyle(R.style.anim_popup_dir);
					mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

					// 设置背景颜色变暗
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = .3f;
					getWindow().setAttributes(lp);
				}
			}
		});
	}

	@Override
	public void selected(ImageFloder floder) {

		if (floder.getName().equalsIgnoreCase("所有图片")) {
			collectionsSort(mImgs);
			mAdapter = new MyAdapter(getApplicationContext(), mImgs,
					R.layout.grid_item);
		} else {
			List<File> mImgs = getArrayListFromDir(floder.getDir());
			collectionsSort(mImgs);
			/**
			 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
			 */
			mAdapter = new MyAdapter(getApplicationContext(), mImgs,
					R.layout.grid_item);
		}

		mGirdView.setAdapter(mAdapter);
		btnChooseDir.setText(floder.getName());
		mAdapter.setAdapterItemListener(this);
		mListImageDirPopupWindow.dismiss();

	}

	public void collectionsSort(List<File> mImgs) {
		Collections.sort(mImgs, new Comparator<File>() {
			public int compare(File file, File newFile) {
				if (file.lastModified() < newFile.lastModified()) {
					return 1;
				} else if (file.lastModified() == newFile.lastModified()) {
					return 0;
				} else {
					return -1;
				}

			}
		});
	}

	@Override
	public void onAdapterItemClick(int mSelectedImageNum) {
		// TODO Auto-generated method stub
		if (mSelectedImageNum > 0) {
			btnComfirm.setText("完成(" + mSelectedImageNum + ")");
			btnReviewImages.setEnabled(true);
		} else {
			btnComfirm.setText("完成");
			btnReviewImages.setEnabled(false);
		}
	}

	private void goToCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		m_strCameraImgPathName = ImgMainActivity.mImgSavePath + File.separator
				+ System.currentTimeMillis() + "_img.jpg";

		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(m_strCameraImgPathName)));
		startActivityForResult(intent, RESULT_CAMERA_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_CAMERA_IMAGE) {
			if (resultCode == RESULT_OK) {
				GetImageFolderSingleInstance.Instance(MultiImageActivity.this)
						.flashInit();
				MyAdapter.mSelectedImage.add(m_strCameraImgPathName);
				Intent intent = new Intent(getApplicationContext(),
						ImgMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("AllSelected",
						MyAdapter.mSelectedImage);
				intent.putExtra("All", bundle);
				startActivity(intent);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCameraEnter() {
		goToCamera();
	}

	@Override
	protected void onDestroy() {
		MyAdapter.mSelectedImage.clear();
		super.onDestroy();
	}

	@Override
	public void hasChaged(int code) {
		mAdapter.notifyDataSetChanged();
		onAdapterItemClick(MyAdapter.mSelectedImage.size());
	}

}
