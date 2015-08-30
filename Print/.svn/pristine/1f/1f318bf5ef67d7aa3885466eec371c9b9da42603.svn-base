package com.vjiazhi.shuiyinwang.ui.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.vjiazhi.yinji.R;
import com.vjiazhi.shuiyinwang.utils.BitmapUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {

	private LayoutInflater inflater; // 视图容器
	private int selectedPosition = -1;// 选中的位置
	private boolean shape;
	// public static List<Bitmap> bmp;

	public static List<String> bitmapPath;
	private Context context;

	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public GridAdapter(Context context) {
		this(context, null);
	}

	public GridAdapter(Context context, List<String> bitmapPath) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		if (bitmapPath != null) {
			this.bitmapPath = bitmapPath;
		} else {
			this.bitmapPath = new ArrayList<String>();
		}
	}

	public void update() {
		// loading();
		handler.sendEmptyMessage(1);
	}

	public int getCount() {
		return (bitmapPath.size() + 1);
	}

	public Object getItem(int arg0) {

		return null;
	}

	public long getItemId(int arg0) {

		return 0;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		final int coord = position;
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.item_published_grid_item,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == bitmapPath.size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.icon_addpic_unfocused));
			if (position == 9) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			Bitmap tempBitmap = null;
			try {
				tempBitmap = BitmapUtil.revitionImageSize(bitmapPath
						.get(position));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (tempBitmap != null) {
				holder.image.setImageBitmap(tempBitmap);
			} else {
				holder.image.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	public class ViewHolder {
		public ImageView image;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};

	// public void loading() {
	// new Thread(new Runnable() {
	// public void run() {
	// while (true) {
	// if (Bimp.max == Bimp.drr.size()) {
	// Message message = new Message();
	// message.what = 1;
	// handler.sendMessage(message);
	// break;
	// } else {
	// try {
	// String path = Bimp.drr.get(Bimp.max);
	// System.out.println(path);
	// Bitmap bm = Bimp.revitionImageSize(path);
	// bmp.add(bm);
	// String newStr = path.substring(
	// path.lastIndexOf("/") + 1,
	// path.lastIndexOf("."));
	// FileUtils.saveBitmap(bm, "" + newStr);
	// Bimp.max += 1;
	// Message message = new Message();
	// message.what = 1;
	// handler.sendMessage(message);
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }).start();
	// }

}
