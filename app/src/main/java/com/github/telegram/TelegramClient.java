package com.github.telegram;

import android.content.Context;

import com.github.ui.LoginActivity;

public class TelegramClient {
	public TelegramClient(Context context, int apiId, String apiHash, String name) {
		AuthAction auth = new AuthAction(context);
		auth.create();
		auth.setParam();
	}

	public void login(String s, LoginActivity loginActivity) {
	}

}
