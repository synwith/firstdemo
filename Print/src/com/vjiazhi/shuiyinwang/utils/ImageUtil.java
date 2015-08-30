package com.vjiazhi.shuiyinwang.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

public class ImageUtil {
	final static String WATERMARK_FONT_NAME = "宋体";
	final static int WATERMARK_EXTRA_EDGE = 10; // 水印图像，基于文字尺寸再加上一些余量
	final static int WATERMARK_MARGIN = 6;
	final static int WATERMARK_MARGIN_RIGHT = 10;

	private static int mColor = Color.RED;
	private static String mScreenshotPath = Environment
			.getExternalStorageDirectory() + "/zzdBitmap";

	/**
	 * 文字生成bitmap，默认颜色为红色
	 * 
	 * @param text
	 * @return
	 */
	public static Bitmap createBitmapFromText(String text, Typeface typeface) {
		return createBitmapFromText(Color.RED, text, typeface);
	}

	/**
	 * 文字生成bitmap
	 * 
	 * @param color
	 * @param text
	 * @return
	 */
	public static Bitmap createBitmapFromText(int color, String text,
			Typeface typeface) {
		mColor = color;
		if (TextUtils.isEmpty(text)) {
			text = "示例水印-by水印王";
		}
		Rect rect = new Rect();
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); // 去除锯齿
		Typeface font = null;
		if (typeface == null)
			font = Typeface.create(WATERMARK_FONT_NAME, Typeface.NORMAL);
		else {
			font = typeface;
		}
		// font
		// =Typeface.createFromAsset(getAssets(),"fonts/HandmadeTypewriter.ttf");
		p.setColor(mColor);
		p.setTypeface(font);
		p.setTextSize(40);

		p.getTextBounds(text, 0, text.length(), rect);

		// 后续可能需要描边
		int nMarkwidth = rect.width() + WATERMARK_MARGIN_RIGHT; // 原先是(int)(nSrcwidth*9.0/10.0);
		int nMarkHeight = rect.height() + WATERMARK_MARGIN; // 原先是(int)(nSrcHeight*1.0/10.0);

		Bitmap bmpWaterMark = Bitmap.createBitmap(nMarkwidth, nMarkHeight,
				Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmpWaterMark);

		canvasTemp.drawColor(Color.TRANSPARENT);
		// canvasTemp.drawBitmap(bitmap, matrix, p);

		canvasTemp.drawText(text, WATERMARK_MARGIN / 2, nMarkHeight
				- WATERMARK_EXTRA_EDGE - WATERMARK_MARGIN / 2, p);

		return bmpWaterMark;
	}

	// 以下是保存逻辑
	public static void saveScreenshot(View view) {
		if (ensureSDCardAccess()) {
			Bitmap bitmap = convertViewToBitmap(view);
			File file = new File(mScreenshotPath + "/"
					+ System.currentTimeMillis() + ".jpg");
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.e("Panel", "FileNotFoundException", e);
			} catch (IOException e) {
				Log.e("Panel", "IOEception", e);
			}
		}
	}

	/**
	 * 把View上的元素画到Canvas上并转化成bitmap
	 * 
	 * @param view
	 * @return
	 */
	private static Bitmap convertViewToBitmap(View view) {

		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
				Bitmap.Config.ARGB_8888);
		// 利用bitmap生成画布
		Canvas canvas = new Canvas(bitmap);

		// 把view中的内容绘制在画布上
		view.draw(canvas);

		return bitmap;
	}

	/**
	 * 文字生成bitmap放到另一张背景图片上
	 * 
	 * @param color
	 * @param text
	 * @return
	 */
	public static Bitmap createBitmapFromBitmap(int color, String text,
			Typeface typeface, Bitmap background) {
		mColor = color;
		if (TextUtils.isEmpty(text)) {
			text = "示例水印-by水印王";
		}
		Rect rect = new Rect();
		Paint p = new Paint(Paint.ANTI_ALIAS_FLAG); // 去除锯齿
		Typeface font = null;
		if (typeface == null)
			font = Typeface.create(WATERMARK_FONT_NAME, Typeface.NORMAL);
		else {
			font = typeface;
		}
		p.setColor(mColor);
		p.setTypeface(font);
		p.setTextSize(40);

		p.getTextBounds(text, 0, text.length(), rect);

		FontMetrics fm = p.getFontMetrics();

		// 后续可能需要描边
		int nMarkwidth = rect.width() + WATERMARK_MARGIN_RIGHT; // 原先是(int)(nSrcwidth*9.0/10.0);
		int nMarkHeight = rect.height() + WATERMARK_MARGIN_RIGHT; // 原先是(int)(nSrcHeight*1.0/10.0);
		Bitmap bmpWaterMark = ThumbnailUtils.extractThumbnail(background,
				nMarkwidth, nMarkHeight);
		Canvas canvasTemp = new Canvas(bmpWaterMark);
		canvasTemp.drawColor(Color.TRANSPARENT);
		canvasTemp.drawText(text, WATERMARK_MARGIN_RIGHT / 2, nMarkHeight
				- fm.descent, p);
		return bmpWaterMark;
	}

	/**
	 * Helper method to ensure that the given path exists.
	 * <p>
	 * TODO: check external storage state
	 */
	private static boolean ensureSDCardAccess() {
		File file = new File(mScreenshotPath);
		if (file.exists()) {
			return true;
		} else if (file.mkdirs()) {
			return true;
		}
		return false;
	}

	public static byte[] decodeBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);
		opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inTempStorage = new byte[16 * 1024];
		FileInputStream is = null;
		Bitmap bmp = null;
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(path);
			bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
			double scale = getScaling(opts.outWidth * opts.outHeight,
					1024 * 600);
			Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
					(int) (opts.outWidth * scale),
					(int) (opts.outHeight * scale), true);
			bmp.recycle();
			baos = new ByteArrayOutputStream();
			bmp2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			bmp2.recycle();
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
		return baos.toByteArray();
	}

	private static double getScaling(int src, int des) {
		/**
		 * 48 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比 49
		 */
		double scale = Math.sqrt((double) des / (double) src);
		return scale;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

}
