package com.github.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.App;
import com.github.drive.Callback;
import com.github.telegram.ActionCallback;
import com.github.telegram.AuthAction;
import com.github.telegram.LoginListener;
import com.github.telegramdrive.R;
import com.github.utils.ViewUtils;

import org.drinkless.td.libcore.telegram.TdApi;

import androidx.annotation.Nullable;

public class LoginActivity extends Activity implements LoginListener {
	private EditText phoneEt, verifyCodeEt, passwordEt;
	private AuthAction auth;
	private View phoneRl, verifyCodeRl, passwordRl;

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
		auth = ((App) getApplication()).getClient().auth;
		auth.setLogListener(new Callback<String, Void>() {
			@Override
			public Void call(String s) {
				Log.i("LOGGIN", s);
				return null;
			}
		});
		auth.setLoginListener(this);
		auth.create();
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
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((App) getApplication()).getClient().getDriveChat(new ActionCallback<TdApi.Ok>() {
					@Override
					public void toObject(TdApi.Ok ok) {
						startActivity(new Intent(LoginActivity.this, FolderActivity.class));
					}
				});
			}
		});
	}

	@Override
	public void onFailure(String failure) {

	}

	public void verifyPhone(View view) {
		if (TextUtils.isEmpty(phoneEt.getText())) {
			ViewUtils.toast(this, "请输入手机号");
			return;
		}
		auth.SetAuthenticationPhoneNumber(phoneEt.getText().toString());
	}

	public void verifyCode(View view) {
		if (TextUtils.isEmpty(verifyCodeEt.getText())) {
			ViewUtils.toast(this, "请输入验证码");
			return;
		}
		auth.CheckAuthenticationCode(verifyCodeEt.getText().toString());
	}

	public void verifyPassword(View view) {
		if (TextUtils.isEmpty(passwordEt.getText())) {
			ViewUtils.toast(this, "请输入密码");
			return;
		}
		auth.CheckAuthenticationPassword(passwordEt.getText().toString());
	}
}
