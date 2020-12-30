package com.github.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.App;
import com.github.telegram.ActionCallback;
import com.github.telegram.LoginListener;
import com.github.telegram.TelegramClient;
import com.github.telegramdrive.R;
import com.psaravan.filebrowserview.lib.Utils.Utils;

import org.drinkless.td.libcore.telegram.TdApi;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity implements LoginListener {
	private EditText phoneEt, verifyCodeEt, passwordEt;
	private View phoneRl, verifyCodeRl, passwordRl;
	private TelegramClient client;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		phoneEt = findViewById(R.id.phone);
		verifyCodeEt = findViewById(R.id.verify_code);
		passwordEt = findViewById(R.id.password);
		phoneRl = findViewById(R.id.phone_rl);
		verifyCodeRl = findViewById(R.id.verify_code_rl);
		passwordRl = findViewById(R.id.password_rl);
		client = ((App) getApplication()).getClient();
		client.setLoginListener(this);
		client.login();
	}

	@Override
	public void onInputPhoneNum() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				phoneRl.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onInputVerifyCode() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				phoneRl.setVisibility(View.GONE);
				verifyCodeRl.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onInputPassword() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				verifyCodeRl.setVisibility(View.GONE);
				passwordRl.setVisibility(View.VISIBLE);
			}
		});
	}

	@Override
	public void onSuccess() {
		runOnUiThread(() -> {
			TelegramClient client = ((App) getApplication()).getClient();
			client.getDriveChat(new ActionCallback<TdApi.Ok>() {
				@Override
				public void toObject(TdApi.Ok ok) {
					startActivity(new Intent(LoginActivity.this, FolderActivity.class));
				}
			});
		});
	}

	@Override
	public void onFailure(String failure) {

	}

	public void verifyPhone(View view) {
		if (TextUtils.isEmpty(phoneEt.getText())) {
			Utils.toast(this, "请输入手机号");
			return;
		}
		client.SetAuthenticationPhoneNumber(phoneEt.getText().toString());
	}

	public void verifyCode(View view) {
		if (TextUtils.isEmpty(verifyCodeEt.getText())) {
			Utils.toast(this, "请输入验证码");
			return;
		}
		client.CheckAuthenticationCode(verifyCodeEt.getText().toString());
	}

	public void verifyPassword(View view) {
		if (TextUtils.isEmpty(passwordEt.getText())) {
			Utils.toast(this, "请输入密码");
			return;
		}
		client.CheckAuthenticationPassword(passwordEt.getText().toString());
	}
}
