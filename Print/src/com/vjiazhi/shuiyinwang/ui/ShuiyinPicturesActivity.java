package com.vjiazhi.shuiyinwang.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.umeng.analytics.MobclickAgent;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.MultiImageActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.ShuiyinPicturesAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.GetImageFolderSingleInstance;
import com.vjiazhi.shuiyinwang.utils.L;
import com.moutian.shuiyinwang.R;



public class ShuiyinPicturesActivity extends Activity implements
		ShuiyinPicturesAdapter.SelectInterface {
	private List<File> mImgs = new ArrayList<File>();
	private GridView mGirdView;
	private Button mSharePicturesButton, mBackButton;
	private TextView mAllImgButton, mDeleteButton, mYulanButton;
	private ShuiyinPicturesAdapter mAdapter;
	private static final int RESULT_CAMERA_IMAGE = 101;
	private ProgressDialog pdialog;
	private List<File> selected_list = new ArrayList<File>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				data3View();
			} else if (msg.what == 1) {
				mAdapter.setData(mImgs);
				mAdapter.clearSelectedList();
				mAdapter.notifyDataSetChanged();
				pdialog.dismiss();
				GetImageFolderSingleInstance.Instance(
						ShuiyinPicturesActivity.this).flashInit();
			}
		}
	};

	private void data3View() {
		List<File> tempList = getArrayListFromDir(ImgMainActivity.mMultiImgsSavePath);
		if (tempList == null) {
			tempList = new ArrayList<File>();
		}
		mImgs.addAll(tempList);
		collectionsSort(mImgs);
		mAdapter = new ShuiyinPicturesAdapter(getApplicationContext(), mImgs,
				R.layout.grid_shuiyinxiangce_item);
		mAdapter.setSelectInterface(this);
		mGirdView.setAdapter(mAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shuiyin_imgs_activity);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		Intent intent = getIntent();
		initView();
		getImages();

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (pdialog != null && pdialog.isShowing()) {
			pdialog.dismiss();
		}
		MobclickAgent.onResume(this);
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储。", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		// mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		mHandler.sendEmptyMessage(0);
	}

	public File[] getFilesFromDir(String dir) {
		File fl = new File(dir);
		File[] files = fl.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg")) {
					return true;
				} else {
					return false;
				}
			}
		});
		return files;
	}

	public List<File> getArrayListFromDir(String dir) {
		File mImgDir = new File(dir);
		if (mImgDir.exists() && mImgDir.listFiles().length > 0) {
			return Arrays.asList(mImgDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					if (filename.endsWith(".jpg") || filename.endsWith(".png")
							|| filename.endsWith(".jpeg"))
						return true;
					return false;
				}
			}));
		} else {
			return null;
		}
	}

	public String lastFileModified(File[] files) {
		long lastMod = Long.MIN_VALUE;
		File choise = null;
		if (files == null || files.length == 0) {
			return null;
		}
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				choise = file;
				lastMod = file.lastModified();
			}
		}
		return choise.getAbsolutePath();
	}

	/**
	 * 初始化View
	 */
	private void initView() {

		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mSharePicturesButton = (Button) findViewById(R.id.share_imgs);
		mBackButton = (Button) findViewById(R.id.back_imgs);
		mDeleteButton = (TextView) findViewById(R.id.img_delete);
		mAllImgButton = (TextView) findViewById(R.id.img_all);
		mYulanButton = (TextView) findViewById(R.id.img_yulan);

		mGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int i, long l) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putString("Shuiyin_Path",
				// mImgs.get(i).getAbsolutePath());
				// intent.putExtra("All_Shuiyin", bundle);
				// setResult(ImgMainActivity.LOAD_FROM_GALLERY, intent);
				// finish();

			}
		});

		mYulanButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (selected_list.size() > 0) {
					Intent intent = new Intent(ShuiyinPicturesActivity.this,
							ShuiyinPicturesPreviewActivity.class);
					Bundle bundle = new Bundle();

					bundle.putStringArrayList("SELECT_LIST",
							getArrayListFromList(selected_list));
					intent.putExtra("VALUE", bundle);
					startActivity(intent);
				} else {
					Toast.makeText(ShuiyinPicturesActivity.this, "请先选择图片!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mAllImgButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (selected_list.size() == mImgs.size()) {
					mAllImgButton.setText("全选");
					mAdapter.setSelectedImgsList(new ArrayList<File>());
					mAdapter.notifyDataSetInvalidated();
				} else {
					mAdapter.setSelectedImgsList(mImgs);
					selected_list.clear();
					selected_list.addAll(mImgs);
					mAdapter.notifyDataSetInvalidated();
					mAllImgButton.setText("全选(" + mImgs.size() + ")");
				}
			}
		});

		mDeleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selected_list = mAdapter.getSelectImgsList();
				if (selected_list.size() > 0) {
					MyImgsProcessTask myTask = new MyImgsProcessTask(
							ShuiyinPicturesActivity.this);
					myTask.execute(selected_list);
				} else {
					Toast.makeText(ShuiyinPicturesActivity.this, "请先选择图片!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mSharePicturesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 分享图片到微信朋友圈

				int count = selected_list != null ? selected_list.size() : -1;
				if (count == -1) {
					Toast.makeText(ShuiyinPicturesActivity.this, "请先生成水印图片！",
							Toast.LENGTH_SHORT).show();
				} else if (count == 0) {
					Toast.makeText(ShuiyinPicturesActivity.this,
							"请先选择需要分享的图片！", Toast.LENGTH_SHORT).show();
				} else if (count == 1) {
					// showShareDialog();
					sharePicsTest(selected_list);
				} else {
					sharePicsTest(selected_list);
				}
			}
		});
	}

	private ArrayList<String> getArrayListFromList(List<File> selected_list2) {
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < selected_list2.size(); i++) {
			temp.add(selected_list2.get(i).getAbsolutePath());
		}
		return temp;
	}

	class MyImgsProcessTask extends AsyncTask<List, Integer, Boolean> {

		Bitmap loadedImg;
		Bitmap savedImg;
		Context mContext;

		public MyImgsProcessTask(Context mcontext) {
			this.mContext = mcontext;
			pdialog = null;
			pdialog = new ProgressDialog(mcontext);
			pdialog.setTitle(mcontext.getString(R.string.str_processing));
			pdialog.setProgress(0);
			pdialog.setMax(selected_list.size());
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pdialog != null) {
				if (!pdialog.isShowing())
					pdialog.show();
			}

		}

		@Override
		protected Boolean doInBackground(List... mlist) {
			final int sum = mlist[0].size();
			if (sum > 0) {
				deleteImgsByThread(mlist[0]);
			}
			return null;
		}

		public void deleteImgsByThread(List<File> mList) {
			for (int i = 0; i < mList.size(); i++) {
				File file = mList.get(i);
				if (file.exists()) {
					file.delete();
				}
				mImgs.remove(mList.get(i));
				publishProgress((int) (i * 100.0 / mList.size()));
			}
			mHandler.sendEmptyMessage(1);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pdialog.dismiss();
			mAllImgButton.setText("全选");
		}
	}

	// /这段代码能够实现不需要微信SDK分享到微信好友
	private void sharePicsTest(List<File> mSelectedImage) {
		ArrayList<Uri> imageUris = new ArrayList<Uri>();
		for (int i = 0; i < mSelectedImage.size(); i++) {
			imageUris.add(Uri.fromFile(mSelectedImage.get(i)));
		}
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
		shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
		shareIntent.setType("image/*");
		startActivity(Intent.createChooser(shareIntent, "分享图片"));
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_CAMERA_IMAGE) {
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(getApplicationContext(),
						ImgMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				intent.putExtra("All", bundle);
				startActivity(intent);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void selectItem(List<File> dataFile) {
		L.l("------hi====" + dataFile.size() + "====img:" + mImgs.size());
		selected_list = dataFile;
		if (selected_list.size() < mImgs.size()) {
			L.l("---2---hi====");
			mAllImgButton.setText("全选");
		} else {
			mAllImgButton.setText("全选(" + mImgs.size() + ")");
		}
	}
}
