package com.github.demo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.demo.DemoFactory;
import com.github.telegramdrive.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class DemoActivity extends Activity implements AdapterView.OnItemClickListener {
	private DemoFactory demoFactory;
	private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		demoFactory = new DemoFactory(this);
		ListView demoList = findViewById(R.id.demo_list);
		ArrayAdapter<String> demoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoFactory.getDemos());
		demoList.setAdapter(demoAdapter);
		demoList.setOnItemClickListener(this);
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
}
