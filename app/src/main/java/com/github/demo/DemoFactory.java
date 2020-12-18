package com.github.demo;

import com.github.drive.Callback;
import com.github.telegram.AuthAction;
import com.github.telegram.ChatAction;
import com.github.telegram.DialogInfo;
import com.github.telegram.MessageAction;
import com.github.telegram.UserAction;

import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

/**
 *
 */
public class DemoFactory {

	private final DemoMap demos = new DemoMap();
	private final AuthAction authAction;

	public DemoFactory(TdApi.TdlibParameters param) {
		authAction = new AuthAction(param);
//		UserAction userAction = new UserAction(authAction);
//		ChatAction chatAction = new ChatAction(authAction, userAction);
//		MessageAction messageAction = new MessageAction(authAction, authAction, userAction, chatAction);
//		demos.put("创建客户端", authAction::create);
//		demos.put("设置参数", authAction::setParam);
//		demos.put("获取加密key", authAction::CheckDatabaseEncryptionKey);
//		demos.put("GetMe", userAction::GetMe);
//		demos.put("创建私聊", chatAction::CreatePrivateChat);
////		demos.put("GetChats", chatAction::GetChats);
//		demos.put("发送信息", messageAction::SendMessage);
//		demos.put("接收消息", chatAction::getChatHistory);
	}

	public List<String> getDemos() {
		return demos.demos;
	}

	public Runnable get(int position) {
		return demos.get(position);
	}

	public void setLogListener(Callback<String, Void> callback) {
		authAction.setLogListener(callback);
	}

	public void setInputListener(Callback<DialogInfo, Void> callback) {
		authAction.setInputListener(callback);
	}

	private static class DemoMap extends HashMap<String, Runnable> {
		private List<String> demos = new ArrayList<>();

		@Nullable
		@Override
		public Runnable put(String key, Runnable value) {
			demos.add(key);
			return super.put(key, value);
		}

		public Runnable get(int position) {
			return super.get(demos.get(position));
		}
	}
}
