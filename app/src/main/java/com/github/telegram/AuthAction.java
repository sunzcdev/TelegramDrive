package com.github.telegram;

import android.content.Context;
import android.text.InputType;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

public class AuthAction extends ClientAction {
	private TdApi.AuthorizationState authorizationState;
	private Context context;

	public AuthAction(Context context) {
		this.context = context;
	}

	@Override
	public void send(TdApi.Function function, Client.ResultHandler handler) {
		if (authorizationState != null && authorizationState.getConstructor() == TdApi.AuthorizationStateReady.CONSTRUCTOR) {
			super.send(function, handler);
		} else {
			log("telegram未登录");
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
		input(new DialogInfo("请输入手机号", "+8615238670618", InputType.TYPE_CLASS_PHONE, s -> {
			client.send(new TdApi.SetAuthenticationPhoneNumber(s, null), this);
			return null;
		}));
	}

	public void CheckAuthenticationCode() {
		input(new DialogInfo("请输入验证码", "", InputType.TYPE_CLASS_PHONE, s -> {
			client.send(new TdApi.CheckAuthenticationCode(s), this);
			return null;
		}));
	}

	public void RegisterUser() {
		input(new DialogInfo("请输入用户名", "+8615238670618", InputType.TYPE_CLASS_PHONE, s -> {
			client.send(new TdApi.RegisterUser(s, ""), this);
			return null;
		}));
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
			case TdApi.UpdateConnectionState.CONSTRUCTOR:
				TdApi.UpdateConnectionState connect = (TdApi.UpdateConnectionState) object;
				show(connect.state.toString());
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
		show(authorizationState.toString());
		switch (state.authorizationState.getConstructor()) {
			case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
				show("等候设置参数");
				break;
			case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
				show("等候加密key");
				break;
			case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
				show("输入手机号");
				break;
			case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR:
				String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) authorizationState).link;
				show("登录连接：" + link);
				break;
			case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
				show("请输入验证码");
				break;
			case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR:
				show("请注册注册");
				break;
			case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
				show("请输入密码");
				break;
			case TdApi.AuthorizationStateReady.CONSTRUCTOR:
				show("登录成功");
				break;
			case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
				show("登出成功");
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
