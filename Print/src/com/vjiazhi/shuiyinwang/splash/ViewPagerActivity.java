package com.vjiazhi.shuiyinwang.splash;

import java.util.ArrayList;
import java.util.List;
import com.vjiazhi.shuiyinwang.splash.FragmentThree.OnCloseInterface;
import com.vjiazhi.shuiyinwang.ui.ImgMainActivity;
import com.moutian.shuiyinwang.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class ViewPagerActivity extends FragmentActivity implements
		OnCloseInterface {
	private ViewPager mVPActivity;
	private FragmentOne mFragmentOne;
	private FragmentTwo mFragmentTwo;
	private FragmentThree mFragmentThree;
	private List<Fragment> mListFragment = new ArrayList<Fragment>();
	private PagerAdapter mPgAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);
		initView();
	}

	private void initView() {
		mVPActivity = (ViewPager) findViewById(R.id.vp_activity);
		mFragmentOne = new FragmentOne();
		mFragmentTwo = new FragmentTwo();
		mFragmentThree = new FragmentThree();
		mFragmentThree.setOnCloseInterface(this);
		mListFragment.add(mFragmentOne);
		mListFragment.add(mFragmentTwo);
		mListFragment.add(mFragmentThree);
		mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
				mListFragment);
		mVPActivity.setAdapter(mPgAdapter);
	}

	@Override
	public void closeActivity() {
		// TODO Auto-generated method stub
		startActivity(new Intent(this, ImgMainActivity.class));
		finish();
	}
	
	
}
