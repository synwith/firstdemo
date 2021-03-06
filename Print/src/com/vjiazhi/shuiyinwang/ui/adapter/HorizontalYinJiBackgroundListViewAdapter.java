package com.vjiazhi.shuiyinwang.ui.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.moutian.shuiyinwang.R;

import com.vjiazhi.shuiyinwang.utils.BitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HorizontalYinJiBackgroundListViewAdapter extends BaseAdapter {
	private List<Integer> mimgPath;
	private Context mContext;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;

	// private String[] imgPath;
	public HorizontalYinJiBackgroundListViewAdapter(Context context, List<Integer> imgPath) {
		this.mContext = context;
		this.mimgPath = imgPath;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mimgPath.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.horizontal_yinjibackground_list_item,
					null);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.img_list_yinjibackground_item);
			holder.out_img_layout = (RelativeLayout) convertView
					.findViewById(R.id.img_yinjibackground_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == mimgPath.size()) {
			holder.out_img_layout.setVisibility(View.INVISIBLE);

		} else {
			// holder.mTitle.setText(mTitles[position]);
			holder.out_img_layout.setVisibility(View.VISIBLE);
			iconBitmap = getPropThumnail(mimgPath.get(position));
			holder.mImage.setImageBitmap(iconBitmap);

		}
		return convertView;
	}

	private static class ViewHolder {
		private RelativeLayout out_img_layout;
		private ImageView mImage;
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
}