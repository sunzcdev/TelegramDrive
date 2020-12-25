package com.github.telegram;

public interface LoginListener {
	void onInputPhoneNum();

	void onInputVerifyCode();

	void onInputPassword();

	void onSuccess();

	void onFailure(String failure);
}
