package com.github.telegram;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseArray;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.HashMap;
import java.util.Map;

public class ClientAction extends TelegramAction {
	protected Client client;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, ActionCallback> callbackMap = new HashMap<>();

	public void send(int returnConstructor, TdApi.Function function, ActionCallback callback) {
		callbackMap.put(returnConstructor, callback);
		if (function != null) {
			Log.i("sunzc", ">>>>>:" + function.toString());
			this.client.send(function, this);
		}
	}

	public void create() {
		Client.execute(new TdApi.SetLogVerbosityLevel(0));
		client = Client.create(ClientAction.this, Throwable::printStackTrace, Throwable::printStackTrace);
	}

	@Override
	public void onResult(TdApi.Object object) {
		log(object.toString());
		if (!callbackMap.isEmpty() && callbackMap.containsKey(object.getConstructor())) {
			ActionCallback callback = callbackMap.get(object.getConstructor());
			if (callback != null) {
				switch (object.getConstructor()) {
					case TdApi.UpdateFile.CONSTRUCTOR:
						break;
					default:
						callbackMap.remove(object.getConstructor());
						break;
				}
				callback.call(object);
			}
		}
	}
}
