package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class UserAction extends TelegramAction {
	private AuthAction authAction;

	public UserAction(AuthAction authAction) {
		super(authAction.context);
		this.authAction = authAction;
	}

	public void GetMe() {
		authAction.send(new TdApi.GetMe(), this);
	}

	@Override
	public void onResult(TdApi.Object object) {
		super.onResult(object);
		switch (object.getConstructor()) {
			case TdApi.User.CONSTRUCTOR:
				TdApi.User user = (TdApi.User) object;
				toast("我的id为:" + user.id);
				break;
		}
	}
}
