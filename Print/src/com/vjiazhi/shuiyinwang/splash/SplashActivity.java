package com.vjiazhi.shuiyinwang.splash;

import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.vjiazhi.shuiyinwang.ui.multi_image.bean.GetImageFolderSingleInstance;
import com.vjiazhi.shuiyinwang.utils.L;
import com.vjiazhi.shuiyinwang.utils.MyConfig;

import com.moutian.yinta.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	private static final long TIME_START = 3000L;

	Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 2) {
				redirectByTime();
			} else {
				goToMainActivity();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		checkVersion();
	}

	public void checkVersion() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int currentVersion = getVersionCode();
					int oldVersionCode = MyConfig
							.getNewVersionCode(SplashActivity.this);
					if (currentVersion > oldVersionCode) {
						MyConfig.setNewVersion(SplashActivity.this,
								currentVersion);
						InitData(1);
					} else {
						InitData(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void InitData(int flag) {
		GetImageFolderSingleInstance.Instance(SplashActivity.this);
		boolean myflag = false;
		long wasteTime = 0;
		int count = 0;
		while (!myflag) { // 限制在5秒内
			myflag = GetImageFolderSingleInstance.Instance(SplashActivity.this)
					.getFinishFlag();
			count++;
			wasteTime = GetImageFolderSingleInstance.Instance(
					SplashActivity.this).getWasteTime();
		}

		if (count == 1) { //因为GetImageFolderSingleInstance是单例，所以打开一次后再打开，可能还保留有信息。
			wasteTime = 0;
		}
		if (wasteTime >= TIME_START) {
			if (flag == 0) {
				mHander.sendMessageDelayed(new Message(), 0L);
			} else {
				Message msg = new Message();
				msg.what = 2;
				mHander.sendMessageDelayed(msg, 0);
			}
		} else {
			if (flag == 0) {
				mHander.sendMessageDelayed(new Message(), TIME_START
						- wasteTime);
			} else {
				Message msg = new Message();
				msg.what = 2;
				mHander.sendMessageDelayed(msg, 0);
			}
		}
	}

	private void goToMainActivity() {
		L.l("===hahha===");
		startActivity(new Intent(SplashActivity.this, ImgMainActivity.class));
		finish();
	}

	private void redirectByTime() {
		L.l("===hahha==22=");
		startActivity(new Intent(SplashActivity.this, ViewPagerActivity.class));
		finish();
	}

	private int getVersionCode() throws Exception {
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		return packInfo.versionCode;
	}
}
