package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class UserAction extends TelegramAction {
	private ClientAction clientAction;
	private AuthAction authAction;

	public UserAction(ClientAction clientAction, AuthAction authAction) {
		super(authAction.context);
		this.clientAction = clientAction;
		this.authAction = authAction;
	}

	public void GetMe() {
		clientAction.send(new TdApi.GetMe(), this);
	}
}
