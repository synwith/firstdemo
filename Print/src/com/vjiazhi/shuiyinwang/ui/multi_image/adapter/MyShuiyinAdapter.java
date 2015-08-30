package com.vjiazhi.shuiyinwang.ui.multi_image.adapter;

import java.io.File;
import java.util.List;
import android.content.Context;
import android.widget.ImageView;
import com.vjiazhi.shuiyinwang.ui.multi_image.ViewHolder;

import com.moutian.yinta.R;



/**
 * 从相册选出图片做水印Adapter
 * @author nil
 *
 */
public class MyShuiyinAdapter extends CommonAdapter<File> {

	public MyShuiyinAdapter(Context context, List<File> mDatas, int itemLayoutId,
			String dirPath) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(final ViewHolder helper, final File item1) {
		helper.setImageResource(R.id.id_shuiyin_image,
				R.drawable.listview_item_down);
		final String item = item1.getAbsolutePath();
		String tempFileString = item;
		final String dirFileString = tempFileString;
		final ImageView mImageView = helper.getView(R.id.id_shuiyin_image);
			helper.setImageByUrl(R.id.id_shuiyin_image, dirFileString);
			mImageView.setColorFilter(null);
	}

}
