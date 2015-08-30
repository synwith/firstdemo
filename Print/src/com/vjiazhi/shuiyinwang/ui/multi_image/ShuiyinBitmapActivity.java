package com.vjiazhi.shuiyinwang.ui.multi_image;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import u.upd.l;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import com.umeng.analytics.MobclickAgent;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.ListImageDirPopupWindow.OnImageDirSelected;
import com.vjiazhi.shuiyinwang.ui.multi_image.adapter.MyShuiyinAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.GetImageFolderSingleInstance;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.ImageFloder;
import com.vjiazhi.shuiyinwang.utils.L;

import com.moutian.yinta.R;


public class ShuiyinBitmapActivity extends Activity implements
		OnImageDirSelected,

		MultiImagePreviewActivity.MyAdapterHasChangedInterface {
	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;
	/**
	 * 所有的图片
	 */
	private List<File> mImgs = new ArrayList<File>();

	private GridView mGirdView;
	private MyShuiyinAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private ArrayList<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();
	// private TextView mImageCount;
	private static final int RESULT_CAMERA_IMAGE = 101;
	private Button mBackShuiyinButton;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shuiyin_main_activity);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		MultiImagePreviewActivity.setAdpaterInterface(this);
		initView();
		getImages();

	}

	private void initListDirPopupWindw() {
		// 把”所有图片”添加进去
		ImageFloder imageFloder = null;
		imageFloder = new ImageFloder();
		imageFloder.setName("所有图片");
		imageFloder.setCount(mImgs.size());
		if (mImgs.size() > 0)
			imageFloder.setFirstImagePath(mImgs.get(0).getAbsolutePath());

		mImageFloders.add(0, imageFloder);
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
		L.l("============is===null?===="
				+ GetImageFolderSingleInstance.Instance(
						ShuiyinBitmapActivity.this).getImageFolderArrayList());
		mImageFloders.addAll(GetImageFolderSingleInstance.Instance(
				ShuiyinBitmapActivity.this).getImageFolderArrayList());
		mImgs.addAll(GetImageFolderSingleInstance.Instance(
				ShuiyinBitmapActivity.this).getImgListFile());
		mAdapter = new MyShuiyinAdapter(getApplicationContext(), mImgs,
				R.layout.grid_shuiyin_item, "");
		mGirdView.setAdapter(mAdapter);
		initListDirPopupWindw();
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

		mBackShuiyinButton = (Button) findViewById(R.id.back_shuiyin);
		mBackShuiyinButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int i, long l) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("Shuiyin_Path", mImgs.get(i).getAbsolutePath());
				intent.putExtra("All_Shuiyin", bundle);
				setResult(ImgMainActivity.LOAD_FROM_GALLERY, intent);
				finish();
			}
		});

	}

	@Override
	public void selected(ImageFloder floder) {

		if (floder.getName().equalsIgnoreCase("所有图片")) {
			collectionsSort(mImgs);
			mAdapter = new MyShuiyinAdapter(getApplicationContext(), mImgs,
					R.layout.grid_shuiyin_item, "");
		} else {
			List<File> mImgs = getArrayListFromDir(floder.getDir());
			collectionsSort(mImgs);
			/**
			 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
			 */
			mAdapter = new MyShuiyinAdapter(getApplicationContext(), mImgs,
					R.layout.grid_shuiyin_item, mImgDir.getAbsolutePath());
		}

		mGirdView.setAdapter(mAdapter);
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
	public void hasChaged(int code) {
		mAdapter.notifyDataSetChanged();
	}

}
