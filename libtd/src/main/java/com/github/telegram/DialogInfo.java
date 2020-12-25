package com.github.telegram;

import com.github.drive.Callback;

public class DialogInfo {
	public final String title;
	public final String message;
	public final int inputType;
	public final Callback<String, Void> callback;

	public DialogInfo(String title, String message, int inputType, Callback<String, Void> callback) {
		this.title = title;
		this.message = message;
		this.inputType = inputType;
		this.callback = callback;
	}
}
