package com.vjiazhi.shuiyinwang.utils;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class L {
	private static boolean DEBUG = true;
	private static String WDXTAG = "Yinji_2015:";

	public static HashMap<String, String> TimeMap = new HashMap();

	public static void logd(Context ctx, String msg) {
		if (DEBUG)
			Log.d(ctx.getClass().getSimpleName(), msg);
	}

	public static void loge(Context ctx, String msg) {
		if (DEBUG)
			Log.e(ctx.getClass().getSimpleName(), msg);
	}

	public static void l(String tag, String content) {
		if (DEBUG) {
			Log.i(WDXTAG + tag, content);
		}
	}

	public static void l(String content) {
		if (DEBUG) {
			Log.i(WDXTAG, content);
		}
	}

	public static void tb(String tag) {
		if (!TimeMap.containsKey(tag)) {
			L.l("=====" + tag + "====begin!");
			long nowTime = System.currentTimeMillis();
			TimeMap.put(tag, nowTime + "");
		} else {
			L.l("====已经存在，请修改Tag或者封闭End====");
		}
	}

	public static long te(String tag) {
		long leftTime = -1;
		if (TimeMap.containsKey(tag)) {
			long nowTime = System.currentTimeMillis();
			long oldTime = Long.parseLong(TimeMap.get(tag));
			leftTime = nowTime - oldTime;
			TimeMap.remove(tag);
		} else {
			L.l("====请先写Begin====");
		}
		return leftTime;
	}

}