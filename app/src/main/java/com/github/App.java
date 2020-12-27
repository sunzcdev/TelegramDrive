package com.github;

import android.app.Application;

import com.github.telegram.TelegramClient;

import org.drinkless.td.libcore.telegram.TdApi;

public class App extends Application {
	private TelegramClient client;

	@Override
	public void onCreate() {
		super.onCreate();
		TdApi.TdlibParameters param = new TdApi.TdlibParameters();
		param.apiId = 2277742;
		param.apiHash = "084b977efa432bfbb24a745a1b9ac913";
		param.filesDirectory = getExternalFilesDir("file").getAbsolutePath();
		param.databaseDirectory = getExternalFilesDir("db").getAbsolutePath();
		param.useTestDc = false;
		param.systemLanguageCode = "en";
		param.deviceModel = "Android";
		param.applicationVersion = "1.0";
		client = TelegramClient.create(this, param);
	}

	public TelegramClient getClient() {
		return this.client;
	}
}
