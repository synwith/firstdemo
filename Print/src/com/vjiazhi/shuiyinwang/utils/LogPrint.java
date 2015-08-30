package com.vjiazhi.shuiyinwang.utils;

import android.content.Context;
import android.util.Log;

public class LogPrint {
	private static boolean DEBUG = true;

	public static void logd(Context ctx, String msg) {
		if (DEBUG)
			Log.d(ctx.getClass().getSimpleName(), msg);
	}

	public static void loge(Context ctx, String msg) {
		if (DEBUG)
			Log.e(ctx.getClass().getSimpleName(), msg);
	}

}