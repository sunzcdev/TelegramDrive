package com.github.telegramdrive.demo;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;
import java.util.List;

public abstract class DemoAction {
	public static final List<DemoAction> DEMO_ACTIONS = new ArrayList<DemoAction>() {
		{
			add(new DemoAction("获取聊天") {
				@Override
				public void onAction() {
					Client.execute(new TdApi.CreatePrivateChat());
				}
			});
		}
	};
	private String name;

	DemoAction(String name) {
		this.name = name;
	}

	public abstract void onAction();

	@Override
	public String toString() {
		return this.name;
	}
}
