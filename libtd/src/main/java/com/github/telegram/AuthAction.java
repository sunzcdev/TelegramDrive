package com.github.telegram;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.List;

public class AuthAction extends TelegramAction {
	private TdApi.TdlibParameters parameters;
	private TdApi.AuthorizationState authorizationState;
	private LoginListener loginListener;
	private List<ActionCallback<TdApi.Update>> listeners;
	public AuthAction(TdApi.TdlibParameters parameters) {
		this.parameters = parameters;
		listeners = new ArrayList<>();
	}

	@Override
	public <T> void send(TdApi.Function function, ActionCallback<T> callback) {
		if (authorizationState != null && authorizationState.getConstructor() == TdApi.AuthorizationStateReady.CONSTRUCTOR) {
			super.send(function, callback);
		} else {
			log("telegram未登录");
		}
	}

	public void setParam() {
		client.send(new TdApi.SetTdlibParameters(this.parameters), this);
	}

	public void CheckDatabaseEncryptionKey() {
		client.send(new TdApi.CheckDatabaseEncryptionKey(), this);
	}

	public void SetAuthenticationPhoneNumber(String phone) {
		client.send(new TdApi.SetAuthenticationPhoneNumber(phone, null), this);
	}

	public void CheckAuthenticationCode(String verifyCode) {
		client.send(new TdApi.CheckAuthenticationCode(verifyCode), this);
	}

	public void RegisterUser(String userName) {
		client.send(new TdApi.RegisterUser(userName, ""), this);
	}

	public void CheckAuthenticationPassword(String password) {
		client.send(new TdApi.CheckAuthenticationPassword(password), this);
	}

	@Override
	public void onResult(TdApi.Object object) {
		log(object.toString());
		if (object instanceof TdApi.Update && !listeners.isEmpty()) {
			for (ActionCallback<TdApi.Update> listener : listeners) {
				try {
					listener.toObject((TdApi.Update) object);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
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
				setParam();
				break;
			case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
				CheckDatabaseEncryptionKey();
				break;
			case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
				loginListener.onInputPhoneNum();
				show("输入手机号");
				break;
			case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR:
				String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) authorizationState).link;
				show("登录连接：" + link);
				break;
			case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
				loginListener.onInputVerifyCode();
				show("请输入验证码");
				break;
			case TdApi.AuthorizationStateWaitRegistration.CONSTRUCTOR:
				show("请注册注册");
				break;
			case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR:
				show("请输入密码");
				loginListener.onInputPassword();
				break;
			case TdApi.AuthorizationStateReady.CONSTRUCTOR:
				loginListener.onSuccess();
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
				loginListener.onFailure(state.authorizationState.toString());
				break;
		}
	}

	public void setLoginListener(LoginListener loginListener) {
		this.loginListener = loginListener;
	}
	public void registerUpdateListener(ActionCallback<TdApi.Update> callback) {
		this.listeners.add(callback);
	}

	public void unregisterUpdateListener(ActionCallback<TdApi.Update> callback) {
		if (this.listeners.remove(callback)) {
			log("监听器移除成功");
		}
	}
}
