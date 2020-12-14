package com.github.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.github.drive.Callback;
import com.github.telegram.DialogInfo;
import com.github.telegramdrive.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class DemoActivity extends Activity implements AdapterView.OnItemClickListener, Callback<String, Void> {
	private DemoFactory demoFactory;
	private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
	private ArrayAdapter<String> logAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		demoFactory = new DemoFactory(this);
		ListView demoList = findViewById(R.id.demo_list);
		ArrayAdapter<String> demoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoFactory.getDemos());
		demoList.setAdapter(demoAdapter);
		demoList.setOnItemClickListener(this);
		ListView logList = findViewById(R.id.log_list);
		logAdapter = new ArrayAdapter<>(this, android.R.layout.test_list_item);
		logList.setAdapter(logAdapter);
		logList.setOnItemClickListener(this);
		demoFactory.setLogListener(this);
		demoFactory.setInputListener(new Callback<DialogInfo, Void>() {
			@Override
			public Void call(DialogInfo info) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DemoActivity.this);
				builder.setTitle(info.title);
				EditText editText = new EditText(DemoActivity.this);
				editText.setText(info.message);
				editText.setInputType(info.inputType);
				builder.setView(editText);
				builder.setPositiveButton("确定", (dialog, which) -> info.callback.call(editText.getText().toString()));
				builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
				builder.show();
				return null;
			}
		});
		int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
		if (write != PackageManager.PERMISSION_GRANTED || read != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, permissions, 0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		demoFactory.get(position).run();
	}

	@Override
	public Void call(String s) {
		runOnUiThread(() -> logAdapter.add(s));
		return null;
	}
}
