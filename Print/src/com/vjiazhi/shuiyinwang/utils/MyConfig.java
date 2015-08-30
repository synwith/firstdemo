package com.vjiazhi.shuiyinwang.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

public class MyConfig {
	public static int m_fSizePerScale = 100;//
	public static String Tag = "shuiyin";
	public static String TEXT = "wenzi";
	public static String COLOR = "color";
	public static String POSITION = "position";
	public static String VERSION = "version";

	private static int SCREEN_WIDTH = 0;
	private static int SCREEN_HEIGHT = 0;
	private static int STATUS_HEIGHT = 0;

	public static void setNewColor(Context context, int nColor) {
		saveIntToSharePreference(context, COLOR, nColor);
	}

	public static void setNewPosition(Context context, int nIndex) {
		saveIntToSharePreference(context, POSITION, nIndex);
	}

	public static void setNewText(Context context, String strText) {
		saveStringToSharePreference(context, TEXT, strText);
	}

	public static int getNewColor(Context context) {
		return getIntFromSharePrefenrence(context, COLOR, Color.WHITE);
	}

	public static int getNewPosition(Context context) {
		return getIntFromSharePrefenrence(context, POSITION, 4);// 中间的位置
	}

	public static String getNewText(Context context) {
		return getStringFromSharePrefenrence(context, TEXT, "示例水印-By印它");
	}

	public static void setNewVersion(Context context, int versionCode) {
		saveIntToSharePreference(context, VERSION, versionCode);
	}
	
	public static int getNewVersionCode(Context context) {
		return getIntFromSharePrefenrence(context, VERSION, 0);// 中间的位置
	}

	public static void setNewSize(int nPercentScale) {
		if (nPercentScale <= 0 || nPercentScale > 200) {
			return;
		}
		m_fSizePerScale = nPercentScale;
	}

	public static int getNewSize() {
		return m_fSizePerScale;
	}

