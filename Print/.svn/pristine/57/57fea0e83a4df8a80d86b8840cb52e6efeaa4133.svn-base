package com.vjiazhi.shuiyinwang.ui.multi_image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.vjiazhi.shuiyinwang.utils.ImageUtil;
import com.vjiazhi.shuiyinwang.utils.L;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageLoader {
	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	/**
	 * 线程池的线程数量，默认为1
	 */
	public static int mThreadCount = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTasks;
	/**
	 * 轮询的线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHander;

	/**
	 * 运行在UI线程的handler，用于给ImageView设置图片
	 */
	private Handler mHandler;

	/**
	 * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
	 */
	private volatile Semaphore mSemaphore = new Semaphore(0);

	/**
	 * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
	 */
	private volatile Semaphore mPoolSemaphore;

	private static ImageLoader mInstance;

	/**
	 * 队列的调度方式
	 * 
	 * @author zhy
	 * 
	 */
	public enum Type {
		FIFO, LIFO
	}

	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static ImageLoader getInstance() {

		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(mThreadCount, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	private ImageLoader(int threadCount, Type type) {
		init(threadCount, type);
	}

	private void init(int threadCount, Type type) {
		// loop thread
		mPoolThread = new Thread() {
			@Override
			public void run() {
				Looper.prepare();

				mPoolThreadHander = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						mThreadPool.execute(getTask());
						try {
							mPoolSemaphore.acquire();
						} catch (InterruptedException e) {
						}
					}
				};
				// 释放一个信号量
				mSemaphore.release();
				Looper.loop();
			}
		};
		mPoolThread.start();

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			};
		};

		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mPoolSemaphore = new Semaphore(threadCount);
		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;

	}

	/**
	 * 加载图片
	 * 
	 * @param path
	 * @param imageView
	 */
	public void loadImage(final String path, final ImageView imageView) {
		// set tag
		imageView.setTag(path);
		// UI线程
		if (mHandler == null) {
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					ImageView imageView = holder.imageView;
					Bitmap bm = holder.bitmap;
					String path = holder.path;
					if (imageView.getTag().toString().equals(path)) {
						imageView.setImageBitmap(bm);
					}
				}
			};
		}

		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null) {
			ImgBeanHolder holder = new ImgBeanHolder();
			holder.bitmap = bm;
			holder.imageView = imageView;
			holder.path = path;
			Message message = Message.obtain();
			message.obj = holder;
			mHandler.sendMessage(message);
		} else {
			addTask(new Runnable() {
				@Override
				public void run() {

					ImageSize imageSize = getImageViewWidth(imageView);

					int reqWidth = imageSize.width;
					int reqHeight = imageSize.height;

					Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
							reqHeight);
					addBitmapToLruCache(path, bm);
					ImgBeanHolder holder = new ImgBeanHolder();
					holder.bitmap = getBitmapFromLruCache(path);
					holder.imageView = imageView;
					holder.path = path;
					Message message = Message.obtain();
					message.obj = holder;
					// Log.e("TAG", "mHandler.sendMessage(message);");
					mHandler.sendMessage(message);
					mPoolSemaphore.release();
				}
			});
		}

	}

	/**
	 * 添加一个任务
	 * 
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable) {
		try {
			// 请求信号量，防止mPoolThreadHander为null
			if (mPoolThreadHander == null)
				mSemaphore.acquire();
		} catch (InterruptedException e) {
		}
		mTasks.add(runnable);

		mPoolThreadHander.sendEmptyMessage(0x110);
	}

	/**
	 * 取出一个任务
	 * 
	 * @return
	 */
	private synchronized Runnable getTask() {
		if (mType == Type.FIFO) {
			return mTasks.removeFirst();
		} else if (mType == Type.LIFO) {
			return mTasks.removeLast();
		}
		return null;
	}

	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static ImageLoader getInstance(int threadCount, Type type) {

		if (mInstance == null) {
			synchronized (ImageLoader.class) {
				if (mInstance == null) {
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 */
	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 * 往LruCache中添加一张图片
	 * 
	 * @param key
	 * @param bitmap
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap) {
		if (getBitmapFromLruCache(key) == null) {
			if (bitmap != null)
				mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根据计算的inSampleSize，得到压缩后图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight) {
		// 原来的代码，但是在这个句子会崩溃BitmapFactory.decodeFile(pathName, options);
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		// final BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inJustDecodeBounds = true;
		// BitmapFactory.decodeFile(pathName, options);
		// // 调用上面定义的方法计算inSampleSize值
		// options.inSampleSize = calculateInSampleSize(options, reqWidth,
		// reqHeight);
		// // 使用获取到的inSampleSize值再次解析图片
		// options.inJustDecodeBounds = false;
		// Bitmap bitmap = getBigBitmap(pathName);
		// return BitmapFactory.decodeFile(pathName, options);

		return getBitmapFromFile(new File(pathName), reqWidth, reqHeight);

		// L.l("==333====bitmap===:" + getBigBitmap(pathName));
		// return getBigBitmap(pathName);
		// Bitmap bitmap = BitmapFactory.decodeByteArray(
		// ImageUtil.decodeBitmap(pathName), 0,
		// ImageUtil.decodeBitmap(pathName).length);

	}

	/**
	 * 大图加载的时候容易溢出，可以使用这个方法加载
	 * 
	 * @author nil
	 * 
	 */
	// ==============测试===================================

	public static Bitmap getBitmapFromFile(File dst, int width, int height) {
		if (null != dst && dst.exists()) {
			BitmapFactory.Options opts = null;
			if (width > 0 && height > 0) {
				opts = new BitmapFactory.Options(); // 设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(dst.getPath(), opts);
				// 计算图片缩放比例
				final int minSideLength = Math.min(width, height);
				int size = computeSampleSize(opts, minSideLength, width
						* height);
				opts.inSampleSize = size; // 这里一定要将其设置回false，因为之前我们将其设置成了true
				opts.inJustDecodeBounds = false;
				opts.inInputShareable = true;
				opts.inPurgeable = true;
			}
			try {
				return BitmapFactory.decodeFile(dst.getPath(), opts);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
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
			// return the larger one when there is no overlapping zone.
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

	public static Bitmap getBigBitmap(String imgPath) {

		BitmapFactory.Options bfOptions = new BitmapFactory.Options();
		bfOptions.inDither = false;
		bfOptions.inPurgeable = true;
		bfOptions.inTempStorage = new byte[12 * 1024];
		bfOptions.inJustDecodeBounds = true;
		File file = new File(imgPath);
		FileInputStream fs = null;
		Bitmap bmp = null;
		try {
			fs = new FileInputStream(file);
			L.l("=====come ==too===here===" + fs + "====path:" + imgPath);
			if (fs != null) {
				bmp = BitmapFactory.decodeFileDescriptor(fs.getFD(), null,
						bfOptions);
				L.l("=====come ==too===here===" + bmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fs != null) {
				try {
					fs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bmp;
	}

	// ============= 如上测试==============================================

	private class ImgBeanHolder {
		Bitmap bitmap;
		ImageView imageView;
		String path;
	}

	private class ImageSize {
		int width;
		int height;
	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;

				Log.e("TAG", value + "");
			}
		} catch (Exception e) {
		}
		return value;
	}

}
