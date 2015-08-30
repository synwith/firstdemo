package com.vjiazhi.shuiyinwang.splash;

import java.lang.reflect.Field;
import com.moutian.shuiyinwang.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FragmentThree extends Fragment {

	public FragmentThree() {
		super();
	}

	public interface OnCloseInterface {
		public void closeActivity();
	}

	private OnCloseInterface onCloseInterface;

	public void setOnCloseInterface(OnCloseInterface in) {
		onCloseInterface = in;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_3, container, false);
		view.findViewById(R.id.tvInNew).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (onCloseInterface != null) {
							onCloseInterface.closeActivity();
						}
					}
				});
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Field childFragmentManager;
		try {
			childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
