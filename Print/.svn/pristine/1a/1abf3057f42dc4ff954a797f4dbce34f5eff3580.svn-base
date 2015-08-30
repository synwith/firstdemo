	
	package com.vjiazhi.shuiyinwang.utils;
	
	// 防止点击过于频繁
	public  class MyTimeUtils {
		private static long lastClickTime;
		private static long lastJsoupTime;
		private static final int DOUBLE_CLICK_INTEVAL = 800;// 800毫秒内按钮无效，这样可以控制快速点击
		private static final int JSOUP_REQUEST_INTEVAL = 4000;//4s内不重复发起JSOUP请求
		
		public static boolean isFastDoubleClick() {
			long time = System.currentTimeMillis();
			long timeD = time - lastClickTime;
			if (0 < timeD && timeD < DOUBLE_CLICK_INTEVAL) { 
				return true;
			}
			lastClickTime = time;
			return false;
		}
		
		public static boolean isJsoupMayExecuting() {
			long time = System.currentTimeMillis();
			long timeD = time - lastJsoupTime;
			if (0 < timeD && timeD < JSOUP_REQUEST_INTEVAL) { 
				return true;
			}
			lastJsoupTime = time;
			return false;
		}
		
	}