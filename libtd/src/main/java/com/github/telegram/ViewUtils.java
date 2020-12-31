package com.github.telegram;

import android.content.Context;
import android.widget.Toast;

public class ViewUtils {
	private static Toast toast;

	public static void toast(Context context, String msg) {
		context.getMainExecutor().execute(() -> {
			if (toast != null) {
				toast.setText(msg);
			} else {
				toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
			}
			toast.show();
		});
	}
}
