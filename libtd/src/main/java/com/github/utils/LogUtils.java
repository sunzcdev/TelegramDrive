package com.github.utils;

import android.util.Log;

import java.util.List;

public class LogUtils {
	public static <T> void printList(String tag, List<T> list) {
		if (list == null || list.isEmpty()) {
			Log.e(tag, "打印列表为空");
			return;
		}
		for (T t : list) {
			Log.i(tag, t.toString());
		}
	}

	public static <T> void printArr(String tag, T[] arr) {
		if (arr == null || arr.length == 0) {
			Log.e(tag, "打印数组为空");
			return;
		}
		for (T t : arr) {
			Log.i(tag, t.toString());
		}
	}

	public static void log(String tag, String msg) {
		Log.i(tag, msg);
	}
}
