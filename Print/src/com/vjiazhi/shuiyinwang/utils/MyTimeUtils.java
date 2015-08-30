	
	package com.vjiazhi.shuiyinwang.utils;
	public  class MyTimeUtils {
		private static long lastClickTime;
		private static long lastJsoupTime;
		private static final int DOUBLE_CLICK_INTEVAL = 800;// 
		private static final int JSOUP_REQUEST_INTEVAL = 4000;//
		
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