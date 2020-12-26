package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

public class UserAction extends TelegramAction {
	private AuthAction authAction;

	public UserAction(AuthAction authAction) {
		this.authAction = authAction;
	}

	public void GetMe(ActionCallback callback) {
		authAction.send(TdApi.User.CONSTRUCTOR, new TdApi.GetMe(), callback);
	}

	@Override
	public void onResult(TdApi.Object object) {
		super.onResult(object);
		switch (object.getConstructor()) {
			case TdApi.User.CONSTRUCTOR:
				TdApi.User user = (TdApi.User) object;
				show("我的id为:" + user.id);
				break;
		}
	}
}
