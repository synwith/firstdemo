package com.vjiazhi.shuiyinwang.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.vjiazhi.shuiyinwang.ui.adapter.HorizontalListViewAdapter;
import com.vjiazhi.shuiyinwang.ui.adapter.HorizontalYinJiBackgroundListViewAdapter;
import com.vjiazhi.shuiyinwang.ui.multi_image.ImageLoader;
import com.vjiazhi.shuiyinwang.ui.multi_image.MultiImageActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.ShuiyinBitmapActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.GetImageFolderSingleInstance;
import com.vjiazhi.shuiyinwang.ui.view.ColorPickerView;
import com.vjiazhi.shuiyinwang.ui.view.ColorPickerView.MyOnClickListener;
import com.vjiazhi.shuiyinwang.ui.view.ContentEnablePanelLayout;
import com.vjiazhi.shuiyinwang.ui.view.ContentFragment;
import com.vjiazhi.shuiyinwang.ui.view.HorizontalListView;
import com.vjiazhi.shuiyinwang.ui.view.MenuFragment;
import com.vjiazhi.shuiyinwang.ui.view.WaterMarkImageView;
import com.vjiazhi.shuiyinwang.ui.view.WaterMarkImageView.WaterMarkPosition;
import com.vjiazhi.shuiyinwang.utils.BitmapUtil;
import com.vjiazhi.shuiyinwang.utils.ImageProcessor;
import com.vjiazhi.shuiyinwang.utils.ImageUtil;
import com.vjiazhi.shuiyinwang.utils.ImgFileUtils;
import com.vjiazhi.shuiyinwang.utils.L;
import com.vjiazhi.shuiyinwang.utils.LogPrint;
import com.vjiazhi.shuiyinwang.utils.MD5Config;
import com.vjiazhi.shuiyinwang.utils.MyConfig;

import com.moutian.yinta.R;