	private static void saveIntToSharePreference(Context context, String key,
			int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Tag,
				Context.MODE_PRIVATE); // 私有数据
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putInt(key, value);
		editor.commit();// 提交修改
	}

	private static void saveStringToSharePreference(Context context,
			String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Tag,
				Context.MODE_PRIVATE); // 私有数据
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString(key, value);
		editor.commit();// 提交修改
	}

	private static int getIntFromSharePrefenrence(Context context, String key,
			int defaultValue) {
		SharedPreferences share = context.getSharedPreferences(Tag,
				Activity.MODE_WORLD_READABLE);

		int i = share.getInt(key, defaultValue);
		return i;

	}

	private static String getStringFromSharePrefenrence(Context context,
			String key, String defautValue) {
		SharedPreferences share = context.getSharedPreferences(Tag,
				Activity.MODE_WORLD_READABLE);

		String in = share.getString(key, defautValue);
		return in;

	}

	/**
	 * 初始化获取屏幕的大小
	 * 
	 * @param activity
	 */
	public static void initScreenWidthHeight(Activity activity) {
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		SCREEN_WIDTH = (int) (metric.widthPixels); // 屏幕宽度（像素）
		SCREEN_HEIGHT = (int) (metric.heightPixels);
	}

	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}

	public static int getScreenHeight() {
		return SCREEN_HEIGHT;
	}

	/**
	 * 初始化状态栏高度
	 */

	public static void initStatusHeight(Activity activity) {
		View view = activity.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT);
		STATUS_HEIGHT = view.getTop();
	}

	public static int getStatusHeight() {
		return STATUS_HEIGHT;
	}

	/******************************************************
	 * 下面是分享图片的代码 *********************************
	 * ****************************************************
	 */
	// AlertDialog mDlgChooseShare = null;
	//
	// private void showShareDialog() {
	// mDlgChooseShare = new AlertDialog.Builder(this).setNegativeButton(
	// android.R.string.cancel, null).create();
	// Window windowDlg = mDlgChooseShare.getWindow();
	//
	// // 使用自定义的网格布局
	// String[] name = { "微信好友", "微信朋友圈", "更多方式" };
	//
	// int[] iconarray = { R.drawable.umeng_socialize_wechat,
	// R.drawable.umeng_socialize_wxcircle, R.drawable.logo_more };
	//
	// LayoutInflater factory = LayoutInflater.from(mContext);
	// View view = factory.inflate(R.layout.share_alert_layout, null);
	// GridView grView = (GridView) view
	// .findViewById(R.id.main_menu_grid_view);
	//
	// GridViewAdapter gridAdapter = new GridViewAdapter(this, name, iconarray);
	// grView.setAdapter(gridAdapter);
	// grView.setOnItemClickListener(new OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View view,
	// int position, long id) {
	// shareCurrent(position);
	// }
	// });
	//
	// windowDlg
	// .setType(android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	// mDlgChooseShare.setView(view);
	// windowDlg.setGravity(Gravity.BOTTOM);
	// mDlgChooseShare.show();
	// }
	//
	// private void shareCurrent(int which) {
	// MobclickAgent.onEvent(this, "share_event");
	// final UMSocialService mController = UMServiceFactory
	// .getUMSocialService("com.umeng.share", RequestType.SOCIAL);
	//
	// String appID = "wx4e79aee91524b279";
	// String appSecret = "6419aaed2e10049d1304c0c72d1267b1";
	// Bitmap bitmap = BitmapFactory.decodeFile(hListViewAdapter
	// .getSelectedFilePathArrayList().get(0));
	// if (which == 0) {
	//
	// // 添加微信平台 UMWXHandler
	// UMWXHandler wxHandler = new UMWXHandler(this, appID, appSecret);
	// wxHandler.setToCircle(false);
	// wxHandler.addToSocialSDK();
	//
	// // 设置微信好友分享内容
	// WeiXinShareContent weixinContent = new WeiXinShareContent(); // 设置分享文字
	// weixinContent.setTitle(mCurrTitle); //
	// // weixinContent.setTargetUrl(mStrImageUrl);
	//
	// UMImage uMImgBitmap = new UMImage(this, bitmap);
	// weixinContent.setShareImage(uMImgBitmap);
	// mController.setShareMedia(weixinContent);
	//
	// mController.postShare(this, SHARE_MEDIA.WEIXIN,
	// new SnsPostListener() {
	//
	// @Override
	// public void onComplete(SHARE_MEDIA arg0, int arg1,
	// SocializeEntity arg2) {
	// }
	//
	// @Override
	// public void onStart() {
	// }
	// });
	//
	// } else if (which == 1) {// 朋友圈
	// UMWXHandler wxCircleHandler = new UMWXHandler(this, appID,
	// appSecret);
	// wxCircleHandler.setToCircle(true);
	// wxCircleHandler.addToSocialSDK();
	//
	// // 设置微信朋友圈分享内容
	// // String mShareContent = getShortTextFromData();
	// CircleShareContent circleMedia = new CircleShareContent();
	// // circleMedia.setShareContent(mShareContent);
	// // 设置朋友圈title
	// circleMedia.setTitle(mCurrTitle);
	// UMImage uMImgBitmap = new UMImage(this, bitmap);
	// circleMedia.setShareImage(uMImgBitmap);
	// // circleMedia.setShareImage(new UMImage().set)
	// mController.setShareMedia(circleMedia);
	//
	// mController.postShare(this, SHARE_MEDIA.WEIXIN_CIRCLE,
	// new SnsPostListener() {
	//
	// @Override
	// public void onComplete(SHARE_MEDIA arg0, int arg1,
	// SocializeEntity arg2) {
	// }
	//
	// @Override
	// public void onStart() {
	// }
	// });
	//
	// } else {// 使用其他分享方式
	// showWaitingDialog();
	// new Handler().postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// Intent inSend = new Intent(Intent.ACTION_SEND);
	// String filePath = "";
	//
	// filePath = saveCurrentBitmap(false);// 这里保存不提示
	//
	// hideWaitingDialog();
	//
	// if (!filePath.isEmpty()) {
	// File file = new File(filePath);
	// if (file != null && file.exists() && file.isFile()) {
	// inSend.setType("image/jpg");
	// Uri u = Uri.fromFile(file);
	// inSend.putExtra(Intent.EXTRA_STREAM, u);
	// }
	//
	// }
	// // 当用户选择短信时使用sms_body取得文字
	// inSend.putExtra(Intent.EXTRA_SUBJECT, mCurrTitle);
	// inSend.putExtra("sms_body", mCurrTitle + "\n"
	// + mBlogUrl + "\n【来自秦刚观点】");
	// inSend.putExtra(Intent.EXTRA_TEXT, mCurrTitle + "\n"
	// + mBlogUrl + "\n【来自秦刚观点】");
	// inSend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// inSend.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	// try {
	// startActivity(Intent
	// .createChooser(inSend, "分享自印它。"));
	// } catch (android.content.ActivityNotFoundException ex) {
	// // if no app handles it, do nothing
	// }
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }, 100);
	//
	// }
	// mDlgChooseShare.dismiss();
	// }
	//
	// // /这段代码能够实现不需要微信SDK分享到微信好友
	// private void sharePicsTest(List<String> mSelectedImage) {
	// ArrayList<Uri> imageUris = new ArrayList<Uri>();
	// for (int i = 0; i < mSelectedImage.size(); i++) {
	// imageUris.add(Uri.fromFile(new File(mSelectedImage.get(i))));
	// }
	// Intent shareIntent = new Intent();
	// shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
	// shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
	// shareIntent.setType("image/*");
	// startActivity(Intent.createChooser(shareIntent, "分享图片"));
	// }

}