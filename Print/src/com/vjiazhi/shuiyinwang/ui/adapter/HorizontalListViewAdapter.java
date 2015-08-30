package com.vjiazhi.shuiyinwang.ui.adapter;

import java.io.IOException;
import java.util.List;

import com.moutian.yinta.R;

import com.vjiazhi.shuiyinwang.utils.BitmapUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class HorizontalListViewAdapter extends BaseAdapter {
	private List<String> mimgPath;
	private Context mContext;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;
	private int selectIndex = -1;

	public HorizontalListViewAdapter(Context context, List<String> imgPath) {
		this.mContext = context;
		this.mimgPath = imgPath;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// LayoutInflater.from(mContext);
	}

	public void setImgPath(List<String> mPathList) {
		this.mimgPath = mPathList;
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

		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater
					.inflate(R.layout.horizontal_list_item, null);
			holder.mImage = (ImageView) convertView
					.findViewById(R.id.img_list_item);
			// holder.mTitle=(TextView)convertView.findViewById(R.id.text_list_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == selectIndex) {
			convertView.setSelected(true);
		} else {
			convertView.setSelected(false);
		}

		if (position == mimgPath.size()) {
			holder.mImage.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.icon_addpic_unfocused));
		} else {
			// holder.mTitle.setText(mTitles[position]);
			iconBitmap = getPropThumnail(mimgPath.get(position));
			holder.mImage.setImageBitmap(iconBitmap);
		}

		return convertView;
	}

	private static class ViewHolder {
		private ImageView mImage;
	}

	private Bitmap getPropThumnail(String path) {
		// Drawable d = mContext.getResources().getDrawable(id);
		Bitmap b = null;
		try {
			b = BitmapUtil.revitionImageSize(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
		int w = mContext.getResources().getDimensionPixelOffset(
				R.dimen.thumnail_default_width);
		int h = mContext.getResources().getDimensionPixelSize(
				R.dimen.thumnail_default_height);
		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
		return thumBitmap;
	}

	public void setSelectIndex(int i) {
		selectIndex = i;
	}
}