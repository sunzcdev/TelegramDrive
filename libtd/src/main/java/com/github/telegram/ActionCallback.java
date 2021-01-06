package com.github.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public abstract class ActionCallback<T> implements Client.ResultHandler {
	private static final String TAG = "ActionCallback";

	@Override
	public void onResult(TdApi.Object object) {
		Log.i(TAG, object.toString());
		toObject((T) object);
	}

	public abstract void toObject(T t);
}
