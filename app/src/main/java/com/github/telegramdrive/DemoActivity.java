package com.github.telegramdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.telegramdrive.demo.DemoAction;

import static com.github.telegramdrive.demo.DemoAction.DEMO_ACTIONS;

public class DemoActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		ListView demoList = findViewById(R.id.demo_list);
		ArrayAdapter<DemoAction> demoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, DEMO_ACTIONS);
		demoList.setAdapter(demoAdapter);
		demoList.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		DEMO_ACTIONS.get(position).onAction();
	}
}
