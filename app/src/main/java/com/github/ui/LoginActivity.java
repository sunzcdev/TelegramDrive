package com.github.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.TelegramApp;
import com.github.telegram.TelegramClient;
import com.github.telegramdrive.R;
import com.github.utils.ViewUtils;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity implements LoginListener {
	private EditText phoneEt;
	private TelegramClient client;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		phoneEt = findViewById(R.id.phone);
		TelegramApp app = (TelegramApp) getApplication();
		client = app.getClient();
	}

	public void login(View view) {
		if (TextUtils.isEmpty(phoneEt.getText())) {
			ViewUtils.toast(this, "请输入手机号");
			return;
		}
		client.login(phoneEt.getText().toString(), this);
	}

	@Override
	public void onInputVerifyCode(String code) {

	}

	@Override
	public void onInputPassword(String password) {

	}

	@Override
	public void onSuccess() {

	}

	@Override
	public void onFailure(String failure) {

	}
}