@TargetApi(9)
public class ImgMainActivity extends Activity implements
		ContentFragment.InterfaceContentView, MenuFragment.DataInterface {

	private Button m_btnLoadImg, m_btnSaveShare;
	private ImageButton img_xiangce, img_shuiyin;
	private Context mContext;
	private WaterMarkImageView mWaterMarkImageView;
	private TextView mBackgroundLayout;
	private Bitmap mCurrentImg = null;
	private static Bitmap mFirstLoadedImg = null;
	private static Bitmap mBlurBackgroundImg = null;
	private static Bitmap mLeftBottomImg = null;
	private String m_strWaterMarkInfo = "示例水印-By印它";
	private String m_strCameraImgPathName = "";
	boolean mIsImgUpdated = false;
	private String mFileNameToSave = "";

	public final static int LOAD_FROM_GALLERY = 0; // 图库
	public final static int LOAD_FROM_CAMERA = 1; // 拍照
	public final static int LOAD_MULTI_GALLERY = 2; // 多图

	public static final int RESULT_GALLERY_IMAGE = 100;
	private static final int RESULT_CAMERA_IMAGE = 101;

	public static String mImgSavePath = Environment
			.getExternalStorageDirectory() + "/vjiazhi";

	public static String mMultiImgsSavePath = mImgSavePath + "/out";
	private ContentEnablePanelLayout slidingPaneLayout;
	private MenuFragment menuFragment;
	private ContentFragment contentFragment;
	private DisplayMetrics displayMetrics = new DisplayMetrics();
	private HorizontalListView hListview2;
	private HorizontalListViewAdapter hListViewAdapter2;

	private ArrayList<String> listfile = new ArrayList<String>();
	private ArrayList<String> listfileOut = new ArrayList<String>(); // 输出后的地址列表
	private final static int IMAGE_QUALITY_PERCENT = 50; // 压缩的是

	private FeedbackAgent mUmengFeedBack = null; // 友盟用户反馈组件
	private View contentView = null;
	private long exitTime = 0;

	private Typeface typeface;

	private static int PREVIEW_WIDTH = 800;
	private static int PREVIEW_HEIGHT = 800;

	private PopupWindow popupWindow;
	private int[] mDefaultImg = { R.drawable.b1, R.drawable.b2, R.drawable.b3,
			R.drawable.b4, R.drawable.b6, R.drawable.b7 };
	private ProgressDialog pdialog;

	private WaterMarkPosition mWaterMarkPosition = null;
	private int selectedPos = 0;

	Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			final Bitmap shuiyin_bitmap = ImageUtil.createBitmapFromText(
					MyConfig.getNewColor(ImgMainActivity.this),
					m_strWaterMarkInfo, typeface);
			if (msg.what == 1) {
				PREVIEW_WIDTH = mWaterMarkImageView.getWidth();
				PREVIEW_HEIGHT = mWaterMarkImageView.getHeight();
				mFirstLoadedImg = ThumbnailUtils.extractThumbnail(
						mFirstLoadedImg, PREVIEW_WIDTH, PREVIEW_HEIGHT);
				mWaterMarkImageView.setWaterMarkBitamp(shuiyin_bitmap);
				mWaterMarkImageView.setImageBitmap(mFirstLoadedImg);
			} else if (msg.what == 2) {
				mWaterMarkImageView.setWaterMarkBitamp(shuiyin_bitmap);
				mWaterMarkImageView.setImageBitmap(mFirstLoadedImg);

			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void exit() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出印它",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyConfig.initScreenWidthHeight(this);
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏

		}
		setContentView(R.layout.slidingpane_main_layout);
		slidingPaneLayout = (ContentEnablePanelLayout) findViewById(R.id.slidingpanellayout);
		menuFragment = new MenuFragment();
		contentFragment = new ContentFragment(this);
		menuFragment.setUmengInterface(this);
		mUmengFeedBack = new FeedbackAgent(this);
		mUmengFeedBack.sync();
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.slidingpane_menu, menuFragment);
		transaction.replace(R.id.slidingpane_content, contentFragment);
		transaction.commit();
		slidingPaneLayout.setEnabled(false);
		m_strWaterMarkInfo = MyConfig.getNewText(ImgMainActivity.this);
		// typeface = Typeface.createFromAsset(getAssets(),
		// "fonts/STXingKai.ttf");
		typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);

	}

	/**
	 * @return the slidingPaneLayout
	 */
	public SlidingPaneLayout getSlidingPaneLayout() {
		return slidingPaneLayout;
	}

	/**
	 * fengyi add
	 * 
	 * @time 2015/02/23 13:47
	 */
	private void prepareVjiazhiFolder() {
		File f = new File(mImgSavePath);
		if (!f.exists()) {
			f.mkdirs();
		} else {
			// System.out.println("VJIAZHI_PATH exists!");
			LogPrint.logd(this, "VJIAZHI_PATH exists!");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 0 && null != data) {
			if (requestCode == RESULT_GALLERY_IMAGE) {
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String mFirstBitmapPath = cursor.getString(columnIndex);
				cursor.close();

				mFirstLoadedImg = BitmapFactory.decodeFile(mFirstBitmapPath);
				// mImageView.setImageBitmap(mFirstLoadedImg);
				mWaterMarkImageView.setImageBitmap(mFirstLoadedImg);
				mWaterMarkImageView.setWaterMarkBitamp(ImageUtil
						.createBitmapFromText(m_strWaterMarkInfo, typeface));
				mIsImgUpdated = true;

			} else if (requestCode == RESULT_CAMERA_IMAGE) {
				mFirstLoadedImg = BitmapFactory
						.decodeFile(m_strCameraImgPathName);
				mCurrentImg = mFirstLoadedImg; // xinghe add
				// mImageView.setImageBitmap(mFirstLoadedImg);
				mWaterMarkImageView.setImageBitmap(mFirstLoadedImg);
				mIsImgUpdated = true;
			} else if (requestCode == LOAD_FROM_GALLERY) {
				Bitmap shuiyin_bitmap = BitmapFactory.decodeFile(data
						.getBundleExtra("All_Shuiyin")
						.getString("Shuiyin_Path"));
				shuiyin_bitmap = ThumbnailUtils.extractThumbnail(
						shuiyin_bitmap, 400, 400);

				mWaterMarkImageView.setWaterMarkBitamp(shuiyin_bitmap);

				img_shuiyin.setImageBitmap(shuiyin_bitmap);
			}
		}

	}

	public String createFileName(boolean bUpdated) {
		String fileName = mFileNameToSave;

		if (bUpdated) {
			Time t = new Time();
			t.setToNow();
			int year = t.year;
			int month = t.month + 1;
			int monthDay = t.monthDay;
			int hour = t.hour;
			int minute = t.minute;
			int second = t.second;

			String syear = String.valueOf(year);
			String smonth = String.valueOf(month);
			String smonthDay = String.valueOf(monthDay);
			String shour = String.valueOf(hour);
			String sminute = String.valueOf(minute);
			String ssecond = String.valueOf(second);
			if (month < 10)
				smonth = "0" + smonth;
			else
				;

			if (monthDay < 10)
				smonthDay = "0" + smonthDay;
			else
				;

			if (hour < 10)
				shour = "0" + shour;
			else
				;

			if (minute < 10)
				sminute = "0" + sminute;
			else
				;

			if (second < 10)
				ssecond = "0" + ssecond;
			else
				;

			fileName = "Img_" + syear + smonth + smonthDay + shour + sminute
					+ ssecond + ".jpg";

		}
		return fileName;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCurrentImg != null && !mCurrentImg.isRecycled()) {
			mCurrentImg.recycle();
			System.gc();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void startShuiyinBitmapActivity() {
		Intent intent = new Intent(ImgMainActivity.this,
				ShuiyinBitmapActivity.class);//
		startActivityForResult(intent, LOAD_FROM_GALLERY);
	}

	private void startImgsFIleListActivity() {
		Intent intent = new Intent(ImgMainActivity.this,
				MultiImageActivity.class);//
		if (listfile.size() > 0) {
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("IMGLIST", listfile);
			intent.putExtra("IMG", bundle);
		}
		startActivity(intent);
	}

	@Override
	public void hasDone() {
		mContext = this;

		if (getIntent() != null) {
			Bundle bundle = getIntent().getBundleExtra("All");
			if (bundle != null) {
				listfile = bundle.getStringArrayList("AllSelected");
			}
		}
		prepareVjiazhiFolder();
		StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		try {
			Class smClass;
			smClass = Class.forName("android.os.storage.StorageManager");
			Log.i("WiFiDirectActivity.TAG", "hexiang-test" + smClass.toString());
			Method meths[] = smClass.getMethods();
			Method getDefaultPath = null;
			for (int i = 0; i < meths.length; i++) {
				if (meths[i].getName().endsWith("getDefaultPath"))
					getDefaultPath = meths[i];
			}

			mImgSavePath = (String) getDefaultPath.invoke(sm, null);
			mImgSavePath = mImgSavePath + "/" + "vjiazhi/";

			mMultiImgsSavePath = mImgSavePath + "out";
		} catch (Exception e) {
			e.printStackTrace();
		}

		initViews();
	}

	// 设置默认的图片
	@SuppressLint("NewApi")
	public synchronized void setPreviewView(int position) {
		// 设置选择的图片的位置
		selectedPos = position;
		final String picturePath = listfile.get(position);

		try {
			mFirstLoadedImg = getFirstLoadingImg(picturePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels;
		Bitmap img = BitmapUtil.blur(ImgMainActivity.this, mFirstLoadedImg,
				width, height);
		mBackgroundLayout
				.setBackground(new BitmapDrawable(getResources(), img));
		img_xiangce.setImageBitmap(ThumbnailUtils.extractThumbnail(
				mFirstLoadedImg, 400, 400));
		mWaterMarkImageView.setImageBitmap(mFirstLoadedImg);
	}

	private static HashMap<String, SoftReference<Bitmap>> imageCache = null;

	public Bitmap getFirstLoadingImg(String path) throws IOException {

		String key = MD5Config.generatePassword(path);
		if (imageCache == null) {
			imageCache = new HashMap<String, SoftReference<Bitmap>>();
			SoftReference<Bitmap> bitmap = new SoftReference<Bitmap>(
					BitmapUtil.revitionImageSize(path));
			imageCache.put(key, bitmap);
			return bitmap.get();
		} else if (imageCache.containsKey(key)) {
			Bitmap bitmap1 = imageCache.get(key).get();
			if (bitmap1 == null) {
				bitmap1 = BitmapUtil.revitionImageSize(path);
				SoftReference<Bitmap> bitmap = new SoftReference<Bitmap>(
						bitmap1);
				imageCache.put(key, bitmap);
				return bitmap1;
			} else {
				return bitmap1;
			}

		} else {
			Bitmap bitmap1 = BitmapUtil.revitionImageSize(path);
			SoftReference<Bitmap> bitmap = new SoftReference<Bitmap>(bitmap1);
			imageCache.put(key, bitmap);
			return bitmap1;
		}

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
		if (pdialog != null && pdialog.isShowing()) {
			pdialog.dismiss();
		}

		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}

	private void initViews() {

		contentView = contentFragment.getContentView();
		m_btnLoadImg = (Button) contentView.findViewById(R.id.btn_load_img);
		img_xiangce = (ImageButton) contentView.findViewById(R.id.img_xiangce);
		img_shuiyin = (ImageButton) contentView.findViewById(R.id.img_shuiyin);
		mWaterMarkImageView = (WaterMarkImageView) contentView
				.findViewById(R.id.iv_watermark);
		mBackgroundLayout = (TextView) contentView.findViewById(R.id.idlayout);
		m_btnSaveShare = (Button) contentView.findViewById(R.id.btn_save_share);

		m_btnLoadImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				slidingPaneLayout.openPane();

			}
		});

		m_btnSaveShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (listfile.size() == 0) {
					Toast.makeText(ImgMainActivity.this, "请点击左下角添加图片",
							Toast.LENGTH_SHORT).show();
				} else {
					new MyImgsProcessTask(ImgMainActivity.this)
							.execute(listfile);
				}

			}
		});

		img_xiangce.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (listfile.size() <= 0) {
					startImgsFIleListActivity();
				} else {
					showPopupWindow(view);
				}
			}
		});

		img_xiangce.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				startImgsFIleListActivity();
				return false;
			}
		});

		img_shuiyin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				showShuiyinPopupWindow(img_xiangce);
			}
		});

		img_shuiyin.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// TODO Auto-generated method stub
				startShuiyinBitmapActivity();
				return false;
			}
		});

		initMainView();

	}

	@SuppressLint("NewApi")
	public void initMainView() {
		if (listfile.size() > 0) {
			// 如果选择的图片数大于0，则默认选择第一张图片为预览图
			String picturePath = listfile.get(0);
			selectedPos = 0;
			final Bitmap shuiyin_bitmap = ImageUtil.createBitmapFromText(
					MyConfig.getNewColor(ImgMainActivity.this),
					m_strWaterMarkInfo, typeface);
			try {
				mFirstLoadedImg = getFirstLoadingImg(picturePath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels; // 屏幕宽度（像素）
			int height = metric.heightPixels;
			mBlurBackgroundImg = BitmapUtil.blur(ImgMainActivity.this,
					mFirstLoadedImg, width, height);

			mBackgroundLayout.setBackground(new BitmapDrawable(getResources(),
					mBlurBackgroundImg));
			mLeftBottomImg = ThumbnailUtils.extractThumbnail(mFirstLoadedImg,
					400, 400);
			img_xiangce.setImageBitmap(mLeftBottomImg);
			img_shuiyin.setImageBitmap(shuiyin_bitmap);
			mHander.sendEmptyMessageDelayed(2, 100);

		} else {
			Random randomno = new Random();
			final Bitmap shuiyin_bitmap = ImageUtil.createBitmapFromText(
					MyConfig.getNewColor(ImgMainActivity.this),
					m_strWaterMarkInfo, typeface);
			mFirstLoadedImg = BitmapUtil.getBitmapFromDrawableId(
					getResources(),
					mDefaultImg[randomno.nextInt(mDefaultImg.length)]);
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels; // 屏幕宽度（像素）
			int height = metric.heightPixels;
			mFirstLoadedImg = ThumbnailUtils.extractThumbnail(mFirstLoadedImg,
					width, width);
			Bitmap img = BitmapUtil.blur(ImgMainActivity.this, mFirstLoadedImg,
					width, height);

			mBackgroundLayout.setBackground(new BitmapDrawable(getResources(),
					img));

			img_xiangce.setImageBitmap(ThumbnailUtils.extractThumbnail(
					mFirstLoadedImg, 400, 400));
			img_shuiyin.setImageBitmap(shuiyin_bitmap);
			mHander.sendEmptyMessageDelayed(1, 100);
		}

	}

	private void showShuiyinPopupWindow(View view) {

		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.popup_window_shuiyin, null);
		ColorPickerView colorPickerView = (ColorPickerView) contentView
				.findViewById(R.id.colorpicker);
		final EditText edtInput = (EditText) contentView
				.findViewById(R.id.edtInput);
		Button sureButton = (Button) contentView.findViewById(R.id.sure);
		final ArrayList<Integer> listfile = new ArrayList<Integer>();
		HorizontalListView wenzi_shuiyin_list = (HorizontalListView) contentView
				.findViewById(R.id.wenzi_bg);
		listfile.add(R.drawable.shuiyin_bg1);
		listfile.add(R.drawable.shuiyin_bg2);
		listfile.add(R.drawable.shuiyin_bg3);
		listfile.add(R.drawable.shuiyin_bg4);
		listfile.add(R.drawable.shuiyin_bg5);
		String shuiyin_wenzi = MyConfig.getNewText(ImgMainActivity.this);
		if (!shuiyin_wenzi.isEmpty()) {
			edtInput.setText(shuiyin_wenzi);
			edtInput.setSelection(shuiyin_wenzi.length());
		}
		HorizontalYinJiBackgroundListViewAdapter hListViewAdapter2 = new HorizontalYinJiBackgroundListViewAdapter(
				getApplicationContext(), listfile);
		wenzi_shuiyin_list.setAdapter(hListViewAdapter2);
		wenzi_shuiyin_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == listfile.size()) {
					return;
				}

				Bitmap tupian_shuiyin_bitmap = getPropThumnail(listfile
						.get(position));
				Bitmap shuiyinBitmap = ImageUtil.createBitmapFromBitmap(
						MyConfig.getNewColor(ImgMainActivity.this),
						m_strWaterMarkInfo, typeface, tupian_shuiyin_bitmap);
				img_shuiyin.setImageBitmap(shuiyinBitmap);
				if (mFirstLoadedImg != null) {
					mWaterMarkImageView.setWaterMarkBitamp(shuiyinBitmap);
				}

			}
		});

		colorPickerView.setOnClickListener(new MyOnClickListener() {

			@Override
			public void colorChanged(int color) {
				// TODO Auto-generated method stub
				MyConfig.setNewColor(ImgMainActivity.this, color);
				if ((!m_strWaterMarkInfo.isEmpty())) {
					Bitmap shuiyinBitmap = ImageUtil.createBitmapFromText(
							MyConfig.getNewColor(ImgMainActivity.this),
							m_strWaterMarkInfo, typeface);
					img_shuiyin.setImageBitmap(shuiyinBitmap);
					if (mFirstLoadedImg != null) {
						mWaterMarkImageView.setWaterMarkBitamp(shuiyinBitmap);
						mIsImgUpdated = true;
					}
				}
			}
		});

		sureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				m_strWaterMarkInfo = edtInput.getText().toString();
				if ((!m_strWaterMarkInfo.isEmpty())) {

					MyConfig.setNewText(ImgMainActivity.this,
							m_strWaterMarkInfo);
					Bitmap shuiyinBitmap = ImageUtil.createBitmapFromText(
							MyConfig.getNewColor(ImgMainActivity.this),
							m_strWaterMarkInfo, typeface);
					img_shuiyin.setImageBitmap(shuiyinBitmap);
					if (mFirstLoadedImg != null) {
						mWaterMarkImageView.setWaterMarkBitamp(shuiyinBitmap);
						mIsImgUpdated = true;
					}
					if (popupWindow != null) {
						if (popupWindow.isShowing()) {
							popupWindow.dismiss();
						}
					}
				} else {
					Toast.makeText(ImgMainActivity.this, "内容不能为空！",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		//
		// showPopupWindows(contentView, view, 0,
		// -(MyConfig.getScreenHeight() / 4 * 3 - 2 * MyConfig
		// .getStatusHeight()), R.drawable.shuiyin_bg);

		showPopupWindows(contentView, view, 0, -120, R.drawable.shuiyin_bg);

	}

	private Bitmap getPropThumnail(int id) {
		Drawable d = mContext.getResources().getDrawable(id);
		Bitmap b = BitmapUtil.drawableToBitmap(d);
		// Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
		int w = mContext.getResources().getDimensionPixelOffset(
				R.dimen.thumnail_default_width);
		int h = mContext.getResources().getDimensionPixelSize(
				R.dimen.thumnail_default_height);

		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);

		return thumBitmap;
	}

	private void showPopupWindow(View view) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.popup_window_xiangce, null);
		// 设置按钮的点击事件

		hListview2 = (HorizontalListView) contentView
				.findViewById(R.id.img_horizon_listview2);
		hListViewAdapter2 = new HorizontalListViewAdapter(
				getApplicationContext(), listfile);

		hListview2.setAdapter(hListViewAdapter2);
		hListview2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == listfile.size()) {
					startImgsFIleListActivity();
				} else {
					setPreviewView(position);
				}

			}
		});

		hListview2
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						listfile.remove(arg2);
						hListViewAdapter2.setImgPath(listfile);
						hListViewAdapter2.notifyDataSetChanged();
						initMainView();
						Toast.makeText(ImgMainActivity.this, "取消选择成功!",
								Toast.LENGTH_SHORT).show();
						return false;
					}
				});

		showPopupWindows(contentView, view, 0, 200, R.drawable.xiangce_bg);

	}

	@SuppressLint("NewApi")
	public void showPopupWindows(View contentView, View view, int x, int y,
			int imgId) {
		popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionevent) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(imgId));
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, y);
	}

	// =================下面是点击水印后的效果==========================

	private boolean saveImgsAndOut(Bitmap bitmap, String path, String name) {
		File savePath = new File(path);
		if (!savePath.exists()) {
			savePath.mkdirs();
		}

		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}

		String filePath = path + name;

		File fileImgToSave = new File(filePath);
		if (fileImgToSave.exists()) {
			return false;
		}

		listfileOut.add(filePath);

		FileOutputStream fOut = null;
		try {
			fileImgToSave.createNewFile();
			fOut = new FileOutputStream(fileImgToSave);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY_PERCENT, fOut);

		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			return false;
		} finally {
			bitmap.recycle();
		}

		return true;
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
			pdialog.setMax(listfile.size());
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

			// fengyi add
			// 以下代码是为了获取当前预览图的对应的WaterMarkPosition对象
			if (sum > 0) {
				String firstimgPath = (String) mlist[0].get(selectedPos);

				Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
						firstimgPath, PREVIEW_WIDTH, PREVIEW_HEIGHT);

				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				float bitmapScaleRate = ImageProcessor.calculateScaleRate(
						width, height);
				mWaterMarkPosition = mWaterMarkImageView.getWaterMarkPosition(
						bitmapScaleRate, width, height);

				// BufferedInputStream in;
				// try {
				// File file = new File(firstimgPath);
				// if (file.exists()) {
				// in = new BufferedInputStream(new FileInputStream(file));
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inJustDecodeBounds = true;
				// BitmapFactory.decodeStream(in, null, options);
				// in.close();
				// int width = options.outWidth;
				// int height = options.outHeight;
				// float bitmapScaleRate = ImageProcessor
				// .calculateScaleRate(width, height);
				// mWaterMarkPosition = mWaterMarkImageView
				// .getWaterMarkPosition(bitmapScaleRate, width,
				// height);
				// }
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

			}
			saveBitmapByThread(mlist[0]);
			return null;
		}

		public void saveBitmapByThread(List<String> mList) {
			for (int i = 0; i < mList.size(); i++) {
				try {
					// 这个地方一定要留足够的时间，否则就会崩溃。
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				String imgPath = (String) mList.get(i);
				// loadedImg = BitmapFactory.decodeFile(imgPath);
				// savedImg =
				// ImageProcessor.createFinalBitmapByBitmap(loadedImg,
				// mWaterMarkImageView.getWaterMarkBitmap(),
				// mWaterMarkPosition);
				// saveImgsAndOut(savedImg, ImgMainActivity.mMultiImgsSavePath,
				// ImgFileUtils.createFileName());
				// loadedImg.recycle();
				// savedImg.recycle();
				// loadedImg = null;
				// savedImg = null;
				System.gc(); // 提醒系统及时回收

				loadedImg = ImageLoader.decodeSampledBitmapFromResource(
						imgPath, PREVIEW_WIDTH, PREVIEW_HEIGHT);// 缩放图片显示
				savedImg = ImageProcessor.createFinalBitmapByBitmap(loadedImg,
						mWaterMarkImageView.getWaterMarkBitmap(),
						mWaterMarkPosition);

				saveImgsAndOut(savedImg, ImgMainActivity.mMultiImgsSavePath,
						ImgFileUtils.createFileName());

				if (!loadedImg.isRecycled()) {
					loadedImg.recycle(); // 回收图片所占的内存
					loadedImg = null;
					System.gc(); // 提醒系统及时回收
				}

				if (!savedImg.isRecycled()) {
					savedImg.recycle(); // 回收图片所占的内存
					savedImg = null;
					System.gc(); // 提醒系统及时回收
				}
				publishProgress((int) (i * 100.0 / mList.size()));
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			pdialog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pdialog.dismiss();
			GetImageFolderSingleInstance.Instance(ImgMainActivity.this)
					.flashInit();
			startSharePicturesActivity();
		}
	}

	private void startSharePicturesActivity() {
		Intent intent = new Intent(ImgMainActivity.this,
				SharePicturesActivity.class);//
		if (listfile.size() > 0) {
			Bundle bundle = new Bundle();
			bundle.putStringArrayList("IMGLIST", listfileOut);
			intent.putExtra("IMGS", bundle);
			listfileOut = new ArrayList<String>();
		}
		startActivity(intent);
	}

	@Override
	public void onClickPosition(int position) {
		if (position == 0) {
			mUmengFeedBack.startFeedbackActivity();
		} else if (position == 1) {
			Toast.makeText(ImgMainActivity.this, "已经是最新版本！", Toast.LENGTH_SHORT)
					.show();
		} else if (position == 3) {
			Toast.makeText(ImgMainActivity.this, "网络超时！", Toast.LENGTH_SHORT)
					.show();
		}
	}

}
