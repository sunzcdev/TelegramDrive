package com.github.telegram;

import android.content.Context;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class AuthAction extends ClientAction {
	private TdApi.AuthorizationState authorizationState;

	public AuthAction(Context context) {
		super(context);
	}

	@Override
	public void send(TdApi.Function function, Client.ResultHandler handler) {
		if (authorizationState != null && authorizationState.getConstructor() == TdApi.AuthorizationStateReady.CONSTRUCTOR) {
			super.send(function, handler);
		} else {
			toast("telegram未登录");
		}
	}

	public void setParam() {
		TdApi.TdlibParameters param = new TdApi.TdlibParameters();
		param.apiId = 2277742;
		param.apiHash = "084b977efa432bfbb24a745a1b9ac913";
		param.filesDirectory = context.getFilesDir().getAbsolutePath();
		param.databaseDirectory = context.getExternalFilesDir("db").getAbsolutePath();
		param.useTestDc = true;
		param.systemLanguageCode = "en";
		param.deviceModel = "Android";
		param.applicationVersion = "1.0";
		client.send(new TdApi.SetTdlibParameters(param), this);
	}

	public void CheckDatabaseEncryptionKey() {
		client.send(new TdApi.CheckDatabaseEncryptionKey(), this);
	}

	public void SetAuthenticationPhoneNumber() {
		client.send(new TdApi.SetAuthenticationPhoneNumber("", null), this);
	}

	public void CheckAuthenticationCode() {
		client.send(new TdApi.CheckAuthenticationCode(), this);
	}

	public void RegisterUser() {
		client.send(new TdApi.RegisterUser("", ""), this);
	}

	public void CheckAuthenticationPassword() {
		client.send(new TdApi.CheckAuthenticationPassword(), this);
	}

	@Override
	public void onResult(TdApi.Object object) {
		super.onResult(object);
		switch (object.getConstructor()) {
			case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
				TdApi.UpdateAuthorizationState state = (TdApi.UpdateAuthorizationState) object;
				updateAuthorizationState(state);
				break;
			case TdApi.UpdateOption.CONSTRUCTOR:
				TdApi.UpdateOption updateOption = (TdApi.UpdateOption) object;
				log(updateOption.toString());
				break;
			case TdApi.Error.CONSTRUCTOR:
				break;
		}
	}

	private void updateAuthorizationState(TdApi.UpdateAuthorizationState state) {
		if (state == null) {
			return;
		}
		authorizationState = state.authorizationState;
		switch (state.authorizationState.getConstructor()) {
			case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
				toast("等候设置参数");
				break;
			case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
				toast("等候加密key");
				break;
			case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
				break;
			case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR:
				String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) authorizationState).link;
				log("登录连接：" + link);
				break;
			case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
				break;
			case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR:
				break;
			case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
				break;
			case TdApi.AuthorizationStateReady.CONSTRUCTOR:
				toast("登录成功");
				break;
			case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
				break;
			case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
				break;
			case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
				break;
			default:
				break;
		}
	}

}
