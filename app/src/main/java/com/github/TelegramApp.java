package com.github;

import android.app.Application;

import com.github.telegram.TelegramClient;

public class TelegramApp extends Application {
	private TelegramClient client;

	@Override
	public void onCreate() {
		super.onCreate();
		client = new TelegramClient(this, 12345, "appHash", "sunzc");
	}

	public TelegramClient getClient() {
		return client;
	}
}
