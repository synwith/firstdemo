/**   
 * Copyright © 2014 All rights reserved.
 * 
 * @Title: SlidingPaneContentFragment.java 
 * @Prject: SlidingPane
 * @Package: com.example.slidingpane 
 * @Description: TODO
 * @author: raot  719055805@qq.com
 * @date: 2014年9月5日 上午10:44:01 
 * @version: V1.0   
 */
package com.vjiazhi.shuiyinwang.ui.view;

import com.vjiazhi.yinji.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ContentFragment extends Fragment {
	private View currentView;

	InterfaceContentView icv = null;

	public View getCurrentView() {
		return currentView;
	}

	public ContentFragment(InterfaceContentView i) {
		icv = i;
	}

	public interface InterfaceContentView {
		void hasDone();
	}

	public void setCurrentViewPararms(FrameLayout.LayoutParams layoutParams) {
		currentView.setLayoutParams(layoutParams);
	}

	public FrameLayout.LayoutParams getCurrentViewParams() {
		return (LayoutParams) currentView.getLayoutParams();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		currentView = inflater.inflate(R.layout.activity_img_main, container,
				false);
		if (icv != null) {
			icv.hasDone();
		}
		return currentView;
	}

	public View getContentView() {
		return currentView;
	}

}
