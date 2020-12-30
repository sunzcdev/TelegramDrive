package com.github.telegram;

import android.util.Log;

import org.drinkless.td.libcore.telegram.TdApi;

import java.lang.reflect.ParameterizedType;

public abstract class ActionCallback2<T1, T2> extends ActionCallback<T1> {
	private static final String TAG = "ActionCallback2";

	@Override
	public void onResult(TdApi.Object object) {
		Log.i(TAG, object.toString());
		Class clazz1 = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class clazz2 = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		if (clazz1.getSimpleName().equals(object.getClass().getSimpleName())) {
			toObject((T1) object);
		} else if (clazz2.getSimpleName().equals(object.getClass().getSimpleName())) {
			toObject2((T2) object);
		}
	}

	@Override
	public abstract void toObject(T1 t1);

	public abstract void toObject2(T2 t);
}
