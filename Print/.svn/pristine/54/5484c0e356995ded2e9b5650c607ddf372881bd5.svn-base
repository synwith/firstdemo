package com.vjiazhi.shuiyinwang.utils;

import android.content.Context;
import android.util.Log;

public class L {
	private static boolean DEBUG = true;
	private static String WDXTAG = "SYW:";

	public static void logd(Context ctx, String msg) {
		if (DEBUG)
			Log.d(ctx.getClass().getSimpleName(), msg);
	}

	public static void loge(Context ctx, String msg) {
		if (DEBUG)
			Log.e(ctx.getClass().getSimpleName(), msg);
	}

	public static void l(String tag,String content) {
		if (DEBUG) {
			Log.i(WDXTAG+tag, content);
		}
	}
	
	public static void l(String content) {
		if (DEBUG) {
			Log.i(WDXTAG, content);
		}
	}

}