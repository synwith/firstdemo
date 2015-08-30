package com.vjiazhi.shuiyinwang.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.adapter.GridViewAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyShuiyinAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.SharePicturesAdapter;
import com.moutian.shuiyinwang.R;



public class SharePicturesActivity extends Activity  {
	private List<File> mImgs = new ArrayList<File>();
	private GridView mGirdView;
	private Button mSharePicturesButton, mBackButton;
	private RelativeLayout relativeLayout;
	private SharePicturesAdapter mAdapter;
	private static final int RESULT_CAMERA_IMAGE = 101;
	private ArrayList<String> img_list = new ArrayList<String>();

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			data3View();
		}
	};

	private void data3View() {
		List<File> tempList = getArrayListFromDir(ImgMainActivity.mMultiImgsSavePath);
		mImgs.addAll(tempList);
		collectionsSort(mImgs);
		mAdapter = new SharePicturesAdapter(getApplicationContext(), mImgs,
				R.layout.grid_share_item,img_list);
		mGirdView.setAdapter(mAdapter);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shuiyin_imgs_activity);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getBundleExtra("IMGS");
			if (bundle != null) {
				img_list = bundle.getStringArrayList("IMGLIST");
			}
		}

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
		relativeLayout=(RelativeLayout)findViewById(R.id.imgs_bottom_ly);
		relativeLayout.setVisibility(View.GONE);
		
		
		mGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int i, long l) {
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
				int count = img_list != null ? img_list.size() : -1;
				if (count == -1) {
					Toast.makeText(SharePicturesActivity.this, "请先生成水印图片！",
							Toast.LENGTH_SHORT).show();
				} else if (count == 0) {
					Toast.makeText(SharePicturesActivity.this, "请先选择需要分享的图片！",
							Toast.LENGTH_SHORT).show();
				} else if (count == 1) {
					// showShareDialog();
					sharePicsTest(img_list);
				} else {
					sharePicsTest(img_list);
				}
			}
		});
	}

	// /这段代码能够实现不需要微信SDK分享到微信好友
	private void sharePicsTest(List<String> mSelectedImage) {
		ArrayList<Uri> imageUris = new ArrayList<Uri>();
		for (int i = 0; i < mSelectedImage.size(); i++) {
			imageUris.add(Uri.fromFile(new File(mSelectedImage.get(i))));
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
}
