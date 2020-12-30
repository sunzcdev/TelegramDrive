package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class UserAction extends TelegramAction {
	private AuthAction authAction;

	public UserAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	public void GetMe(ActionCallback<TdApi.User> callback) {
		authAction.send(new TdApi.GetMe(), callback);
	}

}
