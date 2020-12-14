package com.github.ui;

public interface LoginListener {
	void onInputVerifyCode(String code);

	void onInputPassword(String password);

	void onSuccess();

	void onFailure(String failure);
}
