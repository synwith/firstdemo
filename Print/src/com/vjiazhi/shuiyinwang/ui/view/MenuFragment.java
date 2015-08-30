package com.vjiazhi.shuiyinwang.ui.view;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.moutian.shuiyinwang.R;


import com.vjiazhi.shuiyinwang.ui.AboutAppActivity;
import com.vjiazhi.shuiyinwang.ui.ShuiyinPicturesActivity;
import com.vjiazhi.shuiyinwang.utils.L;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class MenuFragment extends Fragment implements View.OnClickListener {

	private View currentView;
	private RelativeLayout jieshao;
	private RelativeLayout fankui;
	private RelativeLayout gengxin;
	private RelativeLayout shuiyin_xiangce;
	

	public View getCurrentView() {
		return currentView;
	}

	public MenuFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		currentView = inflater.inflate(R.layout.slidingpane_menu_layout,
				container, false);

		jieshao = (RelativeLayout) currentView.findViewById(R.id.zimingxing);
		fankui = (RelativeLayout) currentView.findViewById(R.id.yijian);
		gengxin = (RelativeLayout) currentView.findViewById(R.id.gengxin);
		shuiyin_xiangce = (RelativeLayout) currentView
				.findViewById(R.id.shuiyinxiange);

		jieshao.setOnClickListener(this);
		fankui.setOnClickListener(this);
		gengxin.setOnClickListener(this);
		shuiyin_xiangce.setOnClickListener(this);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int arg0, UpdateResponse arg1) {
				// TODO Auto-generated method stub
				switch (arg0) {
				case 0:
					UmengUpdateAgent.showUpdateDialog(getActivity(),
							 arg1);
					break;
				case 1:
					if (dateInterface != null) {
						dateInterface.onClickPosition(1);
					}
					break;
				case 2:
					L.l("=====2===");
					break;
				case 3:
					if (dateInterface != null) {
						dateInterface.onClickPosition(3);
					}
					break;
				}
			}
		});

		return currentView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// ((ImgMainActivity) getActivity()).getSlidingPaneLayout().closePane();
		if (v.getId() == R.id.zimingxing) {
			Intent intent = new Intent(getActivity(), AboutAppActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.gengxin) {
			UmengUpdateAgent.forceUpdate(getActivity());
		} else if (v.getId() == R.id.yijian) {
			if (dateInterface != null) {
				dateInterface.onClickPosition(0);
			}
		} else if (v.getId() == R.id.shuiyinxiange) {
			Intent intent = new Intent(getActivity(),
					ShuiyinPicturesActivity.class);
			startActivity(intent);
		}
	}

	public interface DataInterface {
		public void onClickPosition(int position);
	}

	DataInterface dateInterface = null;

	public void setUmengInterface(DataInterface u) {
		dateInterface = u;
	}

}
