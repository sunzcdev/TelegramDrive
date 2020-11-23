package com.github.telegramdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.drinkless.td.libcore.telegram.Client;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
