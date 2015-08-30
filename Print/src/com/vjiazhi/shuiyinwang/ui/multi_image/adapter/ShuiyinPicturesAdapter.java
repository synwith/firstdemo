package com.vjiazhi.shuiyinwang.ui.multi_image.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.vjiazhi.shuiyinwang.ui.multi_image.ViewHolder;

import com.moutian.yinta.R;


public class ShuiyinPicturesAdapter extends CommonAdapter<File> {
	private List<File> mSelectedImage = new ArrayList<File>();

	public ShuiyinPicturesAdapter(Context context, List<File> mDatas,
			int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public List<File> getSelectImgsList() {
		return mSelectedImage;
	}

	public void clearSelectedList() {
		this.mSelectedImage.clear();
	}

	public void setSelectedImgsList(List<File> selectedList) {
		this.mSelectedImage.clear();
		this.mSelectedImage.addAll(selectedList);
	}

	@Override
	public void convert(final ViewHolder helper, final File item1) {
		helper.setImageResource(R.id.id_share_image,
				R.drawable.listview_item_down);
		final String item = item1.getAbsolutePath();
		final ImageView mImageView = helper.getView(R.id.id_share_image);
		final ImageView mSelect = helper.getView(R.id.id_share_select);

		helper.setImageByUrl(R.id.id_share_image, item);
		mSelect.setVisibility(View.VISIBLE);
		if (mSelectedImage.contains(item1)) {
			mSelect.setBackgroundResource(R.drawable.pics_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		} else {
			mSelect.setBackgroundResource(R.drawable.pics_unselected);
		}
		mImageView.setColorFilter(null);

		// 设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener() {
			// 选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v) {

				if (mSelectedImage.contains(item1)) {
					mSelectedImage.remove(item1);
					mSelect.setBackgroundResource(R.drawable.pics_unselected);
					mImageView.setColorFilter(null);
				} else {
					mSelectedImage.add(item1);
					mSelect.setBackgroundResource(R.drawable.pics_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
				}

				if (mSelectInterface != null) {
					mSelectInterface.selectItem(mSelectedImage);
				}
			}
		});

		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(item1)) {
			mSelect.setBackgroundResource(R.drawable.pics_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}
	}

	public interface SelectInterface {
		public void selectItem(List<File> dataFile);
	}

	SelectInterface mSelectInterface = null;

	public void setSelectInterface(SelectInterface selectInterface) {
		mSelectInterface = selectInterface;
	}

}
