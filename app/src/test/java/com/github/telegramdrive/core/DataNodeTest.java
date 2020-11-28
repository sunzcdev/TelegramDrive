package com.github.telegramdrive.core;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class DataNodeTest {

	@Test
	public void split() {
		DataNode node = new DataNode();
		DataInfo info = node.split(new File("./localfile.txt"));
		System.out.println(new Gson().toJson(info));
	}

	@Test
	public void merge() {
		DataNode node = new DataNode();
		DataInfo info = node.split(new File("./localfile.txt"));
		System.out.println(new Gson().toJson(info));
	}
}